package com.shrooml;

import static android.widget.Toast.LENGTH_SHORT;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class BackgroundActivity extends AppCompatActivity {
    private boolean listenerInitialized = false;

    private SpeechRecognizer speechRecognizer;

    private TextView title;

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
                    Toast.makeText(BackgroundActivity.this, textcomp + "FIN ECOUTE", LENGTH_SHORT).show();

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
        title = findViewById(R.id.title);
        if(title != null && !listenerInitialized){
            Toast.makeText(BackgroundActivity.this, "RECUP TITLE", LENGTH_SHORT).show();
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
        Toast.makeText(BackgroundActivity.this, "ECOUTE", LENGTH_SHORT).show();

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

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        super.onDestroy();
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
        // Cette méthode sera remplie dans ShroomLocateActivity
    }
}
