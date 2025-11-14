/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.numbers;

public interface NumberSet {
    public int getIndex(int var1);

    public boolean contains(int var1);

    public void add(int var1);

    public int remove(int var1);

    public void clear();

    public int getDataSize();

    public boolean equals(Object var1);

    public Object clone();
}

