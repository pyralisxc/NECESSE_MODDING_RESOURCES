/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
import necesse.engine.sound.SoundSettings;
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
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.AncientVultureEggMob;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AncientVultureProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientVultureMob
extends FlyingBossMob {
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("vulturestaff"), new LootItem("vulturesburst"), new LootItem("vulturestalon"));
    public static LootTable lootTable = new LootTable(new ChanceLootItem(0.1f, "vulturemask"), new ChanceLootItem(0.2f, "beatdownvinyl"));
    public static LootTable privateLootTable = new LootTable(uniqueDrops);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public LinkedList<Mob> spawnedMobs = new LinkedList();
    public static GameDamage collisionDamage = new GameDamage(48.0f);
    public static GameDamage projectileDamage = new GameDamage(44.0f);
    public static GameDamage hatchlingCollision = new GameDamage(40.0f);
    public static GameDamage hatchlingProjectile = new GameDamage(36.0f);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(5000, 6500, 7500, 8500, 10000);
    public final EmptyMobAbility flickSoundAbility;
    public final EmptyMobAbility popSoundAbility;

    public AncientVultureMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 60;
        this.setSpeed(150.0f);
        this.setArmor(20);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-55, -90, 110, 110);
        this.hitBox = new Rectangle(-55, -90, 110, 110);
        this.selectBox = new Rectangle(-75, -110, 150, 130);
        this.flickSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (AncientVultureMob.this.isClient()) {
                    SoundManager.playSound(GameResources.flick, (SoundEffect)SoundEffect.effect(AncientVultureMob.this).pitch(0.8f).falloffDistance(1400));
                }
            }
        });
        this.popSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (AncientVultureMob.this.isClient()) {
                    SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(AncientVultureMob.this).volume(0.8f).pitch(0.5f).falloffDistance(2000));
                }
            }
        });
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
        this.ai = new BehaviourTreeAI<AncientVultureMob>(this, new AncientVultureAI());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.ancientvulturebegin, (SoundEffect)SoundEffect.effect(this).falloffDistance(4000));
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
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.ancientvulturehurt).volume(0.4f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.ancientvulturedeath).fallOffDistance(3000);
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
            SoundManager.setMusic(MusicRegistry.AncientVulturesFeast, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(130.0f + healthPercInv * 90.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(130.0f + healthPercInv * 90.0f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.ancientVulture, 4 + i, 10, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(AncientVultureMob.getTileCoordinate(x), AncientVultureMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 160;
        int drawY = camera.getDrawY(y) - 200;
        int dir = this.getDir();
        long time = level.getWorldEntity().getTime() % 350L;
        int sprite = time < 100L ? 0 : (time < 200L ? 1 : (time < 300L ? 2 : 3));
        float rotate = Math.min(30.0f, this.dx / 5.0f);
        TextureDrawOptionsEnd options = MobRegistry.Textures.ancientVulture.initDraw().sprite(sprite, 0, 320).light(light).mirror(dir == 0, false).rotate(rotate, 160, 100).pos(drawX, drawY);
        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera);
        topList.add(tm -> {
            shadowOptions.draw();
            options.draw();
        });
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.ancientVulture_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 10;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 32;
        int drawY = y - 16;
        int dir = this.getDir();
        MobRegistry.Textures.ancientVulture.initDraw().sprite(0, 4, 96, 96).size(48, 48).mirror(dir == 0, false).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-21, -16, 40, 32);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public class AncientVultureAI<T extends AncientVultureMob>
    extends AINode<T> {
        private int removeCounter;
        private Mob currentTarget;
        private long findNewTargetTime;
        private final ArrayList<AttackRotation> attackRotations = new ArrayList();
        private int currentRotation;

        public AncientVultureAI() {
            this.attackRotations.add(new FlyAroundAttackRotation());
            this.attackRotations.add(new SimpleProjectileAttackRotation());
            this.attackRotations.add(new PlopEggsAttackRotation());
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
            blackboard.onRemoved(e -> AncientVultureMob.this.spawnedMobs.forEach(Mob::remove));
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
                    this.currentRotation = (this.currentRotation + 1) % this.attackRotations.size();
                    this.attackRotations.get(this.currentRotation).start();
                }
                this.attackRotations.get(this.currentRotation).tick();
            } else {
                AncientVultureMob.this.stopMoving();
                ++this.removeCounter;
                if (this.removeCounter > 100) {
                    AncientVultureMob.this.remove();
                }
            }
            return AINodeResult.SUCCESS;
        }

        public void tickTargets() {
            if (this.currentTarget != null) {
                if (!this.currentTarget.isSamePlace((Entity)this.mob()) || this.currentTarget.removed()) {
                    this.refreshTarget();
                } else if (AncientVultureMob.this.getWorldEntity().getTime() <= this.findNewTargetTime) {
                    this.refreshTarget();
                }
            } else {
                this.refreshTarget();
            }
        }

        public void refreshTarget() {
            this.currentTarget = GameUtils.streamServerClients(AncientVultureMob.this.getLevel()).map(c -> c.playerMob).min(Comparator.comparingInt(player -> (int)AncientVultureMob.this.getDistance((Mob)player))).orElse(null);
            this.findNewTargetTime = AncientVultureMob.this.getWorldEntity().getTime() + 5000L;
        }

        private class FlyAroundAttackRotation
        extends AttackRotation {
            private final Point[] positionRotation;
            private int currentPoint;
            private int timer;

            private FlyAroundAttackRotation() {
                this.positionRotation = new Point[]{new Point(260, 260), new Point(-260, -180), new Point(260, -260), new Point(-260, 180)};
            }

            @Override
            public void start() {
                this.timer = 0;
            }

            @Override
            public void tick() {
                ++this.timer;
                if (AncientVultureMob.this.hasArrivedAtTarget()) {
                    this.currentPoint = (this.currentPoint + 1) % this.positionRotation.length;
                }
                Point point = this.positionRotation[this.currentPoint];
                AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, point.x, point.y));
            }

            @Override
            public boolean isOver() {
                float healthPerc = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
                return (float)this.timer > 20.0f * (5.0f + healthPerc * 10.0f);
            }
        }

        private class SimpleProjectileAttackRotation
        extends AttackRotation {
            private int timer;

            private SimpleProjectileAttackRotation() {
            }

            @Override
            public void start() {
                this.timer = 0;
            }

            @Override
            public void tick() {
                ++this.timer;
                AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, 0.0f, -200.0f));
                float healthPerc = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
                int divider = healthPerc < 0.15f ? 4 : (healthPerc < 0.4f ? 3 : (healthPerc < 0.7f ? 2 : 1));
                if (this.timer % (20 / divider) == 0) {
                    AncientVultureMob.this.flickSoundAbility.runAndSend();
                    AncientVultureMob.this.getLevel().entityManager.projectiles.add(new AncientVultureProjectile((Mob)AncientVultureAI.this.mob(), AncientVultureMob.this.getX(), AncientVultureMob.this.getY(), AncientVultureAI.this.currentTarget.getX(), AncientVultureAI.this.currentTarget.getY(), projectileDamage));
                }
            }

            @Override
            public boolean isOver() {
                float healthPerc = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
                return (float)this.timer > 20.0f * (3.0f + healthPerc * 7.0f);
            }
        }

        private class PlopEggsAttackRotation
        extends AttackRotation {
            private int timer;
            private float buffer;

            private PlopEggsAttackRotation() {
            }

            @Override
            public void start() {
                this.timer = 0;
                this.buffer = 0.0f;
                this.findNewPosition();
            }

            @Override
            public void tick() {
                ++this.timer;
                float healthPerc = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
                int secs = healthPerc < 0.08f ? 1 : (healthPerc < 0.3f ? 2 : (healthPerc < 0.5f ? 3 : (healthPerc < 0.7f ? 4 : 5)));
                long clients = GameUtils.streamServerClients(AncientVultureMob.this.getLevel()).filter(c -> !c.isDead() && AncientVultureMob.this.getDistance(c.playerMob) < 1280.0f).count();
                float spawnsMod = Math.min(1.0f + (float)(clients - 1L) / 2.0f, 4.0f);
                this.buffer += 1.0f / (float)secs / 20.0f * spawnsMod;
                if (this.buffer > 1.0f) {
                    this.buffer -= 1.0f;
                    AncientVultureMob.this.popSoundAbility.runAndSend();
                    AncientVultureEggMob mob = new AncientVultureEggMob(AncientVultureMob.this);
                    AncientVultureMob.this.getLevel().entityManager.addMob(mob, AncientVultureMob.this.getX(), AncientVultureMob.this.getY());
                    AncientVultureMob.this.spawnedMobs.removeIf(Entity::removed);
                    AncientVultureMob.this.spawnedMobs.add(mob);
                }
                if (AncientVultureMob.this.hasArrivedAtTarget()) {
                    this.findNewPosition();
                }
            }

            @Override
            public boolean isOver() {
                float healthPerc = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
                return (float)this.timer > 20.0f * (7.0f + healthPerc * 13.0f);
            }

            private void findNewPosition() {
                if (AncientVultureAI.this.currentTarget == null) {
                    AncientVultureMob.this.setMovement(new MobMovementLevelPos(AncientVultureMob.this.x, AncientVultureMob.this.y));
                } else {
                    float angle = GameRandom.globalRandom.nextInt(360);
                    float nx = (float)Math.cos(Math.toRadians(angle));
                    float ny = (float)Math.sin(Math.toRadians(angle));
                    float distance = 300.0f;
                    AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, nx * distance, ny * distance));
                }
            }
        }

        private abstract class AttackRotation {
            private AttackRotation() {
            }

            public abstract void start();

            public abstract void tick();

            public abstract boolean isOver();
        }
    }
}

