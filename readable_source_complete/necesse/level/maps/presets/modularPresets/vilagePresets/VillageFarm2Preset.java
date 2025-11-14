/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import java.util.LinkedList;
import java.util.function.Function;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageFarm2Preset
extends VillagePreset {
    public VillageFarm2Preset(GameRandom random) {
        super(4, 4, false, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 240, feedingtrough2, 55, woodfence, 56, woodfencegate, 239, feedingtrough],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 55, 55, 56, 55, 55, 55, 55, 55, 55, 55, 0, 0, 55, 0, 0, 0, 0, 239, 240, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.open(1, 0, 0);
        this.open(2, 0, 0);
        this.addCustomApplyArea(4, 4, this.width - 4, this.height - 4, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir, blackboard) -> {
            LinkedList<Mob> mobs = new LinkedList<Mob>();
            Function<GameRandom, String> husbandryMobGetter = PresetUtils.getRandomHusbandryMobGetter(random);
            int count = random.getIntBetween(5, 9);
            for (int i = 0; i < count; ++i) {
                String mobStringID = husbandryMobGetter.apply(random);
                if (mobStringID == null) continue;
                Mob mob = MobRegistry.getMob(mobStringID, level);
                if (mob instanceof FriendlyRopableMob) {
                    ((FriendlyRopableMob)mob).setDefaultBuyPrice(random);
                }
                level.entityManager.addMob(mob, random.getIntBetween(levelStartX, levelEndX) * 32 + 16, random.getIntBetween(levelStartY, levelEndY) * 32 + 16);
                mobs.add(mob);
            }
            return (l, presetX, presetY) -> mobs.forEach(Mob::remove);
        });
    }
}

