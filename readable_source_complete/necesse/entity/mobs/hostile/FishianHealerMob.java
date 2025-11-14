/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ConfusedWandererAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.FishianHealProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class FishianHealerMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("coin", 10, 40).splitItems(4), LootItem.between("bamboo", 5, 10).splitItems(4));
    public final TargetedMobAbility startHealVolley;
    public final EmptyMobAbility stopHealVolley;
    protected int healVolleyRunTime = 4000;
    protected int healVolleyProjectiles = 20;
    protected int healVolleyHealPerProjectile = 40;
    protected long healVolleyStartTime;
    protected Mob healVolleyTarget;
    protected float healVolleyBuffer;

    public FishianHealerMob() {
        super(800);
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.5f);
        this.setArmor(25);
        this.attackAnimTime = 1000;
        this.attackCooldown = 4000;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.startHealVolley = this.registerAbility(new TargetedMobAbility(){

            @Override
            protected void run(Mob target) {
                FishianHealerMob.this.healVolleyTarget = target;
                FishianHealerMob.this.healVolleyStartTime = FishianHealerMob.this.getTime();
                FishianHealerMob.this.healVolleyBuffer = 0.0f;
                FishianHealerMob.this.attackAnimTime = FishianHealerMob.this.healVolleyRunTime + 500;
                FishianHealerMob.this.attackCooldown = 4000;
                if (target != null) {
                    FishianHealerMob.this.attack(target.getX(), target.getY(), false);
                }
            }
        });
        this.stopHealVolley = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                FishianHealerMob.this.healVolleyTarget = null;
                FishianHealerMob.this.healVolleyStartTime = 0L;
                FishianHealerMob.this.forceStopAttack();
                FishianHealerMob.this.attackCooldown = 3000;
                FishianHealerMob.this.startAttackCooldown();
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FishianHealerMob>(this, new FishianHealerAI(960, 384, 40000, false, true));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            if (this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS).booleanValue()) {
                return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
            }
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.healVolleyStartTime != 0L) {
            long timeSinceStart = this.getTime() - this.healVolleyStartTime;
            if (timeSinceStart > (long)this.healVolleyRunTime) {
                this.forceStopAttack();
                this.healVolleyStartTime = 0L;
                this.startAttackCooldown();
            } else if (this.healVolleyTarget != null) {
                this.updateAttackDir(this.healVolleyTarget.getX(), this.healVolleyTarget.getY(), false);
                if (!this.isClient()) {
                    float msPerProjectile = (float)this.healVolleyRunTime / (float)this.healVolleyProjectiles;
                    this.healVolleyBuffer += delta;
                    while (this.healVolleyBuffer >= msPerProjectile) {
                        this.healVolleyBuffer -= msPerProjectile;
                        FishianHealProjectile projectile = new FishianHealProjectile(this.getLevel(), this, this.x, this.y, this.healVolleyTarget, new GameDamage(this.healVolleyHealPerProjectile));
                        projectile.moveDist(30.0);
                        float targetAngle = GameMath.getAngle(GameMath.normalize(this.healVolleyTarget.x - this.x, this.healVolleyTarget.y - this.y));
                        targetAngle += GameRandom.globalRandom.getOneOf(Float.valueOf(-45.0f), Float.valueOf(45.0f)).floatValue() + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f);
                        targetAngle = GameMath.fixAngle(targetAngle);
                        Point2D.Float targetDir = GameMath.getAngleDir(targetAngle);
                        projectile.setTarget(this.x + targetDir.x * 100.0f, this.y + targetDir.y * 100.0f);
                        this.getLevel().entityManager.projectiles.add(projectile);
                    }
                    if (this.healVolleyTarget.removed() || this.healVolleyTarget.getHealth() >= this.healVolleyTarget.getMaxHealth()) {
                        this.stopHealVolley.runAndSend();
                    }
                }
                if (!this.isServer()) {
                    Point2D.Float targetDir = GameMath.normalize(this.healVolleyTarget.x - this.x, this.healVolleyTarget.y - this.y);
                    if (GameRandom.globalRandom.getChance(0.5f)) {
                        Point2D.Float perpDir = GameMath.getPerpendicularDir(targetDir);
                        int angle = GameRandom.globalRandom.nextInt(360);
                        Point2D.Float dir = GameMath.getAngleDir(angle);
                        float range = GameRandom.globalRandom.getFloatBetween(10.0f, 20.0f);
                        float targetDirDistance = 36.0f;
                        float perpDirDistance = this.getDir() == 1 ? -24.0f : 24.0f;
                        float startX = this.x + targetDir.x * targetDirDistance + perpDir.x * perpDirDistance + dir.x * range;
                        float startY = this.y + 4.0f;
                        float endHeight = 16.0f - targetDir.y * targetDirDistance - perpDir.y * perpDirDistance;
                        float startHeight = endHeight + dir.y * range;
                        int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                        float speed = dir.x * range * 250.0f / (float)lifeTime;
                        Color color1 = new Color(32, 165, 22);
                        Color color2 = new Color(13, 73, 8);
                        Color color3 = new Color(95, 234, 85);
                        Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
                        this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
                    }
                }
            }
        }
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("fishian", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fishianHealer.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FishianHealerMob.getTileCoordinate(x), FishianHealerMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        float animProgress = this.getAttackAnimProgress();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FishianHealerMob.getTileCoordinate(x), FishianHealerMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.fishianHealer).sprite(sprite).dir(dir).mask(swimMask).light(light);
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("fishianhealerstaff"), null, animProgress, this.attackDir.x, this.attackDir.y);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(0.0f)).min(Float.valueOf(0.75f)));
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.puddleCobble) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
    }

    public static class FishianHealerAI<T extends FishianHealerMob>
    extends SelectorAINode<T> {
        protected boolean wanderAfterAttack = false;

        public FishianHealerAI(int searchDistance, int shootDistance, int wanderFrequency, boolean smartPositioning, boolean changePositionOnHit) {
            this.addChildFirst(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    blackboard.onEvent("wanderAfterAttack", e -> {
                        wanderAfterAttack = true;
                    });
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (wanderAfterAttack && !((FishianHealerMob)mob).isAttacking) {
                        wanderAfterAttack = false;
                        if (((FishianHealerMob)mob).attackDir != null) {
                            float attackAngle = GameMath.getAngle(((FishianHealerMob)mob).attackDir);
                            float runAwayAngle = GameRandom.globalRandom.nextBoolean() ? GameRandom.globalRandom.getFloatBetween(attackAngle - 90.0f, attackAngle - 110.0f) : GameRandom.globalRandom.getFloatBetween(attackAngle + 90.0f, attackAngle + 110.0f);
                            runAwayAngle = GameMath.fixAngle(runAwayAngle);
                            Point2D.Float runAwayDir = GameMath.getAngleDir(runAwayAngle);
                            int confuseTime = GameRandom.globalRandom.getIntBetween(3000, 5000);
                            blackboard.submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime, runAwayDir));
                        }
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChildFirst(new ConfusedWandererAINode());
            this.addChild(new EscapeAINode<T>(){

                @Override
                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    return ((FishianHealerMob)mob).isHostile && !((FishianHealerMob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING) != false;
                }
            });
            PlayerChaserAI chaser = new PlayerChaserAI<T>(searchDistance, shootDistance, smartPositioning, changePositionOnHit){

                @Override
                public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
                    return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, target);
                }

                @Override
                public boolean attackTarget(T mob, Mob target) {
                    if (!((FishianHealerMob)mob).isAttacking && ((Mob)mob).canAttack()) {
                        ((FishianHealerMob)mob).startHealVolley.runAndSend(target);
                        wanderAfterAttack = true;
                        return true;
                    }
                    return false;
                }

                @Override
                public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return distance.streamMobsInRange(base, mob).filter(m -> {
                        if (m == null || m == mob || m.removed() || !m.isVisible()) {
                            return false;
                        }
                        if (!m.isHostile) {
                            return false;
                        }
                        if (m.getID() == mob.getID()) {
                            return false;
                        }
                        return m.getHealth() < m.getMaxHealth();
                    });
                }
            };
            chaser.targetFinderAINode.validity = new TargetValidity<T>(){

                @Override
                public boolean isValidTarget(AINode<T> node, T mob, Mob target, boolean isNewTarget) {
                    return target != null && !target.removed() && target.isVisible() && ((Entity)mob).isSamePlace(target) && target.isHostile && target.getHealth() < target.getMaxHealth();
                }
            };
            chaser.chaserAINode.minimumAttackDistance = 128;
            this.addChild(chaser);
            if (wanderFrequency >= 0) {
                this.addChild(new WandererAINode(wanderFrequency));
            }
        }
    }
}

