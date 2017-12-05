package com.ffe.traveller.classic.models;

public interface IBody {
    public enum Type {
        ROCKY_PLANET, PLANETOID_BELT, LARGE_GAS_GIANT, SMALL_GAS_GIANT, STAR
    }
    public Type getType();
}
