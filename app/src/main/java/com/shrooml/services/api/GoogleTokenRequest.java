package com.shrooml.services.api;

public class GoogleTokenRequest {
    private String idToken;
    private String platform;

    public GoogleTokenRequest(String idToken, String platform) {
        this.idToken = idToken;
        this.platform = platform;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
