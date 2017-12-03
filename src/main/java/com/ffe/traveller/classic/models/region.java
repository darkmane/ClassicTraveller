package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by darkmane on 11/29/15.
 */
@Entity
@Table(name="regions")
public class Region extends Model {
    @Id
    Long id;

    @Column
    @Getter
    @Setter
    public int coord_x;

    @Column
    @Getter
    @Setter
    public int coord_y;

    @Column
    @Getter
    @Setter
    public String name;

    @Column
    @Getter
    @Setter
    public int vertical_size;

    @Column
    @Getter
    @Setter
    public int horizontal_size;


}
