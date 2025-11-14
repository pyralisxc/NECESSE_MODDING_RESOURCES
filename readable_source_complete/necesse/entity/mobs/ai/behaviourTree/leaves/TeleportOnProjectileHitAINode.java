/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.projectile.Projectile;

public abstract class TeleportOnProjectileHitAINode<T extends Mob>
extends AINode<T> {
    protected boolean isInvulnerable;
    public int radius;
    public int cooldown;
    public long next;

    public TeleportOnProjectileHitAINode(int cooldown, int radius) {
        this.cooldown = cooldown;
        this.radius = radius;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onGlobalTick(e -> {
            this.isInvulnerable = false;
        });
        blackboard.onBeforeHit(e -> {
            if (mob.isClient()) {
                return;
            }
            if (this.isInvulnerable || e.event.attacker instanceof Projectile && this.next <= mob.getWorldEntity().getTime() && this.findNewPosition(mob)) {
                this.isInvulnerable = true;
                e.event.prevent();
                e.event.showDamageTip = false;
                e.event.playHitSound = false;
                this.next = mob.getWorldEntity().getTime() + (long)this.cooldown;
                blackboard.submitEvent("resetPathTime", new AIEvent());
            }
        });
    }

    public boolean findNewPosition(T mob) {
        int tileX = ((Entity)mob).getTileX();
        int tileY = ((Entity)mob).getTileY();
        Point moveOffset = ((Mob)mob).getPathMoveOffset();
        ArrayList<Point> possiblePoints = new ArrayList<Point>();
        for (int x = tileX - this.radius; x <= tileX + this.radius; ++x) {
            for (int y = tileY - this.radius; y <= tileY + this.radius; ++y) {
                int mobX = x * 32 + moveOffset.x;
                int mobY = y * 32 + moveOffset.y;
                if (((Mob)mob).collidesWith(((Entity)mob).getLevel(), mobX, mobY)) continue;
                possiblePoints.add(new Point(mobX, mobY));
            }
        }
        while (!possiblePoints.isEmpty()) {
            int index = GameRandom.globalRandom.nextInt(possiblePoints.size());
            Point point = (Point)possiblePoints.get(index);
            if (!this.teleport(mob, point.x, point.y)) continue;
            return true;
        }
        return false;
    }

    public abstract boolean teleport(T var1, int var2, int var3);

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }
}

