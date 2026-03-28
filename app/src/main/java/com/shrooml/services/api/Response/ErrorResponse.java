package com.shrooml.services.api.Response;

/**
 * ErrorResponse
 *
 * Classe simple pour gérer les erreurs de l'API
 * Version compatible et minimale
 */
public class ErrorResponse {

    private int code;
    private String message;

    /**
     * Constructeur simple
     */
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getters
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Vérifier si c'est une erreur d'authentification (401)
     */
    public boolean isAuthenticationError() {
        return code == 401;
    }

    /**
     * Vérifier si c'est une erreur de validation (422)
     */
    public boolean isValidationError() {
        return code == 422;
    }

    /**
     * Vérifier si c'est une erreur serveur (5xx)
     */
    public boolean isServerError() {
        return code >= 500;
    }

    /**
     * Obtenir un message user-friendly
     */
    public String getUserFriendlyMessage() {
        switch (code) {
            case 400:
                return "Requête malformée";
            case 401:
                return "Authentification échouée";
            case 403:
                return "Accès refusé";
            case 404:
                return "Ressource non trouvée";
            case 422:
                return "Données invalides";
            case 500:
                return "Erreur serveur";
            default:
                return "Erreur " + code;
        }
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}