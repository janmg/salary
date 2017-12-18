package com.janmg.salary.utils;

import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Config {
    Properties props;
    private final Log log = LogFactory.getLog(getClass());
    private final String filename = "config.properties";
    private char currency = '€';

    public Config() {
        props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException ioe) {
            log.info("Loading of " + filename + " failed. " + ioe.getMessage() + ", using defaults instead");
            // Maybe nicer to package config.properties as a resource in the JAR and load the defaults from there if loading an external file fails. 
        }

        // Defaults are only set as fallback for properties without key
        setDefault("shift.start", "6:00");
        setDefault("shift.end", "18:00");
        setDefault("date.timezone", "Europe/Helsinki");
        setDefault("date.format", "d.M.yyyy H:mm");
        setDefault("rate.default", "$3.75");
        setDefault("rate.evening", "$1.15");
        setDefault("csv.directory", "./");
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public double getRate(String key) {
        String value = get("rate."+key);
        char chr = value.charAt(0);
        if (!Character.isDigit(chr)) {
            value = value.substring(1);
        }
        return new Double(value);
    }

    public char getDenomination() {
        return currency;
    }
    public char getDenomination(String key) {
        String value = get("rate."+key);
        char chr = value.charAt(0);
        if (!Character.isDigit(chr)) {
            currency = chr;
        }
        return currency;
    }

    private void setDefault(String key, String value) {
        if (!props.containsKey(key))
            props.setProperty(key, value);
    }
}
