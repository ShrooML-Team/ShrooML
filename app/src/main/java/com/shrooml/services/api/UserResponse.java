package com.shrooml.services.api;

public class UserResponse {
    private int id;
    private String identifiant;
    private String email;
    private String photo_profil;
    private String champignon_prefere;
    private Integer description_index;
    private float scoring;
    private int streak;
    private int niveau;
    private int rang;
    private String created_at;
    private boolean is_active;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto_profil() {
        return photo_profil;
    }

    public void setPhoto_profil(String photo_profil) {
        this.photo_profil = photo_profil;
    }

    public String getChampignon_prefere() {
        return champignon_prefere;
    }

    public void setChampignon_prefere(String champignon_prefere) {
        this.champignon_prefere = champignon_prefere;
    }

    public Integer getDescription_index() {
        return description_index;
    }

    public void setDescription_index(Integer description_index) {
        this.description_index = description_index;
    }

    public float getScoring() {
        return scoring;
    }

    public void setScoring(float scoring) {
        this.scoring = scoring;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
