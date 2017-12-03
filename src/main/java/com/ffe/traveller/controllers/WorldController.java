package com.ffe.traveller.controllers;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.ffe.traveller.classic.models.Region;
import com.ffe.traveller.classic.models.Star_System;
import com.ffe.traveller.classic.views.*;

import javax.servlet.http.HttpServlet;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkmane on 1/13/15.
 */

@Path("/{ruleSet}/")
public class WorldController extends HttpServlet {


    @GET
    @Path("/starsystem")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StarSystem> searchAllWorlds(@PathParam("ruleSet") String rules,
                                            @QueryParam("region") String region,
                                            @QueryParam("hex") String hex,
                                            @QueryParam("upp") String UPPs) {

        final EbeanServer ebeanServer = Ebean.getServer(null);

        Planet p = PlanetMaker.CreatePlanet(null, null, null, null, null, null, null, null, null, null, null, null, null);
        List<StarSystem> list = new ArrayList<>();
        List<Region> listRegion = new ArrayList<>();
        if (region != null) {
            listRegion.addAll(ebeanServer.find(Region.class).where().istartsWith("name", region).findList());
        }

        List<Star_System> listStarSystems = new ArrayList<>();
        for (Region r : listRegion) {
            listStarSystems.addAll(ebeanServer.find(Star_System.class).where().between("coord_x",
                    r.getCoord_x(), r.getCoord_x() + r.getHorizontal_size())
                    .between("coord_y", r.getCoord_y(), r.getCoord_y() - r.getVertical_size()).findList());
        }


        list.add(StarSystemMaker.CreateStarSystem(null, p));
        return list;

    }

    @PUT
    @Path("/starsystem")
    @Produces(MediaType.APPLICATION_JSON)
    public StarSystem writeWorld(@PathParam("ruleSet") String rules, @Valid StarSystem system) throws Exception {
        // TODO Implement write

        return StarSystemMaker.CreateStarSystem();


    }

    @PUT
    @Path("/starsystem/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public StarSystem generateWorld() {


        StarSystem newStarSystem = StarSystemMaker.CreateStarSystem();

        return newStarSystem;
    }

    @PUT
    @Path("/starsystem/generate/planet")
    @Produces(MediaType.APPLICATION_JSON)
    public StarSystem generateWorld(Planet planet) {


        StarSystem newStarSystem = StarSystemMaker.CreateStarSystem(null, planet);

        return newStarSystem;
    }

    @PUT
    @Path("/starsystem/generate/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public StarSystem generateWorld(UniversalPlanetaryProfile upp) {

        Planet planet = PlanetMaker.CreatePlanet(null, null, null, null, null,
                upp.getDiameter(), upp.getAtmosphere(), upp.getHydro(), upp.getPopulation(),
                upp.getGovernment(), upp.getLaw_level(), null, null);
        StarSystem newStarSystem = StarSystemMaker.CreateStarSystem(null, planet);

        return newStarSystem;
    }

}
