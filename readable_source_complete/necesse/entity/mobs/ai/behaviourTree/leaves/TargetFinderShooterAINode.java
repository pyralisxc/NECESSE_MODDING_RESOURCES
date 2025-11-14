/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.level.maps.CollisionFilter;

public abstract class TargetFinderShooterAINode<T extends Mob>
extends AINode<T> {
    public int shootDistance;
    public String focusTargetKey;

    public TargetFinderShooterAINode(int shootDistance, String focusTargetKey) {
        this.shootDistance = shootDistance;
        this.focusTargetKey = focusTargetKey;
    }

    public TargetFinderShooterAINode(int shootDistance) {
        this(shootDistance, "focusTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob focusTarget;
        Mob target;
        if (this.canAttack(mob) && (target = (focusTarget = blackboard.getObject(Mob.class, this.focusTargetKey)) != null && this.canShootTarget(mob, focusTarget) ? focusTarget : (Mob)this.streamTargets(mob, this.shootDistance).filter(m -> this.canShootTarget(mob, (Mob)m)).min(Comparator.comparingDouble(arg_0 -> mob.getDistance(arg_0))).orElse(null)) != null) {
            this.shootTarget(mob, target);
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }

    public boolean canAttack(T mob) {
        return ((Mob)mob).canAttack();
    }

    public abstract Stream<Mob> streamTargets(T var1, int var2);

    public boolean canShootTarget(T mob, Mob target) {
        if (((Mob)mob).getDistance(target) > (float)this.shootDistance) {
            return false;
        }
        CollisionFilter collisionFilter = ((Mob)mob).modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target);
        return !((Entity)mob).getLevel().collides(new Line2D.Float(((Mob)mob).x, ((Mob)mob).y, target.x, target.y), collisionFilter);
    }

    public abstract void shootTarget(T var1, Mob var2);

    public static <T extends Mob> Stream<Mob> streamPlayersAndHumans(T mob, int distance) {
        return mob.getLevel().entityManager.streamAreaMobsAndPlayers(mob.x, mob.y, distance).filter(m -> {
            if (m == null || m == mob || m.removed() || !m.isVisible()) {
                return false;
            }
            int team = m.getTeam();
            if (team == -100) {
                return true;
            }
            if (m.isHuman && team != -1) {
                return true;
            }
            return m.isPlayer;
        }).collect(Collectors.toList()).stream();
    }
}

