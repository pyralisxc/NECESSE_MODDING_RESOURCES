/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.regionsStructure;

import java.util.Iterator;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.regionsStructure.RegionStructureDataAbstract;

public class RegionStructureDataArray
extends RegionStructureDataAbstract {
    protected int regionsWidth;
    protected int regionsHeight;
    protected final Region[][] regions;
    protected final GameLinkedList.Element[][] listElements;
    protected final GameLinkedList<Region> list;

    public RegionStructureDataArray(RegionManager manager, RegionStructureDataAbstract.NewRegionConstructor constructor, RegionStructureDataAbstract.NewRegionLoader loader) {
        super(manager, constructor, loader);
        if (manager.level.tileWidth < 0 || manager.level.tileHeight < 0) {
            throw new IllegalArgumentException("Level must have a size to use RegionLayersDataArray");
        }
        this.regionsWidth = GameMath.divideByPowerOf2RoundedDown(manager.level.tileWidth, 4) + (manager.level.tileWidth % 16 > 0 ? 1 : 0);
        this.regionsHeight = GameMath.divideByPowerOf2RoundedDown(manager.level.tileHeight, 4) + (manager.level.tileHeight % 16 > 0 ? 1 : 0);
        this.regions = new Region[this.regionsWidth][this.regionsHeight];
        this.listElements = new GameLinkedList.Element[this.regionsWidth][this.regionsHeight];
        this.list = new GameLinkedList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized Region getRegion(int regionX, int regionY, boolean loadIfNotLoaded, boolean forceSkipGenerate) {
        if (regionX < 0 || regionX >= this.regionsWidth || regionY < 0 || regionY >= this.regionsHeight) {
            return null;
        }
        Region region = this.regions[regionX][regionY];
        if (region == null && loadIfNotLoaded && (region = this.constructor.construct(regionX, regionY)) != null) {
            Object object = this.manager.level.entityManager.lock;
            synchronized (object) {
                this.regions[regionX][regionY] = region;
                this.listElements[regionX][regionY] = this.list.addLast(region);
                this.generator.load(region, forceSkipGenerate);
            }
        }
        return region;
    }

    @Override
    public synchronized Region removeRegion(int regionX, int regionY) {
        if (regionX < 0 || regionX >= this.regionsWidth || regionY < 0 || regionY >= this.regionsHeight) {
            return null;
        }
        Region region = this.regions[regionX][regionY];
        if (region != null) {
            this.listElements[regionX][regionY].remove();
            this.listElements[regionX][regionY] = null;
            this.regions[regionX][regionY] = null;
        }
        return region;
    }

    @Override
    public synchronized boolean isRegionLoaded(int regionX, int regionY) {
        if (regionX < 0 || regionX >= this.regionsWidth || regionY < 0 || regionY >= this.regionsHeight) {
            return false;
        }
        return this.regions[regionX][regionY] != null;
    }

    @Override
    public int getLoadedRegionsSize() {
        return this.list.size();
    }

    @Override
    public Iterator<Region> iterator() {
        return this.list.iterator();
    }
}

