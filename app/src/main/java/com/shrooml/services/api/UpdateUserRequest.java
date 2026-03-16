package com.shrooml.services.api;

public class UpdateUserRequest {
    private String email;
    private String description;
    private String champignon_prefere;
    private String photo_profil;

    public UpdateUserRequest(String email, String description, String champignon_prefere, String photo_profil) {
        this.email = email;
        this.description = description;
        this.champignon_prefere = champignon_prefere;
        this.photo_profil = photo_profil;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getChampignon_prefere() {
        return champignon_prefere;
    }

    public String getPhoto_profil() {
        return photo_profil;
    }
}