package com.shrooml.services.api;

public class IdentificationHistoryCreateRequest {
    private String champignon;
    private float score;
    private String heure;
    private String localisation;
    private Float latitude;
    private Float longitude;
    private String notes;

    public IdentificationHistoryCreateRequest(String champignon, float score, String heure, String localisation, Float latitude, Float longitude, String notes) {
        this.champignon = champignon;
        this.score = score;
        this.heure = heure;
        this.localisation = localisation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
    }
}