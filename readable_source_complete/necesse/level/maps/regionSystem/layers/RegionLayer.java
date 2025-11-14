/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import necesse.engine.registries.IDData;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;

public abstract class RegionLayer {
    public final IDData idData = new IDData();
    public final Region region;
    public final RegionManager manager;
    public final Level level;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public RegionLayer(Region region) {
        this.region = region;
        this.manager = region.manager;
        this.level = region.manager.level;
    }

    public abstract void init();

    public abstract void onLayerLoaded();

    public abstract void onLoadingComplete();

    public void simulateWorld(long worldTimeIncrease, boolean sendChanges) {
    }

    public abstract void onLayerUnloaded();

    public void onDispose() {
    }
}

