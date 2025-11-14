/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.misc;

import java.awt.Shape;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.trees.HusbandryImpregnateWandererAI;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public abstract class ProcessMobHandler<C extends Mob, T extends Mob> {
    public final C mob;
    public T target;
    public GameObjectReservable reservable;

    public ProcessMobHandler(C mob, T target, GameObjectReservable reservable) {
        this.mob = mob;
        this.target = target;
        this.reservable = reservable;
    }

    public void tickReserve() {
        if (this.reservable != null) {
            this.reservable.reserve((Entity)this.mob);
        }
    }

    public HusbandryImpregnateWandererAI.MoveToTarget getMoveToTile() {
        double distance = GameMath.diagonalMoveDistance(((Entity)this.mob).getX(), ((Entity)this.mob).getY(), ((Entity)this.target).getX(), ((Entity)this.target).getY());
        if (distance <= 160.0 && !((Entity)this.mob).getLevel().collides((Shape)new MovedRectangle((Mob)this.mob, ((Entity)this.target).getX(), ((Entity)this.target).getY()), ((Mob)this.mob).modifyChasingCollisionFilter(((Mob)this.mob).getLevelCollisionFilter(), (Mob)this.target))) {
            return new HusbandryImpregnateWandererAI.MoveToTarget(new MobMovementRelative((Mob)this.target, 0.0f, 0.0f));
        }
        return new HusbandryImpregnateWandererAI.MoveToTarget(((Entity)this.target).getTileX(), ((Entity)this.target).getTileY());
    }

    public abstract boolean isValid();

    public abstract void completeProgress();

    public abstract int getTimeItTakesInMilliseconds();
}

