/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderShooterAINode;

public abstract class StationaryPlayerShooterAI<T extends Mob>
extends TargetFinderShooterAINode<T> {
    public StationaryPlayerShooterAI(int shootDistance, String focusTargetKey) {
        super(shootDistance, focusTargetKey);
    }

    public StationaryPlayerShooterAI(int shootDistance) {
        super(shootDistance);
    }

    @Override
    public Stream<Mob> streamTargets(T mob, int shootDistance) {
        return GameUtils.streamServerClients(((Entity)mob).getLevel()).map(c -> c.playerMob);
    }
}

