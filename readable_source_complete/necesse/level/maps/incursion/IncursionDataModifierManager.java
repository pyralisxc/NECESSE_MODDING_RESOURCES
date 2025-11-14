/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.Collections;
import java.util.stream.Stream;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.IncursionDataModifiers;
import necesse.level.maps.levelBuffManager.ActiveLevelBuff;

public class IncursionDataModifierManager
extends ModifierManager<ModifierContainer> {
    private IncursionData incursionData;

    public IncursionDataModifierManager(IncursionData incursionData) {
        super(IncursionDataModifiers.LIST);
        this.incursionData = incursionData;
    }

    @Override
    protected Iterable<? extends ActiveLevelBuff> getModifierContainers() {
        return Collections.emptyList();
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return this.incursionData.nextIncursionPerkIDs.stream().map(IncursionPerksRegistry::getPerk).flatMap(IncursionPerk::getIncursionDataModifiers);
    }

    @Override
    public void updateModifiers() {
        super.updateModifiers();
    }
}

