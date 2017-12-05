package com.ffe.traveller.classic.views;

import com.ffe.traveller.classic.isUniversalPlanetaryProfile;
import com.ffe.traveller.classic.models.IBody;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author markknights
 */
public class Planet {
    final static String PREFIX = "PSR ";

    private static final double maxLand = 0.2;
    private static final double minLand = 0.2;
    private static final double water = 0.02;
    private static final double maxIce = 0.85;
    private static final double maxCloud = 0.8;
    private static final double minIce = 0.55;
    private static final double minCloud = 0.4;

    public static final int Ring = 0;
    public static final int Small = -1;

    private int maxOrbits = 0;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Map<Integer, Planet> satellites = new HashMap<>();

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String sector;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String subsector;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected String name;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected int hexLocation;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PROTECTED)
    protected isUniversalPlanetaryProfile profile;

    @Setter(AccessLevel.PROTECTED)
    protected Boolean navalBase;

    @Setter(AccessLevel.PROTECTED)
    protected Boolean scoutBase;


    /**
     * Produces an unnamed, unidentified planet
     */
    protected Planet() {
        name = "Unnamed";
        hexLocation = -1;
    }

    /**
     * @param identified Generates an unexplored planet but one that may have been identified
     *                   by science with a scientifically generated name.  If not it will be
     *                   unnamed.  If the hex location is unknown enter a negative number if
     *                   a location is to be entered enter it in the integer format CCNN where
     *                   C is the column number and N is the hex number
     */
    protected Planet(boolean identified, int hexLocale) {
        if (identified) {
            name = getScientificName();
        } else {
            name = "Unnamed";
        }

        if (hexLocale >= 0) {
            hexLocation = hexLocale;
        } else {
            hexLocation = -1;
        }

    }

    /**
     * @param planetName
     * @param hexLocale
     * @param upp
     * @param naval
     * @param scout      Generates a fully formed planet.  Hex location is expected but if it is
     *                   not yet placed put a negative number into the hexLocale parameter
     */
    protected Planet(@Null String planetName, @Null Integer hexLocale, isUniversalPlanetaryProfile upp, Boolean naval, Boolean scout) {
        name = planetName;

        if (hexLocale != null) {
            hexLocation = hexLocale;
        }

        profile = upp;
        if(naval != null) {
            navalBase = naval;
        }

        if(scout != null) {
            scoutBase = scout;
        }
    }

    /**
     * @return
     */
    protected static String getScientificName() {
        String scienceName;
        Random generator = new Random();
        int charValue = 97 + (Math.abs(generator.nextInt() % 26));
        char postfix = (char) charValue;

        scienceName = PREFIX + Math.abs(generator.nextInt() % 10000) + "+" + Math.abs(generator.nextInt() % 100) + "-" + postfix;
        return scienceName;
    }

    public IBody.Type getPlanetType() {
        if (profile.getDiameter() == 0)
            return IBody.Type.PLANETOID_BELT;
        else
            return IBody.Type.ROCKY_PLANET;
    }

}
