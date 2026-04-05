package com.shrooml.models;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IdentificationEntityGraphTest {

    @Test
    public void nestedEntities_roundTripValues() {
        SuggestionEntity suggestion = new SuggestionEntity();
        suggestion.setName("Boletus edulis");
        suggestion.setProbability(0.95);

        ClassificationEntity classification = new ClassificationEntity();
        classification.setSuggestions(Arrays.asList(suggestion));

        IsMushroomEntity isMushroom = new IsMushroomEntity();
        isMushroom.setBinary("true");

        ResultEntity result = new ResultEntity();
        result.setClassification(classification);
        result.setIs_mushroom(isMushroom);

        IdentificationEntity identification = new IdentificationEntity();
        identification.setResult(result);

        assertNotNull(identification.getResult());
        assertEquals(1, identification.getResult().getClassification().getSuggestions().size());
        assertEquals("Boletus edulis", identification.getResult().getClassification().getSuggestions().get(0).getName());
        assertEquals(0.95, identification.getResult().getClassification().getSuggestions().get(0).getProbability(), 0.0001);
        assertEquals("true", identification.getResult().getIs_mushroom().getBinary());
    }
}
