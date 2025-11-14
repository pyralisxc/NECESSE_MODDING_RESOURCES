/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashMap;
import necesse.entity.manager.RegionUnloadedListenerEntityComponent;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.regionSystem.Region;

public class PathBreakDownLevelData
extends LevelData
implements RegionUnloadedListenerEntityComponent {
    protected PointHashMap<RegionBreakData> regionBreakData = new PointHashMap();

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void onRegionUnloaded(Region region) {
        this.regionBreakData.remove(region.regionX, region.regionY);
    }

    protected RegionBreakData getRegionData(int regionX, int regionY, boolean createIfMissing) {
        if (!createIfMissing) {
            return this.regionBreakData.get(regionX, regionY);
        }
        return this.regionBreakData.compute(regionX, regionY, (key, old) -> {
            if (old == null) {
                return new RegionBreakData();
            }
            return old;
        });
    }

    protected PathBreakData getTileData(int tileX, int tileY, boolean createIfMissing) {
        int regionY;
        int regionX = GameMath.getRegionCoordByTile(tileX);
        RegionBreakData regionData = this.getRegionData(regionX, regionY = GameMath.getRegionCoordByTile(tileY), createIfMissing);
        if (regionData == null) {
            return null;
        }
        if (!createIfMissing) {
            return regionData.tileBreakData.get(tileX, tileY);
        }
        return regionData.tileBreakData.compute(tileX, tileY, (tile, old) -> {
            if (old == null) {
                return new PathBreakData(tileX, tileY);
            }
            return old;
        });
    }

    public int getFinalDamage(int tileX, int tileY, int damage) {
        return this.getTileData(tileX, tileY, true).doDamage(damage);
    }

    public void clear(int tileX, int tileY) {
        int regionY;
        int regionX = GameMath.getRegionCoordByTile(tileX);
        RegionBreakData regionData = this.getRegionData(regionX, regionY = GameMath.getRegionCoordByTile(tileY), false);
        if (regionData == null) {
            return;
        }
        regionData.tileBreakData.remove(tileX, tileY);
    }

    public static PathBreakDownLevelData getPathBreakDownData(Level level) {
        LevelData pathBreak = level.getLevelData("pathbreak");
        if (pathBreak instanceof PathBreakDownLevelData) {
            return (PathBreakDownLevelData)pathBreak;
        }
        PathBreakDownLevelData newPathBreak = new PathBreakDownLevelData();
        level.addLevelData("pathbreak", newPathBreak);
        return newPathBreak;
    }

    protected class RegionBreakData {
        PointHashMap<PathBreakData> tileBreakData = new PointHashMap();

        protected RegionBreakData() {
        }
    }

    protected class PathBreakData {
        public final int tileX;
        public final int tileY;
        public int objectID;
        public float damageBuffer;
        public long lastDamageDoneTime;

        public PathBreakData(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.objectID = PathBreakDownLevelData.this.getLevel().getObjectID(tileX, tileY);
        }

        public int doDamage(int damage) {
            float floatDamage = damage;
            GameObject object = PathBreakDownLevelData.this.getLevel().getObject(this.tileX, this.tileY);
            if (object.getID() != this.objectID) {
                this.damageBuffer = 0.0f;
            } else {
                long timeSinceLastDamage = PathBreakDownLevelData.this.getLevel().getWorldEntity().getTime() - this.lastDamageDoneTime;
                if (timeSinceLastDamage > 60000L) {
                    this.damageBuffer = 0.0f;
                }
                if (timeSinceLastDamage < 1000L) {
                    float percentSince = (float)timeSinceLastDamage / 1000.0f;
                    floatDamage = Math.max(0.1f, floatDamage * percentSince);
                }
            }
            this.damageBuffer += floatDamage;
            this.lastDamageDoneTime = PathBreakDownLevelData.this.getLevel().getWorldEntity().getTime();
            if (this.damageBuffer >= 1.0f) {
                int finalDamage = (int)this.damageBuffer;
                this.damageBuffer -= (float)finalDamage;
                return finalDamage;
            }
            return 0;
        }
    }
}

