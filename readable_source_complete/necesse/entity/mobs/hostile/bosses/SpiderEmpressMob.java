/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GamePoint3D;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InverseKinematics;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.EmpressAcidProjectile;
import necesse.entity.projectile.EmpressSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;
import necesse.entity.projectile.EmpressWebBallProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEmpressMob
extends FlyingBossMob {
    public static RotationLootItem vinylRotation = RotationLootItem.globalLootRotation(new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), RotationLootItem.globalLootRotation(4, new LootItem("venomousreckoningvinyl"), new LootItem("wrathoftheempressvinyl")));
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(vinylRotation));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("empressmask", 1));
    public static LootTable privateLootTable = new LootTable(new LootItemMultiplierIgnored(uniqueDrops));
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(35000, 46000, 52000, 58000, 69000);
    public static float ACID_LINGER_SECONDS = 15.0f;
    public static float ACID_LINGER_SECONDS_RAGE = 10.0f;
    public static GameDamage collisionDamage = new GameDamage(115.0f);
    public static GameDamage rageCollisionDamage = new GameDamage(140.0f);
    public static GameDamage slashDamage = new GameDamage(115.0f);
    public static GameDamage webBallDamage = new GameDamage(100.0f);
    public static GameDamage acidProjectileDamage = new GameDamage(115.0f);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    private ArrayList<SpiderEmpressLeg> frontLegs;
    private ArrayList<SpiderEmpressLeg> backLegs;
    public SpiderEmpressArm leftArm = new SpiderEmpressArm(this, true);
    public SpiderEmpressArm rightArm = new SpiderEmpressArm(this, false);
    public float currentHeight;
    public boolean isRaging = false;
    public float rageProgress = 0.0f;
    public float passiveRageGenerationTime = 60.0f;
    public float passiveRageDecayTime = 30.0f;
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    protected final BooleanMobAbility setRagingAbility;
    protected final EmptyMobAbility setAcidBreathAnimationAbility;
    public boolean isAcidBreathing;
    public int acidBreathingParticleTimer;
    protected final EmptyMobAbility setWebVolleyAnimationAbility;
    protected final EmptyMobAbility setScreenSlashWarningAnimationAbility;
    protected final EmptyMobAbility setScreenSlashAttackAnimationAbility;
    protected final EmptyMobAbility setIdleAttackAnimationAbility;
    protected final EmptyMobAbility roarAbility;
    protected final EmptyMobAbility slashSoundAbility;
    protected final EmptyMobAbility acidSoundAbility;
    protected final EmptyMobAbility chargeSoundAbility;
    protected final EmptyMobAbility webBallSoundAbility;

    public SpiderEmpressMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(120.0f);
        this.setArmor(20);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-40, -30, 80, 80);
        this.hitBox = new Rectangle(-30, -90, 60, 120);
        this.selectBox = new Rectangle(-75, -120, 150, 200);
        this.setRaging(false);
        this.setRagingAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                SpiderEmpressMob.this.setRaging(value);
            }
        });
        this.setAcidBreathAnimationAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SpiderEmpressMob.this.isAcidBreathing = true;
                SpiderEmpressMob.this.addAcidBreathParticles();
                SpiderEmpressMob.this.leftArm.doAcidBreathAnimation();
                SpiderEmpressMob.this.rightArm.doAcidBreathAnimation();
            }
        });
        this.setWebVolleyAnimationAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SpiderEmpressMob.this.leftArm.doWebVolleyAnimation();
                SpiderEmpressMob.this.rightArm.doWebVolleyAnimation();
            }
        });
        this.setScreenSlashWarningAnimationAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SpiderEmpressMob.this.leftArm.doScreenSlashWarningAnimation();
                SpiderEmpressMob.this.rightArm.doScreenSlashWarningAnimation();
            }
        });
        this.setScreenSlashAttackAnimationAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SpiderEmpressMob.this.leftArm.doScreenSlashAttackAnimation();
                SpiderEmpressMob.this.rightArm.doScreenSlashAttackAnimation();
            }
        });
        this.setIdleAttackAnimationAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SpiderEmpressMob.this.isAcidBreathing = false;
                SpiderEmpressMob.this.leftArm.returnToIdleAnimation();
                SpiderEmpressMob.this.rightArm.returnToIdleAnimation();
            }
        });
        this.roarAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.shears, SoundEffect.globalEffect().pitch(0.2f));
            }
        });
        this.slashSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.magicbolt4, SoundEffect.globalEffect().volume(0.75f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.8f)));
            }
        });
        this.acidSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.spit, SoundEffect.globalEffect().volume(6.0f).pitch(0.45f));
            }
        });
        this.chargeSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().volume(2.0f).pitch(3.0f));
            }
        });
        this.webBallSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SpiderEmpressMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.swing1, SoundEffect.globalEffect().volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.8f)));
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.setRaging(this.isRaging);
        writer.putNextBoolean(this.isAcidBreathing);
        this.leftArm.setupSpawnPacket(writer);
        this.rightArm.setupSpawnPacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isRaging = reader.getNextBoolean();
        this.isAcidBreathing = reader.getNextBoolean();
        this.leftArm.applySpawnPacket(reader);
        this.rightArm.applySpawnPacket(reader);
    }

    @Override
    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    @Override
    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        int beforeHealth = this.getHealth();
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        int deltaHealth = beforeHealth - this.getHealth();
        if (deltaHealth > 0) {
            this.rageProgress += (float)deltaHealth / (float)this.getMaxHealth();
        }
    }

    @Override
    public void setPos(float x, float y, boolean isDirect) {
        super.setPos(x, y, isDirect);
        if (isDirect && this.backLegs != null && this.frontLegs != null) {
            for (SpiderEmpressLeg leg : this.backLegs) {
                leg.snapToPosition();
            }
            for (SpiderEmpressLeg leg : this.frontLegs) {
                leg.snapToPosition();
            }
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SpiderEmpressMob>(this, new SpiderCastleBossAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.spiderempressbegin, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(4000));
        }
        this.currentHeight = this.getDesiredHeight();
        this.frontLegs = new ArrayList();
        this.backLegs = new ArrayList();
        int legCount = 8;
        float[] angles = new float[]{65.0f, 95.0f, 125.0f, 155.0f, -65.0f, -95.0f, -125.0f, -155.0f};
        for (int i = 0; i < legCount; ++i) {
            final float angle = angles[i] - 90.0f;
            boolean mirror = angles[i] > 0.0f;
            float offsetPercent = (float)(i + (i % 2 == 0 ? 4 : 0)) / (float)legCount % 1.0f;
            final Point2D.Float dir = GameMath.getAngleDir(angle);
            float maxLeftAngle = 170.0f;
            float maxRightAngle = 170.0f;
            if (dir.x < 0.0f) {
                maxRightAngle = 0.0f;
            } else if (dir.x > 0.0f) {
                maxLeftAngle = 0.0f;
            }
            SpiderEmpressLeg leg = new SpiderEmpressLeg(this, 80.0f, offsetPercent, maxLeftAngle, maxRightAngle, mirror){

                @Override
                public GamePoint3D getCenterPosition() {
                    int dist = 10;
                    return new GamePoint3D((float)SpiderEmpressMob.this.getDrawX() + dir.x * (float)dist, (float)SpiderEmpressMob.this.getDrawY() + dir.y * (float)dist * 0.2f, SpiderEmpressMob.this.getFlyingHeight() + 30);
                }

                @Override
                public GamePoint3D getDesiredPosition() {
                    Point2D.Float dir2 = GameMath.getAngleDir(angle);
                    int dist = SpiderEmpressMob.this.isRaging ? 90 : 60;
                    float moveMod = Math.min(SpiderEmpressMob.this.getCurrentSpeed() / (SpiderEmpressMob.this.isRaging ? 200.0f : 150.0f), 1.0f);
                    Point2D.Float moveDir = GameMath.normalize(SpiderEmpressMob.this.dx, SpiderEmpressMob.this.dy);
                    if (moveDir.y < 0.0f) {
                        moveMod *= 1.0f + -moveDir.y * 80.0f / SpiderEmpressMob.this.getSpeed();
                    }
                    return new GamePoint3D((float)SpiderEmpressMob.this.getDrawX() + dir2.x * (float)dist + moveDir.x * (float)dist * moveMod, (float)SpiderEmpressMob.this.getDrawY() + dir2.y * (float)dist + moveDir.y * (float)dist * moveMod, 0.0f);
                }

                @Override
                public float getJumpHeight() {
                    return 40.0f;
                }
            };
            if (dir.y < 0.0f) {
                this.backLegs.add(leg);
                continue;
            }
            this.frontLegs.add(leg);
        }
        this.frontLegs.sort(Comparator.comparingDouble(l -> l.y));
        this.backLegs.sort(Comparator.comparingDouble(l -> l.y));
    }

    @Override
    public void tickMovement(float delta) {
        float desiredHeight = this.getDesiredHeight();
        float heightDelta = desiredHeight - this.currentHeight;
        float heightSpeed = Math.abs(heightDelta) * 2.0f + 10.0f;
        float heightToMove = heightSpeed * delta / 250.0f;
        this.currentHeight = Math.abs(heightDelta) < heightToMove ? desiredHeight : (this.currentHeight += Math.signum(heightDelta) * heightToMove);
        super.tickMovement(delta);
        for (SpiderEmpressLeg leg : this.backLegs) {
            leg.tickMovement(delta);
        }
        for (SpiderEmpressLeg leg : this.frontLegs) {
            leg.tickMovement(delta);
        }
        this.leftArm.tickMovement(delta);
        this.rightArm.tickMovement(delta);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.WrathOfTheEmpress, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        if (this.isRaging) {
            this.addRageParticles();
        }
        if (this.isAcidBreathing) {
            this.acidBreathingParticleTimer += 50;
            while (this.acidBreathingParticleTimer >= 25) {
                this.addAcidBreathParticles();
                this.acidBreathingParticleTimer -= 25;
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        this.rageProgress = !this.isRaging ? (this.rageProgress += 1.0f / (this.passiveRageGenerationTime * 20.0f)) : (this.rageProgress -= 1.0f / (this.passiveRageDecayTime * 20.0f));
    }

    protected void setRaging(boolean isRaging) {
        this.isRaging = isRaging;
        if (isRaging) {
            this.setArmor(60);
        } else {
            this.setArmor(20);
        }
        if (isRaging && this.isClient()) {
            SoundManager.playSound(GameResources.shears, SoundEffect.globalEffect().pitch(0.2f));
            this.addInitialRageParticles();
        }
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.currentHeight;
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
        float height = GameMath.sin(perc * 360.0f) * 5.0f;
        if (this.isAcidBreathing) {
            return height - 20.0f;
        }
        return 20 + (int)height - (this.isRaging ? 20 : 0);
    }

    @Override
    public int getRespawnTime() {
        if (this.isSummoned) {
            return BossMob.getBossRespawnTime(this);
        }
        return super.getRespawnTime();
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.spiderempresshurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.spiderempressdeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.isRaging ? rageCollisionDamage : collisionDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void setFacingDir(float deltaX, float deltaY) {
        if (deltaX < 0.0f) {
            this.setDir(0);
        } else if (deltaX > 0.0f) {
            this.setDir(1);
        }
    }

    private void addInitialRageParticles() {
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 9.0f;
        for (int i = 0; i < 40; ++i) {
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
            this.getLevel().entityManager.addParticle(this, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 3), 0, 22)).sizeFades(30, 100).movesFriction(dx, dy, 0.8f).color(new Color(228, 92, 95)).heightMoves(0.0f, 10.0f).lifeTime(1500);
        }
    }

    private void addRageParticles() {
        int i;
        GameRandom random = GameRandom.globalRandom;
        for (i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(this, (float)random.getIntBetween(-25, 25), (float)random.getIntBetween(-20, 20) * 0.75f - 25.0f, Particle.GType.CRITICAL).color(new Color(228, 92, 95)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).movesConstant(0.0f, -5.0f).height(75.0f).lifeTime(1000).givesLight(0.0f, 75.0f).sizeFades(32, 48);
        }
        for (i = 0; i < 1; ++i) {
            this.getLevel().entityManager.addParticle(this.x + (float)random.getIntBetween(-25, 25), this.y + (float)random.getIntBetween(-20, 20) * 0.75f, Particle.GType.CRITICAL).color(new Color(228, 92, 95)).movesConstant(0.0f, -5.0f).height(75.0f).lifeTime(1000).givesLight(0.0f, 75.0f).sizeFades(16, 24);
        }
    }

    public void addAcidBreathParticles() {
        for (int i = 0; i < 5; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
            float startX = this.x + dir.x * range;
            float startY = this.y + 20.0f;
            float endHeight = 29.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            this.getLevel().entityManager.addTopParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 26).rotates().height(this.currentHeight).heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(new Color(166, 204, 52)).givesLight(74.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(lifeTime);
            this.getLevel().entityManager.addTopParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().height(this.currentHeight).heightMoves(startHeight, endHeight).movesConstant(speed * 2.0f, 0.0f).color(new Color(166, 204, 52)).givesLight(74.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 7; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.spiderEmpressDebris, i, 0, 32, this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 15.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture textureDress;
        GameTexture textureTorso;
        GameTexture textureHead;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderEmpressMob.getTileCoordinate(x), SpiderEmpressMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        drawY -= this.getFlyingHeight();
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        if (this.isRaging) {
            textureHead = MobRegistry.Textures.spiderEmpressRageHead;
            textureTorso = MobRegistry.Textures.spiderEmpressRageTorso;
            textureDress = MobRegistry.Textures.spiderEmpressRageDress;
        } else {
            textureHead = MobRegistry.Textures.spiderEmpressHead;
            textureTorso = MobRegistry.Textures.spiderEmpressTorso;
            textureDress = MobRegistry.Textures.spiderEmpressDress;
        }
        TextureDrawOptionsEnd dress = textureDress.initDraw().light(light).rotate(rotate * 4.0f, MobRegistry.Textures.spiderEmpressDress.getWidth() / 2, MobRegistry.Textures.spiderEmpressDress.getHeight()).posMiddle(drawX - (int)(rotate * 2.5f), drawY + 20 - 57 + (int)(rotate <= 0.0f ? rotate : -rotate));
        TextureDrawOptionsEnd body = textureTorso.initDraw().light(light).rotate(rotate * 2.0f, MobRegistry.Textures.spiderEmpressTorso.getWidth() / 2, MobRegistry.Textures.spiderEmpressTorso.getHeight()).posMiddle(drawX, drawY - 18 - 57);
        TextureDrawOptionsEnd head = textureHead.initDraw().light(light).rotate(rotate * 0.6f, MobRegistry.Textures.spiderEmpressHead.getWidth() / 2, MobRegistry.Textures.spiderEmpressHead.getHeight() + 150).posMiddle(drawX, drawY - 54 - 57);
        DrawOptionsList legsShadows = new DrawOptionsList();
        DrawOptionsList backLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList backLegsDrawsTop = new DrawOptionsList();
        DrawOptionsList frontLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList frontLegsDrawsTop = new DrawOptionsList();
        for (SpiderEmpressLeg leg : this.backLegs) {
            leg.addDrawOptions(legsShadows, backLegsDrawBottom, backLegsDrawsTop, level, camera, this.isRaging);
        }
        for (SpiderEmpressLeg leg : this.frontLegs) {
            leg.addDrawOptions(legsShadows, frontLegsDrawBottom, frontLegsDrawsTop, level, camera, this.isRaging);
        }
        DrawOptionsList armsDrawHand = new DrawOptionsList();
        DrawOptionsList armsDrawBottom = new DrawOptionsList();
        DrawOptionsList armsDrawTop = new DrawOptionsList();
        this.leftArm.addDrawOptions(armsDrawHand, armsDrawBottom, armsDrawTop, level, x + (int)rotate, y + (int)rotate, camera, this.isRaging);
        this.rightArm.addDrawOptions(armsDrawHand, armsDrawBottom, armsDrawTop, level, x + (int)rotate, y - (int)rotate, camera, this.isRaging);
        topList.add(tm -> {
            legsShadows.draw();
            dress.draw();
            backLegsDrawBottom.draw();
            backLegsDrawsTop.draw();
            frontLegsDrawBottom.draw();
            frontLegsDrawsTop.draw();
            armsDrawTop.draw();
            body.draw();
            head.draw();
            armsDrawBottom.draw();
            armsDrawHand.draw();
        });
    }

    @Override
    public float getSoundPositionY() {
        return super.getSoundPositionY() - 40.0f;
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 42;
        MobRegistry.Textures.spiderEmpressHead.initDraw().sprite(0, 0, 62, 54).size(48, 48).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-24, -34, 48, 34);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedProjectiles.clear();
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        super.addHoverTooltips(tooltips, debug);
        if (debug) {
            tooltips.add("Raging: " + this.isRaging);
        }
    }

    public static class SpiderEmpressArm {
        private final SpiderEmpressMob mob;
        private final boolean isLeftHand;
        private boolean targetReached;
        private boolean isAttacking;
        private float handRotation;
        private float targetHandRotation;
        private float moveSpeed = 50.0f;
        private float targetX;
        private float targetY;
        public float currentX;
        public float currentY;
        public InverseKinematics limbs;

        public SpiderEmpressArm(SpiderEmpressMob mob, boolean isLeftHand) {
            this.isLeftHand = isLeftHand;
            this.mob = mob;
            this.targetX = isLeftHand ? 50.0f : -50.0f;
            this.targetY = -100.0f;
            this.currentX = this.targetX;
            this.currentY = this.targetY;
            this.handRotation = 0.0f;
            this.targetHandRotation = 0.0f;
            this.isAttacking = false;
            if (isLeftHand) {
                this.limbs = InverseKinematics.startFromPoints(15.0f, -90.0f, 35.0f, -90.0f, 140.0f, -10.0f);
                this.limbs.addJointPoint(65.0f, -90.0f);
            } else {
                this.limbs = InverseKinematics.startFromPoints(-15.0f, -90.0f, -35.0f, -90.0f, -10.0f, 140.0f);
                this.limbs.addJointPoint(-65.0f, -90.0f);
            }
        }

        public void setupSpawnPacket(PacketWriter writer) {
            writer.putNextFloat(this.currentX);
            writer.putNextFloat(this.currentY);
            writer.putNextFloat(this.targetX);
            writer.putNextFloat(this.targetY);
            writer.putNextFloat(this.handRotation);
            writer.putNextFloat(this.targetHandRotation);
        }

        public void applySpawnPacket(PacketReader reader) {
            this.currentX = reader.getNextFloat();
            this.currentY = reader.getNextFloat();
            this.targetX = reader.getNextFloat();
            this.targetY = reader.getNextFloat();
            this.handRotation = reader.getNextFloat();
            this.targetHandRotation = reader.getNextFloat();
        }

        public void setTarget(float targetX, float targetY, float targetHandRotation) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetHandRotation = targetHandRotation;
            this.targetReached = false;
        }

        public void tickMovement(float delta) {
            double dist = GameMath.preciseDistance(this.targetX, this.targetY, this.currentX, this.currentY);
            if (dist == 0.0) {
                this.targetReached = true;
                if (!this.isAttacking) {
                    this.doIdleAnimation();
                }
            } else {
                float distToMove = this.moveSpeed * delta / 250.0f;
                if (dist <= (double)distToMove) {
                    this.currentX = this.targetX;
                    this.currentY = this.targetY;
                    this.targetReached = true;
                } else {
                    Point2D.Float dir = GameMath.normalize(this.targetX - this.currentX, this.targetY - this.currentY);
                    this.currentX += dir.x * distToMove;
                    this.currentY += dir.y * distToMove;
                }
                this.limbs.apply(this.currentX, this.currentY, 0.1f, 0.5f, 100);
            }
            if (this.targetHandRotation == this.handRotation) {
                return;
            }
            float handMoveSpeed = this.moveSpeed * delta / 250.0f;
            float deltaHandRotation = this.targetHandRotation - this.handRotation;
            this.handRotation = Math.abs(deltaHandRotation) < handMoveSpeed ? this.targetHandRotation : (this.handRotation += handMoveSpeed * Math.signum(deltaHandRotation));
        }

        public void doAcidBreathAnimation() {
            this.moveSpeed = 100.0f;
            this.isAttacking = true;
            this.setTarget(this.isLeftHand ? 20.0f : -20.0f, 0.0f, this.isLeftHand ? 20.0f : -20.0f);
        }

        public void doWebVolleyAnimation() {
            this.moveSpeed = 100.0f;
            this.isAttacking = true;
            this.setTarget(this.isLeftHand ? 30.0f : -30.0f, -40.0f, this.isLeftHand ? 90.0f : -90.0f);
        }

        public void doScreenSlashWarningAnimation() {
            this.moveSpeed = 100.0f;
            this.isAttacking = true;
            this.setTarget(this.isLeftHand ? 60.0f : -60.0f, -150.0f, this.isLeftHand ? -130.0f : 130.0f);
        }

        public void doScreenSlashAttackAnimation() {
            this.moveSpeed = 200.0f;
            this.setTarget(this.isLeftHand ? 40.0f : -40.0f, -10.0f, this.isLeftHand ? 20.0f : -20.0f);
        }

        public void returnToIdleAnimation() {
            this.moveSpeed = 30.0f;
            this.isAttacking = false;
            this.setTarget(this.isLeftHand ? 50.0f : -50.0f, -100.0f, 0.0f);
        }

        public void doIdleAnimation() {
            if (this.targetReached) {
                if ((this.currentX == 60.0f || this.currentX == -60.0f) && this.currentY == -75.0f) {
                    this.setTarget(this.isLeftHand ? 50.0f : -50.0f, -100.0f, 0.0f);
                } else {
                    this.setTarget(this.isLeftHand ? 60.0f : -60.0f, -75.0f, 0.0f);
                }
                this.moveSpeed = 10.0f;
            }
        }

        public void addDrawOptions(DrawOptionsList armsDrawHand, DrawOptionsList armsDrawBottom, DrawOptionsList armsDrawTop, Level level, int x, int y, GameCamera camera, boolean isRaging) {
            if (this.mob.isServer()) {
                return;
            }
            int currentHeight = (int)this.mob.currentHeight;
            for (InverseKinematics.Limb limb : this.limbs.limbs) {
                int textureHeight;
                int textureWidth;
                GameTexture texture;
                GameLight light = level.getLightLevel(Entity.getTileCoordinate(x), Entity.getTileCoordinate(y));
                if (this.limbs.limbs.getLast() == limb) {
                    TextureDrawOptionsEnd handDrawOptions;
                    TextureDrawOptionsEnd drawOptions;
                    texture = isRaging ? MobRegistry.Textures.spiderEmpressRageArmBottom : MobRegistry.Textures.spiderEmpressArmBottom;
                    textureWidth = texture.getWidth();
                    textureHeight = texture.getHeight();
                    GameTexture handTexture = isRaging ? MobRegistry.Textures.spiderEmpressRageHand : MobRegistry.Textures.spiderEmpressHand;
                    int handTextureWidth = handTexture.getWidth();
                    int handTextureHeight = handTexture.getHeight();
                    if (this.isLeftHand) {
                        drawOptions = texture.initDraw().mirrorX().rotate(limb.angle + 20.0f, 4, textureHeight - 4).light(light).size(textureWidth, textureHeight).pos(camera.getDrawX(limb.inboundX - 4.0f) + x, camera.getDrawY(limb.inboundY - (float)textureHeight / 2.0f - 4.0f) + y - currentHeight);
                        handDrawOptions = handTexture.initDraw().mirrorX().rotate(this.handRotation, 10, 8).light(light).size(handTextureWidth, handTextureHeight).pos(camera.getDrawX(limb.outboundX - 10.0f) + x, camera.getDrawY(limb.outboundY - 8.0f) + y - currentHeight);
                        armsDrawHand.add(handDrawOptions);
                    } else {
                        drawOptions = texture.initDraw().rotate(limb.angle - 200.0f, textureWidth - 4, textureHeight - 4).light(light).size(textureWidth, textureHeight).pos(camera.getDrawX(limb.inboundX - (float)(textureWidth - 4)) + x, camera.getDrawY(limb.inboundY - (float)textureHeight / 2.0f - 4.0f) + y - currentHeight);
                        handDrawOptions = handTexture.initDraw().rotate(this.handRotation, handTextureWidth - 10, 8).light(light).size(handTextureWidth, handTextureHeight).pos(camera.getDrawX(limb.outboundX - (float)(handTextureWidth - 10)) + x, camera.getDrawY(limb.outboundY - 8.0f) + y - currentHeight);
                    }
                    armsDrawBottom.add(drawOptions);
                    armsDrawHand.add(handDrawOptions);
                    continue;
                }
                texture = isRaging ? MobRegistry.Textures.spiderEmpressRageArmTop : MobRegistry.Textures.spiderEmpressArmTop;
                textureWidth = texture.getWidth();
                textureHeight = texture.getHeight();
                TextureDrawOptionsEnd drawOptions = this.isLeftHand ? texture.initDraw().mirrorX().rotate(limb.angle - 45.0f, 4, 4).light(light).size(textureWidth, textureHeight).pos(camera.getDrawX(limb.inboundX - 4.0f) + x, camera.getDrawY(limb.inboundY - 4.0f) + y - currentHeight) : texture.initDraw().rotate(limb.angle - 135.0f, textureWidth - 4, 4).light(light).size(textureWidth, textureHeight).pos(camera.getDrawX(limb.inboundX - (float)(textureWidth - 4)) + x, camera.getDrawY(limb.inboundY - 4.0f) + y - currentHeight);
                armsDrawTop.add(drawOptions);
            }
            if (GlobalData.debugActive()) {
                Color color = this.isLeftHand ? Color.BLUE : Color.RED;
                armsDrawTop.add(() -> this.limbs.drawDebug(camera, x, y - currentHeight, color, Color.GREEN));
            }
        }
    }

    public static abstract class SpiderEmpressLeg {
        public final Mob mob;
        public float startX;
        public float startY;
        public float x;
        public float y;
        public float nextX;
        public float nextY;
        public boolean isMoving;
        private InverseKinematics ik;
        private List<Line2D.Float> shadowLines;
        public float maxLeftAngle;
        public float maxRightAngle;
        private final float moveAtDist;
        private float checkX;
        private float checkY;
        private float distBuffer;
        private final boolean mirror;

        public SpiderEmpressLeg(Mob mob, float moveAtDist, float offsetPercent, float maxLeftAngle, float maxRightAngle, boolean mirror) {
            this.mob = mob;
            this.moveAtDist = moveAtDist;
            this.distBuffer = moveAtDist * offsetPercent;
            this.maxLeftAngle = maxLeftAngle;
            this.maxRightAngle = maxRightAngle;
            this.mirror = mirror;
            this.snapToPosition();
            this.checkX = this.x;
            this.checkY = this.y;
        }

        public void snapToPosition() {
            GamePoint3D desiredPosition = this.getDesiredPosition();
            this.x = desiredPosition.x;
            this.y = desiredPosition.y;
            this.nextX = desiredPosition.x;
            this.nextY = desiredPosition.y;
            this.updateIK();
        }

        public void tickMovement(float delta) {
            GamePoint3D centerPos = this.getCenterPosition();
            double checkDist = new Point2D.Float(centerPos.x, centerPos.y).distance(this.checkX, this.checkY);
            this.distBuffer = (float)((double)this.distBuffer + checkDist);
            this.checkX = centerPos.x;
            this.checkY = centerPos.y;
            if (checkDist == 0.0) {
                this.distBuffer += delta / (this.moveAtDist / 5.0f);
            }
            if (!this.isMoving) {
                GamePoint3D desiredPos = this.getDesiredPosition();
                double desiredDist = new Point2D.Float(desiredPos.x, desiredPos.y).distance(this.x, this.y);
                if (desiredDist > 175.0) {
                    this.distBuffer += this.moveAtDist;
                }
                if (this.distBuffer >= this.moveAtDist) {
                    this.distBuffer -= this.moveAtDist;
                    if (this.x != desiredPos.x || this.y != desiredPos.y) {
                        this.startX = this.x;
                        this.startY = this.y;
                        this.nextX = desiredPos.x;
                        this.nextY = desiredPos.y;
                        this.isMoving = true;
                    }
                }
            }
            if (this.isMoving) {
                float speed;
                float distToMove;
                double nextDist = new Point2D.Float(this.x, this.y).distance(this.nextX, this.nextY);
                if (nextDist < (double)(distToMove = (speed = (float)nextDist * 2.0f + this.mob.getSpeed() * 1.2f) * delta / 250.0f)) {
                    if (this.mob.isClient()) {
                        SoundManager.playSound(GameResources.tap2, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 0.75f)));
                    }
                    this.x = this.nextX;
                    this.y = this.nextY;
                    this.isMoving = false;
                } else {
                    Point2D.Float dir = GameMath.normalize(this.nextX - this.x, this.nextY - this.y);
                    this.x += dir.x * distToMove;
                    this.y += dir.y * distToMove;
                }
            }
            this.updateIK();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void updateIK() {
            if (this.mob.isServer()) {
                return;
            }
            GamePoint3D centerPos = this.getCenterPosition();
            Point2D.Float dir = GameMath.normalize(this.x - centerPos.x, this.y - centerPos.y);
            float jointDistMod = 0.5f;
            float dist = (float)new Point2D.Float(centerPos.x, centerPos.y).distance(this.x, this.y) * jointDistMod;
            float jointHeight = 20.0f;
            GamePoint3D jointPos = centerPos.dirFromLength(centerPos.x + dir.x * dist, centerPos.y + dir.y * dist, jointHeight, 40.0f);
            GamePoint3D footPos = jointPos.dirFromLength(this.x, this.y, 0.0f, 70.0f);
            float jumpHeight = this.getJumpHeight();
            float perspectiveMod = 0.6f;
            SpiderEmpressLeg spiderEmpressLeg = this;
            synchronized (spiderEmpressLeg) {
                this.shadowLines = Collections.synchronizedList(new LinkedList());
                this.shadowLines.add(new Line2D.Float(centerPos.x, centerPos.y + 18.0f, jointPos.x, jointPos.y - jointPos.height * perspectiveMod));
                this.shadowLines.add(new Line2D.Float(jointPos.x, jointPos.y - jointPos.height * perspectiveMod, this.x, this.y));
            }
            this.ik = InverseKinematics.startFromPoints(centerPos.x, centerPos.y - centerPos.height * perspectiveMod - jumpHeight, jointPos.x, jointPos.y - jointPos.height * perspectiveMod - jumpHeight, this.maxLeftAngle, this.maxRightAngle);
            this.ik.addJointPoint(footPos.x, footPos.y - footPos.height * perspectiveMod - jumpHeight);
            this.ik.apply(this.x, this.y - this.getCurrentLegLift() - Math.max(jumpHeight - 150.0f, jumpHeight / 4.0f), 0.0f, 2.0f, 500);
        }

        public float getCurrentLegLift() {
            double startDist = new Point2D.Float(this.startX, this.startY).distance(this.nextX, this.nextY);
            float lift = Math.min((float)startDist / 40.0f, 1.0f) * 20.0f + 5.0f;
            double currentDist = new Point2D.Float(this.x, this.y).distance(this.nextX, this.nextY);
            double progress = GameMath.limit(currentDist / startDist, 0.0, 1.0);
            return GameMath.sin((float)progress * 180.0f) * lift;
        }

        public abstract float getJumpHeight();

        public abstract GamePoint3D getDesiredPosition();

        public abstract GamePoint3D getCenterPosition();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addDrawOptions(DrawOptionsList legsShadows, DrawOptionsList legsDrawBottom, DrawOptionsList legsDrawsTop, Level level, GameCamera camera, boolean isRaging) {
            TextureDrawOptionsEnd drawOptions;
            if (this.mob.isServer()) {
                return;
            }
            int legShadowWidth = MobRegistry.Textures.queenSpiderLeg_shadow.getWidth() / 2;
            SpiderEmpressLeg spiderEmpressLeg = this;
            synchronized (spiderEmpressLeg) {
                for (Line2D.Float shadowLine : this.shadowLines) {
                    GameLight light = level.getLightLevel(Entity.getTileCoordinate((shadowLine.x1 + shadowLine.x2) / 2.0f), Entity.getTileCoordinate((shadowLine.y1 + shadowLine.y2) / 2.0f));
                    float angle = GameMath.getAngle(new Point2D.Float(shadowLine.x1 - shadowLine.x2, shadowLine.y1 - shadowLine.y2));
                    float length = (float)shadowLine.getP1().distance(shadowLine.getP2());
                    drawOptions = MobRegistry.Textures.queenSpiderLeg_shadow.initDraw().rotate(angle + 90.0f, legShadowWidth / 2, 6).light(light).size(legShadowWidth, (int)length + 16).pos(camera.getDrawX(shadowLine.x1 - (float)legShadowWidth / 2.0f), camera.getDrawY(shadowLine.y1));
                    legsShadows.add(drawOptions);
                }
            }
            float jumpHeight = this.getJumpHeight();
            for (InverseKinematics.Limb limb : this.ik.limbs) {
                GameTexture texture;
                int legTextureWidth = MobRegistry.Textures.spiderEmpressLegBottom.getWidth() + 2;
                GameLight light = level.getLightLevel(Entity.getTileCoordinate((limb.inboundX + limb.outboundX) / 2.0f), Entity.getTileCoordinate((limb.inboundY + limb.outboundY) / 2.0f + jumpHeight));
                if (this.ik.limbs.getLast() == limb) {
                    texture = isRaging ? MobRegistry.Textures.spiderEmpressRageLegBottom : MobRegistry.Textures.spiderEmpressLegBottom;
                    drawOptions = this.mirror ? texture.initDraw().mirrorX().rotate(limb.angle - 90.0f, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0f), camera.getDrawY(limb.inboundY) - 8) : texture.initDraw().rotate(limb.angle - 90.0f, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0f), camera.getDrawY(limb.inboundY) - 8);
                    legsDrawBottom.add(drawOptions);
                    continue;
                }
                texture = isRaging ? MobRegistry.Textures.spiderEmpressRageLegTop : MobRegistry.Textures.spiderEmpressLegTop;
                legTextureWidth = texture.getWidth();
                drawOptions = this.mirror ? texture.initDraw().mirrorX().rotate(limb.angle - 90.0f, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0f), camera.getDrawY(limb.inboundY) - 8) : texture.initDraw().rotate(limb.angle - 90.0f, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0f), camera.getDrawY(limb.inboundY) - 8);
                legsDrawsTop.add(drawOptions);
            }
            if (GlobalData.debugActive()) {
                legsDrawsTop.add(() -> Renderer.drawCircle(camera.getDrawX(this.x), camera.getDrawY(this.y - this.getCurrentLegLift()), 4, 12, 1.0f, 0.0f, 0.0f, 1.0f, false));
                legsDrawsTop.add(() -> this.ik.drawDebug(camera, Color.RED, Color.GREEN));
            }
        }
    }

    public static class SpiderCastleBossAI<T extends SpiderEmpressMob>
    extends SequenceAINode<T> {
        public SpiderCastleBossAI() {
            int i;
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            int maxIdleTime = 500;
            int minIdleTime = 100;
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<SpiderEmpressMob>(new IsolateRunningAINode(attackStages), m -> !m.isRaging, AINodeResult.SUCCESS));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new ScreenSlashStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 1500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new WebVolleyWindupStage(minIdleTime, maxIdleTime));
            attackStages.addChild(new WebVolleyStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new ScreenSlashStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new AcidWaveWindupStage(minIdleTime, maxIdleTime));
            attackStages.addChild(new AcidWaveStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild(new CheckRageTransitionStage());
            attackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            maxIdleTime = 400;
            minIdleTime = 50;
            AttackStageManagerNode rageAttackStages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<SpiderEmpressMob>(new IsolateRunningAINode(rageAttackStages), m -> m.isRaging, AINodeResult.SUCCESS));
            rageAttackStages.addChild(new FlyToRandomPositionAttackStage(true, 400));
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, maxIdleTime)));
            for (i = 0; i < 3; ++i) {
                rageAttackStages.addChild(new ScreenSlashStage());
            }
            rageAttackStages.addChild(new CheckRageTransitionStage());
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 1500)));
            rageAttackStages.addChild(new FlyToRandomPositionAttackStage(true, 400));
            rageAttackStages.addChild(new WebVolleyWindupStage(minIdleTime, maxIdleTime));
            rageAttackStages.addChild(new WebVolleyStage());
            rageAttackStages.addChild(new FlyToRandomPositionAttackStage(true, 400));
            rageAttackStages.addChild(new WebVolleyWindupStage(minIdleTime, maxIdleTime));
            rageAttackStages.addChild(new WebVolleyStage());
            rageAttackStages.addChild(new CheckRageTransitionStage());
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            for (i = 0; i < 2; ++i) {
                rageAttackStages.addChild(new ChargeTargetStage());
            }
            rageAttackStages.addChild(new CheckRageTransitionStage());
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            rageAttackStages.addChild(new FlyToRandomPositionAttackStage(true, 400));
            rageAttackStages.addChild(new AcidWaveWindupStage(minIdleTime, maxIdleTime));
            rageAttackStages.addChild(new AcidWaveStage());
            rageAttackStages.addChild(new CheckRageTransitionStage());
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
            rageAttackStages.addChild(new FlyToRandomPositionAttackStage(true, 100));
            rageAttackStages.addChild(new AcidWaveWindupStage(minIdleTime, maxIdleTime));
            rageAttackStages.addChild(new AcidWaveStage());
            for (i = 0; i < 2; ++i) {
                rageAttackStages.addChild(new ChargeTargetStage());
            }
            rageAttackStages.addChild(new CheckRageTransitionStage());
            rageAttackStages.addChild(new IdleTimeAttackStage<SpiderEmpressMob>(m -> this.getIdleTime(m, 500)));
        }

        private int getIdleTime(T mob, int maxTime) {
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((SpiderEmpressMob)mob).getMaxHealth();
            return (int)((float)maxTime * healthPerc);
        }
    }

    public static class AcidWaveStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private boolean useRageVariant;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.useRageVariant = ((SpiderEmpressMob)mob).isRaging;
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((SpiderEmpressMob)mob).getMaxHealth();
                float healthPercInv = Math.abs(healthPerc - 1.0f);
                if (this.useRageVariant) {
                    float speed = 120.0f + healthPercInv * 60.0f;
                    for (int i = -1; i <= 1; ++i) {
                        EmpressAcidProjectile projectile = new EmpressAcidProjectile(((Entity)mob).getLevel(), ((SpiderEmpressMob)mob).x, ((SpiderEmpressMob)mob).y, target.x, target.y, speed, 1000, acidProjectileDamage, (Mob)mob);
                        projectile.setAngle(projectile.getAngle() + (float)(10 * i));
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                        ((SpiderEmpressMob)mob).spawnedProjectiles.add(projectile);
                    }
                    ((SpiderEmpressMob)mob).acidSoundAbility.runAndSend();
                } else {
                    float speed = 120.0f + healthPercInv * 40.0f;
                    EmpressAcidProjectile projectile = new EmpressAcidProjectile(((Entity)mob).getLevel(), ((SpiderEmpressMob)mob).x, ((SpiderEmpressMob)mob).y, target.x, target.y, speed, 1000, acidProjectileDamage, (Mob)mob);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    ((SpiderEmpressMob)mob).spawnedProjectiles.add(projectile);
                    ((SpiderEmpressMob)mob).acidSoundAbility.runAndSend();
                }
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.FAILURE;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class AcidWaveWindupStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private float timer;
        private final Function<T, Integer> duration;

        public AcidWaveWindupStage(Function<T, Integer> duration) {
            this.duration = duration;
        }

        public AcidWaveWindupStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> {
                int delta = fullHealthIdleTime - noHealthIdleTime;
                float healthPerc = (float)m.getHealth() / (float)m.getMaxHealth();
                return noHealthIdleTime + (int)((float)delta * healthPerc);
            });
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0.0f;
            ((SpiderEmpressMob)mob).setAcidBreathAnimationAbility.runAndSend();
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.timer += 50.0f;
            if (this.timer > (float)this.duration.apply(mob).intValue()) {
                ((SpiderEmpressMob)mob).setIdleAttackAnimationAbility.runAndSend();
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class ChargeTargetStage<T extends SpiderEmpressMob>
    extends FlyToOppositeDirectionAttackStage<T> {
        public ChargeTargetStage() {
            super(true, 250.0f, 0.0f);
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            super.onStarted(mob, blackboard);
            if (blackboard.mover.isMoving()) {
                ((SpiderEmpressMob)mob).chargeSoundAbility.runAndSend();
                ((SpiderEmpressMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, (Mob)mob, 5.0f, null), true);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
            super.onEnded(mob, blackboard);
            ((SpiderEmpressMob)mob).buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
        }
    }

    public static class WebVolleyStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private boolean useRageVariant;
        private boolean initial;
        public int direction;
        private float angleBuffer;
        private float remainingAngle;
        private float totalAngle;
        private float currentAngleLeft;
        private float currentAngleRight;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.useRageVariant = ((SpiderEmpressMob)mob).isRaging;
            this.initial = true;
            this.angleBuffer = 0.0f;
            this.totalAngle = 180.0f;
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            float anglePerProjectile;
            if (this.initial) {
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                if (target != null) {
                    this.currentAngleLeft = -6.0f;
                    this.currentAngleRight = 6.0f;
                    this.remainingAngle = this.totalAngle;
                    this.initial = false;
                } else {
                    return AINodeResult.SUCCESS;
                }
            }
            this.angleBuffer += this.totalAngle * 50.0f / 500.0f;
            float f = anglePerProjectile = this.useRageVariant ? 10.0f : 15.0f;
            while (this.angleBuffer >= anglePerProjectile) {
                this.currentAngleLeft += anglePerProjectile;
                EmpressWebBallProjectile projectileLeft = new EmpressWebBallProjectile(((SpiderEmpressMob)mob).x, ((SpiderEmpressMob)mob).y, this.currentAngleLeft, webBallDamage, 120.0f, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectileLeft);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(projectileLeft);
                ((SpiderEmpressMob)mob).webBallSoundAbility.runAndSend();
                this.currentAngleRight -= anglePerProjectile;
                EmpressWebBallProjectile projectileRight = new EmpressWebBallProjectile(((SpiderEmpressMob)mob).x, ((SpiderEmpressMob)mob).y, this.currentAngleRight, webBallDamage, 120.0f, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectileRight);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(projectileRight);
                this.angleBuffer -= anglePerProjectile;
                this.remainingAngle -= anglePerProjectile;
                ((SpiderEmpressMob)mob).webBallSoundAbility.runAndSend();
                if (!(this.remainingAngle < 1.0f)) continue;
                break;
            }
            if (this.angleBuffer >= this.remainingAngle) {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class WebVolleyWindupStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private float timer;
        private final Function<T, Integer> duration;

        public WebVolleyWindupStage(Function<T, Integer> duration) {
            this.duration = duration;
        }

        public WebVolleyWindupStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> {
                int delta = fullHealthIdleTime - noHealthIdleTime;
                float healthPerc = (float)m.getHealth() / (float)m.getMaxHealth();
                return noHealthIdleTime + (int)((float)delta * healthPerc);
            });
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0.0f;
            ((SpiderEmpressMob)mob).setWebVolleyAnimationAbility.runAndSend();
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.timer += 50.0f;
            if (this.timer > (float)this.duration.apply(mob).intValue()) {
                ((SpiderEmpressMob)mob).setIdleAttackAnimationAbility.runAndSend();
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class ScreenSlashStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private int slashesRemaining;
        private final ArrayList<Float> attackAngles = new ArrayList();
        private final ArrayList<Point> attackPos = new ArrayList();
        private float timeSinceStart = 0.0f;
        private float timeBeforeSlash;
        private float slashBuffer;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.slashesRemaining = ((SpiderEmpressMob)mob).isRaging ? 20 : 10;
            this.timeSinceStart = 0.0f;
            this.slashBuffer = 0.0f;
            ((SpiderEmpressMob)mob).setScreenSlashWarningAnimationAbility.runAndSend();
            this.calculateTimeBeforeSlash(mob);
        }

        private void calculateTimeBeforeSlash(T mob) {
            float defaultTime = ((SpiderEmpressMob)mob).isRaging ? 750.0f : 1000.0f;
            float healthPercInv = Math.abs(((Mob)mob).getHealthPercent() - 1.0f);
            this.timeBeforeSlash = defaultTime - healthPercInv * 250.0f;
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            float angle;
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target == null) {
                return AINodeResult.RUNNING;
            }
            if (this.timeSinceStart >= this.timeBeforeSlash) {
                ((SpiderEmpressMob)mob).setScreenSlashAttackAnimationAbility.runAndSend();
                if (this.attackAngles.isEmpty()) {
                    ((SpiderEmpressMob)mob).setIdleAttackAnimationAbility.runAndSend();
                    return AINodeResult.SUCCESS;
                }
                angle = this.attackAngles.get(0).floatValue();
                int attackX = this.attackPos.get((int)0).x;
                int attackY = this.attackPos.get((int)0).y;
                EmpressSlashProjectile slashProjectile = new EmpressSlashProjectile(attackX, attackY, angle, slashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectile);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(slashProjectile);
                EmpressSlashProjectile slashProjectileReverse = new EmpressSlashProjectile(attackX, attackY, angle - 180.0f, slashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectileReverse);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(slashProjectileReverse);
                ((SpiderEmpressMob)mob).slashSoundAbility.runAndSend();
                this.attackAngles.remove(0);
                this.attackPos.remove(0);
                if (this.slashesRemaining < 1) {
                    return AINodeResult.RUNNING;
                }
            }
            GameRandom random = GameRandom.globalRandom;
            this.timeSinceStart += 50.0f;
            this.slashBuffer += 50.0f;
            while (this.slashesRemaining > 0 && this.slashBuffer > 50.0f) {
                int randomX = random.getIntBetween(-200, 200);
                int randomY = random.getIntBetween(-200, 200);
                angle = random.getIntBetween(-180, 180);
                EmpressSlashWarningProjectile slashProjectile = new EmpressSlashWarningProjectile((int)(target.x + (float)randomX), (int)(target.y + (float)randomY), angle, slashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectile);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(slashProjectile);
                EmpressSlashWarningProjectile slashProjectileReverse = new EmpressSlashWarningProjectile((int)(target.x + (float)randomX), (int)(target.y + (float)randomY), angle - 180.0f, slashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectileReverse);
                ((SpiderEmpressMob)mob).spawnedProjectiles.add(slashProjectileReverse);
                this.attackAngles.add(Float.valueOf(angle));
                this.attackPos.add(new Point((int)(target.x + (float)randomX), (int)(target.y + (float)randomY)));
                this.slashBuffer -= 50.0f;
                --this.slashesRemaining;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class CheckRageTransitionStage<T extends SpiderEmpressMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (0.0f < ((SpiderEmpressMob)mob).rageProgress && ((SpiderEmpressMob)mob).rageProgress < 1.0f) {
                return AINodeResult.SUCCESS;
            }
            if (((SpiderEmpressMob)mob).rageProgress < 0.0f && ((SpiderEmpressMob)mob).isRaging) {
                ((SpiderEmpressMob)mob).setRagingAbility.runAndSend(false);
            } else if (((SpiderEmpressMob)mob).rageProgress > 1.0f && !((SpiderEmpressMob)mob).isRaging) {
                ((SpiderEmpressMob)mob).setRagingAbility.runAndSend(true);
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
}

