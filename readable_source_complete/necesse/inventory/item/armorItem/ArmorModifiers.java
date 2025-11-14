/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.BuffModifiers;

public class ArmorModifiers
extends ModifierContainer {
    public ArmorModifiers(ModifierValue ... values) {
        super(BuffModifiers.LIST);
        for (ModifierValue value : values) {
            value.apply(this);
        }
    }

    public void onBeforeHit(Mob mob, MobBeforeHitEvent event) {
    }

    public void onBeforeAttacked(Mob mob, MobBeforeHitEvent event) {
    }

    public void onBeforeHitCalculated(Mob mob, MobBeforeHitCalculatedEvent event) {
    }

    public void onBeforeAttackedCalculated(Mob mob, MobBeforeHitCalculatedEvent event) {
    }

    public void onWasHitLogic(Mob mob, MobWasHitEvent event) {
    }

    public void onHasAttackedLogic(Mob mob, MobWasHitEvent event) {
    }

    public void serverTick(Mob mob) {
    }

    public void clientTick(Mob mob) {
    }

    public void tickEffect(Mob mob, boolean isCosmetic) {
    }
}

