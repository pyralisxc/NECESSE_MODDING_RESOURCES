/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.Level
 */
package aphorea.buffs.Runes;

import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import aphorea.items.runes.AphRunesInjector;
import aphorea.registry.AphBuffs;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class AphBaseRuneTrinketBuff
extends TrinketBuff {
    protected float baseEffectNumber;
    protected float extraEffectNumberMod;
    protected final int duration;
    protected String buff;
    protected final boolean isTemporary;
    protected float durationModifier;
    protected float cooldownModifier;
    protected boolean preventUsage;
    protected boolean preventCooldown;
    protected boolean preventBuff;
    protected boolean temporalBuff;
    protected float healthCost;

    private AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int duration, String buff, boolean isTemporary) {
        this.baseEffectNumber = baseEffectNumber;
        this.extraEffectNumberMod = extraEffectNumberMod;
        this.duration = duration;
        this.buff = buff;
        this.isTemporary = isTemporary;
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int duration, String buff) {
        this(baseEffectNumber, extraEffectNumberMod, duration, buff, true);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff, boolean isTemporary) {
        this(baseEffectNumber, 1.0f, duration, buff, isTemporary);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff) {
        this(baseEffectNumber, 1.0f, duration, buff, true);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldownDuration) {
        this(baseEffectNumber, extraEffectNumberMod, cooldownDuration, null, false);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int cooldownDuration) {
        this(baseEffectNumber, 1.0f, cooldownDuration, null, false);
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public String canRun(PlayerMob player) {
        if (player.buffManager.hasBuff(AphBuffs.RUNE_INJECTOR_ACTIVE) || player.buffManager.hasBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN)) {
            return "";
        }
        if (player.getLevel().isTrialRoom) {
            return "cannotusetrial";
        }
        if (player.getHealthPercent() <= this.getHealthCost(player)) {
            return "insuficienthealth";
        }
        return null;
    }

    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
        this.initRun(player.getLevel(), player, targetX, targetY);
        if (!this.preventUsage) {
            this.run(player.getLevel(), player, targetX, targetY);
        }
        if (!this.preventUsage) {
            int duration = this.getDuration(player);
            if (duration > 0) {
                if (this.buff != null && !this.preventBuff) {
                    player.buffManager.addBuff(new ActiveBuff(this.buff, (Mob)player, this.temporalBuff ? (int)((float)duration * this.durationModifier) : (int)((float)this.getCooldownDuration(player) * this.cooldownModifier), null), true);
                }
                if (this.temporalBuff) {
                    player.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_ACTIVE, (Mob)player, (int)((float)duration * this.durationModifier), null), true);
                } else if (!this.preventCooldown) {
                    player.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN, (Mob)player, (int)((float)this.getCooldownDuration(player) * this.cooldownModifier), null), true);
                }
            }
            List modifiersList = AphBaseRuneTrinketBuff.getRuneModifiers(player).collect(Collectors.toList());
            for (AphModifierRuneTrinketBuff aphModifierRuneTrinketBuff : modifiersList) {
                aphModifierRuneTrinketBuff.runServer(server, player, targetX, targetY);
            }
        }
        this.postRun(player.getLevel(), player, targetX, targetY);
    }

    public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
        this.initRun(client.getLevel(), player, targetX, targetY);
        if (!this.preventUsage) {
            this.run(client.getLevel(), player, targetX, targetY);
        }
        if (!this.preventUsage) {
            List modifiersList = AphBaseRuneTrinketBuff.getRuneModifiers(player).collect(Collectors.toList());
            for (AphModifierRuneTrinketBuff aphModifierRuneTrinketBuff : modifiersList) {
                aphModifierRuneTrinketBuff.runClient(client, player, targetX, targetY);
            }
        }
        this.postRun(client.getLevel(), player, targetX, targetY);
    }

    public void initRun(Level level, PlayerMob player, int targetX, int targetY) {
        this.durationModifier = 1.0f;
        this.cooldownModifier = 1.0f;
        this.preventUsage = false;
        this.preventCooldown = false;
        this.preventBuff = false;
        this.temporalBuff = this.isTemporary;
    }

    public void run(Level level, PlayerMob player, int targetX, int targetY) {
        int healthMod;
        float healthCost;
        if (!this.preventUsage && (healthCost = this.getHealthCost(player)) != 0.0f && (healthMod = (int)(-healthCost * (float)player.getMaxHealth())) != 0) {
            if (level.isServer()) {
                player.getLevel().entityManager.events.add((LevelEvent)new MobHealthChangeEvent((Mob)player, healthMod));
            } else if (level.isClient()) {
                if (healthCost > 0.0f) {
                    SoundManager.playSound((GameSound)GameResources.npchurt, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).pitch(((Float)GameRandom.globalRandom.getOneOf((Object[])new Float[]{Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)})).floatValue()));
                } else {
                    SoundManager.playSound((GameSound)GameResources.magicbolt1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(1.0f).pitch(1.0f));
                }
            }
        }
    }

    public void postRun(Level level, PlayerMob player, int targetX, int targetY) {
    }

    public int getDuration(PlayerMob player) {
        return this.duration;
    }

    public int getBaseCooldown() {
        return this.duration;
    }

    public int getCooldownDuration(PlayerMob player) {
        return (int)((float)this.duration * AphBaseRuneTrinketBuff.getCooldownVariation(player));
    }

    public AphBaseRuneTrinketBuff setHealthCost(float healthCost) {
        this.healthCost = healthCost;
        return this;
    }

    public float getBaseHealthCost() {
        return this.healthCost - this.getBaseHealing();
    }

    public float getHealthCost(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(0.0f));
        AphBaseRuneTrinketBuff.getRuneModifiers(player).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getHealthCost())));
        return this.healthCost + variation.get().floatValue() - this.getHealing(this.getEffectNumberVariation(player));
    }

    public float getBaseHealing() {
        return 0.0f;
    }

    public float getHealing(float effectNumberVariation) {
        return this.getBaseHealing();
    }

    public float getBaseEffectNumber() {
        return this.baseEffectNumber;
    }

    public float getEffectNumber(PlayerMob player) {
        return this.getBaseEffectNumber() * this.getEffectNumberVariation(player);
    }

    public static float getCooldownVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(1.0f));
        AphBaseRuneTrinketBuff.getRuneModifiers(player).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getCooldownVariation())));
        return Math.max(variation.get().floatValue(), 0.1f);
    }

    public float getEffectNumberVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(1.0f));
        AphBaseRuneTrinketBuff.getRuneModifiers(player).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getEffectNumberVariation())));
        return Math.max(1.0f + (variation.get().floatValue() - 1.0f) * this.extraEffectNumberMod, 0.0f);
    }

    public float getEffectNumberVariation(InventoryItem item, AphRunesInjector runesInjector) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(1.0f));
        runesInjector.getModifierBuffs(item).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getEffectNumberVariation())));
        return Math.max(1.0f + (variation.get().floatValue() - 1.0f) * this.extraEffectNumberMod, 0.0f);
    }

    public static float getCooldownVariation(InventoryItem item, AphRunesInjector runesInjector) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(1.0f));
        runesInjector.getModifierBuffs(item).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getCooldownVariation())));
        return Math.max(variation.get().floatValue(), 0.0f);
    }

    public float getFinalHealthCost(InventoryItem item, AphRunesInjector runesInjector) {
        return this.getFinalHealthCost(item, runesInjector, this.getEffectNumberVariation(item, runesInjector));
    }

    public float getFinalHealthCost(InventoryItem item, AphRunesInjector runesInjector, float effectNumberVariation) {
        AtomicReference<Float> variation = new AtomicReference<Float>(Float.valueOf(0.0f));
        runesInjector.getModifierBuffs(item).forEach(b -> variation.updateAndGet(v -> Float.valueOf(v.floatValue() + b.getHealthCost())));
        return this.healthCost + variation.get().floatValue() - this.getHealing(effectNumberVariation);
    }

    public static Stream<AphModifierRuneTrinketBuff> getRuneModifiers(PlayerMob player) {
        return player.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphModifierRuneTrinketBuff).map(b -> (AphModifierRuneTrinketBuff)b.buff);
    }

    public String getBuff() {
        return this.buff;
    }

    public float getExtraEffectNumberMod() {
        return this.extraEffectNumberMod;
    }

    public boolean isTemporary() {
        return this.isTemporary;
    }
}

