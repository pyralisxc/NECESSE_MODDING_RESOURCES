/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;

public abstract class GameMapIcon
implements IDDataContainer {
    public final IDData idData = new IDData();

    @Override
    public final String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public final int getID() {
        return this.idData.getID();
    }

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public void loadTextures() {
    }

    public abstract Rectangle getDrawBoundingBox();

    public abstract void drawIcon(int var1, int var2, Color var3);
}

