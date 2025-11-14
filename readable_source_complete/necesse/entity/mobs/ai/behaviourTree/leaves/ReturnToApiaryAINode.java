/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;

public class ReturnToApiaryAINode<T extends HoneyBeeMob>
extends MoveTaskAINode<T> {
    public long findPathHomeCooldown;
    public boolean movingToApiary;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        AbstractBeeHiveObjectEntity beeHiveEntity;
        if (this.movingToApiary && ((HoneyBeeMob)mob).apiaryHome != null && ((HoneyBeeMob)mob).shouldReturnToApiary()) {
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                return AINodeResult.RUNNING;
            }
            beeHiveEntity = ((Entity)mob).getLevel().entityManager.getObjectEntity(((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y, AbstractBeeHiveObjectEntity.class);
            if (beeHiveEntity != null && TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), ((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y, ((Entity)mob).getTileX(), ((Entity)mob).getTileY())) {
                beeHiveEntity.onRoamingBeeReturned((Mob)mob);
                ((Mob)mob).remove();
            } else {
                this.movingToApiary = false;
            }
        }
        if (this.findPathHomeCooldown <= ((Entity)mob).getWorldEntity().getTime()) {
            this.findPathHomeCooldown = ((Entity)mob).getWorldEntity().getTime() + (long)(GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
            if (((HoneyBeeMob)mob).apiaryHome != null && ((HoneyBeeMob)mob).shouldReturnToApiary()) {
                beeHiveEntity = ((Entity)mob).getLevel().entityManager.getObjectEntity(((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y, AbstractBeeHiveObjectEntity.class);
                if (beeHiveEntity != null && ((Mob)mob).estimateCanMoveTo(((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y, true)) {
                    return this.moveToTileTask(((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), ((HoneyBeeMob)mob).apiaryHome.x, ((HoneyBeeMob)mob).apiaryHome.y), path -> {
                        if (path.result.foundTarget) {
                            this.movingToApiary = true;
                            path.move(null);
                            return AINodeResult.RUNNING;
                        }
                        return AINodeResult.FAILURE;
                    });
                }
                ((HoneyBeeMob)mob).clearApiaryHome();
            }
        }
        return AINodeResult.FAILURE;
    }
}

