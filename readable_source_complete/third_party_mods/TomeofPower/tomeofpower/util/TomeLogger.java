/*
 * Decompiled with CFR 0.152.
 */
package tomeofpower.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TomeLogger {
    private static Level currentLevel = Level.INFO;
    private static final String MOD_PREFIX = "[TomeOfPower]";

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    public static void error(String message) {
        TomeLogger.log(Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        TomeLogger.log(Level.ERROR, message + " - " + throwable.getMessage());
        if (currentLevel == Level.DEBUG && throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void warn(String message) {
        TomeLogger.log(Level.WARN, message);
    }

    public static void info(String message) {
        TomeLogger.log(Level.INFO, message);
    }

    public static void debug(String message) {
        TomeLogger.log(Level.DEBUG, message);
    }

    private static void log(Level level, String message) {
        if (level.shouldLog(currentLevel)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(String.format("[%s] %s [%s] %s", timestamp, MOD_PREFIX, level.name(), message));
        }
    }

    public static boolean isDebugEnabled() {
        return Level.DEBUG.shouldLog(currentLevel);
    }

    public static enum Level {
        ERROR(0),
        WARN(1),
        INFO(2),
        DEBUG(3);

        private final int priority;

        private Level(int priority) {
            this.priority = priority;
        }

        public boolean shouldLog(Level currentLevel) {
            return this.priority <= currentLevel.priority;
        }
    }
}

