package com.shrooml.services.api.Requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * FitAARequest - Requête pour l'endpoint /fit
 *
 * y est List<Integer> (0 = edible, 1 = poisonous)
 */
public class FitAARequest {

    @SerializedName("X")
    private List<Map<String, Integer>> X;

    @SerializedName("y")
    private List<Integer> y;

    @SerializedName("automl_params")
    private Map<String, Object> automl_params;

    public FitAARequest() {
    }

    public FitAARequest(List<Map<String, Integer>> X, List<Integer> y, Map<String, Object> params) {
        this.X = X;
        this.y = y;
        this.automl_params = params;
    }

    public List<Map<String, Integer>> getX() {
        return X;
    }

    public void setX(List<Map<String, Integer>> x) {
        X = x;
    }

    public List<Integer> getY() {
        return y;
    }

    public void setY(List<Integer> y) {
        this.y = y;
    }

    public Map<String, Object> getAutoml_params() {
        return automl_params;
    }

    public void setAutoml_params(Map<String, Object> automl_params) {
        this.automl_params = automl_params;
    }

    @Override
    public String toString() {
        return "FitAARequest{" +
                "X=" + X +
                ", y=" + y +
                ", automl_params=" + automl_params +
                '}';
    }
}