/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.LightMap;

public class StaticLightMap
extends LightMap {
    public StaticLightMap(LightManager manager, int startX, int startY, int endX, int endY) {
        super(manager, startX, startY, endX, endY, 25);
    }

    @Override
    protected GameLight getNewLight(int x, int y) {
        GameLight light = this.level.objectLayer.getCombinedLight(x, y);
        light.combine(this.level.getTile(x, y).getLight(this.level));
        return light;
    }
}

