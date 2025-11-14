/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.SwampDwellerStaffFlowerProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class SwampDwellerStaffProjectileToolItem
extends MagicProjectileToolItem {
    public SwampDwellerStaffProjectileToolItem() {
        super(1600, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(89.0f).setUpgradedValue(1.0f, 126.00004f);
        this.knockback.setBaseValue(80);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.velocity.setBaseValue(100);
        this.attackRange.setBaseValue(200);
        this.manaCost.setBaseValue(2.25f).setUpgradedValue(1.0f, 2.25f);
        this.resilienceGain.setBaseValue(2.0f);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "swampdwellerstafftip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        SwampDwellerStaffFlowerProjectile projectile = new SwampDwellerStaffFlowerProjectile(attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
        this.consumeMana(attackerMob, item);
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.magicbolt3).volume(0.2f);
    }
}

