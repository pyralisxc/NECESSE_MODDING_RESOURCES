/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;

public class PacketPlayerLoadedRegions
extends Packet {
    public final int levelIdentifierHashCode;
    public final PointHashSet loadedRegionPositions;

    public PacketPlayerLoadedRegions(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.loadedRegionPositions = new PointHashSet();
        int count = reader.getNextShortUnsigned();
        for (int i = 0; i < count; ++i) {
            int regionX = reader.getNextInt();
            int regionY = reader.getNextInt();
            this.loadedRegionPositions.add(regionX, regionY);
        }
    }

    public PacketPlayerLoadedRegions(LevelIdentifier identifier, PointHashSet loadedRegionPositions) {
        this.levelIdentifierHashCode = identifier.hashCode();
        this.loadedRegionPositions = loadedRegionPositions;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(loadedRegionPositions.size());
        for (Point position : loadedRegionPositions) {
            writer.putNextInt(position.x);
            writer.putNextInt(position.y);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        ClientLevelLoading loading = client.levelManager.loading();
        if (loading != null) {
            loading.refreshLoadedRegionsFromServer(this.loadedRegionPositions);
        }
    }
}

