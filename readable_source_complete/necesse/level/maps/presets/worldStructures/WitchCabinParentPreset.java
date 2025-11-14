/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.entity.mobs.friendly.human.humanShop.FriendlyWitchHumanMob;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class WitchCabinParentPreset
extends LandStructurePreset {
    public WitchCabinParentPreset(int width, int height) {
        super(width, height);
    }

    protected void spawnWitch(int x, int y) {
        this.addMob("friendlywitchhuman", x, y, FriendlyWitchHumanMob.class, witch -> {
            witch.setHome(witch.getTileX(), witch.getTileY());
            witch.setLost(true);
        });
    }
}

