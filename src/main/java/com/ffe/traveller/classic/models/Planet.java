package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by darkmane on 11/30/15.
 */
@Entity
@Table(name="planets")
public class Planet extends Model {
    @Id
    Long id;

    @ManyToOne
    Star_System star_system;

    @Column
    String name;

    @OneToOne
    Orbit orbit;

    @Column
    byte diameter;

    @Column
    byte atmosphere;

    @Column
    byte hydro;

    @Column
    byte population;

    @Column
    byte government;

    @Column
    byte law_level;

    @Column
    byte tech_level;

    @Column
    String star_port;

    @Column
    boolean scout_base;

    @Column
    boolean naval_base;

    @Column
    boolean main;
}
