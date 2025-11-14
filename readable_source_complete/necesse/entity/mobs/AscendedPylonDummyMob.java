/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.AscendedBatJailLevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedLightningLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedPylonChargeUpAttackLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeWarningEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.PossessedSettlerMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedBeamMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedGauntletMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedSunlightChampionStage;
import necesse.entity.mobs.mobMovement.MobMovementSpiral;
import necesse.entity.mobs.mobMovement.MobMovementSpiralLevelPos;
import necesse.entity.objectEntity.AscendedPylonObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AscendedBoltProjectile;
import necesse.entity.projectile.AscendedShardBombProjectile;
import necesse.entity.projectile.AscendedSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.AscendedPylonObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.incursions.CrystalHollowBiome;
import necesse.level.maps.biomes.incursions.GraveyardBiome;
import necesse.level.maps.biomes.incursions.SlimeCaveBiome;
import necesse.level.maps.biomes.incursions.SpiderCastleBiome;
import necesse.level.maps.biomes.plains.PlainsBiome;
import necesse.level.maps.light.GameLight;

public class AscendedPylonDummyMob
extends HostileMob {
    public static int CHARGE_PARTICLE_HEIGHT = 46;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(10000, 16000, 20000, 25000, 32000);
    public static GameDamage ASCENDED_BEAM_DAMAGE = new GameDamage(120.0f);
    public static GameDamage MAGIC_VOLLEY_DAMAGE = new GameDamage(120.0f);
    public static GameDamage SLIME_QUAKE_DAMAGE = new GameDamage(120.0f);
    public static GameDamage LIGHTNING_DAMAGE = new GameDamage(120.0f);
    public static GameDamage SHARD_BOMB_DAMAGE = new GameDamage(110.0f);
    public static GameDamage SLASH_DAMAGE = new GameDamage(100.0f);
    public static MobSpawnTable defaultMobSpawnTable = new MobSpawnTable().add(100, "possessedsettler");
    public static MobSpawnTable duskSettlersSpawnTable = new MobSpawnTable().add(100, (level, client, spawnTile) -> {
        PossessedSettlerMob mob = (PossessedSettlerMob)MobRegistry.getMob("possessedsettler", level);
        mob.setArmor("duskhelmet", "duskchestplate", "duskboots", true);
        InventoryItem weapon = new InventoryItem(GameRandom.globalRandom.getOneOf("slimestaff", "phantompopper"));
        mob.setWeapon(weapon, true);
        return mob;
    });
    public static MobSpawnTable dawnSettlersSpawnTable = new MobSpawnTable().add(100, (level, client, spawnTile) -> {
        PossessedSettlerMob mob = (PossessedSettlerMob)MobRegistry.getMob("possessedsettler", level);
        mob.setArmor("dawnhelmet", "dawnchestplate", "dawnboots", true);
        InventoryItem weapon = (InventoryItem)GameRandom.globalRandom.getOneOf(() -> {
            InventoryItem item = new InventoryItem("slimeglaive");
            item.getGndData().setFloat("damageMod", 0.5f);
            return item;
        }, () -> {
            InventoryItem item = new InventoryItem("causticexecutioner");
            item.getGndData().setFloat("damageMod", 1.0f);
            return item;
        });
        mob.setWeapon(weapon, true);
        return mob;
    });
    private int aliveTimer;
    private int tileX;
    private int tileY;
    private AscendedPylonObjectEntity objectEntity;
    public TicksPerSecond spawnTicker = TicksPerSecond.ticksPerSecond(2);
    public int lifeTimeHealthSpawns = 150;
    public int maxHealthSpawnsPerSecond = 20;
    public float nearbyPlayerSpawnsPerSecond = 0.1f;
    public int lastGameTickHealth;
    public int lastSpawnTickHealth;
    public float nextSpawnBuffer;
    public float healthDamageToTriggerInvincibility = 0.35f;
    public int invincibilityRandomMinCooldown = 90000;
    public int invincibilityRandomMaxCooldown = 120000;
    public int invincibilityDuration = 8000;
    protected float nextInvincibilityBuffer;
    public final IntMobAbility triggerInvincibility;
    protected int invincibilityTimeLeft;
    public int nextAttackMinCooldown = 2000;
    public int nextAttackMaxCooldown = 7000;
    protected float nextAttackBuffer;
    protected AscendedPylonAttack currentAttack;
    protected MobSpawnTable currentSpawnTable = defaultMobSpawnTable;
    protected long currentEffectStartTime;
    protected long currentEffectEndTime;
    protected int currentEffectSpriteY = -1;
    public final EmptyMobAbility triggerElectricExplosionAbility;
    public final EmptyMobAbility playMagicBoltSoundAbility;
    public final EmptyMobAbility playLightningBoltSoundAbility;
    public final IntMobAbility playHoverEffectAbility;

    public AscendedPylonDummyMob() {
        super(20000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setArmor(40);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-22, -18, 44, 36);
        this.selectBox = new Rectangle(-22, -66, 44, 80);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
        this.setTeam(-2);
        this.triggerInvincibility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                AscendedPylonDummyMob.this.invincibilityTimeLeft = AscendedPylonDummyMob.this.invincibilityDuration;
            }
        });
        this.triggerElectricExplosionAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!AscendedPylonDummyMob.this.isServer()) {
                    int levelX = AscendedPylonDummyMob.this.getX();
                    int levelY = AscendedPylonDummyMob.this.getY();
                    for (int i = 0; i < 50; ++i) {
                        int lifeTime = GameRandom.globalRandom.getIntBetween(500, 5000);
                        float lifePerc = (float)lifeTime / 5000.0f;
                        float endHeight = (float)CHARGE_PARTICLE_HEIGHT + (float)GameRandom.globalRandom.getIntBetween(50, 150) * lifePerc;
                        AscendedPylonDummyMob.this.getLevel().entityManager.addParticle((float)levelX + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), (float)levelY + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-100.0f, 100.0f), GameRandom.globalRandom.getFloatBetween(-100.0f, 100.0f), 1.0f).heightMoves(CHARGE_PARTICLE_HEIGHT, endHeight).colorRandom(300.0f, 0.8f, 0.5f, 5.0f, 0.1f, 0.1f).lifeTime(lifeTime);
                    }
                    SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(AscendedPylonDummyMob.this).volume(1.5f).falloffDistance(2000));
                }
            }
        });
        this.playMagicBoltSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                SoundManager.playSound(GameResources.laserBlast1, (SoundEffect)SoundEffect.effect(AscendedPylonDummyMob.this).volume(0.3f).falloffDistance(2000));
            }
        });
        this.playLightningBoltSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                SoundManager.playSound(GameResources.zap1, (SoundEffect)SoundEffect.effect(AscendedPylonDummyMob.this).volume(0.3f).falloffDistance(2000));
            }
        });
        this.playHoverEffectAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (value == -1) {
                    AscendedPylonDummyMob.this.currentEffectEndTime = AscendedPylonDummyMob.this.getLocalTime();
                } else {
                    AscendedPylonDummyMob.this.currentEffectStartTime = AscendedPylonDummyMob.this.getLocalTime();
                    AscendedPylonDummyMob.this.currentEffectSpriteY = value;
                    AscendedPylonDummyMob.this.currentEffectEndTime = 0L;
                }
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.nextInvincibilityBuffer = GameRandom.globalRandom.getIntBetween(this.invincibilityRandomMinCooldown, this.invincibilityRandomMaxCooldown);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.currentAttack != null) {
            this.currentAttack.movementTick(delta);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickAlive();
        this.tickInvincibilityTimer();
        if (this.isInCombat()) {
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickAlive();
        this.tickInvincibilityTimer();
        this.spawnTicker.gameTick();
        if (this.spawnTicker.shouldTick()) {
            float ticksPerSecond = this.spawnTicker.getTicksPerSecond();
            boolean hasPlayers = this.getLevel().entityManager.players.streamAreaTileRange(this.getX(), this.getY(), 25).anyMatch(p -> !p.removed());
            if (hasPlayers) {
                this.nextSpawnBuffer += this.nearbyPlayerSpawnsPerSecond / ticksPerSecond;
            }
            int nextHealth = this.getHealth();
            int lostHealth = this.lastSpawnTickHealth - nextHealth;
            this.lastSpawnTickHealth = nextHealth;
            if (lostHealth > 0) {
                float modifier = (float)this.getMaxHealth() / (float)this.getMaxHealthFlat();
                float lifePerProjectile = (float)this.getMaxHealthFlat() / (float)this.lifeTimeHealthSpawns * modifier;
                float increase = (float)lostHealth / lifePerProjectile;
                this.nextSpawnBuffer += Math.min((float)this.maxHealthSpawnsPerSecond / ticksPerSecond, increase);
            }
            while (this.nextSpawnBuffer > 1.0f) {
                this.nextSpawnBuffer -= 1.0f;
                if (this.currentSpawnTable == null || this.spawnMob(this.currentSpawnTable, 1)) continue;
                this.nextSpawnBuffer += 0.5f;
            }
        }
        int nextHealth = this.getHealth();
        int lostHealth = this.lastGameTickHealth - nextHealth;
        this.lastGameTickHealth = nextHealth;
        if (this.invincibilityTimeLeft <= 0) {
            if (lostHealth > 0) {
                float modifier = (float)this.getMaxHealth() / (float)this.getMaxHealthFlat();
                float lifePerFullInvincibilityCooldown = (float)this.getMaxHealthFlat() * this.healthDamageToTriggerInvincibility * modifier;
                float cooldownPercentIncrease = (float)lostHealth / lifePerFullInvincibilityCooldown;
                float cooldownTimeDecrease = (float)this.invincibilityRandomMaxCooldown * cooldownPercentIncrease;
                this.nextInvincibilityBuffer -= cooldownTimeDecrease;
            }
            this.nextInvincibilityBuffer -= 50.0f;
            if (this.nextInvincibilityBuffer <= 0.0f) {
                this.triggerInvincibility.runAndSend(this.invincibilityDuration);
                this.nextInvincibilityBuffer = GameRandom.globalRandom.getIntBetween(this.invincibilityRandomMinCooldown, this.invincibilityRandomMaxCooldown);
            }
        }
        if (this.currentAttack != null) {
            this.currentAttack.serverTick();
            if (this.currentAttack.isDone()) {
                this.currentAttack.onDone();
                this.currentAttack = null;
                this.playHoverEffectAbility.runAndSend(-1);
            }
        } else if (this.objectEntity != null) {
            float nextAttackCooldown = GameMath.lerp(this.getHealthPercent(), this.nextAttackMinCooldown, this.nextAttackMaxCooldown);
            float increasePerTick = 1.0f / (nextAttackCooldown / 50.0f);
            this.nextAttackBuffer += increasePerTick;
            if (this.nextAttackBuffer >= 1.0f) {
                boolean hasPlayer;
                this.nextAttackBuffer -= 1.0f;
                int playerRange = 800;
                boolean bl = hasPlayer = this.isInCombat() || this.getLevel().entityManager.players.streamArea(this.getX(), this.getY(), playerRange).filter(p -> GameMath.diagonalMoveDistance(p.getX(), p.getY(), this.getX(), this.getY()) < (double)playerRange).findFirst().isPresent();
                if (hasPlayer) {
                    AscendedPylonAttack nextAttack = this.objectEntity.getNextAttack(this);
                    if (nextAttack != null) {
                        this.currentAttack = nextAttack;
                        this.currentAttack.onStarted();
                        MobSpawnTable mobSpawnTable = this.currentAttack.getMobSpawnTable();
                        if (mobSpawnTable != null) {
                            this.currentSpawnTable = mobSpawnTable;
                            int spawnedMobsCount = this.currentAttack.getSpawnedMobsCount();
                            for (int i = 0; i < spawnedMobsCount; ++i) {
                                this.spawnMob(mobSpawnTable, 20);
                            }
                        } else {
                            this.currentSpawnTable = defaultMobSpawnTable;
                        }
                        this.playHoverEffectAbility.runAndSend(this.currentAttack.effectSpriteY);
                    } else {
                        this.nextAttackBuffer = 0.5f;
                    }
                }
            }
        }
    }

    public PlayerMob getClosestPlayer(int range) {
        return this.getLevel().entityManager.players.streamArea(this.getX(), this.getY(), range).filter(p -> GameMath.diagonalMoveDistance(p.getX(), p.getY(), this.getX(), this.getY()) < (double)range).findFirst().orElse(null);
    }

    public boolean spawnMob(MobSpawnTable spawnTable, int attempts) {
        int players = this.getLevel().entityManager.countPlayers(this.getX(), this.getY(), Mob.MOB_SPAWN_AREA);
        float maxNearbyMobs = EntityManager.getSpawnCap(players, 20.0f, -10.0f) * this.getWorldSettings().difficulty.enemySpawnCapModifier * this.getLevel().entityManager.getSpawnCapMod(this.tileX, this.tileY) * 3.0f;
        return this.spawnMob(spawnTable, attempts, maxNearbyMobs);
    }

    public boolean spawnMob(MobSpawnTable spawnTable, int attempts, float maxNearbyMobs) {
        int totalMobs;
        if (maxNearbyMobs > 0.0f && (float)(totalMobs = this.getLevel().entityManager.countMobs(this.getX(), this.getY(), Mob.MOB_SPAWN_AREA, m -> m.isHostile)) >= maxNearbyMobs) {
            return false;
        }
        for (int i = 0; i < attempts; ++i) {
            Collection<Mob> spawnedMobs;
            MobChance randomMob;
            Point spawnTile = EntityManager.getMobSpawnTile(this.getLevel(), this.getX(), this.getY(), Mob.MOB_SPAWN_AREA, null);
            if (spawnTile == null || (randomMob = spawnTable.getRandomMob(this.getLevel(), null, spawnTile, GameRandom.globalRandom, "ascendedpylon")) == null || (spawnedMobs = randomMob.spawnMob(this.getLevel(), null, spawnTile, m -> {
                m.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
                return true;
            }, null, "ascendedpylon")) == null || spawnedMobs.isEmpty()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    protected void tickInvincibilityTimer() {
        if (this.invincibilityTimeLeft > 0) {
            this.invincibilityTimeLeft -= 50;
            if (this.invincibilityTimeLeft <= 0) {
                this.invincibilityTimeLeft = 0;
                if (!this.isServer()) {
                    int particleCount = 25;
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                    float anglePerParticle = 360.0f / (float)particleCount;
                    for (int i = 0; i < particleCount; ++i) {
                        int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
                        float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
                        this.getLevel().entityManager.addParticle(this.x, this.y, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(255, 34, 226)).givesLight(300.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
                    }
                    SoundManager.playSound(GameResources.shatter1, (SoundEffect)SoundEffect.effect(this).volume(0.1f).falloffDistance(2000));
                    SoundManager.playSound(GameResources.fadedeath3, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(2000));
                }
            }
        }
    }

    private void tickAlive() {
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(AscendedPylonObjectEntity entity) {
        this.tileX = entity.tileX;
        this.tileY = entity.tileY;
        this.aliveTimer = 20;
        this.objectEntity = entity;
        this.setPos(this.tileX * 32 + 16, this.tileY * 32 + 16, true);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    public DrawOptions getEffectDrawOptions(GameTexture effectsTexture, int tileX, int tileY, GameLight light, GameCamera camera) {
        if (this.currentEffectSpriteY == -1) {
            return null;
        }
        long timeSinceStart = this.getLocalTime() - this.currentEffectStartTime;
        long timeSinceEnd = this.getLocalTime() - this.currentEffectEndTime;
        float alpha = 1.0f;
        if (timeSinceStart < 1000L) {
            alpha *= Math.max(0.0f, (float)timeSinceStart / 1000.0f);
        }
        if (this.currentEffectEndTime > 0L) {
            if (timeSinceEnd < 1000L) {
                alpha *= Math.max(0.0f, 1.0f - (float)timeSinceEnd / 1000.0f);
            } else {
                this.currentEffectSpriteY = -1;
                return null;
            }
        }
        float animFloat = GameUtils.getAnimFloatContinuous(this.getLocalTime(), 4000);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        return effectsTexture.initDraw().sprite(0, this.currentEffectSpriteY, 96, 64).light(light).alpha(alpha *= GameMath.lerp(animFloat, 0.2f, 0.4f)).pos(drawX + 16 - 48, drawY - 120);
    }

    @Override
    public boolean canTakeDamage() {
        return this.invincibilityTimeLeft <= 0;
    }

    @Override
    public boolean countDamageDealt() {
        return true;
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        GameObject object;
        super.onDeath(attacker, attackers);
        if (this.isServer() && (object = this.getLevel().getObject(this.tileX, this.tileY)) instanceof AscendedPylonObject) {
            this.getLevel().entityManager.doObjectDamageOverride(0, this.tileX, this.tileY, object.objectHealth);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        super.spawnDeathParticles(knockbackX, knockbackY);
        if (this.isClient()) {
            this.getClient().addShockwaveEffect(this.getX(), this.getY() - CHARGE_PARTICLE_HEIGHT, 2000.0f, 50.0f, 500.0f, 100.0f, 150);
        }
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.ascendedPylonDeath).volume(1.8f).fallOffDistance(5000);
    }

    public static abstract class AscendedPylonAttack {
        public final AscendedPylonDummyMob mob;
        public final int effectSpriteY;

        public AscendedPylonAttack(AscendedPylonDummyMob mob, int effectSpriteY) {
            this.mob = mob;
            this.effectSpriteY = effectSpriteY;
        }

        public abstract void onStarted();

        public void movementTick(float delta) {
        }

        public abstract void serverTick();

        public abstract boolean isDone();

        public void onDone() {
        }

        public abstract MobSpawnTable getMobSpawnTable();

        public int getSpawnedMobsCount() {
            return GameMath.lerp(this.mob.getHealthPercent(), 5, 2);
        }
    }

    public static class AscendedSlashesPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public float minSlashesPerSec = 8.0f;
        public float maxSlashesPerSec = 12.0f;
        public int minSlashes = 40;
        public int maxSlashes = 60;
        public float nextFireBuffer;
        public int totalFired;
        public boolean firing;

        public AscendedSlashesPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, 5);
        }

        @Override
        public void onChargedEventReady() {
            this.firing = true;
        }

        @Override
        public void movementTick(float delta) {
            super.movementTick(delta);
            if (this.firing) {
                GameRandom random = GameRandom.globalRandom;
                float projectilesPerSec = GameMath.lerp(this.mob.getHealthPercent(), this.maxSlashesPerSec, this.minSlashesPerSec);
                float projectilesPerTick = projectilesPerSec * delta / 1000.0f;
                this.nextFireBuffer += projectilesPerTick;
                while (this.nextFireBuffer >= 1.0f) {
                    this.nextFireBuffer -= 1.0f;
                    ++this.totalFired;
                    final int startX = this.mob.getX() + random.getIntBetween(-800, 800);
                    final int startY = this.mob.getY() + random.getIntBetween(-800, 800);
                    final int randomX = random.getIntBetween(-200, 200);
                    final int randomY = random.getIntBetween(-200, 200);
                    final float angle = random.getIntBetween(-180, 180);
                    EmpressSlashWarningProjectile slashProjectile = new EmpressSlashWarningProjectile(startX + randomX, startY + randomY, angle, SLASH_DAMAGE, this.mob);
                    this.mob.getLevel().entityManager.projectiles.add(slashProjectile);
                    EmpressSlashWarningProjectile slashProjectileReverse = new EmpressSlashWarningProjectile(startX + randomX, startY + randomY, angle - 180.0f, SLASH_DAMAGE, this.mob);
                    this.mob.getLevel().entityManager.projectiles.add(slashProjectileReverse);
                    this.mob.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent(0.5f){

                        @Override
                        public void onWaitOver() {
                            AscendedSlashProjectile slashProjectile = new AscendedSlashProjectile(startX + randomX, startY + randomY, angle, SLASH_DAMAGE, mob);
                            mob.getLevel().entityManager.projectiles.add(slashProjectile);
                            AscendedSlashProjectile slashProjectileReverse = new AscendedSlashProjectile(startX + randomX, startY + randomY, angle - 180.0f, SLASH_DAMAGE, mob);
                            mob.getLevel().entityManager.projectiles.add(slashProjectileReverse);
                        }
                    });
                    if (this.totalFired % 2 != 1) continue;
                    this.mob.playLightningBoltSoundAbility.runAndSend();
                }
            }
        }

        @Override
        public boolean isDone() {
            float totalProjectiles = GameMath.lerp(this.mob.getHealthPercent(), this.maxSlashes, this.minSlashes);
            return (float)this.totalFired >= totalProjectiles;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return SpiderCastleBiome.mobs;
        }
    }

    public static class AscendedShardBombsPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public float minBombsPerSec = 4.0f;
        public float maxBombsPerSec = 6.0f;
        public int minBombs = 20;
        public int maxBombs = 30;
        public float nextFireBuffer;
        public int totalFired;
        public boolean firing;

        public AscendedShardBombsPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, 0);
        }

        @Override
        public void onChargedEventReady() {
            this.firing = true;
        }

        @Override
        public void movementTick(float delta) {
            super.movementTick(delta);
            if (this.firing) {
                GameRandom random = GameRandom.globalRandom;
                float projectilesPerSec = GameMath.lerp(this.mob.getHealthPercent(), this.maxBombsPerSec, this.minBombsPerSec);
                float projectilesPerTick = projectilesPerSec * delta / 1000.0f;
                this.nextFireBuffer += projectilesPerTick;
                while (this.nextFireBuffer >= 1.0f) {
                    this.nextFireBuffer -= 1.0f;
                    ++this.totalFired;
                    float randomAngle = random.getFloatBetween(0.0f, 360.0f);
                    Point2D.Float dir = GameMath.getAngleDir(randomAngle);
                    float randomDistance = random.getFloatBetween(300.0f, 1200.0f);
                    int targetX = (int)((float)this.mob.getX() + dir.x * randomDistance);
                    int targetY = (int)((float)this.mob.getY() + dir.y * randomDistance);
                    AscendedShardBombProjectile projectile = new AscendedShardBombProjectile(this.mob.x, this.mob.y, targetX, targetY, 100, (int)randomDistance, SHARD_BOMB_DAMAGE, this.mob);
                    this.mob.getLevel().entityManager.projectiles.add(projectile);
                    this.mob.playLightningBoltSoundAbility.runAndSend();
                }
            }
        }

        @Override
        public boolean isDone() {
            float totalProjectiles = GameMath.lerp(this.mob.getHealthPercent(), this.maxBombs, this.minBombs);
            return (float)this.totalFired >= totalProjectiles;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return CrystalHollowBiome.mobs;
        }
    }

    public static class SpawnGauntletsPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public ArrayList<AscendedGauntletMob> gauntletMobs;

        public SpawnGauntletsPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, 3);
        }

        @Override
        public void onChargedEventReady() {
            PlayerMob player = this.mob.getClosestPlayer(1280);
            this.gauntletMobs = AscendedSunlightChampionStage.spawnGauntletPair(this.mob, player);
        }

        @Override
        public boolean isDone() {
            if (this.gauntletMobs != null) {
                this.gauntletMobs.removeIf(Entity::removed);
            }
            return this.gauntletMobs != null && this.gauntletMobs.isEmpty();
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return dawnSettlersSpawnTable;
        }
    }

    public static class BatJailPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public AscendedBatJailLevelEvent event;

        public BatJailPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, 2);
        }

        @Override
        public void onChargedEventReady() {
            this.event = new AscendedBatJailLevelEvent(this.mob, this.mob.x, this.mob.y);
            this.mob.getLevel().entityManager.events.addHidden(this.event);
        }

        @Override
        public boolean isDone() {
            return this.event != null && this.event.isOver();
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return GraveyardBiome.mobs;
        }
    }

    public static class LightningStrikesPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public float minStrikesPerSec = 3.0f;
        public float maxStrikesPerSec = 5.0f;
        public int minStrikes = 15;
        public int maxStrikes = 25;
        public float nextFireBuffer;
        public int totalFired;
        public boolean firing;

        public LightningStrikesPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, -1);
        }

        @Override
        public void onChargedEventReady() {
            this.firing = true;
        }

        @Override
        public void movementTick(float delta) {
            super.movementTick(delta);
            if (this.firing) {
                GameRandom random = GameRandom.globalRandom;
                float projectilesPerSec = GameMath.lerp(this.mob.getHealthPercent(), this.maxStrikesPerSec, this.minStrikesPerSec);
                float projectilesPerTick = projectilesPerSec * delta / 1000.0f;
                this.nextFireBuffer += projectilesPerTick;
                while (this.nextFireBuffer >= 1.0f) {
                    this.nextFireBuffer -= 1.0f;
                    ++this.totalFired;
                    float randomAngle = random.getFloatBetween(0.0f, 360.0f);
                    Point2D.Float dir = GameMath.getAngleDir(randomAngle);
                    float randomDistance = random.getFloatBetween(100.0f, 600.0f);
                    int targetX = (int)((float)this.mob.getX() + dir.x * randomDistance);
                    int targetY = (int)((float)this.mob.getY() + dir.y * randomDistance);
                    AscendedLightningLevelEvent event = new AscendedLightningLevelEvent(this.mob, 300, LIGHTNING_DAMAGE, targetX, targetY);
                    this.mob.getLevel().entityManager.events.add(event);
                    this.mob.playLightningBoltSoundAbility.runAndSend();
                }
            }
        }

        @Override
        public boolean isDone() {
            float totalProjectiles = GameMath.lerp(this.mob.getHealthPercent(), this.maxStrikes, this.minStrikes);
            return (float)this.totalFired >= totalProjectiles;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return null;
        }
    }

    public static class BeamTornadoPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public float minBeamsPerSec = 3.0f;
        public float maxBeamsPerSec = 5.0f;
        public int minBeams = 15;
        public int maxBeams = 25;
        public float nextFireBuffer;
        public int totalFired;
        public boolean firing;

        public BeamTornadoPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, -1);
        }

        @Override
        public void onChargedEventReady() {
            this.firing = true;
        }

        @Override
        public void movementTick(float delta) {
            super.movementTick(delta);
            if (this.firing) {
                GameRandom random = GameRandom.globalRandom;
                float projectilesPerSec = GameMath.lerp(this.mob.getHealthPercent(), this.maxBeamsPerSec, this.minBeamsPerSec);
                float projectilesPerTick = projectilesPerSec * delta / 1000.0f;
                this.nextFireBuffer += projectilesPerTick;
                while (this.nextFireBuffer >= 1.0f) {
                    this.nextFireBuffer -= 1.0f;
                    ++this.totalFired;
                    Mob beam = MobRegistry.getMob("ascendedbeam", this.mob.getLevel());
                    ((AscendedBeamMob)beam).damage = ASCENDED_BEAM_DAMAGE;
                    ((AscendedBeamMob)beam).master.uniqueID = this.mob.getUniqueID();
                    beam.setPos(this.mob.x, this.mob.y, true);
                    int distanceAtTheEnd = 4000;
                    int radiusDecrease = random.getIntBetween(100, 300);
                    int semiCircles = MobMovementSpiral.getSemiCircles(distanceAtTheEnd, radiusDecrease, 10.0f);
                    int startAngle = random.nextInt(360);
                    boolean clockwise = random.nextBoolean();
                    beam.setMovement(new MobMovementSpiralLevelPos(beam, this.mob.x, this.mob.y, 10.0f, semiCircles, -radiusDecrease, beam.getSpeed(), startAngle, clockwise));
                    this.mob.getLevel().entityManager.mobs.add(beam);
                }
            }
        }

        @Override
        public boolean isDone() {
            float totalProjectiles = GameMath.lerp(this.mob.getHealthPercent(), this.maxBeams, this.minBeams);
            return (float)this.totalFired >= totalProjectiles;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return PlainsBiome.deepCaveMobs;
        }
    }

    public static class MagicVolleyAscendedPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public float minProjectilesPerSec = 8.0f;
        public float maxProjectilesPerSec = 12.0f;
        public int minProjectiles = 20;
        public int maxProjectiles = 30;
        public float nextProjectileBuffer;
        public int projectilesFired;
        public boolean firingProjectiles;

        public MagicVolleyAscendedPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 0, 4);
        }

        @Override
        public void onChargedEventReady() {
            this.firingProjectiles = true;
        }

        @Override
        public void movementTick(float delta) {
            super.movementTick(delta);
            if (this.firingProjectiles) {
                GameRandom random = GameRandom.globalRandom;
                float projectilesPerSec = GameMath.lerp(this.mob.getHealthPercent(), this.maxProjectilesPerSec, this.minProjectilesPerSec);
                float projectilesPerTick = projectilesPerSec * delta / 1000.0f;
                this.nextProjectileBuffer += projectilesPerTick;
                while (this.nextProjectileBuffer >= 1.0f) {
                    float targetY;
                    float targetX;
                    this.nextProjectileBuffer -= 1.0f;
                    ++this.projectilesFired;
                    PlayerMob player = this.mob.getClosestPlayer(1280);
                    if (player != null) {
                        targetX = player.getX();
                        targetY = player.getY();
                    } else {
                        float angle = random.getFloatBetween(0.0f, 360.0f);
                        Point2D.Float dir = GameMath.getAngleDir(angle);
                        targetX = (float)this.mob.getX() + dir.x * 150.0f;
                        targetY = (float)this.mob.getY() + dir.y * 150.0f;
                    }
                    AscendedBoltProjectile projectile = new AscendedBoltProjectile(this.mob.getLevel(), this.mob.x, this.mob.y, targetX + (float)random.getIntBetween(-50, 50), targetY + (float)random.getIntBetween(-50, 50), 200.0f, 1500, MAGIC_VOLLEY_DAMAGE, this.mob);
                    this.mob.getLevel().entityManager.projectiles.add(projectile);
                    this.mob.playMagicBoltSoundAbility.runAndSend();
                }
            }
        }

        @Override
        public boolean isDone() {
            float totalProjectiles = GameMath.lerp(this.mob.getHealthPercent(), this.maxProjectiles, this.minProjectiles);
            return (float)this.projectilesFired >= totalProjectiles;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return duskSettlersSpawnTable;
        }
    }

    public static class SlimeQuakeAscendedPylonAttack
    extends ChargeEventAscendedPylonAttack {
        public int minVelocity = 500;
        public int maxVelocity = 800;
        public int minWarningTime = 1000;
        public int maxWarningTime = 2000;

        public SlimeQuakeAscendedPylonAttack(AscendedPylonDummyMob mob) {
            super(mob, 250, 1);
        }

        @Override
        public void onChargedEventReady() {
            final int range = 1000;
            final int velocity = GameMath.lerp(this.mob.getHealthPercent(), this.maxVelocity, this.minVelocity);
            final int warningTime = GameMath.lerp(this.mob.getHealthPercent(), this.minWarningTime, this.maxWarningTime);
            int totalEvents = GameRandom.globalRandom.getIntBetween(8, 12);
            if (totalEvents % 2 == 1) {
                ++totalEvents;
            }
            float totalSeconds = GameRandom.globalRandom.getFloatBetween(0.5f, 1.0f);
            float secondsPerEvent = totalSeconds / (float)totalEvents;
            float baseAngleOffset = GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f);
            final float angleExtent = 360.0f / (float)totalEvents;
            ArrayList<Integer> eventOrder = new ArrayList<Integer>(totalEvents);
            for (int i = 0; i < totalEvents; ++i) {
                eventOrder.add(i);
            }
            Collections.shuffle(eventOrder, GameRandom.globalRandom);
            int offset = GameRandom.globalRandom.nextInt(135);
            for (int index = 0; index < totalEvents; ++index) {
                int currentIndex = (Integer)eventOrder.get(index);
                final int thisOffset = currentIndex % 2 == 0 ? offset : offset + 200;
                final float thisAngle = (float)currentIndex * angleExtent + baseAngleOffset;
                this.mob.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent((float)index * secondsPerEvent){

                    @Override
                    public void onWaitOver() {
                        if (mob.removed()) {
                            return;
                        }
                        mob.getLevel().entityManager.events.add(new AscendedSlimeQuakeWarningEvent((Mob)mob, mob.getX(), mob.getY(), new GameRandom(), thisAngle, angleExtent, (float)velocity, (float)range, warningTime, (float)thisOffset));
                        mob.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent((float)warningTime / 1000.0f){

                            @Override
                            public void onWaitOver() {
                                if (mob.removed()) {
                                    return;
                                }
                                mob.getLevel().entityManager.events.add(new AscendedSlimeQuakeEvent(mob, mob.getX(), mob.getY(), new GameRandom(), thisAngle, angleExtent, SLIME_QUAKE_DAMAGE, velocity, 50.0f, range, thisOffset));
                            }
                        });
                    }
                });
            }
        }

        @Override
        public boolean isDone() {
            return this.chargeEvent == null || this.chargeEvent.getEventPercentProgress() >= 1.0f;
        }

        @Override
        public MobSpawnTable getMobSpawnTable() {
            return SlimeCaveBiome.mobs;
        }
    }

    public static abstract class ChargeEventAscendedPylonAttack
    extends AscendedPylonAttack {
        public final int triggerAtTimeLeft;
        protected AscendedPylonChargeUpAttackLevelEvent chargeEvent;
        protected boolean spawnedAttackEvent;
        public float healthDamageToTriggerChargeEvent = 0.1f;
        public int chargeEventMinChargeTime = 2000;
        public int chargeEventMaxChargeTime = 6000;
        public int lastGameTickHealth;

        public ChargeEventAscendedPylonAttack(AscendedPylonDummyMob mob, int triggerAtTimeLeft, int effectSpriteY) {
            super(mob, effectSpriteY);
            this.triggerAtTimeLeft = triggerAtTimeLeft;
        }

        @Override
        public void onStarted() {
            this.spawnedAttackEvent = false;
            int changeTime = GameMath.lerp(this.mob.getHealthPercent(), this.chargeEventMaxChargeTime, this.chargeEventMinChargeTime);
            this.chargeEvent = new AscendedPylonChargeUpAttackLevelEvent(this.mob, GameRandom.globalRandom.nextInt(), changeTime);
            this.mob.getLevel().entityManager.events.add(this.chargeEvent);
            this.lastGameTickHealth = this.mob.getHealth();
        }

        @Override
        public void serverTick() {
            if (this.chargeEvent == null) {
                if (!this.spawnedAttackEvent) {
                    this.spawnedAttackEvent = true;
                    this.onChargedEventReady();
                }
                return;
            }
            int nextHealth = this.mob.getHealth();
            int lostHealth = this.lastGameTickHealth - nextHealth;
            this.lastGameTickHealth = nextHealth;
            float timeLeft = this.chargeEvent.getEventTimeLeft();
            if (lostHealth > 0 && timeLeft > (float)this.triggerAtTimeLeft) {
                float modifier = (float)this.mob.getMaxHealth() / (float)this.mob.getMaxHealthFlat();
                float lifePerFullChargeEvent = (float)this.mob.getMaxHealthFlat() * this.healthDamageToTriggerChargeEvent * modifier;
                float chargeEventPercentIncrease = (float)lostHealth / lifePerFullChargeEvent;
                float chargeEventTimeIncrease = (float)this.chargeEvent.chargeTime * chargeEventPercentIncrease;
                this.chargeEvent.setCurrentTimeAction.runAndSend(Math.min(this.chargeEvent.currentTime + chargeEventTimeIncrease, (float)(this.chargeEvent.chargeTime - this.triggerAtTimeLeft)));
            }
            if (!this.spawnedAttackEvent && timeLeft <= (float)this.triggerAtTimeLeft) {
                this.spawnedAttackEvent = true;
                this.onChargedEventReady();
            }
        }

        public abstract void onChargedEventReady();

        @Override
        public void onDone() {
            super.onDone();
            if (this.chargeEvent != null) {
                this.chargeEvent.over();
                this.chargeEvent = null;
                this.mob.triggerElectricExplosionAbility.runAndSend();
            }
        }
    }
}

