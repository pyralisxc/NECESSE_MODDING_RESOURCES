/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.maps.regionSystem.Region;

public class PacketRegionData
extends Packet {
    public final int levelIdentifierHashCode;
    public final int regionX;
    public final int regionY;
    public final Packet regionData;

    public PacketRegionData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.regionX = reader.getNextInt();
        this.regionY = reader.getNextInt();
        this.regionData = reader.getNextContentPacket();
    }

    public PacketRegionData(Region region) {
        this.levelIdentifierHashCode = region.manager.level.getIdentifierHashCode();
        this.regionX = region.regionX;
        this.regionY = region.regionY;
        this.regionData = new Packet();
        region.writeRegionDataPacket(new PacketWriter(this.regionData));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.regionX);
        writer.putNextInt(this.regionY);
        writer.putNextContentPacket(this.regionData);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        client.levelManager.loading().applyRegionData(this);
        client.loading.levelPreloadPhase.updateLoadingMessage();
    }
}

