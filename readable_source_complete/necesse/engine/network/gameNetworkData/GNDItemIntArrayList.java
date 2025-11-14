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
import java.util.stream.IntStream;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemIntArrayList
extends GNDItem
implements Iterable<Integer> {
    private ArrayList<Integer> items;

    public GNDItemIntArrayList(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemIntArrayList(LoadData data) {
        this.applyLoadData(data);
    }

    public GNDItemIntArrayList(int initialCapacity) {
        this.items = new ArrayList(initialCapacity);
    }

    private GNDItemIntArrayList(ArrayList<Integer> items) {
        this.items = items;
    }

    public GNDItemIntArrayList(Collection<Integer> items) {
        this(new ArrayList<Integer>(items));
    }

    public GNDItemIntArrayList(Integer ... items) {
        this(Arrays.asList(items));
    }

    public GNDItemIntArrayList() {
        this.items = new ArrayList();
    }

    public int get(int index) {
        return this.items.get(index);
    }

    public void add(int index, int item) {
        this.items.add(index, item);
    }

    public boolean add(int item) {
        return this.items.add(item);
    }

    public int remove(int index) {
        return this.items.remove(index);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public Iterator<Integer> iterator() {
        return this.items.iterator();
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
        for (Integer item : this.items) {
            action.accept(item);
        }
    }

    @Override
    public Spliterator<Integer> spliterator() {
        return this.items.spliterator();
    }

    public IntStream stream() {
        return this.items.stream().mapToInt(e -> e);
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
        if (item instanceof GNDItemIntArrayList) {
            GNDItemIntArrayList other = (GNDItemIntArrayList)item;
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
        ArrayList<Integer> copy = new ArrayList<Integer>(this.items);
        return new GNDItemIntArrayList(copy);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addIntCollection("value", this.items);
    }

    private void applyLoadData(LoadData data) {
        this.items = data.getIntCollection("value", new ArrayList<Integer>(), false);
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
        for (Integer item : this.items) {
            writer.putNextInt(item);
        }
    }

    @Override
    public void readPacket(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        this.items = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            this.items.add(reader.getNextInt());
        }
    }
}

