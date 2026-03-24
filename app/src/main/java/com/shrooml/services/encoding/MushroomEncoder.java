package com.shrooml.services.encoding;

import java.util.HashMap;
import java.util.Map;

public class MushroomEncoder {

    private static final Map<String, Integer> CAP_SHAPE_ENCODING = createMap(
            "b", 0, "c", 1, "f", 2, "k", 3, "s", 4, "x", 5
    );

    private static final Map<String, Integer> CAP_SURFACE_ENCODING = createMap(
            "f", 0, "g", 1, "s", 2, "y", 3
    );

    private static final Map<String, Integer> CAP_COLOR_ENCODING = createMap(
            "b", 0, "c", 1, "e", 2, "g", 3, "n", 4, "p", 5, "r", 6, "u", 7, "w", 8, "y", 9, "z", 10
    );

    private static final Map<String, Integer> BRUISES_ENCODING = createMap(
            "f", 0, "t", 1
    );

    private static final Map<String, Integer> ODOR_ENCODING = createMap(
            "a", 0, "c", 1, "f", 2, "l", 3, "m", 4, "n", 5, "p", 6, "s", 7, "y", 8
    );

    private static final Map<String, Integer> GILL_ATTACHMENT_ENCODING = createMap(
            "a", 0, "d", 1, "f", 2, "n", 3
    );

    private static final Map<String, Integer> GILL_SPACING_ENCODING = createMap(
            "c", 0, "w", 1, "d", 2
    );

    private static final Map<String, Integer> GILL_SIZE_ENCODING = createMap(
            "b", 0, "n", 1
    );

    private static final Map<String, Integer> GILL_COLOR_ENCODING = createMap(
            "b", 0, "e", 1, "g", 2, "h", 3, "k", 4, "l", 5, "o", 6, "p", 7, "u", 8, "r", 9, "w", 10, "y", 11
    );

    private static final Map<String, Integer> STALK_SHAPE_ENCODING = createMap(
            "e", 0, "t", 1
    );

    private static final Map<String, Integer> STALK_ROOT_ENCODING = createMap(
            "b", 0, "c", 1, "u", 2, "e", 3, "z", 4, "r", 5, "?", 6
    );

    private static final Map<String, Integer> STALK_SURFACE_ABOVE_RING_ENCODING = createMap(
            "f", 0, "s", 1, "k", 2, "y", 3
    );

    private static final Map<String, Integer> STALK_SURFACE_BELOW_RING_ENCODING = createMap(
            "f", 0, "s", 1, "k", 2, "y", 3
    );

    private static final Map<String, Integer> STALK_COLOR_ABOVE_RING_ENCODING = createMap(
            "b", 0, "e", 1, "c", 2, "g", 3, "o", 4, "p", 5, "r", 6, "w", 7, "y", 8
    );

    private static final Map<String, Integer> STALK_COLOR_BELOW_RING_ENCODING = createMap(
            "b", 0, "e", 1, "c", 2, "g", 3, "o", 4, "p", 5, "r", 6, "w", 7, "y", 8
    );

    private static final Map<String, Integer> VEIL_TYPE_ENCODING = createMap(
            "p", 0, "u", 1
    );

    private static final Map<String, Integer> VEIL_COLOR_ENCODING = createMap(
            "n", 0, "o", 1, "w", 2, "y", 3
    );

    private static final Map<String, Integer> RING_NUMBER_ENCODING = createMap(
            "n", 0, "o", 1, "t", 2
    );

    private static final Map<String, Integer> RING_TYPE_ENCODING = createMap(
            "e", 0, "f", 1, "l", 2, "n", 3, "p", 4, "r", 5, "z", 6, "?", 7
    );

    private static final Map<String, Integer> SPORE_PRINT_COLOR_ENCODING = createMap(
            "k", 0, "n", 1, "b", 2, "h", 3, "g", 4, "o", 5, "u", 6, "r", 7, "w", 8, "y", 9
    );

    private static final Map<String, Integer> POPULATION_ENCODING = createMap(
            "a", 0, "c", 1, "n", 2, "s", 3, "v", 4, "y", 5
    );

    private static final Map<String, Integer> HABITAT_ENCODING = createMap(
            "g", 0, "l", 1, "m", 2, "p", 3, "u", 4, "w", 5, "d", 6
    );

    private static Integer safeGet(Map<String, Integer> map, String key) {
        Integer result = map.get(key);
        return result != null ? result : -1;
    }

    public static Integer encodeCapShape(String value) {
        return safeGet(CAP_SHAPE_ENCODING, value);
    }

    public static Integer encodeCapSurface(String value) {
        return safeGet(CAP_SURFACE_ENCODING, value);
    }

    public static Integer encodeCapColor(String value) {
        return safeGet(CAP_COLOR_ENCODING, value);
    }

    public static Integer encodeBruises(String value) {
        return safeGet(BRUISES_ENCODING, value);
    }

    public static Integer encodeOdor(String value) {
        return safeGet(ODOR_ENCODING, value);
    }

    public static Integer encodeGillAttachment(String value) {
        return safeGet(GILL_ATTACHMENT_ENCODING, value);
    }

    public static Integer encodeGillSpacing(String value) {
        return safeGet(GILL_SPACING_ENCODING, value);
    }

    public static Integer encodeGillSize(String value) {
        return safeGet(GILL_SIZE_ENCODING, value);
    }

    public static Integer encodeGillColor(String value) {
        return safeGet(GILL_COLOR_ENCODING, value);
    }

    public static Integer encodeStalkShape(String value) {
        return safeGet(STALK_SHAPE_ENCODING, value);
    }

    public static Integer encodeStalkRoot(String value) {
        return safeGet(STALK_ROOT_ENCODING, value);
    }

    public static Integer encodeStalkSurfaceAboveRing(String value) {
        return safeGet(STALK_SURFACE_ABOVE_RING_ENCODING, value);
    }

    public static Integer encodeStalkSurfaceBelowRing(String value) {
        return safeGet(STALK_SURFACE_BELOW_RING_ENCODING, value);
    }

    public static Integer encodeStalkColorAboveRing(String value) {
        return safeGet(STALK_COLOR_ABOVE_RING_ENCODING, value);
    }

    public static Integer encodeStalkColorBelowRing(String value) {
        return safeGet(STALK_COLOR_BELOW_RING_ENCODING, value);
    }

    public static Integer encodeVeilType(String value) {
        return safeGet(VEIL_TYPE_ENCODING, value);
    }

    public static Integer encodeVeilColor(String value) {
        return safeGet(VEIL_COLOR_ENCODING, value);
    }

    public static Integer encodeRingNumber(String value) {
        return safeGet(RING_NUMBER_ENCODING, value);
    }

    public static Integer encodeRingType(String value) {
        return safeGet(RING_TYPE_ENCODING, value);
    }

    public static Integer encodeSporeColor(String value) {
        return safeGet(SPORE_PRINT_COLOR_ENCODING, value);
    }

    public static Integer encodePopulation(String value) {
        return safeGet(POPULATION_ENCODING, value);
    }

    public static Integer encodeHabitat(String value) {
        return safeGet(HABITAT_ENCODING, value);
    }

    private static Map<String, Integer> createMap(Object... args) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], (Integer) args[i + 1]);
        }
        return map;
    }
}