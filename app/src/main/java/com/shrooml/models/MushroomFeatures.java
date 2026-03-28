package com.shrooml.models;

import java.util.HashMap;
import java.util.Map;

/**
 * MushroomFeatures
 *
 * Modèle de données pour les features de champignons
 * Contient toutes les 23 caractéristiques + la classe cible
 */
public class MushroomFeatures {

    // Features
    private String capShape;           // x, b, f, k, s
    private String capSurface;         // f, g, y, s
    private String capColor;           // n, b, c, g, r, p, u, e, w, y
    private String bruises;            // t, f
    private String odor;               // a, l, c, y, f, m, n, p, s
    private String gillAttachment;     // a, d, f, n
    private String gillSpacing;        // c, w, d
    private String gillSize;           // b, n
    private String gillColor;          // k, n, b, h, o, p, u, e, w, y, g, r
    private String stalkShape;         // e, t
    private String stalkRoot;          // b, c, u, e, z, r
    private String stalkSurfaceAboveRing;  // f, y, k, s
    private String stalkSurfaceBelowRing;  // f, y, k, s
    private String stalkColorAboveRing;    // b, c, e, g, n, o, p, w, y
    private String stalkColorBelowRing;    // b, c, e, g, n, o, p, w, y
    private String veilType;           // p, u
    private String veilColor;          // n, o, w, y
    private String ringNumber;         // n, o, t
    private String ringType;           // c, e, f, l, n, p, s, z
    private String sporePrintColor;    // k, n, b, h, o, r, u, w, y, g
    private String population;         // a, c, n, s, v, y
    private String habitat;            // g, l, m, p, u, w, d

    // Constructeur vide
    public MushroomFeatures() {}

    // Getters et Setters
    public String getCapShape() { return capShape; }
    public void setCapShape(String capShape) { this.capShape = capShape; }

    public String getCapSurface() { return capSurface; }
    public void setCapSurface(String capSurface) { this.capSurface = capSurface; }

    public String getCapColor() { return capColor; }
    public void setCapColor(String capColor) { this.capColor = capColor; }

    public String getBruises() { return bruises; }
    public void setBruises(String bruises) { this.bruises = bruises; }

    public String getOdor() { return odor; }
    public void setOdor(String odor) { this.odor = odor; }

    public String getGillAttachment() { return gillAttachment; }
    public void setGillAttachment(String gillAttachment) { this.gillAttachment = gillAttachment; }

    public String getGillSpacing() { return gillSpacing; }
    public void setGillSpacing(String gillSpacing) { this.gillSpacing = gillSpacing; }

    public String getGillSize() { return gillSize; }
    public void setGillSize(String gillSize) { this.gillSize = gillSize; }

    public String getGillColor() { return gillColor; }
    public void setGillColor(String gillColor) { this.gillColor = gillColor; }

    public String getStalkShape() { return stalkShape; }
    public void setStalkShape(String stalkShape) { this.stalkShape = stalkShape; }

    public String getStalkRoot() { return stalkRoot; }
    public void setStalkRoot(String stalkRoot) { this.stalkRoot = stalkRoot; }

    public String getStalkSurfaceAboveRing() { return stalkSurfaceAboveRing; }
    public void setStalkSurfaceAboveRing(String stalkSurfaceAboveRing) { this.stalkSurfaceAboveRing = stalkSurfaceAboveRing; }

    public String getStalkSurfaceBelowRing() { return stalkSurfaceBelowRing; }
    public void setStalkSurfaceBelowRing(String stalkSurfaceBelowRing) { this.stalkSurfaceBelowRing = stalkSurfaceBelowRing; }

    public String getStalkColorAboveRing() { return stalkColorAboveRing; }
    public void setStalkColorAboveRing(String stalkColorAboveRing) { this.stalkColorAboveRing = stalkColorAboveRing; }

    public String getStalkColorBelowRing() { return stalkColorBelowRing; }
    public void setStalkColorBelowRing(String stalkColorBelowRing) { this.stalkColorBelowRing = stalkColorBelowRing; }

    public String getVeilType() { return veilType; }
    public void setVeilType(String veilType) { this.veilType = veilType; }

    public String getVeilColor() { return veilColor; }
    public void setVeilColor(String veilColor) { this.veilColor = veilColor; }

    public String getRingNumber() { return ringNumber; }
    public void setRingNumber(String ringNumber) { this.ringNumber = ringNumber; }

    public String getRingType() { return ringType; }
    public void setRingType(String ringType) { this.ringType = ringType; }

    public String getSporePrintColor() { return sporePrintColor; }
    public void setSporePrintColor(String sporePrintColor) { this.sporePrintColor = sporePrintColor; }

    public String getPopulation() { return population; }
    public void setPopulation(String population) { this.population = population; }

    public String getHabitat() { return habitat; }
    public void setHabitat(String habitat) { this.habitat = habitat; }

    /**
     * Convertir en Map pour l'API (format expected by AutoML API)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("cap_shape", capShape);
        map.put("cap_surface", capSurface);
        map.put("cap_color", capColor);
        map.put("bruises", bruises);
        map.put("odor", odor);
        map.put("gill_attachment", gillAttachment);
        map.put("gill_spacing", gillSpacing);
        map.put("gill_size", gillSize);
        map.put("gill_color", gillColor);
        map.put("stalk_shape", stalkShape);
        map.put("stalk_root", stalkRoot);
        map.put("stalk_surface_above_ring", stalkSurfaceAboveRing);
        map.put("stalk_surface_below_ring", stalkSurfaceBelowRing);
        map.put("stalk_color_above_ring", stalkColorAboveRing);
        map.put("stalk_color_below_ring", stalkColorBelowRing);
        map.put("veil_type", veilType);
        map.put("veil_color", veilColor);
        map.put("ring_number", ringNumber);
        map.put("ring_type", ringType);
        map.put("spore_print_color", sporePrintColor);
        map.put("population", population);
        map.put("habitat", habitat);
        return map;
    }

    @Override
    public String toString() {
        return "MushroomFeatures{" +
                "capShape='" + capShape + '\'' +
                ", capSurface='" + capSurface + '\'' +
                ", capColor='" + capColor + '\'' +
                ", bruises='" + bruises + '\'' +
                ", odor='" + odor + '\'' +
                ", gillAttachment='" + gillAttachment + '\'' +
                ", gillSpacing='" + gillSpacing + '\'' +
                ", gillSize='" + gillSize + '\'' +
                ", gillColor='" + gillColor + '\'' +
                '}';
    }
}