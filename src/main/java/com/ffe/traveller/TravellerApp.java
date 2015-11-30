package com.ffe.traveller;

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

    static {
        loadProperties();

    }

    public TravellerApp() {


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
            String env = "development";
            if (envMap.containsKey("ENVIRONMENT"))
                env = envMap.get("ENVIRONMENT");

            Properties prop = new Properties();
            InputStream in = TravellerApp.class.getResourceAsStream("traveller." + env + ".properties");

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

