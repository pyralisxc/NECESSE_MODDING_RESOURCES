/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.io.IOException;
import java.util.Arrays;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;

public abstract class UnsignedShortRegionLayer
extends RegionLayer
implements RegionPacketHandlerRegionLayer,
SaveDataRegionLayer {
    protected short[] data;

    public UnsignedShortRegionLayer(Region region) {
        super(region);
        this.data = new short[region.tileWidth * region.tileHeight];
        short defaultValue = (short)this.getDefault();
        if (defaultValue != 0) {
            Arrays.fill(this.data, defaultValue);
        }
    }

    protected int get(int regionTileX, int regionTileY) {
        return this.data[this.getDataIndex(regionTileX, regionTileY)] & 0xFFFF;
    }

    protected void set(int regionTileX, int regionTileY, int value) {
        this.data[this.getDataIndex((int)regionTileX, (int)regionTileY)] = (short)value;
    }

    protected int getDataIndex(int regionTileX, int regionTileY) {
        return regionTileX + regionTileY * this.region.tileWidth;
    }

    protected int getDefault() {
        return 0;
    }

    @Override
    public void writeLayerPacket(PacketWriter writer) {
        for (short value : this.data) {
            writer.putNextShort(value);
        }
    }

    @Override
    public boolean applyLayerPacket(PacketReader reader) {
        for (int i = 0; i < this.data.length; ++i) {
            int tileX = i % this.region.tileWidth;
            int tileY = i / this.region.tileWidth;
            short next = reader.getNextShort();
            if (!this.isValidValue(tileX, tileY, next & 0xFFFF)) {
                return false;
            }
            this.data[i] = next;
        }
        return true;
    }

    protected abstract boolean isValidValue(int var1, int var2, int var3);

    @Override
    public void addSaveData(SaveData save) {
        try {
            save.addCompressedShortArray(this.getSaveDataName(), this.data);
        }
        catch (IOException e) {
            save.addShortArray(this.getSaveDataName(), this.data);
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        try {
            short[] newData;
            LoadData load = save.getFirstLoadDataByName(this.getSaveDataName());
            if (load == null) {
                this.handleSaveNotFound();
                return;
            }
            try {
                newData = LoadData.getCompressedShortArray(load);
            }
            catch (Exception e) {
                newData = LoadData.getShortArray(load);
            }
            this.data = newData.length != this.data.length ? Arrays.copyOf(newData, this.data.length) : newData;
        }
        catch (Exception e) {
            this.handleLoadException(e);
        }
    }

    protected String getSaveDataName() {
        return this.getStringID();
    }

    protected void handleSaveNotFound() {
        GameLog.warn.println("Could not find " + this.level.getIdentifier() + " " + this.getSaveDataName() + " region data for region " + this.region.regionX + "x" + this.region.regionY);
    }

    protected void handleLoadException(Exception e) {
        System.err.println("Failed to load " + this.level.getIdentifier() + " " + this.getSaveDataName() + " region data in region " + this.region.regionX + "x" + this.region.regionY + ", resulting in a wipe");
        this.data = new short[this.region.tileWidth * this.region.tileHeight];
        short defaultValue = (short)this.getDefault();
        if (defaultValue != 0) {
            Arrays.fill(this.data, defaultValue);
        }
    }
}

