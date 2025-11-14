/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.DryadBowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SmallSpiritLeafProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public class DryadBowProjectileToolItem
extends BowProjectileToolItem {
    private final int dryadHauntedStacksOnHit = 3;

    public DryadBowProjectileToolItem() {
        super(1550, BowWeaponsLootTable.bowWeapons);
        this.attackAnimTime.setBaseValue(400);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(48.0f).setUpgradedValue(1.0f, 89.83336f);
        this.velocity.setBaseValue(225);
        this.attackRange.setBaseValue(750);
        this.attackXOffset = 12;
        this.attackYOffset = 33;
        this.resilienceGain.setBaseValue(1.0f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dryadbowtip"), 400);
        tooltips.add(new SpacerGameTooltip(5));
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "dryadhauntweapontip", "value", (Object)3), new Color(30, 177, 143), 400));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        int checkForMobsRange = 512;
        if (level.isServer()) {
            GameUtils.streamTargetsRange(attackerMob, attackerMob.getX(), attackerMob.getY(), checkForMobsRange).filter(m -> m.buffManager.hasBuff(BuffRegistry.Debuffs.DRYAD_HAUNTED)).filter(m -> m.getDistance(attackerMob) <= (float)checkForMobsRange).forEach(m -> {
                SmallSpiritLeafProjectile p = new SmallSpiritLeafProjectile(attackerMob, (Mob)m, attackerMob.x, attackerMob.y, m.x, m.y, 200.0f, 640, new GameDamage(this.getAttackDamage((InventoryItem)item).damage / 2.0f), this.getKnockback(item, attackerMob) / 2);
                p.setLevel(level);
                p.moveDist(this.moveDist);
                level.entityManager.projectiles.add(p);
            });
        }
        return item;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new DryadBowProjectile(owner, owner.x, owner.y, x, y, velocity, range, damage, 3, knockback);
    }
}

