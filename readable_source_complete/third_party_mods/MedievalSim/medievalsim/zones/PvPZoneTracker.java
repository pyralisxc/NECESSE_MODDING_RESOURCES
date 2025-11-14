/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import medievalsim.packets.PacketPvPZoneSpawnDialog;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.ZoneConstants;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PvPZoneTracker {
    private static final Map<Long, PlayerPvPState> playerStates = new HashMap<Long, PlayerPvPState>();

    public static PlayerPvPState getPlayerState(ServerClient client) {
        return playerStates.computeIfAbsent(client.authentication, k -> new PlayerPvPState());
    }

    public static void cleanupPlayerState(ServerClient client) {
        if (client != null) {
            playerStates.remove(client.authentication);
        }
    }

    public static void cleanupPlayerState(long authentication) {
        playerStates.remove(authentication);
    }

    public static boolean isInPvPZone(ServerClient client) {
        PlayerPvPState state = playerStates.get(client.authentication);
        return state != null && state.currentZoneID != -1;
    }

    public static PvPZone getCurrentZone(ServerClient client, Level level) {
        PlayerPvPState state = playerStates.get(client.authentication);
        if (state == null || state.currentZoneID == -1 || level == null) {
            return null;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        return zoneData != null ? zoneData.getPvPZone(state.currentZoneID) : null;
    }

    public static void enterZone(ServerClient client, PvPZone zone) {
        PlayerPvPState state = PvPZoneTracker.getPlayerState(client);
        state.currentZoneID = zone.uniqueID;
    }

    public static void exitZone(ServerClient client, long serverTime) {
        PlayerPvPState state = PvPZoneTracker.getPlayerState(client);
        state.currentZoneID = -1;
        state.lastCombatTime = 0L;
        state.lastExitTime = serverTime;
        state.hasShownSpawnDialog = false;
    }

    public static boolean canReEnter(ServerClient client, long serverTime) {
        PlayerPvPState state = playerStates.get(client.authentication);
        if (state == null || state.lastExitTime == 0L) {
            return true;
        }
        return serverTime - state.lastExitTime >= ZoneConstants.getPvpReentryCooldownMs();
    }

    public static int getRemainingReEntryCooldown(ServerClient client, long serverTime) {
        PlayerPvPState state = playerStates.get(client.authentication);
        if (state == null || state.lastExitTime == 0L) {
            return 0;
        }
        long elapsed = serverTime - state.lastExitTime;
        long remaining = ZoneConstants.getPvpReentryCooldownMs() - elapsed;
        return remaining > 0L ? (int)Math.ceil((double)remaining / 1000.0) : 0;
    }

    public static void recordCombat(ServerClient client, long serverTime) {
        PlayerPvPState state = PvPZoneTracker.getPlayerState(client);
        state.lastCombatTime = serverTime;
    }

    public static boolean isInCombat(ServerClient client, Level level, long serverTime) {
        PlayerPvPState state = playerStates.get(client.authentication);
        if (state == null || state.currentZoneID == -1 || level == null || state.lastCombatTime == 0L) {
            return false;
        }
        PvPZone zone = PvPZoneTracker.getCurrentZone(client, level);
        if (zone == null) {
            return false;
        }
        long combatLockMs = (long)zone.combatLockSeconds * 1000L;
        return serverTime - state.lastCombatTime < combatLockMs;
    }

    public static int getRemainingCombatLockSeconds(ServerClient client, Level level, long serverTime) {
        PlayerPvPState state = playerStates.get(client.authentication);
        if (state == null || state.currentZoneID == -1 || level == null) {
            return 0;
        }
        PvPZone zone = PvPZoneTracker.getCurrentZone(client, level);
        if (zone == null) {
            return 0;
        }
        long combatLockMs = (long)zone.combatLockSeconds * 1000L;
        long elapsed = serverTime - state.lastCombatTime;
        long remaining = combatLockMs - elapsed;
        return remaining > 0L ? (int)Math.ceil((double)remaining / 1000.0) : 0;
    }

    public static void handleSpawnInZone(ServerClient client, PvPZone zone, Server server, long serverTime) {
        PlayerPvPState state = PvPZoneTracker.getPlayerState(client);
        if (state.hasShownSpawnDialog) {
            return;
        }
        state.hasShownSpawnDialog = true;
        client.sendPacket((Packet)new PacketPvPZoneSpawnDialog(zone));
    }

    public static Point findClosestTileInZone(PvPZone zone, float playerX, float playerY) {
        int playerTileX = (int)(playerX / 32.0f);
        int playerTileY = (int)(playerY / 32.0f);
        if (zone.containsTile(playerTileX, playerTileY) && !zone.zoning.isEdgeTile(playerTileX, playerTileY)) {
            return new Point(playerTileX, playerTileY);
        }
        Rectangle bounds = zone.zoning.getTileBounds();
        if (bounds == null) {
            return null;
        }
        int maxSearchRadius = Math.max(bounds.width, bounds.height);
        for (int searchRadius = 1; searchRadius <= maxSearchRadius; ++searchRadius) {
            for (int dx = -searchRadius; dx <= searchRadius; ++dx) {
                for (int dy = -searchRadius; dy <= searchRadius; ++dy) {
                    int checkY;
                    int checkX;
                    if (Math.abs(dx) != searchRadius && Math.abs(dy) != searchRadius || !zone.containsTile(checkX = playerTileX + dx, checkY = playerTileY + dy) || zone.zoning.isEdgeTile(checkX, checkY)) continue;
                    return new Point(checkX, checkY);
                }
            }
        }
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;
        return new Point(centerX, centerY);
    }

    public static Point findClosestTileOutsideZone(PvPZone zone, float playerX, float playerY) {
        int playerTileX = (int)(playerX / 32.0f);
        int playerTileY = (int)(playerY / 32.0f);
        if (!zone.containsTile(playerTileX, playerTileY)) {
            return new Point(playerTileX, playerTileY);
        }
        Rectangle bounds = zone.zoning.getTileBounds();
        if (bounds == null) {
            return null;
        }
        int[] dx = new int[]{-1, 1, 0, 0};
        int[] dy = new int[]{0, 0, -1, 1};
        Point closest = null;
        double closestDist = Double.MAX_VALUE;
        for (int i = 0; i < 4; ++i) {
            int checkX = playerTileX;
            int checkY = playerTileY;
            while (zone.containsTile(checkX, checkY)) {
                checkX += dx[i];
                checkY += dy[i];
            }
            double dist = Math.sqrt(Math.pow(checkX - playerTileX, 2.0) + Math.pow(checkY - playerTileY, 2.0));
            if (!(dist < closestDist)) continue;
            closestDist = dist;
            closest = new Point(checkX, checkY);
        }
        return closest != null ? closest : new Point(playerTileX, playerTileY);
    }

    public static class PlayerPvPState {
        public int currentZoneID = -1;
        public long lastCombatTime = 0L;
        public long lastExitTime = 0L;
        public boolean hasShownSpawnDialog = false;
    }
}

