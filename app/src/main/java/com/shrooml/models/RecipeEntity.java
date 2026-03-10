package com.shrooml.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeEntity {

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("area")
    private String area;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("ingredients")
    private List<String> ingredients;

    @SerializedName("image")
    private String image;

    @SerializedName("source")
    private String source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
// Getters / setters...
}
