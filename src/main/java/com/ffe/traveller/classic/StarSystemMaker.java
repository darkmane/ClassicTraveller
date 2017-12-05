package com.ffe.traveller.classic;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.ffe.traveller.classic.models.*;
import com.ffe.traveller.classic.models.Star_System.Zone;
import com.ffe.traveller.util.Utility;

import javax.validation.constraints.Null;
import java.util.*;

import static com.ffe.traveller.classic.PlanetMaker.*;
import static com.ffe.traveller.classic.models.Star.StarPosition.*;
import static com.ffe.traveller.classic.models.Star.StellarClass.*;
import static com.ffe.traveller.classic.models.Star.StellarSize.*;
import static com.ffe.traveller.util.DiceGenerator.roll;
import static com.ffe.traveller.util.DiceGenerator.rollDice;


/**
 * Created by darkmane on 1/15/15.
 */
public class StarSystemMaker {

    private static final int SOLITARY = 7;
    private static final int BINARY = 11;
    private static final int TRINARY = 12;


    public static Star_System CreateStarSystem() {
        return CreateStarSystem(null, null);
    }

    public static Star_System CreateStarSystem(@Null Random random, @Null Planet planet) {
        final EbeanServer ebeanServer = Ebean.getServer(null);
        Star_System newStarSystem = null;
        try {
            Random rng = random;
            ebeanServer.beginTransaction();
            Star_System system = null;
            if (!(planet.getOrbit() == null || planet.getOrbit().getStar_system() == null)) {
                system = planet.getOrbit().getStar_system();
            }

            if (random == null) {
                rng = new Random();

                String hashSeed = system == null ? "||" : String.format("%s|%s|%2d%2d", system.getSector(), system.getSubsector(),
                        system.getCoord_x(), system.getCoord_y());
                if (!hashSeed.equals("||")) {
                    rng = new Random(Utility.getSHA256(hashSeed));
                }
            }

            // 11. Place known components.
            //   A. Place gas giants.
            //   B. Place planetoid belts.
            //   C. Place main world in habitable zone.

            newStarSystem = system == null ? new Star_System(): system;
            int orbitRoll = roll(rng);

            planet.setMain(true);

            // 10. Determine star system details.
            //   A. System nature (solitary, binary, or trinary star system).
            //   B. Primary star type and size. DM+4 if main world has population 8+ or atmosphere 4 - 9.
            //   C. Companion star type and size.
            //   D. Companion orbit.
            //   E. Number of orbits available for each star.
            //   F. Unavailable, inner, habitable, and outer zones within the system.

            Map<Star.StarPosition, Star> starMap =  generateStars(rng, planet.getPopulation(), planet.getAtmosphere());
            Star centralStar = starMap.get(PRIMARY);

            switch (centralStar.getStarSize()) {
                case III:
                    orbitRoll += 4;
                case Ia:
                case Ib:
                case II:
                    orbitRoll += 8;
            }
            switch (centralStar.getStellarClass()) {
                case M:
                    orbitRoll -= 4;
                case K:
                    orbitRoll -= 2;
            }

            int orbits = orbitRoll > 0 ? orbitRoll : 1;

            Set<Integer> empty = new HashSet<>();

            int emptyRoll = rollDice(rng, 1);
            int numberEmptyRoll = rollDice(rng, 1);
            if (centralStar.getStellarClass() == B ||
                    centralStar.getStellarClass() == A) {
                emptyRoll += 1;
                numberEmptyRoll += 1;
            }

            int numberEmpty = 0;

            if (emptyRoll >= 4) {
                switch (numberEmptyRoll) {
                    case 1:
                    case 2:
                        numberEmpty = 1;
                    case 3:
                        numberEmpty = 2;
                    default:
                        numberEmpty = 3;
                }
            }

            while (empty.size() < numberEmpty) {
                empty.add(roll(rng));
            }


            Set<Integer> availableOrbits = new HashSet(centralStar.getHabitableOrbits());
            availableOrbits.addAll(centralStar.getOuterOrbits());
            availableOrbits.removeAll(empty);

            //   G. Captured planets and empty orbits.
            //   H. Presence and quantity of gas giants.
            int gasGiants = roll(rng);
            int numberOfGasGiants = 0;

            if (gasGiants < 10) {
                int numberGGRoll = roll(rng);

                switch (numberGGRoll) {
                    case 1:
                    case 2:
                    case 3:
                        numberOfGasGiants = 1;
                        break;
                    case 4:
                    case 5:
                        numberOfGasGiants = 2;
                        break;
                    case 6:
                    case 7:
                        numberOfGasGiants = 3;
                        break;
                    case 8:
                    case 9:
                    case 10:
                        numberOfGasGiants = 4;
                        break;
                    case 11:
                    case 12:
                        numberOfGasGiants = 5;
                        break;
                }

                // Place Gas Giants
                numberOfGasGiants = numberOfGasGiants > newStarSystem.getMaxOrbits() ? newStarSystem.getMaxOrbits() : numberOfGasGiants;


                for (int counter = 0; counter < numberOfGasGiants; counter++) {
                    List<Integer> orbitSet = new ArrayList<>();
                    if (availableOrbits.isEmpty()) {
                        availableOrbits.addAll(centralStar.getInnerOrbits());
                    }
                    for (Integer orbit : availableOrbits) {
                        for (int counter2 = 0; counter2 < orbit; counter2++) {
                            orbitSet.add(orbit);
                        }
                    }

                    int listIndex = rng.nextInt(orbitSet.size());
                    int orbitNum = orbitSet.get(listIndex);
                    Zone z = centralStar.getZone(orbitNum);

                    GasGiant gg = CreateGasGiant(rng, z, planet);
                    Orbit o = new Orbit();
                    o.setBody(gg);
                    o.setStellar_orbit(orbitNum);
                    o.setStar_system(newStarSystem);
                    o.save();

                }
            }

            //   I. Presence and quantity of planetoid belts.
            int planetoidBelt = roll(rng);
            planetoidBelt -= numberOfGasGiants;
            planetoidBelt = planetoidBelt < 0 ? 0 : planetoidBelt;
            if (planetoidBelt < 7) {

                int numberPBRoll = roll(rng);
                int numberOfPlanetoidBelts = 0;
                numberPBRoll = (numberPBRoll - numberOfGasGiants < 0 ? 0 : numberPBRoll - numberOfGasGiants);

                switch (numberPBRoll) {
                    case 0:
                        numberOfPlanetoidBelts = 3;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        numberOfPlanetoidBelts = 2;
                        break;

                    default:
                        numberOfPlanetoidBelts = 1;
                        break;
                }

                numberOfPlanetoidBelts = numberOfPlanetoidBelts > (newStarSystem.getMaxOrbits() - numberOfGasGiants) ?
                        (newStarSystem.getMaxOrbits() - numberOfGasGiants) : numberOfPlanetoidBelts;

                // Place planetoid belts
                for (int counter = 0; counter < numberOfPlanetoidBelts; counter++) {
                    List<Integer> orbitList = new ArrayList<>();
                    if (availableOrbits.isEmpty()) {
                        availableOrbits.addAll(centralStar.getInnerOrbits());
                    }
                    for (Integer orbit : availableOrbits) {
                        int probability = orbit;

                        if (newStarSystem.getOrbitsMap().containsKey(orbit + 1) &&
                                (newStarSystem.getOrbits().get(orbit + 1).getBody().getType() == Planet.Type.LARGE_GAS_GIANT ||
                                        newStarSystem.getOrbits().get(orbit + 1).getBody().getType() == Planet.Type.SMALL_GAS_GIANT)) {
                            probability *= 2;

                        }
                        for (int counter2 = 0; counter2 < probability; counter2++) {
                            orbitList.add(orbit);
                        }
                    }

                    int listIndex = rng.nextInt(orbitList.size());
                    int orbitNum = orbitList.get(listIndex);
                    Planet pb = CreatePlanet(rng, null,
                            null, null, (byte) 0, null,
                            null, null, null, null,
                            null, null, null);

                    Orbit o = new Orbit();
                    o.setStar_system(newStarSystem);
                    o.setStellar_orbit(orbitNum);
                    o.setBody(pb);
                    o.save();
                    availableOrbits.remove(orbitNum);
                }

            }


            // Place Mainworld
            Set<Integer> hz = new HashSet();
            hz.addAll(newStarSystem.getStars().get(Star.StarPosition.PRIMARY).calculateHabitableZone(planet));

            Set<Integer> all = new HashSet();
            all.addAll(centralStar.getInnerOrbits());
            all.addAll(centralStar.getHabitableOrbits());
            all.addAll(centralStar.getOuterOrbits());

            List<Integer> unoccupied = new ArrayList();
            hz.removeAll(newStarSystem.getOrbitsMap().keySet());

            if (hz.isEmpty()) {
                hz.addAll(all);
            }

            hz.removeAll(newStarSystem.getOrbitsMap().keySet());

            Boolean mainWorldIsSatellite = hz.isEmpty();
            if (mainWorldIsSatellite) {
                List<Orbit> listGasGiantOrbits = newStarSystem.getGasGiantOrbits();
                for (Orbit o : listGasGiantOrbits) {
                    GasGiant gg = (GasGiant)o.getBody();
                    if (gg.getType() == Planet.Type.LARGE_GAS_GIANT) {
                        gg.createOrbits(roll(rng));
                    }
                    if (gg.getType() == Planet.Type.SMALL_GAS_GIANT) {
                        gg.createOrbits(roll(rng));
                    }
                }

                Orbit centralPlanetOrbit = listGasGiantOrbits.get(rng.nextInt(listGasGiantOrbits.size()));
                // TODO: Found the stellar orbit, now choose a planetary orbit and create the correct Orbit
                int numberOfSatelliteOrbits = 0;


            } else {
                List<Integer> habitableList = new ArrayList<>(hz);
                Integer mainWorldOrbit = habitableList.get(rng.nextInt(hz.size()));
                Orbit o = new Orbit();
                o.setStellar_orbit(mainWorldOrbit);
                o.setBodytype(BodyType.planet);
                o.setBody(planet);
                o.save();
                planet.setMain(true);
                planet.save();

            }

            Set<Integer> remainingOrbits = new HashSet<>();

            remainingOrbits.addAll(centralStar.getInnerOrbits());
            remainingOrbits.addAll(centralStar.getHabitableOrbits());
            remainingOrbits.addAll(centralStar.getOuterOrbits());
            remainingOrbits.removeAll(newStarSystem.getOrbitsMap().keySet());


            for (Integer orbit : remainingOrbits) {
                Star_System.Zone z = Star_System.Zone.INNER;
                if (centralStar.getHabitableOrbits().contains(orbit)) {
                    z = Zone.HABITABLE;
                }
                if (centralStar.getOuterOrbits().contains(orbit)) {
                    z = Zone.OUTER;
                }
                Orbit o = new Orbit();
                o.setStellar_orbit(orbit);
                o.setStar_system(newStarSystem);
                o.setBodytype(BodyType.planet);
                o.setBody(CreateMinorPlanet(rng, centralStar, orbit, z, newStarSystem.getMainWorld()));
                o.save();

            }

            newStarSystem.save();
            ebeanServer.commitTransaction();
        } catch (Exception ex) {
            ebeanServer.rollbackTransaction();

        } finally {
            ebeanServer.endTransaction();
            return newStarSystem;
        }

    }

    private static Star generateCompanionStar(@Null Random rng, int classMod, int sizeRoll, Star.StarPosition position) {
        int rollClass = roll(rng);
        int rollSize = roll(rng);
        int rollOrbit = roll(rng);
        int orbitRoll = roll(rng);

        int orbit = Star.CENTER;
        Star.StellarClass sClass = M;
        Star.StellarSize sSize = D;
        int orbits = 0;

        rollClass += classMod;
        rollSize += sizeRoll;
        rollOrbit += position == TERTIARY ? 4 : 0;

        switch (rollClass) {

            case 1:
                sClass = B;
                break;
            case 2:
                sClass = A;
                break;
            case 3:
            case 4:
                sClass = F;
                break;
            case 5:
            case 6:
                sClass = G;
            case 7:
            case 8:
                sClass = K;
                break;
        }


        switch (rollSize) {
            case 0:
                sSize = Ia;
                break;
            case 1:
                sSize = Ib;
                break;
            case 2:
                sSize = II;
                break;
            case 3:
                sSize = III;
                break;
            case 4:
                sSize = IV;
                break;
            case 5:
            case 6:
                sSize = D;
                break;
            case 7:
            case 8:
                sSize = V;
                break;
            case 9:
                sSize = VI;
                break;

        }

        if (rollOrbit < 4) {
            orbit = Star.NEAR_ORBIT;
        } else if (rollOrbit == 12) {
            orbit = Star.FAR_ORBIT;
        } else {
            orbit = rollOrbit - 3;
            if (rollOrbit > 6) {
                orbit += roll(rng);
            }
        }

        switch (sSize) {
            case III:
                orbitRoll += 4;
            case Ia:
            case Ib:
            case II:
                orbitRoll += 8;
        }
        switch (sClass) {
            case M:
                orbitRoll -= 4;
            case K:
                orbitRoll -= 2;
        }

        orbits = orbitRoll > 0 ? orbitRoll : 0;


        return new Star(sClass, sSize, rollOrbit);
    }

    private static Map<Star.StarPosition, Star> generateStars(@Null Random rng, @Null Byte population, @Null Byte atmosphere) {
        int rollClass = roll(rng);
        int rollSize = roll(rng);
        int star = roll(rng);
        int orbitRoll = roll(rng);
        HashMap<Star.StarPosition, Star> map = new HashMap<>();


        if (population > 7 || (atmosphere > 3 && atmosphere < 10)) {
            rollClass += 4;
            rollSize += 4;
        }
        Star.StellarClass sClass = M;
        Star.StellarSize sSize = V;
        int orbits = 0;

        switch (rollClass) {
            case 0:
            case 1:
                sClass = B;
                break;
            case 2:
                sClass = A;
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                sClass = M;
                break;
            case 8:
                sClass = K;
                break;
            case 9:
                sClass = G;
                break;
            case 10:
            case 11:
            case 12:
                sClass = F;
                break;


        }

        switch (rollSize) {
            case 0:
                sSize = Ia;
                break;
            case 1:
                sSize = Ib;
                break;
            case 2:
                sSize = II;

                break;
            case 3:
                sSize = III;
                break;
            case 4:
                sSize = IV;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                sSize = V;
                break;
            case 11:
                sSize = VI;
                break;
            case 12:
                sSize = D;
                break;

        }

        if (sClass == M && sSize == IV) {
            sSize = V;
        }

        if ((sClass == B || sClass == A || sClass == F) && sSize == VI) {
            sSize = V;
        }

        map.put(PRIMARY, new Star(sClass, sSize, Star.CENTER));

        if (star > BINARY) {
            map.put(SECONDARY, generateCompanionStar(rng, rollClass, rollSize, SECONDARY));
        }

        if (star > TRINARY) {
            map.put(TERTIARY, generateCompanionStar(rng, rollClass, rollSize, TERTIARY));
        }
        return map;
    }

}
