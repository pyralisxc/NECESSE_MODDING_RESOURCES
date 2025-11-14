/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.regionSystem.layers.ObjectRegionLayer;

public abstract class ObjectLayerAbstract {
    public final IDData idData = new IDData();
    protected final ObjectRegionLayer layer;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public ObjectLayerAbstract(ObjectRegionLayer layer) {
        this.layer = layer;
    }

    public abstract short getObjectID(int var1, int var2);

    public abstract void setObjectID(int var1, int var2, short var3);

    public abstract byte getObjectRotation(int var1, int var2);

    public abstract void setObjectRotation(int var1, int var2, byte var3);

    public abstract boolean isPlayerPlaced(int var1, int var2);

    public abstract void setIsPlayerPlaced(int var1, int var2, boolean var3);

    public void clearTile(int regionTileX, int regionTileY) {
        this.setObjectID(regionTileX, regionTileY, (short)0);
        this.setObjectRotation(regionTileX, regionTileY, (byte)0);
        this.setIsPlayerPlaced(regionTileX, regionTileY, false);
    }

    public abstract void clearLayer();

    protected abstract void addUsedObjectIDs(HashSet<Integer> var1);

    public abstract void addSaveData(SaveData var1);

    public abstract boolean applyLoadData(LoadData var1, int[] var2);

    public abstract void writeLayerPacket(PacketWriter var1);

    public abstract boolean readLayerPacket(PacketReader var1);
}

