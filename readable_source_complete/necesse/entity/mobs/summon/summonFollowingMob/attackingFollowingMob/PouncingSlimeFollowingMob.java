/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingJumpingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PouncingSlimeFollowingMob
extends AttackingFollowingJumpingMob {
    public IntMobAbility startPounceAbility;
    public CoordinateMobAbility pounceAbility;
    public Mob pounceTarget;
    public long pounceStartTime;
    public long pounceLastTime;
    public int pounceChargeTime;
    public CameraShake pounceShake;

    public PouncingSlimeFollowingMob() {
        super(10);
        this.setSpeed(60.0f);
        this.setFriction(2.0f);
        this.jumpStats.setJumpAnimationTime(400);
        this.jumpStats.setJumpStrength(200.0f);
        this.jumpStats.setJumpCooldown(50);
        this.jumpStats.jumpStrengthUseSpeedMod = false;
        this.collision = new Rectangle(-8, -6, 16, 12);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-12, -16, 26, 24);
        this.swimMaskMove = 8;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = 0;
        this.startPounceAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (value >= 0) {
                    PouncingSlimeFollowingMob.this.pounceStartTime = PouncingSlimeFollowingMob.this.getWorldEntity().getLocalTime();
                    PouncingSlimeFollowingMob.this.pounceChargeTime = value;
                } else {
                    PouncingSlimeFollowingMob.this.pounceStartTime = 0L;
                }
            }
        });
        this.pounceAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                float dist = GameMath.limit(PouncingSlimeFollowingMob.this.getDistance(x, y), 128.0f, 512.0f);
                Point2D.Float norm = GameMath.normalize(x - PouncingSlimeFollowingMob.this.getX(), y - PouncingSlimeFollowingMob.this.getY());
                PouncingSlimeFollowingMob.this.runJump(norm.x * dist * 3.0f, norm.y * dist * 3.0f);
                PouncingSlimeFollowingMob.this.pounceStartTime = 0L;
                PouncingSlimeFollowingMob.this.pounceLastTime = PouncingSlimeFollowingMob.this.getWorldEntity().getLocalTime();
            }
        });
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.summonDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 20;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null && target != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
        }
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return super.canCollisionHit(target);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<PouncingSlimeFollowingMob>(this, new PouncingSlimeAI(576, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 500, 256, 640, 96));
    }

    public boolean isPouncing() {
        return this.pounceStartTime > 0L;
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        if (this.isPouncing()) {
            if (this.isServer()) {
                if (this.pounceTarget != null && this.isSamePlace(this.pounceTarget) && this.getDistance(this.pounceTarget) <= 288.0f) {
                    long timeSinceStartPounce = this.getWorldEntity().getLocalTime() - this.pounceStartTime;
                    if (timeSinceStartPounce >= (long)this.pounceChargeTime) {
                        if (!this.getLevel().collides((Shape)new MovedRectangle(this, this.pounceTarget.getX(), this.pounceTarget.getY()), this.modifyChasingCollisionFilter(this.getLevelCollisionFilter(), this.pounceTarget))) {
                            this.ai.blackboard.put("currentTarget", this.pounceTarget);
                            this.ai.blackboard.put("chaserTarget", this.pounceTarget);
                            Point2D.Float pos = Projectile.getPredictedTargetPos(this.pounceTarget, this.x, this.y, Math.max(16.0f, this.getDistance(this.pounceTarget)), 0.0f);
                            this.pounceAbility.runAndSend((int)pos.x, (int)pos.y);
                        } else {
                            this.startPounceAbility.runAndSend(-1);
                        }
                    }
                } else {
                    this.startPounceAbility.runAndSend(-1);
                }
            }
            super.calcAcceleration(speed, friction, 0.0f, 0.0f, delta);
        } else {
            super.calcAcceleration(speed, friction, moveX, moveY, delta);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.pouncingSlime, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.npcdeath).volume(0.1f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int spriteX;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PouncingSlimeFollowingMob.getTileCoordinate(x), PouncingSlimeFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 26 - 32;
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000);
        } else {
            spriteX = this.getJumpAnimationFrame(6);
            if (spriteX == 0 && this.isPouncing()) {
                if (this.pounceShake == null || this.pounceShake.isOver(this.getWorldEntity().getLocalTime())) {
                    this.pounceShake = new CameraShake(this.getWorldEntity().getLocalTime(), 1000, 50, 2.0f, 2.0f, true);
                }
                Point2D.Float shake = this.pounceShake.getCurrentShake(this.getWorldEntity().getLocalTime());
                drawX = (int)((float)drawX + shake.x);
                drawY = (int)((float)drawY + shake.y);
                spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 200);
            }
        }
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.pouncingSlime.initDraw().sprite(spriteX, inLiquid ? 1 : 0, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(PouncingSlimeFollowingMob.getTileCoordinate(x), PouncingSlimeFollowingMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.pouncingSlime_shadow.initDraw().sprite(spriteX, 0, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.slimeSplash2).volume(0.15f).pitchVariance(0.1f), this);
        }
    }

    public static class PouncingSlimeAI
    extends SelectorAINode<PouncingSlimeFollowingMob> {
        public PouncingSlimeAI(int searchDistance, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int attackCooldown, int attackDistance, int teleportDistance, int stoppingDistance) {
            SequenceAINode chaserSequence = new SequenceAINode();
            chaserSequence.addChild(new FollowerBaseSetterAINode());
            chaserSequence.addChild(new FollowerFocusTargetSetterAINode());
            final SummonTargetFinderAINode targetFinder = new SummonTargetFinderAINode(searchDistance);
            chaserSequence.addChild(targetFinder);
            chaserSequence.addChild(new FollowerAINode<PouncingSlimeFollowingMob>(-1, attackDistance - 100){

                @Override
                public Mob getFollowingMob(PouncingSlimeFollowingMob mob) {
                    return this.getBlackboard().getObject(Mob.class, targetFinder.currentTargetKey);
                }
            });
            CooldownAttackTargetAINode<PouncingSlimeFollowingMob> cooldownAttackNode = new CooldownAttackTargetAINode<PouncingSlimeFollowingMob>(cooldownTimer, attackCooldown, attackDistance){

                @Override
                public boolean canAttackTarget(PouncingSlimeFollowingMob mob, Mob target) {
                    if (mob.inLiquid() || mob.isPouncing()) {
                        return false;
                    }
                    return !mob.getLevel().collides((Shape)new MovedRectangle(mob, target.getX(), target.getY()), mob.getLevelCollisionFilter());
                }

                @Override
                public boolean attackTarget(PouncingSlimeFollowingMob mob, Mob target) {
                    mob.pounceTarget = target;
                    mob.startPounceAbility.runAndSend(500);
                    return true;
                }
            };
            cooldownAttackNode.randomizeAttackTimer();
            chaserSequence.addChild(cooldownAttackNode);
            this.addChild(chaserSequence);
            this.addChild(new PlayerFlyingFollowerAINode(teleportDistance, stoppingDistance));
        }
    }
}

