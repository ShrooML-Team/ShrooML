package com.shrooml.models;

public class ResultEntity {

    private ClassificationEntity classification;

    private IsMushroomEntity is_mushroom;

    public ClassificationEntity getClassification() {
        return classification;
    }

    public void setClassification(ClassificationEntity classification) {
        this.classification = classification;
    }

    public IsMushroomEntity getIs_mushroom() {
        return this.is_mushroom;
    }

    public void setIs_mushroom(IsMushroomEntity is_mushroom) {
        this.is_mushroom = is_mushroom;
    }
}
