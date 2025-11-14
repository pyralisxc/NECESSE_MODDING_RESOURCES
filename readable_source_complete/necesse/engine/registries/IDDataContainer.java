/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.registries.IDData;

public interface IDDataContainer {
    public IDData getIDData();

    default public String getStringID() {
        return this.getIDData().getStringID();
    }

    default public int getID() {
        return this.getIDData().getID();
    }
}

