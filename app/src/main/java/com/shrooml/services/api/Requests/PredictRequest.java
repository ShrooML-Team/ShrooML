package com.shrooml.services.api.Requests;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * PredictRequest - Requête pour l'endpoint /predict
 *
 * Format exact attendu par l'API (basé sur schemas.py):
 * {
 *   "X": [
 *     {"cap-shape": 5, "cap-surface": 2, ...}
 *   ]
 * }
 *
 * IMPORTANT: PAS de champ "y" pour /predict!
 * Le modèle Pydantic ne le reconnaît pas → erreur 422
 */
public class PredictRequest {

    private List<Map<String, Integer>> X;

    public PredictRequest() {
        this.X = new ArrayList<>();
    }

    public PredictRequest(List<Map<String, Integer>> X) {
        this.X = X;
    }

    public List<Map<String, Integer>> getX() {
        return X;
    }

    public void setX(List<Map<String, Integer>> X) {
        this.X = X;
    }

    @Override
    public String toString() {
        return "PredictRequest{" +
                "X=" + X +
                '}';
    }
}