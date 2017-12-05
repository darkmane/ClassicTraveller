/**
 * @author markknights
 *
 * Bringing Traveller into the Applications world!
 */
package com.ffe.traveller.classic;

import com.ffe.traveller.classic.models.Starport;

public interface isUniversalPlanetaryProfile {


    public Byte getDiameter();

    public Byte getAtmosphere();

    public Byte getHydro();

    public Byte getPopulation();

    public Byte getGovernment();

    public Byte getLawLevel();

    public Byte getTechnologicalLevel();

    public Starport getStarport();

}
