package com.shrooml.services;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shrooml.models.MushroomEntity;
import com.shrooml.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MushroomRepository {

    private List<MushroomEntity> mushrooms;

    public MushroomRepository(Context context) throws IOException {
        super();
        String json = readJsonFromRaw(context);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<MushroomEntity>>() {}.getType();
        mushrooms = gson.fromJson(json, listType);
        if (mushrooms == null) {
            mushrooms = new ArrayList<>();
        }
    }

    private String readJsonFromRaw(Context context) throws IOException {
        Resources res = context.getResources();
        InputStream is = res.openRawResource(R.raw.mushrooms_cleaned); // fichier dans res/raw/
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public int size() {
        return mushrooms.size();
    }

    public MushroomEntity getByIndex(int index) {
        return mushrooms.get(index);
    }

    public List<MushroomEntity> getAll() {
        return mushrooms;
    }
}
