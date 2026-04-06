package com.shrooml.models;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MushroomEntityTest {

    @Test
    public void constructor_populatesAllFields() {
        String[] season = {"spring", "autumn"};
        String[] habitat = {"forest", "meadow"};

        MushroomEntity entity = new MushroomEntity();
        entity.setScientific_name("Agaricus bisporus");
        entity.setCommon_name("Champignon de Paris");
        entity.setEdibility("edible");
        entity.setSeason(season);
        entity.setMin_temp(12);
        entity.setMax_temp(24);
        entity.setMin_humidity(60);
        entity.setHabitat(habitat);

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
