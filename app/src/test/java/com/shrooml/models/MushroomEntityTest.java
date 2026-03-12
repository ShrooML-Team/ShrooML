package com.shrooml.models;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MushroomEntityTest {

    @Test
    public void constructor_populatesAllFields() {
        String[] season = {"spring", "autumn"};
        String[] habitat = {"forest", "meadow"};

        MushroomEntity entity = new MushroomEntity(
                "Agaricus bisporus",
                "Champignon de Paris",
                "edible",
                season,
                12,
                24,
                60,
                habitat
        );

        assertEquals("Agaricus bisporus", entity.getScientific_name());
        assertEquals("Champignon de Paris", entity.getCommon_name());
        assertEquals("edible", entity.getEdibility());
        assertArrayEquals(season, entity.getSeason());
        assertEquals(12, entity.getMin_temp());
        assertEquals(24, entity.getMax_temp());
        assertEquals(60, entity.getMin_humidity());
        assertArrayEquals(habitat, entity.getHabitat());
    }

    @Test
    public void imageSetterAndGetter_roundTripValue() {
        MushroomEntity entity = new MushroomEntity();

        entity.setImage("https://example.com/mushroom.jpg");

        assertEquals("https://example.com/mushroom.jpg", entity.getImage());
    }
}
