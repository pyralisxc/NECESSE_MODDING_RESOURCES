/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.PerfectBlockTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.light.GameLight;

public class ShieldTrinketItem
extends TrinketItem {
    public int armorValue;
    public float minSlowModifier;
    public int msToDepleteStamina;
    public float staminaUsageOnBlock;
    public int damageTakenPercent;
    public float angleCoverage;
    public int knockback = 100;
    public boolean isPerfectBlocker = false;
    private final ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public ArrayList<String> trinketStringIDs = new ArrayList();

    public ShieldTrinketItem(Item.Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost, OneOfLootItems lootTableCategory) {
        super(rarity, enchantCost, lootTableCategory);
        this.armorValue = armorValue;
        this.minSlowModifier = minSlowModifier;
        this.msToDepleteStamina = msToDepleteStamina;
        this.staminaUsageOnBlock = staminaUsageOnBlock;
        this.damageTakenPercent = GameMath.limit(damageTakenPercent, 0, 100);
        this.angleCoverage = angleCoverage;
    }

    public ShieldTrinketItem addCombinedTrinkets(String ... trinketStringIDs) {
        this.trinketStringIDs.addAll(Arrays.asList(trinketStringIDs));
        return this;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "shieldtip"));
        tooltips.add(Localization.translate("itemtooltip", "shieldmodifier", "percent", 100 - this.damageTakenPercent + "%"));
        tooltips.add(this.getExtraShieldTooltips(item, perspective, blackboard));
        tooltips.add(Localization.translate("itemtooltip", "staminausertip"));
        return tooltips;
    }

    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        return new ListGameTooltips();
    }

    @Override
    public boolean holdsItem(InventoryItem item, PlayerMob player) {
        return this.holdTexture != null && player != null && player.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE);
    }

    @Override
    public boolean holdItemInFrontOfArms(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return true;
    }

    public int getShieldArmorValue(InventoryItem item, Mob mob) {
        return this.armorValue;
    }

    public float getShieldMinSlowModifier(InventoryItem item, Mob mob) {
        return this.minSlowModifier;
    }

    public int getShieldMSToDepleteStamina(InventoryItem item, Mob mob) {
        return this.msToDepleteStamina;
    }

    public float getShieldStaminaUsageOnBlock(InventoryItem item, Mob mob) {
        return this.staminaUsageOnBlock;
    }

    public float getShieldFinalDamageMultiplier(InventoryItem item, Mob mob) {
        return (float)this.damageTakenPercent / 100.0f;
    }

    public float getShieldAngleCoverage(InventoryItem item, Mob mob) {
        return this.angleCoverage;
    }

    public void onShieldHit(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        Mob attackOwner;
        float knockbackModifier;
        if (this.canPerfectBlock(mob)) {
            this.onPerfectBlock(mob);
        }
        if (mob.isClient()) {
            PlayerMob me = mob.getLevel().getClient().getPlayer();
            if (mob == me) {
                this.playHitSound(item, mob, hitEvent);
            }
        } else if (hitEvent.attacker instanceof Mob && (knockbackModifier = (attackOwner = (Mob)hitEvent.attacker).getKnockbackModifier()) != 0.0f) {
            attackOwner.knockback(attackOwner.x - mob.x, attackOwner.y - mob.y, (float)this.getKnockback(mob) / knockbackModifier);
            attackOwner.sendMovementPacket(false);
        }
    }

    public int getKnockback(Mob mob) {
        return this.knockback;
    }

    public void playHitSound(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(mob).volume(0.8f));
    }

    public String getShieldTrinketBuffStringID() {
        return "shieldtrinket";
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        String shieldTrinketBuffStringID = this.getShieldTrinketBuffStringID();
        TrinketBuff[] out = shieldTrinketBuffStringID == null ? new TrinketBuff[]{} : new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff(shieldTrinketBuffStringID)};
        for (TrinketItem trinketItem : (TrinketItem[])this.streamCombinedTrinkets().toArray(TrinketItem[]::new)) {
            out = GameUtils.concat(out, trinketItem.getBuffs(item));
        }
        return out;
    }

    @Override
    public boolean disabledBy(InventoryItem item) {
        if (super.disabledBy(item)) {
            return true;
        }
        return this.streamCombinedTrinkets().anyMatch(i -> i.disabledBy(item));
    }

    @Override
    public boolean disables(InventoryItem item) {
        if (super.disables(item)) {
            return true;
        }
        if (this.trinketStringIDs.stream().anyMatch(s -> s.equals(item.item.getStringID()))) {
            return true;
        }
        return this.streamCombinedTrinkets().anyMatch(i -> i.disables(item));
    }

    public Stream<TrinketItem> streamCombinedTrinkets() {
        return this.trinketStringIDs.stream().map(s -> (TrinketItem)ItemRegistry.getItem(s));
    }

    public boolean canPerfectBlock(Mob mob) {
        boolean hasPerfectBlockTrinket = false;
        if (!this.isPerfectBlocker) {
            hasPerfectBlockTrinket = mob.buffManager.getBuffs().values().stream().anyMatch(b -> b.buff instanceof PerfectBlockTrinketBuff);
        }
        return (this.isPerfectBlocker || hasPerfectBlockTrinket) && mob.buffManager.hasBuff(BuffRegistry.PERFECT_BLOCK);
    }

    public void onPerfectBlock(Mob mob) {
        if (mob.isClient()) {
            GameRandom random = GameRandom.globalRandom;
            for (int i = 0; i < 20; ++i) {
                mob.getLevel().entityManager.addParticle(mob.x, mob.y, this.typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(0, 0, 22)).sizeFades(22, 44).movesFriction(random.getIntBetween(-100, 100), random.getIntBetween(-100, 100), 0.8f).lifeTime(250);
            }
            SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(mob).pitch(2.5f).volume(1.5f));
        }
    }
}

