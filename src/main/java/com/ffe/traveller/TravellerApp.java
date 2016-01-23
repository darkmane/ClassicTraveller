package com.ffe.traveller;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import org.apache.log4j.Logger;
import org.avaje.agentloader.AgentLoader;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by darkmane on 1/13/15.
 */
public class TravellerApp extends ResourceConfig {

    private static Properties properties = null;
    final static Logger logger = Logger.getLogger(TravellerApp.class);

    static {
        loadProperties();

    }

    public TravellerApp() {
        loadProperties();
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent","debug=1;packages=com.ffe.traveller.classic.models.**")) {
            logger.error("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }else{
            logger.info("avaje-ebeanorm-agent found in classpath - dynamically loaded");
        }
        ServerConfig config = new ServerConfig();

        config.loadFromProperties(TravellerApp.properties);

        config.setDefaultServer(true);
        config.setName("default");

        EbeanServer server = EbeanServerFactory.create(config);
        System.out.println("EBean Server registered");

        packages(true, "com.ffe.traveller.controllers");

//        register(WorldController.class);
//        register(ValidationFeature.class);
//        register(HttpMethodBeforeFilter.class);
//        register(HttpMethodAfterFilter.class);
//        register(UnauthorizedMapper.class);
//        register(BaseExceptionMapper.class);
    }


    private static void loadProperties() {
        if (properties == null) {
            Map<String, String> envMap = System.getenv();


            Properties prop = new Properties();
            InputStream in = TravellerApp.class.getResourceAsStream("traveller.properties");

            try {
                prop.load(in);
                in.close();
                properties = prop;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}

