/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.packet.PacketRegionsData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelDeathLocation;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.WorldDeathLocation;
import necesse.level.maps.Level;

public class PacketLevelData
extends Packet {
    public int levelID;
    public final LevelIdentifier levelIdentifier;
    public final int width;
    public final int height;
    public final ArrayList<LevelDeathLocation> deathLocations;
    public final Packet levelContent;
    public PointHashMap<Packet> regionData;

    public PacketLevelData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelID = reader.getNextShortUnsigned();
        this.levelIdentifier = new LevelIdentifier(reader);
        this.width = reader.getNextInt();
        this.height = reader.getNextInt();
        int deathLocationsCount = reader.getNextShortUnsigned();
        this.deathLocations = new ArrayList(deathLocationsCount);
        for (int i = 0; i < deathLocationsCount; ++i) {
            int secondsSince = reader.getNextInt();
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            this.deathLocations.add(new LevelDeathLocation(secondsSince, x, y));
        }
        this.levelContent = reader.getNextContentPacket();
        this.regionData = PacketRegionsData.readRegionsDataPacket(reader);
    }

    public PacketLevelData(Level level, ServerClient client, Collection<Point> regions) {
        this.levelID = level.getID();
        this.levelIdentifier = level.getIdentifier();
        this.width = level.tileWidth;
        this.height = level.tileHeight;
        this.deathLocations = new ArrayList();
        for (WorldDeathLocation deathLocation : client.getDeathLocations()) {
            if (!deathLocation.levelIdentifier.equals(this.levelIdentifier)) continue;
            this.deathLocations.add(new LevelDeathLocation(deathLocation.getSecondsSince(client.characterStats()), deathLocation.x, deathLocation.y));
        }
        this.levelContent = new Packet();
        level.writeLevelDataPacket(new PacketWriter(this.levelContent));
        this.regionData = PacketRegionsData.toRegionData(level, regions);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(this.levelID);
        this.levelIdentifier.writePacket(writer);
        writer.putNextInt(this.width);
        writer.putNextInt(this.height);
        writer.putNextShortUnsigned(this.deathLocations.size());
        for (LevelDeathLocation location : this.deathLocations) {
            writer.putNextInt(location.secondsSince);
            writer.putNextInt(location.x);
            writer.putNextInt(location.y);
        }
        writer.putNextContentPacket(this.levelContent);
        PacketRegionsData.writeRegionsDataPacket(writer, this.regionData);
    }

    public boolean isSameLevel(Level level) {
        return level.getIdentifier().equals(this.levelIdentifier) && level.tileWidth == this.width && level.tileHeight == this.height;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.worldEntity == null) {
            GameLog.warn.println("Got level data packet before getting world entity packet");
            return;
        }
        if (client.levelManager.updateLevel(this)) {
            client.loading.levelDataPhase.submitLevelDataPacket(this);
            ClientLevelLoading loading = client.levelManager.loading();
            if (loading.level.getIdentifier().equals(this.levelIdentifier)) {
                loading.applyRegionData(this.regionData);
            }
        }
    }
}

