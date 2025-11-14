/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.packet.PacketSpawnPlayer
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameMath
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import medievalsim.zones.ZoneProtectionHandler;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class ServerClientSubmitSpawnPacketPatch {

    @ModMethodPatch(target=ServerClient.class, name="submitSpawnPacket", arguments={PacketSpawnPlayer.class})
    public static class SubmitSpawnPacket {
        @Advice.OnMethodExit
        static void onExit(@Advice.This ServerClient client, @Advice.FieldValue(value="server") Server server, @Advice.FieldValue(value="hasSpawned") boolean hasSpawned) {
            if (client.playerMob == null || client.isDead()) {
                return;
            }
            int tileX = GameMath.getTileCoordinate((int)((int)client.playerMob.x));
            int tileY = GameMath.getTileCoordinate((int)((int)client.playerMob.y));
            Level level = client.playerMob.getLevel();
            if (level == null) {
                return;
            }
            PvPZone zone = ZoneProtectionHandler.getPvPZoneAt(level, tileX, tileY);
            if (zone != null) {
                long serverTime = server.world.worldEntity.getTime();
                PvPZoneTracker.handleSpawnInZone(client, zone, server, serverTime);
            }
        }
    }
}

