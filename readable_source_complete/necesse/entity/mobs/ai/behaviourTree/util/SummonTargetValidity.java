/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;

public class SummonTargetValidity<T extends Mob>
extends TargetValidity<T> {
    public String baseKey = "mobBase";

    @Override
    public boolean isValidTarget(AINode<T> node, T mob, Mob target, boolean isNewTarget) {
        if (!super.isValidTarget(node, mob, target, isNewTarget)) {
            return false;
        }
        Point base = node.getBlackboard().getObjectNotNull(Point.class, this.baseKey, new Point(((Entity)mob).getX(), ((Entity)mob).getY()));
        if (target.getLevel().getLightLevel(target).getLevel() <= 0.0f && !target.isBoss() && target.getDistance(base.x, base.y) >= 192.0f) {
            return false;
        }
        if (!target.isHostile && target != node.getBlackboard().getObject(Mob.class, "focusTarget")) {
            return false;
        }
        PathDoorOption doorOption = ((Entity)mob).getLevel().regionManager.BASIC_DOOR_OPTIONS;
        return doorOption.canMoveToTile(GameMath.getTileCoordinate(base.x), GameMath.getTileCoordinate(base.y), target.getTileX(), target.getTileY(), false);
    }
}

