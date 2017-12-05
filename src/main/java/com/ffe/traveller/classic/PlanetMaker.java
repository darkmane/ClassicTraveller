package com.ffe.traveller.classic;


import com.ffe.traveller.classic.models.*;
import org.yaml.snakeyaml.Yaml;

import javax.validation.constraints.Null;
import java.io.InputStream;
import java.util.*;

import static com.ffe.traveller.util.DiceGenerator.*;


/**
 * @author darkmane
 */

public class PlanetMaker {

    private static Map<String, List<Integer>> satelliteOrbits = new HashMap<>();

    /**
     * @param planetName
     * @param hexLocale
     * @param starportType
     * @param planetSize
     * @param planetAtmosphere
     * @param hydroPercent
     * @param population
     * @param planetGovernment
     * @param law
     * @param techLevel
     * @param navalBase
     * @param scoutBase        Generates a fully formed planet.  Hex location is expected but if it is
     *                         not yet placed put a negative number into the hexLocale parameter
     */
    public static Planet CreatePlanet(@Null Random rng, @Null String planetName, @Null Integer hexLocale, @Null Starport starportType,
                                      @Null Byte planetSize, @Null Byte planetAtmosphere, @Null Byte hydroPercent,
                                      @Null Byte population, @Null Byte planetGovernment, @Null Byte law,
                                      @Null Byte techLevel, @Null Boolean navalBase, @Null Boolean scoutBase) {

        Planet p = new Planet();
        p.setStarport(starportType);
        p.setDiameter(planetSize);
        p.setAtmosphere(planetAtmosphere);
        p.setHydro(hydroPercent);
        p.setPopulation(population);
        p.setGovernment(planetGovernment);
        p.setLaw_level(law);
        p.setTech_level(techLevel);

        if (navalBase == null) {
            if ((p.getStarport() == Starport.A || p.getStarport() == Starport.B)
                    && rollDice(1) % 2 == 0) {
                navalBase = true;
            }
        }
        if (scoutBase == null) {
            if ((p.getStarport() == Starport.A || p.getStarport() == Starport.B
                    || p.getStarport() == Starport.C || p.getStarport() == Starport.D)
                    && rollDice(1) % 2 == 0) {
                scoutBase = true;
            }
        }


        return p;

    }

    /**
     * @param rng
     * @param zone
     * @param mainPlanet
     * @return
     */
    public static GasGiant CreateGasGiant(Random rng, Star_System.Zone zone, Planet mainPlanet) {
        GasGiant gg = new GasGiant();

        if (roll(rng) % 2 == 0) {
            gg.setType(IBody.Type.SMALL_GAS_GIANT);

        } else {
            gg.setType(IBody.Type.LARGE_GAS_GIANT);

        }
        return gg;
    }

    public static Collection<Planet> CreateGasGiantSatellites(Random rng, Star_System.Zone zone,
                                                              Planet mainPlanet, Planet gg) {

        int numberOfMoons;
        Map<Integer, Planet> moons = new HashMap<>();
        if (gg.getType() == IBody.Type.SMALL_GAS_GIANT) {
            numberOfMoons = rollDiceWithModifier(rng, 2, -4);
        } else {
            numberOfMoons = rollDice(rng, 2);
        }

        for (int counter = 0; counter < numberOfMoons; counter++) {
            Planet moon = CreateSatellite(rng, zone, mainPlanet, gg);
            int orbit = lookUpOrbit(rng, (moon.getDiameter() == 0));
            moons.put(orbit, moon);
        }

        return moons.values();
    }


    /**
     * @param rng
     * @param star
     * @param orbit
     * @param zone
     * @param mainPlanet
     * @return
     */
    public static Planet CreateMinorPlanet(Random rng, Star star, Integer orbit,
                                           Star_System.Zone zone, Planet mainPlanet) {
        Starport port = null;
        Planet s = new Planet();
        int size = rollDiceWithModifier(rng, 2, -2);

        if (orbit <= 2) {
            size -= (5 - orbit);
        }
        if (star.getStellarClass() == Star.StellarClass.M) {
            size -= 2;
        }


        int atmo = rollDiceWithModifier(rng, 2, -7) + size;
        int hydro = rollDiceWithModifier(rng, 2, -7) + size;
        int pop = rollDiceWithModifier(rng, 2, -2);
        int gov = rollDiceWithModifier(rng, 1, 0);
        int law = rollDiceWithModifier(rng, 1, -3) + mainPlanet.getLawLevel();
        switch (zone) {
            case INNER:
                atmo -= 2;
                hydro -= 4;
                pop -= 6;
                break;
            case HABITABLE:
                break;
            case OUTER:
                atmo -= 4;
                hydro -= 2;
                pop -= 5;
                break;
        }

        if (size < 1) {
            atmo = 0;
            hydro = 0;
        }

        //if(zone == OUTER && orbit - )

        if (size <= 4) {
            pop -= 2;
        }

        if (!(atmo == 5 || atmo == 6 || atmo == 8)) {
            pop -= 2;
        }

        if (size == 0) {
            pop = 0;
        }

        if (pop < 0) {
            pop = 0;
        }
        if (mainPlanet.getGovernment() == 6) {
            gov += pop;
        } else if (mainPlanet.getGovernment() >= 7) {
            gov += 1;
        }

        switch (gov) {
            case 1:
                gov = 0;
                break;
            case 2:
                gov = 1;
                break;
            case 3:
                gov = 2;
                break;
            case 4:
                gov = 3;
                break;
            default:
                gov = 6;
                break;
        }

        if (pop == 0) {
            gov = 0;
        }

        if (law < 0 || pop == 0) {
            law = 0;
        }

        if (size == 0) {
            size = Planet.Ring;
        } else if (size < 0) {
            size = Planet.Small;
        }

        if (atmo < 0) {
            atmo = 0;
        }

        if (hydro < 0) {
            hydro = 0;
        }

        s.setStarport(port);
        s.setDiameter((byte) size);
        s.setAtmosphere((byte) atmo);
        s.setHydro((byte) hydro);
        s.setPopulation((byte) pop);
        s.setGovernment((byte) gov);
        s.setLaw_level((byte) law);

        s.setTradeClassifications(calculateTradeFacilities(rng, s, mainPlanet, zone));

        int techLevel = mainPlanet.getTechnologicalLevel() - 1;

        if (s.getTradeClassifications().contains(TradeClassifications.Military)) {
            techLevel = mainPlanet.getTechnologicalLevel();
        }
        int mainWorldAtmosphere = mainPlanet.getAtmosphere();
        if (techLevel < 7 && !(mainWorldAtmosphere == 5 || mainWorldAtmosphere == 6 || mainWorldAtmosphere == 8)) {
            techLevel = 7;
        }
        s.setTech_level((byte)techLevel);

        int numberOfMoons = rollDiceWithModifier(rng, 1, -3);
        Map<Integer, Planet> moons = new HashMap<>();
        for (int counter = 0; counter < numberOfMoons; counter++) {
            Planet moon = CreateSatellite(rng, zone, mainPlanet, s);
            int o = lookUpOrbit(rng, (moon.getDiameter() == 0));
            moons.put(o, moon);
        }

        return s;
    }

    /**
     * @param rng
     * @param zone
     * @param mainPlanet
     * @param parentPlanet
     * @return
     */
    public static Planet CreateSatellite(Random rng, Star_System.Zone zone, Planet mainPlanet, Planet parentPlanet) {
        Starport port = null;
        Planet s = new Planet();
        int size = parentPlanet.getDiameter() - rollDice(rng, 1);
        int atmo = rollDiceWithModifier(rng, 2, -7) + size;
        int hydro = rollDiceWithModifier(rng, 2, -7) + size;
        int pop = rollDiceWithModifier(rng, 2, -2);
        int gov = rollDiceWithModifier(rng, 1, 0);
        int law = rollDiceWithModifier(rng, 1, -3) + mainPlanet.getLawLevel();

        if (parentPlanet.getType() == Planet.Type.SMALL_GAS_GIANT) {
            size = rollDiceWithModifier(rng, 2, -6);
        } else if (parentPlanet.getType() == Planet.Type.LARGE_GAS_GIANT) {
            size = rollDiceWithModifier(rng, 2, -4);
        }

        switch (zone) {
            case INNER:
                atmo -= 4;
                hydro -= 4;
                pop -= 6;
                break;
            case HABITABLE:
                break;
            case OUTER:
                atmo -= 4;
                hydro -= 2;
                pop -= 5;
                break;
        }

        if (size <= 1) {
            atmo = 0;
            hydro = 0;
        }

        if (size <= 4) {
            pop -= 2;
        }

        if (!(atmo == 5 || atmo == 6 || atmo == 8)) {
            pop -= 2;
        }

        if (size == 0) {
            pop = 0;
        }

        if (pop < 0) {
            pop = 0;
        }
        if (mainPlanet.getGovernment() == 6) {
            gov += pop;
        } else if (mainPlanet.getGovernment() >= 7) {
            gov += 1;
        }

        switch (gov) {
            case 1:
                gov = 0;
                break;
            case 2:
                gov = 1;
                break;
            case 3:
                gov = 2;
                break;
            case 4:
                gov = 3;
                break;
            default:
                gov = 6;
                break;
        }

        if (pop == 0) {
            gov = 0;
        }

        if (law < 0 || pop == 0) {
            law = 0;
        }

        if (size == 0) {
            size = Planet.Ring;
        } else if (size < 0) {
            size = Planet.Small;
        }

        if (atmo < 0) {
            atmo = 0;
        }

        if (hydro < 0) {
            hydro = 0;
        }

        s.setStarport(port);
        s.setDiameter((byte)size);
        s.setAtmosphere((byte)atmo);
        s.setHydro((byte)hydro);
        s.setPopulation((byte)pop);
        s.setGovernment((byte)gov);
        s.setLaw_level((byte)law);



        s.setTradeClassifications(calculateTradeFacilities(rng, s, mainPlanet, zone));

        int techLevel = mainPlanet.getTechnologicalLevel() - 1;
        if (s.getTradeClassifications().contains(TradeClassifications.Military)) {
            techLevel = mainPlanet.getTechnologicalLevel();
        }
        int mainWorldAtmosphere = mainPlanet.getAtmosphere();
        if (techLevel < 7 && !(mainWorldAtmosphere == 5 || mainWorldAtmosphere == 6 || mainWorldAtmosphere == 8)) {
            techLevel = 7;
        }

        s.setTech_level((byte)techLevel);
        return s;
    }

    /**
     * @param rng
     * @param profile
     * @param mainWorld
     * @param zone
     * @return
     */
    private static Set<TradeClassifications> calculateTradeFacilities(Random rng, Planet profile,
                                                                      Planet mainWorld, Star_System.Zone zone) {
        // Agricultural

        int atmosphere = profile.getAtmosphere();
        int hydro = profile.getHydro();
        int population = profile.getPopulation();
        int planGov = profile.getGovernment();
        Set<TradeClassifications> mainWorldClassifications = mainWorld.getTradeClassifications();

        Set<TradeClassifications> tradeClassifications = new HashSet<>();

        // Farming
        if ((atmosphere > 3 && atmosphere < 10) && (hydro > 3 && hydro < 9)
                && population > 2 && zone == Star_System.Zone.HABITABLE) {
            tradeClassifications.add(TradeClassifications.Farming);
        }

        // Mining
        if (mainWorldClassifications.contains(TradeClassifications.Industrial) &&
                population > 2) {
            tradeClassifications.add(TradeClassifications.Mining);
        }

        // Colony
        if (planGov == 6 && population >= 5) {
            tradeClassifications.add(TradeClassifications.Colony);
        }

        // Financial
        if ((atmosphere == 6 || atmosphere == 8) && (population > 5 && population < 9)
                && (planGov > 3 && planGov < 10)) {
            tradeClassifications.add(TradeClassifications.Rich);
        } else if ((atmosphere > 1 && atmosphere < 6) && hydro < 4) {
            tradeClassifications.add(TradeClassifications.Poor);
        }

        int mod = mainWorld.getTechnologicalLevel() >= 10 ? 2 : 0;

        if (rollDiceWithModifier(rng, 2, mod) >= 10) {
            if (mainWorld.getTechnologicalLevel() >= 8 && population > 0) {
                tradeClassifications.add(TradeClassifications.Research);
            }
        }

        mod = 0;
        if (mainWorld.getPopulation() >= 8) {
            mod = 1;
        }

        if (mainWorld.getAtmosphere() == atmosphere) {
            mod += 2;
        }

        if (mainWorld.getNavalBase() || mainWorld.getScoutBase()) {
            mod += 1;
        }

        if (rollDiceWithModifier(rng, 2, mod) > 12) {
            if (population > 0 && !mainWorldClassifications.contains(TradeClassifications.Poor)) {
                tradeClassifications.add(TradeClassifications.Military);
            }
        }


        return tradeClassifications;
    }

    /**
     * @param rng
     * @param profile
     * @return
     */
    private static Set<TradeClassifications> calculateTradeClassifications(Random rng,
                                                                           isUniversalPlanetaryProfile profile) {
        int atmosphere = profile.getAtmosphere();
        int hydro = profile.getHydro();
        int population = profile.getPopulation();
        int planGov = profile.getGovernment();
        Set<TradeClassifications> tradeClassifications = new HashSet<>();

        if (atmosphere > 3 && atmosphere < 10 && hydro > 3 && hydro < 9 && population > 4 && population < 8) {
            tradeClassifications.add(TradeClassifications.Agricultural);
        } else if (atmosphere < 4 && hydro < 4 && population > 5) {
            tradeClassifications.add(TradeClassifications.NonAgricultural);
        }

        if (population > 8 && (atmosphere == 0 ||
                atmosphere == 1 ||
                atmosphere == 2 ||
                atmosphere == 4 ||
                atmosphere == 7 ||
                atmosphere == 9)) {
            tradeClassifications.add(TradeClassifications.Industrial);
        } else if (population < 7) {
            tradeClassifications.add(TradeClassifications.NonIndustrial);
        }
        // Financial
        if ((atmosphere == 6 || atmosphere == 8) && (population > 5 && population < 9)
                && (planGov > 3 && planGov < 10)) {
            tradeClassifications.add(TradeClassifications.Rich);
        } else if ((atmosphere > 1 && atmosphere < 6) && hydro < 4) {
            tradeClassifications.add(TradeClassifications.Poor);
        }

        return tradeClassifications;
    }

    private static Integer lookUpOrbit(Random rng, boolean isRing) {

        if (satelliteOrbits.isEmpty()) {
            loadProperties();
        }

        int orbitRoll = rollDice(rng, 2);
        List<Integer> orbits;

        if (orbitRoll <= 7) {
            orbits = satelliteOrbits.get("close");
        } else if (orbitRoll <= 11) {
            orbits = satelliteOrbits.get("far");
        } else {
            orbits = satelliteOrbits.get("extreme");
        }

        orbitRoll = rollDice(rng, 2);

        if (isRing) {
            orbits = satelliteOrbits.get("ring");
            orbitRoll = rollDice(rng, 1);
        }

        return orbits.get(orbitRoll - 1);

    }

    @SuppressWarnings("unchecked")
    private static void loadProperties() {
        InputStream input;

        if (satelliteOrbits.isEmpty()) {

            input = Star.class.getResourceAsStream("orbital_distance.yml");

            Yaml yaml = new Yaml();

            Map<String, Object> propertyMap = (Map<String, Object>) yaml.load(input);
            satelliteOrbits = (Map<String, List<Integer>>) propertyMap.get("satellite_orbits");
        }

    }
}
