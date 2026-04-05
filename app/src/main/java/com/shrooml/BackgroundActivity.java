package com.shrooml;

import static android.widget.Toast.LENGTH_SHORT;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.adapters.NavbarAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BackgroundActivity extends AppCompatActivity {
    private boolean listenerInitialized = false;

    private SpeechRecognizer speechRecognizer;

    private TextView title;

    protected int activityId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                speechRecognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(getApplicationContext());
            } else {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            }

            RecognitionListener listener = new RecognitionListener() {

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches =
                            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    String textcomp = "";

                    if (matches != null) {

                        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                        boolean corrupted = prefs.getBoolean("corrupted", false);
                        View root = findViewById(R.id.rootLayout);

                        for (String text : matches) {
                            textcomp = textcomp + text;
                            text = text.toLowerCase();

                            if ((text.contains("poison") || text.contains("corrupted")) && !corrupted) {
                                animatePoisonEffect(root);
                                corrupted = true;
                                prefs.edit().putBoolean("corrupted", true).apply();
                                onCorruptedStateChanged();
                                updateTaggedShapes(root, Color.parseColor("#4f004f"));
                            } else if ((text.contains("heal") || text.contains("cure")) && corrupted) {
                                animateHealEffect(root);
                                corrupted = false;
                                prefs.edit().putBoolean("corrupted", false).apply();
                                onCorruptedStateChanged();
                                updateTaggedShapes(root, Color.parseColor("#A06A42"));
                            }
                        }
                    }

                    title.setClickable(true);
                }

                @Override public void onReadyForSpeech(Bundle params) {}
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {
                    title.setClickable(true);
                }
                @Override public void onError(int error) {
                    Toast.makeText(BackgroundActivity.this, "FIN ECOUTE ERROR" + error, LENGTH_SHORT).show();
                    title.setClickable(true);}
                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            };

            speechRecognizer.setRecognitionListener(listener);
        } else {
            Toast.makeText(this, "Reconnaissance vocale non disponible sur ce support", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        title = findViewById(R.id.title);
        if(title != null && !listenerInitialized){
            title.setClickable(true);
            listenerInitialized = true;

            final int[] tapCount = {0};
            final Handler handler = new Handler();
            final long DELAY = 500; // temps max entre les taps

            title.setOnClickListener(v -> {
                tapCount[0]++;

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    if (tapCount[0] >= 3) {
                        startVoiceRecognition();
                    }
                    tapCount[0] = 0;
                }, DELAY);
            });
        }

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        boolean corrupted = prefs.getBoolean("corrupted", false);
        View root = findViewById(R.id.rootLayout);
        int colorMain = corrupted ? Color.parseColor("#800080") : Color.parseColor("#C98D63");
        int colorItem = corrupted ? Color.parseColor("#4f004f") : Color.parseColor("#A06A42");

        if (root != null) {
            root.setBackgroundColor(colorMain);

            updateTaggedShapes(root, colorItem);
        }
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void updateTaggedShapes(View view, int color) {
        // On vérifie si la vue a le tag "item_bg"
        Object tag = view.getTag();
        if (tag != null && tag.equals("item_bg")) {
            android.graphics.drawable.Drawable bg = view.getBackground();
            if (bg instanceof android.graphics.drawable.GradientDrawable) {
                ((android.graphics.drawable.GradientDrawable) bg.mutate()).setColor(color);
            }
        }

        // On continue de chercher dans les enfants (si c'est un groupe de vues)
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                updateTaggedShapes(group.getChildAt(i), color);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchSpeechRecognizer();
            } else {
                Toast.makeText(this, "Permission microphone refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startVoiceRecognition() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, 101);
            return;
        }
        launchSpeechRecognizer(); // ta méthode existante qui crée le SpeechRecognizer
    }

    private void launchSpeechRecognizer() {

        title.setClickable(false);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra("android.speech.extra.DICTATION_MODE", true);


        new Handler().postDelayed(() -> {
            try {
                speechRecognizer.startListening(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage() + "Impossible de démarrer la reconnaissance vocale", LENGTH_SHORT).show();
            }
        }, 200);
    }


    private void animateHealEffect(View view) {
        // Couleur de départ (poison) et couleur finale (normale)
        int colorFrom = Color.parseColor("#800080");
        int colorTo = Color.parseColor("#C98D63");

        // Crée un ValueAnimator pour le dégradé de lumière
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, Color.GREEN, colorTo);
        colorAnimation.setDuration(1600); // durée totale de l'effet
        colorAnimation.addUpdateListener(animator -> view.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
        view.setAlpha(0.8f);
        view.animate().alpha(1f).setDuration(1200).start();
    }

    private void animatePoisonEffect(View view) {
        // Couleur de départ (normale) et couleur finale (poison)
        int colorFrom = Color.parseColor("#C98D63");
        int colorTo = Color.parseColor("#800080");

        // Animation de couleur avec pulsation
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.setDuration(600); // temps de transition
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setRepeatCount(4); // petites oscillations pour effet vivant
        colorAnimator.addUpdateListener(animator ->
                view.setBackgroundColor((int) animator.getAnimatedValue())
        );

        // Animation alpha pour créer un effet de halo vivant
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.8f, 1f);
        alphaAnimator.setDuration(800);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setRepeatCount(3);
        alphaAnimator.addUpdateListener(animator ->
                view.setAlpha((float) animator.getAnimatedValue())
        );

        // Lancer les deux animations ensemble
        colorAnimator.start();
        alphaAnimator.start();

    }

    // Dans BackgroundActivity.java
    protected void onCorruptedStateChanged() {
        // Cette méthode sera remplie dans ShroomLocateActivity and ranking
    }

    protected void initNavBar(Context context) {
        RecyclerView navRecycler = findViewById(R.id.bottomNavRecycler);
        ImageView left = findViewById(R.id.chevronLeft);
        ImageView right = findViewById(R.id.chevronRight);

        if (navRecycler != null) {
            navRecycler.post(() -> {
                int itemWidth = (int) (120 * getResources().getDisplayMetrics().density); // 120dp en pixels
                int padding = (navRecycler.getWidth() / 2) - (itemWidth / 2);
                navRecycler.setPadding(padding, 0, padding, 0);
                navRecycler.setClipToPadding(false);

                // Une fois le padding mis, on centre sur l'activité actuelle
                int midPosition = (NavbarAdapter.LOOP_COUNT / 2) - ((NavbarAdapter.LOOP_COUNT / 2) % 4) + activityId;
                navRecycler.scrollToPosition(midPosition);
            });
            List<String> menus = Arrays.asList("Quiz", "Locate", "Profile", "Identify");

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            navRecycler.setLayoutManager(layoutManager);

            navRecycler.setOnFlingListener(null);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(navRecycler);

            NavbarAdapter adapter = new NavbarAdapter(menus, activityId, position -> {
                if (position != activityId) navigateTo(context, position);
            });
            navRecycler.setAdapter(adapter);

            navRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int centerX = recyclerView.getWidth() / 2;

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        TextView title = child.findViewById(R.id.nav_title);

                        if (title == null) continue;

                        int childCenterX = (child.getLeft() + child.getRight()) / 2;
                        int distance = Math.abs(centerX - childCenterX);

                        // Ratio de proximité (250f pour une transition douce)
                        float proximity = Math.min(1.0f, (float) distance / 250f);

                        // EFFET DE GROSSISSEMENT ET DE BLANC (Seulement sur le texte)
                        float alpha = 1.0f - (proximity * 0.5f); // 1.0 au centre, 0.5 sur les bords
                        float scale = 1.2f - (proximity * 0.2f); // 1.2 au centre, 1.0 sur les bords

                        title.setAlpha(alpha);
                        title.setScaleX(scale);
                        title.setScaleY(scale);

                        // Note : On ne touche pas à "nav_indicator" ici !
                        // Il reste tel que l'Adapter l'a défini (visible ou invisible).
                    }
                }
            });

            // ASTUCE POUR LE CYCLIQUE :
            // On calcule une position au milieu de la liste géante qui correspond à notre activityId
            int midPosition = (NavbarAdapter.LOOP_COUNT / 2) - ((NavbarAdapter.LOOP_COUNT / 2) % menus.size()) + activityId;

            navRecycler.scrollToPosition(midPosition);

            // Gestion des chevrons (plus besoin de Math.max/min car c'est "infini")
            if (findViewById(R.id.chevronLeft) != null) {
                findViewById(R.id.chevronLeft).setOnClickListener(v -> {
                    // On défile simplement vers la gauche
                    int currentPos = layoutManager.findFirstVisibleItemPosition();
                    navRecycler.smoothScrollToPosition(currentPos - 1);
                });
            }
            if (findViewById(R.id.chevronRight) != null) {
                findViewById(R.id.chevronRight).setOnClickListener(v -> {
                    // On défile simplement vers la droite
                    int currentPos = layoutManager.findFirstVisibleItemPosition();
                    navRecycler.smoothScrollToPosition(currentPos + 1);
                });
            }
        }
    }

    private void navigateTo(Context context, int position) {
        Intent intent = null;
        switch (position) {
            case 0: intent = new Intent(context, QuizActivity.class); break;
            case 1: intent = new Intent(context, ShroomLocateActivity.class); break;
            case 2: intent = new Intent(context, ProfileActivity.class); break;
            case 3: intent = new Intent(context, ChoiceIdentifyActivity.class); break;
        }
        if (intent != null) {
            startActivity(intent);
            finish();
            // Optionnel : petite transition fluide
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void hideSystemUI() {
        // Pour les versions récentes d'Android (API 30+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                // On cache la barre de navigation (boutons) et la barre de statut (heure/batterie)
                controller.hide(WindowInsets.Type.systemBars());
                // On fait en sorte qu'elles réapparaissent temporairement au swipe
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Pour les anciennes versions d'Android (Legacy)
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}
