package com.shrooml.services.api.Requests;

import java.util.List;
import java.util.Map;

public class EvalAARequest {
    private List<Map<String,Object>> X;
    private List<String> y;

    public EvalAARequest(List<Map<String,Object>> X, List<String> y){
        this.X = X;
        this.y = y;
    }

    public void setX(List<Map<String, Object>> x) {
        X = x;
    }

    public void setY(List<String> y) {
        this.y = y;
    }

    public List<Map<String, Object>> getX() {
        return X;
    }

    public List<String> getY() {
        return y;
    }
}
