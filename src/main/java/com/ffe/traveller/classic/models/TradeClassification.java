package com.ffe.traveller.classic.models;

import com.avaje.ebean.Model;
import lombok.Getter;
import lombok.Setter;

public class TradeClassification extends Model {
    @Getter
    @Setter
    long body;

    @Getter
    @Setter
    TradeClassifications classification;
}
