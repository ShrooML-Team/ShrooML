package com.shrooml.services.api.Requests;

import java.util.List;
import java.util.Map;

public class FitRequest {
    private List<Map<String,Object>> X;
    private List<String> y;
    private Map<String,Object> automl_params;

    public FitRequest(List<Map<String,Object>> X, List<String> y,Map<String,Object> params){
        this.X = X;
        this.y = y;
        this.automl_params = params;
    }

    public List<Map<String, Object>> getX() {
        return X;
    }

    public Map<String, Object> getAutoml_params() {
        return automl_params;
    }

    public List<String> getY() {
        return y;
    }

    public void setAutoml_params(Map<String, Object> automl_params) {
        this.automl_params = automl_params;
    }

    public void setX(List<Map<String, Object>> x) {
        X = x;
    }

    public void setY(List<String> y) {
        this.y = y;
    }
}
