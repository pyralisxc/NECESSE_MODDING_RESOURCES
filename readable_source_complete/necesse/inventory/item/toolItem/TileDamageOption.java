/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;

public class TileDamageOption {
    public final int layerID;
    public final int tileX;
    public final int tileY;

    public TileDamageOption(int layerID, int tileX, int tileY) {
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public TileDamageOption(PacketReader reader) {
        this.layerID = reader.getNextByte();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextByte((byte)this.layerID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
    }

    public TileDamageOption(String prefix, GNDItemMap map) {
        if (prefix == null) {
            prefix = "";
        }
        this.layerID = map.getByte(prefix + (prefix.isEmpty() ? "layerID" : "LayerID"));
        this.tileX = map.getInt(prefix + (prefix.isEmpty() ? "tileX" : "TileX"));
        this.tileY = map.getInt(prefix + (prefix.isEmpty() ? "tileY" : "TileY"));
    }

    public void writeGNDMap(String prefix, GNDItemMap map) {
        if (prefix == null) {
            prefix = "";
        }
        map.setByte(prefix + (prefix.isEmpty() ? "layerID" : "LayerID"), (byte)this.layerID);
        map.setInt(prefix + (prefix.isEmpty() ? "tileX" : "TileX"), this.tileX);
        map.setInt(prefix + (prefix.isEmpty() ? "tileY" : "TileY"), this.tileY);
    }
}

