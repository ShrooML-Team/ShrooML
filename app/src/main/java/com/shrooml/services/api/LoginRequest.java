package com.shrooml.services.api;

public class LoginRequest {
    private String identifiant;
    private String mot_de_passe;

    public LoginRequest(String identifiant, String mot_de_passe) {
        this.identifiant = identifiant;
        this.mot_de_passe = mot_de_passe;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }
}
