package com.shrooml.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MushroomCompleteEntity {

    // --- Getters & Setters ---

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

    public List<String> getSeason() {
        return season;
    }

    public void setSeason(List<String> season) {
        this.season = season;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(int minHumidity) {
        this.minHumidity = minHumidity;
    }

    public List<String> getHabitat() {
        return habitat;
    }

    public void setHabitat(List<String> habitat) {
        this.habitat = habitat;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // --- Fields mapping JSON ---

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

    @SerializedName("season")
    private List<String> season;

    @SerializedName("min_temp")
    private int minTemp;

    @SerializedName("max_temp")
    private int maxTemp;

    @SerializedName("min_humidity")
    private int minHumidity;

    @SerializedName("habitat")
    private List<String> habitat;

    @SerializedName("notes")
    private String notes;
}
