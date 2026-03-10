package com.shrooml.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class MushroomCompleteEntity {

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getEdibility() {
        return edibility;
    }

    public void setEdibility(String edibility) {
        this.edibility = edibility;
    }

    public String getToxicity() {
        return toxicity;
    }

    public void setToxicity(String toxicity) {
        this.toxicity = toxicity;
    }

    public boolean isPsychoactive() {
        return psychoactive;
    }

    public void setPsychoactive(boolean psychoactive) {
        this.psychoactive = psychoactive;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RecipeEntity getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }

    @SerializedName("scientific_name")
    private String scientificName;

    @SerializedName("common_name")
    private String commonName;

    @SerializedName("edibility")
    private String edibility;

    @SerializedName("toxicity")
    private String toxicity;

    @SerializedName("psychoactive")
    private boolean psychoactive;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("recipe")
    private RecipeEntity recipe;

    // Getters / setters...
}


