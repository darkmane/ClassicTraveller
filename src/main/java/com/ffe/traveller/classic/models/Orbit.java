package com.ffe.traveller.classic.models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Null;

/**
 * Created by darkmane on 11/30/15.
 */
@Entity
@Table(name="orbits")
public class Orbit extends Model {
    @Id
    Long id;

    @ManyToOne
            @Getter @Setter
    Star_System star_system;

    @Column
    @Getter
    @Setter
    Integer stellar_orbit;

    @Column
    @Getter @Setter
            @Null
    Integer planetary_orbit;

    @Column
    @Getter @Setter
    BodyType bodytype;

    @Column
    long body;

    public void setBody(IBody b){
        if(b.getClass() == Star.class){
            this.bodytype = BodyType.star;
            body = ((Star) b).getId();
        }

        if(b.getClass() == Planet.class){
            this.bodytype = BodyType.planet;
            body = ((Planet) b).getId();
        }

        if(b.getClass() == GasGiant.class){
            this.bodytype = BodyType.gas_giant;
            body = ((GasGiant) b).getId();
        }
    }

    public IBody getBody(){
        Class c = Planet.class;
        switch (bodytype){
            case star:
                c = Star.class;
                break;
            case gas_giant:
                c = GasGiant.class;
                break;
            default:
        }
        final EbeanServer ebeanServer = Ebean.getServer(null);
        return (IBody)ebeanServer.find(c, this.body);
    }
}
