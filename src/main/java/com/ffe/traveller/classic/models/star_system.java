package com.ffe.traveller.classic.models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by darkmane on 11/29/15.
 */
@Entity
@Table(name="star_systems")
public class Star_System extends Model {
    @Id
    Long id;

    @Column
    @Getter
    @Setter
    public Long coord_x;

    @Column
    @Getter
    @Setter
    public Long coord_y;

    @Column
    @Getter
    @Setter
    public String name;


    @ManyToOne
    @Getter
    @Setter
    public Region sector;


    @ManyToOne
    @Getter
    @Setter
    public Region subsector;

    @ManyToOne
    @Getter
    @Setter
    public Travel_Zone travel_zone;

    @Column
    @Getter
    @Setter
    public boolean scout_base;

    @Column
    @Getter
    @Setter
    public boolean naval_base;

    @OneToMany
    @Getter
    List<Orbit> orbits;

    @Getter
    public final int maxOrbits = 19;




    public Map<Integer, Planet> getPlanets(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        Map<?, Planet> returnValue = ebeanServer.find(Planet.class).fetch("planet.orbit").where()
                .eq("planet.orbit.star_system_id",this.id ).setMapKey("planet.orbit.stellar_orbit").findMap();
        return (Map<Integer, Planet>)returnValue;
    }

    public enum Zone {
        UNAVAILABLE, INNER, HABITABLE, OUTER
    }

    public Map<Star.StarPosition, Star> getStars() {
        final EbeanServer ebeanServer = Ebean.getServer(null);

        List<Star> starList = ebeanServer.find(Star.class).fetch("star.orbit").orderBy("star.orbit.stellar_orbit")
                .where().eq("star.orbit.bodytype", BodyType.star).findList();

        Map<Star.StarPosition, Star> returnValue = new HashMap<Star.StarPosition, Star>();

        switch(starList.size()){
            default:
                // Only ever return the first 3 stars, anymore are a bug.
            case 3:
                returnValue.put(Star.StarPosition.TERTIARY, starList.get(2));
            case 2:
                returnValue.put(Star.StarPosition.SECONDARY, starList.get(1));
            case 1:
                returnValue.put(Star.StarPosition.PRIMARY, starList.get(0));
            case 0:

        }
        return returnValue;
    }

    public Planet getMainWorld(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        Planet mw = ebeanServer.find(Planet.class).fetch("planet.orbit")
                .fetch("planet.orbit.star_system")
                .where().eq("planet.orbit.star_system.id", this.id)
                .eq("planet.main", true).findUnique();

        return mw;
    }

    public List<Orbit> getOrbits(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        return ebeanServer.find(Orbit.class).orderBy("orbit.stellar_orbit").where()
                .eq("star_system_id", this.id).isNull("orbit.planetary_orbit").findList();

    }

    public Map<Integer, Orbit> getOrbitsMap(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        return (Map<Integer, Orbit>)ebeanServer.find(Orbit.class).where().eq("star_system_id", this.id)
                .isNull("orbit.planetary_orbit").setMapKey("orbit.stellar_orbit").findMap();

    }

    public List<Orbit> getGasGiantOrbits(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        return ebeanServer.find(Orbit.class).where().eq("star_system_id", this.id)
                .isNull("orbit.planetary_orbit").eq("bodytype", BodyType.gas_giant).findList();
    }


    public List<Orbit> getPlanetaryOrbits(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        return ebeanServer.find(Orbit.class).where().eq("star_system_id", this.id)
                .isNull("planetary_orbit").eq("bodytype", BodyType.planet).findList();
    }
}
