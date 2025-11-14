/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import java.util.Collection;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.HashMapPointEntry;
import necesse.engine.util.PointHashMap;
import necesse.level.maps.Level;

public class PacketRegionsData
extends Packet {
    public final int levelIdentifierHashCode;
    public PointHashMap<Packet> regionData;

    public PacketRegionsData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.regionData = PacketRegionsData.readRegionsDataPacket(reader);
    }

    public PacketRegionsData(ServerClient client, Level level, Collection<Point> regions) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.regionData = PacketRegionsData.toRegionData(level, regions);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        PacketRegionsData.writeRegionsDataPacket(writer, this.regionData);
    }

    public static PointHashMap<Packet> toRegionData(Level level, Collection<Point> regions) {
        PointHashMap<Packet> regionData = new PointHashMap<Packet>(regions.size());
        for (Point regionPos : regions) {
            regionData.put(regionPos.x, regionPos.y, level.regionManager.getRegionDataPacket(regionPos.x, regionPos.y));
        }
        return regionData;
    }

    public static void writeRegionsDataPacket(PacketWriter writer, PointHashMap<Packet> regionData) {
        writer.putNextShortUnsigned(regionData.size());
        for (HashMapPointEntry<Point, Packet> entry : regionData.getEntries()) {
            Point regionPos = entry.getKey();
            writer.putNextInt(regionPos.x);
            writer.putNextInt(regionPos.y);
            writer.putNextContentPacket(entry.getValue());
        }
    }

    public static PointHashMap<Packet> readRegionsDataPacket(PacketReader reader) {
        int regionsSize = reader.getNextShortUnsigned();
        PointHashMap<Packet> regionData = new PointHashMap<Packet>(regionsSize);
        for (int i = 0; i < regionsSize; ++i) {
            int regionX = reader.getNextInt();
            int regionY = reader.getNextInt();
            Packet regionContent = reader.getNextContentPacket();
            regionData.put(regionX, regionY, regionContent);
        }
        return regionData;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        client.levelManager.loading().applyRegionData(this.regionData);
        client.loading.levelPreloadPhase.updateLoadingMessage();
    }
}

