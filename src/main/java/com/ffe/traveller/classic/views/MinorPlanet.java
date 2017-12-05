package com.ffe.traveller.classic.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffe.traveller.classic.models.Star_System.Zone;
import com.ffe.traveller.classic.models.TradeClassifications;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by darkmane on 2/19/15.
 */
public class MinorPlanet extends Planet {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    private Planet mainWorld;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    private Zone zone;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Set<TradeClassifications> tradeClassifications;





}
