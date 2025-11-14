/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.incursionPresets;

import necesse.level.maps.presets.Preset;

public class GraveyardMegaUpgradeShardsClusterPreset
extends Preset {
    public GraveyardMegaUpgradeShardsClusterPreset() {
        super("PRESET = {\n\twidth = 10,\n\theight = 8,\n\ttileIDs = [65, cryptash],\n\ttiles = [-1, -1, -1, -1, -1, 65, -1, -1, -1, -1, -1, -1, -1, 65, -1, -1, 65, -1, 65, -1, -1, -1, -1, 65, -1, 65, 65, 65, 65, -1, -1, -1, 65, 65, 65, 65, -1, -1, -1, 65, -1, 65, 65, 65, 65, 65, -1, 65, 65, -1, -1, -1, 65, -1, 65, 65, -1, -1, -1, -1, -1, -1, -1, 65, -1, 65, 65, 65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 727, deadwoodcandles, 1163, cryptupgradeshardorerocksmall],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1163, -1, -1, 0, 727, -1, -1, -1, -1, 0, 0, 0, 0, 1163, 0, 1163, -1, -1, 0, 727, 1163, 1163, 1163, 0, 1163, 0, -1, -1, 1163, 0, 0, 1163, 0, 1163, 1163, 1163, -1, -1, -1, 0, 1163, 0, 1163, 1163, 0, 0, -1, -1, -1, -1, 0, -1, 1163, -1, -1, 727, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 1, 1, 1, 0, 3, 3, 0, 1, 3, 3, 1, 1, 0, 0, 0, 0, 1, 0, 2, 3, 1, 0, 1, 0, 0, 2, 0, 2, 0, 3, 1, 0, 0, 0, 2, 0, 2, 2, 2, 1, 1, 1, 0, 2, 0, 1, 2, 3, 0, 1, 1, 1, 1, 0, 3, 2, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (!level.isLiquidTile(x, y)) continue;
                    return false;
                }
            }
            return true;
        });
    }
}

