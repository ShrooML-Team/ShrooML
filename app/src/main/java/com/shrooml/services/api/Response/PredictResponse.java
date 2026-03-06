package com.shrooml.services.api.Response;

import java.util.List;

public class PredictResponse {
    private List<String> predictions;

    public List<String> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<String> predictions) {
        this.predictions = predictions;
    }
}
