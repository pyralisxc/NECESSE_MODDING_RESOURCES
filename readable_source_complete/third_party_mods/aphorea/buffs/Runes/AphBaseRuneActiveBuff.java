/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 */
package aphorea.buffs.Runes;

import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.registry.AphBuffs;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class AphBaseRuneActiveBuff
extends Buff {
    protected float baseEffectNumber;
    protected float extraEffectNumberMod;
    protected int cooldown;
    protected String cooldownBuff;
    protected ModifierValue<?>[] modifiers;

    public AphBaseRuneActiveBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldown, String cooldownBuff, ModifierValue<?> ... modifiers) {
        this.baseEffectNumber = baseEffectNumber;
        this.extraEffectNumberMod = extraEffectNumberMod;
        this.cooldown = cooldown;
        this.cooldownBuff = cooldownBuff;
        this.modifiers = modifiers;
        this.isVisible = false;
        this.canCancel = false;
    }

    public AphBaseRuneActiveBuff(int cooldown, float extraEffectNumberMod, String cooldownBuff, ModifierValue<?> ... modifiers) {
        this(0.0f, extraEffectNumberMod, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, float extraEffectNumberMod, ModifierValue<?> ... modifiers) {
        this(cooldown, extraEffectNumberMod, (String)null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldown, ModifierValue<?> ... modifiers) {
        this(baseEffectNumber, extraEffectNumberMod, cooldown, (String)null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, String cooldownBuff, ModifierValue<?> ... modifiers) {
        this(baseEffectNumber, 1.0f, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, String cooldownBuff, ModifierValue<?> ... modifiers) {
        this(0.0f, 1.0f, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, ModifierValue<?> ... modifiers) {
        this(cooldown, 1.0f, (String)null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, ModifierValue<?> ... modifiers) {
        this(baseEffectNumber, 1.0f, cooldown, (String)null, modifiers);
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply((ModifierContainer)buff);
        }
        if (buff.owner.isPlayer) {
            this.initExtraModifiers(buff, this.getEffectNumber((PlayerMob)buff.owner));
        }
    }

    public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
    }

    public float getEffectNumber(PlayerMob player) {
        return this.baseEffectNumber * this.getEffectNumberVariation(player);
    }

    public float getEffectNumberVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(1.0f));
        AphBaseRuneTrinketBuff.getRuneModifiers(player).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getEffectNumberVariation())));
        return Math.max(variation.get().floatValue(), 0.0f) * this.extraEffectNumberMod;
    }

    public int getCooldownDuration(PlayerMob player) {
        return (int)((float)this.getBaseCooldownDuration() * AphBaseRuneTrinketBuff.getCooldownVariation(player));
    }

    public int getBaseCooldownDuration() {
        return this.cooldown;
    }

    public void onRemoved(ActiveBuff buff) {
        int cooldownDuration;
        super.onRemoved(buff);
        if (buff.owner.isPlayer && (cooldownDuration = this.getCooldownDuration((PlayerMob)buff.owner)) > 0) {
            if (this.cooldownBuff != null) {
                buff.owner.buffManager.addBuff(new ActiveBuff(this.cooldownBuff, buff.owner, cooldownDuration, null), false);
            }
            buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN, buff.owner, cooldownDuration, null), false);
        }
    }
}

