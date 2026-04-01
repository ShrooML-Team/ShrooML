package com.shrooml.services.api;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * EvalAAResponse - Réponse de l'endpoint /eval
 *
 * JSON: {"scores": {"accuracy": 0.95, "precision": 0.94, ...}}
 */
public class EvalAAResponse {

    @SerializedName("scores")
    private Map<String, Object> scores;

    public EvalAAResponse() {}

    public EvalAAResponse(Map<String, Object> scores) {
        this.scores = scores;
    }

    public Map<String, Object> getScores() {
        return scores;
    }

    public void setScores(Map<String, Object> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "EvalAAResponse{" +
                "scores=" + scores +
                '}';
    }
}