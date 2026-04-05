package com.shrooml.services.api;

import com.google.gson.annotations.SerializedName;

/**
 * FitAAResponse - Réponse de l'endpoint /fit
 *
 * JSON: {"status": "model trained"}
 */
public class FitAAResponse {

    @SerializedName("status")
    private String status;

    public FitAAResponse() {}

    public FitAAResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FitAAResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}