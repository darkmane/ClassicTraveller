package com.ffe.traveller.classic.models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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


    public Map<?, Planet> getPlanets(){
        final EbeanServer ebeanServer = Ebean.getServer(null);

        Map<?, Planet> returnValue = ebeanServer.find(Planet.class).where()
                .eq("star_system_id",this.id ).setMapKey("orbit.stellar_orbit").findMap();
        return returnValue;
    }

}
