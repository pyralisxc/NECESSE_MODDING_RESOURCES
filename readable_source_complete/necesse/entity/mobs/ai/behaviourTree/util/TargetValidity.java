/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;

public class TargetValidity<T extends Mob> {
    public boolean isValidTarget(AINode<T> node, T mob, Mob target, boolean isNewTarget) {
        return target != null && !target.removed() && target.isVisible() && ((Entity)mob).isSamePlace(target) && target.canBeTargeted((Mob)mob, null);
    }
}

