package com.shrooml.models;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MushroomCompleteEntityParsingTest {

    @Test
    public void gsonParsing_mapsSnakeCaseJsonToModelFields() {
        String json = "{" +
                "\"scientific_name\":\"Boletus edulis\"," +
                "\"common_name\":\"Cep\"," +
                "\"edibility\":\"edible\"," +
                "\"toxicity\":\"none\"," +
                "\"psychoactive\":false," +
                "\"image_url\":\"https://img.example/boletus.jpg\"," +
                "\"season\":[\"summer\",\"autumn\"]," +
                "\"min_temp\":10," +
                "\"max_temp\":22," +
                "\"min_humidity\":65," +
                "\"habitat\":[\"forest\"]," +
                "\"notes\":\"Excellent comestible\"," +
                "\"recipe\":{\"name\":\"Poelee de cepes\",\"category\":\"Main\",\"ingredients\":[\"cepes\",\"garlic\"]}" +
                "}";

        MushroomCompleteEntity entity = new Gson().fromJson(json, MushroomCompleteEntity.class);

        assertEquals("Boletus edulis", entity.getScientificName());
        assertEquals("Cep", entity.getCommonName());
        assertEquals("edible", entity.getEdibility());
        assertEquals("none", entity.getToxicity());
        assertFalse(entity.isPsychoactive());
        assertEquals("https://img.example/boletus.jpg", entity.getImageUrl());
        assertEquals(2, entity.getSeason().size());
        assertEquals(10, entity.getMinTemp());
        assertEquals(22, entity.getMaxTemp());
        assertEquals(65, entity.getMinHumidity());
        assertEquals("forest", entity.getHabitat().get(0));
        assertEquals("Excellent comestible", entity.getNotes());

        assertNotNull(entity.getRecipe());
        assertEquals("Poelee de cepes", entity.getRecipe().getName());
        assertEquals("Main", entity.getRecipe().getCategory());
        assertEquals(2, entity.getRecipe().getIngredients().size());
    }
}
