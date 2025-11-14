/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.util;

import medievalsim.util.ModLogger;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static boolean isValidServerLevel(Level level) {
        return level != null && level.isServer();
    }

    public static boolean isValidClient(ServerClient client) {
        return client != null && client.authentication != -1L;
    }

    public static boolean validateServerLevel(Level level, String operation) {
        if (!ValidationUtil.isValidServerLevel(level)) {
            ModLogger.warn("Attempted %s with invalid level (null or not server)", operation);
            return false;
        }
        return true;
    }

    public static boolean validateClient(ServerClient client, String operation) {
        if (!ValidationUtil.isValidClient(client)) {
            ModLogger.warn("Attempted %s with invalid client (null or not authenticated)", operation);
            return false;
        }
        return true;
    }

    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidCoordinate(int coordinate) {
        return coordinate >= -100000 && coordinate <= 100000;
    }

    public static boolean isValidRectangle(int x, int y, int width, int height) {
        return ValidationUtil.isValidCoordinate(x) && ValidationUtil.isValidCoordinate(y) && width > 0 && height > 0 && width <= 10000 && height <= 10000;
    }
}

