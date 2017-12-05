package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ffe.traveller.classic.models.Star_System.Zone;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.InputStream;
import java.util.*;

/**
 * Created by sechitwood on 2/9/15.
 */

public class  Star extends Model implements IBody {

    private static Map<String, Map<String, Double>> luminosityMap = new HashMap<>();
    private static final double K_temp = 374.025;
    private static Map<String, Object> orbitalDistanceMap = new HashMap<>();
    private static Map<String, Object> orbitalInfo = new HashMap<>();


    private Set<Integer> unavailableOrbits;
    private Set<Integer> innerOrbits;
    private Set<Integer> habitableOrbits;
    private Set<Integer> outerOrbits;

    @Override
    public Type getType() {
        return Type.STAR;
    }

    public enum StellarClass {
        B, A, M, K, G, F
    }

    public enum StellarSize {
        Ia, Ib, II, III, IV, V, VI, D
    }

    public enum StarPosition {
        PRIMARY, SECONDARY, TERTIARY
    }

    public static final Integer NEAR_ORBIT = -1;
    public static final Integer FAR_ORBIT = Integer.MAX_VALUE;
    public static final Integer CENTER = Integer.MIN_VALUE;


    @Id
    @Getter
    @Setter
    long id;

    @Column
    @Getter
    @JsonProperty("class")
    private StellarClass stellarClass;

    @Column
    @Getter
    @JsonProperty("size")
    private StellarSize starSize;

    @Column
    @Getter
    @JsonIgnore
    Integer starOrbit;


    /**
     * @param c        Class
     * @param s        Size
     * @param location Orbit
     */
    public Star(StellarClass c, StellarSize s, Integer location) {
        stellarClass = c;
        starSize = s;
        starOrbit = location;
        loadProperties();

    }

    @JsonIgnore
    public double getLuminosity() {
        double lum = 0.0;
        loadProperties();
        System.out.println("Class: " + stellarClass + ", Size: " + starSize);
        lum = luminosityMap.get(this.stellarClass.toString() + "0").get(this.starSize.toString());
        return lum;
    }

    @SuppressWarnings("unchecked")
    private static void loadProperties() {

        InputStream input;

        if (luminosityMap.isEmpty()) {
            input = Star.class.getResourceAsStream("stellar_luminosity.yml");
            Yaml yaml = new Yaml();
            luminosityMap = (Map<String, Map<String, Double>>) yaml.load(input);
        }

        if (orbitalDistanceMap.isEmpty()) {
            input = Star.class.getResourceAsStream("orbital_distance.yml");
            Yaml yaml = new Yaml();
            orbitalDistanceMap = (Map<String, Object>) yaml.load(input);
        }

        if (orbitalInfo.isEmpty()) {
            input = Star.class.getResourceAsStream("orbit_tables.yml");
            Yaml yaml = new Yaml();
            orbitalInfo = (Map<String, Object>) yaml.load(input);
        }

    }

    @JsonProperty("orbit")
    protected String OrbitJson() {
        String rv = starOrbit.toString();
        if (starOrbit == CENTER) {
            rv = "Central";
        }

        if (starOrbit == NEAR_ORBIT) {
            rv = "Near Orbit";
        }

        if (starOrbit == FAR_ORBIT) {
            rv = "Far Orbit";
        }

        return rv;
    }


    private void determineOrbits() {

    }

    public Set<Integer> getUnavailableOrbits() {
        if (unavailableOrbits == null) {
            determineOrbits();
        }
        Set<Integer> s = new HashSet<>();
        for (int counter = 0; counter < 20; counter++) {
            s.add(new Integer(counter));
        }
        s.removeAll(getInnerOrbits());
        s.removeAll(getHabitableOrbits());
        s.removeAll(getOuterOrbits());
        return s;
    }

    private Set<Integer> getOrbits(Zone z) {
        if (unavailableOrbits == null) {
            determineOrbits();
        }
        Map<String, Object> tables =
                (Map<String, Object>) orbitalInfo.get("zones");

        Map<String, Object> sizeTable = (Map<String, Object>) tables.get(this.starSize.toString());
        Map<String, Object> classTable = (Map<String, Object>) sizeTable.get(this.stellarClass.toString() + "0");
        List<Integer> zones = (List<Integer>) classTable.get(z.toString().toLowerCase());


        return ImmutableSet.copyOf(zones);
    }

    public Set<Integer> getInnerOrbits() {
        return getOrbits(Zone.INNER);
    }

    public Set<Integer> getHabitableOrbits() {
        return getOrbits(Zone.HABITABLE);
    }

    public Set<Integer> getOuterOrbits() {
        return getOrbits(Zone.OUTER);
    }

    public Zone getZone(Integer i){
        Zone z = Zone.UNAVAILABLE;
        if(getInnerOrbits().contains(i)){
            z = Zone.INNER;
        }

        if(getHabitableOrbits().contains(i)){
            z = Zone.HABITABLE;
        }

        if(getOuterOrbits().contains(i)){
            z = Zone.OUTER;
        }

        return z;
    }


    public Set<Integer> calculateHabitableZone(Planet p) {
        Set<Integer> rv = new HashSet<>();

        double minG = p.getMinimumGreenhouse();
        double maxG = p.getMaximumGreenhouse();

        double minA = p.getMinimumAlbedo();
        double maxA = p.getMaximumAlbedo();

        double luminosity = this.getLuminosity();
        double min_habitable_distance = Math.sqrt(luminosity) *
                Math.pow((K_temp * minG * (1.0 - minA) / (50.0 + 272.15)), 2.0);
        double max_habitable_distance = Math.sqrt(luminosity) *
                Math.pow((K_temp * maxG * (1.0 - maxA) / (-20.0 + 272.15)), 2.0);

        List<Double> orbital_dist = (ArrayList<Double>) orbitalDistanceMap.get("orbital_distances");
        int counter = 0;
        while (orbital_dist.get(counter) < min_habitable_distance) {
            counter++;
        }
        while (orbital_dist.get(counter) < max_habitable_distance) {
            rv.add(new Integer(counter++));
        }

        return ImmutableSet.copyOf(rv);
    }
}
