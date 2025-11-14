/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.LongStream;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemLongArrayList
extends GNDItem
implements Iterable<Long> {
    private ArrayList<Long> items;

    public GNDItemLongArrayList(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemLongArrayList(LoadData data) {
        this.applyLoadData(data);
    }

    public GNDItemLongArrayList(int initialCapacity) {
        this.items = new ArrayList(initialCapacity);
    }

    private GNDItemLongArrayList(ArrayList<Long> items) {
        this.items = items;
    }

    public GNDItemLongArrayList(Collection<Long> items) {
        this(new ArrayList<Long>(items));
    }

    public GNDItemLongArrayList(Long ... items) {
        this(Arrays.asList(items));
    }

    public GNDItemLongArrayList() {
        this.items = new ArrayList();
    }

    public long get(int index) {
        return this.items.get(index);
    }

    public void add(int index, long item) {
        this.items.add(index, item);
    }

    public boolean add(long item) {
        return this.items.add(item);
    }

    public long remove(int index) {
        return this.items.remove(index);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public Iterator<Long> iterator() {
        return this.items.iterator();
    }

    @Override
    public void forEach(Consumer<? super Long> action) {
        for (Long item : this.items) {
            action.accept(item);
        }
    }

    @Override
    public Spliterator<Long> spliterator() {
        return this.items.spliterator();
    }

    public LongStream stream() {
        return this.items.stream().mapToLong(e -> e);
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
        if (item instanceof GNDItemLongArrayList) {
            GNDItemLongArrayList other = (GNDItemLongArrayList)item;
            if (other.size() == this.size()) {
                for (int i = 0; i < this.items.size(); ++i) {
                    if (this.items.get(i) == other.items.get(i)) continue;
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
        ArrayList<Long> copy = new ArrayList<Long>(this.items);
        return new GNDItemLongArrayList(copy);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addLongCollection("value", this.items);
    }

    private void applyLoadData(LoadData data) {
        this.items = data.getLongCollection("value", new ArrayList<Long>(), false);
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
        for (Long item : this.items) {
            writer.putNextLong(item);
        }
    }

    @Override
    public void readPacket(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        this.items = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            this.items.add(reader.getNextLong());
        }
    }
}

