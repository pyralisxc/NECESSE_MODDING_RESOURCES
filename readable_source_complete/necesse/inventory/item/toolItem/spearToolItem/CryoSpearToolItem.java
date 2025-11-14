/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;
import necesse.level.maps.Level;

public class CryoSpearToolItem
extends SpearToolItem {
    public CryoSpearToolItem() {
        super(1500, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(35.0f).setUpgradedValue(1.0f, 58.33335f);
        this.attackRange.setBaseValue(140);
        this.knockback.setBaseValue(50);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cryospeartip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        InventoryItem out = super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        if (animAttack == 0) {
            Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y + (float)attackHeight);
            Mob mount = attackerMob.getMount();
            if (mount != null) {
                attackHeight -= mount.getRiderDrawYOffset();
            }
            CryoSpearShardProjectile projectile = new CryoSpearShardProjectile(attackerMob.x + dir.x, attackerMob.y + dir.y, attackerMob.x + dir.x * 1000.0f, attackerMob.y + dir.y * 1000.0f, 150.0f, 200, this.getAttackDamage(item), this.getKnockback(item, attackerMob), attackerMob, attackHeight);
            projectile.setLevel(level);
            projectile.resetUniqueID(new GameRandom(seed));
            projectile.moveDist(this.getAttackRange(item) - 35);
            projectile.traveledDistance = 0.0f;
            attackerMob.addAndSendAttackerProjectile(projectile);
        }
        return out;
    }
}

