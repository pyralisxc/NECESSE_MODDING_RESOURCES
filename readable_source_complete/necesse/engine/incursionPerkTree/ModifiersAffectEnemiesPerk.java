/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.incursionModifiers.ModifiersAffectEnemiesLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class ModifiersAffectEnemiesPerk
extends IncursionPerk {
    public ModifiersAffectEnemiesPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        ModifiersAffectEnemiesLevelEvent event = new ModifiersAffectEnemiesLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt(this.getStringID() + modifierIndex, event.getUniqueID());
    }
}

