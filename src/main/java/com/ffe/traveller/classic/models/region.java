package com.ffe.traveller.classic.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

/**
 * Created by darkmane on 11/29/15.
 */
@Entity
@Table(name="regions")
public class Region extends Model {
    @Id
    Long id;

    @Column
    int coord_x;

    @Column
    int coord_y;

    @Column
    String name;

    @Column
    int vertical_size;

    @Column
    int horizontal_size;


}
