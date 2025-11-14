/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.attackHandler.NecroticGreatswordAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.HexedBladeGreatswordWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

public class HexedBladeGreatswordAttackHandler
extends NecroticGreatswordAttackHandler {
    public HexedBladeGreatswordAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
    }

    @Override
    protected Projectile getWaveProjectile(GameDamage damage, int range, float speed, Point2D.Float dir) {
        return new HexedBladeGreatswordWaveProjectile(this.attackerMob.getLevel(), this.attackerMob, this.attackerMob.getX(), this.attackerMob.getY(), (int)(this.attackerMob.x + dir.x * 100.0f), (int)(this.attackerMob.y + dir.y * 100.0f), damage, speed, range, this.currentChargeLevel);
    }
}

