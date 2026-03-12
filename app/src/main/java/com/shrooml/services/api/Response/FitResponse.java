package com.shrooml.services.api;

import com.google.gson.annotations.SerializedName;

/**
 * FitResponse - Réponse de l'endpoint /fit
 *
 * JSON: {"status": "model trained"}
 */
public class FitResponse {

    @SerializedName("status")
    private String status;

    public FitResponse() {}

    public FitResponse(String status) {
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
        return "FitResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}