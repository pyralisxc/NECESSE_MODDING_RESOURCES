/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.gameObject.ModularCarpetObject;

public class GroundSlimeObject
extends ModularCarpetObject {
    public GroundSlimeObject() {
        super("groundslime", new Color(182, 197, 60));
        this.objectHealth = 1;
        this.isGrass = true;
        this.canPlaceOnShore = true;
    }

    @Override
    public ModifierValue<Float> getSlowModifier(Mob mob) {
        if (mob.isHostile || mob.isFlying()) {
            return super.getSpeedModifier(mob);
        }
        return new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.5f));
    }
}

