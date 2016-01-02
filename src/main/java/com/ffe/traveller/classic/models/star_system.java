package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by darkmane on 11/29/15.
 */
@Entity
@Table(name="star_systems")
public class Star_System extends Model {
    @Id
    Long id;

    @Column
    Long coord_x;

    @Column
    Long coord_y;

    @Column
    String name;


    @ManyToOne
    Region sector;


    @ManyToOne
    Region subsector;

    @ManyToOne
    Trade_Zone travel_zone;

    @Column
    boolean scout_base;

    @Column
    boolean naval_base;

}
