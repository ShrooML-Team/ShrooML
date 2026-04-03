package com.shrooml.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;

import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.translate.Translator;
import com.shrooml.R;
import com.shrooml.models.MushroomEntity;
import com.shrooml.services.InaturalistService;
import com.shrooml.services.MushroomHistoryService;
import com.shrooml.services.ShroomLocService;

public class QuizGame {
    private static final InaturalistService img_api = new InaturalistService();

    private MushroomEntity mushroom;

    private final List<String> allScientName = new ArrayList<>();

    private final List<String> allCommonName = new ArrayList<>();

    private final List<String> allSeason = new ArrayList<>();

    private final List<String> allHabitat = new ArrayList<>();

    private final String[] questions = new String[5];

    private final String[] titres = new String[6];

    private List<String> currentAnswer = null;

    private int score =0;

    private Translator engToLang;

    private Translator frToLang;

    public interface TranslationCallback {
        void onTranslated(String translatedText);
    }

    public interface AnswerCheckCallback {
        void onResult(boolean isCorrect);
    }

    public interface QuizCallback {
        void onQuizReady();
        void onError(String errorMessage);
    }

    public QuizGame(Context context, Translator engtolanguage, Translator frtolanguage, QuizCallback callback){
        ShroomLocService repo = new ShroomLocService();
        engToLang = engtolanguage;
        frToLang = frtolanguage;

        repo.getAll(new ShroomLocService.MushroomsCallback() {
            @Override
            public void onSuccess(List<MushroomEntity> mushrooms) {

                List<String> habunique = new ArrayList<>();

                if (mushrooms == null || mushrooms.isEmpty()) {
                    callback.onError("Liste de champignons vide");
                    return;
                }

                for (MushroomEntity mush : mushrooms){
                    // Le nom scientifique est en latin, pas besoin de le traduire !
                    allScientName.add(mush.getScientific_name());

                    // Remplissage asynchrone pour le nom commun
                    translateDynamicText(mush.getCommon_name(), translatedName -> {
                        allCommonName.add(translatedName);
                    });

                    // Remplissage asynchrone pour les habitats
                    for (String hab : mush.getHabitat()){
                        if(!habunique.contains(hab)){
                            habunique.add(hab);
                        }
                    }
                }

                for (String hab : habunique){
                    translateDynamicText(hab, translatedHab -> {
                        allHabitat.add(translatedHab);
                    });
                }

                allSeason.add(context.getString(R.string.autumn));
                allSeason.add(context.getString(R.string.winter));
                allSeason.add(context.getString(R.string.spring));
                allSeason.add(context.getString(R.string.summer));

                Set<Integer> shroom_used = MushroomHistoryService.loadUsedIndices(context);

                int indice;

                if(shroom_used.size() == mushrooms.size()) {
                    MushroomHistoryService.reset(context);
                    shroom_used.clear();
                }

                do {
                    indice = ThreadLocalRandom.current().nextInt(0,mushrooms.size());
                }while(shroom_used.contains(indice));

                shroom_used.add(indice);

                MushroomHistoryService.saveUsedIndices(context,shroom_used);

                mushroom = mushrooms.get(indice);
                img_api.getMushroomImage(mushroom.getScientific_name(), new InaturalistService.ImageCallback() {

                    @Override
                    public void onSuccess(String imageUrl) {
                        mushroom.setImage(imageUrl);
                        initTitres(context);
                        initQuestions(context);
                        callback.onQuizReady();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        mushroom.setImage("");
                        initTitres(context);
                        initQuestions(context);
                        callback.onQuizReady();
                    }
                });

            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("Erreur chargement champignons : " + errorMessage);
            }
        });

    }

    private void initQuestions(Context context){
        questions[0] = context.getString(R.string.quizz_question_1);
        questions[1] = context.getString(R.string.quizz_question_2, mushroom.getCommon_name());
        questions[2] = context.getString(R.string.quizz_question_3, mushroom.getCommon_name());
        questions[3] = context.getString(R.string.quizz_question_4, mushroom.getCommon_name());
        questions[4] = context.getString(R.string.quizz_question_5, mushroom.getCommon_name());
    }

    private void initTitres(Context context){
        titres[0] = context.getString(R.string.title_1);
        titres[1] = context.getString(R.string.title_2);
        titres[2] = context.getString(R.string.title_3);
        titres[3] = context.getString(R.string.title_4);
        titres[4] = context.getString(R.string.title_5);
        titres[5] = context.getString(R.string.title_6);
    }
    public String getQuestion(int index){
        return this.questions[index];
    }

    public String getImg(){
        return this.mushroom.getImage();
    }

    public List<String> getAllScientName(){
        return allScientName;
    }

    public List<String> getAllCommonName(){
        return allCommonName;
    }

    public List<String> getAllSeason(){
        return allSeason;
    }

    public List<String> getAllHabitat(){
        return allHabitat;
    }

    public void checkAnswer(int question, String answer, Context contexte, AnswerCheckCallback callback){
        boolean correct = false;
        List<String> answtranslate = new ArrayList<>();

        switch(question){

            case 0:
                //String translateName = translateDynamicText(mushroom.getCommon_name());
                translateDynamicText(mushroom.getCommon_name(), translatedName -> {
                    this.currentAnswer = new ArrayList<>(Collections.singletonList(translatedName));
                    callback.onResult(answer.equalsIgnoreCase(translatedName));
                });break;

            case 1:
                this.currentAnswer = new ArrayList<>(Collections.singleton(mushroom.getScientific_name()));
                callback.onResult(answer.equalsIgnoreCase(mushroom.getScientific_name()));
                break;

            case 2:
                if(mushroom.getEdibility().equals("edible")){
                    this.currentAnswer = new ArrayList<>(Collections.singleton(contexte.getString(R.string.trueAnswer)));
                    callback.onResult(answer.equalsIgnoreCase("true"));
                } else {
                    this.currentAnswer = new ArrayList<>(Collections.singleton(contexte.getString(R.string.falseAnswer)));
                    callback.onResult( answer.equalsIgnoreCase("false"));
                }
                break;

            case 3:
                // Astuce DevOps : Compter les tâches asynchrones pour savoir quand on a fini
                AtomicInteger countHab = new AtomicInteger(0);
                boolean[] isCorrectHab = {false}; // Tableau à 1 élément pour être modifiable dans le callback

                for(String h : mushroom.getHabitat()){
                    translateDynamicText(h, habtranslate -> {
                        answtranslate.add(habtranslate);
                        if(habtranslate.equalsIgnoreCase(answer)) isCorrectHab[0] = true;

                        // Si on a traduit le dernier habitat, on renvoie le résultat !
                        if(countHab.incrementAndGet() == mushroom.getHabitat().length){
                            this.currentAnswer = answtranslate;
                            callback.onResult(isCorrectHab[0]);
                        }
                    });
                }
                break;

            case 4:
                AtomicInteger countSeason = new AtomicInteger(0);
                boolean[] isCorrectSeason = {false};

                for(String s : mushroom.getSeason()){
                    translateDynamicText(s, seasontranslate -> {
                        answtranslate.add(seasontranslate);
                        if(seasontranslate.equalsIgnoreCase(answer)) isCorrectSeason[0] = true;

                        if(countSeason.incrementAndGet() == mushroom.getSeason().length){
                            this.currentAnswer = answtranslate;
                            callback.onResult(isCorrectSeason[0]);
                        }
                    });
                }
                break;
            default:callback.onResult(false);break;
        }
        return;
    }

    public void upScore(){
        score++;
    }

    public int getScore(){
        return score;
    }

    public String getTitre(int sco){
        return titres[sco];
    }

    public List<String> getCurrentAnswer(){
        return this.currentAnswer;
    }

    protected void translateDynamicText(String text, TranslationCallback callback) {
        if (text == null || text.isEmpty()) {
            callback.onTranslated("");
            return;
        }

        String textWithPlaceholders = text.replace("\n", " Br_tag ");

        LanguageIdentification.getClient().identifyLanguage(textWithPlaceholders)
                .addOnSuccessListener(languageCode -> {
                    String deviceLang = Locale.getDefault().getLanguage();

                    if (languageCode.equals(deviceLang)) {
                        callback.onTranslated(textWithPlaceholders.replace(" Br_tag ", "\n"));
                    }
                    else if (languageCode.equals("en") && engToLang != null) {
                        engToLang.translate(textWithPlaceholders)
                                .addOnSuccessListener(res -> callback.onTranslated(res.replace(" Br_tag ", "\n")))
                                .addOnFailureListener(e -> callback.onTranslated(textWithPlaceholders.replace(" Br_tag ", "\n")));
                    }
                    else if (languageCode.equals("fr") && frToLang != null) {
                        frToLang.translate(textWithPlaceholders)
                                .addOnSuccessListener(res -> callback.onTranslated(res.replace(" Br_tag ", "\n")))
                                .addOnFailureListener(e -> callback.onTranslated(textWithPlaceholders.replace(" Br_tag ", "\n")));
                    }
                    else {
                        callback.onTranslated(textWithPlaceholders.replace(" Br_tag ", "\n"));
                    }
                })
                .addOnFailureListener(e -> callback.onTranslated(textWithPlaceholders.replace(" Br_tag ", "\n")));
    }
}
