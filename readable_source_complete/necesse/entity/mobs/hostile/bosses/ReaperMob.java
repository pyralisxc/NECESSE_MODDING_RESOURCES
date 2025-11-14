/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritPortalMob;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.boomerangProjectile.ReaperScytheProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "halodromevinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("shadowbeam"), new LootItem("reaperscall"), new LootItem("deathripper"), new LootItem("reaperscythe"));
    public static LootTable privateLootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, uniqueDrops));
    public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(6000, 9000, 11000, 13000, 18000);
    public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(23000, 35000, 40000, 46000, 58000);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public LinkedList<Mob> spawnedMobs = new LinkedList();
    private long appearTime;
    private boolean hasScythe;
    private boolean isHiding;
    public final BooleanMobAbility hasScytheAbility;
    public final BooleanMobAbility hidingAbility;
    public final CoordinateMobAbility appearAbility;
    public final EmptyMobAbility magicSoundAbility;
    public final EmptyMobAbility roarSoundAbility;
    public GameDamage collisionDamage;
    public GameDamage scytheDamage;
    public static GameDamage baseCollisionDamage = new GameDamage(70.0f);
    public static GameDamage baseScytheDamage = new GameDamage(80.0f);
    public static GameDamage incursionCollisionDamage = new GameDamage(115.0f);
    public static GameDamage incursionScytheDamage = new GameDamage(140.0f);

    public ReaperMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
        this.moveAccuracy = 60;
        this.hasScythe = true;
        this.setSpeed(80.0f);
        this.setArmor(30);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-35, -50, 70, 80);
        this.hitBox = new Rectangle(-40, -70, 80, 90);
        this.selectBox = new Rectangle(-40, -80, 80, 110);
        this.hasScytheAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                ReaperMob.this.hasScythe = value;
                if (ReaperMob.this.isClient() && !ReaperMob.this.hasScythe) {
                    SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(ReaperMob.this).pitch(0.8f));
                }
            }
        });
        this.hidingAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                ReaperMob.this.isHiding = value;
                if (!ReaperMob.this.isHiding) {
                    ReaperMob.this.appearTime = ReaperMob.this.getWorldEntity().getTime();
                } else if (ReaperMob.this.isClient()) {
                    ReaperMob.this.spawnFadingParticle(500);
                }
            }
        });
        this.appearAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (ReaperMob.this.isClient() && !ReaperMob.this.isHiding) {
                    ReaperMob.this.spawnFadingParticle(500);
                }
                ReaperMob.this.setPos(x, y, true);
                ReaperMob.this.isHiding = false;
                ReaperMob.this.appearTime = ReaperMob.this.getWorldEntity().getTime();
            }
        });
        this.magicSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (ReaperMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(ReaperMob.this));
                }
            }
        });
        this.roarSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (ReaperMob.this.isClient()) {
                    SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7f).pitch(1.3f));
                }
            }
        });
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.hasScythe = reader.getNextBoolean();
        this.isHiding = reader.getNextBoolean();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.hasScythe);
        writer.putNextBoolean(this.isHiding);
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
        if (this.getLevel() instanceof IncursionLevel) {
            this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
            this.setHealth(this.getMaxHealth());
            this.collisionDamage = incursionCollisionDamage;
            this.scytheDamage = incursionScytheDamage;
        } else {
            this.collisionDamage = baseCollisionDamage;
            this.scytheDamage = baseScytheDamage;
        }
        super.init();
        this.ai = new BehaviourTreeAI<ReaperMob>(this, new DeepReaperAI());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.reaperbegin, (SoundEffect)SoundEffect.effect(this).volume(1.4f).falloffDistance(4000));
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

    @Override
    public boolean canTakeDamage() {
        return !this.isHiding;
    }

    @Override
    public boolean canLevelInteract() {
        return !this.isHiding;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return !this.isHiding;
    }

    @Override
    public boolean isVisible() {
        return !this.isHiding;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return !this.isHiding && super.canCollisionHit(target);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
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

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.ReapersRequiem, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(85.0f + healthPercInv * 65.0f);
        if (this.isVisible()) {
            for (int i = 0; i < 3; ++i) {
                this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 12.0) + (float)(this.getDir() == 0 ? 18 : -18), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0) + 10.0f, Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(24, 27, 27)).height(0.0f);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(85.0f + healthPercInv * 65.0f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 8; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.reaper, 4 + i, 16, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
        this.spawnFadingParticle(1000);
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.reaperhurt, (SoundEffect)SoundEffect.effect(this).volume(0.4f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.reaperdeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isHiding) {
            GameLight light = level.getLightLevel(ReaperMob.getTileCoordinate(x), ReaperMob.getTileCoordinate(y));
            float alpha = 1.0f;
            long curTime = this.getWorldEntity().getTime();
            if (curTime - this.appearTime < 500L) {
                alpha = (float)(curTime - this.appearTime) / 500.0f;
            }
            DrawOptions bodyDrawOptions = this.getBodyDrawOptions(level, x, y, tickManager, camera, this.hasScythe, this.getDir(), alpha);
            TextureDrawOptions shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera);
            topList.add(tm -> {
                shadowOptions.draw();
                bodyDrawOptions.draw();
            });
        }
    }

    public DrawOptions getBodyDrawOptions(Level level, int x, int y, TickManager tickManager, GameCamera camera, boolean hasScythe, int dir, float alpha) {
        int spriteY;
        GameLight light = level.getLightLevel(ReaperMob.getTileCoordinate(x), ReaperMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 75;
        int drawY = camera.getDrawY(y) - 100;
        long time = level.getWorldEntity().getTime();
        int n = spriteY = this.isSecondStage() ? 2 : 0;
        if (hasScythe) {
            ++spriteY;
        }
        int spriteX = GameUtils.getAnim(time, 3, 300);
        TextureDrawOptionsEnd body = MobRegistry.Textures.reaper.initDraw().sprite(spriteX, spriteY, 128).size(128, 128).light(light).alpha(alpha).mirror(dir != 0, false).pos(drawX, drawY);
        int minLight = 100;
        TextureDrawOptionsEnd eyes = MobRegistry.Textures.reaperGlow.initDraw().sprite(spriteX, spriteY, 128).size(128, 128).light(light.minLevelCopy(minLight)).alpha(alpha).mirror(dir != 0, false).pos(drawX, drawY);
        return () -> {
            body.draw();
            eyes.draw();
        };
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.reaper_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2 - (this.getDir() == 0 ? 5 : 15);
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 20;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return !this.isHiding;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        MobRegistry.Textures.reaper.initDraw().sprite(2 + (this.isSecondStage() ? 1 : 0), 9, 64).size(32, 32).mirror(this.getDir() != 0, false).draw(drawX, drawY);
        MobRegistry.Textures.reaperGlow.initDraw().sprite(2 + (this.isSecondStage() ? 1 : 0), 9, 64).size(32, 32).mirror(this.getDir() != 0, false).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-16, -16, 32, 32);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)));
    }

    public void setHasScythe(boolean hasScythe) {
        if (this.hasScythe != hasScythe) {
            this.hasScytheAbility.runAndSend(hasScythe);
        }
    }

    public void setIsHiding(boolean isHiding) {
        if (this.isHiding != isHiding) {
            this.hidingAbility.runAndSend(isHiding);
        }
    }

    public boolean isSecondStage() {
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        return healthPerc < 0.5f;
    }

    public void spawnFadingParticle(int fadeTime) {
        final int dir = this.getDir();
        final boolean hasScythe = this.hasScythe;
        float dx = this.dx;
        float dy = this.dy;
        this.getLevel().entityManager.addParticle(new Particle(this.getLevel(), this.getX(), this.getY(), dx, dy, fadeTime){

            @Override
            public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                float alpha = Math.abs(this.getLifeCyclePercent() - 1.0f);
                DrawOptions drawOptions = ReaperMob.this.getBodyDrawOptions(level, this.getX(), this.getY(), tickManager, camera, hasScythe, dir, alpha);
                topList.add(tm -> drawOptions.draw());
            }
        }, Particle.GType.CRITICAL);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public class DeepReaperAI<T extends ReaperMob>
    extends AINode<T> {
        private int removeCounter;
        private Mob currentTarget;
        private long findNewTargetTime;
        private final ArrayList<AttackRotation> attackRotations = new ArrayList();
        private int currentRotation;

        public DeepReaperAI() {
            this.attackRotations.add(new FadeChargeAttackRotation());
            this.attackRotations.add(new SpawnSpiritsAttackRotation());
            this.attackRotations.add(new CirclingTargetAttackRotation());
            this.currentRotation = 0;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            blackboard.onEvent("refreshBossDespawn", event -> {
                this.removeCounter = 0;
            });
            if (((Entity)mob).isServer()) {
                this.attackRotations.get(this.currentRotation).start();
            }
            blackboard.onRemoved(e -> ReaperMob.this.spawnedMobs.forEach(Mob::remove));
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.tickTargets();
            if (this.currentTarget != null) {
                this.removeCounter = 0;
                if (this.attackRotations.get(this.currentRotation).isOver()) {
                    this.attackRotations.get(this.currentRotation).end();
                    this.currentRotation = (this.currentRotation + 1) % this.attackRotations.size();
                    this.attackRotations.get(this.currentRotation).start();
                }
                this.attackRotations.get(this.currentRotation).tick();
            } else {
                ReaperMob.this.stopMoving();
                ++this.removeCounter;
                if (this.removeCounter > 100) {
                    ReaperMob.this.remove();
                }
            }
            return AINodeResult.SUCCESS;
        }

        public void tickTargets() {
            if (this.currentTarget != null) {
                if (!this.currentTarget.isSamePlace((Entity)this.mob()) || this.currentTarget.removed()) {
                    this.refreshTarget();
                } else if (ReaperMob.this.getWorldEntity().getTime() <= this.findNewTargetTime) {
                    this.refreshTarget();
                }
            } else {
                this.refreshTarget();
            }
        }

        public void refreshTarget() {
            this.currentTarget = GameUtils.streamServerClients(ReaperMob.this.getLevel()).map(c -> c.playerMob).min(Comparator.comparingInt(player -> (int)ReaperMob.this.getDistance((Mob)player))).orElse(null);
            this.findNewTargetTime = ReaperMob.this.getWorldEntity().getTime() + 5000L;
        }

        private class FadeChargeAttackRotation
        extends AttackRotation {
            private int chargeTimes;
            private boolean isCharging;
            private int currentCharge;
            private boolean isDone;
            private int timer;
            private List<Integer> currentAttackAngles;

            private FadeChargeAttackRotation() {
                this.timer = 0;
            }

            @Override
            public void start() {
                this.currentAttackAngles = new ArrayList<Integer>(Arrays.asList(0, 45, 90, 135, 180, 225, 270, 315));
                this.chargeTimes = 4;
                this.currentCharge = 0;
                this.isCharging = false;
                this.timer = 0;
                ReaperMob.this.stopMoving();
                ReaperMob.this.setIsHiding(true);
                this.isDone = false;
            }

            @Override
            public void tick() {
                if (!this.isCharging) {
                    ++this.timer;
                    float healthPerc = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
                    int cooldown = 20;
                    if (healthPerc < 0.25f) {
                        cooldown = (int)((float)cooldown * 1.0f / 4.0f);
                    } else if (healthPerc < 0.5f) {
                        cooldown = (int)((float)cooldown * 1.0f / 3.0f);
                    } else if (healthPerc < 0.7f) {
                        cooldown = (int)((float)cooldown * 1.0f / 2.0f);
                    }
                    if (this.timer > cooldown) {
                        this.timer = 0;
                        int attackAngleIndex = GameRandom.globalRandom.nextInt(this.currentAttackAngles.size());
                        int attackAngle = this.currentAttackAngles.remove(attackAngleIndex);
                        float range = 200.0f + ReaperMob.this.getSpeed() / 2.0f;
                        int x = (int)(Math.cos(Math.toRadians(attackAngle)) * (double)range);
                        int y = (int)(Math.sin(Math.toRadians(attackAngle)) * (double)range);
                        ReaperMob.this.appearAbility.runAndSend(DeepReaperAI.this.currentTarget.getX() + x, DeepReaperAI.this.currentTarget.getY() + y);
                        ReaperMob.this.setMovement(new MobMovementLevelPos(DeepReaperAI.this.currentTarget.getX() - x, DeepReaperAI.this.currentTarget.getY() - y));
                        this.isCharging = true;
                        ReaperMob.this.roarSoundAbility.runAndSend();
                        ++this.currentCharge;
                    }
                } else if (ReaperMob.this.hasArrivedAtTarget()) {
                    if (this.currentCharge < this.chargeTimes) {
                        this.isCharging = false;
                        ReaperMob.this.setIsHiding(true);
                    } else {
                        this.isDone = true;
                    }
                }
            }

            @Override
            public boolean isOver() {
                return this.isDone;
            }

            @Override
            public void end() {
            }
        }

        private class SpawnSpiritsAttackRotation
        extends AttackRotation {
            private float spawnCounter;
            private int timer;
            private int currentPortal;
            private final ArrayList<EmptyMobAbility> portals;

            private SpawnSpiritsAttackRotation() {
                this.portals = new ArrayList();
            }

            @Override
            public void start() {
                this.timer = 0;
                this.spawnCounter = 0.0f;
                this.currentPortal = 0;
                this.portals.clear();
                ReaperMob.this.stopMoving();
                int presentPlayers = ReaperMob.this.getLevel().presentPlayers;
                int totalPortals = GameMath.limit((presentPlayers - 1) / 2, 0, 10);
                if (ReaperMob.this.isSecondStage()) {
                    totalPortals += 2;
                    ReaperMob.this.setIsHiding(true);
                } else {
                    this.portals.add(ReaperMob.this.magicSoundAbility);
                }
                if (totalPortals > 0) {
                    Point2D.Float dir = GameMath.normalize(DeepReaperAI.this.currentTarget.getX() - ReaperMob.this.getX(), DeepReaperAI.this.currentTarget.getY() - ReaperMob.this.getY());
                    float myAngleToTarget = (float)Math.toDegrees(Math.atan2(dir.y, dir.x));
                    int anglePerPortal = 360 / totalPortals;
                    int randomize = anglePerPortal / 8;
                    for (int i = 0; i < totalPortals; ++i) {
                        this.spawnPortal(myAngleToTarget + 90.0f + (float)(anglePerPortal * i) + (float)GameRandom.globalRandom.getIntOffset(0, randomize));
                    }
                }
            }

            public void spawnPortal(float angle) {
                int x = (int)(Math.cos(Math.toRadians(angle)) * 300.0);
                int y = (int)(Math.sin(Math.toRadians(angle)) * 300.0);
                ReaperSpiritPortalMob portal = (ReaperSpiritPortalMob)MobRegistry.getMob("reaperspiritportal", ReaperMob.this.getLevel());
                portal.owner = ReaperMob.this;
                ReaperMob.this.getLevel().entityManager.addMob(portal, DeepReaperAI.this.currentTarget.getX() + x, DeepReaperAI.this.currentTarget.getY() + y);
                ReaperMob.this.spawnedMobs.add(portal);
                this.portals.add(portal.magicSoundAbility);
            }

            @Override
            public void tick() {
                float portalMultiplier = Math.max((float)this.portals.size() / 2.0f, 1.0f);
                ++this.timer;
                float healthPerc = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
                float frequency = healthPerc < 0.25f ? 0.25f : (healthPerc < 0.5f ? 0.2f : (healthPerc < 0.7f ? 0.15f : 0.1f));
                this.spawnCounter += frequency * portalMultiplier;
                while (this.spawnCounter > 1.0f) {
                    this.spawnCounter -= 1.0f;
                    EmptyMobAbility current = this.portals.get(this.currentPortal);
                    Mob portal = current.getMob();
                    current.runAndSend();
                    ReaperSpiritMob mob = (ReaperSpiritMob)MobRegistry.getMob("reaperspirit", ReaperMob.this.getLevel());
                    mob.owner = ReaperMob.this;
                    ReaperMob.this.getLevel().entityManager.addMob(mob, portal.getX(), portal.getY());
                    ReaperMob.this.spawnedMobs.add(mob);
                    this.currentPortal = (this.currentPortal + 1) % this.portals.size();
                }
            }

            @Override
            public boolean isOver() {
                float healthPerc = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
                return (float)this.timer > 20.0f * (2.0f + healthPerc * 3.0f);
            }

            @Override
            public void end() {
                for (EmptyMobAbility portal : this.portals) {
                    Mob mob = portal.getMob();
                    if (mob == DeepReaperAI.this.mob()) continue;
                    mob.remove();
                }
                ReaperMob.this.setIsHiding(false);
                this.portals.clear();
            }
        }

        private class CirclingTargetAttackRotation
        extends AttackRotation {
            private int timer;
            private int cooldown;
            private Mob circlingTarget;
            private boolean reverse;

            private CirclingTargetAttackRotation() {
                this.reverse = false;
            }

            @Override
            public void start() {
                this.timer = 0;
                this.cooldown = 0;
                this.circlingTarget = null;
                this.reverse = GameRandom.globalRandom.nextBoolean();
            }

            @Override
            public void tick() {
                ++this.timer;
                if (this.circlingTarget != DeepReaperAI.this.currentTarget) {
                    this.circlingTarget = DeepReaperAI.this.currentTarget;
                    ReaperMob.this.setMovement(new MobMovementCircleRelative((Mob)DeepReaperAI.this.mob(), this.circlingTarget, 350, 2.0f, this.reverse));
                }
                if (ReaperMob.this.hasScythe) {
                    ++this.cooldown;
                    if (this.cooldown > 40) {
                        this.cooldown = 0;
                        ReaperMob.this.setHasScythe(false);
                        ReaperMob.this.getLevel().entityManager.projectiles.add(new ReaperScytheProjectile(ReaperMob.this, ReaperMob.this.getX(), ReaperMob.this.getY(), DeepReaperAI.this.currentTarget.getX(), DeepReaperAI.this.currentTarget.getY(), ReaperMob.this.scytheDamage, (int)(ReaperMob.this.getSpeed() * 1.1f), ReaperMob.this.isSecondStage() ? 400 : 650));
                        if (ReaperMob.this.isSecondStage()) {
                            int deltaX = DeepReaperAI.this.currentTarget.getX() - ReaperMob.this.getX();
                            int deltaY = DeepReaperAI.this.currentTarget.getY() - ReaperMob.this.getY();
                            ReaperMob.this.appearAbility.runAndSend(DeepReaperAI.this.currentTarget.getX() + deltaX, DeepReaperAI.this.currentTarget.getY() + deltaY);
                            this.reverse = !this.reverse;
                            ReaperMob.this.setMovement(new MobMovementCircleRelative((Mob)DeepReaperAI.this.mob(), this.circlingTarget, 350, 2.0f, this.reverse));
                        }
                    }
                }
            }

            @Override
            public boolean isOver() {
                float healthPerc = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
                return ReaperMob.this.hasScythe && (float)this.timer > 20.0f * (5.0f + healthPerc * 10.0f);
            }

            @Override
            public void end() {
            }
        }

        private abstract class AttackRotation {
            private AttackRotation() {
            }

            public abstract void start();

            public abstract void tick();

            public abstract boolean isOver();

            public abstract void end();
        }
    }
}

