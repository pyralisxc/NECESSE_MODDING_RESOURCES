/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.regionsStructure;

import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.regionsStructure.RegionStructureDataAbstract;

public class RegionRegionStructureDataWorldRegionMap
extends RegionStructureDataAbstract {
    protected static final int WORLD_REGION_SIZE_BITS = 4;
    protected static final int WORLD_REGION_SIZE = 16;
    protected final HashMap<Long, WorldRegion> worldRegions = new HashMap();
    protected long lastWorldRegionGetKey;
    protected WorldRegion lastWorldRegionGet;

    public RegionRegionStructureDataWorldRegionMap(RegionManager manager, RegionStructureDataAbstract.NewRegionConstructor constructor, RegionStructureDataAbstract.NewRegionLoader loader) {
        super(manager, constructor, loader);
    }

    protected synchronized WorldRegion getWorldRegion(int regionX, int regionY, boolean createIfNotExists) {
        int worldRegionX = GameMath.divideByPowerOf2RoundedDown(regionX, 4);
        int worldRegionY = GameMath.divideByPowerOf2RoundedDown(regionY, 4);
        long worldKey = GameMath.getUniqueLongKey(worldRegionX, worldRegionY);
        if (createIfNotExists) {
            if (this.lastWorldRegionGetKey == worldKey && this.lastWorldRegionGet != null) {
                return this.lastWorldRegionGet;
            }
            WorldRegion worldRegion = this.worldRegions.get(worldKey);
            if (worldRegion == null) {
                worldRegion = new WorldRegion(worldRegionX, worldRegionY);
                this.worldRegions.put(worldKey, worldRegion);
            }
            this.lastWorldRegionGetKey = worldKey;
            this.lastWorldRegionGet = worldRegion;
            return worldRegion;
        }
        if (this.lastWorldRegionGetKey == worldKey) {
            return this.lastWorldRegionGet;
        }
        WorldRegion worldRegion = this.worldRegions.get(worldKey);
        this.lastWorldRegionGetKey = worldKey;
        this.lastWorldRegionGet = worldRegion;
        return worldRegion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized Region getRegion(int regionX, int regionY, boolean loadIfNotLoaded, boolean forceSkipGenerate) {
        WorldRegion worldRegion = this.getWorldRegion(regionX, regionY, loadIfNotLoaded);
        if (worldRegion != null) {
            if (loadIfNotLoaded) {
                Object object = this.manager.level.entityManager.lock;
                synchronized (object) {
                    return worldRegion.getRegion(regionX, regionY, this.constructor, this.generator, forceSkipGenerate);
                }
            }
            return worldRegion.getRegion(regionX, regionY, this.constructor, null, forceSkipGenerate);
        }
        return null;
    }

    @Override
    public synchronized Region removeRegion(int regionX, int regionY) {
        WorldRegion worldRegion = this.getWorldRegion(regionX, regionY, false);
        if (worldRegion == null) {
            return null;
        }
        Region out = worldRegion.removeRegion(regionX, regionY);
        if (worldRegion.isEmpty()) {
            long worldKey = GameMath.getUniqueLongKey(worldRegion.worldRegionX, worldRegion.worldRegionY);
            this.worldRegions.remove(worldKey);
        }
        return out;
    }

    @Override
    public boolean isRegionLoaded(int regionX, int regionY) {
        WorldRegion worldRegion = this.getWorldRegion(regionX, regionY, false);
        return worldRegion != null && worldRegion.isRegionLoaded(regionX, regionY);
    }

    @Override
    public int getLoadedRegionsSize() {
        return this.worldRegions.values().stream().mapToInt(worldRegion -> ((WorldRegion)worldRegion).list.size()).sum();
    }

    @Override
    public Iterator<Region> iterator() {
        return this.worldRegions.values().stream().flatMap(worldRegion -> ((WorldRegion)worldRegion).list.stream()).iterator();
    }

    protected static class WorldRegion
    implements Iterable<Region> {
        public final int worldRegionX;
        public final int worldRegionY;
        private final Region[][] regions;
        private final GameLinkedList.Element[][] listElements;
        private final GameLinkedList<Region> list;

        public WorldRegion(int worldRegionX, int worldRegionY) {
            this.worldRegionX = worldRegionX;
            this.worldRegionY = worldRegionY;
            this.regions = new Region[16][16];
            this.listElements = new GameLinkedList.Element[16][16];
            this.list = new GameLinkedList();
        }

        public synchronized Region getRegion(int regionX, int regionY, RegionStructureDataAbstract.NewRegionConstructor constructor, RegionStructureDataAbstract.NewRegionLoader loader, boolean forceSkipGenerate) {
            int localRegionX = regionX - GameMath.multiplyByPowerOf2(this.worldRegionX, 4);
            int localRegionY = regionY - GameMath.multiplyByPowerOf2(this.worldRegionY, 4);
            Region region = this.regions[localRegionX][localRegionY];
            if (loader != null && region == null && (region = constructor.construct(regionX, regionY)) != null) {
                GameLinkedList.Element element = this.list.addLast(region);
                this.regions[localRegionX][localRegionY] = region;
                this.listElements[localRegionX][localRegionY] = element;
                loader.load(region, forceSkipGenerate);
            }
            return region;
        }

        public synchronized Region removeRegion(int regionX, int regionY) {
            int localRegionY;
            int localRegionX = regionX - GameMath.multiplyByPowerOf2(this.worldRegionX, 4);
            Region region = this.regions[localRegionX][localRegionY = regionY - GameMath.multiplyByPowerOf2(this.worldRegionY, 4)];
            if (region != null) {
                this.listElements[localRegionX][localRegionY].remove();
                this.listElements[localRegionX][localRegionY] = null;
            }
            return region;
        }

        public synchronized boolean isRegionLoaded(int regionX, int regionY) {
            int localRegionY;
            int localRegionX = regionX - GameMath.multiplyByPowerOf2(this.worldRegionX, 4);
            return this.regions[localRegionX][localRegionY = regionY - GameMath.multiplyByPowerOf2(this.worldRegionY, 4)] != null;
        }

        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        @Override
        public Iterator<Region> iterator() {
            return this.list.iterator();
        }
    }
}

