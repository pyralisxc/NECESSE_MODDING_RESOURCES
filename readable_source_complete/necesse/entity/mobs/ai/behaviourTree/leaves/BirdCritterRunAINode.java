/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.stream.Collectors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.CritterRunAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.BirdCritterAI;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.mobs.mobMovement.MobMovementConstant;

public class BirdCritterRunAINode<T extends BirdMob>
extends AINode<T> {
    public int flyAwayFromPlayers;
    protected Point2D.Float runDir;
    public int flyAwayIn;

    public BirdCritterRunAINode(int flyAwayFromPlayers) {
        this.flyAwayFromPlayers = flyAwayFromPlayers;
        this.flyAwayIn = GameRandom.globalRandom.getIntBetween(40, 2400);
    }

    public BirdCritterRunAINode() {
        this(150);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Iterator<AIWasHitEvent> iterator = blackboard.getLastHits().iterator();
        if (iterator.hasNext()) {
            float deltaY;
            float deltaX;
            Mob attackOwner;
            AIWasHitEvent e = iterator.next();
            Mob mob2 = attackOwner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
            if (attackOwner != null) {
                deltaX = ((Entity)mob).getX() - attackOwner.getX();
                deltaY = ((Entity)mob).getY() - attackOwner.getY();
            } else {
                deltaX = (float)GameRandom.globalRandom.nextGaussian();
                deltaY = (float)GameRandom.globalRandom.nextGaussian();
            }
            this.flyAway(deltaX, deltaY);
        }
        if (((CritterMob)mob).isRunning()) {
            if (blackboard.mover.hasMovingNode()) {
                blackboard.mover.stopMoving((Mob)mob);
            }
            if (this.runDir != null) {
                ((Mob)mob).setMovement(new MobMovementConstant(this.runDir.x, this.runDir.y));
            }
            if (EscapeAINode.canEscape(mob, 3)) {
                ((Mob)mob).remove();
            }
        } else {
            CritterRunAINode.TempDistance closest;
            float deltaY;
            --this.flyAwayIn;
            if (this.flyAwayIn <= 0) {
                float deltaX;
                ServerClient randomClient = (ServerClient)GameRandom.globalRandom.getOneOf(GameUtils.streamServerClients(((Entity)mob).getLevel()).collect(Collectors.toList()));
                if (randomClient != null) {
                    int posXOffset = GameRandom.globalRandom.getIntOffset(randomClient.playerMob.getX(), 200);
                    int posYOffset = GameRandom.globalRandom.getIntOffset(randomClient.playerMob.getY(), 200);
                    deltaX = (float)posXOffset - ((BirdMob)mob).x;
                    deltaY = (float)posYOffset - ((BirdMob)mob).y;
                } else {
                    deltaX = (float)GameRandom.globalRandom.nextGaussian();
                    deltaY = (float)GameRandom.globalRandom.nextGaussian();
                }
                this.flyAway(deltaX, deltaY);
            } else if (this.flyAwayFromPlayers > 0 && (closest = (CritterRunAINode.TempDistance)((Entity)mob).getLevel().entityManager.players.getInRegionByTileRange(((Entity)mob).getTileX(), ((Entity)mob).getTileY(), this.flyAwayFromPlayers / 32 + 2).stream().map(p -> new CritterRunAINode.TempDistance((PlayerMob)p, (Mob)mob)).min((p1, p2) -> Float.compare(p1.distance, p2.distance)).orElse(null)) != null && closest.distance <= (float)this.flyAwayFromPlayers) {
                float deltaX = ((BirdMob)mob).x - closest.player.x;
                deltaY = ((BirdMob)mob).y - closest.player.y;
                this.flyAway(deltaX, deltaY);
            }
        }
        return AINodeResult.SUCCESS;
    }

    public <T extends BirdMob> void flyAway(float deltaX, float deltaY) {
        this.runDir = GameMath.normalize(deltaX, deltaY);
        if (Math.abs(this.runDir.y) > 0.5f) {
            this.runDir = GameMath.normalize(this.runDir.x == 0.0f ? 1.0f : Math.signum(this.runDir.x), Math.signum(this.runDir.y) * 0.5f);
        }
        ((BirdMob)this.mob()).setRunning(true);
        ((BirdMob)this.mob()).getLevel().entityManager.mobs.streamInRegionsInTileRange(((BirdMob)this.mob()).getX(), ((BirdMob)this.mob()).getY(), 5).filter(m -> m instanceof BirdMob && m.ai.tree instanceof BirdCritterAI).filter(m -> m.getDistance((Mob)this.mob()) < 100.0f).forEach(m -> {
            BirdCritterRunAINode runNode = ((BirdCritterAI)m.ai.tree).runNode;
            if (runNode.runDir == null) {
                runNode.flyAway(deltaX, deltaY);
            }
        });
    }
}

