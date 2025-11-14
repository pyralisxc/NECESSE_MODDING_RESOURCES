/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChargeShowerLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.ChargeShowerProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;

public class ChargeShowerAttackHandler
extends MouseAngleAttackHandler {
    protected int seed;
    protected ChargeShowerLevelEvent event;
    protected int attackDistance;

    public ChargeShowerAttackHandler(ItemAttackerMob itemAttacker, ItemAttackSlot slot, int updateInterval, float speed, int startTargetX, int startTargetY, int attackSeed, ChargeShowerLevelEvent event, int attackDistance) {
        super(itemAttacker, slot, updateInterval, speed, startTargetX, startTargetY);
        this.seed = attackSeed;
        this.event = event;
        this.attackDistance = attackDistance;
    }

    public boolean canAIStillHitTarget(Mob target) {
        return ChaserAINode.isTargetHitboxWithinRange(this.attackerMob, this.attackerMob.x, this.attackerMob.y, target, this.attackDistance);
    }

    @Override
    public void onUpdate() {
        float progress;
        super.onUpdate();
        if (!this.attackerMob.isPlayer) {
            if (this.event.isOver()) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
            if (this.lastItemAttackerTarget != null && !this.canAIStillHitTarget(this.lastItemAttackerTarget)) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }
        if (!this.attackerMob.isPlayer && (progress = this.event.getEventProgress()) >= 1.0f) {
            this.attackerMob.endAttackHandler(true);
        }
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        InventoryItem attackItem = this.item.copy();
        attackItem.getGndData().setBoolean("charging", true);
        this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
        this.event.over();
        if (bySelf) {
            float progress = this.event.getEventProgress();
            if (progress > 0.5f) {
                if (!this.attackerMob.isPlayer) {
                    this.attackerMob.startGenericCooldown("chargeShower", 250L);
                }
                float power = GameMath.clamp(progress, 0.5f, 1.0f);
                Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                ToolItem toolItem = (ToolItem)this.item.item;
                int attackRange = (int)((float)toolItem.getAttackRange(this.item) * GameMath.lerp(power, 0.2f, 1.0f));
                GameDamage damage = toolItem.getAttackDamage(this.item).modFinalMultiplier(GameMath.lerp(power, 0.25f, 1.0f));
                int knockback = toolItem.getKnockback(this.item, this.attackerMob);
                ChargeShowerProjectile projectile = new ChargeShowerProjectile(this.attackerMob.getLevel(), this.attackerMob.x, this.attackerMob.y, this.attackerMob.x + dir.x * 1000.0f, this.attackerMob.y + dir.y * 1000.0f, attackRange, damage, knockback, this.attackerMob);
                projectile.resetUniqueID(new GameRandom(this.seed).nextSeeded(57));
                this.attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 40);
            } else if (!this.attackerMob.isServer()) {
                SoundManager.playSound(GameResources.fadedeath1, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(0.3f).pitch(1.8f));
            }
        }
    }
}

