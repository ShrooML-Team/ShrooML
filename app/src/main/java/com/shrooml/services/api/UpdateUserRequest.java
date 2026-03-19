package com.shrooml.services.api;

public class UpdateUserRequest {
    private String email;
    private String champignon_prefere;
    private String photo_profil;
    private Float scoring;
    private Integer description_index;

    public UpdateUserRequest(String email, String champignon_prefere, String photo_profil, Float scoring) {
        this(email, champignon_prefere, photo_profil, scoring, null);
    }

    public UpdateUserRequest(String email, String champignon_prefere, String photo_profil, Float scoring, Integer description_index) {
        this.email = email;
        this.champignon_prefere = champignon_prefere;
        this.photo_profil = photo_profil;
        this.scoring = scoring;
        this.description_index = description_index;
    }

    public String getEmail() {
        return email;
    }

    public String getChampignon_prefere() {
        return champignon_prefere;
    }

    public String getPhoto_profil() {
        return photo_profil;
    }

    public Float getScoring() {
        return scoring;
    }

    public Integer getDescription_index() {
        return description_index;
    }
}