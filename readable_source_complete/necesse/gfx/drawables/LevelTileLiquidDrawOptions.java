/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.util.HashMap;
import necesse.engine.util.GameMath;
import necesse.gfx.drawables.LevelTileLiquidRegionDrawOptions;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LevelTileLiquidDrawOptions {
    private final Level level;
    private final HashMap<Long, LevelTileLiquidRegionDrawOptions> regionMap = new HashMap();
    private long lastKey = Long.MIN_VALUE;
    private LevelTileLiquidRegionDrawOptions lastDrawOptions;

    public LevelTileLiquidDrawOptions(Level level) {
        this.level = level;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LevelTileLiquidRegionDrawOptions getByTile(Level level, int tileX, int tileY) {
        int regionX = level.regionManager.getRegionCoordByTile(tileX);
        int regionY = level.regionManager.getRegionCoordByTile(tileY);
        long key = GameMath.getUniqueLongKey(regionX, regionY);
        Object object = level.entityManager.lock;
        synchronized (object) {
            Region region;
            if (this.lastKey == key) {
                return this.lastDrawOptions;
            }
            LevelTileLiquidRegionDrawOptions drawOptions = this.regionMap.get(key);
            if (drawOptions == null && (region = level.regionManager.getRegion(regionX, regionY, false)) != null) {
                drawOptions = new LevelTileLiquidRegionDrawOptions(region);
                this.regionMap.put(key, drawOptions);
            }
            this.lastKey = key;
            this.lastDrawOptions = drawOptions;
            return drawOptions;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw() {
        Object object = this.level.entityManager.lock;
        synchronized (object) {
            for (LevelTileLiquidRegionDrawOptions drawOptions : this.regionMap.values()) {
                drawOptions.draw();
            }
        }
    }
}

