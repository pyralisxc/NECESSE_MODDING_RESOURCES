/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
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
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.mobs.hostile.HostileSlimeMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class GiantSwampSlimeMob
extends HostileSlimeMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage baseDamage = new GameDamage(70.0f);
    public static GameDamage incursionDamage = new GameDamage(90.0f);
    public IntMobAbility startPounceAbility;
    public CoordinateMobAbility pounceAbility;
    public Mob pounceTarget;
    public long pounceStartTime;
    public int pounceChargeTime;
    public CameraShake pounceShake;

    public GiantSwampSlimeMob() {
        super(450);
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.setKnockbackModifier(0.6f);
        this.setArmor(25);
        this.jumpStats.setJumpStrength(150.0f);
        this.jumpStats.setJumpCooldown(150);
        this.collision = new Rectangle(-20, -10, 40, 16);
        this.hitBox = new Rectangle(-22, -20, 44, 28);
        this.selectBox = new Rectangle(-24, -40, 48, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
        this.startPounceAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (value >= 0) {
                    GiantSwampSlimeMob.this.pounceStartTime = GiantSwampSlimeMob.this.getWorldEntity().getLocalTime();
                    GiantSwampSlimeMob.this.pounceChargeTime = value;
                    SoundManager.playSound(new SoundSettings(GameResources.slimeSplash3).volume(0.3f), GiantSwampSlimeMob.this);
                } else {
                    GiantSwampSlimeMob.this.pounceStartTime = 0L;
                }
            }
        });
        this.pounceAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                float dist = Math.min(512.0f, GiantSwampSlimeMob.this.getDistance(x, y));
                Point2D.Float norm = GameMath.normalize(x - GiantSwampSlimeMob.this.getX(), y - GiantSwampSlimeMob.this.getY());
                GiantSwampSlimeMob.this.runJump(norm.x * dist * 2.6f, norm.y * dist * 2.6f);
                GiantSwampSlimeMob.this.pounceStartTime = 0L;
                SoundManager.playSound(new SoundSettings(GameResources.slimeSplash4).volume(0.2f), GiantSwampSlimeMob.this);
            }
        });
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.blunthit).volume(0.1f), this);
            SoundManager.playSound(new SoundSettings(GameResources.slimeSplash2).volume(0.2f), this);
        }
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(600);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(30);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        this.ai = new BehaviourTreeAI<GiantSwampSlimeMob>(this, new GiantSwampSlimeAI(512, damage, 100, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 3500, 480, 40000));
    }

    public boolean isPouncing() {
        return this.pounceStartTime > 0L;
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        if (this.isPouncing()) {
            if (this.isServer()) {
                if (this.pounceTarget != null && this.isSamePlace(this.pounceTarget)) {
                    long timeSinceStartPounce = this.getWorldEntity().getLocalTime() - this.pounceStartTime;
                    if (timeSinceStartPounce >= (long)this.pounceChargeTime) {
                        if (!this.getLevel().collides((Shape)new MovedRectangle(this, this.pounceTarget.getX(), this.pounceTarget.getY()), this.modifyChasingCollisionFilter(this.getLevelCollisionFilter(), this.pounceTarget))) {
                            this.ai.blackboard.put("currentTarget", this.pounceTarget);
                            this.ai.blackboard.put("chaserTarget", this.pounceTarget);
                            this.pounceAbility.runAndSend(this.pounceTarget.getX(), this.pounceTarget.getY());
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
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.giantSwampSlime, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public Point getPathMoveOffset() {
        return new Point(32, 32);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int spriteX;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GiantSwampSlimeMob.getTileCoordinate(x), GiantSwampSlimeMob.getTileCoordinate(y));
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
        final TextureDrawOptionsEnd options = MobRegistry.Textures.giantSwampSlime.initDraw().sprite(spriteX, inLiquid ? 1 : 0, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(GiantSwampSlimeMob.getTileCoordinate(x), GiantSwampSlimeMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.giantSwampSlime_shadow.initDraw().sprite(spriteX, 0, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }

    public static class GiantSwampSlimeAI
    extends SelectorAINode<GiantSwampSlimeMob> {
        public GiantSwampSlimeAI(int searchDistance, GameDamage damage, int knockback, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int attackCooldown, int attackDistance, int wanderFrequency) {
            this.addChild(new EscapeAINode<GiantSwampSlimeMob>(){

                @Override
                public boolean shouldEscape(GiantSwampSlimeMob mob, Blackboard<GiantSwampSlimeMob> blackboard) {
                    return mob.isHostile && !mob.isSummoned && mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING) != false;
                }
            });
            CollisionPlayerChaserAI chaser = new CollisionPlayerChaserAI(searchDistance, damage, knockback);
            CooldownAttackTargetAINode<GiantSwampSlimeMob> cooldownAttackNode = new CooldownAttackTargetAINode<GiantSwampSlimeMob>(cooldownTimer, attackCooldown, attackDistance){

                @Override
                public boolean canAttackTarget(GiantSwampSlimeMob mob, Mob target) {
                    if (mob.inLiquid() || mob.isPouncing()) {
                        return false;
                    }
                    if (mob.getDistance(target) < 64.0f) {
                        return false;
                    }
                    return !mob.getLevel().collides((Shape)new MovedRectangle(mob, target.getX(), target.getY()), mob.modifyChasingCollisionFilter(mob.getLevelCollisionFilter(), target));
                }

                @Override
                public boolean attackTarget(GiantSwampSlimeMob mob, Mob target) {
                    mob.pounceTarget = target;
                    mob.startPounceAbility.runAndSend(1000);
                    return true;
                }
            };
            cooldownAttackNode.randomizeAttackTimer();
            chaser.addChild(cooldownAttackNode);
            this.addChild(chaser);
            this.addChild(new WandererAINode(wanderFrequency));
        }
    }
}

