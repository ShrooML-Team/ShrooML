package com.shrooml.services.api;

public class RegisterRequest {
    private String identifiant;
    private String email;
    private String mot_de_passe;
    private String champignon_prefere;

    public RegisterRequest(String identifiant, String email, String mot_de_passe, String champignon_prefere) {
        this.identifiant = identifiant;
        this.email = email;
        this.mot_de_passe = mot_de_passe;
        this.champignon_prefere = champignon_prefere;
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

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public String getChampignon_prefere() {
        return champignon_prefere;
    }

    public void setChampignon_prefere(String champignon_prefere) {
        this.champignon_prefere = champignon_prefere;
    }
}
