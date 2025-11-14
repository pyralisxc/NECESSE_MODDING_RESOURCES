/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.FloatMobAbility;
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
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.StarBarrierPickupEntity;
import necesse.entity.projectile.CrescentDiscFollowingProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.StarVeilProjectile;
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

public class MoonlightDancerMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "moonlightsrehearsalvinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("duskhelmet", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("duskchestplate", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("duskboots", 1, new GNDItemMap().setInt("upgradeLevel", 100)));
    public static LootTable privateLootTable = new LootTable(new LootItemMultiplierIgnored(uniqueDrops), new ChanceLootItem(0.25f, "kineticboots").preventLootMultiplier());
    public float currentHeight;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    public final int defaultSpeed = 50;
    public final int invincibleSpeed = 200;
    public static GameDamage collisionDamage = new GameDamage(80.0f);
    public static GameDamage starVeilDamage = new GameDamage(115.0f);
    public static GameDamage crescentDiscDamage = new GameDamage(130.0f);
    public static GameDamage astralShotgunDamage = new GameDamage(115.0f);
    public static GameDamage crushingDarknessDamage = new GameDamage(400.0f);
    protected final BooleanMobAbility setInvincibilityAbility;
    protected final FloatMobAbility setInvincibilityAlphaAbility;
    public boolean isInvincible;
    public float invincibilityAlpha = 0.0f;
    protected final EmptyMobAbility chargeAstralShotgunParticleAbility;
    protected final EmptyMobAbility starVeilSoundAbility;
    protected final EmptyMobAbility crescentDiscSoundAbility;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(23000, 35000, 40000, 46000, 58000);

    public MoonlightDancerMob() {
        super(35000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(50.0f);
        this.setArmor(20);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-40, -30, 80, 80);
        this.hitBox = new Rectangle(-25, -60, 50, 160);
        this.selectBox = new Rectangle(-70, -85, 140, 210);
        this.setInvincibilityAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                if (!MoonlightDancerMob.this.isServer()) {
                    SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(MoonlightDancerMob.this).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 0.3f)).falloffDistance(5000));
                }
                MoonlightDancerMob.this.setSpeed(value ? 200.0f : 50.0f);
                MoonlightDancerMob.this.isInvincible = value;
            }
        });
        this.setInvincibilityAlphaAbility = this.registerAbility(new FloatMobAbility(){

            @Override
            protected void run(float value) {
                MoonlightDancerMob.this.invincibilityAlpha = value;
            }
        });
        this.chargeAstralShotgunParticleAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!MoonlightDancerMob.this.isClient()) {
                    return;
                }
                MoonlightDancerMob.this.spawnChargingParticles();
            }
        });
        this.starVeilSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!MoonlightDancerMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(MoonlightDancerMob.this).volume(0.75f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.8f)).falloffDistance(4000));
            }
        });
        this.crescentDiscSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (!MoonlightDancerMob.this.isClient()) {
                    return;
                }
                SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(MoonlightDancerMob.this).volume(2.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.8f)).falloffDistance(4000));
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
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isInvincible);
        writer.putNextFloat(this.invincibilityAlpha);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.setSpeed(reader.getNextInt());
        this.isInvincible = reader.getNextBoolean();
        this.setSpeed(this.isInvincible ? 200.0f : 50.0f);
        this.invincibilityAlpha = reader.getNextFloat();
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
        this.ai = new BehaviourTreeAI<MoonlightDancerMob>(this, new MoonlightDancerAI());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.moonlightdancerbegin, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(4000));
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
            SoundManager.setMusic(MusicRegistry.MoonlightsRehearsal, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.moonlightdancerhurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.moonlightdancerdeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
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
    public boolean canCollisionHit(Mob target) {
        if (this.isInvincible) {
            return false;
        }
        return super.canCollisionHit(target);
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
    public boolean canBeHit(Attacker attacker) {
        if (this.isInvincible) {
            return false;
        }
        return super.canBeHit(attacker);
    }

    public void spawnChargingParticles() {
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 50.0f;
        this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0f, 0.3f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x + GameMath.sin(angle) * distance;
            pos.y = this.y + 50.0f + (this.x - pos.x) - angle / 10.0f + GameMath.cos(angle) * distance;
        }).lifeTime(1000).sizeFades(22, 44);
        this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0f, 0.3f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x + GameMath.sin(angle) * distance;
            pos.y = this.y + 50.0f - (this.x - pos.x) - angle / 10.0f + GameMath.cos(angle) * distance;
        }).lifeTime(1000).sizeFades(22, 44);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(MoonlightDancerMob.getTileCoordinate(x), MoonlightDancerMob.getTileCoordinate(y));
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 80;
        int timePerFrame = 150;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 8;
        final TextureDrawOptionsEnd options = MobRegistry.Textures.moonlightDancer.initDraw().sprite(spriteIndex, 0, 128, 178).size(128, 178).rotate(rotate).light(light).pos(drawX, drawY -= this.getFlyingHeight()).alpha(1.0f - this.invincibilityAlpha / 2.0f);
        final TextureDrawOptionsEnd invincibilityOptions = MobRegistry.Textures.moonlightDancerInvincible.initDraw().sprite(spriteIndex, 0, 128, 178).size(128, 178).rotate(rotate).light(light).pos(drawX, drawY).alpha(this.invincibilityAlpha);
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
                invincibilityOptions.draw();
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
        int drawY = y - 23;
        MobRegistry.Textures.moonlightDancerHead.initDraw().sprite(0, 0, 48, 46).size(48, 46).draw(drawX, drawY);
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
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int i;
        GameRandom random = GameRandom.globalRandom;
        for (i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.moonlightDancerDebris, i, 0, 32, this.x + random.floatGaussian() * 15.0f, this.y + random.floatGaussian() * 15.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
        for (i = 0; i < 50; ++i) {
            this.getLevel().entityManager.addTopParticle(this.x + random.floatGaussian() * 16.0f, this.y + random.floatGaussian() * 12.0f, Particle.GType.IMPORTANT_COSMETIC).sizeFades(22, 11).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).movesFrictionAngle(random.getIntBetween(0, 360), 150.0f, 0.5f).lifeTime(5000).givesLight(75.0f, 0.5f);
        }
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
            int sunArenasCompleted;
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            if (isIncursionTier5OrHigher && c.achievementsLoaded() && !c.needPlayerStats() && (sunArenasCompleted = c.playerStats().completed_incursions.getData("sunarena").getTotalTiersAbove(5, true)) > 0) {
                c.achievements().MASTER_OF_SUN_AND_MOON.markCompleted((ServerClient)c);
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static class MoonlightDancerAI<T extends MoonlightDancerMob>
    extends SequenceAINode<T> {
        public MoonlightDancerAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 200));
            attackStages.addChild(new StartInvincibilityStage());
            attackStages.addChild(new CrushingDarknessStage(7500, 10000));
            attackStages.addChild(new IdleTimeAttackStage<MoonlightDancerMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new AstralShotgun(20, 3000));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 500));
            attackStages.addChild(new AstralShotgun(20, 3000));
            attackStages.addChild(new EndInvincibilityStage());
            attackStages.addChild(new IdleTimeAttackStage<MoonlightDancerMob>(m -> this.getIdleTime(m, 2000)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 200));
            attackStages.addChild(new CrescentDiscStage());
            attackStages.addChild(new IdleTimeAttackStage<MoonlightDancerMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 200));
            attackStages.addChild(new StarVeil());
        }

        private int getIdleTime(T mob, int maxTime) {
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((MoonlightDancerMob)mob).getMaxHealth();
            return (int)((float)maxTime * healthPerc);
        }
    }

    public static class AstralShotgun<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final int totalProjectiles;
        private final int attackDuration;
        private int projectilesRemaining;
        private float attackBuffer;
        private float elapsedTime;

        public AstralShotgun(int totalProjectiles, int attackDuration) {
            this.totalProjectiles = totalProjectiles;
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
            if (this.elapsedTime < (float)this.attackDuration / 2.0f) {
                ((MoonlightDancerMob)mob).chargeAstralShotgunParticleAbility.runAndSend();
                this.elapsedTime += 50.0f;
            } else {
                this.attackBuffer += 50.0f;
                if (this.attackBuffer > (float)(this.attackDuration / 2) / (float)this.totalProjectiles) {
                    GameRandom random = GameRandom.globalRandom;
                    Mob target = blackboard.getObject(Mob.class, "currentTarget");
                    float targetX = target.x + (float)random.getIntBetween(-200, 200);
                    float targetY = target.y + (float)random.getIntBetween(-200, 200);
                    StarVeilProjectile projectile = new StarVeilProjectile(((MoonlightDancerMob)mob).x, ((MoonlightDancerMob)mob).y, targetX, targetY, astralShotgunDamage, 200.0f, (Mob)mob);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    ((MoonlightDancerMob)mob).spawnedProjectiles.add(projectile);
                    ((MoonlightDancerMob)mob).starVeilSoundAbility.runAndSend();
                    --this.projectilesRemaining;
                }
            }
            if (this.projectilesRemaining > 0) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.elapsedTime = 0.0f;
            this.projectilesRemaining = this.totalProjectiles;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class CrescentDiscStage<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            CrescentDiscFollowingProjectile projectile = new CrescentDiscFollowingProjectile(((Entity)mob).getLevel(), ((MoonlightDancerMob)mob).x, ((MoonlightDancerMob)mob).y, target.x, target.y, 25.0f, 3000, crescentDiscDamage, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
            ((MoonlightDancerMob)mob).spawnedProjectiles.add(projectile);
            ((MoonlightDancerMob)mob).crescentDiscSoundAbility.runAndSend();
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class EndInvincibilityStage<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        float alpha;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.alpha -= 0.016666668f;
            ((MoonlightDancerMob)mob).setInvincibilityAlphaAbility.runAndSend(this.alpha);
            if (this.alpha <= 0.0f) {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.alpha = 1.0f;
            if (mob != null) {
                ((MoonlightDancerMob)mob).setInvincibilityAbility.runAndSend(false);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class StartInvincibilityStage<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        float alpha;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.alpha += 0.016666668f;
            ((MoonlightDancerMob)mob).setInvincibilityAlphaAbility.runAndSend(this.alpha);
            if (this.alpha >= 1.0f) {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.alpha = 0.0f;
            if (mob != null) {
                ((MoonlightDancerMob)mob).setInvincibilityAbility.runAndSend(true);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class StarVeil<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public int direction;
        private float angleBuffer;
        private float remainingAngle;
        private float currentAngle;
        private final float totalAngle = 1080.0f;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            this.angleBuffer += 18.0f;
            float anglePerProjectile = 13.0f;
            while (this.angleBuffer >= anglePerProjectile) {
                this.currentAngle -= anglePerProjectile;
                StarVeilProjectile projectile = new StarVeilProjectile(((MoonlightDancerMob)mob).x, ((MoonlightDancerMob)mob).y, this.currentAngle, starVeilDamage, 25.0f, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((MoonlightDancerMob)mob).spawnedProjectiles.add(projectile);
                ((MoonlightDancerMob)mob).starVeilSoundAbility.runAndSend();
                this.angleBuffer -= anglePerProjectile;
                this.remainingAngle -= anglePerProjectile;
                if (!(this.remainingAngle < 1.0f)) continue;
                break;
            }
            if (this.angleBuffer >= this.remainingAngle) {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.remainingAngle = 1080.0f;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class CrushingDarknessStage<T extends MoonlightDancerMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private final Function<T, Integer> attackDuration;

        public CrushingDarknessStage(Function<T, Integer> attackDuration) {
            this.attackDuration = attackDuration;
        }

        public CrushingDarknessStage(int noHealthIdleTime, int fullHealthIdleTime) {
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
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            return AINodeResult.SUCCESS;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            GameUtils.streamServerClients(((Entity)mob).getServer(), ((Entity)mob).getLevel()).forEach(c -> {
                PlayerMob player = c.playerMob;
                ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.CRUSHING_DARKNESS, (Mob)player, (int)this.attackDuration.apply((MoonlightDancerMob)mob), (Attacker)mob);
                ab.getGndData().setInt("uniqueID", mob.getUniqueID());
                player.buffManager.addBuff(ab, true);
                ArrayList<Point> spawnPoints = new ArrayList<Point>();
                int maxRange = 15;
                for (int x = -maxRange; x <= maxRange; ++x) {
                    int tileX = player.getTileX() + x;
                    for (int y = -maxRange; y <= maxRange; ++y) {
                        int tileY = player.getTileY() + y;
                        if (mob.getLevel().isSolidTile(tileX, tileY)) continue;
                        spawnPoints.add(new Point(tileX * 32 + 16, tileY * 32 + 16));
                    }
                }
                for (int i = 0; i < 4; ++i) {
                    if (!spawnPoints.isEmpty()) {
                        Point spawnPoint = (Point)spawnPoints.remove(GameRandom.globalRandom.nextInt(spawnPoints.size()));
                        StarBarrierPickupEntity pickup = new StarBarrierPickupEntity(mob.getLevel(), spawnPoint.x, spawnPoint.y, 0.0f, 0.0f);
                        mob.getLevel().entityManager.pickups.add(pickup);
                        continue;
                    }
                    player.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAR_BARRIER_BUFF, (Mob)player, 20000, null), true);
                }
            });
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
}

