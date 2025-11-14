/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.presets.Preset;

public class AscendedCagePreset
extends Preset {
    public AscendedCagePreset() {
        super(9, 9);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [5, sandtile],\n\ttiles = [5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5],\n\tobjectIDs = [992, rock, 1120, ascendedwall, 0, air],\n\tobjects = [992, 992, 1120, 1120, 1120, 1120, 1120, 992, 992, 992, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 992, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 992, 992, 992, 1120, 1120, 1120, 1120, 1120, 1120, 992, 992, 992, 1120, 1120, 1120, 1120, 1120, 1120, 992, 992, 992, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 992, 1120, 1120, 1120, 1120, 1120, 1120, 1120, 992, 992, 992, 1120, 1120, 1120, 1120, 1120, 992, 992],\n\trotations = [3, 3, 2, 2, 2, 2, 2, 3, 3, 3, 0, 0, 3, 3, 3, 0, 0, 3, 2, 3, 0, 3, 3, 0, 1, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 2, 2, 3, 0, 0, 0, 0, 3, 1, 2, 2, 0, 0, 0, 0, 0, 1, 3, 3, 2, 0, 0, 0, 0, 0, 0, 1, 3, 3, 0, 0, 0, 0, 1, 3, 0, 3, 3, 3, 2, 2, 2, 2, 3, 3, 3],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        this.replaceTile(TileRegistry.sandID, -1);
        this.replaceObject(ObjectRegistry.getObjectID("rock"), -1);
    }
}

