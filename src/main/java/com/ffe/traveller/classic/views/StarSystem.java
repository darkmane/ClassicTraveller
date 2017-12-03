package com.ffe.traveller.classic.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ffe.traveller.classic.models.Star_System;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * Created by darkmane on 1/15/15.
 */
public class StarSystem {


    public StarSystem(Star_System ss) {
        this.zone = TravelZone.Green;
        if (ss.getTravel_zone() == null) {
            switch (ss.getTravel_zone().getName().toLowerCase()) {

                case "yellow":
                    this.zone = TravelZone.Amber;
                    break;
                case "red":
                    this.zone = TravelZone.Red;
                    break;

            }
        }

    }

    public enum Zone {
        UNAVAILABLE, INNER, HABITABLE, OUTER
    }

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Planet mainWorld;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private TravelZone zone;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private HashMap<Star.StarPosition, Star> stars;


    @Getter
    HashMap<Integer, Planet> orbits;


    private static Map<String, Object> orbitalDistanceMap = new HashMap<>();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int maxOrbits;


    public UniversalPlanetaryProfile getProfile() {
        return mainWorld.getProfile();
    }

    protected StarSystem() {
        mainWorld = PlanetMaker.CreatePlanet(null, null, null, null, null, null, null, null, null, null, null, null, null);

        stars = new HashMap<>();

        zone = TravelZone.Green;

        orbits = new HashMap<>();
    }

    @JsonProperty
    public Integer getMainWorldOrbit() {
        Integer orbit = null;
        for (Integer i : orbits.keySet()) {
            if (orbits.get(i) == mainWorld)
                orbit = i;
        }
        return orbit;
    }

    @SuppressWarnings("unchecked")
    private static void loadProperties() {

        InputStream input;

        if (orbitalDistanceMap.isEmpty()) {

            input = Star.class.getResourceAsStream("orbital_distance.yml");

            Yaml yaml = new Yaml();
            orbitalDistanceMap = (Map<String, Object>) yaml.load(input);
        }

    }


}
