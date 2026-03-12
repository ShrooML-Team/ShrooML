package com.shrooml.models;

public class SuggestionEntity {
    private String name;

    private Double probability;

    public String getName(){
        return this.name;
    }

    public Double getProbability(){
        return this.probability;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }
}
