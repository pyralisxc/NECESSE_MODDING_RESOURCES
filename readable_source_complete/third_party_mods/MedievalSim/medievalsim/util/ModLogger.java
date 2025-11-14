/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.util;

import java.util.logging.Logger;

public final class ModLogger {
    private static final String MOD_ID = "medieval.sim";
    private static Logger logger;

    private static void initializeLogger() {
        try {
            logger = Logger.getLogger(MOD_ID);
        }
        catch (Exception e) {
            logger = Logger.getLogger("Medieval Sim");
        }
    }

    public static void info(String message) {
        logger.info("MedievalSim: INFO - " + message);
    }

    public static void info(String format, Object ... args) {
        ModLogger.info(String.format(format, args));
    }

    public static void warn(String message) {
        logger.warning("MedievalSim: WARNING - " + message);
    }

    public static void warn(String format, Object ... args) {
        ModLogger.warn(String.format(format, args));
    }

    public static void error(String message) {
        logger.severe("MedievalSim: ERROR - " + message);
    }

    public static void error(String format, Object ... args) {
        ModLogger.error(String.format(format, args));
    }

    public static void error(String message, Throwable throwable) {
        logger.severe("MedievalSim: ERROR - " + message + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public static void debug(String message) {
        logger.info("MedievalSim: DEBUG - " + message);
    }

    public static void debug(String format, Object ... args) {
        ModLogger.debug(String.format(format, args));
    }

    private ModLogger() {
    }

    static {
        ModLogger.initializeLogger();
    }
}

