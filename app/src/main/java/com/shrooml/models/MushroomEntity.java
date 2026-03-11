package com.shrooml.models;

public class MushroomEntity {
  private String scientific_name;

  private String common_name;
  
  private String edibility;
  
  private String season[];
  
  private int min_temp;
  
  private int max_temp;
  
  private int min_humidity;
  
  private String habitat[];

  private String image;

  public MushroomEntity() {}

  public MushroomEntity(String scientific_name, String common_name, String edibility,
      String[] season, int min_temp, int max_temp, int min_humidity, String[] habitat) {
    this.scientific_name = scientific_name;
    this.common_name = common_name;
    this.edibility = edibility;
    this.season = season;
    this.min_temp = min_temp;
    this.max_temp = max_temp;
    this.min_humidity = min_humidity;
    this.habitat = habitat;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getScientific_name() {
    return scientific_name;
  }
  
  public void setScientific_name(String scientific_name) {
    this.scientific_name = scientific_name;
  }

  public String getCommon_name() {
    return common_name;
  }

  public void setCommon_name(String common_name) {
    this.common_name = common_name;
  }

  public String getEdibility() {
    return edibility;
  }

  public void setEdibility(String edibility) {
    this.edibility = edibility;
  }
  public String[] getSeason() {
    return season;
  }

  public void setSeason(String[] season) {
    this.season = season;
  }

  public int getMin_temp() {
    return min_temp;
  }

  public void setMin_temp(int min_temp) {
    this.min_temp = min_temp;
  }

  public int getMax_temp() {
    return max_temp;
  }

  public void setMax_temp(int max_temp) {
    this.max_temp = max_temp;
  }

  public int getMin_humidity() {
    return min_humidity;
  }

  public void setMin_humidity(int min_humidity) {
    this.min_humidity = min_humidity;
  }

  public String[] getHabitat() {
    return habitat;
  }

  public void setHabitat(String[] habitat) {
    this.habitat = habitat;
  }

}