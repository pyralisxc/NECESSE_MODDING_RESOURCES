/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.util.stream.Stream;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.modifiers.ModifierValue;
import necesse.level.maps.incursion.IncursionDataModifiers;

public class MoreHuntsPerk
extends IncursionPerk {
    public MoreHuntsPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public Stream<ModifierValue<?>> getIncursionDataModifiers() {
        return Stream.of(new ModifierValue<Float>(IncursionDataModifiers.MODIFIER_EXTRACTION_DROPS, Float.valueOf(1.0f)));
    }
}

