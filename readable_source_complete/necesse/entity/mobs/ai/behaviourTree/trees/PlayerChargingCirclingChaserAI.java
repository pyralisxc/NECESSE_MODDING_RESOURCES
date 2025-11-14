/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class PlayerChargingCirclingChaserAI<T extends Mob>
extends SelectorAINode<T> {
    public final EscapeAINode<T> escapeAINode;
    public final TargetFinderAINode<T> targetFinder;
    public final ChargingCirclingChaserAINode<T> chaser;

    public PlayerChargingCirclingChaserAI(final Supplier<Boolean> shouldEscape, int searchDistance, int circlingRange, int nextAngleOffset) {
        this.escapeAINode = new EscapeAINode<T>(){

            @Override
            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                if (((Mob)mob).isHostile && !((Mob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING).booleanValue()) {
                    return true;
                }
                return shouldEscape != null && (Boolean)shouldEscape.get() != false;
            }
        };
        this.addChild(this.escapeAINode);
        SequenceAINode chaserSequence = new SequenceAINode();
        this.targetFinder = new TargetFinderAINode<T>(searchDistance){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
            }
        };
        chaserSequence.addChild(this.targetFinder);
        this.chaser = new ChargingCirclingChaserAINode(circlingRange, nextAngleOffset);
        chaserSequence.addChild(this.chaser);
        this.addChild(chaserSequence);
        this.addChild(new WandererAINode(0));
    }
}

