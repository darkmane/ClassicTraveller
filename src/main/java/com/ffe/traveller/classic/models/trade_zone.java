package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by darkmane on 11/29/15.
 */

@Entity
@Table(name="trade_zones")
public class Trade_Zone extends Model {
    @Id
    Long id;

    @Column
    String name;
}
