/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.regionsStructure;

import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;

public abstract class RegionStructureDataAbstract
implements Iterable<Region> {
    public final RegionManager manager;
    protected final NewRegionConstructor constructor;
    protected final NewRegionLoader generator;

    public RegionStructureDataAbstract(RegionManager manager, NewRegionConstructor constructor, NewRegionLoader loader) {
        this.manager = manager;
        this.constructor = constructor;
        this.generator = loader;
    }

    public abstract Region getRegion(int var1, int var2, boolean var3, boolean var4);

    public abstract Region removeRegion(int var1, int var2);

    public abstract boolean isRegionLoaded(int var1, int var2);

    public abstract int getLoadedRegionsSize();

    @FunctionalInterface
    public static interface NewRegionConstructor {
        public Region construct(int var1, int var2);
    }

    @FunctionalInterface
    public static interface NewRegionLoader {
        public void load(Region var1, boolean var2);
    }
}

