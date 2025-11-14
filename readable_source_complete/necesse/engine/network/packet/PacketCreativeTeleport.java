/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import java.util.Comparator;
import java.util.function.Function;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketCreativeCheck;
import necesse.engine.network.packet.PacketPlayerInventoryAction;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.WorldDeathLocation;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;

public class PacketCreativeTeleport
extends PacketCreativeCheck {
    public final Destination destination;
    public final int targetPlayerSlot;

    public PacketCreativeTeleport(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.destination = reader.getNextEnum(Destination.class);
        this.targetPlayerSlot = this.destination == Destination.player ? reader.getNextShortUnsigned() : -1;
    }

    public PacketCreativeTeleport(ClientClient target) {
        this.destination = Destination.player;
        this.targetPlayerSlot = target.slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextEnum(this.destination);
        writer.putNextShortUnsigned(this.targetPlayerSlot);
    }

    public PacketCreativeTeleport(Destination destination) {
        if (destination == Destination.player) {
            throw new IllegalArgumentException("Used player destination without target");
        }
        this.destination = destination;
        this.targetPlayerSlot = -1;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextEnum(destination);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        TeleportPos to;
        if (!PacketCreativeTeleport.checkCreativeAndSendUpdate(server, client)) {
            return;
        }
        switch (this.destination) {
            case player: {
                ServerClient target = server.getClient(this.targetPlayerSlot);
                if (target == null) {
                    return;
                }
                to = new TeleportPos(target, new StaticMessage(target.getName()), target.getLevelIdentifier(), target.playerMob.getX(), target.playerMob.getY());
                break;
            }
            case spawn: {
                client.validateSpawnPoint(true);
                Point offset = new Point(16, 16);
                if (!client.isDefaultSpawnPoint()) {
                    offset = RespawnObject.calculateSpawnOffset(server.world.getLevel(client.spawnLevelIdentifier), client.spawnTile.x, client.spawnTile.y, client);
                }
                to = new TeleportPos(null, new LocalMessage("ui", "creativeteleporttospawn"), client.spawnLevelIdentifier, client.spawnTile.x * 32 + offset.x, client.spawnTile.y * 32 + offset.y);
                break;
            }
            case worldSpawn: {
                to = new TeleportPos(null, new LocalMessage("ui", "creativeteleporttoworldspawn"), server.world.worldEntity.spawnLevelIdentifier, server.world.worldEntity.spawnTile.x * 32 + 16, server.world.worldEntity.spawnTile.y * 32 + 16);
                break;
            }
            case death: {
                WorldDeathLocation deathLocation = client.streamDeathLocations().max(Comparator.comparingInt(d -> d.deathTime)).orElse(null);
                if (deathLocation == null) {
                    client.sendChatMessage(new LocalMessage("ui", "creativeteleporttodeathfailed"));
                    return;
                }
                to = new TeleportPos(null, new LocalMessage("ui", "creativeteleporttodeath"), deathLocation.levelIdentifier, deathLocation.x, deathLocation.y);
                break;
            }
            default: {
                return;
            }
        }
        client.playerMob.setInventoryExtended(false);
        client.sendPacket(new PacketPlayerInventoryAction(client.slot, client.playerMob));
        client.playerMob.dx = 0.0f;
        client.playerMob.dy = 0.0f;
        GameLog.out.println("Teleported " + client.getName() + " to " + to.name.translate());
        if (client.isSamePlace(to.levelIdentifier)) {
            Point pos = to.levelPosGetter.apply(client.getLevel());
            client.playerMob.setPos(pos.x, pos.y, true);
            server.network.sendToClientsWithEntity(new PacketPlayerMovement(client, true), client.playerMob);
        } else {
            client.changeLevel(to.levelIdentifier, to.levelPosGetter, true);
        }
    }

    public static enum Destination {
        player,
        spawn,
        worldSpawn,
        death;

    }

    private static class TeleportPos {
        public final ServerClient client;
        public final GameMessage name;
        public final LevelIdentifier levelIdentifier;
        public final Function<Level, Point> levelPosGetter;

        private TeleportPos(ServerClient client, GameMessage name, LevelIdentifier levelIdentifier, Function<Level, Point> levelPosGetter) {
            this.client = client;
            this.name = name;
            this.levelIdentifier = levelIdentifier;
            this.levelPosGetter = levelPosGetter;
        }

        private TeleportPos(ServerClient client, GameMessage name, LevelIdentifier levelIdentifier, int levelX, int levelY) {
            this(client, name, levelIdentifier, level -> new Point(levelX, levelY));
        }
    }
}

