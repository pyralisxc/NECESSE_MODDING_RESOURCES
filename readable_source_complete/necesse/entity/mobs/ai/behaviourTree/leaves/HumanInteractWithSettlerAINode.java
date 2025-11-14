/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.SettlerInteractReqestAIEvent;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.components.FormExpressionWheel;
import necesse.inventory.item.Item;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HumanInteractWithSettlerAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    protected HumanMob settlerCurrentlyInteractingWith;
    protected boolean isControllingInteraction;
    protected long nextPathFindTime;
    protected int currentInteractionStageTicker;
    protected boolean interactionPositive;
    public int searchInteractionTickCountdown;

    protected void refreshCooldown() {
        this.searchInteractionTickCountdown = 20 * GameRandom.globalRandom.getIntBetween(30, 240);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.refreshCooldown();
        blackboard.onWasHit(e -> {
            if (this.settlerCurrentlyInteractingWith != null) {
                this.settlerCurrentlyInteractingWith.ai.blackboard.submitEvent("humanInteractionEnded", new AIEvent());
            }
            this.settlerCurrentlyInteractingWith = null;
        });
        blackboard.onEvent("humanInteractRequest", e -> {
            if (e instanceof SettlerInteractReqestAIEvent) {
                SettlerInteractReqestAIEvent interactEvent = (SettlerInteractReqestAIEvent)e;
                if (interactEvent.accepted || this.settlerCurrentlyInteractingWith != null) {
                    return;
                }
                this.settlerCurrentlyInteractingWith = interactEvent.from;
                this.isControllingInteraction = false;
                interactEvent.accepted = true;
                this.refreshCooldown();
            }
        });
        blackboard.onEvent("humanInteractionEnded", e -> {
            this.settlerCurrentlyInteractingWith = null;
        });
    }

    @Override
    protected void onInterruptRunning(T mob, Blackboard<T> blackboard) {
        super.onInterruptRunning(mob, blackboard);
        if (this.settlerCurrentlyInteractingWith != null) {
            this.settlerCurrentlyInteractingWith.ai.blackboard.submitEvent("humanInteractionEnded", new AIEvent());
        }
        this.settlerCurrentlyInteractingWith = null;
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        ((HumanMob)mob).canInteractWithOtherSettlers = false;
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        ((HumanMob)mob).canInteractWithOtherSettlers = true;
        if (!(this.settlerCurrentlyInteractingWith == null || !this.settlerCurrentlyInteractingWith.removed() && this.settlerCurrentlyInteractingWith.isSamePlace((Entity)mob) && this.settlerCurrentlyInteractingWith.canInteractWithOtherSettlers)) {
            if (this.settlerCurrentlyInteractingWith != null) {
                this.settlerCurrentlyInteractingWith.ai.blackboard.submitEvent("humanInteractionEnded", new AIEvent());
            }
            this.settlerCurrentlyInteractingWith = null;
        }
        if (this.settlerCurrentlyInteractingWith != null) {
            if (!this.isControllingInteraction) {
                if (blackboard.mover.isMoving()) {
                    blackboard.mover.stopMoving((Mob)mob);
                }
                return AINodeResult.SUCCESS;
            }
            if (((Mob)mob).getDistance(this.settlerCurrentlyInteractingWith) <= 48.0f) {
                Point2D.Float interactionDir = GameMath.normalize(this.settlerCurrentlyInteractingWith.x - ((HumanMob)mob).x, this.settlerCurrentlyInteractingWith.y - ((HumanMob)mob).y);
                ((HumanMob)mob).setFacingDir(interactionDir.x, interactionDir.y);
                if (this.currentInteractionStageTicker == 60) {
                    this.settlerCurrentlyInteractingWith.sendMovementPacket(false);
                }
                if (this.currentInteractionStageTicker >= 60) {
                    this.settlerCurrentlyInteractingWith.setFacingDir(-interactionDir.x, -interactionDir.y);
                }
                if (blackboard.mover.isMoving()) {
                    blackboard.mover.stopMoving((Mob)mob);
                }
                boolean interactionDone = false;
                ++this.currentInteractionStageTicker;
                if (this.currentInteractionStageTicker == 20) {
                    ((Mob)mob).sendMovementPacket(false);
                    List<HumanMob> settlers = this.findRandomSettler(mob, 30, 8);
                    List<String> animals = this.findRandomAnimalStringIDs(mob, 30, 5);
                    List<Item> foods = this.findRandomFoodItem(mob, 8);
                    int totalTickets = settlers.size() + animals.size() + foods.size();
                    if (totalTickets == 0) {
                        animals.add(GameRandom.globalRandom.getOneOf("honeybee", "rabbit", "turtle", "duck"));
                        totalTickets = 1;
                    }
                    int decidedTicket = GameRandom.globalRandom.nextInt(totalTickets);
                    int averageHappiness = GameMath.limit((((HumanMob)mob).getSettlerHappiness() + this.settlerCurrentlyInteractingWith.getSettlerHappiness()) / 2, 0, 100);
                    float averageHappinessFloat = (float)averageHappiness / 100.0f;
                    if (decidedTicket < settlers.size()) {
                        HumanMob target = settlers.get(decidedTicket);
                        ((HumanMob)mob).showOtherSettlerThoughtAbility.runAndSend(target, 6000);
                        this.interactionPositive = GameRandom.globalRandom.getChance(GameMath.lerp(averageHappinessFloat, 0.3f, 0.8f));
                    } else if (decidedTicket < settlers.size() + animals.size()) {
                        String animalStringID = animals.get(decidedTicket - settlers.size());
                        ((HumanMob)mob).showMobThoughtAbility.runAndSend(animalStringID, 6000);
                        this.interactionPositive = GameRandom.globalRandom.getChance(GameMath.lerp(averageHappinessFloat, 0.3f, 0.8f));
                    } else {
                        Item foodItem = foods.get(decidedTicket - settlers.size() - animals.size());
                        ((HumanMob)mob).showItemThoughtAbility.runAndSend(foodItem, 6000);
                        this.interactionPositive = GameRandom.globalRandom.getChance(GameMath.lerp(averageHappinessFloat, 0.3f, 0.7f)) || this.settlerCurrentlyInteractingWith.recentFoodItemIDsEaten.stream().anyMatch(itemID -> itemID.intValue() == foodItem.getID());
                    }
                } else if (this.currentInteractionStageTicker == 80) {
                    this.settlerCurrentlyInteractingWith.sendMovementPacket(false);
                    if (this.interactionPositive) {
                        this.settlerCurrentlyInteractingWith.showLoveThoughtAbility.runAndSend(6000);
                        this.settlerCurrentlyInteractingWith.startExpressionAbility.runAndSend(FormExpressionWheel.Expression.SURPRISED, 6000);
                    } else {
                        this.settlerCurrentlyInteractingWith.showDisagreeThoughtAbility.runAndSend(6000);
                        this.settlerCurrentlyInteractingWith.startExpressionAbility.runAndSend(FormExpressionWheel.Expression.BORED, 6000);
                    }
                } else if (this.currentInteractionStageTicker == 160) {
                    if (this.interactionPositive) {
                        ((HumanMob)mob).showExcitedThoughtAbility.runAndSend(6000);
                        ((HumanMob)mob).startExpressionAbility.runAndSend(FormExpressionWheel.Expression.SURPRISED, 6000);
                    } else {
                        ((HumanMob)mob).startExpressionAbility.runAndSend(FormExpressionWheel.Expression.ANGRY, 6000);
                        interactionDone = true;
                    }
                } else if (this.currentInteractionStageTicker >= 240) {
                    interactionDone = true;
                }
                if (interactionDone) {
                    this.settlerCurrentlyInteractingWith.ai.blackboard.submitEvent("humanInteractionEnded", new AIEvent());
                    this.settlerCurrentlyInteractingWith.ai.blackboard.submitEvent("wanderNow", new AIEvent());
                    blackboard.submitEvent("wanderNow", new AIEvent());
                    this.settlerCurrentlyInteractingWith = null;
                    this.isControllingInteraction = false;
                    return AINodeResult.FAILURE;
                }
                return AINodeResult.SUCCESS;
            }
            if (this.nextPathFindTime < mob.getLocalTime()) {
                this.nextPathFindTime = mob.getLocalTime() + 2000L;
                int targetTileX = this.settlerCurrentlyInteractingWith.getTileX();
                int targetTileY = this.settlerCurrentlyInteractingWith.getTileY();
                return this.moveToTileTask(targetTileX, targetTileY, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), targetTileX, targetTileY), path -> {
                    if (path.moveIfWithin(-1, 1, () -> {
                        this.nextPathFindTime = 0L;
                    })) {
                        int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                        this.nextPathFindTime = mob.getLocalTime() + (long)nextPathTimeAdd;
                        return AINodeResult.SUCCESS;
                    }
                    return AINodeResult.FAILURE;
                });
            }
            return AINodeResult.SUCCESS;
        }
        --this.searchInteractionTickCountdown;
        if (this.searchInteractionTickCountdown <= 0) {
            this.refreshCooldown();
            List<HumanMob> validSettlers = this.findSettlersToInteractWith(mob);
            while (!validSettlers.isEmpty()) {
                HumanMob nextSettlerToInteractWith = validSettlers.remove(GameRandom.globalRandom.nextInt(validSettlers.size()));
                SettlerInteractReqestAIEvent event = new SettlerInteractReqestAIEvent((HumanMob)mob);
                nextSettlerToInteractWith.ai.blackboard.submitEvent("humanInteractRequest", event);
                if (!event.accepted) continue;
                this.settlerCurrentlyInteractingWith = nextSettlerToInteractWith;
                this.isControllingInteraction = true;
                this.currentInteractionStageTicker = 0;
                this.nextPathFindTime = 0L;
                return AINodeResult.SUCCESS;
            }
        }
        return AINodeResult.FAILURE;
    }

    protected List<HumanMob> findSettlersToInteractWith(T mob) {
        ZoneTester zoneTester = ((HumanMob)mob).getJobRestrictZone();
        return ((Entity)mob).getLevel().entityManager.mobs.streamAreaTileRange(((Entity)mob).getX(), ((Entity)mob).getY(), 30).filter(m -> m != mob).filter(m -> m instanceof HumanMob).map(m -> (HumanMob)m).filter(m -> m.canInteractWithOtherSettlers).filter(m -> mob.estimateCanMoveTo(m.getTileX(), m.getTileY(), true)).filter(m -> zoneTester.containsTile(m.getTileX(), m.getTileY())).findExtraDistance(1, Collectors.toList());
    }

    protected List<HumanMob> findRandomSettler(T mob, int maxTileRange, int limit) {
        return ((Entity)mob).getLevel().entityManager.mobs.streamAreaTileRange(((Entity)mob).getX(), ((Entity)mob).getY(), maxTileRange).filter(m -> m != mob && m != this.settlerCurrentlyInteractingWith).filter(m -> m instanceof HumanMob).map(m -> (HumanMob)m).findExtraItems(limit, Collectors.toList());
    }

    protected List<String> findRandomAnimalStringIDs(T mob, int maxTileRange, int limit) {
        return ((Entity)mob).getLevel().entityManager.mobs.streamAreaTileRange(((Entity)mob).getX(), ((Entity)mob).getY(), maxTileRange).filter(m -> m != mob && m != this.settlerCurrentlyInteractingWith).filter(m -> m instanceof HusbandryMob).map(m -> (HusbandryMob)m).filter(HusbandryMob::canBeUsedAsSettlerThought).map(Mob::getStringID).findExtraItems(limit, Collectors.toList());
    }

    protected List<Item> findRandomFoodItem(T mob, int limit) {
        ArrayList<Item> list = new ArrayList<Item>(limit);
        Iterator iterator = ((HumanMob)mob).recentFoodItemIDsEaten.iterator();
        while (iterator.hasNext()) {
            int itemID = (Integer)iterator.next();
            Item item = ItemRegistry.getItem(itemID);
            if (item == null) continue;
            list.add(item);
            if (list.size() < limit) continue;
            break;
        }
        return list;
    }
}

