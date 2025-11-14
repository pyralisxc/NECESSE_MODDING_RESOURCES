/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.ComputedObjectValue;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;

public class HumanAngerTargetAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    public TargetValidity<T> validity;
    public TargetFinderDistance<T> distance;
    public String currentTargetKey;
    public ArrayList<Mob> enemies = new ArrayList();
    public float anger = 0.0f;

    public HumanAngerTargetAINode(TargetFinderDistance<T> distance, TargetValidity<T> validity, String currentTargetKey) {
        this.distance = distance;
        this.validity = validity;
        this.currentTargetKey = currentTargetKey;
    }

    public HumanAngerTargetAINode(TargetFinderDistance<T> distance, TargetValidity<T> validity) {
        this(distance, validity, "currentTarget");
    }

    public HumanAngerTargetAINode(TargetFinderDistance<T> distance) {
        this(distance, new TargetValidity());
    }

    public static void addNearbyHumansAnger(Mob mob, Attacker attacker, Predicate<HumanMob> isValidMob, boolean alertNearbyToo) {
        if (mob.isClient()) {
            return;
        }
        float percent = Math.abs((float)mob.getHealth() / (float)mob.getMaxHealth() - 1.0f);
        float anger = percent * 2.5f;
        HumanAngerTargetAINode.addNearbyHumansAnger(mob.getLevel(), mob.getTileX(), mob.getTileY(), anger, attacker, isValidMob, alertNearbyToo);
    }

    public static void addNearbyHumansAnger(Level level, int tileX, int tileY, float anger, Attacker attacker, Predicate<HumanMob> isValidMob, boolean alertNearbyToo) {
        Mob attackOwner;
        if (level.isClient()) {
            return;
        }
        Mob mob = attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        if (attackOwner == null || !attackOwner.isPlayer) {
            return;
        }
        if (attackOwner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
            return;
        }
        level.entityManager.mobs.getInRegionByTileRange(tileX, tileY, 25).stream().filter(m -> m instanceof HumanMob && isValidMob.test((HumanMob)m)).forEach(m -> {
            HumanAngerTargetAINode humanAngerHandler = m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
            humanAngerHandler.addAnger(anger, attackOwner, alertNearbyToo);
        });
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.put("humanAngerHandler", this);
        blackboard.onWasHit(e -> {
            Mob attackOwner;
            if (mob.isClient() || e.event.wasPrevented) {
                return;
            }
            Mob mob2 = attackOwner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
            if (attackOwner == null || !attackOwner.isPlayer) {
                return;
            }
            if (attackOwner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
                return;
            }
            if (mob.isFriendlyClient(((PlayerMob)attackOwner).getNetworkClient())) {
                return;
            }
            float percent = Math.abs((float)mob.getHealth() / (float)mob.getMaxHealth() - 1.0f);
            this.addAnger(percent * 2.5f, attackOwner, true);
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        this.enemies.removeIf(m -> m.removed() || m.isPlayer && mob.isFriendlyClient(((PlayerMob)m).getNetworkClient()));
        if (this.anger > 0.0f) {
            if (!((HumanMob)mob).buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY) || ((HumanMob)mob).buffManager.getBuff(BuffRegistry.HUMAN_ANGRY).getDurationLeft() < 500) {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.HUMAN_ANGRY, (Mob)mob, 5.0f, null);
                ((Mob)mob).addBuff(ab, true);
            }
            Mob lastTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
            float decreasePerSecond = 0.2f;
            this.anger -= decreasePerSecond / 20.0f;
            Point base = ((HumanMob)mob).home == null || ((HumanMob)mob).hasCommandOrders() ? new Point(((Entity)mob).getX(), ((Entity)mob).getY()) : new Point(((HumanMob)mob).home.x * 32 + 16, ((HumanMob)mob).home.y * 32 + 16);
            Mob target = this.enemies.stream().filter(m -> this.validity.isValidTarget(this, (HumanMob)mob, (Mob)m, true)).map(m -> new ComputedObjectValue<Mob, Float>((Mob)m, () -> Float.valueOf(this.distance.getDistance(base, (Mob)m)))).filter(cv -> ((Float)cv.get()).floatValue() < (float)(this.distance.getTargetLostDistance((HumanMob)mob, (Mob)cv.object) * 3)).min(Comparator.comparingDouble(cv -> ((Float)cv.get()).floatValue())).map(cv -> (Mob)cv.object).orElse(null);
            if (target != null) {
                blackboard.put(this.currentTargetKey, target);
                return AINodeResult.SUCCESS;
            }
            if (lastTarget != null && this.enemies.contains(lastTarget)) {
                blackboard.put(this.currentTargetKey, null);
                if (((HumanMob)mob).home != null && !((HumanMob)mob).hasCommandOrders()) {
                    return this.moveToTileTask(((HumanMob)mob).home.x, ((HumanMob)mob).home.y, null, path -> {
                        path.move(null);
                        return AINodeResult.SUCCESS;
                    });
                }
                return AINodeResult.SUCCESS;
            }
        } else if (this.anger <= 0.0f && !this.enemies.isEmpty()) {
            Mob lastTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
            this.anger = 0.0f;
            if (((HumanMob)mob).buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY)) {
                ((HumanMob)mob).buffManager.removeBuff(BuffRegistry.HUMAN_ANGRY, true);
            }
            if (this.enemies.contains(lastTarget)) {
                blackboard.put(this.currentTargetKey, null);
                blackboard.mover.stopMoving((Mob)mob);
            }
            this.enemies.clear();
        }
        return AINodeResult.FAILURE;
    }

    public void addAnger(float anger, Mob attackOwner, boolean alertNearbyToo) {
        GameMessage angryMessage;
        float oldAnger = this.anger;
        this.anger += anger;
        HumanMob mob = (HumanMob)this.mob();
        if (this.anger >= 1.0f) {
            GameMessage attackMessage;
            if (oldAnger < 1.0f && !mob.removed() && (attackMessage = mob.getRandomAttackMessage()) != null && mob.isServer()) {
                mob.getServer().network.sendToClientsWithEntity(new PacketMobChat(mob.getUniqueID(), attackMessage), mob);
            }
            this.addEnemy(attackOwner, this.anger);
            if (alertNearbyToo) {
                mob.getLevel().entityManager.mobs.getInRegionByTileRange(mob.getTileX(), mob.getTileY(), 25).stream().filter(m -> m.isSameTeam(mob) && m instanceof HumanMob).forEach(m -> {
                    HumanAngerTargetAINode humanAngerHandler = m.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                    if (humanAngerHandler != null) {
                        humanAngerHandler.addEnemy(attackOwner, this.anger);
                    }
                });
            }
        } else if (!mob.removed() && (angryMessage = mob.getRandomAngryMessage()) != null && mob.isServer()) {
            mob.getServer().network.sendToClientsWithEntity(new PacketMobChat(mob.getUniqueID(), angryMessage), mob);
        }
    }

    public void addEnemy(Mob mob, float anger) {
        if (!this.enemies.contains(mob)) {
            this.enemies.add(mob);
        }
        this.anger = anger;
    }
}

