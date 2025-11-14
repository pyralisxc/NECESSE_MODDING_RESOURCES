/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.AbstractMusicList;
import necesse.engine.CameraShake;
import necesse.engine.MusicList;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketStartCredits;
import necesse.engine.network.server.ServerClient;
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
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.tween.Easings;
import necesse.engine.world.worldEvent.AscendedFlashWorldEvent;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
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
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.StopMovingAttackStage;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidBlackHoleStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidClawBeamStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidClawGroundAtPositionShatterStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidClawGroundShatterStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidConjureMoreClawsStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidHornBulletHell;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidLaserHornsStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidLaserMouthStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidMovingRainStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidMultiClawSlamStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidReformHornsStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidStunnedStage;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidTeleportAboveTargetStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.DamageRelativeRotationLootItem;
import necesse.level.maps.AscendedVoidLevel;
import necesse.level.maps.Level;

public class TheVoidMob
extends FlyingBossMob
implements DamageRelativeRotationLootItem.PlayerDamageDealtGetter {
    public static int MAX_HEALTH_AND_DAMAGE_MULTIPLIER = 5;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(35000 * MAX_HEALTH_AND_DAMAGE_MULTIPLIER, 44000 * MAX_HEALTH_AND_DAMAGE_MULTIPLIER, 52000 * MAX_HEALTH_AND_DAMAGE_MULTIPLIER, 57000 * MAX_HEALTH_AND_DAMAGE_MULTIPLIER, 67000 * MAX_HEALTH_AND_DAMAGE_MULTIPLIER);
    public static GameDamage collisionDamage = new GameDamage(120.0f);
    public static GameDamage blackHoleDamage = new GameDamage(160.0f);
    public static GameDamage laserDamage = new GameDamage(160.0f);
    public static GameDamage breathAttackDamage = new GameDamage(250.0f);
    public static GameDamage voidRainDamage = new GameDamage(110.0f);
    public static GameDamage boltDamage = new GameDamage(120.0f);
    public static GameDamage clawCollisionDamage = new GameDamage(130.0f);
    public static GameDamage clawShatterDamage = new GameDamage(100.0f);
    public static GameDamage clawBeamDamage = new GameDamage(160.0f);
    public AscendedWizardMob ascendedWizardMob;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    public ArrayList<LevelEvent> spawnedEvents = new ArrayList();
    public ArrayList<Mob> spawnedMobs = new ArrayList();
    public ArrayList<TheVoidClawMob> spawnedClaws = new ArrayList();
    public ArrayList<LevelMob<TheVoidHornMob>> spawnedHorns = new ArrayList();
    protected long fadeStartTime;
    protected int fadeTransitionDuration;
    protected int fadeKeepDuration;
    public final StartFadeAbility startFadeAbility;
    protected boolean isVulnerable;
    private float currentHeight;
    private final Point leftHornProjectileOffset = new Point(-187, -92);
    private final Point rightHornProjectileOffset = new Point(187, -92);
    public float regenHornsAtPercentHealth;
    protected long showHornChargeUpEndTime;
    public final BooleanMobAbility setVulnerableAbility;
    public final IntMobAbility showHornChargeUpAbility;
    public final CoordinateMobAbility magicBoltSoundAbility;
    public final EmptyMobAbility telegraphVoidRainAbility;
    public final EmptyMobAbility startDeathAbility;
    protected long deathAnimationStartTime;
    protected int totalDeathAnimationDuration = 7500;
    protected PausableSound deathSound;
    protected long nextHurtSoundLocalTime;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public PlayerDamageTypeTracker damageTypeTracker = new PlayerDamageTypeTracker();

    public TheVoidMob() {
        super(500000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(150.0f);
        this.setFriction(1.0f);
        this.moveAccuracy = 32;
        this.setArmor(50);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-110, -60, 220, 120);
        this.hitBox = new Rectangle(-110, -90, 220, 180);
        this.selectBox = new Rectangle(-130, -130, 260, 260);
        this.shouldSave = false;
        this.startFadeAbility = this.registerAbility(new StartFadeAbility());
        this.setVulnerableAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                TheVoidMob.this.isVulnerable = value;
            }
        });
        this.showHornChargeUpAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                TheVoidMob.this.showHornChargeUpEndTime = TheVoidMob.this.getTime() + (long)value;
            }
        });
        this.magicBoltSoundAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(x, y).volume(0.3f).pitch(1.5f).falloffDistance(2000), new SameNearSoundCooldown(50, 300));
            }
        });
        this.telegraphVoidRainAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (TheVoidMob.this.isClient()) {
                    TheVoidMob.this.spawnTelegraphVoidRainParticles();
                }
            }
        });
        this.startDeathAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                TheVoidMob.this.clearSpawnedEntities();
                if (TheVoidMob.this.deathAnimationStartTime != 0L) {
                    return;
                }
                TheVoidMob.this.deathAnimationStartTime = TheVoidMob.this.getTime();
                if (TheVoidMob.this.isClient()) {
                    CameraShake shake = TheVoidMob.this.getClient().startCameraShake(TheVoidMob.this, TheVoidMob.this.totalDeathAnimationDuration, 40, 5.0f, 5.0f, false);
                    shake.listenDistance = 10000;
                }
                if (!TheVoidMob.this.isServer()) {
                    TheVoidMob.this.deathSound = new PausableSound(GameResources.theVoidDeath, SoundEffect.effect(TheVoidMob.this).volume(1.5f).falloffDistance(3000));
                    TheVoidMob.this.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent((float)(TheVoidMob.this.totalDeathAnimationDuration - 2500) / 1000.0f){

                        @Override
                        public void onWaitOver() {
                            PausableSound sound = new PausableSound(GameResources.theVoidFlashBegin, SoundEffect.globalEffect().volume(2.5f));
                            this.getWorldEntity().addWorldEventHidden(new AscendedFlashWorldEvent(TheVoidMob.this.x, TheVoidMob.this.y - (float)TheVoidMob.this.getFlyingHeight(), sound));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isVulnerable);
        writer.putNextShortUnsigned(this.spawnedHorns.size());
        for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
            writer.putNextInt(lm.uniqueID);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isVulnerable = reader.getNextBoolean();
        for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
            TheVoidHornMob last = lm.get(this.getLevel());
            if (last == null) continue;
            last.remove();
        }
        this.spawnedHorns.clear();
        int hornsCount = reader.getNextShortUnsigned();
        for (int i = 0; i < hornsCount; ++i) {
            this.spawnedHorns.add(new LevelMob<int>(reader.getNextInt()));
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
        this.ai = new BehaviourTreeAI<TheVoidMob>(this, new TheVoidMobAI());
        this.updateHornMobs();
        if (!this.isClient()) {
            this.spawnClaw();
            this.spawnClaw();
        }
        if (this.isClient()) {
            SoundManager.playSound(GameResources.theVoidBegin, (SoundEffect)SoundEffect.effect(this).volume(1.5f).falloffDistance(5000));
        }
    }

    public void updateHornMobs() {
        GameRandom uniqueIDRandom = new GameRandom();
        while (this.spawnedHorns.size() < 2) {
            int newUniqueID = TheVoidMob.getNewUniqueID(this.getLevel(), uniqueIDRandom);
            this.spawnedHorns.add(new LevelMob<int>(newUniqueID));
        }
        for (int i = 0; i < this.spawnedHorns.size(); ++i) {
            LevelMob<TheVoidHornMob> levelMob = this.spawnedHorns.get(i);
            TheVoidHornMob horn = new TheVoidHornMob();
            horn.isLeftHorn = i == 0;
            horn.setLevel(this.getLevel());
            horn.setUniqueID(levelMob.uniqueID);
            horn.setPos(this.x, this.y, true);
            horn.setMaxHealth(this.getMaxHealth());
            horn.setHealthHidden(this.getHealth());
            horn.master.uniqueID = this.getUniqueID();
            this.getLevel().entityManager.mobs.addHidden(horn);
            this.spawnedHorns.set(i, new LevelMob<TheVoidHornMob>(horn));
        }
    }

    public void spawnClaw() {
        int index = this.spawnedClaws.size();
        TheVoidClawMob claw = new TheVoidClawMob();
        claw.leftHanded = index % 2 == 0;
        claw.clawIndex = index / 2;
        claw.setLevel(this.getLevel());
        Point positionOffset = claw.getClawBasePositionOffset();
        claw.setPos(this.x + (float)positionOffset.x, this.y + (float)positionOffset.y, true);
        claw.setMaxHealth(this.getMaxHealth());
        claw.setHealthHidden(this.getHealth());
        claw.master.uniqueID = this.getUniqueID();
        this.getLevel().entityManager.mobs.addHidden(claw);
        this.spawnedClaws.add(claw);
    }

    @Override
    public LootTable getLootTable() {
        if (this.ascendedWizardMob == null) {
            return AscendedWizardMob.lootTable;
        }
        return super.getLootTable();
    }

    @Override
    public LootTable getPrivateLootTable() {
        if (this.ascendedWizardMob == null) {
            return AscendedWizardMob.privateLootTable;
        }
        return super.getPrivateLootTable();
    }

    public int getTimeSinceFadeStart() {
        if (this.fadeStartTime <= 0L) {
            return 0;
        }
        return (int)(this.getTime() - this.fadeStartTime);
    }

    public float getFadeAlpha() {
        if (this.fadeStartTime <= 0L) {
            return 1.0f;
        }
        long timeSinceStart = this.getTime() - this.fadeStartTime;
        if (timeSinceStart < (long)this.fadeTransitionDuration) {
            float progress = (float)timeSinceStart / (float)this.fadeTransitionDuration;
            return Math.min(1.0f, 1.0f - progress);
        }
        if ((timeSinceStart -= (long)this.fadeTransitionDuration) < (long)this.fadeKeepDuration) {
            return 0.0f;
        }
        if ((timeSinceStart -= (long)this.fadeKeepDuration) < (long)this.fadeTransitionDuration) {
            float progress = (float)timeSinceStart / (float)this.fadeTransitionDuration;
            return Math.max(0.0f, progress);
        }
        return 1.0f;
    }

    public boolean isInDeathAnimation() {
        return this.deathAnimationStartTime != 0L;
    }

    public float getFadeWhiteness() {
        if (this.deathAnimationStartTime == 0L) {
            return 0.0f;
        }
        int timeSinceStart = (int)(this.getTime() - this.deathAnimationStartTime);
        float progress = (float)timeSinceStart / (float)(this.totalDeathAnimationDuration - 500);
        return Easings.BounceInOut.ease(Math.min(progress, 1.0f));
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.setSpeed(GameMath.lerp(this.getHealthPercent(), 250, 150));
        if (this.isInDeathAnimation()) {
            SoundManager.setMusic((AbstractMusicList)new MusicList(), SoundManager.MusicPriority.EVENT);
        } else {
            SoundManager.setMusic(MusicRegistry.FractureoftheVoid, SoundManager.MusicPriority.EVENT, 1.5f);
        }
        EventStatusBarManager.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
        if (!this.isVulnerable) {
            this.checkVulnerableTick();
        }
        if (this.deathSound != null) {
            this.deathSound = this.deathSound.gameTick();
        }
        for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
            lm.computeIfPresent(this.getLevel(), horn -> {
                horn.removeTicker = 0;
            });
        }
        if (this.getTime() < this.showHornChargeUpEndTime) {
            for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
                TheVoidHornMob spawnedHorn = lm.get(this.getLevel());
                if (spawnedHorn == null || spawnedHorn.isBroken) continue;
                this.spawnTelegraphBulletHellParticles(spawnedHorn.isLeftHorn);
            }
        }
        float alpha = this.getFadeAlpha();
        alpha = Math.min(1.0f - this.getFadeWhiteness(), alpha);
        for (int i = 0; i < 3; ++i) {
            int xOffset = GameRandom.globalRandom.getIntBetween(-64, 64);
            this.getLevel().entityManager.addParticle(this.x + (float)xOffset, this.y - 32.0f + (float)GameRandom.globalRandom.getIntBetween(-30, 30) - this.currentHeight, Particle.GType.COSMETIC).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).sizeFades(24, 48).color(1.0f, 1.0f, 1.0f, alpha).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-60, -90) + Math.abs(xOffset), 0.8f).lifeTime(1000);
        }
    }

    @Override
    public void serverTick() {
        int timeSinceStart;
        super.serverTick();
        this.setSpeed(GameMath.lerp(this.getHealthPercent(), 250, 150));
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        if (!this.isVulnerable) {
            this.checkVulnerableTick();
        }
        for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
            lm.computeIfPresent(this.getLevel(), horn -> {
                horn.removeTicker = 0;
            });
        }
        if (this.ascendedWizardMob != null && !this.ascendedWizardMob.removed()) {
            this.ascendedWizardMob.refreshFinalStageAlive();
        }
        if (this.deathAnimationStartTime != 0L && (timeSinceStart = (int)(this.getTime() - this.deathAnimationStartTime)) > this.totalDeathAnimationDuration) {
            this.remove(0.0f, 0.0f, null, true);
        }
        if (this.getLevel().tickManager().isFirstGameTickInSecond()) {
            this.cleanupSpawnedEntities();
        }
    }

    public boolean areAllHornsBroken() {
        return this.spawnedHorns.stream().map(lm -> (TheVoidHornMob)lm.get(this.getLevel())).filter(Objects::nonNull).allMatch(h -> h.isBroken);
    }

    public boolean isAnyHornBroken() {
        return this.spawnedHorns.stream().map(lm -> (TheVoidHornMob)lm.get(this.getLevel())).filter(Objects::nonNull).anyMatch(h -> h.isBroken);
    }

    private void checkVulnerableTick() {
        if (this.areAllHornsBroken()) {
            this.isVulnerable = true;
        }
    }

    @Override
    public void tickMovement(float delta) {
        if (this.deathAnimationStartTime != 0L) {
            this.dx = 0.0f;
            this.dy = 0.0f;
            this.stopMoving();
        }
        super.tickMovement(delta);
        float heightDelta = this.getDesiredHeight() - this.currentHeight;
        float heightSpeed = Math.abs(heightDelta) * 2.0f + 10.0f;
        float heightToMove = heightSpeed * delta / 250.0f;
        this.currentHeight = Math.abs(heightDelta) < heightToMove ? this.getDesiredHeight() : (this.currentHeight += Math.signum(heightDelta) * heightToMove);
        for (LevelMob<TheVoidHornMob> lm : this.spawnedHorns) {
            TheVoidHornMob horn = lm.get(this.getLevel());
            if (horn == null) continue;
            horn.setPos(this.x, this.y, true);
        }
    }

    @Override
    public float getIncomingDamageModifier() {
        return MAX_HEALTH_AND_DAMAGE_MULTIPLIER;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.getFlyingHeight() < 100) {
            return collisionDamage;
        }
        return super.getCollisionDamage(target, fromPacket, packetSubmitter);
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.currentHeight;
    }

    public float getDesiredHeight() {
        long startTime = this.getTime();
        if (this.deathAnimationStartTime != 0L) {
            startTime = this.deathAnimationStartTime;
        }
        float perc = GameUtils.getAnimFloat(startTime, 2000);
        float height = GameMath.sin(perc * 360.0f) * 10.0f;
        if (this.deathAnimationStartTime != 0L) {
            int timeSinceDeathAnimationStart = (int)(this.getTime() - this.deathAnimationStartTime);
            float totalHeightIncreaseOnDeath = 150.0f;
            float deathProgress = (float)timeSinceDeathAnimationStart / (float)this.totalDeathAnimationDuration;
            height += GameMath.lerp(deathProgress, 0.0f, totalHeightIncreaseOnDeath);
        }
        return (int)height;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void playHurtSound() {
        this.playHurtSound(false);
    }

    public void playHurtSound(boolean ignoreCooldown) {
        if (!ignoreCooldown && this.getLocalTime() < this.nextHurtSoundLocalTime) {
            return;
        }
        GameSound sound = GameRandom.globalRandom.getOneOf(GameResources.theVoidHurt1, GameResources.theVoidHurt2, GameResources.theVoidHurt3, GameResources.theVoidHurt4);
        float pitch = GameRandom.globalRandom.getFloatBetween(0.85f, 1.15f);
        SoundPlayer player = SoundManager.playSound(sound, (SoundEffect)SoundEffect.effect(this).volume(1.7f).pitch(pitch).falloffDistance(4000));
        if (player != null) {
            this.nextHurtSoundLocalTime = this.getLocalTime() + (long)(player.getLengthInSeconds() * 1000.0f) + (long)GameRandom.globalRandom.getIntBetween(1000, 2000);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int height = this.getFlyingHeight() + 10;
        for (int i = 0; i < 12; ++i) {
            float xOffset = GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f);
            float yOffset = GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f);
            int spriteX = i % 6;
            int spriteY = 1 + i / 6;
            FleshParticle particle = new FleshParticle(this.getLevel(), MobRegistry.Textures.theVoidDebris, spriteX, spriteY, 96, this.x + xOffset, this.y + yOffset, knockbackX, knockbackY);
            particle.lifeTime = 10000L;
            particle.dx = GameRandom.globalRandom.getFloatBetween(-100.0f, 100.0f);
            particle.dy = GameRandom.globalRandom.getFloatBetween(-100.0f, 100.0f);
            particle.friction = 0.2f;
            particle.height = GameRandom.globalRandom.getFloatBetween(height, height + 20);
            this.getLevel().entityManager.addParticle(particle, Particle.GType.CRITICAL);
        }
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        if (health <= 0) {
            health = 1;
            if (this.deathAnimationStartTime == 0L && !this.isClient()) {
                this.startDeathAbility.runAndSend();
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
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        this.clearSpawnedEntities();
        this.spawnedClaws.forEach(Mob::remove);
        this.spawnedClaws.clear();
        this.spawnedHorns.stream().map(lm -> (TheVoidHornMob)lm.get(this.getLevel())).filter(Objects::nonNull).forEach(Mob::remove);
        this.spawnedHorns.clear();
        if (this.ascendedWizardMob != null && !this.ascendedWizardMob.removed()) {
            this.ascendedWizardMob.damageTypeTracker.addOtherStats(this.damageTypeTracker);
            this.ascendedWizardMob.remove(0.0f, 0.0f, attacker, isDeath);
        }
        if (this.isServer() && this.getLevel() instanceof AscendedVoidLevel) {
            this.getServer().world.levelManager.deleteLevel(this.getLevel().getIdentifier(), null);
        } else if (this.ascendedWizardMob != null) {
            ArenaEntrancePortalMob portal = new ArenaEntrancePortalMob();
            portal.targetPos = this.ascendedWizardMob.getPositionPoint();
            portal.targetLevelIdentifier = this.ascendedWizardMob.getLevel().getIdentifier();
            portal.keepAliveAlways = true;
            this.getLevel().entityManager.mobs.add(portal);
        }
        this.ascendedWizardMob = null;
    }

    public void cleanupSpawnedEntities() {
        this.spawnedProjectiles.removeIf(Entity::removed);
        this.spawnedEvents.removeIf(LevelEvent::isOver);
        this.spawnedMobs.removeIf(Entity::removed);
    }

    public void clearSpawnedEntities() {
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedProjectiles.clear();
        this.spawnedEvents.forEach(LevelEvent::over);
        this.spawnedEvents.clear();
        this.spawnedMobs.forEach(Mob::remove);
        this.spawnedMobs.clear();
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            int kills = c.characterStats().mob_kills.getKills(this.getStringID());
            if (kills <= 1) {
                c.sendPacket(new PacketStartCredits());
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public Point getHornOffset(boolean isLeftHorn) {
        Point p = isLeftHorn ? this.leftHornProjectileOffset : this.rightHornProjectileOffset;
        return new Point(p.x, p.y - this.getFlyingHeight());
    }

    private void spawnTelegraphBulletHellParticles(boolean isLeftHorn) {
        GameRandom random = GameRandom.globalRandom;
        for (int i = 0; i < 10; ++i) {
            int angle = random.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = random.getFloatBetween(25.0f, 75.0f);
            float startX = this.x + (float)this.getHornOffset((boolean)isLeftHorn).x + dir.x * range;
            float startY = this.y + (float)this.getHornOffset((boolean)isLeftHorn).y - 16.0f;
            float endHeight = 0.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = random.getIntBetween(100, 300);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            this.getLevel().entityManager.addTopParticle(startX, startY, this.particleTypeSwitcher.next()).sprite(GameResources.ascendedParticle.sprite(random.nextInt(5), 0, 20)).sizeFades(40, 80).rotates().movesConstant(-speed, 0.0f).heightMoves(startHeight, endHeight).fadesAlphaTime(100, 50).ignoreLight(true).lifeTime(lifeTime);
        }
    }

    private void spawnTelegraphVoidRainParticles() {
        for (int i = 0; i < 20; ++i) {
            float startX = this.x;
            float startY = this.y - 64.0f;
            float xDir = GameRandom.globalRandom.getFloatBetween(-0.5f, 0.5f);
            this.getLevel().entityManager.addTopParticle(startX, startY, this.particleTypeSwitcher.next()).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).sizeFades(48, 48).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                pos.x += xDir * delta * 2.0f * Math.max(0.5f - lifePercent, 0.0f);
                pos.y -= (delta + (0.5f - Math.abs(xDir)) * 10.0f) * 2.0f * lifePercent;
            }).ignoreLight(true).trail(new Color(255, 0, 231), 100, 300, 10.0f).lifeTime(1000);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float overlayAlpha;
        float headAlpha;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        int drawX = camera.getDrawX(x) - 224;
        int drawY = camera.getDrawY(y) - 176 + 25;
        drawY -= this.getFlyingHeight();
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        int spriteIndex = GameUtils.getAnim(this.getTime(), 6, 600);
        float alpha = this.getFadeAlpha();
        final float whiteness = this.getFadeWhiteness();
        alpha = Math.max(alpha, whiteness);
        if (alpha >= 0.5f) {
            headAlpha = 1.0f;
            overlayAlpha = GameMath.lerp(GameMath.clamp(alpha, 0.5f, 1.0f), 1.0f, 0.0f);
        } else {
            headAlpha = 0.0f;
            overlayAlpha = GameMath.lerp(GameMath.clamp(alpha, 0.0f, 0.5f), 0.0f, 1.0f);
        }
        int jawDrawYOffset = 0;
        final TextureDrawOptionsEnd headBaseDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 0, 448, 352).rotate(rotate).alpha(headAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd tongueDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 1, 448, 352).rotate(rotate).alpha(headAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd jawDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 2, 448, 352).rotate(rotate).alpha(headAlpha).pos(drawX, drawY + jawDrawYOffset);
        final TextureDrawOptionsEnd teethDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 3, 448, 352).rotate(rotate).alpha(headAlpha).pos(drawX, drawY + jawDrawYOffset);
        final TextureDrawOptionsEnd headBaseOverlayDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 0, 448, 352).rotate(rotate).color(0.0f, 0.0f, 0.0f).alpha(overlayAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd tongueOverlayDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 1, 448, 352).rotate(rotate).color(0.0f, 0.0f, 0.0f).alpha(overlayAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd jawOverlayDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 2, 448, 352).rotate(rotate).color(0.0f, 0.0f, 0.0f).alpha(overlayAlpha).pos(drawX, drawY + jawDrawYOffset);
        final TextureDrawOptionsEnd teethOverlayDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 3, 448, 352).rotate(rotate).color(0.0f, 0.0f, 0.0f).alpha(overlayAlpha).pos(drawX, drawY + jawDrawYOffset);
        final TextureDrawOptionsEnd runeOverlayDrawOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteIndex, 4, 448, 352).rotate(rotate).alpha(overlayAlpha).pos(drawX, drawY);
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                try {
                    GameResources.whiteShader.use();
                    GameResources.whiteShader.pass1f("white", whiteness);
                    tongueDrawOptions.draw();
                    jawDrawOptions.draw();
                    tongueOverlayDrawOptions.draw();
                    jawOverlayDrawOptions.draw();
                }
                finally {
                    GameResources.whiteShader.stop();
                }
            }
        });
        topList.add(100, new Drawable(){

            @Override
            public void draw(TickManager tickManager) {
                try {
                    GameResources.whiteShader.use();
                    GameResources.whiteShader.pass1f("white", whiteness);
                    headBaseDrawOptions.draw();
                    headBaseOverlayDrawOptions.draw();
                    teethDrawOptions.draw();
                    teethOverlayDrawOptions.draw();
                    runeOverlayDrawOptions.draw();
                }
                finally {
                    GameResources.whiteShader.stop();
                }
            }
        });
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
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 26;
        int drawY = y - 17;
        MobRegistry.Textures.theVoidMapIcon.initDraw().sprite(0, 0, 422, 272).size(53, 34).draw(drawX, drawY);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return DeathMessageTable.fromRange("thevoid", 5);
    }

    public class StartFadeAbility
    extends MobAbility {
        public void runAndSendFadeIn(int transitionDuration, int keepDuration) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextBoolean(true);
            writer.putNextInt(transitionDuration);
            writer.putNextInt(keepDuration);
            this.runAndSendAbility(content);
        }

        public void runAndSendFadeOut(int transitionDuration) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextBoolean(false);
            writer.putNextInt(transitionDuration);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            float currentAlpha = TheVoidMob.this.getFadeAlpha();
            boolean isFadingIn = reader.getNextBoolean();
            TheVoidMob.this.fadeTransitionDuration = reader.getNextInt();
            if (isFadingIn) {
                TheVoidMob.this.fadeKeepDuration = reader.getNextInt();
                TheVoidMob.this.fadeStartTime = TheVoidMob.this.getTime() - (long)((int)((1.0f - currentAlpha) * (float)TheVoidMob.this.fadeTransitionDuration));
            } else {
                TheVoidMob.this.fadeKeepDuration = 0;
                TheVoidMob.this.fadeStartTime = TheVoidMob.this.getTime() - (long)TheVoidMob.this.fadeTransitionDuration - (long)((int)(currentAlpha * (float)TheVoidMob.this.fadeTransitionDuration));
            }
        }
    }

    public static class TheVoidMobAI<T extends TheVoidMob>
    extends SequenceAINode<T> {
        public TheVoidMobAI() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (((TheVoidMob)mob).isInDeathAnimation()) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.FAILURE;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(6400){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            int minIdleTime = 500;
            int maxIdleTime = 1000;
            AttackStageManagerNode phase1Stages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<TheVoidMob>(new IsolateRunningAINode(phase1Stages), m -> !m.isVulnerable && m.getHealthPercent() > 0.5f, AINodeResult.SUCCESS));
            phase1Stages.addChild(new TheVoidReformHornsStage(false));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidHornBulletHell(400.0f));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidBlackHoleStage(0));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidMultiClawSlamStage(500.0f, 10000, 4.0f, 1.0f));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidMovingRainStage(20.0f, 25.0f, 100, 150, 600.0f));
            phase1Stages.addChild(new TheVoidReformHornsStage(false));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidClawGroundShatterStage(false));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime * 2, maxIdleTime * 2));
            phase1Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase1Stages.addChild(new TheVoidClawGroundShatterStage(true));
            phase1Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase1Stages.addChild(new StopMovingAttackStage());
            phase1Stages.addChild(new TheVoidTeleportAboveTargetStage());
            phase1Stages.addChild(new IdleTimeAttackStage(100, 500));
            AttackStageManagerNode beamStages = new AttackStageManagerNode();
            phase1Stages.addChild(new ConditionAINode<TheVoidMob>(beamStages, m -> !m.isAnyHornBroken(), AINodeResult.SUCCESS));
            beamStages.addChild(new StopMovingAttackStage());
            beamStages.addChild(new TheVoidLaserHornsStage());
            beamStages.addChild(new IdleTimeAttackStage(200));
            beamStages.addChild(new TheVoidLaserMouthStage(750));
            phase1Stages.addChild(new TheVoidClawBeamStage(true));
            minIdleTime = 200;
            maxIdleTime = 400;
            AttackStageManagerNode phase2Stages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<TheVoidMob>(new IsolateRunningAINode(phase2Stages), m -> !m.isVulnerable && m.getHealthPercent() <= 0.5f, AINodeResult.SUCCESS));
            phase2Stages.addChild(new TheVoidConjureMoreClawsStage());
            phase2Stages.addChild(new TheVoidReformHornsStage(false));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidMovingRainStage(20.0f, 25.0f, 100, 150, 600.0f));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase2Stages.addChild(new StopMovingAttackStage());
            phase2Stages.addChild(new TheVoidTeleportAboveTargetStage());
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidClawGroundAtPositionShatterStage(false));
            phase2Stages.addChild(new TheVoidClawGroundAtPositionShatterStage(false));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidClawGroundShatterStage(false));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime * 2, maxIdleTime * 2));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidClawGroundShatterStage(false));
            phase2Stages.addChild(new TheVoidReformHornsStage(false));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime * 2, maxIdleTime * 2));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidBlackHoleStage(400));
            phase2Stages.addChild(new TheVoidBlackHoleStage(400));
            phase2Stages.addChild(new TheVoidBlackHoleStage(400));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidHornBulletHell(400.0f));
            phase2Stages.addChild(new IdleTimeAttackStage(minIdleTime, maxIdleTime));
            phase2Stages.addChild(new StopMovingAttackStage());
            phase2Stages.addChild(new TheVoidTeleportAboveTargetStage());
            phase2Stages.addChild(new IdleTimeAttackStage(100, 500));
            beamStages = new AttackStageManagerNode();
            phase2Stages.addChild(new ConditionAINode<TheVoidMob>(beamStages, m -> !m.isAnyHornBroken(), AINodeResult.SUCCESS));
            beamStages.addChild(new StopMovingAttackStage());
            beamStages.addChild(new TheVoidLaserHornsStage());
            beamStages.addChild(new IdleTimeAttackStage(200));
            beamStages.addChild(new TheVoidLaserMouthStage(750));
            phase2Stages.addChild(new TheVoidClawBeamStage(true));
            phase2Stages.addChild(new FlyToRandomPositionAttackStage(false, 600));
            phase2Stages.addChild(new TheVoidMultiClawSlamStage(500.0f, 10000, 6.0f, 4.0f));
            AttackStageManagerNode vulnerableStages = new AttackStageManagerNode();
            this.addChild(new ConditionAINode<TheVoidMob>(new IsolateRunningAINode(vulnerableStages), m -> m.isVulnerable, AINodeResult.SUCCESS));
            vulnerableStages.addChild(new TheVoidStunnedStage());
            vulnerableStages.addChild(new TheVoidReformHornsStage(true));
        }

        private int getIdleTime(T mob, int minTime, int maxTime) {
            return GameMath.lerp(((Mob)mob).getHealthPercent(), minTime, maxTime);
        }
    }
}

