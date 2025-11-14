/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketIterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.layers.LevelLayer;

public class PacketLevelLayerData
extends Packet {
    public final int levelIdentifierHashCode;
    public final int layerID;
    public final PacketIterator iterator;

    public PacketLevelLayerData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.layerID = reader.getNextShortUnsigned();
        this.iterator = new PacketReader(reader);
    }

    public PacketLevelLayerData(LevelLayer layer) {
        this.levelIdentifierHashCode = layer.level.getIdentifierHashCode();
        this.layerID = layer.getID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(this.layerID);
        this.iterator = new PacketWriter(writer);
        layer.writeLevelDataPacket(writer);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    LevelLayer layer = level.getLayer(this.layerID, LevelLayer.class);
                    layer.readLevelDataPacket(new PacketReader(this.iterator));
                    server.network.sendToClientsAtEntireLevel((Packet)this, level);
                } else {
                    System.out.println(client.getName() + " tried to change level layer data on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to change level layer data, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to change level layer data, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        LevelLayer layer = client.getLevel().getLayer(this.layerID, LevelLayer.class);
        layer.readLevelDataPacket(new PacketReader(this.iterator));
    }
}

