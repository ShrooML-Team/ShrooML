package com.shrooml.services.api;

public class IdentificationHistoryResponse {
    private int id;
    private int user_id;
    private String champignon;
    private float score;
    private String date;
    private String heure;
    private String localisation;
    private Float latitude;
    private Float longitude;
    private String notes;
    private String created_at;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getChampignon() {
        return champignon;
    }

    public float getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public String getHeure() {
        return heure;
    }

    public String getLocalisation() {
        return localisation;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreated_at() {
        return created_at;
    }
}
