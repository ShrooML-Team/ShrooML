package com.shrooml.games;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import android.content.Context;

import com.shrooml.R;
import com.shrooml.models.MushroomEntity;
import com.shrooml.services.Inaturalist;
import com.shrooml.services.MushroomHistoryService;
import com.shrooml.services.ShroomLocService;

public class QuizGame {
    private static final Inaturalist api_img = new Inaturalist();

    private static MushroomEntity mushroom;

    private static String[] questions;

    QuizGame(Context context){
        super();
        ShroomLocService repo = null;

        try {
            repo = new ShroomLocService(context);
        } catch(IOException err){}

        Set<Integer> shroom_used = null;

        shroom_used = MushroomHistoryService.loadUsedIndices(context);

        int indice = 0;

        if(shroom_used.size() == repo.size()) {
                MushroomHistoryService.reset(context);
            shroom_used.clear();
        }

        do {
            indice = ThreadLocalRandom.current().nextInt(0,repo.size());
        }while(shroom_used.contains(indice));
        shroom_used.add(indice);

        MushroomHistoryService.saveUsedIndices(context,shroom_used);

        mushroom = repo.getByIndex(indice);
        mushroom.setImage(api_img.getMushroomImage(mushroom.getScientific_name()));

        questions[0] = context.getString(R.string.quizz_question_1, mushroom.getCommon_name());
        questions[1] = context.getString(R.string.quizz_question_2, mushroom.getCommon_name());
        questions[2] = context.getString(R.string.quizz_question_3, mushroom.getCommon_name());
        questions[3] = context.getString(R.string.quizz_question_4, mushroom.getCommon_name());
        questions[4] = context.getString(R.string.quizz_question_5);

    }

    public static String getQuestion(int index){
        return questions[index];
    }

    public static String getImg(int index){
        return mushroom.getImage();
    }
}
