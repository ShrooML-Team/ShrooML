package com.shrooml.services.api.Requests;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * PredictRequest - Requête pour l'endpoint /predict
 *
 * Envoie les features (X) ET un Y vide
 * (au cas où le serveur en aurait besoin)
 *
 * JSON généré:
 * {
 *   "X": [
 *     {"cap-shape": 5, "cap-surface": 2, ...}
 *   ],
 *   "y": []
 * }
 */
public class PredictRequest {

    private List<Map<String, Object>> X;
    private List<String> y;

    public PredictRequest() {
        this.X = new ArrayList<>();
        this.y = new ArrayList<>();
    }

    // Constructeur avec juste X
    public PredictRequest(List<Map<String, Object>> X) {
        this.X = X;
        this.y = new ArrayList<>();  // Y vide
    }

    // Constructeur avec X et Y
    public PredictRequest(List<Map<String, Object>> X, List<String> y) {
        this.X = X;
        this.y = y;
    }

    public List<Map<String, Object>> getX() {
        return X;
    }

    public void setX(List<Map<String, Object>> X) {
        this.X = X;
    }

    public List<String> getY() {
        return y;
    }

    public void setY(List<String> y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "PredictRequest{" +
                "X=" + X +
                ", y=" + y +
                '}';
    }
}