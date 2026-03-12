package com.shrooml.services.encoding;

import java.util.HashMap;
import java.util.Map;

/**
 * MushroomEncoder - Encode les données mushroom (lettres → nombres)
 *
 * Les données du CSV sont en lettres, mais l'API AutoML exige des nombres.
 * Cette classe mappe chaque valeur textuelle vers son encodage numérique.
 *
 * Exemple:
 * MushroomEncoder.encodeCapShape("x") → 5
 * MushroomEncoder.encodeHabitat("w") → 5
 */
public class MushroomEncoder {

    // ========== ENCODAGES PAR COLONNE ==========

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

    // ========== MÉTHODES D'ENCODAGE ==========

    /**
     * Encode cap-shape
     * b→0, c→1, f→2, k→3, s→4, x→5
     */
    public static Integer encodeCapShape(String value) {
        return CAP_SHAPE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode cap-surface
     * f→0, g→1, s→2, y→3
     */
    public static Integer encodeCapSurface(String value) {
        return CAP_SURFACE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode cap-color
     * b→0, c→1, e→2, g→3, n→4, p→5, r→6, u→7, w→8, y→9, z→10
     */
    public static Integer encodeCapColor(String value) {
        return CAP_COLOR_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode bruises
     * f→0, t→1
     */
    public static Integer encodeBruises(String value) {
        return BRUISES_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode odor
     * a→0, c→1, f→2, l→3, m→4, n→5, p→6, s→7, y→8
     */
    public static Integer encodeOdor(String value) {
        return ODOR_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode gill-attachment
     * a→0, d→1, f→2, n→3
     */
    public static Integer encodeGillAttachment(String value) {
        return GILL_ATTACHMENT_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode gill-spacing
     * c→0, w→1, d→2
     */
    public static Integer encodeGillSpacing(String value) {
        return GILL_SPACING_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode gill-size
     * b→0, n→1
     */
    public static Integer encodeGillSize(String value) {
        return GILL_SIZE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode gill-color
     * b→0, e→1, g→2, h→3, k→4, l→5, o→6, p→7, u→8, r→9, w→10, y→11
     */
    public static Integer encodeGillColor(String value) {
        return GILL_COLOR_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-shape
     * e→0, t→1
     */
    public static Integer encodeStalkShape(String value) {
        return STALK_SHAPE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-root
     * b→0, c→1, u→2, e→3, z→4, r→5, ?→6
     */
    public static Integer encodeStalkRoot(String value) {
        return STALK_ROOT_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-surface-above-ring
     * f→0, s→1, k→2, y→3
     */
    public static Integer encodeStalkSurfaceAboveRing(String value) {
        return STALK_SURFACE_ABOVE_RING_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-surface-below-ring
     * f→0, s→1, k→2, y→3
     */
    public static Integer encodeStalkSurfaceBelowRing(String value) {
        return STALK_SURFACE_BELOW_RING_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-color-above-ring
     * b→0, e→1, c→2, g→3, o→4, p→5, r→6, w→7, y→8
     */
    public static Integer encodeStalkColorAboveRing(String value) {
        return STALK_COLOR_ABOVE_RING_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode stalk-color-below-ring
     * b→0, e→1, c→2, g→3, o→4, p→5, r→6, w→7, y→8
     */
    public static Integer encodeStalkColorBelowRing(String value) {
        return STALK_COLOR_BELOW_RING_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode veil-type
     * p→0, u→1
     */
    public static Integer encodeVeilType(String value) {
        return VEIL_TYPE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode veil-color
     * n→0, o→1, w→2, y→3
     */
    public static Integer encodeVeilColor(String value) {
        return VEIL_COLOR_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode ring-number
     * n→0, o→1, t→2
     */
    public static Integer encodeRingNumber(String value) {
        return RING_NUMBER_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode ring-type
     * e→0, f→1, l→2, n→3, p→4, r→5, z→6, ?→7
     */
    public static Integer encodeRingType(String value) {
        return RING_TYPE_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode spore-print-color
     * k→0, n→1, b→2, h→3, g→4, o→5, u→6, r→7, w→8, y→9
     */
    public static Integer encodeSporeColor(String value) {
        return SPORE_PRINT_COLOR_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode population
     * a→0, c→1, n→2, s→3, v→4, y→5
     */
    public static Integer encodePopulation(String value) {
        return POPULATION_ENCODING.getOrDefault(value, -1);
    }

    /**
     * Encode habitat
     * g→0, l→1, m→2, p→3, u→4, w→5, d→6
     */
    public static Integer encodeHabitat(String value) {
        return HABITAT_ENCODING.getOrDefault(value, -1);
    }

    // ========== HELPER METHODS ==========

    /**
     * Helper pour créer une map facilement
     */
    private static Map<String, Integer> createMap(Object... args) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], (Integer) args[i + 1]);
        }
        return map;
    }
}