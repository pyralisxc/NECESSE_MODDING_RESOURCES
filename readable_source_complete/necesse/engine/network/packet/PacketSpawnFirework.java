/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameMath;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.level.maps.Level;

public class PacketSpawnFirework
extends Packet {
    public final int levelIdentifierHashCode;
    public final float x;
    public final float y;
    public final int height;
    public final float size;
    public final GNDItemMap gndData;
    public final int seed;

    public PacketSpawnFirework(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.height = reader.getNextInt();
        this.size = reader.getNextFloat();
        this.gndData = new GNDItemMap(reader);
        this.seed = reader.getNextInt();
    }

    public PacketSpawnFirework(Level level, float x, float y, int height, float size, GNDItemMap gndData, int seed) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.x = x;
        this.y = y;
        this.height = height;
        this.size = size;
        this.gndData = gndData;
        this.seed = seed;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextInt(height);
        writer.putNextFloat(size);
        gndData.writePacket(writer);
        writer.putNextInt(seed);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, GameMath.getTileCoordinate(this.x), GameMath.getTileCoordinate(this.y), true)) {
            return;
        }
        FireworkPlaceableItem.spawnFireworks(this.gndData, client.getLevel(), this.x, this.y, this.height, this.size, this.seed);
    }
}

