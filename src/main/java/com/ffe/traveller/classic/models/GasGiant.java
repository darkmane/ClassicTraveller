package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

public class GasGiant extends Model implements IBody {


    @Id
    @Getter
    @Setter
    long id;


    @Column
    @Setter
    Type type;

    @Override
    public Type getType() {
        return type;
    }

    public void createOrbits(int count){
        int max = count;
    }
}
