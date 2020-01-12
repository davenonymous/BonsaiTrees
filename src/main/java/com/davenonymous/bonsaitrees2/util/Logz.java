package com.davenonymous.bonsaitrees2.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

public class Logz {
    private static final Logger LOGGER = LogManager.getLogger("BonsaiTrees2");

    public static void log(Level logLevel, String fmt, Object... data) {
        LOGGER.log(logLevel, fmt, data);
    }

    public static void all(String fmt, Object... data) {
        log(Level.ALL, fmt, data);
    }

    public static void debug(String fmt, Object... data) {
        log(Level.DEBUG, fmt, data);
    }

    public static void error(String fmt, Object... data) {
        log(Level.ERROR, fmt, data);
    }

    public static void fatal(String fmt, Object... data) {
        log(Level.FATAL, fmt, data);
    }

    public static void info(String fmt, Object... data) {
        log(Level.INFO, fmt, data);
    }

    public static void off(String fmt, Object... data) {
        log(Level.OFF, fmt, data);
    }

    public static void trace(String fmt, Object... data) {
        log(Level.TRACE, fmt, data);
    }

    public static void warn(String fmt, Object... data) {
        log(Level.WARN, fmt, data);
    }


    public static void log(Marker mark, Level logLevel, String fmt, Object... data) {
        LOGGER.log(logLevel, mark, fmt, data);
    }

    public static void all(Marker mark, String fmt, Object... data) {
        log(mark, Level.ALL, fmt, data);
    }

    public static void debug(Marker mark, String fmt, Object... data) {
        log(mark, Level.DEBUG, fmt, data);
    }

    public static void error(Marker mark, String fmt, Object... data) {
        log(mark, Level.ERROR, fmt, data);
    }

    public static void fatal(Marker mark, String fmt, Object... data) {
        log(mark, Level.FATAL, fmt, data);
    }

    public static void info(Marker mark, String fmt, Object... data) {
        log(mark, Level.INFO, fmt, data);
    }

    public static void off(Marker mark, String fmt, Object... data) {
        log(mark, Level.OFF, fmt, data);
    }

    public static void trace(Marker mark, String fmt, Object... data) {
        log(mark, Level.TRACE, fmt, data);
    }

    public static void warn(Marker mark, String fmt, Object... data) {
        log(mark, Level.WARN, fmt, data);
    }

}
