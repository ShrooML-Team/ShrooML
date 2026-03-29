package com.shrooml;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.MushroomAdapter;
import com.shrooml.models.MushroomCompleteEntity;
import com.shrooml.services.OAuthService;
import com.shrooml.services.ShroomLocService;

import java.util.List;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;

public class ShroomLocateActivity extends BackgroundActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float shakeThreshold = 12f; // seuil de secouage
    private long lastShakeTime = 0;

    private static final int REQUEST_LOCATION = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private ShroomLocService api;

    private ImageView refreshButton;

    private double lastLat = 0;
    private double lastLon = 0;

    private ObjectAnimator refreshAnimator;

    private TextView loadingText;
    private View emptyState;
    private TextView emptyTitle;
    private TextView emptySubtitle;

    private RecyclerView recycler;

    private Handler handler = new Handler();
    private Runnable loadingAnimation;

    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        loadingText = findViewById(R.id.loadingText);
        emptyState = findViewById(R.id.emptyState);
        emptyTitle = findViewById(R.id.emptyTitle);
        emptySubtitle = findViewById(R.id.emptySubtitle);

        recycler = findViewById(R.id.mushroomRecycler);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        String[] frames = {
                getString(R.string.loading_1),
                getString(R.string.loading_2),
                getString(R.string.loading_3)
        };
        final int[] index = {0};

        loadingAnimation = new Runnable() {
            @Override
            public void run() {
                loadingText.setText(frames[index[0]]);
                index[0] = (index[0] + 1) % frames.length;
                handler.postDelayed(this, 500);
            }
        };


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        OAuthService api_auth = new OAuthService();

        api = new ShroomLocService();
        requestLocationPermission();

        refreshButton = findViewById(R.id.refreshButton);
        refreshAnimator = ObjectAnimator.ofFloat(refreshButton, "rotation", 0f, 360f);
        refreshAnimator.setDuration(800);
        refreshAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        refreshAnimator.setInterpolator(new LinearInterpolator());


        refreshButton.setOnClickListener(v -> {
            if (!refreshAnimator.isRunning()) {
                refreshAnimator.start();
            }

            if (lastLat != 0 && lastLon != 0) {
                callApi(lastLat, lastLon);
            } else {
                Toast.makeText(this, "Location not ready yet", Toast.LENGTH_SHORT).show();
            }
        });



        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_locate);


        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_locate) {
                    return true;
                }
                if (id == R.id.nav_quiz) {
                    startActivity(new Intent(ShroomLocateActivity.this, QuizActivity.class));
                    return true;
                }
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(ShroomLocateActivity.this, ProfileActivity.class));
                    return true;
                }
                if(id == R.id.nav_identify) {
                    startActivity(new Intent(ShroomLocateActivity.this, ChoiceIdentifyActivity.class));
                    return true;
                }

                return false;
            }
        });

    }

    private final SensorEventListener shakeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcul de l'accélération brute
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            long currentTime = System.currentTimeMillis();

            if (acceleration > shakeThreshold && (currentTime - lastShakeTime) > 1000) {
                lastShakeTime = currentTime;

                // 👉 Action : lancer le refresh
                if (!refreshAnimator.isRunning()) {
                    refreshAnimator.start();
                }

                if (lastLat != 0 && lastLon != 0) {
                    callApi(lastLat, lastLon);
                } else {
                    Toast.makeText(ShroomLocateActivity.this, "Location not ready yet", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeListener);
    }


    private void requestLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) { // Android 12+
            // demander ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        REQUEST_LOCATION
                );
            } else {
                getLocation();
            }
        } else { // Android < 12
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION
                );
            } else {
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callApi(location.getLatitude(), location.getLongitude());
                    } else {
                        requestNewLocation();
                    }
                });
    }

    private void requestNewLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission manquante → on ne fait rien
            return;
        }

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(500);
        request.setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(
                request,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            callApi(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(ShroomLocateActivity.this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                getMainLooper()
        );
    }




    private void callApi(double lat, double lon) {
        lastLat = lat;
        lastLon = lon;
        loadingText.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);

        handler.post(loadingAnimation);

        api.getMushroomsByLocation(lat, lon, new ShroomLocService.MushroomsLocationCallBack() {
            @Override
            public void onSucces(List<MushroomCompleteEntity> mushrooms) {

                // Stop loader
                handler.removeCallbacks(loadingAnimation);
                loadingText.setVisibility(View.GONE);

                if (refreshAnimator != null && refreshAnimator.isRunning()) {
                    refreshAnimator.end();
                    refreshButton.setRotation(0f);
                }

                // Cas liste vide
                if (mushrooms == null || mushrooms.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);

                    //Retour haptique
                    if (vibrator != null && vibrator.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(250, 150));
                        }
                    }
                    return;
                }

                // Cas liste non vide
                emptyState.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);

                recycler.setLayoutManager(new LinearLayoutManager(ShroomLocateActivity.this));

                MushroomAdapter adapter = new MushroomAdapter(ShroomLocateActivity.this, mushrooms);
                recycler.setAdapter(adapter);

                adapter.setOnMushroomClickListener(m -> {
                    Intent intent = new Intent(ShroomLocateActivity.this, ShroomDetailsActivity.class);
                    intent.putExtra("scientificName", m.getScientificName());
                    startActivity(intent);
                });

                //Retour haptique
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(250, 150));
                    }
                }
            }


            @Override
            public void onError(String errorMessage) {

                handler.removeCallbacks(loadingAnimation);
                loadingText.setVisibility(View.GONE);

                if (refreshAnimator != null && refreshAnimator.isRunning()) {
                    refreshAnimator.end();
                    refreshButton.setRotation(0f);
                }

                if ("EMPTY_LIST".equals(errorMessage)) {
                    emptyTitle.setText(getString(R.string.noMushroomFoundNearYou));
                    emptySubtitle.setText(getString(R.string.changeLocation));

                    emptyState.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);

                } else {
                    emptyTitle.setText(getString(R.string.noMushroomFoundNearYou));
                    emptySubtitle.setText(getString(R.string.changeLocation));

                    emptyState.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);

                }

                emptyState.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            boolean granted = false;
            for (int i = 0; i < permissions.length; i++) {
                if ((permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION))
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Dans ShroomLocateActivity.java
    @Override
    protected void onCorruptedStateChanged() {
        super.onCorruptedStateChanged();

        // On demande à la liste de se rafraîchir immédiatement
        if (recycler != null && recycler.getAdapter() != null) {
            // notifyDataSetChanged force onBindViewHolder à s'exécuter pour chaque item
            recycler.getAdapter().notifyDataSetChanged();
        }
    }
}
