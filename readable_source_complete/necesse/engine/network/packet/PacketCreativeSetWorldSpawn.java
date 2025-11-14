/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketCreativeCheck;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketCreativeSetWorldSpawn
extends PacketCreativeCheck {
    public final LevelIdentifier levelIdentifier;
    public final Point tile;

    public PacketCreativeSetWorldSpawn(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        boolean reset = reader.getNextBoolean();
        if (reset) {
            this.levelIdentifier = null;
            this.tile = null;
        } else {
            this.levelIdentifier = new LevelIdentifier(reader);
            this.tile = new Point(reader.getNextInt(), reader.getNextInt());
        }
    }

    protected PacketCreativeSetWorldSpawn(LevelIdentifier levelIdentifier, Point tilePos) {
        this.levelIdentifier = levelIdentifier;
        this.tile = tilePos;
        PacketWriter writer = new PacketWriter(this);
        if (levelIdentifier != null) {
            writer.putNextBoolean(false);
            levelIdentifier.writePacket(writer);
            writer.putNextInt(tilePos.x);
            writer.putNextInt(tilePos.y);
        } else {
            writer.putNextBoolean(true);
        }
    }

    public static PacketCreativeSetWorldSpawn setSpawnPacket(LevelIdentifier levelIdentifier, Point tilePos) {
        return new PacketCreativeSetWorldSpawn(levelIdentifier, tilePos);
    }

    public static PacketCreativeSetWorldSpawn clearSpawnPacket() {
        return new PacketCreativeSetWorldSpawn(null, null);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!PacketCreativeSetWorldSpawn.checkCreativeAndSendUpdate(server, client)) {
            return;
        }
        LevelIdentifier levelIdentifier = this.levelIdentifier;
        Point tile = this.tile;
        if (levelIdentifier == null) {
            levelIdentifier = LevelIdentifier.SURFACE_IDENTIFIER;
            tile = server.world.worldEntity.defaultSpawnTile;
        }
        server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeworldspawnchanged", "player", client.getName(), "level", levelIdentifier.stringID, "tilex", tile.x, "tiley", tile.y)));
        server.world.worldEntity.spawnLevelIdentifier = levelIdentifier;
        server.world.worldEntity.spawnTile = tile;
    }
}

