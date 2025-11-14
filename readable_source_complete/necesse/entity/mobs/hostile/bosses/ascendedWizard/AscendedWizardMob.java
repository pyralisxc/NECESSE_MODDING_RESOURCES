/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.AbstractMusicList;
import necesse.engine.CameraShake;
import necesse.engine.MusicList;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.PausableSound;
import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.TeleportResult;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.world.worldEvent.AscendedFlashWorldEvent;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.EnumMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.mobs.hostile.bosses.ArenaEntrancePortalMob;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.PlayerDamageTypeTracker;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedCageRotationStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedChargeTargetStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedGatlingGunStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedOrbRingStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedPushStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedRandomBossAttackStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedWizardOrbitalBeamStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1ArmsDownAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1ArmsUpAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1ChannelingAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1LaughingAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1StartTransformationAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1TransformationAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage2DeathAnimation;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.particle.AscendedCageBombParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.AscendedOrbProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.DamageRelativeRotationLootItem;
import necesse.inventory.lootTable.lootItem.LootInventoryItemSupplier;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.AscendedVoidLevel;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.Region;

public class AscendedWizardMob
extends FlyingBossMob
implements DamageRelativeRotationLootItem.PlayerDamageDealtGetter {
    public static RotationLootItem vinylRotation = RotationLootItem.globalLootRotation(new LootItem("settlementofhaunteddreamsvinyl"), new LootItem("ascendedreturnvinyl"), new LootItem("ascendedmadnessvinyl"), new LootItem("fractureofthevoidvinyl"));
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(vinylRotation), new LootItemMultiplierIgnored(new LootItem("voidvessel")));
    public static DamageRelativeRotationLootItem uniqueDrops = new DamageRelativeRotationLootItem().setMeleeLoot(new LootInventoryItemSupplier(){

        @Override
        public InventoryItem getNewItem(GameRandom random, float lootMultiplier, Object ... extra) {
            InventoryItem item = new InventoryItem("voidclaw");
            item.item.setUpgradeTier(item, 10.0f);
            return item;
        }
    }).setRangedLoot(new LootInventoryItemSupplier(){

        @Override
        public InventoryItem getNewItem(GameRandom random, float lootMultiplier, Object ... extra) {
            InventoryItem item = new InventoryItem("ascendedbow");
            item.item.setUpgradeTier(item, 10.0f);
            return item;
        }
    }).setMagicLoot(new LootInventoryItemSupplier(){

        @Override
        public InventoryItem getNewItem(GameRandom random, float lootMultiplier, Object ... extra) {
            InventoryItem item = new InventoryItem("ascendedstaff");
            item.item.setUpgradeTier(item, 10.0f);
            return item;
        }
    }).setSummonLoot(new LootInventoryItemSupplier(){

        @Override
        public InventoryItem getNewItem(GameRandom random, float lootMultiplier, Object ... extra) {
            InventoryItem item = new InventoryItem("eyeofthevoid");
            item.item.setUpgradeTier(item, 10.0f);
            return item;
        }
    });
    public static LootTable privateLootTable = new LootTable(new LootItemMultiplierIgnored(uniqueDrops), new LootItemMultiplierIgnored(new LootItem("voidphasingstaff")));
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(30000, 38000, 44000, 50000, 55000);
    public static GameDamage collisionDamage = new GameDamage(115.0f);
    public static GameDamage orbRingDamage = new GameDamage(110.0f);
    public static GameDamage boltDamage = new GameDamage(120.0f);
    public static GameDamage bombDamage = new GameDamage(300.0f);
    public static GameDamage slimeCircleDamage = new GameDamage(130.0f);
    public static GameDamage spiderSlashDamage = new GameDamage(115.0f);
    public static GameDamage moonDarkness = new GameDamage(300.0f);
    public static GameDamage sunGauntletDamage = new GameDamage(130.0f);
    public static GameDamage crystalGolemDamage = new GameDamage(110.0f);
    public static GameDamage shardBombDamage = new GameDamage(110.0f);
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected float spinningParticleBuffer;
    protected float hoveringParticleBuffer;
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    public ArrayList<Projectile> spawnedProblematicProjectiles = new ArrayList();
    public ArrayList<LevelEvent> spawnedEvents = new ArrayList();
    public ArrayList<Mob> spawnedMobs = new ArrayList();
    protected boolean isInBlackHolePhase = false;
    public final EmptyMobAbility endBlackHolePhase;
    public final SetShieldedAbility setShieldedAbility;
    public final SpawnOrbRingAbility spawnOrbRingAbility;
    public final AscendedTeleportAbility ascendedTeleportAbility;
    public final StartBeamSpawningAnimationAbility startBeamSpawningAnimationAbility;
    public final StartCageSpawningAnimationAbility startCageAbility;
    public final IntMobAbility startChannelAnimation;
    public final EmptyMobAbility startArmsUpAnimation;
    public final EmptyMobAbility startArmsDownAnimation;
    public final CoordinateMobAbility slashSoundAbility;
    public final CoordinateMobAbility laserBoltSoundAbility;
    public final EmptyMobAbility playLightningBoltSoundAbility;
    public final EnumMobAbility<BossSound> playBossSoundAbility;
    protected long shieldedStartTime;
    protected long shieldedEndTime;
    protected boolean isTransformed;
    protected boolean isTransforming;
    public final CoordinateMobAbility startTransformationAbility;
    public final EmptyMobAbility markTransformationEndedAbility;
    protected AscendedWizardAnimation deathAnimation;
    public final CoordinateMobAbility startDeathAnimationAbility;
    public final CoordinateMobAbility startFinalStageTransitionAbility;
    protected boolean isInFinalStage;
    protected int finalStageAliveBuffer;
    protected SoundPlayer windSound;
    protected SoundPlayer rumbleSound;
    protected PausableSound stage1EndSound;
    protected PausableSound stage2EndSound;
    protected long nextHurtSoundLocalTime;
    protected AscendedWizardAnimation currentAnimation;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public PlayerDamageTypeTracker damageTypeTracker = new PlayerDamageTypeTracker();

    public AscendedWizardMob() {
        super(75000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 20;
        this.setSpeed(100.0f);
        this.setFriction(1.0f);
        this.setArmor(40);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-26, -26, 52, 52);
        this.selectBox = new Rectangle(-23, -45, 46, 74);
        this.setDir(2);
        this.shouldSave = false;
        this.endBlackHolePhase = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                AscendedWizardMob.this.isInBlackHolePhase = false;
                SoundManager.playSound(GameResources.ascendedWizardBegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(1.2f).falloffDistance(4000));
                AscendedWizardMob.this.startAnimation(new AscendedWizardStage1ArmsDownAnimation());
            }
        });
        this.setShieldedAbility = this.registerAbility(new SetShieldedAbility());
        this.spawnOrbRingAbility = this.registerAbility(new SpawnOrbRingAbility());
        this.ascendedTeleportAbility = this.registerAbility(new AscendedTeleportAbility());
        this.startBeamSpawningAnimationAbility = this.registerAbility(new StartBeamSpawningAnimationAbility());
        this.startCageAbility = this.registerAbility(new StartCageSpawningAnimationAbility());
        this.startChannelAnimation = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                AscendedWizardMob.this.startAnimation(new AscendedWizardStage1ChannelingAnimation(value));
            }
        });
        this.startArmsUpAnimation = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                AscendedWizardMob.this.startAnimation(new AscendedWizardStage1ArmsUpAnimation());
            }
        });
        this.startArmsDownAnimation = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                AscendedWizardMob.this.startAnimation(new AscendedWizardStage1ArmsDownAnimation());
            }
        });
        this.slashSoundAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect(x, y).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.8f)).falloffDistance(3000));
            }
        });
        this.laserBoltSoundAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                SoundManager.playSound(GameResources.laserBlast1, (SoundEffect)SoundEffect.effect(x, y).volume(0.7f).falloffDistance(3000), new SameNearSoundCooldown(25, 300));
            }
        });
        this.playLightningBoltSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                SoundManager.playSound(GameResources.zap1, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(0.5f).falloffDistance(3000));
            }
        });
        this.playBossSoundAbility = this.registerAbility(new EnumMobAbility<BossSound>(BossSound.class){

            @Override
            protected void run(BossSound value) {
                if (!AscendedWizardMob.this.isClient()) {
                    return;
                }
                float volumeModifier = 1.2f;
                float pitchModifier = 1.2f;
                switch (value) {
                    case MOTHER_SLIME: {
                        SoundManager.playSound(GameResources.motherslimebegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(1.5f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                        break;
                    }
                    case MOONLIGHT_DANCER: {
                        SoundManager.playSound(GameResources.moonlightdancerbegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(1.2f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                        break;
                    }
                    case CRYSTAL_DRAGON: {
                        SoundManager.playSound(GameResources.crystaldragonbegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(0.8f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                        break;
                    }
                    case SPIDER_EMPRESS: {
                        SoundManager.playSound(GameResources.spiderempressbegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(1.2f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                        break;
                    }
                    case SUNLIGHT_CHAMPION: {
                        SoundManager.playSound(GameResources.sunlightchampionbegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(0.9f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                        break;
                    }
                    case NIGHT_SWARM: {
                        SoundManager.playSound(GameResources.nightswarmbegin, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(0.9f * volumeModifier).pitch(pitchModifier).falloffDistance(4000));
                    }
                }
            }
        });
        this.startTransformationAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                AscendedWizardMob.this.buffManager.removeBuff(BuffRegistry.ASCENDED_DASH, false);
                AscendedWizardMob.this.setPos(x, y, true);
                AscendedWizardMob.this.dx = 0.0f;
                AscendedWizardMob.this.dy = 0.0f;
                AscendedWizardMob.this.stopMoving();
                AscendedWizardMob.this.clearSpawnedEntities();
                final long startTime = AscendedWizardMob.this.getTime();
                AscendedWizardMob.this.isTransforming = true;
                final int initialWaitTime = 3000;
                final int regenWaitTime = 5000;
                int totalWaitTime = initialWaitTime + regenWaitTime;
                int animationTime = AscendedWizardStage1TransformationAnimation.SLOW_FRAMES_TIME + AscendedWizardStage1TransformationAnimation.FAST_FRAMES_TIME;
                int totalAnimationTime = totalWaitTime + animationTime;
                if (!AscendedWizardMob.this.isServer()) {
                    if (AscendedWizardMob.this.isClient()) {
                        CameraShake cameraShake = AscendedWizardMob.this.getClient().startCameraShake(AscendedWizardMob.this, totalAnimationTime, 40, 4.0f, 4.0f, true);
                        cameraShake.minDistance = 300;
                        cameraShake.listenDistance = 3000;
                    }
                    AscendedWizardMob.this.stage1EndSound = new PausableSound(GameResources.ascendedWizardStage1End, SoundEffect.effect(AscendedWizardMob.this).volume(1.8f).falloffDistance(5000));
                    for (int i = 0; i < 50; ++i) {
                        int lifeTime2 = GameRandom.globalRandom.getIntBetween(500, 5000);
                        float lifePerc = (float)lifeTime2 / 5000.0f;
                        float startHeight = GameRandom.globalRandom.getIntBetween(-10, 10);
                        float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(70, 150) * lifePerc;
                        AscendedWizardMob.this.getLevel().entityManager.addParticle(AscendedWizardMob.this.x + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), AscendedWizardMob.this.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), Particle.GType.CRITICAL).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), 0.5f).heightMoves(startHeight, height).colorRandom(311.0f, 0.95f, 0.95f, 10.0f, 0.05f, 0.05f).lifeTime(lifeTime2);
                    }
                    int particles = 40;
                    for (int i = 0; i < particles; ++i) {
                        int startHeight = -20;
                        int endHeight = 60;
                        float startDistance = GameRandom.globalRandom.getIntBetween(14, 26);
                        float endDistance = startDistance + (float)GameRandom.globalRandom.getIntBetween(10, 16);
                        float heightDistanceIncrease = 8.0f;
                        int randomHeightOffset = GameRandom.globalRandom.nextInt(28);
                        int heightToMove = GameRandom.globalRandom.nextInt(endHeight - startHeight);
                        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                        AscendedWizardMob.this.getLevel().entityManager.addParticle(AscendedWizardMob.this.x + GameMath.sin(currentAngle.get().floatValue()) * startDistance, AscendedWizardMob.this.y + GameMath.cos(currentAngle.get().floatValue()) * startDistance * 0.75f, AscendedWizardMob.this.particleTypeSwitcher.next()).color(new Color(255, 0, 219)).height((lifeTime, timeAlive, lifePercent) -> {
                            if (timeAlive < totalWaitTime) {
                                return startHeight + randomHeightOffset;
                            }
                            float progress = Math.min((float)(timeAlive -= totalWaitTime) / (float)animationTime, 1.0f);
                            return (float)(startHeight + randomHeightOffset) + (float)heightToMove * progress;
                        }).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                            float currentDistance;
                            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                            if (timeAlive < totalWaitTime) {
                                currentDistance = startDistance;
                            } else {
                                float progress = Math.min((float)(timeAlive -= totalWaitTime) / (float)animationTime, 1.0f);
                                currentDistance = startDistance + (endDistance - startDistance + (float)heightToMove / (float)(endHeight - startHeight) * heightDistanceIncrease) * progress;
                            }
                            pos.x = AscendedWizardMob.this.x + GameMath.sin(angle) * currentDistance;
                            pos.y = AscendedWizardMob.this.y + GameMath.cos(angle) * currentDistance * 0.75f;
                        }).lifeTime(totalWaitTime + animationTime).fadesAlphaTime(1000, 500).trail(new Color(255, 0, 220), 1000, 200, 8.0f, 0.0f).removeIf(AscendedWizardMob.this::removed).sizeFadesInAndOut(12, 16, 1000, 500);
                    }
                }
                AscendedWizardMob.this.startAnimation(new AscendedWizardStage1StartTransformationAnimation(totalWaitTime){

                    @Override
                    public void onMovementTick(Mob mob, float delta) {
                        super.onMovementTick(mob, delta);
                        long timeSinceStart = AscendedWizardMob.this.getTime() - startTime;
                        if (timeSinceStart > (long)initialWaitTime) {
                            float progress = (float)(timeSinceStart - (long)initialWaitTime) / (float)regenWaitTime;
                            int health = Math.max((int)((float)mob.getMaxHealth() * progress), 1);
                            mob.setHealthHidden(health);
                        }
                    }

                    @Override
                    public void onAnimationEnded(Mob mob) {
                        super.onAnimationEnded(mob);
                        AscendedWizardMob.this.startAnimation(new AscendedWizardStage1TransformationAnimation(){

                            @Override
                            public void onAnimationEnded(Mob mob) {
                                super.onAnimationEnded(mob);
                                AscendedWizardMob.this.isTransformed = true;
                                AscendedWizardMob.this.isTransforming = false;
                                AscendedWizardMob.this.setHealthHidden(AscendedWizardMob.this.getMaxHealth());
                                if (!AscendedWizardMob.this.isClient()) {
                                    AscendedWizardMob.this.markTransformationEndedAbility.runAndSend();
                                }
                            }
                        });
                    }
                });
            }
        });
        this.markTransformationEndedAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                AscendedWizardMob.this.isTransformed = true;
                AscendedWizardMob.this.isTransforming = false;
                AscendedWizardMob.this.setHealthHidden(AscendedWizardMob.this.getMaxHealth());
            }
        });
        this.startDeathAnimationAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                AscendedWizardMob.this.buffManager.removeBuff(BuffRegistry.ASCENDED_DASH, false);
                AscendedWizardMob.this.setPos(x, y, true);
                AscendedWizardMob.this.dx = 0.0f;
                AscendedWizardMob.this.dy = 0.0f;
                AscendedWizardMob.this.stopMoving();
                AscendedWizardMob.this.clearSpawnedEntities();
                if (!AscendedWizardMob.this.isServer()) {
                    SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(AscendedWizardMob.this).volume(1.5f).falloffDistance(2000));
                    AscendedWizardMob.this.stage2EndSound = new PausableSound(GameResources.ascendedWizardStage2End, SoundEffect.effect(AscendedWizardMob.this).volume(2.0f).falloffDistance(5000));
                }
                AscendedWizardMob.this.startDeathAnimation();
            }
        });
        this.startFinalStageTransitionAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(final int x, final int y) {
                if (AscendedWizardMob.this.isInFinalStage) {
                    return;
                }
                AscendedWizardMob.this.isInFinalStage = true;
                AscendedWizardMob.this.finalStageAliveBuffer = -100;
                if (!AscendedWizardMob.this.isServer()) {
                    PausableSound sound = new PausableSound(GameResources.ascendedWizardFlashBegin, SoundEffect.effect(AscendedWizardMob.this).volume(2.5f).falloffDistance(5000));
                    AscendedWizardMob.this.getWorldEntity().addWorldEventHidden(new AscendedFlashWorldEvent(x, y, sound));
                }
                if (AscendedWizardMob.this.isServer()) {
                    final Server server = AscendedWizardMob.this.getServer();
                    final Level baseLevel = AscendedWizardMob.this.getLevel();
                    baseLevel.entityManager.events.addHidden(new WaitForSecondsEvent(2.5f){

                        @Override
                        public void onWaitOver() {
                            LevelIdentifier newIdentifier = new LevelIdentifier(this.getLevel().getIdentifier() + "-void");
                            Point baseTile = AscendedWizardMob.this.getBaseTile(null);
                            ArenaEntrancePortalMob arenaPortal = new ArenaEntrancePortalMob();
                            arenaPortal.setLevel(baseLevel);
                            arenaPortal.keepLoaded = true;
                            arenaPortal.targetLevelIdentifier = newIdentifier;
                            arenaPortal.targetPos = new Point(x, y);
                            arenaPortal.shouldSave = false;
                            arenaPortal.onSpawned(baseTile.x * 32 + 16, (baseTile.y - 4) * 32 + 16);
                            baseLevel.entityManager.mobs.add(arenaPortal);
                            IncursionData incursionData = baseLevel instanceof IncursionLevel ? ((IncursionLevel)baseLevel).incursionData : null;
                            Level newLevel = server.world.getLevel(newIdentifier, () -> new AscendedVoidLevel(newIdentifier, 0, 0, server2.world.worldEntity, incursionData, arenaPortal));
                            newLevel.fallbackIdentifier = baseLevel.getIdentifier();
                            newLevel.fallbackTilePos = new Point(baseTile.x, baseTile.y - 4);
                            baseLevel.childLevels.add(newIdentifier);
                            TheVoidMob newBoss = new TheVoidMob();
                            newBoss.setLevel(newLevel);
                            newBoss.onSpawned(x, y);
                            newBoss.ascendedWizardMob = AscendedWizardMob.this;
                            newLevel.entityManager.mobs.add(newBoss);
                            int range = 3200;
                            GameUtils.streamServerClients(this.getLevel()).filter(c -> !c.isDead()).filter(c -> c.playerMob.getDiagonalMoveDistance(x, y) <= (float)range).forEach(c -> {
                                c.setFallbackLevel(AscendedWizardMob.this.getLevel(), c.playerMob.getTileX(), c.playerMob.getTileY());
                                c.changeLevelCheck(newIdentifier, null, level -> new TeleportResult(true, c.playerMob.getX(), c.playerMob.getY()), true);
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isInBlackHolePhase);
        writer.putNextBoolean(this.isTransformed);
        writer.putNextBoolean(this.isTransforming);
        writer.putNextBoolean(this.isInFinalStage);
        writer.putNextBoolean(this.deathAnimation != null);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isInBlackHolePhase = reader.getNextBoolean();
        this.isTransformed = reader.getNextBoolean();
        this.isTransforming = reader.getNextBoolean();
        this.isInFinalStage = reader.getNextBoolean();
        boolean isInDeathAnimation = reader.getNextBoolean();
        if (isInDeathAnimation && this.deathAnimation != null) {
            this.startDeathAnimation();
        }
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
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<AscendedWizardMob>(this, new AscendedWizardAI());
        if (this.currentAnimation == null) {
            if (this.isInBlackHolePhase) {
                this.startAnimation(new AscendedWizardStage1LaughingAnimation(-1));
            } else {
                this.startAnimation(new AscendedWizardStage1ArmsDownAnimation());
            }
        }
        if (!this.isServer() && !this.isInBlackHolePhase) {
            SoundManager.playSound(GameResources.ascendedWizardBegin, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(4000));
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public void startAnimation(AscendedWizardAnimation animation) {
        if (this.currentAnimation != null) {
            this.currentAnimation.onAnimationEnded(this);
        }
        this.currentAnimation = animation;
        this.currentAnimation.onAnimationStarted(this);
    }

    public void startBlackHolePhase() {
        this.isInBlackHolePhase = true;
    }

    public void endBlackHolePhase() {
        this.endBlackHolePhase.runAndSend();
    }

    public boolean isTransformed() {
        return this.isTransformed;
    }

    public boolean isTransforming() {
        return this.isTransforming;
    }

    public void startDeathAnimation() {
        this.deathAnimation = new AscendedWizardStage2DeathAnimation(() -> {
            if (!this.isClient()) {
                this.startFinalStageTransitionAbility.runAndSend(this.getX(), this.getY());
            }
        });
        this.deathAnimation.onAnimationStarted(this);
    }

    public boolean isInDeathAnimation() {
        return this.deathAnimation != null || this.isInFinalStage;
    }

    public void refreshFinalStageAlive() {
        this.finalStageAliveBuffer = 0;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.currentAnimation != null) {
            AscendedWizardAnimation animation = this.currentAnimation;
            animation.onMovementTick(this, delta);
            if (animation.isAnimationFinished(this)) {
                this.currentAnimation = null;
                animation.onAnimationEnded(this);
            }
        }
        if (this.currentAnimation == null) {
            this.startAnimation(new AscendedWizardStage1ArmsDownAnimation());
        }
        if (this.deathAnimation != null) {
            this.deathAnimation.onMovementTick(this, delta);
            if (this.deathAnimation.isAnimationFinished(this)) {
                this.deathAnimation.onAnimationEnded(this);
                this.deathAnimation = null;
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isTransformed()) {
            this.setSpeed(GameMath.lerp(this.getHealthPercent(), 170, 130));
        } else {
            this.setSpeed(GameMath.lerp(this.getHealthPercent(), 130, 100));
        }
        if (!this.isTransforming() && !this.isInDeathAnimation()) {
            if (this.windSound == null || this.windSound.isDone()) {
                this.windSound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this).falloffDistance(800).volume(0.0f));
                if (this.windSound != null) {
                    this.windSound.fadeIn(1.0f);
                    this.windSound.effect.volume(0.4f);
                }
            }
            if (this.windSound != null) {
                this.windSound.refreshLooping(1.0f);
            }
        }
        if (this.isTransforming()) {
            if (this.rumbleSound == null || this.rumbleSound.isDone()) {
                this.rumbleSound = SoundManager.playSound(GameResources.rumble, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000).volume(0.0f));
                if (this.rumbleSound != null) {
                    this.rumbleSound.fadeIn(1.0f);
                    this.rumbleSound.effect.volume(0.8f);
                }
            }
            if (this.rumbleSound != null) {
                this.rumbleSound.refreshLooping(1.0f);
            }
        }
        if (this.stage1EndSound != null) {
            this.stage1EndSound = this.stage1EndSound.gameTick();
        }
        if (this.stage2EndSound != null) {
            this.stage2EndSound = this.stage2EndSound.gameTick();
        }
        if (!this.isInFinalStage) {
            if (this.isTransforming() || this.isInBlackHolePhase || this.isInDeathAnimation()) {
                SoundManager.setMusic((AbstractMusicList)new MusicList(), SoundManager.MusicPriority.EVENT);
            } else if (this.isTransformed()) {
                SoundManager.setMusic(MusicRegistry.AscendedMadness, SoundManager.MusicPriority.EVENT, 1.5f);
            } else {
                SoundManager.setMusic(MusicRegistry.AscendedReturn, SoundManager.MusicPriority.EVENT, 1.5f);
            }
            EventStatusBarManager.registerMobHealthStatusBar(this);
            BossNearbyBuff.applyAround(this);
        } else {
            if (this.finalStageAliveBuffer < 0) {
                ++this.finalStageAliveBuffer;
                SoundManager.setMusic((AbstractMusicList)new MusicList(), SoundManager.MusicPriority.EVENT);
            }
            this.setHealthHidden(1);
        }
        if (!this.isTransforming() && !this.isInDeathAnimation()) {
            int maxHeight;
            int minHeight;
            int particlesPerSecond = 30;
            this.spinningParticleBuffer += (float)(50 * particlesPerSecond) / 1000.0f;
            while (this.spinningParticleBuffer >= 1.0f) {
                this.spinningParticleBuffer -= 1.0f;
                minHeight = 4;
                maxHeight = 6;
                final boolean reversed = GameRandom.globalRandom.nextBoolean();
                final float height = GameMath.lerp(GameRandom.globalRandom.nextFloat(), minHeight, maxHeight);
                final AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                float distance = 20.0f;
                this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance * 0.75f, this.particleTypeSwitcher.next()).colorRandom(310.0f, 0.95f, 0.95f, 10.0f, 0.05f, 0.05f).minDrawLight(150).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                    pos.x = this.x + GameMath.sin(angle) * 70.0f;
                    pos.y = this.y + (reversed ? -GameMath.cos(angle) : GameMath.cos(angle)) * 70.0f * 0.9f;
                }).height(new ParticleOption.HeightGetter(){

                    @Override
                    public float tick(float delta, int lifeTime, int timeAlive, float lifePercent) {
                        Float angle = (Float)currentAngle.get();
                        float offset = GameMath.sin(angle.floatValue()) * 35.0f;
                        return height + (reversed ? -offset : offset);
                    }
                }).sizeFades(16, 20).lifeTime(2000);
            }
            particlesPerSecond = 20;
            this.hoveringParticleBuffer += (float)(50 * particlesPerSecond) / 1000.0f;
            while (this.hoveringParticleBuffer >= 1.0f) {
                this.hoveringParticleBuffer -= 1.0f;
                minHeight = -20;
                maxHeight = -10;
                float height = GameMath.lerp(GameRandom.globalRandom.nextFloat(), minHeight, maxHeight);
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.particleTypeSwitcher.next()).colorRandom(310.0f, 0.95f, 0.95f, 10.0f, 0.05f, 0.05f).minDrawLight(150).movesFriction(this.dx + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), this.dy + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), 0.8f).heightMoves(height, height - 30.0f).sizeFades(10, 16).lifeTime(1000);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isTransformed()) {
            this.setSpeed(GameMath.lerp(this.getHealthPercent(), 170, 130));
        } else {
            this.setSpeed(GameMath.lerp(this.getHealthPercent(), 130, 100));
        }
        this.scaling.serverTick();
        if (this.isInFinalStage) {
            ++this.finalStageAliveBuffer;
            if (this.finalStageAliveBuffer >= 20) {
                this.remove(0.0f, 0.0f, null, false);
            } else {
                this.getLevel().unloadLevelBuffer = 0;
                Region region = this.getLevel().regionManager.getRegionByTile(this.getTileX(), this.getTileY(), false);
                if (region != null) {
                    region.unloadRegionBuffer.keepLoaded();
                }
                this.setHealthHidden(1);
            }
        } else {
            BossNearbyBuff.applyAround(this);
        }
        if (this.getLevel().tickManager().isFirstGameTickInSecond()) {
            this.cleanupSpawnedEntities();
        }
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        if (this.isInBlackHolePhase || this.isShielded() || this.isTransforming() || this.isInDeathAnimation() || this.isInFinalStage) {
            return false;
        }
        return super.canBeHit(attacker);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.buffManager.hasBuff(BuffRegistry.ASCENDED_DASH)) {
            return collisionDamage;
        }
        return super.getCollisionDamage(target, fromPacket, packetSubmitter);
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        this.clearSpawnedEntities();
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            if (c.achievementsLoaded()) {
                c.achievements().DEFEAT_ASCENDED.markCompleted((ServerClient)c);
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public void cleanupSpawnedEntities() {
        this.spawnedProblematicProjectiles.removeIf(Entity::removed);
        this.spawnedProjectiles.removeIf(Entity::removed);
        this.spawnedEvents.removeIf(LevelEvent::isOver);
        this.spawnedMobs.removeIf(Entity::removed);
    }

    public void clearProblematicEntities() {
        this.spawnedProblematicProjectiles.forEach(Projectile::remove);
        this.spawnedProblematicProjectiles.clear();
        this.spawnedMobs.forEach(Mob::remove);
        this.spawnedMobs.clear();
    }

    public void clearSpawnedEntities() {
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedProjectiles.clear();
        this.spawnedProblematicProjectiles.forEach(Projectile::remove);
        this.spawnedProblematicProjectiles.clear();
        this.spawnedEvents.forEach(LevelEvent::over);
        this.spawnedEvents.clear();
        this.spawnedMobs.forEach(Mob::remove);
        this.spawnedMobs.clear();
    }

    public boolean isShielded() {
        return this.getTime() <= this.shieldedEndTime;
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 85;
        boolean transformed = this.isTransformed();
        float rotate = Math.min(20.0f, this.dx / 8.0f);
        Point sprite = this.currentAnimation != null && !transformed ? this.currentAnimation.getSprite(this) : (transformed && this.deathAnimation != null ? this.deathAnimation.getSprite(this) : new Point(GameUtils.getAnim(level.getTime(), 4, 400), 0));
        boolean showShield = this.isInBlackHolePhase || this.isShielded();
        float shieldAlpha = 1.0f;
        long timeSinceShieldStart = this.getTime() - this.shieldedStartTime;
        if (timeSinceShieldStart < 1000L) {
            shieldAlpha = (float)timeSinceShieldStart / 1000.0f;
        }
        TextureDrawOptionsEnd backDrawOptions = null;
        if (showShield) {
            int anim = GameUtils.getAnim(level.getTime(), 4, 400);
            backDrawOptions = MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(anim, 2, 128).light(light).rotate(rotate, 64, 64).alpha(shieldAlpha).pos(drawX, drawY);
        }
        GameTexture texture = transformed ? MobRegistry.Textures.ascendedWizard_stage2 : MobRegistry.Textures.ascendedWizard_stage1;
        final TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 128).light(light).rotate(rotate, 64, 64).pos(drawX, drawY);
        TextureDrawOptionsEnd frontDrawOptions = null;
        if (showShield) {
            int anim = GameUtils.getAnim(level.getTime(), 4, 400);
            frontDrawOptions = MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(anim, 3, 128).light(light).rotate(rotate, 64, 64).alpha(shieldAlpha).pos(drawX, drawY);
        }
        final TextureDrawOptionsEnd finalBackDrawOptions = backDrawOptions;
        final TextureDrawOptionsEnd finalFrontDrawOptions = frontDrawOptions;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (finalBackDrawOptions != null) {
                    finalBackDrawOptions.draw();
                }
                drawOptions.draw();
                if (finalFrontDrawOptions != null) {
                    finalFrontDrawOptions.draw();
                }
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_big_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean isVisible() {
        if (this.isInFinalStage && this.deathAnimation == null) {
            return false;
        }
        return super.isVisible();
    }

    @Override
    public void playHurtSound() {
        if (this.getLocalTime() < this.nextHurtSoundLocalTime) {
            return;
        }
        GameSound sound = this.isTransformed() ? GameRandom.globalRandom.getOneOf(GameResources.ascendedWizardStage2Hurt1, GameResources.ascendedWizardStage2Hurt2, GameResources.ascendedWizardStage2Hurt3) : GameRandom.globalRandom.getOneOf(GameResources.ascendedWizardHurt1, GameResources.ascendedWizardHurt2, GameResources.ascendedWizardHurt3);
        float pitch = GameRandom.globalRandom.getFloatBetween(0.85f, 1.15f);
        SoundPlayer player = SoundManager.playSound(sound, (SoundEffect)SoundEffect.effect(this).volume(1.5f).pitch(pitch).falloffDistance(4000));
        if (player != null) {
            this.nextHurtSoundLocalTime = this.getLocalTime() + (long)(player.getLengthInSeconds() * 1000.0f) + (long)GameRandom.globalRandom.getIntBetween(1000, 2000);
        }
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-12, -28, 24, 34);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 32;
        int drawY = y - 46;
        float rotate = Math.min(20.0f, this.dx / 8.0f);
        Point sprite = this.currentAnimation != null ? this.currentAnimation.getSprite(this) : new Point(GameUtils.getAnim(this.getTime(), 4, 400), 0);
        MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(sprite.x, sprite.y, 128).rotate(rotate, 32, 32).size(64, 64).draw(drawX, drawY);
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        if (health <= 0) {
            health = 1;
            if (this.isServer()) {
                if (!this.isTransformed) {
                    if (!this.isTransforming) {
                        this.startTransformationAbility.runAndSend(this.getX(), this.getY());
                    }
                } else if (this.deathAnimation == null && !this.isInFinalStage) {
                    this.startDeathAnimationAbility.runAndSend(this.getX(), this.getY());
                }
            }
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
    }

    @Override
    public MobWasHitEvent isServerHit(GameDamage damage, float x, float y, float knockback, Attacker attacker) {
        return this.damageTypeTracker.runIsServerHit(super.isServerHit(damage, x, y, knockback, attacker));
    }

    @Override
    public DamageType getMostDamageTypeDealt(ServerClient client) {
        return this.damageTypeTracker.getMostDamageTypeDealt(client);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    public Point getBaseTile(Blackboard<?> blackboard) {
        Mob currentTarget;
        Point baseTile = new Point(this.getTileX(), this.getTileY());
        if (this.spawnTilePosition != null) {
            baseTile = new Point(this.spawnTilePosition.x, this.spawnTilePosition.y);
        } else if (blackboard != null && (currentTarget = blackboard.getObject(Mob.class, "currentTarget")) != null) {
            baseTile = new Point(currentTarget.getTileX(), currentTarget.getTileY());
        }
        return baseTile;
    }

    public PriorityMap<Point> findNewTiles(Blackboard<?> blackboard) {
        Point baseTile = this.getBaseTile(blackboard);
        int searchRadius = 20;
        int minRadius = 3;
        PriorityMap<Point> priorityMap = new PriorityMap<Point>();
        for (int x = -searchRadius; x <= searchRadius; ++x) {
            if (x > -minRadius && x < minRadius) continue;
            for (int y = -searchRadius; y <= searchRadius; ++y) {
                if (y > -minRadius && y < minRadius || GameMath.diagonalMoveDistance(x, y) > (double)searchRadius) continue;
                int tileX = baseTile.x + x;
                int tileY = baseTile.y + y;
                if (this.getLevel().isSolidTile(tileX, tileY)) continue;
                priorityMap.add(10, new Point(tileX, tileY));
            }
        }
        return priorityMap;
    }

    public Point findNewTile(Blackboard<?> blackboard) {
        return this.findNewTiles(blackboard).getRandomBestObject(GameRandom.globalRandom, 20);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return DeathMessageTable.fromRange("ascendedwizard", 5);
    }

    public class SetShieldedAbility
    extends MobAbility {
        public void runAndSend(long shieldEndTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextLong(shieldEndTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            long shieldEndTime = reader.getNextLong();
            if (shieldEndTime >= AscendedWizardMob.this.getTime()) {
                AscendedWizardMob.this.shieldedStartTime = AscendedWizardMob.this.getTime();
            }
            AscendedWizardMob.this.shieldedEndTime = shieldEndTime;
        }
    }

    public class SpawnOrbRingAbility
    extends MobAbility {
        public void runAndSend(Mob target, int orbCount, int timeBeforeFreeze, int timeBeforeMove, int projectileSeed) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(target.getUniqueID());
            writer.putNextInt(orbCount);
            writer.putNextInt(timeBeforeFreeze);
            writer.putNextInt(timeBeforeMove);
            writer.putNextInt(projectileSeed);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int targetUniqueID = reader.getNextInt();
            Mob target = GameUtils.getLevelMob(targetUniqueID, AscendedWizardMob.this.getLevel());
            int orbCount = reader.getNextInt();
            int timeBeforeFreeze = reader.getNextInt();
            int timeBeforeMove = reader.getNextInt();
            int projectileSeed = reader.getNextInt();
            if (target != null) {
                GameRandom random = new GameRandom(projectileSeed);
                for (int i = 0; i < orbCount; ++i) {
                    AscendedOrbProjectile projectile = new AscendedOrbProjectile(AscendedWizardMob.this.getLevel(), target, i, timeBeforeFreeze, timeBeforeMove, orbRingDamage, AscendedWizardMob.this);
                    projectile.getUniqueID(random);
                    AscendedWizardMob.this.getLevel().entityManager.projectiles.addHidden(projectile);
                    if (AscendedWizardMob.this.isClient()) continue;
                    AscendedWizardMob.this.spawnedProblematicProjectiles.add(projectile);
                }
            }
        }
    }

    public class AscendedTeleportAbility
    extends MobAbility {
        public void runAndSend(int x, int y) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            AscendedWizardMob.this.setPos(x, y, true);
        }
    }

    public class StartBeamSpawningAnimationAbility
    extends MobAbility {
        public void runAndSend(int x, int y, int spawnTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextInt(spawnTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            int spawnTime = reader.getNextInt();
            AscendedWizardMob.this.getLevel().entityManager.addParticle(new Particle(AscendedWizardMob.this.getLevel(), x, y, spawnTime){

                @Override
                public void clientTick() {
                    super.clientTick();
                    GameRandom random = GameRandom.globalRandom;
                    AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                    float distance = 20.0f;
                    this.getLevel().entityManager.addParticle(this.x + (float)random.getIntBetween(-20, 20), this.y + (float)random.getIntBetween(-20, 20), AscendedWizardMob.this.particleTypeSwitcher.next()).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).heightMoves(0.0f, 100.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                        pos.x = this.x + GameMath.sin(angle) * distance;
                        pos.y = this.y + GameMath.cos(angle) * distance * 0.75f;
                    }).lifeTime(1000).sizeFades(16, 24);
                }

                @Override
                public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                }
            }, true, Particle.GType.CRITICAL);
        }
    }

    public class StartCageSpawningAnimationAbility
    extends MobAbility {
        public void runAndSend(int x, int y, int chargeTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextInt(chargeTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            int chargeTime = reader.getNextInt();
            if (!AscendedWizardMob.this.isServer()) {
                AscendedWizardMob.this.getLevel().entityManager.addParticle(new AscendedCageBombParticle(AscendedWizardMob.this.getLevel(), x, y, chargeTime), Particle.GType.CRITICAL);
            }
            AscendedWizardMob.this.startAnimation(new AscendedWizardStage1ChannelingAnimation(chargeTime));
            AscendedWizardMob.this.shieldedStartTime = AscendedWizardMob.this.getTime();
            AscendedWizardMob.this.shieldedEndTime = AscendedWizardMob.this.getTime() + (long)chargeTime;
        }
    }

    public static enum BossSound {
        MOTHER_SLIME,
        MOONLIGHT_DANCER,
        CRYSTAL_DRAGON,
        SPIDER_EMPRESS,
        SUNLIGHT_CHAMPION,
        NIGHT_SWARM;

    }

    public static class AscendedWizardAI<T extends AscendedWizardMob>
    extends SequenceAINode<T> {
        public AscendedWizardAI() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (((AscendedWizardMob)mob).isInBlackHolePhase || ((AscendedWizardMob)mob).isTransforming() || ((AscendedWizardMob)mob).isInDeathAnimation()) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.FAILURE;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            int minIdleTime = 100;
            int maxIdleTime = 600;
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<AscendedWizardMob>(new IsolateRunningAINode(attackStages), m -> !m.isTransformed, AINodeResult.SUCCESS));
            ArrayList<Integer> rotationTracker = new ArrayList<Integer>();
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedGatlingGunStage());
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedPushStage());
            attackStages.addChild(new AscendedChargeTargetStage(true, false));
            attackStages.addChild(new AscendedChargeTargetStage(false, false));
            attackStages.addChild(new AscendedChargeTargetStage(false, true));
            attackStages.addChild(new AscendedCageRotationStage(0.25f, 0.5f, 0.75f));
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedOrbRingStage());
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedGatlingGunStage());
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new AscendedWizardOrbitalBeamStage());
            attackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            minIdleTime = 0;
            maxIdleTime = 300;
            AttackStageManagerNode transformedAttackStages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<AscendedWizardMob>(new IsolateRunningAINode(transformedAttackStages), m -> m.isTransformed, AINodeResult.SUCCESS));
            rotationTracker = new ArrayList();
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedGatlingGunStage());
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedPushStage());
            transformedAttackStages.addChild(new AscendedChargeTargetStage(true, false));
            transformedAttackStages.addChild(new AscendedChargeTargetStage(false, false));
            transformedAttackStages.addChild(new AscendedChargeTargetStage(false, true));
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedOrbRingStage());
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedGatlingGunStage());
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedRandomBossAttackStage(rotationTracker));
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
            transformedAttackStages.addChild(new FindNewPosStage(true));
            transformedAttackStages.addChild(new AscendedWizardOrbitalBeamStage());
            transformedAttackStages.addChild(new IdleTimeAttackStage<AscendedWizardMob>(m -> this.getIdleTime(m, minIdleTime, maxIdleTime)));
        }

        private int getIdleTime(T mob, int minTime, int maxTime) {
            return Math.max((int)((float)maxTime * ((Mob)mob).getHealthPercent()), minTime);
        }
    }

    protected static class FindNewPosStage<T extends AscendedWizardMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public boolean waitForArrive;
        private boolean hasStartedMoving;

        public FindNewPosStage(boolean waitForArrive) {
            this.waitForArrive = waitForArrive;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (!this.hasStartedMoving) {
                this.hasStartedMoving = true;
                Point pos = ((AscendedWizardMob)mob).findNewTile(blackboard);
                if (pos != null) {
                    blackboard.mover.directMoveTo(this, pos.x * 32 + 16, pos.y * 32 + 16);
                    return this.waitForArrive ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
                }
            }
            if (this.waitForArrive && blackboard.mover.isCurrentlyMovingFor(this) && blackboard.mover.isMoving()) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.hasStartedMoving = false;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
}

