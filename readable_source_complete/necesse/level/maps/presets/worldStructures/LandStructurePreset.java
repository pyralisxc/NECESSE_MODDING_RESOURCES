/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.level.maps.presets.Preset;

public class LandStructurePreset
extends Preset {
    public LandStructurePreset(int width, int height, int landPadding) {
        super(width, height);
        this.addCanApplyRectPredicate(-landPadding, -landPadding, width + landPadding * 2, height + landPadding * 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int tileX = levelStartX; tileX <= levelEndX; ++tileX) {
                for (int tileY = levelStartY; tileY <= levelEndY; ++tileY) {
                    if (level.isLiquidTile(tileX, tileY)) {
                        return false;
                    }
                    if (!level.isShore(tileX, tileY)) continue;
                    return false;
                }
            }
            return true;
        });
    }

    public LandStructurePreset(int width, int height) {
        this(width, height, 5);
    }
}

