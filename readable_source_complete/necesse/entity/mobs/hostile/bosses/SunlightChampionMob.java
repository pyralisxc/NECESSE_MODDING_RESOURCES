/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.SupernovaExplosionEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SunlightOrbEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
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
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SunlightChampionMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "sunlightsexamvinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("dawnhelmet", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("dawnchestplate", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("dawnboots", 1, new GNDItemMap().setInt("upgradeLevel", 100)));
    public static LootTable privateLootTable = new LootTable(new LootItemMultiplierIgnored(uniqueDrops), new ChanceLootItem(0.25f, "kineticboots").preventLootMultiplier());
    public float currentHeight;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    public ArrayList<SunlightGauntletMob> gauntlets = new ArrayList();
    public boolean[] gauntletAttackStatus;
    public long superNovaEndTime;
    public SoundPlayer superNovaRumbleSound;
    public ParticleTypeSwitcher superNovaParticleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public static GameDamage collisionDamage = new GameDamage(110.0f);
    public static GameDamage gauntletCollisionDamage = new GameDamage(130.0f);
    public static GameDamage sunlightOrbDamage = new GameDamage(115.0f);
    public static GameDamage supernovaDamage = new GameDamage(400.0f);
    protected final IntMobAbility startSupernovaCharge;
    protected final EmptyMobAbility supernovaSoundAbility;
    protected final EmptyMobAbility rocketPunchSoundAbility;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(35000, 46000, 52000, 58000, 69000);

    public SunlightChampionMob() {
        super(40000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(80.0f);
        this.setArmor(40);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-30, -20, 60, 70);
        this.hitBox = new Rectangle(-25, -60, 50, 160);
        this.selectBox = new Rectangle(-70, -85, 140, 210);
        this.startSupernovaCharge = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                SunlightChampionMob.this.superNovaEndTime = SunlightChampionMob.this.getTime() + (long)value;
                if (SunlightChampionMob.this.isClient()) {
                    SunlightChampionMob.this.getClient().startCameraShake(null, value, 60, 3.0f, 3.0f, false);
                }
            }
        });
        this.supernovaSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SunlightChampionMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(SunlightChampionMob.this).volume(2.0f).pitch(0.5f).falloffDistance(5000));
            }
        });
        this.rocketPunchSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!SunlightChampionMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.firespell1, (SoundEffect)SoundEffect.effect(SunlightChampionMob.this).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 2.5f)).falloffDistance(4000));
            }
        });
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
        SoundManager.playSound(GameResources.sunlightchampionhurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.sunlightchampiondeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        for (SunlightGauntletMob mob : this.gauntlets) {
            writer.putNextBoolean((Boolean)mob.isAttacking.get());
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.gauntletAttackStatus = new boolean[2];
        for (int i = 0; i < 2; ++i) {
            this.gauntletAttackStatus[i] = reader.getNextBoolean();
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
        this.ai = new BehaviourTreeAI<SunlightChampionMob>(this, new SunlightChampionAI());
        GameRandom uniqueIDRandom = new GameRandom();
        for (int i = 0; i < 2; ++i) {
            uniqueIDRandom.setSeed(this.getUniqueID() + i);
            SunlightGauntletMob gauntlet = new SunlightGauntletMob();
            gauntlet.leftHanded = i == 0;
            gauntlet.setLevel(this.getLevel());
            gauntlet.setUniqueID(SunlightChampionMob.getNewUniqueID(this.getLevel(), uniqueIDRandom));
            gauntlet.setPos(this.x, this.y, true);
            gauntlet.setMaxHealth(this.getMaxHealth());
            gauntlet.setHealthHidden(this.getHealth());
            gauntlet.master.uniqueID = this.getUniqueID();
            if (this.gauntletAttackStatus != null) {
                gauntlet.isAttacking.set(this.gauntletAttackStatus[i]);
            }
            this.getLevel().entityManager.mobs.addHidden(gauntlet);
            this.gauntlets.add(gauntlet);
        }
        if (this.isClient()) {
            SoundManager.playSound(GameResources.sunlightchampionbegin, (SoundEffect)SoundEffect.effect(this).volume(0.9f).falloffDistance(4000));
        }
    }

    @Override
    public void tickMovement(float delta) {
        float desiredHeight = this.getDesiredHeight();
        float heightDelta = desiredHeight - this.currentHeight;
        float heightSpeed = Math.abs(heightDelta) * 2.0f + 10.0f;
        float heightToMove = heightSpeed * delta / 250.0f;
        this.currentHeight = Math.abs(heightDelta) < heightToMove ? desiredHeight : (this.currentHeight += Math.signum(heightDelta) * heightToMove);
        super.tickMovement(delta);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.SunlightsExam, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        int dx = GameRandom.globalRandom.getIntBetween(-25, 25);
        int dy = GameRandom.globalRandom.getIntBetween(-1, -10);
        int colorRandomizer = GameRandom.globalRandom.getIntBetween(0, 30);
        this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + colorRandomizer, 120 + colorRandomizer, 110 + colorRandomizer)).sizeFades(50, 75).movesConstant(dx, dy).height(-80.0f).lifeTime(1000);
        if (this.getTime() < this.superNovaEndTime) {
            this.spawnChargeSupernovaParticles();
            if (this.superNovaRumbleSound != null && !this.superNovaRumbleSound.isDone()) {
                this.superNovaRumbleSound.refreshLooping(1.0f);
            } else {
                this.superNovaRumbleSound = SoundManager.playSound(GameResources.rumble, (SoundEffect)SoundEffect.effect(this).pitch(1.2f).volume(1.5f).falloffDistance(5000));
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.currentHeight;
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
        float height = GameMath.sin(perc * 360.0f) * 5.0f;
        return (int)height;
    }

    @Override
    public int getRespawnTime() {
        if (this.isSummoned) {
            return BossMob.getBossRespawnTime(this);
        }
        return super.getRespawnTime();
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return collisionDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    private void spawnChargeSupernovaParticles() {
        for (int i = 0; i < 5; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
            float startX = this.x + dir.x * range;
            float startY = this.y + 20.0f;
            float endHeight = 29.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime2 = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime2;
            this.getLevel().entityManager.addTopParticle(startX, startY, this.superNovaParticleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(15, 30).rotates().height(this.currentHeight).heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(new Color(249, 155, 78)).givesLight(35.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(lifeTime2);
            this.getLevel().entityManager.addTopParticle(startX, startY, this.superNovaParticleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().height(this.currentHeight).heightMoves(startHeight, endHeight).movesConstant(speed * 2.0f, 0.0f).color(new Color(255, 233, 73)).givesLight(50.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(lifeTime2);
        }
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
        float distance = 100.0f;
        this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance * 0.75f, this.superNovaParticleTypeSwitcher.next()).color(new Color(249, 155, 78)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x + GameMath.sin(angle) * (distance * lifePercent);
            pos.y = this.y + GameMath.cos(angle) * distance * lifePercent;
        }).lifeTime(1000).sizeFades(50, 50);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SunlightChampionMob.getTileCoordinate(x), SunlightChampionMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        int timePerFrame = 100;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 4;
        final TextureDrawOptionsEnd headOptions = MobRegistry.Textures.sunlightChampionEye.initDraw().sprite(spriteIndex, 0, 64).size(64, 64).rotate(rotate).pos(drawX - 32 + (int)rotate, (drawY -= this.getFlyingHeight()) - 78);
        final TextureDrawOptionsEnd chestplateOptions = MobRegistry.Textures.sunlightChampionChestplate.initDraw().sprite(0, 0, 84, 64).size(84, 64).rotate(rotate).light(light).pos(drawX - 42, drawY - 32);
        final TextureDrawOptionsEnd jetOptions = MobRegistry.Textures.sunlightChampionJet.initDraw().sprite(spriteIndex % 2, 0, 56, 60).size(56, 60).rotate(rotate * 4.0f).pos(drawX - 28 - (int)(rotate * 2.0f), drawY + 15 + (int)(rotate <= 0.0f ? rotate : -rotate));
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                jetOptions.draw();
                chestplateOptions.draw();
                headOptions.draw();
            }
        });
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
        MobRegistry.Textures.sunlightChampionEye.initDraw().sprite(0, 0, 64).size(48, 48).draw(drawX, drawY);
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
        this.gauntlets.forEach(Mob::remove);
        this.gauntlets.clear();
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedProjectiles.clear();
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sunlightChampionChestplate, 1, 0, 84, 64, this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 15.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        boolean isIncursionTier5OrHigher;
        super.onDeath(attacker, attackers);
        Level level = this.getLevel();
        if (level instanceof IncursionLevel) {
            IncursionLevel incursionLevel = (IncursionLevel)level;
            isIncursionTier5OrHigher = incursionLevel.incursionData != null ? incursionLevel.incursionData.getTabletTier() >= 5 : false;
        } else {
            isIncursionTier5OrHigher = false;
        }
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            int moonArenasCompleted;
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            if (isIncursionTier5OrHigher && c.achievementsLoaded() && !c.needPlayerStats() && (moonArenasCompleted = c.playerStats().completed_incursions.getData("moonarena").getTotalTiersAbove(5, true)) > 0) {
                c.achievements().MASTER_OF_SUN_AND_MOON.markCompleted((ServerClient)c);
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static class SunlightGauntletMob
    extends FlyingBossMob {
        public final LevelMob<SunlightChampionMob> master = new LevelMob();
        public boolean leftHanded;
        public BooleanNetworkField isAttacking;
        protected final EmptyMobAbility feintAttackAbility;

        public SunlightGauntletMob() {
            super(10);
            this.isSummoned = true;
            this.dropsLoot = false;
            this.collision = new Rectangle(-20, -20, 40, 40);
            this.hitBox = new Rectangle(-30, -30, 60, 60);
            this.selectBox = new Rectangle(-50, -50, 100, 100);
            this.setKnockbackModifier(0.0f);
            this.setRegen(0.0f);
            this.setSpeed(600.0f);
            this.isAttacking = this.registerNetworkField(new BooleanNetworkField(false));
            this.feintAttackAbility = this.registerAbility(new EmptyMobAbility(){

                @Override
                protected void run() {
                    dx = 0.0f;
                    dy = 0.0f;
                    if (!this.isClient()) {
                        return;
                    }
                    SoundManager.playSound(GameResources.explosionLight, SoundEffect.globalEffect().volume(2.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.4f, 0.8f)));
                    this.spawnFeintingAttackParticles();
                }
            });
        }

        @Override
        public boolean shouldSendSpawnPacket() {
            return false;
        }

        @Override
        public Mob getSpawnPacketMaster() {
            return this.master.get(this.getLevel());
        }

        @Override
        public void init() {
            super.init();
            this.countStats = false;
        }

        @Override
        public void clientTick() {
            super.clientTick();
            this.tickMaster();
            int colorRandomizer = GameRandom.globalRandom.getIntBetween(0, 30);
            this.getLevel().entityManager.addParticle(this.x, this.y - 20.0f, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + colorRandomizer, 120 + colorRandomizer, 110 + colorRandomizer)).sizeFades(50, 75).height(-20.0f).movesConstantAngle(GameRandom.globalRandom.getIntBetween(0, 360), 15.0f).lifeTime(500);
        }

        @Override
        public void serverTick() {
            super.serverTick();
            this.movementUpdateTime = this.getWorldEntity().getTime();
            this.healthUpdateTime = this.getWorldEntity().getTime();
            this.tickMaster();
        }

        @Override
        public int getHealth() {
            SunlightChampionMob head;
            if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
                return head.getHealth();
            }
            return super.getHealth();
        }

        @Override
        public int getMaxHealth() {
            SunlightChampionMob head;
            if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
                return head.getMaxHealth();
            }
            return super.getMaxHealth();
        }

        @Override
        public void requestServerUpdate() {
        }

        public void tickMaster() {
            if (this.removed()) {
                return;
            }
            if (this.master.get(this.getLevel()) == null) {
                this.remove();
            }
            this.master.computeIfPresent(this.getLevel(), m -> {
                this.setMaxHealth(m.getMaxHealth());
                this.setHealthHidden(m.getHealth(), 0.0f, 0.0f, null);
                this.setArmor(m.getArmorFlat() * 2);
            });
        }

        @Override
        public int stoppingDistance(float friction, float currentSpeed) {
            return 0;
        }

        @Override
        public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
            if (this.master != null) {
                this.master.computeIfPresent(this.getLevel(), m -> m.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate));
            }
            super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        }

        @Override
        public boolean isHealthBarVisible() {
            return false;
        }

        @Override
        public float getIncomingDamageModifier() {
            SunlightChampionMob master = this.master.get(this.getLevel());
            return master == null ? super.getIncomingDamageModifier() : master.getIncomingDamageModifier();
        }

        @Override
        public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
            return gauntletCollisionDamage;
        }

        @Override
        public int getCollisionKnockback(Mob target) {
            return 350;
        }

        public void spawnFeintingAttackParticles() {
            ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
            float anglePerParticle = 18.0f;
            for (int i = 0; i < 20; ++i) {
                int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
                float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
                this.getLevel().entityManager.addTopParticle(this, typeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).color((options, lifeTime1, timeAlive, lifePercent) -> options.color(new Color((int)(255.0f - 55.0f * lifePercent), (int)(225.0f - 200.0f * lifePercent), (int)(155.0f - 125.0f * lifePercent)))).movesFriction(dx, dy, 0.8f).heightMoves(0.0f, 10.0f).lifeTime(1000);
            }
        }

        @Override
        public void spawnDeathParticles(float knockbackX, float knockbackY) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sunlightGauntlet, 1, 0, 64, 76, this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 15.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

        @Override
        protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
            GameLight light = level.getLightLevel(SunlightGauntletMob.getTileCoordinate(x), SunlightGauntletMob.getTileCoordinate(y));
            int drawX = camera.getDrawX(x);
            int drawY = camera.getDrawY(y);
            float rotate = (float)Math.toDegrees(Math.atan2(this.dy, this.dx)) - 90.0f;
            int timePerFrame = 100;
            int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame % 4L);
            final TextureDrawOptionsEnd gauntletOptions = MobRegistry.Textures.sunlightGauntlet.initDraw().sprite(0, 0, 64, 76).size(64, 76).mirror(this.leftHanded, false).rotate(rotate).light(light).pos(drawX - 32, drawY -= this.getFlyingHeight());
            final TextureDrawOptionsEnd gauntletFireOptions = MobRegistry.Textures.sunlightGauntletFire.initDraw().sprite(spriteIndex, 0, 64, 76).size(64, 76).mirror(this.leftHanded, false).rotate(rotate).pos(drawX - 32, drawY);
            final TextureDrawOptionsEnd jetOptions = MobRegistry.Textures.sunlightGauntletJet.initDraw().sprite(spriteIndex % 2, 0, 38, 81).size(38, 81).rotate(rotate, 19, 81).posMiddle(drawX, drawY);
            topList.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    if (((Boolean)isAttacking.get()).booleanValue()) {
                        jetOptions.draw();
                    } else {
                        gauntletFireOptions.draw();
                    }
                    gauntletOptions.draw();
                }
            });
        }
    }

    public static class SunlightChampionAI<T extends SunlightChampionMob>
    extends SequenceAINode<T> {
        public SunlightChampionAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new GatherGauntletsStage());
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new SunlightOrbStage());
            attackStages.addChild(new IdleTimeAttackStage<SunlightChampionMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild(new RocketPunchStage());
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new RocketPunchStage());
            attackStages.addChild(new FeintingAttackStage());
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new GatherGauntletsStage());
            for (int i = 0; i < 2; ++i) {
                attackStages.addChild(new ChargeTargetStage());
                attackStages.addChild(new RocketPunchStage());
                attackStages.addChild(new ChargeTargetStage());
            }
            attackStages.addChild(new FeintingAttackStage());
            attackStages.addChild(new SupernovaStage(3000));
            attackStages.addChild(new GatherGauntletsStage());
            attackStages.addChild(new RocketPunchStage());
            attackStages.addChild(new IdleTimeAttackStage<SunlightChampionMob>(m -> this.getIdleTime(m, 2000)));
            attackStages.addChild(new SunlightOrbStage());
            attackStages.addChild(new FeintingAttackStage());
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new RocketPunchStage());
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new FeintingAttackStage());
            attackStages.addChild(new ChargeTargetStage());
        }

        private int getIdleTime(T mob, int maxTime) {
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((SunlightChampionMob)mob).getMaxHealth();
            return (int)((float)maxTime * healthPerc);
        }
    }

    public static class ChargeTargetStage<T extends SunlightChampionMob>
    extends FlyToOppositeDirectionAttackStage<T> {
        public ChargeTargetStage() {
            super(true, 250.0f, 0.0f);
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            super.onStarted(mob, blackboard);
            if (blackboard.mover.isMoving()) {
                ((SunlightChampionMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, (Mob)mob, 5.0f, null), true);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
            super.onEnded(mob, blackboard);
            ((SunlightChampionMob)mob).buffManager.removeBuff(BuffRegistry.MOVE_SPEED_BURST, true);
        }
    }

    public static class SupernovaStage<T extends SunlightChampionMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final int attackDuration;
        private long explosionTime;

        public SupernovaStage(int attackDuration) {
            this.attackDuration = attackDuration;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (mob.getTime() < this.explosionTime) {
                return AINodeResult.RUNNING;
            }
            SupernovaExplosionEvent event = new SupernovaExplosionEvent(((SunlightChampionMob)mob).x, ((SunlightChampionMob)mob).y, 650, supernovaDamage, 0.0f, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.events.add(event);
            ((SunlightChampionMob)mob).supernovaSoundAbility.runAndSend();
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.explosionTime = mob.getTime() + (long)this.attackDuration;
            ((SunlightChampionMob)mob).startSupernovaCharge.runAndSend(this.attackDuration);
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class SunlightOrbStage<T extends SunlightChampionMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            for (ServerClient client : ((Entity)mob).getLevel().getServer().getClients()) {
                PlayerMob player = client.playerMob;
                ArrayList<Point> spawnPoints = new ArrayList<Point>();
                int maxRange = 5;
                for (int x = -maxRange; x <= maxRange; ++x) {
                    int tileX = player.getTileX() + x;
                    for (int y = -maxRange; y <= maxRange; ++y) {
                        int tileY = player.getTileY() + y;
                        if (((Entity)mob).getLevel().isSolidTile(tileX, tileY)) continue;
                        spawnPoints.add(new Point(tileX * 32 + 16, tileY * 32 + 16));
                    }
                }
                for (int i = 0; i < 2; ++i) {
                    if (spawnPoints.isEmpty()) continue;
                    Point spawnPoint = (Point)spawnPoints.remove(GameRandom.globalRandom.nextInt(spawnPoints.size()));
                    SunlightOrbEvent event = new SunlightOrbEvent((Mob)mob, spawnPoint.x, spawnPoint.y, GameRandom.globalRandom, sunlightOrbDamage, 1000L);
                    ((Entity)mob).getLevel().entityManager.events.add(event);
                }
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class FeintingAttackStage<T extends SunlightChampionMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            for (SunlightGauntletMob gauntlet : ((SunlightChampionMob)mob).gauntlets) {
                if (!((Boolean)gauntlet.isAttacking.get()).booleanValue()) continue;
                ((SunlightChampionMob)mob).rocketPunchSoundAbility.runAndSend();
                gauntlet.feintAttackAbility.runAndSend();
                gauntlet.setMovement(new MobMovementRelative(target, 0.0f, 0.0f));
                break;
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class RocketPunchStage<T extends SunlightChampionMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            for (SunlightGauntletMob gauntlet : ((SunlightChampionMob)mob).gauntlets) {
                if (((Boolean)gauntlet.isAttacking.get()).booleanValue()) continue;
                ((SunlightChampionMob)mob).rocketPunchSoundAbility.runAndSend();
                gauntlet.isAttacking.set(true);
                gauntlet.setMovement(new MobMovementRelative(target, 0.0f, 0.0f));
                break;
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class GatherGauntletsStage<T extends SunlightChampionMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            for (SunlightGauntletMob gauntlet : ((SunlightChampionMob)mob).gauntlets) {
                gauntlet.isAttacking.set(false);
                gauntlet.setMovement(new MobMovementRelative((Mob)mob, gauntlet.leftHanded ? 100.0f : -100.0f, 0.0f));
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
}

