/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.FastLightMap;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.regionSystem.Region;

public class FastParticleLightMap
extends FastLightMap {
    public FastParticleLightMap(LightManager manager, int startX, int startY, int endX, int endY) {
        super(manager, startX, startY, endX, endY, 15);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected GameLight getNewLight(int x, int y) {
        Region region = this.level.regionManager.getRegionByTile(x, y, false);
        if (region == null) {
            return this.manager.newLight(0.0f);
        }
        Object object = this.manager.particlesLock;
        synchronized (object) {
            GameLight light = region.lightLayer.getParticleLightByRegion(x - region.tileXOffset, y - region.tileYOffset);
            if (light == null) {
                return this.manager.newLight(0.0f);
            }
            return light;
        }
    }
}

