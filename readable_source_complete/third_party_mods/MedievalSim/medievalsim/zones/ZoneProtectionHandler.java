/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameMath
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.util.Map;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;

public class ZoneProtectionHandler {
    public static boolean canModifyInProtectedZone(ServerClient client, Level level, int tileX, int tileY) {
        if (level == null || client == null) {
            return true;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return true;
        }
        Map<Integer, ProtectedZone> protectedZones = zoneData.getProtectedZones();
        for (ProtectedZone zone : protectedZones.values()) {
            if (!zone.containsTile(tileX, tileY)) continue;
            if (Settings.serverOwnerAuth != -1L && client.authentication == Settings.serverOwnerAuth) {
                return true;
            }
            if (zone.creatorAuth == client.authentication) {
                return true;
            }
            if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
                return true;
            }
            client.sendChatMessage(Localization.translate((String)"misc", (String)"protectedzone", (String)"zone", (String)(zone.name.isEmpty() ? "Protected Zone" : zone.name)));
            return false;
        }
        return true;
    }

    public static boolean isInPvPZone(Level level, int tileX, int tileY) {
        if (level == null) {
            return false;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return false;
        }
        Map<Integer, PvPZone> pvpZones = zoneData.getPvPZones();
        for (PvPZone zone : pvpZones.values()) {
            if (!zone.containsTile(tileX, tileY)) continue;
            return true;
        }
        return false;
    }

    public static boolean isInPvPZone(Level level, float x, float y) {
        return ZoneProtectionHandler.isInPvPZone(level, GameMath.getTileCoordinate((int)((int)x)), GameMath.getTileCoordinate((int)((int)y)));
    }

    public static PvPZone getPvPZoneAt(Level level, int tileX, int tileY) {
        if (level == null) {
            return null;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return null;
        }
        Map<Integer, PvPZone> pvpZones = zoneData.getPvPZones();
        for (PvPZone zone : pvpZones.values()) {
            if (!zone.containsTile(tileX, tileY)) continue;
            return zone;
        }
        return null;
    }
}

