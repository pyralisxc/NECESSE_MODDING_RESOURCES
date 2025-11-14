/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CircleEndNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class ChargeEndNightSwarmBatStage
extends NightSwarmBatStage {
    public Mob target;
    public float midX;
    public float midY;
    public int range;
    public long endTime;
    public Point2D.Float hitDir;

    public ChargeEndNightSwarmBatStage(Mob target, float midX, float midY, int range, long endTime) {
        super(false);
        this.target = target;
        this.midX = midX;
        this.midY = midY;
        this.range = range;
        this.endTime = endTime;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
        mob.setMovement(new MobMovementRelative(this.target, 0.0f, 0.0f));
        mob.disableShareCooldown.runAndSend();
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return this.hitDir != null || this.target == null || this.target.removed() || !this.target.isVisible() || this.endTime <= mob.getWorldEntity().getTime();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
        mob.setMovement(null);
        if (this.hitDir != null) {
            mob.stages.add(0, new CircleEndNightSwarmBatStage(this.midX, this.midY, mob.x - this.hitDir.x * 100.0f, mob.y - this.hitDir.y * 100.0f, this.range, this.endTime, this.endTime));
        } else {
            mob.stages.add(0, new CircleEndNightSwarmBatStage(this.midX, this.midY, this.midX, this.midY, this.range, this.endTime, this.endTime));
        }
    }

    @Override
    public void onCollisionHit(NightSwarmBatMob mob, Mob target) {
        super.onCollisionHit(mob, target);
        this.hitDir = GameMath.normalize(mob.x - target.x, mob.y - target.y);
    }

    @Override
    public void onWasHit(NightSwarmBatMob mob, MobWasHitEvent event) {
        super.onWasHit(mob, event);
        Mob attacker = event.attacker.getAttackOwner();
        this.hitDir = attacker != null ? GameMath.normalize(mob.x - attacker.x, mob.y - attacker.y) : (event.knockbackX != 0.0f || event.knockbackY != 0.0f ? GameMath.normalize(event.knockbackX, event.knockbackY) : GameMath.normalize(mob.x - this.midX, mob.y - this.midY));
        float multiplier = mob.getSpeed() * 0.8f;
        mob.dx = this.hitDir.x * multiplier;
        mob.dy = this.hitDir.y * multiplier;
        mob.sendMovementPacket(false);
    }
}

