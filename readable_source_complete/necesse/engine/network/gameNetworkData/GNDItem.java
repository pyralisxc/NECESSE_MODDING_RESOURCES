/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.save.SaveData;

public abstract class GNDItem {
    public final IDData idData = new IDData();

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public GNDItem() {
        this.initIDData();
    }

    protected void initIDData() {
        GNDRegistry.applyIDData(this);
    }

    public abstract String toString();

    public abstract boolean isDefault();

    public static boolean isDefault(GNDItem item) {
        return item == null || item.isDefault();
    }

    public abstract boolean equals(GNDItem var1);

    public boolean equals(Object obj) {
        if (obj instanceof GNDItem) {
            return this.equals((GNDItem)obj);
        }
        return super.equals(obj);
    }

    public abstract GNDItem copy();

    public abstract void addSaveData(SaveData var1);

    public abstract void writePacket(PacketWriter var1);

    public abstract void readPacket(PacketReader var1);

    public static boolean equals(GNDItem item1, GNDItem item2) {
        return Objects.equals(item1, item2);
    }

    public static abstract class GNDPrimitive
    extends GNDItem {
        public abstract boolean getBoolean();

        public abstract byte getByte();

        public abstract short getShort();

        public abstract int getInt();

        public abstract long getLong();

        public abstract float getFloat();

        public abstract double getDouble();
    }
}

