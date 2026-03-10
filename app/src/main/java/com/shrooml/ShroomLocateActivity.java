package com.shrooml;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.MushroomAdapter;
import com.shrooml.models.MushroomCompleteEntity;
import com.shrooml.services.OAuthService;
import com.shrooml.services.ShroomLocService;
import com.shrooml.services.api.ShroomLocApi;
import com.shrooml.services.api.ShroomLocRetrofitClient;

import java.text.BreakIterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShroomLocateActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private ShroomLocService api;
    private TextView mushroomListText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        OAuthService api_auth = new OAuthService();


        api_auth.login("admin", "password123", new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String token) {
                ShroomLocRetrofitClient.setToken(token);
                api = new ShroomLocService();
                Toast.makeText(ShroomLocateActivity.this, "IDENTIFIER", Toast.LENGTH_SHORT).show();

                getLocation();   // ✔️ D’ABORD
                Toast.makeText(ShroomLocateActivity.this, "IDENTIFIER APRES LOCATION", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ShroomLocateActivity.this, "Erreur : "
                        + errorMessage, Toast.LENGTH_LONG).show();
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
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ShroomLocateActivity.this, SplashActivity.class));
                    return true;
                }

                return false;
            }
        });

    }


    private void requestLocationPermission() {
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

    private void getLocation() {
        Toast.makeText(this, "ICI LOCATION ", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, "lat = " + lat + ", lon = " + lon, Toast.LENGTH_SHORT).show();
        api.getMushroomsByLocation(lat, lon, new ShroomLocService.MushroomsLocationCallBack() {
            @Override
            public void onSucces(List<MushroomCompleteEntity> mushrooms) {
                if (mushrooms == null || mushrooms.isEmpty()) {
                    Toast.makeText(ShroomLocateActivity.this, "Aucun champignon trouvé ici", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder sb = new StringBuilder();

                MushroomCompleteEntity first = mushrooms.get(0);
                Toast.makeText(
                        ShroomLocateActivity.this,
                        "Premier champignon : " + first.getCommonName(),
                        Toast.LENGTH_LONG
                ).show();

                for (MushroomCompleteEntity m : mushrooms) {
                    sb.append(m.getCommonName())
                            .append(" (")
                            .append(m.getScientificName())
                            .append(")\n");
                }

                RecyclerView recycler = findViewById(R.id.mushroomRecycler);
                recycler.setLayoutManager(new LinearLayoutManager(ShroomLocateActivity.this));
                recycler.setAdapter(new MushroomAdapter(ShroomLocateActivity.this, mushrooms));


            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ShroomLocateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
