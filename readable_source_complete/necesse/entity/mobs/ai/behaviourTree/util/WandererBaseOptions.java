/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;

public interface WandererBaseOptions<T extends Mob> {
    public Point getBaseTile(T var1);

    default public int getBaseRadius(T mob, WandererAINode<T> node) {
        return node.searchRadius;
    }

    default public boolean forceFindAroundBase(T mob) {
        return false;
    }

    default public boolean isBaseHouse(T mob, Point base) {
        return false;
    }

    default public boolean isBaseRoom(T mob, Point base) {
        return false;
    }
}

