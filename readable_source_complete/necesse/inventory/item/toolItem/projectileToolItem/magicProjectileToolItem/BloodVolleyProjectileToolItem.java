/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
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

public class BloodVolleyProjectileToolItem
extends MagicProjectileToolItem {
    public BloodVolleyProjectileToolItem() {
        super(650, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 56.00002f);
        this.velocity.setBaseValue(120);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(1000);
        this.manaCost.setBaseValue(3.0f).setUpgradedValue(1.0f, 3.0f);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bloodvolleytip"));
        return tooltips;
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.magicbolt1).volume(0.5f);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.bloodVolley).volume(0.2f);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        for (int i = -1; i <= 1; ++i) {
            Projectile projectile = ProjectileRegistry.getProjectile("bloodbolt", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.resetUniqueID(random);
            attackerMob.addAndSendAttackerProjectile(projectile, 20, 10 * i);
        }
        this.consumeMana(attackerMob, item);
        return item;
    }
}

