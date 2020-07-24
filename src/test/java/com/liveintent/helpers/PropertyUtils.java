package com.liveintent.helpers;

import org.apache.commons.lang3.StringUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {
    private static final String PROPERTY_FILE_NAME = "./config.properties";
    private static final Properties PROPERTIES = getProperties();
    
    /**
     * This method provides Properties object
     *
     * @return Properties
     */
    private static synchronized Properties getProperties() {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(PROPERTY_FILE_NAME));
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading the properties file " + e);
        }
    }

    /**
     * This method return value of provided key from the properties file
     *
     * @param param Environment
     * @return String
     */
    public static String get(Environment param) {
        if (System.getProperty(param.getKey()) != null && !System.getProperty(param.getKey()).trim().equals("")) {
            return System.getProperty(param.getKey());
        } else {
            String value = PROPERTIES.getProperty(param.getKey());
            return (!(value == null || value.trim().equals(""))) ? value : StringUtils.EMPTY;
        }
    }

}

