/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class GenieLampProjectileToolItem
extends MagicProjectileToolItem {
    public GenieLampProjectileToolItem() {
        super(1150, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(150);
        this.attackDamage.setBaseValue(28.0f).setUpgradedValue(1.0f, 56.00002f);
        this.velocity.setBaseValue(150);
        this.attackXOffset = 6;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(300);
        this.knockback.setBaseValue(5);
        this.manaCost.setBaseValue(0.75f).setUpgradedValue(1.0f, 0.75f);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "genielamptip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile("waterspray", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, 20, random.getIntBetween(-10, 10));
        this.consumeMana(attackerMob, item);
        return item;
    }

    @Override
    protected void playAttackSound(Mob source) {
        SoundManager.playSound(new SoundSettings(GameResources.magicbolt1).volume(0.8f).basePitch(1.2f), source);
        super.playAttackSound(source);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.genieLamp).volume(0.07f);
    }
}

