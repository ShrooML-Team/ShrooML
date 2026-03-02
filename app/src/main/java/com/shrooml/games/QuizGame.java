package com.shrooml.games;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import android.content.Context;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shrooml.QuizActivity;
import com.shrooml.R;
import com.shrooml.models.MushroomEntity;
import com.shrooml.services.Inaturalist;
import com.shrooml.services.InaturalistService;
import com.shrooml.services.MushroomHistoryService;
import com.shrooml.services.ShroomLocService;

public class QuizGame {
    private static final InaturalistService img_api = new InaturalistService();

    private MushroomEntity mushroom;

    private String[] questions = new String[5];

    public interface QuizCallback {
        void onQuizReady();
        void onError(String errorMessage);
    }

    public QuizGame(Context context, String username, String password, QuizCallback callback){
        ShroomLocService repo = new ShroomLocService(username, password);

        repo.getAll(new ShroomLocService.MushroomsCallback() {
            @Override
            public void onSuccess(List<MushroomEntity> mushrooms) {

                if (mushrooms == null || mushrooms.isEmpty()) {
                    callback.onError("Liste de champignons vide");
                    return;
                }
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
                        initQuestions(context);
                        callback.onQuizReady();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        mushroom.setImage("");
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
        questions[0] = context.getString(R.string.quizz_question_1, mushroom.getCommon_name());
        questions[1] = context.getString(R.string.quizz_question_2, mushroom.getCommon_name());
        questions[2] = context.getString(R.string.quizz_question_3, mushroom.getCommon_name());
        questions[3] = context.getString(R.string.quizz_question_4, mushroom.getCommon_name());
        questions[4] = context.getString(R.string.quizz_question_5);
    }
    public String getQuestion(int index){
        return this.questions[index];
    }

    public String getImg(){
        return this.mushroom.getImage();
    }
}
