/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketUnloadRegion
extends Packet {
    public final int levelIdentifierHashCode;
    public final int regionX;
    public final int regionY;

    public PacketUnloadRegion(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.regionX = reader.getNextInt();
        this.regionY = reader.getNextInt();
    }

    public PacketUnloadRegion(Level level, int regionX, int regionY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.regionX = regionX;
        this.regionY = regionY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(regionX);
        writer.putNextInt(regionY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Level level = client.getLevel();
        if (level != null && level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            ClientLevelLoading loading = client.levelManager.loading();
            loading.unloadRegion(this.regionX, this.regionY, false);
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        if (level != null && level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            client.removeLoadedRegion(level, this.regionX, this.regionY, false, false);
        }
    }
}

