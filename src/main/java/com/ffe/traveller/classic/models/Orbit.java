package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by darkmane on 11/30/15.
 */
@Entity
@Table(name="orbits")
public class Orbit extends Model {
    @Id
    Long id;

    @ManyToOne
    Star_System star_system;

    @Column
    int stellar_orbit;

    @Column
    int planetary_orbit;

    @ManyToOne
    BodyType bodytype;


    @Column
    int Body;
}
