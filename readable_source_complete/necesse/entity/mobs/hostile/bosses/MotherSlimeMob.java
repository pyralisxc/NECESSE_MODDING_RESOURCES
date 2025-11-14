/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.CameraShake;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
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
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeWarningEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SlimeEggProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MotherSlimeMob
extends FlyingBossMob {
    public static RotationLootItem vinylRotation = RotationLootItem.globalLootRotation(new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), RotationLootItem.globalLootRotation(4, new LootItem("slimesurgevinyl"), new LootItem("motherslimestremblevinyl")));
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(vinylRotation));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemInterface[0]);
    public static LootTable privateLootTable = new LootTable(new LootItemMultiplierIgnored(uniqueDrops));
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(35000, 46000, 52000, 58000, 69000);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(20);
    protected float height;
    protected long wobbleTimerOffset;
    protected long jumpStartTime;
    protected int jumpAnimationTime;
    protected float jumpStartX;
    protected float jumpStartY;
    protected float jumpTargetX;
    protected float jumpTargetY;
    public final SlimeBossJumpMobAbility jumpAbility;
    protected long squishStartTime;
    protected int squishAnimationTime;
    public CameraShake squishShake;
    public final IntMobAbility squishLaunchAbility;
    public static GameDamage collisionDamage = new GameDamage(150.0f);
    public static GameDamage quakeDamage = new GameDamage(130.0f);
    public final EmptyMobAbility flickSoundAbility;
    public final EmptyMobAbility popSoundAbility;

    public MotherSlimeMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 60;
        this.setSpeed(150.0f);
        this.setArmor(40);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-100, -100, 200, 100);
        this.hitBox = new Rectangle(-100, -100, 200, 100);
        this.selectBox = new Rectangle(-110, -140, 220, 150);
        this.jumpAbility = this.registerAbility(new SlimeBossJumpMobAbility());
        this.squishLaunchAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                MotherSlimeMob.this.squishStartTime = MotherSlimeMob.this.getLocalTime();
                MotherSlimeMob.this.squishAnimationTime = value;
                MotherSlimeMob.this.squishShake = new CameraShake(MotherSlimeMob.this.getLocalTime(), MotherSlimeMob.this.squishAnimationTime, 50, 2.0f, 2.0f, true);
            }
        });
        this.flickSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (MotherSlimeMob.this.isClient()) {
                    SoundManager.playSound(GameResources.slimeSplash4, (SoundEffect)SoundEffect.effect(MotherSlimeMob.this).pitch(1.0f));
                    SoundManager.playSound(GameResources.flick, (SoundEffect)SoundEffect.effect(MotherSlimeMob.this).pitch(0.8f));
                }
            }
        });
        this.popSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (MotherSlimeMob.this.isClient()) {
                    SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(MotherSlimeMob.this).volume(0.3f).pitch(0.5f).falloffDistance(1400));
                }
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.jumpStartTime != 0L) {
            writer.putNextBoolean(true);
            int timeSinceStart = (int)(this.getLocalTime() - this.jumpStartTime);
            writer.putNextInt(timeSinceStart);
            writer.putNextFloat(this.jumpStartX);
            writer.putNextFloat(this.jumpStartY);
            writer.putNextFloat(this.jumpTargetX);
            writer.putNextFloat(this.jumpTargetY);
            writer.putNextInt(this.jumpAnimationTime);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        if (reader.getNextBoolean()) {
            int timeSinceStart = reader.getNextInt();
            this.jumpStartTime = this.getLocalTime() - (long)timeSinceStart;
            this.jumpStartX = reader.getNextFloat();
            this.jumpStartY = reader.getNextFloat();
            this.jumpTargetX = reader.getNextFloat();
            this.jumpTargetY = reader.getNextFloat();
            this.jumpAnimationTime = reader.getNextInt();
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
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<MotherSlimeMob>(this, new SlimeBossAI());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.motherslimebegin, (SoundEffect)SoundEffect.effect(this).volume(1.5f).falloffDistance(5000));
        }
    }

    @Override
    public void tickMovement(float delta) {
        if (this.jumpStartTime != 0L) {
            long timeSinceJumpStart = this.getLocalTime() - this.jumpStartTime;
            float jumpPercentProgress = GameMath.limit((float)timeSinceJumpStart / (float)this.jumpAnimationTime, 0.0f, 1.0f);
            if (timeSinceJumpStart <= (long)this.jumpAnimationTime) {
                float heightProgress = (float)Math.pow(jumpPercentProgress, 1.5);
                float moveProgress = (float)Math.pow(Math.min(jumpPercentProgress * 1.2f, 1.0f), 0.8f);
                this.x = GameMath.lerp(moveProgress, this.jumpStartX, this.jumpTargetX);
                this.y = GameMath.lerp(moveProgress, this.jumpStartY, this.jumpTargetY);
                this.height = (float)Math.sin(Math.PI * (double)heightProgress) * 140.0f;
            } else {
                this.spawnLandParticles();
                this.height = 0.0f;
                this.jumpStartTime = 0L;
                this.wobbleTimerOffset = this.getLocalTime() - 300L;
            }
        } else if (this.squishStartTime != 0L) {
            long timeSinceSquishStart = this.getLocalTime() - this.squishStartTime;
            if (timeSinceSquishStart > (long)this.squishAnimationTime) {
                this.squishStartTime = 0L;
                this.wobbleTimerOffset = this.getLocalTime();
            }
            this.height = 0.0f;
        } else {
            this.height = 0.0f;
        }
        super.tickMovement(delta);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.MotherSlimesTremble, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        this.particleTicks.gameTick();
        while (this.particleTicks.shouldTick()) {
            int x = this.getX() + GameRandom.globalRandom.getIntBetween(-90, 90);
            int y = this.getY() + 10;
            float dx = GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f);
            float startHeight = this.height + (float)GameRandom.globalRandom.getIntBetween(30, 90);
            float endHeight = startHeight + 300.0f;
            int lifeTime = GameRandom.globalRandom.getIntBetween(4000, 6000);
            this.getLevel().entityManager.addParticle(x, y, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).movesFriction(dx, 0.0f, 0.5f).sizeFades(14, 20).rotates().heightMoves(startHeight, endHeight).colorRandom(36.0f, 0.7f, 0.6f, 10.0f, 0.1f, 0.1f).fadesAlphaTime(500, 1000).lifeTime(lifeTime);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
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
        SoundManager.playSound(GameResources.motherslimehurt, (SoundEffect)SoundEffect.effect(this).pitch(pitch).volume(0.3f).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.motherslimedeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        if (this.height > 50.0f) {
            return false;
        }
        return super.canBeHit(attacker);
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
    public boolean canCollisionHit(Mob target) {
        return this.height < 20.0f && super.canCollisionHit(target);
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

    public void spawnLandParticles() {
        if (this.isServer()) {
            return;
        }
        int particles = 200;
        float anglePerParticle = 360.0f / (float)particles;
        for (int i = 0; i < particles; ++i) {
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            int startRange = GameRandom.globalRandom.getIntBetween(50, 80);
            float startX = this.x + (float)Math.sin(Math.toRadians(angle)) * (float)startRange;
            float startY = this.y + (float)Math.cos(Math.toRadians(angle)) * (float)startRange * 0.6f;
            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(50, 100);
            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(50, 100) * 0.6f;
            this.getLevel().entityManager.addParticle(startX, startY - 40.0f, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8f).colorRandom(36.0f, 0.7f, 0.6f, 10.0f, 0.1f, 0.1f).heightMoves(0.0f, 50.0f).lifeTime(1500);
        }
        SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(this).volume(0.7f).pitch(0.8f));
        SoundManager.playSound(GameResources.slimeSplash4, (SoundEffect)SoundEffect.effect(this).pitch(0.8f));
        this.getLevel().getClient().startCameraShake(this, 400, 40, 8.0f, 8.0f, true);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 10; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.motherSlime.body, i % 5, 4, 64, this.x, this.y - 40.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(MotherSlimeMob.getTileCoordinate(x), MotherSlimeMob.getTileCoordinate(y));
        float widthPercent = GameUtils.getAnimFloatContinuous(this.getWorldEntity().getLocalTime() - this.wobbleTimerOffset, 600);
        float heightPercent = GameUtils.getAnimFloatContinuous(this.getWorldEntity().getLocalTime() - this.wobbleTimerOffset + 200L, 600);
        if (this.jumpStartTime != 0L) {
            float next;
            long timeSinceJumpStart = this.getLocalTime() - this.jumpStartTime;
            float jumpPercentProgress = GameMath.limit((float)timeSinceJumpStart / (float)this.jumpAnimationTime, 0.0f, 1.0f);
            if (jumpPercentProgress < 0.4f) {
                next = jumpPercentProgress / 0.4f;
                widthPercent = 1.0f - next;
                heightPercent = 1.0f;
            } else if (jumpPercentProgress < 0.8f) {
                widthPercent = next = GameMath.limit((jumpPercentProgress - 0.4f) / 0.4f, 0.0f, 1.0f);
                heightPercent = 1.0f - next;
            } else {
                next = GameMath.limit((jumpPercentProgress - 0.8f) / 0.2f, 0.0f, 1.0f);
                widthPercent = 1.0f;
                heightPercent = next;
            }
        } else if (this.squishStartTime != 0L) {
            int expandTime;
            long timeSinceSquishStart = this.getLocalTime() - this.squishStartTime;
            if (timeSinceSquishStart < (long)(this.squishAnimationTime - (expandTime = Math.min(150, this.squishAnimationTime / 2)))) {
                if (!this.squishShake.isOver(this.getLocalTime())) {
                    Point2D.Float shake = this.squishShake.getCurrentShake(this.getLocalTime());
                    x = (int)((float)x + shake.x);
                    y = (int)((float)y + shake.y);
                }
                float compressProgress = GameMath.limit((float)timeSinceSquishStart / (float)(this.squishAnimationTime - expandTime), 0.0f, 1.0f);
                heightPercent = GameMath.lerp(compressProgress, 1.0f, -1.0f);
                widthPercent = 1.0f;
            } else {
                float expandProgress = GameMath.limit((float)(timeSinceSquishStart - (long)(this.squishAnimationTime - expandTime)) / (float)expandTime, 0.0f, 1.0f);
                heightPercent = GameMath.lerp(expandProgress, -1.0f, 1.0f);
                widthPercent = GameMath.lerp(expandProgress, 1.0f, 0.0f);
            }
        }
        float widthFloat = GameMath.lerp(widthPercent, 0.9f, 1.0f);
        int width = (int)(256.0f * widthFloat);
        float heightFloat = GameMath.lerp(heightPercent, 0.9f, 1.0f);
        int height = (int)(256.0f * heightFloat);
        int drawX = camera.getDrawX(x) - width / 2;
        int drawY = camera.getDrawY(y) - 215 + (256 - height);
        final TextureDrawOptionsEnd body = MobRegistry.Textures.motherSlime.body.initDraw().sprite(0, 0, 256).light(light).size(width, height).pos(drawX, drawY - (int)this.height);
        float shadowSize = 1.0f - GameMath.limit(this.height / 700.0f, 0.0f, 0.8f);
        int shadowWidth = (int)((float)width * shadowSize);
        int shadowHeight = (int)((float)height * shadowSize);
        int shadowDrawX = camera.getDrawX(x) - shadowWidth / 2;
        int shadowDrawY = camera.getDrawY(y) - 215 + (256 - shadowHeight);
        TextureDrawOptionsEnd shadowOptions = MobRegistry.Textures.motherSlime.shadow.initDraw().sprite(0, 0, 256).light(light).size(shadowWidth, shadowHeight).pos(shadowDrawX, shadowDrawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.height;
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
        MobRegistry.Textures.motherSlime.body.initDraw().sprite(0, 0, 256).size(48, 48).draw(drawX, drawY - (int)(this.height / 10.0f));
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

    public class SlimeBossJumpMobAbility
    extends MobAbility {
        public void runAndSend(float targetX, float targetY, int animationTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextFloat(targetX);
            writer.putNextFloat(targetY);
            writer.putNextInt(animationTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            MotherSlimeMob.this.jumpStartTime = MotherSlimeMob.this.getLocalTime();
            MotherSlimeMob.this.jumpStartX = MotherSlimeMob.this.x;
            MotherSlimeMob.this.jumpStartY = MotherSlimeMob.this.y;
            MotherSlimeMob.this.jumpTargetX = reader.getNextFloat();
            MotherSlimeMob.this.jumpTargetY = reader.getNextFloat();
            MotherSlimeMob.this.jumpAnimationTime = reader.getNextInt();
            if (!MotherSlimeMob.this.isServer()) {
                SoundManager.playSound(GameResources.slimeSplash2, (SoundEffect)SoundEffect.effect(MotherSlimeMob.this).pitch(0.6f));
            }
        }
    }

    public static class SlimeBossAI<T extends MotherSlimeMob>
    extends SequenceAINode<T> {
        public SlimeBossAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode<MotherSlimeMob> attackStages = new AttackStageManagerNode<MotherSlimeMob>();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new WaitForJumpDoneStage());
            attackStages.addChild((AINode<MotherSlimeMob>)new IdleTimeAttackStage<MotherSlimeMob>(m -> this.getIdleTime(m, 1000, 2000)));
            attackStages.addChild(new JumpSlamStage());
            attackStages.addChild(new WaitForJumpDoneStage());
            attackStages.addChild((AINode<MotherSlimeMob>)new IdleTimeAttackStage<MotherSlimeMob>(m -> this.getIdleTime(m, 500, 1000)));
            attackStages.addChild(new JumpQuakeStage(300, 0));
            attackStages.addChild(new WaitForJumpDoneStage());
            attackStages.addChild((AINode<MotherSlimeMob>)new IdleTimeAttackStage<MotherSlimeMob>(m -> this.getIdleTime(m, 500, 1000)));
            attackStages.addChild(new JumpQuakeStage(300, 200));
            attackStages.addChild(new WaitForJumpDoneStage());
            attackStages.addChild((AINode<MotherSlimeMob>)new IdleTimeAttackStage<MotherSlimeMob>(m -> this.getIdleTime(m, 500, 1000)));
            attackStages.addChild(new JumpQuakeStage(300, 300));
            attackStages.addChild(new WaitForJumpDoneStage());
            attackStages.addChild((AINode<MotherSlimeMob>)new IdleTimeAttackStage<MotherSlimeMob>(m -> this.getIdleTime(m, 500, 1000)));
            attackStages.addChild(new SquishLaunchStage());
        }

        public int getIdleTime(T mob, int minTime, int maxTime) {
            return GameMath.lerp(((Mob)mob).getHealthPercent(), minTime, maxTime);
        }

        public Mob getCurrentTarget() {
            return this.getBlackboard().getObject(Mob.class, "currentTarget");
        }

        public class WaitForJumpDoneStage
        extends AINode<T> {
            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                return ((MotherSlimeMob)mob).jumpStartTime == 0L ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
            }
        }

        public class JumpSlamStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public long nextJumpTime;
            public long endTime;

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.endTime = mob.getTime() + (long)GameMath.lerp(((Mob)mob).getHealthPercent(), 5000, 8000);
                this.nextJumpTime = 0L;
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.endTime <= mob.getTime()) {
                    return AINodeResult.SUCCESS;
                }
                if (this.nextJumpTime <= mob.getTime() && ((MotherSlimeMob)mob).jumpStartTime == 0L) {
                    Mob target = SlimeBossAI.this.getCurrentTarget();
                    if (target != null) {
                        float distance = GameMath.limit(target.getDistance((Mob)mob), 200.0f, 500.0f);
                        Point2D.Float dir = GameMath.normalize(target.x - ((MotherSlimeMob)mob).x, target.y - ((MotherSlimeMob)mob).y + 40.0f);
                        int randomXOffset = GameRandom.globalRandom.getIntBetween(-40, 40);
                        int randomYOffset = GameRandom.globalRandom.getIntBetween(-40, 40);
                        float exp = GameMath.expSmooth(((Mob)mob).getHealthPercent(), 1.0f, 0.3f);
                        int animationTime = GameMath.lerp(exp, 400, 900);
                        ((MotherSlimeMob)mob).jumpAbility.runAndSend(((MotherSlimeMob)mob).x + dir.x * distance + (float)randomXOffset, ((MotherSlimeMob)mob).y + dir.y * distance + (float)randomYOffset, animationTime);
                        int cooldownTime = GameMath.lerp(exp, 150, 800);
                        this.nextJumpTime = mob.getTime() + (long)animationTime + (long)cooldownTime;
                    } else {
                        this.nextJumpTime = mob.getTime() + 500L;
                    }
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        public class JumpQuakeStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public int desiredDistanceFromTarget;
            public int jumpDistance;
            public int targetX;
            public int targetY;
            public int jumpAnimationTime;
            public long warningTime;
            public long quakeTime;
            public int offset;
            public float velocity;

            public JumpQuakeStage(int desiredDistanceFromTarget, int jumpDistance) {
                this.desiredDistanceFromTarget = desiredDistanceFromTarget;
                this.jumpDistance = jumpDistance;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                if (this.jumpDistance != 0) {
                    Mob currentTarget = SlimeBossAI.this.getCurrentTarget();
                    if (currentTarget != null) {
                        float distToTarget = ((Mob)mob).getDistance(currentTarget);
                        Point2D.Float dirToTarget = GameMath.normalize(currentTarget.x - ((MotherSlimeMob)mob).x, currentTarget.y - ((MotherSlimeMob)mob).y);
                        float distancePercentNeeded = GameMath.limit(((float)this.desiredDistanceFromTarget - distToTarget) / (float)this.jumpDistance, -1.0f, 1.0f);
                        float angleOffset = distancePercentNeeded < 0.0f ? (float)GameMath.lerp(Math.abs(distancePercentNeeded), 90, 0) : (float)GameMath.lerp(Math.abs(distancePercentNeeded), 90, 180);
                        if (GameRandom.globalRandom.nextBoolean()) {
                            angleOffset = -angleOffset;
                        }
                        float angle = GameMath.fixAngle(GameMath.getAngle(dirToTarget) + angleOffset);
                        Point2D.Float dir = GameMath.getAngleDir(angle);
                        this.targetX = (int)(((MotherSlimeMob)mob).x + dir.x * (float)this.jumpDistance);
                        this.targetY = (int)(((MotherSlimeMob)mob).y + dir.y * (float)this.jumpDistance);
                    } else {
                        int angle = GameRandom.globalRandom.nextInt(360);
                        Point2D.Float dir = GameMath.getAngleDir(angle);
                        this.targetX = (int)(((MotherSlimeMob)mob).x + dir.x * (float)this.jumpDistance);
                        this.targetY = (int)(((MotherSlimeMob)mob).y + dir.y * (float)this.jumpDistance);
                    }
                } else {
                    this.targetX = ((Entity)mob).getX();
                    this.targetY = ((Entity)mob).getY();
                }
                float exp = GameMath.expSmooth(((Mob)mob).getHealthPercent(), 1.0f, 0.3f);
                this.jumpAnimationTime = GameMath.lerp(exp, 1000, 1200);
                this.warningTime = mob.getTime() + (long)this.jumpAnimationTime - 400L;
                this.quakeTime = this.warningTime + (long)GameMath.lerp(exp, 1000, 1300);
                this.offset = GameRandom.globalRandom.nextInt(15000);
                this.velocity = GameMath.lerp(exp, 700, 400);
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.jumpAnimationTime != 0) {
                    ((MotherSlimeMob)mob).jumpAbility.runAndSend(this.targetX, this.targetY, this.jumpAnimationTime);
                    this.jumpAnimationTime = 0;
                }
                if (this.warningTime != 0L && this.warningTime <= mob.getTime()) {
                    int timeToQuake = (int)(this.quakeTime - mob.getTime());
                    ((Entity)mob).getLevel().entityManager.events.add(new SlimeQuakeWarningEvent((Mob)mob, this.targetX, this.targetY - 40, new GameRandom(), 0.0f, this.velocity, 1200.0f, timeToQuake, this.offset));
                    this.warningTime = 0L;
                    return AINodeResult.RUNNING;
                }
                if (this.quakeTime != 0L && this.quakeTime <= mob.getTime()) {
                    ((Entity)mob).getLevel().entityManager.events.add(new SlimeQuakeEvent((Mob)mob, this.targetX, this.targetY - 40, new GameRandom(), 0.0f, quakeDamage, this.velocity, 50.0f, 1200.0f, (float)this.offset));
                    this.quakeTime = 0L;
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        public class SquishLaunchStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public boolean hasSentSoundEffect;

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                float exp = GameMath.expSmooth(((Mob)mob).getHealthPercent(), 1.0f, 0.3f);
                int animationTime = GameMath.lerp(exp, 1000, 3000);
                ((MotherSlimeMob)mob).squishLaunchAbility.runAndSend(animationTime);
                this.hasSentSoundEffect = false;
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                int expandTime;
                long timeSinceSquishStart;
                if (!this.hasSentSoundEffect && (timeSinceSquishStart = mob.getLocalTime() - ((MotherSlimeMob)mob).squishStartTime) >= (long)(((MotherSlimeMob)mob).squishAnimationTime - (expandTime = Math.min(150, ((MotherSlimeMob)mob).squishAnimationTime / 2)))) {
                    ((MotherSlimeMob)mob).flickSoundAbility.runAndSend();
                    this.hasSentSoundEffect = true;
                }
                return ((MotherSlimeMob)mob).squishStartTime == 0L ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
                int spawns = GameMath.lerp(((Mob)mob).getHealthPercent(), 5, 15);
                for (int i = 0; i < spawns; ++i) {
                    float mod = GameRandom.globalRandom.getFloatBetween(0.7f, 1.0f);
                    int dist = (int)(300.0f * mod);
                    float angle = GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(((MotherSlimeMob)mob).x + dir.x * (float)dist, ((MotherSlimeMob)mob).y + dir.y * (float)dist);
                    float x = ((MotherSlimeMob)mob).x + dir.x * 70.0f;
                    float y = ((MotherSlimeMob)mob).y - 40.0f + dir.y * 30.0f;
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new SlimeEggProjectile(((Entity)mob).getLevel(), (Mob)mob, x, y, targetPos.x, targetPos.y, 30.0f, dist, new GameDamage(0.0f), 50));
                }
            }
        }
    }
}

