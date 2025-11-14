/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.util.HashSet;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;

public class MigrateToApiaryAINode<T extends QueenBeeMob>
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
        if (this.movingToApiary && ((QueenBeeMob)mob).migrationApiary != null && ((QueenBeeMob)mob).shouldMigrate()) {
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                return AINodeResult.RUNNING;
            }
            beeHiveEntity = ((Entity)mob).getLevel().entityManager.getObjectEntity(((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y, AbstractBeeHiveObjectEntity.class);
            if (beeHiveEntity != null && TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), ((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y, ((Entity)mob).getTileX(), ((Entity)mob).getTileY())) {
                if (beeHiveEntity.canTakeMigratingQueen()) {
                    HashSet<Integer> removedUniqueIDs = new HashSet<Integer>();
                    for (int uniqueID2 : ((QueenBeeMob)mob).honeyBeeUniqueIDs) {
                        Mob foundMob = ((Entity)mob).getLevel().entityManager.mobs.get(uniqueID2, false);
                        if (foundMob instanceof HoneyBeeMob) {
                            HoneyBeeMob honeyBeeMob = (HoneyBeeMob)foundMob;
                            honeyBeeMob.setApiaryHome(((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y);
                            honeyBeeMob.followingQueen.uniqueID = -1;
                            continue;
                        }
                        removedUniqueIDs.add(uniqueID2);
                    }
                    removedUniqueIDs.forEach(uniqueID -> mob.honeyBeeUniqueIDs.remove(uniqueID));
                    beeHiveEntity.migrateQueen((QueenBeeMob)mob);
                    ((Mob)mob).remove();
                } else {
                    ((QueenBeeMob)mob).clearMigrationApiary();
                }
            } else {
                this.movingToApiary = false;
            }
        }
        if (this.findPathHomeCooldown <= ((Entity)mob).getWorldEntity().getTime()) {
            this.findPathHomeCooldown = ((Entity)mob).getWorldEntity().getTime() + (long)(GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
            if (((QueenBeeMob)mob).migrationApiary != null && ((QueenBeeMob)mob).shouldMigrate()) {
                beeHiveEntity = ((Entity)mob).getLevel().entityManager.getObjectEntity(((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y, AbstractBeeHiveObjectEntity.class);
                if (beeHiveEntity != null && ((Mob)mob).estimateCanMoveTo(((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y, true)) {
                    return this.moveToTileTask(((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), ((QueenBeeMob)mob).migrationApiary.x, ((QueenBeeMob)mob).migrationApiary.y), path -> {
                        if (path.result.foundTarget) {
                            this.movingToApiary = true;
                            path.move(null);
                            return AINodeResult.RUNNING;
                        }
                        return AINodeResult.FAILURE;
                    });
                }
                ((QueenBeeMob)mob).clearMigrationApiary();
            }
        }
        return AINodeResult.FAILURE;
    }
}

