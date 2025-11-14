/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.awt.Rectangle;
import java.util.Map;
import java.util.Random;
import medievalsim.util.ValidationUtil;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class ZoneManager {
    private static final Random colorRandom = new Random();

    private ZoneManager() {
    }

    private static AdminZonesLevelData getValidatedZoneData(Level level, boolean createIfMissing) {
        if (!ValidationUtil.isValidServerLevel(level)) {
            return null;
        }
        return AdminZonesLevelData.getZoneData(level, createIfMissing);
    }

    public static ProtectedZone createProtectedZone(Level level, String name, ServerClient creator) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, true);
        if (zoneData == null) {
            return null;
        }
        long creatorAuth = creator != null ? creator.authentication : -1L;
        int colorHue = ZoneManager.generateRandomColorHue();
        String useName = name == null || name.trim().isEmpty() ? zoneData.getUniqueZoneName() : name;
        ProtectedZone zone = zoneData.addProtectedZone(useName, creatorAuth, colorHue);
        if (creator != null) {
            zone.setOwnerName(creator.getName());
            int teamID = creator.getTeamID();
            if (teamID != -1) {
                zone.addAllowedTeam(teamID);
            }
        }
        return zone;
    }

    public static PvPZone createPvPZone(Level level, String name, ServerClient creator) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, true);
        if (zoneData == null) {
            return null;
        }
        long creatorAuth = creator != null ? creator.authentication : -1L;
        int colorHue = ZoneManager.generateRandomColorHue();
        String useName = name == null || name.trim().isEmpty() ? zoneData.getUniqueZoneName() : name;
        return zoneData.addPvPZone(useName, creatorAuth, colorHue);
    }

    public static void deleteProtectedZone(Level level, int uniqueID) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, false);
        if (zoneData != null) {
            zoneData.removeProtectedZone(uniqueID);
        }
    }

    public static void deletePvPZone(Level level, int uniqueID) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, false);
        if (zoneData != null) {
            zoneData.removePvPZone(uniqueID);
        }
    }

    public static ProtectedZone getProtectedZone(Level level, int uniqueID) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, false);
        return zoneData != null ? zoneData.getProtectedZone(uniqueID) : null;
    }

    public static PvPZone getPvPZone(Level level, int uniqueID) {
        AdminZonesLevelData zoneData = ZoneManager.getValidatedZoneData(level, false);
        return zoneData != null ? zoneData.getPvPZone(uniqueID) : null;
    }

    public static Map<Integer, ProtectedZone> getAllProtectedZones(Level level) {
        if (level == null) {
            return Map.of();
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        return zoneData != null ? zoneData.getProtectedZones() : Map.of();
    }

    public static Map<Integer, PvPZone> getAllPvPZones(Level level) {
        if (level == null) {
            return Map.of();
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        return zoneData != null ? zoneData.getPvPZones() : Map.of();
    }

    public static boolean expandZone(Level level, AdminZone zone, Rectangle rectangle) {
        if (zone == null || rectangle == null) {
            return false;
        }
        return zone.expand(rectangle);
    }

    public static boolean shrinkZone(Level level, AdminZone zone, Rectangle rectangle) {
        if (zone == null || rectangle == null) {
            return false;
        }
        return zone.shrink(rectangle);
    }

    public static boolean canClientModifyTile(Level level, ServerClient client, int tileX, int tileY) {
        if (level == null || !level.isServer()) {
            return true;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return true;
        }
        return zoneData.canClientModifyTile(client, tileX, tileY);
    }

    public static boolean areBothInPvPZone(Level level, float x1, float y1, float x2, float y2) {
        if (level == null) {
            return false;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return false;
        }
        return zoneData.areBothInPvPZone(x1, y1, x2, y2);
    }

    private static int generateRandomColorHue() {
        return colorRandom.nextInt(360);
    }

    public static void renameZone(AdminZone zone, String newName) {
        if (zone != null && newName != null) {
            zone.name = newName;
        }
    }

    public static void changeZoneColor(AdminZone zone, int colorHue) {
        if (zone != null) {
            zone.colorHue = Math.max(0, Math.min(360, colorHue));
        }
    }

    public static void addAllowedTeam(ProtectedZone zone, int teamID) {
        if (zone != null) {
            zone.addAllowedTeam(teamID);
        }
    }

    public static void removeAllowedTeam(ProtectedZone zone, int teamID) {
        if (zone != null) {
            zone.removeAllowedTeam(teamID);
        }
    }
}

