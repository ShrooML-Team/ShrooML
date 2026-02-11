package com.shrooml.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Inaturalist {

    private static final String url = "https://api.inaturalist.org/v1/observations";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String getMushroomImage(String specie_name) {
        try {
            String encodedName = URLEncoder.encode(specie_name, StandardCharsets.UTF_8);

            String requestUrl = url
                    + "?taxon_name=" + encodedName
                    + "&photos=true"
                    + "&per_page=1"
                    + "&quality_grade=research";

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            String json = response.body().string();
            response.close();

            // Parse JSON avec Gson
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray results = root.getAsJsonArray("results");

            if (results.size() == 0) return null;

            JsonObject firstResult = results.get(0).getAsJsonObject();
            JsonArray photos = firstResult.getAsJsonArray("photos");

            if (photos.size() == 0) return null;

            JsonObject firstPhoto = photos.get(0).getAsJsonObject();
            String photoUrl = firstPhoto.get("url").getAsString();

            return photoUrl.replace("square", "large");

        } catch (IOException e) {
            System.err.println("Erreur image iNaturalist pour '" + specie_name + "': " + e.getMessage());
            return null;
        }
    }
}
