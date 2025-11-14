/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemNull;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemArray
extends GNDItem
implements Iterable<GNDItem> {
    private GNDItem[] items;

    public GNDItemArray(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemArray(LoadData data) {
        this.applyLoadData(data);
    }

    public GNDItemArray(int length) {
        this.items = new GNDItem[length];
    }

    public GNDItemArray(GNDItem ... items) {
        this.items = items;
    }

    public GNDItem get(int index) {
        return this.items[index];
    }

    public void set(int index, GNDItem item) {
        this.items[index] = item;
    }

    public int length() {
        return this.items.length;
    }

    @Override
    public Iterator<GNDItem> iterator() {
        return Arrays.stream(this.items).iterator();
    }

    @Override
    public void forEach(Consumer<? super GNDItem> action) {
        for (GNDItem item : this.items) {
            action.accept(item);
        }
    }

    @Override
    public Spliterator<GNDItem> spliterator() {
        return Arrays.stream(this.items).spliterator();
    }

    @Override
    public String toString() {
        return Arrays.toString(this.items);
    }

    @Override
    public boolean isDefault() {
        return this.items.length == 0;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemArray) {
            GNDItemArray other = (GNDItemArray)item;
            if (other.length() == this.length()) {
                for (int i = 0; i < this.items.length; ++i) {
                    if (this.items[i] != null && other.items[i] != null) {
                        if (this.items[i].equals(other.items[i])) continue;
                        return false;
                    }
                    if (this.items[i] == null && other.items[i] != null) {
                        return false;
                    }
                    if (this.items[i] == null || other.items[i] != null) continue;
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public GNDItem copy() {
        GNDItem[] copy = new GNDItem[this.items.length];
        for (int i = 0; i < this.items.length; ++i) {
            GNDItem item = this.items[i];
            copy[i] = item == null ? null : item.copy();
        }
        return new GNDItemArray(copy);
    }

    @Override
    public void addSaveData(SaveData data) {
        for (GNDItem item : this.items) {
            if (item == null) {
                item = new GNDItemNull();
            }
            SaveData itemData = new SaveData("");
            GNDRegistry.writeGNDItem(itemData, item);
            data.addSaveData(itemData);
        }
    }

    private void applyLoadData(LoadData data) {
        List<LoadData> comps = data.getLoadData();
        this.items = new GNDItem[comps.size()];
        for (int i = 0; i < comps.size(); ++i) {
            this.items[i] = GNDRegistry.loadGNDItem(comps.get(i));
            if (!(this.items[i] instanceof GNDItemNull)) continue;
            this.items[i] = null;
        }
    }

    public Packet getContentPacket() {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        this.writePacket(writer);
        return p;
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.items.length);
        for (GNDItem item : this.items) {
            if (item == null) {
                item = new GNDItemNull();
            }
            GNDRegistry.writeGNDItem(writer, item);
        }
    }

    @Override
    public void readPacket(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        this.items = new GNDItem[size];
        for (int i = 0; i < size; ++i) {
            this.items[i] = GNDRegistry.readGNDItem(reader);
            if (!(this.items[i] instanceof GNDItemNull)) continue;
            this.items[i] = null;
        }
    }
}

