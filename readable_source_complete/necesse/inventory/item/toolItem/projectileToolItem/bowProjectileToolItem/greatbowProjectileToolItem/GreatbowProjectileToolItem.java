/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatbowAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class GreatbowProjectileToolItem
extends BowProjectileToolItem {
    public Color particleColor = new Color(255, 219, 36);

    public GreatbowProjectileToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.knockback.setBaseValue(100);
        this.tierOneEssencesUpgradeRequirement = "bioessence";
        this.tierTwoEssencesUpgradeRequirement = "slimeessence";
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraBowTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "greatbowtip"));
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (!level.isClient()) {
            return;
        }
        if (item.getGndData().getBoolean("charging")) {
            return;
        }
        if (!item.getGndData().getBoolean("charged")) {
            return;
        }
        float chargePerc = item.getGndData().getFloat("chargePercent");
        SoundSettings sound = this.getGreatbowShootSound(chargePerc);
        sound.setPitchVarianceIfNotSet(0.04f);
        SoundManager.playSound(sound, attackerMob);
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        float resilienceGainMod;
        float knockbackMod;
        float damageMod;
        float rangeMod;
        float velocityMod;
        float percentCharge = item.getGndData().getFloat("chargePercent");
        if ((percentCharge = GameMath.limit(percentCharge, 0.0f, 1.0f)) >= 1.0f) {
            velocityMod = 1.0f;
            rangeMod = 1.0f;
            damageMod = 1.0f;
            knockbackMod = 1.0f;
            resilienceGainMod = 1.0f;
        } else {
            velocityMod = GameMath.lerp(percentCharge, 0.1f, 0.4f);
            rangeMod = GameMath.lerp(percentCharge, 0.05f, 0.4f);
            damageMod = GameMath.lerp(percentCharge, 0.05f, 0.4f);
            knockbackMod = GameMath.lerp(percentCharge, 0.05f, 0.2f);
            resilienceGainMod = GameMath.lerp(percentCharge, 0.0f, 0.5f);
        }
        GameDamage damage = arrow.modDamage(this.getAttackDamage(item)).modDamage(damageMod);
        float velocity = arrow.modVelocity(this.getProjectileVelocity(item, attackerMob)) * velocityMod;
        float range = (float)arrow.modRange(this.getAttackRange(item)) * rangeMod;
        float knockback = (float)arrow.modKnockback(this.getKnockback(item, attackerMob)) * knockbackMod;
        float resilienceGain = this.getResilienceGain(item) * resilienceGainMod;
        return this.getProjectile(level, x, y, attackerMob, item, seed, arrow, consumeAmmo, velocity, (int)range, damage, (int)knockback, resilienceGain, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        attackerMob.startAttackHandler(new GreatbowAttackHandler(attackerMob, slot, item, this, animTime, this.particleColor, seed));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean shouldRunOnAttackedBuffEvent(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return false;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "greatbow");
    }

    private SoundSettings getGreatbowShootSound(float chargePercent) {
        if (chargePercent >= 1.0f) {
            return this.getGreatbowShootSoundStrong();
        }
        return this.getGreatbowShootSoundWeak();
    }

    protected SoundSettings getGreatbowShootSoundWeak() {
        return new SoundSettings(GameResources.regularGreatBowWeak).volume(0.8f);
    }

    protected SoundSettings getGreatbowShootSoundStrong() {
        return new SoundSettings(GameResources.regularGreatBowStrong).volume(0.8f);
    }
}

