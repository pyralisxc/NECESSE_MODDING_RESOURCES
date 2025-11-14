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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.CameraShake;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChieftainDashLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.EarthSpikeMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.ChieftainShieldProjectile;
import necesse.entity.projectile.EarthSpikesProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.gameObject.ChieftainBoneSpikeWallObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class ChieftainMob
extends BossMob {
    public static LootTable lootTable = new LootTable(new ChanceLootItem(0.2f, "theruneboundtrialpart1"), new ChanceLootItem(0.2f, "theruneboundtrialpart2"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("runeboundscepter"), new LootItem("captorsshortbow"), new LootItem("brutesbattleaxe"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("runicheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("runicheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("runicheart"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    public static GameDamage dashDamage = new GameDamage(55.0f);
    public static GameDamage shieldTossDamage = new GameDamage(48.0f);
    public static GameDamage stompSpikeDamage = new GameDamage(55.0f);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(3000, 5000, 6000, 7000, 9000);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public Point spawnPos;
    public Point arenaCenterPos;
    protected boolean attackingWithAxe;
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    public final ChieftainSetHostileTeleportAbility setHostileTeleportAbility;
    public final IntMobAbility shoutAbility;
    protected long shoutStartTime;
    protected int shoutDuration;
    public final EmptyMobAbility startJumpChargeUpAbility;
    protected Point jumpStartPos;
    protected Point jumpEndPos;
    protected boolean jumpAbilityChargeUp;
    protected long jumpStartTime;
    protected long jumpAnimationTime;
    public final CoordinateMobAbility jumpAbility;
    public final CoordinateMobAbility showShieldTossAbility;
    public final IntMobAbility recallShieldAbility;
    protected long recallShieldStartTime;
    protected int recallShieldDuration;
    protected IntMobAbility startStompAbility;
    protected long stompStartTime;
    protected int stompDuration;
    protected int stompIndex;
    protected IntMobAbility startRoarAbility;
    protected long roarStartTime;
    protected int roarDuration;
    protected long chargeStartTime;
    protected boolean isChargingUp;
    protected int chargeDuration;
    protected int chargeTargetX;
    protected int chargeTargetY;
    protected Mob chargeTarget;
    public final ChieftainStartDashChargeAbility startDashChargeAbility;
    public final EmptyMobAbility tiptoeSoundAbility;
    protected SoundPlayer tiptoeSoundPlayer;

    public ChieftainMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(60.0f);
        this.setFriction(3.0f);
        this.setArmor(12);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-26, -24, 52, 48);
        this.selectBox = new Rectangle(-19, -52, 38, 64);
        this.swimMaskMove = 20;
        this.swimMaskOffset = -20;
        this.swimSinkOffset = 0;
        this.setDir(2);
        this.shouldSave = true;
        this.isHostile = false;
        this.setHostileTeleportAbility = this.registerAbility(new ChieftainSetHostileTeleportAbility());
        this.shoutAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ChieftainMob.this.shoutStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.shoutDuration = value;
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(GameResources.warcry, (SoundEffect)SoundEffect.effect(ChieftainMob.this).volume(3.0f).falloffDistance(2000));
                    CameraShake cameraShake = ChieftainMob.this.getClient().startCameraShake(ChieftainMob.this.x, ChieftainMob.this.y, value, 40, 2.0f, 2.0f, true);
                    cameraShake.minDistance = 200;
                    cameraShake.listenDistance = 2500;
                }
            }
        });
        this.startJumpChargeUpAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                ChieftainMob.this.jumpAbilityChargeUp = true;
                if (!ChieftainMob.this.isServer()) {
                    SoundManager.playSound(GameResources.dragonfly1, (SoundEffect)SoundEffect.effect(ChieftainMob.this).volume(2.0f).falloffDistance(2000));
                    SoundManager.playSound(GameResources.chieftainbegin, (SoundEffect)SoundEffect.effect(ChieftainMob.this).volume(1.4f).falloffDistance(4000));
                }
            }
        });
        this.jumpAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                ChieftainMob.this.jumpAbilityChargeUp = false;
                ChieftainMob.this.jumpStartPos = new Point(ChieftainMob.this.getX(), ChieftainMob.this.getY());
                ChieftainMob.this.jumpEndPos = new Point(x, y);
                ChieftainMob.this.jumpStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.jumpAnimationTime = 1500L;
                if (!ChieftainMob.this.isServer()) {
                    int particles = 50;
                    float anglePerParticle = 360.0f / (float)particles;
                    for (int i = 0; i < particles; ++i) {
                        int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(20, 80);
                        float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(20, 80) * 0.8f;
                        float endHeight = GameRandom.globalRandom.getFloatBetween(50.0f, 100.0f);
                        ChieftainMob.this.getLevel().entityManager.addParticle(ChieftainMob.this.jumpStartPos.x, ChieftainMob.this.jumpStartPos.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.5f).colorRandom(21.0f, 0.8f, 0.4f, 5.0f, 0.2f, 0.2f).heightMoves(0.0f, endHeight).lifeTime(2000);
                    }
                }
            }
        });
        this.showShieldTossAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                ChieftainMob.this.attackAnimTime = 250;
                ChieftainMob.this.attackingWithAxe = false;
                ChieftainMob.this.showAttack(x, y, false);
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(ChieftainMob.this).pitch(0.65f));
                }
            }
        });
        this.recallShieldAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ChieftainMob.this.recallShieldStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.recallShieldDuration = value;
                ChieftainMob.this.setDir(2);
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(ChieftainMob.this).pitch(0.65f));
                    SoundManager.playSound(GameResources.warcryumf, (SoundEffect)SoundEffect.effect(ChieftainMob.this).volume(3.0f).falloffDistance(2000));
                }
            }
        });
        this.startStompAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ChieftainMob.this.stompStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.stompDuration = value;
                ++ChieftainMob.this.stompIndex;
                ChieftainMob.this.setDir(2);
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(ChieftainMob.this).falloffDistance(2000));
                    SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(ChieftainMob.this).falloffDistance(2000));
                }
            }
        });
        this.startRoarAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ChieftainMob.this.roarStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.roarDuration = value;
                ChieftainMob.this.setDir(2);
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(GameResources.warcry, (SoundEffect)SoundEffect.effect(ChieftainMob.this).falloffDistance(2000));
                    CameraShake cameraShake = ChieftainMob.this.getClient().startCameraShake(ChieftainMob.this.x, ChieftainMob.this.y, value, 40, 2.0f, 2.0f, true);
                    cameraShake.minDistance = 200;
                    cameraShake.listenDistance = 2500;
                }
            }
        });
        this.startDashChargeAbility = this.registerAbility(new ChieftainStartDashChargeAbility(){});
        this.tiptoeSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (ChieftainMob.this.isClient() && (ChieftainMob.this.tiptoeSoundPlayer == null || ChieftainMob.this.tiptoeSoundPlayer.isDone())) {
                    ChieftainMob.this.tiptoeSoundPlayer = SoundManager.playSound(SoundSettingsRegistry.humanFootsteps, ChieftainMob.this);
                }
            }
        });
    }

    @Override
    public void dispose() {
        if (this.tiptoeSoundPlayer != null) {
            this.tiptoeSoundPlayer.stop();
        }
        super.dispose();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.spawnPos != null) {
            save.addPoint("spawnPos", this.spawnPos);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.spawnPos = save.getPoint("spawnPos", this.spawnPos, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.spawnPos != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.spawnPos.x);
            writer.putNextInt(this.spawnPos.y);
        } else {
            writer.putNextBoolean(false);
        }
        if (this.jumpStartPos != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.jumpStartPos.x);
            writer.putNextInt(this.jumpStartPos.y);
            writer.putNextInt(this.jumpEndPos.x);
            writer.putNextInt(this.jumpEndPos.y);
            writer.putNextLong(this.jumpStartTime);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnPos = reader.getNextBoolean() ? new Point(reader.getNextInt(), reader.getNextInt()) : null;
        if (reader.getNextBoolean()) {
            this.jumpStartPos = new Point(reader.getNextInt(), reader.getNextInt());
            this.jumpEndPos = new Point(reader.getNextInt(), reader.getNextInt());
            this.jumpStartTime = reader.getNextLong();
            this.jumpAbilityChargeUp = false;
        }
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isHostile);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isHostile = reader.getNextBoolean();
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
    public void init() {
        super.init();
        this.resetAI();
        this.setDir(2);
        if (this.isServer()) {
            if (this.spawnPos == null) {
                this.setHostileTeleportAbility.makeHostile(true);
            } else if (!this.isHostile) {
                this.setPos(this.spawnPos.x, this.spawnPos.y, true);
                this.setHealth(this.getMaxHealth());
            }
        }
    }

    protected ChieftainAI<ChieftainMob> resetAI() {
        ChieftainAI<ChieftainMob> chieftainAI = new ChieftainAI<ChieftainMob>();
        this.ai = new BehaviourTreeAI<ChieftainMob>(this, chieftainAI);
        return chieftainAI;
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
        SoundManager.playSound(GameResources.chieftainhurt, (SoundEffect)SoundEffect.effect(this).volume(0.5f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.chieftaindeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.setSpeed(GameMath.lerp(this.getHealthPercent(), 60, 90));
        if (this.isHostile) {
            if (this.isClientPlayerNearby()) {
                SoundManager.setMusic(MusicRegistry.TheRuneboundTrialPart2, SoundManager.MusicPriority.EVENT, 1.5f);
                EventStatusBarManager.registerMobHealthStatusBar(this);
            }
            BossNearbyBuff.applyAround(this);
        }
        this.tickChargeUp();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.setSpeed(GameMath.lerp(this.getHealthPercent(), 60, 90));
        this.scaling.serverTick();
        if (this.isHostile) {
            BossNearbyBuff.applyAround(this);
        }
        this.tickChargeUp();
    }

    public void tickChargeUp() {
        if (this.isChargingUp && this.chargeTarget != null) {
            long timeSinceChargeUp = this.getTime() - this.chargeStartTime;
            if (timeSinceChargeUp > (long)(this.chargeDuration + 2000)) {
                this.isChargingUp = false;
            } else {
                this.setDir(this.chargeTarget.x < this.x ? 3 : 1);
            }
        }
    }

    public void showChargeAttack(int targetX, int targetY, int animationTime) {
        this.attackAnimTime = animationTime;
        this.showAttack(targetX, targetY, false);
        this.isChargingUp = false;
        this.attackingWithAxe = true;
    }

    public boolean isInJumpOrLandingAnimation() {
        return this.getTime() < this.jumpStartTime + this.jumpAnimationTime + 1000L;
    }

    @Override
    public void tickMovement(float delta) {
        if (this.isInJumpOrLandingAnimation()) {
            if (this.jumpStartPos != null && this.jumpEndPos != null) {
                if (this.getTime() >= this.jumpStartTime + this.jumpAnimationTime) {
                    this.x = this.jumpEndPos.x;
                    this.y = this.jumpEndPos.y;
                    this.nX = this.x;
                    this.nY = this.y;
                    this.isSmoothSnapped = true;
                    this.jumpStartPos = null;
                    this.jumpEndPos = null;
                    if (!this.isServer()) {
                        int particles = 150;
                        float anglePerParticle = 360.0f / (float)particles;
                        for (int i = 0; i < particles; ++i) {
                            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(20, 140);
                            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(20, 140) * 0.8f;
                            float endHeight = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                            this.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.6f).colorRandom(21.0f, 0.8f, 0.4f, 5.0f, 0.2f, 0.2f).heightMoves(0.0f, endHeight).lifeTime(5000);
                        }
                        SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect(this));
                        if (this.isClient()) {
                            this.getClient().startCameraShake(this, 400, 40, 12.0f, 12.0f, false);
                        }
                    } else {
                        this.resetAI();
                    }
                    this.isHostile = true;
                } else {
                    float jumpProgress = GameMath.limit((float)(this.getTime() - this.jumpStartTime) / (float)this.jumpAnimationTime, 0.0f, 1.0f);
                    this.x = GameMath.lerp(jumpProgress, this.jumpStartPos.x, this.jumpEndPos.x);
                    this.y = GameMath.lerp(jumpProgress, this.jumpStartPos.y, this.jumpEndPos.y);
                    this.nX = this.x;
                    this.nY = this.y;
                    this.isSmoothSnapped = true;
                }
            }
            return;
        }
        super.tickMovement(delta);
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock || tp.object().object instanceof ChieftainBoneSpikeWallObject);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return super.canTakeDamage() && this.isHostile;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            int sprite = GameRandom.globalRandom.nextInt(7);
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.chieftain.body, sprite % 4, 8 + i / 4, 64, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        long timeSinceRoarStart;
        long timeSinceStompStart;
        long timeSinceShoutStart;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(ChieftainMob.getTileCoordinate(x), ChieftainMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 86;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(ChieftainMob.getTileCoordinate(x), ChieftainMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        long timeSinceRecallStart = this.getTime() - this.recallShieldStartTime;
        if (timeSinceRecallStart <= (long)this.recallShieldDuration) {
            sprite = new Point(6, 5);
        }
        if ((timeSinceShoutStart = this.getTime() - this.shoutStartTime) <= (long)this.shoutDuration) {
            sprite = new Point(5, 5);
        }
        if ((timeSinceStompStart = this.getTime() - this.stompStartTime) <= (long)this.stompDuration) {
            sprite = new Point(3 + this.stompIndex % 2, 5);
        }
        if ((timeSinceRoarStart = this.getTime() - this.roarStartTime) <= (long)this.roarDuration) {
            sprite = new Point(5, 5);
        }
        if (this.jumpAbilityChargeUp) {
            sprite = new Point(5, 5);
        } else {
            long jumpProgressTime = this.getTime() - this.jumpStartTime;
            if (jumpProgressTime > 0L) {
                if (jumpProgressTime < this.jumpAnimationTime) {
                    float jumpProgress = (float)jumpProgressTime / (float)this.jumpAnimationTime;
                    drawY = (int)((double)drawY - Math.sin((double)jumpProgress * Math.PI) * 200.0);
                    sprite = jumpProgressTime < this.jumpAnimationTime - this.jumpAnimationTime / 3L ? new Point(6, 2) : new Point(6, 5);
                } else if (jumpProgressTime < this.jumpAnimationTime + 1000L) {
                    sprite = new Point(5, 4);
                }
            }
        }
        float animProgress = this.getAttackAnimProgress();
        boolean isAttacking = this.isAttacking;
        if (isAttacking && this.attackingWithAxe) {
            sprite.x = 1 + GameUtils.getAnim(this.getTime(), 4, 400);
        }
        Point armCenterPos = dir == 0 ? new Point(70, 20) : (dir == 1 ? new Point(53, 23) : (dir == 2 ? new Point(41, 22) : new Point(73, 25)));
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.chieftain).sprite(sprite, 128).size(128, 128).mask(swimMask).dir(dir).light(light).attackOffsets(armCenterPos.x, armCenterPos.y, 20, 30, 25, 0, 30);
        long timeSinceChargeUp = this.getTime() - this.chargeStartTime;
        if (this.isChargingUp) {
            float progress = Math.min((float)timeSinceChargeUp / (float)this.chargeDuration, 1.0f);
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.chieftain.body, 3, 4, 128).itemRotatePoint(8, 8).itemEnd().armSprite(MobRegistry.Textures.chieftain.body, 0, 8, 64).itemAfterHand().light(light);
            attackOptions.swingRotation(progress, -50.0f, 50.0f);
            humanDrawOptions.attackAnim(attackOptions, progress);
            isAttacking = false;
        }
        if (isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir);
            if (this.attackingWithAxe) {
                attackOptions.itemSprite(MobRegistry.Textures.chieftain.body, 3, 4, 128).itemRotatePoint(8, 8).itemEnd();
            }
            attackOptions.armSprite(MobRegistry.Textures.chieftain.body, 0, 8, 64).itemAfterHand().light(light);
            attackOptions.swingRotation(animProgress);
            humanDrawOptions.attackAnim(attackOptions, animProgress);
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
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_big_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
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
        int drawY = y - 34;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
        new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.chieftain).sprite(sprite, 128).dir(dir).size(64, 64).draw(drawX, drawY);
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return this.isHostile;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.ATTACK_MOVEMENT_MOD, Float.valueOf(0.0f)));
    }

    public void clearSpawnedProjectiles() {
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedProjectiles.clear();
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        this.clearSpawnedProjectiles();
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public class ChieftainSetHostileTeleportAbility
    extends MobAbility {
        public void makeHostile(boolean isHostile) {
            if (!isHostile) {
                if (ChieftainMob.this.spawnPos != null) {
                    this.runAndSend(ChieftainMob.this.spawnPos.x, ChieftainMob.this.spawnPos.y, 2, true, isHostile);
                } else {
                    this.runAndSend(ChieftainMob.this.getX(), ChieftainMob.this.getY(), 2, false, false);
                }
                ChieftainMob.this.clearSpawnedProjectiles();
            } else {
                this.runAndSend(ChieftainMob.this.getX(), ChieftainMob.this.getY(), 2, false, true);
            }
        }

        public void runAndSend(int x, int y, int dir, boolean spawnParticles, boolean isHostile) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextInt(dir);
            writer.putNextBoolean(spawnParticles);
            writer.putNextBoolean(isHostile);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            int dir = reader.getNextInt();
            boolean spawnParticles = reader.getNextBoolean();
            if (spawnParticles && ChieftainMob.this.isClient()) {
                ChieftainMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(ChieftainMob.this.getLevel(), ChieftainMob.this.getX(), ChieftainMob.this.getY(), 96, new Color(77, 39, 11)), Particle.GType.CRITICAL);
                ChieftainMob.this.getLevel().lightManager.refreshParticleLightFloat((float)x, (float)y, 270.0f, 0.5f);
            }
            ChieftainMob.this.setDir(dir);
            ChieftainMob.this.setPos(x, y, true);
            if (spawnParticles && ChieftainMob.this.isClient()) {
                ChieftainMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(ChieftainMob.this.getLevel(), ChieftainMob.this.getX(), ChieftainMob.this.getY(), 96, new Color(77, 39, 11)), Particle.GType.CRITICAL);
                ChieftainMob.this.getLevel().lightManager.refreshParticleLightFloat((float)x, (float)y, 270.0f, 0.5f);
            }
            boolean lastIsHostile = ChieftainMob.this.isHostile;
            ChieftainMob.this.isHostile = reader.getNextBoolean();
            if (lastIsHostile && !ChieftainMob.this.isHostile) {
                ChieftainMob.this.setDir(2);
                ChieftainMob.this.stopMoving();
                ChieftainMob.this.resetAI();
            }
        }
    }

    public class ChieftainStartDashChargeAbility
    extends MobAbility {
        public void runAndSendLock(int targetX, int targetY) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(targetX);
            writer.putNextInt(targetY);
            writer.putNextInt(-1);
            this.runAndSendAbility(content);
        }

        public void runAndSendStart(Mob target, int duration) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(target.getX());
            writer.putNextInt(target.getY());
            writer.putNextInt(target.getUniqueID());
            writer.putNextInt(duration);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            ChieftainMob.this.chargeTargetX = reader.getNextInt();
            ChieftainMob.this.chargeTargetY = reader.getNextInt();
            int targetUniqueID = reader.getNextInt();
            if (targetUniqueID != -1) {
                ChieftainMob.this.chargeTarget = GameUtils.getLevelMob(targetUniqueID, ChieftainMob.this.getLevel(), true);
                ChieftainMob.this.chargeStartTime = ChieftainMob.this.getTime();
                ChieftainMob.this.chargeDuration = reader.getNextInt();
                if (ChieftainMob.this.isClient()) {
                    SoundManager.playSound(new SoundSettings(GameResources.warcry).volume(0.5f).basePitch(0.85f).pitchVariance(0.0f).fallOffDistance(800), ChieftainMob.this);
                }
            } else {
                ChieftainMob.this.chargeTarget = null;
            }
            ChieftainMob.this.isChargingUp = true;
        }
    }

    public static class ChieftainAI<T extends ChieftainMob>
    extends SequenceAINode<T> {
        private int noTargetsTimer;

        public ChieftainAI() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (!((ChieftainMob)mob).isHostile) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.FAILURE;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    return ((ChieftainMob)mob).isInJumpOrLandingAnimation() ? AINodeResult.FAILURE : AINodeResult.SUCCESS;
                }
            });
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    blackboard.onEvent("refreshBossDespawn", event -> noTargetsTimer = 0);
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
                    if (currentTarget == null) {
                        noTargetsTimer++;
                        if (noTargetsTimer > 100) {
                            ((ChieftainMob)mob).setHostileTeleportAbility.makeHostile(false);
                            noTargetsTimer = 0;
                            ((ChieftainMob)mob).clearSpawnedProjectiles();
                            ((Mob)mob).setHealth(((ChieftainMob)mob).getMaxHealth());
                            return AINodeResult.FAILURE;
                        }
                    } else {
                        noTargetsTimer = 0;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new MoveRandomAroundArena(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new DashAttackStage(500, 2000, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new MoveToArenaCenter(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new PatternStompStage(250, 750));
            attackStages.addChild(new IdleTimeAttackStage(1000, 1500));
            attackStages.addChild(new WarCryStage(500, 1000));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new MoveRandomAroundArena(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new MoveRandomAroundArena(true));
            attackStages.addChild(new IdleTimeAttackStage(250, 500));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new RecallShieldStage(250, 750));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new MoveToArenaCenter(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new SingleSpikeStompStage(500, 1000));
            attackStages.addChild(new SingleSpikeStompStage(500, 1000));
            attackStages.addChild(new SingleSpikeStompStage(500, 1000));
            attackStages.addChild(new SingleSpikeStompStage(500, 1000));
            attackStages.addChild(new SingleSpikeStompStage(500, 1000));
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new ShieldTossStage());
            attackStages.addChild(new IdleTimeAttackStage(250, 1000));
            attackStages.addChild(new WarCryStage(500, 1000));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new RecallShieldStage(250, 750));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
            attackStages.addChild(new MoveRandomAroundArena(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 500));
            attackStages.addChild(new DashAttackStage(500, 1250, 50, 400));
            attackStages.addChild(new IdleTimeAttackStage(0, 1000));
        }
    }

    public static class MoveRandomAroundArena<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public boolean isRunningWhileMoving;

        public MoveRandomAroundArena(boolean isRunningWhileMoving) {
            this.isRunningWhileMoving = isRunningWhileMoving;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.isRunningWhileMoving && blackboard.mover.isMoving()) {
                ((ChieftainMob)mob).tiptoeSoundAbility.runAndSend();
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            float angle = GameRandom.globalRandom.nextFloat() * 360.0f;
            Point2D.Float dir = GameMath.getAngleDir(angle);
            Point centerPos = ((ChieftainMob)mob).arenaCenterPos;
            if (centerPos == null) {
                Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
                centerPos = currentTarget != null ? new Point(currentTarget.getX(), currentTarget.getY()) : new Point(((Entity)mob).getX(), ((Entity)mob).getY());
            }
            int maxDistance = 500;
            Ray<LevelObjectHit> hit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)centerPos.x, (double)centerPos.y, (double)dir.x, (double)dir.y, (double)maxDistance, ((ChieftainMob)mob).getLevelCollisionFilter());
            if (hit != null) {
                maxDistance = (int)centerPos.distance(hit.getIntersectionPoint());
            }
            int distance = GameRandom.globalRandom.getIntBetween(maxDistance / 4, maxDistance - maxDistance / 4);
            blackboard.mover.directMoveTo(this, (int)((float)centerPos.x + dir.x * (float)distance), (int)((float)centerPos.y + dir.y * (float)distance));
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class MoveToArenaCenter<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public boolean isRunningWhileMoving;

        public MoveToArenaCenter(boolean isRunningWhileMoving) {
            this.isRunningWhileMoving = isRunningWhileMoving;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.isRunningWhileMoving && blackboard.mover.isMoving()) {
                ((ChieftainMob)mob).tiptoeSoundAbility.runAndSend();
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            if (((ChieftainMob)mob).arenaCenterPos != null) {
                blackboard.mover.directMoveTo(this, ((ChieftainMob)mob).arenaCenterPos.x, ((ChieftainMob)mob).arenaCenterPos.y);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class WarCryStage<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> attackDurationGetter;
        private int attackDuration;
        private long attackStartTime;

        public WarCryStage(Function<T, Integer> attackDurationGetter) {
            this.attackDurationGetter = attackDurationGetter;
        }

        public WarCryStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> GameMath.lerp(m.getHealthPercent(), noHealthIdleTime, fullHealthIdleTime));
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (mob.getTime() - this.attackStartTime > (long)this.attackDuration) {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.attackDuration = this.attackDurationGetter.apply(mob);
            ((ChieftainMob)mob).startRoarAbility.runAndSend(this.attackDuration);
            List<EarthSpikeMob> nearbySpikes = this.getNearbySpikes(((Entity)mob).getLevel(), (Mob)mob);
            if (!nearbySpikes.isEmpty()) {
                for (EarthSpikeMob nearbySpike : nearbySpikes) {
                    if (nearbySpike.isCracking) continue;
                    nearbySpike.startCrackAbility.runAndSend(this.attackDuration);
                }
            }
            this.attackStartTime = mob.getTime();
        }

        public List<EarthSpikeMob> getNearbySpikes(Level level, Mob owner) {
            int checkInRange = 640;
            return level.entityManager.mobs.streamInRegionsInRange(owner.x, owner.y, checkInRange).filter(s -> s instanceof EarthSpikeMob).map(s -> (EarthSpikeMob)s).filter(s -> s.mobOwner == owner).filter(s -> s.getDistance(owner) <= (float)checkInRange).collect(Collectors.toList());
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class PatternStompStage<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> attackCooldown;
        private float lastAngle;
        private float nextAngleOffset;
        private long lastAttackTime;
        private int remainingAttacks;

        public PatternStompStage(Function<T, Integer> attackCooldown) {
            this.attackCooldown = attackCooldown;
        }

        public PatternStompStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> GameMath.lerp(m.getHealthPercent(), noHealthIdleTime, fullHealthIdleTime));
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            int attackDuration = this.attackCooldown.apply(mob);
            if (mob.getTime() > (long)attackDuration + this.lastAttackTime) {
                --this.remainingAttacks;
                this.lastAttackTime = mob.getTime();
                this.lastAngle += this.nextAngleOffset;
                ((ChieftainMob)mob).startStompAbility.runAndSend(attackDuration + 500);
                float speed = GameMath.lerp(((Mob)mob).getHealthPercent(), 160, 120);
                int distance = 1000;
                Point2D.Float dir = GameMath.getAngleDir(this.lastAngle);
                Ray<LevelObjectHit> hit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)((ChieftainMob)mob).x, (double)((ChieftainMob)mob).y, (double)dir.x, (double)dir.y, (double)distance, ((ChieftainMob)mob).getLevelCollisionFilter());
                if (hit != null) {
                    distance = (int)((Mob)mob).getDistance((float)hit.x2, (float)hit.y2);
                    distance -= distance / 4;
                }
                EarthSpikesProjectile projectile = new EarthSpikesProjectile(((Entity)mob).getLevel(), (Mob)mob, ((ChieftainMob)mob).x, ((ChieftainMob)mob).y, ((ChieftainMob)mob).x + dir.x * (float)distance, ((ChieftainMob)mob).y + dir.y * (float)distance, speed, distance, stompSpikeDamage, 100);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((ChieftainMob)mob).spawnedProjectiles.add(projectile);
                distance = 1000;
                dir = GameMath.getAngleDir(this.lastAngle + 180.0f);
                hit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)((ChieftainMob)mob).x, (double)((ChieftainMob)mob).y, (double)dir.x, (double)dir.y, (double)distance, ((ChieftainMob)mob).getLevelCollisionFilter());
                if (hit != null) {
                    distance = (int)((Mob)mob).getDistance((float)hit.x2, (float)hit.y2);
                    distance -= distance / 3;
                }
                projectile = new EarthSpikesProjectile(((Entity)mob).getLevel(), (Mob)mob, ((ChieftainMob)mob).x, ((ChieftainMob)mob).y, ((ChieftainMob)mob).x + dir.x * (float)distance, ((ChieftainMob)mob).y + dir.y * (float)distance, speed, distance, stompSpikeDamage, 100);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((ChieftainMob)mob).spawnedProjectiles.add(projectile);
                if (this.remainingAttacks <= 0) {
                    return AINodeResult.SUCCESS;
                }
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            float angleLeftClear = 40.0f;
            this.lastAngle = GameRandom.globalRandom.nextFloat() * 360.0f;
            float angleToCover = 180.0f - angleLeftClear;
            this.remainingAttacks = 5;
            this.nextAngleOffset = angleToCover / (float)this.remainingAttacks;
            if (GameRandom.globalRandom.nextBoolean()) {
                this.nextAngleOffset *= -1.0f;
            }
            this.lastAttackTime = mob.getTime();
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class SingleSpikeStompStage<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> waitTimeGetter;
        private long startTime;
        private int waitTime;

        public SingleSpikeStompStage(Function<T, Integer> waitTimeGetter) {
            this.waitTimeGetter = waitTimeGetter;
        }

        public SingleSpikeStompStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> GameMath.lerp(m.getHealthPercent(), noHealthIdleTime, fullHealthIdleTime));
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (mob.getTime() <= this.startTime + (long)this.waitTime) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.startTime = mob.getTime();
            this.waitTime = this.waitTimeGetter.apply(mob);
            ((ChieftainMob)mob).startStompAbility.runAndSend(this.waitTime + 500);
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                float speed = GameMath.lerp(((Mob)mob).getHealthPercent(), 160, 120);
                float distance = ((Mob)mob).getDistance(target) + 100.0f;
                Point2D.Float dir = GameMath.normalize(target.x - ((ChieftainMob)mob).x, target.y - ((ChieftainMob)mob).y);
                Ray<LevelObjectHit> hit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)((ChieftainMob)mob).x, (double)((ChieftainMob)mob).y, (double)dir.x, (double)dir.y, (double)distance, ((ChieftainMob)mob).getLevelCollisionFilter());
                if (hit != null) {
                    distance = (int)((Mob)mob).getDistance((float)hit.x2, (float)hit.y2);
                }
                EarthSpikesProjectile projectile = new EarthSpikesProjectile(((Entity)mob).getLevel(), (Mob)mob, ((ChieftainMob)mob).x, ((ChieftainMob)mob).y, target.x, target.y, speed, (int)distance, stompSpikeDamage, 100);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((ChieftainMob)mob).spawnedProjectiles.add(projectile);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class RecallShieldStage<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> durationGetter;
        private long startTime;
        private int duration;

        public RecallShieldStage(Function<T, Integer> durationGetter) {
            this.durationGetter = durationGetter;
        }

        public RecallShieldStage(int noHealthIdleTime, int fullHealthIdleTime) {
            this(m -> GameMath.lerp(m.getHealthPercent(), noHealthIdleTime, fullHealthIdleTime));
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (mob.getTime() <= this.startTime + (long)this.duration) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.startTime = mob.getTime();
            this.duration = this.durationGetter.apply(mob);
            ((ChieftainMob)mob).recallShieldAbility.runAndSend(this.duration);
            for (int i = 0; i < ((ChieftainMob)mob).spawnedProjectiles.size(); ++i) {
                Projectile p = ((ChieftainMob)mob).spawnedProjectiles.get(i);
                if (!(p instanceof ChieftainShieldProjectile)) continue;
                ((ChieftainShieldProjectile)p).recall();
                ((ChieftainMob)mob).spawnedProjectiles.remove(i);
                --i;
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class ShieldTossStage<T extends ChieftainMob>
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
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                float speed = GameMath.lerp(((Mob)mob).getHealthPercent(), 180, 120);
                ChieftainShieldProjectile projectile = new ChieftainShieldProjectile(((ChieftainMob)mob).x, ((ChieftainMob)mob).y, target.x, target.y, shieldTossDamage, speed, 1000, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((ChieftainMob)mob).spawnedProjectiles.add(projectile);
                ((ChieftainMob)mob).showShieldTossAbility.runAndSend(target.getX(), target.getY());
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.FAILURE;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class DashAttackStage<T extends ChieftainMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> chargeUpDurationGetter;
        private final Function<T, Integer> lockedDurationGetter;
        private int chargeUpDuration;
        private int lockedDuration;
        private boolean isLocked;
        private ChieftainDashLevelEvent event;

        public DashAttackStage(Function<T, Integer> chargeUpDurationGetter, Function<T, Integer> lockedDurationGetter) {
            this.chargeUpDurationGetter = chargeUpDurationGetter;
            this.lockedDurationGetter = lockedDurationGetter;
        }

        public DashAttackStage(int noHealthChargeUpTime, int fullHealthChargeUpTime, int noHealthLockedTime, int fullHealthLockedTime) {
            this(m -> GameMath.lerp(m.getHealthPercent(), noHealthChargeUpTime, fullHealthChargeUpTime), m -> GameMath.lerp(m.getHealthPercent(), noHealthLockedTime, fullHealthLockedTime));
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.event != null) {
                if (this.event.isOver()) {
                    this.event = null;
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }
            long timeSinceStart = mob.getTime() - ((ChieftainMob)mob).chargeStartTime;
            if (timeSinceStart >= (long)this.chargeUpDuration) {
                if (!this.isLocked) {
                    if (((ChieftainMob)mob).chargeTarget == null) {
                        return AINodeResult.SUCCESS;
                    }
                    ((ChieftainMob)mob).startDashChargeAbility.runAndSendLock(((ChieftainMob)mob).chargeTarget.getX(), ((ChieftainMob)mob).chargeTarget.getY());
                    this.isLocked = true;
                }
                if (timeSinceStart >= (long)(this.chargeUpDuration + this.lockedDuration)) {
                    Point2D.Float dir = GameMath.normalize((float)((ChieftainMob)mob).chargeTargetX - ((ChieftainMob)mob).x, (float)((ChieftainMob)mob).chargeTargetY - ((ChieftainMob)mob).y);
                    float distance = Math.min(1000.0f, ((Mob)mob).getDistance(((ChieftainMob)mob).chargeTargetX, ((ChieftainMob)mob).chargeTargetY) + 300.0f);
                    Ray<LevelObjectHit> hit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)((ChieftainMob)mob).x, (double)((ChieftainMob)mob).y, (double)dir.x, (double)dir.y, (double)distance, ((ChieftainMob)mob).getLevelCollisionFilter());
                    if (hit != null) {
                        distance = (int)((Mob)mob).getDistance((float)hit.x2, (float)hit.y2);
                    }
                    int animTime = (int)distance;
                    this.event = new ChieftainDashLevelEvent((Mob)mob, GameRandom.globalRandom.nextInt(), dir.x, dir.y, distance, animTime, dashDamage);
                    ((Entity)mob).getLevel().entityManager.events.add(this.event);
                    return AINodeResult.RUNNING;
                }
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.chargeUpDuration = this.chargeUpDurationGetter.apply(mob);
            this.lockedDuration = this.lockedDurationGetter.apply(mob);
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                ((ChieftainMob)mob).startDashChargeAbility.runAndSendStart(target, this.chargeUpDuration);
            }
            this.isLocked = false;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
}

