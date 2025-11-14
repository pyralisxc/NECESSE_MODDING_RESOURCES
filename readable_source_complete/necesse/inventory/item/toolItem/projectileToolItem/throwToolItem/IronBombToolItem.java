/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.IronBombProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.SettlerIgnoredThrowToolItem;
import necesse.level.maps.Level;

public class IronBombToolItem
extends SettlerIgnoredThrowToolItem {
    public IronBombToolItem() {
        this.stackSize = 100;
        this.attackAnimTime.setBaseValue(500);
        this.attackRange.setBaseValue(300);
        this.attackDamage.setBaseValue(60.0f);
        this.velocity.setBaseValue(100);
        this.rarity = Item.Rarity.COMMON;
        this.resilienceGain.setBaseValue(0.0f);
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "destructivetip"));
        return tooltips;
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        int range = this.getAttackRange(item);
        return new Point((int)(player.x + aimDirX * (float)range), (int)(player.y + aimDirY * (float)range));
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        Point targetPos = this.controlledRangePosition(random, attackerMob, x, y, item, 0, 40);
        int newRange = (int)attackerMob.getDistance(targetPos.x, targetPos.y);
        IronBombProjectile projectile = new IronBombProjectile(attackerMob.x, attackerMob.y, targetPos.x, targetPos.y, this.getThrowingVelocity(item, attackerMob), newRange, this.getAttackDamage(item), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.setLevel(level);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile);
        item.setAmount(item.getAmount() - 1);
        return item;
    }
}

