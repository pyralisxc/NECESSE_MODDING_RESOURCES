/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public interface ItemAttackerWeaponItem {
    default public AINode<ItemAttackerMob> getItemAttackerWeaponChaserAI(final ItemAttackerChaserAINode<? extends ItemAttackerMob> node, ItemAttackerMob mob, InventoryItem item, final ItemAttackSlot slot) {
        final AtomicReference<InventoryItem> lastItem = new AtomicReference<InventoryItem>(item);
        return new ChaserAINode<ItemAttackerMob>(50, false, false){

            @Override
            public AINodeResult tickNode(ItemAttackerMob mob, Blackboard<ItemAttackerMob> blackboard) {
                InventoryItem item = slot.getItem();
                if (item == null) {
                    return AINodeResult.FAILURE;
                }
                if (item.item == ItemAttackerWeaponItem.this) {
                    lastItem.set(item);
                    this.attackDistance = ItemAttackerWeaponItem.this.getItemAttackerAttackRange(mob, item);
                    this.stoppingDistance = ItemAttackerWeaponItem.this.getItemAttackerStoppingDistance(mob, item, this.attackDistance);
                    this.lostTargetStopAttackingDistance = ItemAttackerWeaponItem.this.getItemAttackerLostTargetDistance(mob, item, this.attackDistance);
                    this.minimumAttackDistance = ItemAttackerWeaponItem.this.getItemAttackerMinimumAttackRange(mob, item);
                    this.runAwayDistance = ItemAttackerWeaponItem.this.getItemAttackerRunAwayDistance(mob, item);
                    return super.tickNode(mob, blackboard);
                }
                return AINodeResult.FAILURE;
            }

            @Override
            public boolean isTargetWithinAttackRange(ItemAttackerMob mob, Mob target, float distanceToTarget, boolean hasStartedAttack) {
                int lostDistance = hasStartedAttack ? Math.max(this.attackDistance, this.lostTargetStopAttackingDistance) : this.attackDistance;
                return distanceToTarget >= (float)this.minimumAttackDistance && 1.isTargetHitboxWithinRange(mob, mob.x, mob.y, target, Math.max(lostDistance - 20, 10));
            }

            @Override
            public boolean canHitTarget(ItemAttackerMob mob, float fromX, float fromY, Mob target) {
                return ItemAttackerWeaponItem.this.canItemAttackerHitTarget(mob, fromX, fromY, target, (InventoryItem)lastItem.get());
            }

            @Override
            public boolean attackTarget(ItemAttackerMob mob, Mob target) {
                Point attackPos;
                int seed;
                InventoryItem invItem;
                if (mob.isAttacking && mob.canAnimAttackAgain(invItem = (InventoryItem)lastItem.get()) && mob.getNextAnimAttackCooldown() <= 0L) {
                    seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
                    attackPos = ItemAttackerWeaponItem.this.getItemAttackerAttackPosition(mob.getLevel(), mob, target, seed, invItem);
                    if (attackPos != null) {
                        GNDItemMap attackMap = mob.runItemAttack(invItem, attackPos.x, attackPos.y, seed, mob.animAttack, slot, null);
                        AttackHandler newAttackHandler = mob.getAttackHandler();
                        if (newAttackHandler != null) {
                            newAttackHandler.setItemAttackerTarget(target);
                        }
                        node.runOnAttackedEvent(mob, invItem, attackPos.x, attackPos.y, mob.animAttack, attackMap);
                        return true;
                    }
                }
                if (mob.canAttack()) {
                    invItem = (InventoryItem)lastItem.get();
                    if (mob.isItemOnCooldown(invItem.item)) {
                        return false;
                    }
                    seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
                    attackPos = ItemAttackerWeaponItem.this.getItemAttackerAttackPosition(mob.getLevel(), mob, target, seed, invItem);
                    if (attackPos != null) {
                        String canAttack;
                        AttackHandler currentAttackHandler = mob.getAttackHandler();
                        if (currentAttackHandler != null) {
                            if (!invItem.item.getConstantUse(invItem)) {
                                return false;
                            }
                            if (!currentAttackHandler.canRunAttack(mob.getLevel(), attackPos.x, attackPos.y, mob, invItem, slot)) {
                                return false;
                            }
                        }
                        if ((canAttack = invItem.item.canAttack(mob.getLevel(), attackPos.x, attackPos.y, mob, invItem)) == null) {
                            GNDItemMap attackMap = mob.runItemAttack(invItem, attackPos.x, attackPos.y, seed, 0, slot, null);
                            mob.showItemAttackMobAbility.runAndSend(invItem, attackPos.x, attackPos.y, 0, seed, attackMap);
                            AttackHandler newAttackHandler = mob.getAttackHandler();
                            if (newAttackHandler != null) {
                                newAttackHandler.setItemAttackerTarget(target);
                            }
                            node.runOnAttackedEvent(mob, invItem, attackPos.x, attackPos.y, 0, attackMap);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    default public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return null;
    }

    default public boolean shouldUpdateItemAttackerChaserAI(ItemAttackerMob mob, InventoryItem last, InventoryItem item) {
        return false;
    }

    default public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        if (this instanceof ToolItem) {
            return ((ToolItem)this).getAttackRange(item);
        }
        return 50;
    }

    default public int getItemAttackerStoppingDistance(ItemAttackerMob mob, InventoryItem item, int attackRange) {
        return attackRange - 20;
    }

    default public int getItemAttackerLostTargetDistance(ItemAttackerMob mob, InventoryItem item, int attackRange) {
        return attackRange + 20;
    }

    default public float getItemAttackerWeaponValueFlat(InventoryItem item) {
        if (this instanceof ToolItem) {
            return ((ToolItem)this).getEnchantCost(item);
        }
        return 0.0f;
    }

    default public float getItemAttackerWeaponValue(ItemAttackerMob mob, InventoryItem item) {
        if (this instanceof ToolItem) {
            ToolItemEnchantment enchantment = ((ToolItem)this).getEnchantment(item);
            return this.getItemAttackerWeaponValueFlat(item) * (enchantment == null ? 1.0f : enchantment.getEnchantCostMod());
        }
        return 0.0f;
    }

    default public float getRaiderTicketModifier(InventoryItem item, HashSet<String> obtainedItems) {
        return 1.0f;
    }

    default public int getItemAttackerMinimumAttackRange(ItemAttackerMob attackerMob, InventoryItem item) {
        return 0;
    }

    default public int getItemAttackerRunAwayDistance(ItemAttackerMob attackerMob, InventoryItem item) {
        return 0;
    }

    default public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return ChaserAINode.hasLineOfSightToTarget(attackerMob, fromX, fromY, target);
    }

    default public boolean itemAttackerHasLineOfSightToTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, float hitBoxWidth) {
        return ChaserAINode.hasLineOfSightToTarget(attackerMob, fromX, fromY, target, hitBoxWidth);
    }

    default public boolean itemAttackerHasLineOfSightToTarget(ItemAttackerMob attackerMob, Mob target, float hitBoxWidth) {
        return ChaserAINode.hasLineOfSightToTarget(attackerMob, attackerMob.x, attackerMob.y, target, hitBoxWidth);
    }

    default public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    default public Point getPredictedItemAttackerAttackPosition(ItemAttackerMob attackerMob, Mob target, float velocity, float distanceOffset) {
        Point2D.Float targetPos = Projectile.getPredictedTargetPos(target, attackerMob.x, attackerMob.y, velocity, distanceOffset);
        return new Point((int)targetPos.x, (int)targetPos.y);
    }

    default public Point getPredictedItemAttackerAttackPositionMillis(Mob target, int millisDelay) {
        float nextX = Projectile.getPositionAfterMillis(target.dx, millisDelay);
        float nextY = Projectile.getPositionAfterMillis(target.dy, millisDelay);
        return new Point((int)(target.x + nextX), (int)(target.y + nextY));
    }

    default public Point applyInaccuracy(ItemAttackerMob mob, InventoryItem item, Point target) {
        double dist = target.distance(mob.getX(), mob.getY());
        float skillPercent = item.getGndData().hasKey("skillPercent") ? item.getGndData().getFloat("skillPercent") : mob.getWeaponSkillPercent(item);
        float skillPercentInv = 1.0f - GameMath.limit(skillPercent, 0.0f, 1.0f);
        GameRandom random = new GameRandom(GameRandom.prime(mob.getInaccuracySeed(item)));
        int xInaccuracy = (int)(random.getDoubleBetween(-dist, dist) * (double)skillPercentInv * (double)0.9f);
        int yInaccuracy = (int)(random.getDoubleBetween(-dist, dist) * (double)skillPercentInv * (double)0.9f);
        return new Point(target.x + xInaccuracy, target.y + yInaccuracy);
    }

    default public void itemAttackerTickHolding(InventoryItem item, ItemAttackerMob mob) {
        if (mob.isClient()) {
            item.item.refreshLight(mob.getLevel(), mob.x, mob.y, item, true);
        }
    }
}

