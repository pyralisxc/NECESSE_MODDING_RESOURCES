/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemNull;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemArrayList
extends GNDItem
implements Iterable<GNDItem> {
    private ArrayList<GNDItem> items;

    public GNDItemArrayList(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemArrayList(LoadData data) {
        this.applyLoadData(data);
    }

    public GNDItemArrayList(int initialCapacity) {
        this.items = new ArrayList(initialCapacity);
    }

    private GNDItemArrayList(ArrayList<GNDItem> items) {
        this.items = items;
    }

    public GNDItemArrayList(Collection<GNDItem> items) {
        this(new ArrayList<GNDItem>(items));
    }

    public GNDItemArrayList(GNDItem ... items) {
        this(Arrays.asList(items));
    }

    public GNDItemArrayList() {
        this.items = new ArrayList();
    }

    public GNDItem get(int index) {
        return this.items.get(index);
    }

    public void add(int index, GNDItem item) {
        this.items.add(index, item);
    }

    public boolean add(GNDItem item) {
        return this.items.add(item);
    }

    public GNDItem remove(int index) {
        return this.items.remove(index);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public Iterator<GNDItem> iterator() {
        return this.items.iterator();
    }

    @Override
    public void forEach(Consumer<? super GNDItem> action) {
        for (GNDItem item : this.items) {
            action.accept(item);
        }
    }

    @Override
    public Spliterator<GNDItem> spliterator() {
        return this.items.spliterator();
    }

    public Stream<GNDItem> stream() {
        return this.items.stream();
    }

    @Override
    public String toString() {
        return Arrays.toString(this.items.toArray());
    }

    @Override
    public boolean isDefault() {
        return this.items.isEmpty();
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemArrayList) {
            GNDItemArrayList other = (GNDItemArrayList)item;
            if (other.size() == this.size()) {
                for (int i = 0; i < this.items.size(); ++i) {
                    if (this.items.get(i) != null && other.items.get(i) != null) {
                        if (this.items.get(i).equals(other.items.get(i))) continue;
                        return false;
                    }
                    if (this.items.get(i) == null && other.items.get(i) != null) {
                        return false;
                    }
                    if (this.items.get(i) == null || other.items.get(i) != null) continue;
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
        ArrayList<GNDItem> copy = new ArrayList<GNDItem>(this.items.size());
        for (GNDItem item : this.items) {
            copy.add(item == null ? null : item.copy());
        }
        return new GNDItemArrayList(copy);
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
        this.items = new ArrayList(comps.size());
        for (LoadData comp : comps) {
            GNDItem item = GNDRegistry.loadGNDItem(comp);
            if (item instanceof GNDItemNull) {
                item = null;
            }
            this.items.add(item);
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
        writer.putNextShortUnsigned(this.items.size());
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
        this.items = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            GNDItem item = GNDRegistry.readGNDItem(reader);
            if (item instanceof GNDItemNull) {
                item = null;
            }
            this.items.add(item);
        }
    }
}

