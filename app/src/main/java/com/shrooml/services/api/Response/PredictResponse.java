package com.shrooml.services.api.Response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * PredictResponse - Réponse de l'endpoint /predict
 *
 * predictions est List<Integer> (0 = edible, 1 = poisonous)
 */
public class PredictResponse {

    @SerializedName("predictions")
    private List<Integer> predictions;

    public PredictResponse() {
    }

    public PredictResponse(List<Integer> predictions) {
        this.predictions = predictions;
    }

    public List<Integer> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Integer> predictions) {
        this.predictions = predictions;
    }

    @Override
    public String toString() {
        return "PredictResponse{" +
                "predictions=" + predictions +
                '}';
    }
}