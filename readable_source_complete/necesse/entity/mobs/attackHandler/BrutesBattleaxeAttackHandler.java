/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.theRunebound.RuneboundBruteMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;

public class BrutesBattleaxeAttackHandler
extends GreatswordAttackHandler {
    protected int seed;
    protected int startX;
    protected int startY;
    protected Level level;
    protected Point2D.Float lockedTargetPos;

    public BrutesBattleaxeAttackHandler(ItemAttackerMob attackerMob, Level level, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, boolean shouldLockOnTargetPos, ItemAttackSlot slot, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
        this.level = level;
        this.seed = seed;
        if (shouldLockOnTargetPos) {
            this.lockedTargetPos = GameMath.getAngleDir(this.currentAngle);
            if (attackerMob instanceof RuneboundBruteMob) {
                ((RuneboundBruteMob)attackerMob).shouldResetFacingPos = true;
            }
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.updateCurrentChargeLevel();
        if (this.currentChargeLevel >= 0 && !this.endedByInteract) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
            InventoryItem attackItem = this.item.copy();
            GreatswordChargeLevel currentLevel = this.chargeLevels[this.currentChargeLevel];
            attackItem.getGndData().setInt("cooldown", this.toolItem.getAttackAnimTime(attackItem, this.attackerMob) + 100);
            attackItem.getGndData().setBoolean("charged", true);
            currentLevel.setupAttackItem(this, attackItem);
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem)((Object)attackItem.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                attackX = attackPos.x;
                attackY = attackPos.y;
            }
            this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
                b.onItemAttacked(attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, attackMap);
            }
            float distance = 0.0f;
            switch (this.currentChargeLevel) {
                case 2: {
                    distance = 320.0f;
                    break;
                }
                case 1: {
                    distance = 160.0f;
                }
            }
            if (this.level.isServer() && this.attackerMob != null) {
                Point2D.Float targetPoint = GameMath.getAngleDir(this.currentAngle);
                int animTime = (int)distance * 2;
                if (this.lockedTargetPos != null) {
                    targetPoint = this.lockedTargetPos;
                    animTime = (int)distance * 4;
                }
                MobDashLevelEvent event = new MobDashLevelEvent(this.attackerMob, this.seed, targetPoint.x, targetPoint.y, distance, animTime / 2, this.toolItem.getAttackDamage(attackItem));
                this.level.entityManager.events.add(event);
                if (this.attackerMob.isPlayer) {
                    this.attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, (Mob)this.attackerMob, animTime, null), false);
                }
            }
        } else {
            this.attackerMob.doAndSendStopAttackAttacker(false);
        }
    }
}

