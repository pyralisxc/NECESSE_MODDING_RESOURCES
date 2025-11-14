/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.BeePollinateAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowMobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ReturnToApiaryAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.entity.mobs.friendly.HoneyBeeMob;

public class HoneyBeeAI<T extends HoneyBeeMob>
extends SelectorAINode<T> {
    public final FollowMobAINode<T> followQueenAINode = new FollowMobAINode<T>(){

        @Override
        public Mob getFollowingMob(T mob) {
            return ((HoneyBeeMob)mob).followingQueen.get(((Entity)mob).getLevel());
        }
    };
    public final ReturnToApiaryAINode<T> returnToApiaryAINode;
    public final BeePollinateAINode<T> pollinateAINode;
    public final WandererAINode<T> wandererAINode;

    public HoneyBeeAI(int wanderFrequency) {
        this.followQueenAINode.tileRadius = 1;
        this.followQueenAINode.minChangePositionCooldown = 500;
        this.followQueenAINode.maxChangePositionCooldown = 1000;
        this.addChild(this.followQueenAINode);
        this.returnToApiaryAINode = new ReturnToApiaryAINode();
        this.addChild(this.returnToApiaryAINode);
        this.pollinateAINode = new BeePollinateAINode();
        this.addChild(this.pollinateAINode);
        this.wandererAINode = new WandererAINode<T>(wanderFrequency){

            @Override
            public WandererBaseOptions<T> getBaseOptions() {
                return new WandererBaseOptions<T>(){

                    @Override
                    public Point getBaseTile(T mob) {
                        return ((HoneyBeeMob)mob).apiaryHome;
                    }

                    @Override
                    public int getBaseRadius(T mob, WandererAINode<T> node) {
                        return 15;
                    }
                };
            }
        };
        this.addChild(this.wandererAINode);
    }
}

