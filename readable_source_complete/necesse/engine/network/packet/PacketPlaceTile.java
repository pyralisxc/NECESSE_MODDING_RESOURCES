/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class PacketPlaceTile
extends Packet {
    public final int levelIdentifierHashCode;
    public final int slot;
    public final int tileID;
    public final int tileX;
    public final int tileY;

    public PacketPlaceTile(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.slot = reader.getNextByteUnsigned();
        this.tileID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketPlaceTile(Level level, ServerClient client, int tileID, int tileX, int tileY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.slot = client == null ? 255 : client.slot;
        this.tileID = tileID;
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextInt(tileID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    public GameTile getTile() {
        return TileRegistry.getTile(this.tileID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        GameTile tile = this.getTile();
        if (this.slot == client.getSlot()) {
            tile.playPlaceSound(this.tileX, this.tileY);
        }
        Level level = client.getLevel();
        tile.placeTile(level, this.tileX, this.tileY, true);
        level.tileLayer.setIsPlayerPlaced(this.tileX, this.tileY, true);
    }
}

