package com.ffe.traveller.classic.models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Model;
import com.ffe.traveller.classic.isUniversalPlanetaryProfile;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by darkmane on 11/30/15.
 */
@Entity
@Table(name = "planets")
public class Planet extends Model implements isUniversalPlanetaryProfile, IBody {

    final static String PREFIX = "PSR ";

    public static final int Ring = 0;
    public static final int Small = -1;

    private int maxOrbits = 0;

    private double percWater = 0.0;
    private double percLand = 1.0;
    private double percIce = 0.1;

    private double cloudiness = calculateCloudiness();

    private double unobstructedLand = percLand * (1 - cloudiness);
    private double unobstructedWater = percWater * (1 - cloudiness);
    private double unobstructedIce = percIce * (1 - cloudiness);


    @Id
    @Getter
    @Setter
    Long id;

    @ManyToOne
    @Getter
    @Setter
    Star_System star_system;

    @Column
    @Getter
    @Setter
    String name;

    @OneToOne
    @Getter
    @Setter
    Orbit orbit;

    @Column
    @Setter
    byte diameter;

    @Column
    byte atmosphere;

    @Column
    byte hydro;

    @Column
    @Setter
    byte population;

    @Column
    @Setter
    byte government;

    @Column
    @Setter
    byte law_level;

    @Column
    @Setter
    byte tech_level;

    @Column
    @Setter
    Starport starport;

    @Column
    @Setter
    @Nullable
    Boolean scout_base;

    @Column
    @Setter
    @Nullable
    Boolean naval_base;

    @OneToMany
    @Getter
    Set<TradeClassifications> tradeClassifications;

    @Column
    @Getter
    @Setter
    boolean main;

    public Star_System getStarSystem() {
        final EbeanServer ebeanServer = Ebean.getServer(null);
        return ebeanServer.find(Star_System.class).fetch("orbits").fetch("orbits.body").where()
                .eq("orbits.body", this.id).findUnique();
    }

    public void setHydro(byte hydro){
        this.hydro = hydro;
        calculateAlbedoValues();
    }

    public void setAtmosphere(byte atmos){
        this.atmosphere = atmos;
        calculateAlbedoValues();
    }

    public void setTradeClassifications(Set<TradeClassifications> classifications){
        this.tradeClassifications.clear();
        for(TradeClassifications tc : classifications){
            this.tradeClassifications.add(tc);
        }
    }

    public Boolean getNavalBase(){
        return (naval_base == null)? false: naval_base;
    }

    public Boolean getScoutBase(){ return (scout_base == null)? false: scout_base; }

    public double getMinimumGreenhouse() {
        double minG = 1.0;
        switch (this.atmosphere) {
            case 4:
            case 5:
                minG = 1.05;
                break;
            case 6:
            case 7:
            case 14:
                minG = 1.1;
                break;
            case 8:
            case 9:
            case 13:
                minG = 1.15;
                break;
            case 10:
                minG = 1.2;
                break;
            case 11:
            case 12:
                minG = 1.2;
                break;
        }
        return minG;
    }

    public double getMaximumGreenhouse() {
        double maxG = 1.0;
        switch (this.atmosphere) {
            case 4:
            case 5:
                maxG = 1.05;
                break;
            case 6:
            case 7:
            case 14:
                maxG = 1.1;
                break;
            case 8:
            case 9:
            case 13:
                maxG = 1.15;
                break;
            case 10:
                maxG = 1.7;
                break;
            case 11:
            case 12:
                maxG = 2.2;
                break;
        }
        return maxG;
    }

    public double getMaximumAlbedo() {
        final double maxLand = 0.2;
        final double water = 0.02;
        final double maxIce = 0.85;
        final double maxCloud = 0.8;


        return (this.cloudiness * maxCloud) + (this.unobstructedLand * maxLand) +
                (this.unobstructedWater * water) + (this.unobstructedIce * maxIce);

    }

    public double getMinimumAlbedo() {

        final double minLand = 0.2;
        final double water = 0.02;
        final double minIce = 0.55;
        final double minCloud = 0.4;

        return (this.cloudiness * minCloud) + (this.unobstructedLand * minLand) +
                (this.unobstructedWater * water) + (this.unobstructedIce * minIce);

    }

    private void calculateAlbedoValues(){
        this.percWater = this.hydro / 10.0;
        this.percLand = 1 - percWater;
        this.percIce = percLand * 0.1;
        this.percWater -= percIce / 2;
        this.percLand -= percIce / 2;

        if ((this.atmosphere == 0 || this.atmosphere == 1) && this.hydro > 0) {
            this.percWater = 0.0;
            this.percIce = this.hydro / 10.0;
            this.percLand = 1 - percIce;
        }

        this.cloudiness = calculateCloudiness();

        this.unobstructedLand = percLand * (1 - cloudiness);
        this.unobstructedWater = percWater * (1 - cloudiness);
        this.unobstructedIce = percIce * (1 - cloudiness);
    }

    private double calculateCloudiness() {
        double cloudiness = 0.0;
        switch (this.hydro) {
            case 2:
                cloudiness = 0.1;
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                cloudiness = 0.1 * (this.hydro - 2);
                break;
            case 10:
                cloudiness = 0.7;
                break;
        }

        switch (this.atmosphere) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (cloudiness > 0.20) {
                    cloudiness = 0.2;
                }
                break;
            case 10:
            case 11:
            case 12:
            case 13:
                cloudiness += 0.4;
                break;
            case 14:
                cloudiness /= 2;
                break;

        }
        return cloudiness;
    }

    public void createOrbits(int number) {
        maxOrbits = 0;

    }

    @Override
    public Byte getDiameter() {
        return diameter;
    }

    @Override
    public Byte getAtmosphere() {
        return atmosphere;
    }

    @Override
    public Byte getHydro() {
        return hydro;
    }

    @Override
    public Byte getPopulation() {
        return population;
    }

    @Override
    public Byte getGovernment() {
        return government;
    }

    @Override
    public Byte getLawLevel() {
        return law_level;
    }

    @Override
    public Byte getTechnologicalLevel() {
        return tech_level;
    }

    @Override
    public Starport getStarport() {
        return starport;
    }




    public Type getType(){
        Type value = Type.ROCKY_PLANET;
        if(diameter == 0){
            value = Type.PLANETOID_BELT;
        }
        return value;
    }


}
