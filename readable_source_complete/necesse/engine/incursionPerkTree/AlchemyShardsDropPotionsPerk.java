/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.incursionModifiers.AlchemyShardsDropPotionsLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class AlchemyShardsDropPotionsPerk
extends IncursionPerk {
    public static float POTION_DROP_CHANCE = 0.05f;

    public AlchemyShardsDropPotionsPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        AlchemyShardsDropPotionsLevelEvent event = new AlchemyShardsDropPotionsLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt(this.getStringID() + modifierIndex, event.getUniqueID());
    }
}

