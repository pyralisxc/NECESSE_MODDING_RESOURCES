/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.presets.Preset;

public class HousePreset
extends Preset {
    public HousePreset() {
        super(15, 15);
        int floor = TileRegistry.getTileID("dungeonfloor");
        int wall = ObjectRegistry.getObjectID("dungeonwall");
        this.fillTile(0, 0, 15, 15, floor);
        this.fillObject(0, 0, 15, 15, 0);
        this.boxObject(0, 0, 15, 15, wall);
        this.boxObject(0, 0, 13, 13, wall);
    }
}

