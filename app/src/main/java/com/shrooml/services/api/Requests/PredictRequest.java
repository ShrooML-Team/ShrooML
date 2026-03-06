package com.shrooml.services.api.Requests;

import java.util.List;
import java.util.Map;

public class PredictRequest {
    private List<Map<String,Object>> X;
    public PredictRequest(List<Map<String,Object>> X){
        this.X = X;
    }

    public List<Map<String, Object>> getX() {
        return X;
    }

    public void setX(List<Map<String, Object>> x) {
        X = x;
    }
}
