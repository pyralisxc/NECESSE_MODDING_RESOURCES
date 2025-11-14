/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.regionsStructure;

import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.util.GameMath;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.regionsStructure.RegionStructureDataAbstract;

public class RegionStructureDataMap
extends RegionStructureDataAbstract {
    protected final HashMap<Long, Region> regions = new HashMap();
    protected long lastRegionKey = Long.MIN_VALUE;
    protected Region lastRegion;
    protected boolean lastRegionLoadIfNotLoaded;

    public RegionStructureDataMap(RegionManager manager, RegionStructureDataAbstract.NewRegionConstructor constructor, RegionStructureDataAbstract.NewRegionLoader loader) {
        super(manager, constructor, loader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Region getRegion(int regionX, int regionY, boolean loadIfNotLoaded, boolean forceSkipGenerate) {
        long key = GameMath.getUniqueLongKey(regionX, regionY);
        if (loadIfNotLoaded) {
            Object object = this.manager.level.entityManager.lock;
            synchronized (object) {
                if (this.lastRegionKey == key && (this.lastRegionLoadIfNotLoaded || this.lastRegion != null)) {
                    return this.lastRegion;
                }
            }
            object = this.manager.level.entityManager.lock;
            synchronized (object) {
                Region region = this.regions.get(key);
                if (region == null && (region = this.constructor.construct(regionX, regionY)) != null) {
                    this.regions.put(key, region);
                    this.generator.load(region, forceSkipGenerate);
                }
                this.lastRegionKey = key;
                this.lastRegion = region;
                this.lastRegionLoadIfNotLoaded = true;
                return region;
            }
        }
        Object object = this.manager.level.entityManager.lock;
        synchronized (object) {
            if (this.lastRegionKey == key) {
                return this.lastRegion;
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            Region region = this.regions.get(key);
            this.lastRegionKey = key;
            this.lastRegion = region;
            this.lastRegionLoadIfNotLoaded = false;
            return region;
        }
    }

    @Override
    public Region removeRegion(int regionX, int regionY) {
        return this.regions.remove(GameMath.getUniqueLongKey(regionX, regionY));
    }

    @Override
    public boolean isRegionLoaded(int regionX, int regionY) {
        return this.regions.containsKey(GameMath.getUniqueLongKey(regionX, regionY));
    }

    @Override
    public int getLoadedRegionsSize() {
        return this.regions.size();
    }

    @Override
    public Iterator<Region> iterator() {
        return this.regions.values().iterator();
    }
}

