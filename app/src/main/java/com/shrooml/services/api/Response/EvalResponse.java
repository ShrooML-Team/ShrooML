package com.shrooml.services.api;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * EvalResponse - Réponse de l'endpoint /eval
 *
 * JSON: {"scores": {"accuracy": 0.95, "precision": 0.94, ...}}
 */
public class EvalResponse {

    @SerializedName("scores")
    private Map<String, Object> scores;

    public EvalResponse() {}

    public EvalResponse(Map<String, Object> scores) {
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
        return "EvalResponse{" +
                "scores=" + scores +
                '}';
    }
}