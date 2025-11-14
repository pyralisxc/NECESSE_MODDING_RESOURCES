/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class MouseBeamAttackHandler
extends MouseAngleAttackHandler {
    public float manaCostPerUpdate = 0.0f;
    public float lifeCostPerUpdate = 0.0f;
    protected float lifeCostBuffer;
    public int seed;
    public MouseBeamLevelEvent event;

    public MouseBeamAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int updateInterval, int attackSeed, MouseBeamLevelEvent event) {
        super(attackerMob, slot, updateInterval, 10000.0f, 0, 0);
        this.seed = attackSeed;
        this.event = event;
        this.currentAngle = event.currentAngle;
        this.targetAngle = event.targetAngle;
    }

    public MouseBeamAttackHandler setManaCostPerSecond(float manaCostPerSecond) {
        this.manaCostPerUpdate = manaCostPerSecond / (1000.0f / (float)this.updateInterval);
        return this;
    }

    public MouseBeamAttackHandler setLifeCostPerSecond(float lifeCostPerSecond) {
        this.lifeCostPerUpdate = lifeCostPerSecond / (1000.0f / (float)this.updateInterval);
        return this;
    }

    public boolean canAIStillHitTarget(Mob target) {
        return ChaserAINode.hasLineOfSightToTarget(this.attackerMob, target, 2.0f) && ChaserAINode.isTargetHitboxWithinRange(this.attackerMob, this.attackerMob.x, this.attackerMob.y, target, this.event.getDistance());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer) {
            if (this.event.isOver()) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
            float lifeCostPerSecond = this.lifeCostPerUpdate * (1000.0f / (float)this.updateInterval);
            if (lifeCostPerSecond > 0.0f && (float)this.attackerMob.getHealth() < lifeCostPerSecond * 3.0f) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
            if (this.lastItemAttackerTarget != null && !this.canAIStillHitTarget(this.lastItemAttackerTarget)) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }
        if (this.event.isServer() && this.event.targetAngle != this.targetAngle) {
            this.event.setTargetAngleAction.runAndSend(this.targetAngle, false);
        } else {
            this.event.targetAngle = this.targetAngle;
        }
        Point2D.Float dir = GameMath.getAngleDir(this.event.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        InventoryItem attackItem = this.item.copy();
        attackItem.getGndData().setBoolean("charging", true);
        this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
        if (this.manaCostPerUpdate > 0.0f) {
            this.attackerMob.useMana(this.manaCostPerUpdate, this.attackerMob.isPlayer && ((PlayerMob)this.attackerMob).isServerClient() ? ((PlayerMob)this.attackerMob).getServerClient() : null);
        }
        if (this.lifeCostPerUpdate > 0.0f) {
            this.lifeCostBuffer += this.lifeCostPerUpdate;
            if (this.lifeCostBuffer >= 1.0f) {
                int lifeCostInt = (int)this.lifeCostBuffer;
                this.lifeCostBuffer -= (float)lifeCostInt;
                this.attackerMob.useLife(lifeCostInt, this.attackerMob.isPlayer && ((PlayerMob)this.attackerMob).isServerClient() ? ((PlayerMob)this.attackerMob).getServerClient() : null, this.item.getItemLocalization());
            }
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
        this.event.over();
    }

    @Override
    public void drawControllerAimPos(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
    }
}

