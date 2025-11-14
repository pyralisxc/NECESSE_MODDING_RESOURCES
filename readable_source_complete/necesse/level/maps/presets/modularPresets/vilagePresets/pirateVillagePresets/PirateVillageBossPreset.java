/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageBossPreset
extends PirateVillagePreset {
    public PirateVillageBossPreset(GameRandom random) {
        super(3, 3, false, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [12, woodfloor],\n\ttiles = [12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12],\n\tobjectIDs = [0, air, 195, goldlamp, 63, woodwall],\n\tobjects = [63, 63, 63, 0, 0, 0, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 63, 63, 0, 195, 0, 0, 0, 195, 0, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 0, 195, 0, 0, 0, 195, 0, 0, 63, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 0, 0, 0, 63, 63, 63],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(3, 2, random);
        this.applyRandomCoinStack(6, 4, random);
        this.applyRandomCoinStack(4, 7, random);
        this.applyRandomCoinStack(8, 6, random);
        this.addCustomApply(4, 4, 0, (level, levelX, levelY, dir, blackboard) -> {
            PirateCaptainMob captain = new PirateCaptainMob();
            captain.setLevel(level);
            level.entityManager.addMob(captain, levelX * 32 + 16, levelY * 32 + 16);
            captain.dropLadder = true;
            captain.canDespawn = false;
            return (level1, presetX, presetY) -> captain.remove();
        });
        Point stylistTile = random.getOneOf(new Point(3, 3), new Point(5, 5), new Point(3, 5), new Point(5, 3));
        this.addCustomApply(stylistTile.x, stylistTile.y, 0, (level, levelX, levelY, dir, blackboard) -> {
            StylistHumanMob stylistHuman = (StylistHumanMob)MobRegistry.getMob("stylisthuman", level);
            stylistHuman.setTrapped();
            stylistHuman.wasTrappedByPirates = true;
            level.entityManager.addMob(stylistHuman, levelX * 32 + 16, levelY * 32 + 16);
            return (level1, presetX, presetY) -> stylistHuman.remove();
        });
        this.open(1, 0, 0);
        this.open(2, 1, 1);
        this.open(1, 2, 2);
        this.open(0, 1, 3);
    }
}

