package com.shrooml.services.api;

/**
 * Constantes regroupant les 22 caractéristiques (features) utilisées par l'API AutoML.
 * 
 * Les valeurs correspondent aux positions dans les Spinners (1-based index car 0 est "Choose...").
 */
public class MushroomAttributes {

    // Noms des champs (Keys)
    public static final String CAP_SHAPE = "cap-shape";
    public static final String CAP_SURFACE = "cap-surface";
    public static final String CAP_COLOR = "cap-color";
    public static final String BRUISES = "bruises";
    public static final String ODOR = "odor";
    public static final String GILL_ATTACHMENT = "gill-attachment";
    public static final String GILL_SPACING = "gill-spacing";
    public static final String GILL_SIZE = "gill-size";
    public static final String GILL_COLOR = "gill-color";
    public static final String STALK_SHAPE = "stalk-shape";
    public static final String STALK_ROOT = "stalk-root";
    public static final String STALK_SURFACE_ABOVE_RING = "stalk-surface-above-ring";
    public static final String STALK_SURFACE_BELOW_RING = "stalk-surface-below-ring";
    public static final String STALK_COLOR_ABOVE_RING = "stalk-color-above-ring";
    public static final String STALK_COLOR_BELOW_RING = "stalk-color-below-ring";
    public static final String VEIL_TYPE = "veil-type";
    public static final String VEIL_COLOR = "veil-color";
    public static final String RING_NUMBER = "ring-number";
    public static final String RING_TYPE = "ring-type";
    public static final String SPORE_PRINT_COLOR = "spore-print-color";
    public static final String POPULATION = "population";
    public static final String HABITAT = "habitat";

    // Valeurs possibles (Values) - Index 1-based
    
    public static class CapShape {
        public static final int BELL = 1;
        public static final int CONICAL = 2;
        public static final int CONVEX = 3;
        public static final int FLAT = 4;
        public static final int KNOBBED = 5;
        public static final int SUNKEN = 6;
    }

    public static class Bruises {
        public static final int NO = 1;
        public static final int YES = 2;
    }

    public static class Odor {
        public static final int ALMOND = 1;
        public static final int ANISE = 2;
        public static final int CREOSOTE = 3;
        public static final int FISHY = 4;
        public static final int FOUL = 5;
        public static final int MUSTY = 6;
        public static final int NONE = 7;
        public static final int PUNGENT = 8;
        public static final int SPICY = 9;
    }

    public static class Prediction {
        public static final int EDIBLE = 0;
        public static final int POISONOUS = 1;
    }
}
