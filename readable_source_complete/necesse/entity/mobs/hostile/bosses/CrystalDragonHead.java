/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystalBombEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystalDragonLaserLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
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
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.CrystalDragonBody;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CrystalDragonShardProjectile;
import necesse.entity.projectile.CrystalGolemSpawnProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
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

public class CrystalDragonHead
extends BossWormMobHead<CrystalDragonBody, CrystalDragonHead> {
    public static RotationLootItem vinylRotation = RotationLootItem.globalLootRotation(new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), new LootItemList(new LootItemInterface[0]), RotationLootItem.globalLootRotation(4, new LootItem("pastbehindglassvinyl"), new LootItem("dragonshoardvinyl")));
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(vinylRotation));
    protected SoundPlayer windSound;
    public static float lengthPerBodyPart = 60.0f;
    public static float waveLength = 800.0f;
    public static final int totalBodyParts = 7;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(35000, 46000, 52000, 58000, 69000);
    public static GameDamage headCollisionDamage = new GameDamage(150.0f);
    public static GameDamage bodyCollisionDamage = new GameDamage(115.0f);
    public static GameDamage shardDamage = new GameDamage(115.0f);
    public static GameDamage broadsideBarrageDamage = new GameDamage(115.0f);
    public static GameDamage laserDamage = new GameDamage(150.0f);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected float lastBombPlaceMoveUnits = 0.0f;
    protected float placeBombsEveryMoveUnits = 0.0f;
    protected final FloatMobAbility startPlaceBombs;
    protected SoundPlayer dashSoundPlayer;
    protected final EmptyMobAbility dashSoundAbility;

    public CrystalDragonHead() {
        super(100, waveLength, 100.0f, 7, 0.0f, -5.0f);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 100;
        this.movementUpdateCooldown = 2000;
        this.movePosTolerance = 100.0f;
        this.setSpeed(250.0f);
        this.setArmor(35);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
        this.startPlaceBombs = this.registerAbility(new FloatMobAbility(){

            @Override
            protected void run(float value) {
                CrystalDragonHead.this.lastBombPlaceMoveUnits = CrystalDragonHead.this.distanceMoved;
                CrystalDragonHead.this.placeBombsEveryMoveUnits = value;
            }
        });
        this.dashSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CrystalDragonHead.this.isClient() && (CrystalDragonHead.this.dashSoundPlayer == null || CrystalDragonHead.this.dashSoundPlayer.isDone())) {
                    CrystalDragonHead.this.dashSoundPlayer = SoundManager.playSound(new SoundSettings(GameResources.swoosh).volume(0.3f).basePitch(1.4f).pitchVariance(0.04f).fallOffDistance(1000), CrystalDragonHead.this);
                }
            }
        });
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
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
    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.roar, (SoundEffect)SoundEffect.effect(this).pitch(1.1f).volume(0.8f).falloffDistance(4000));
        }
    }

    @Override
    protected float getDistToBodyPart(CrystalDragonBody bodyPart, int index, float lastDistance) {
        if (index >= 1) {
            return lengthPerBodyPart + 10.0f;
        }
        return lengthPerBodyPart;
    }

    @Override
    protected CrystalDragonBody createNewBodyPart(int index) {
        CrystalDragonBody bodyPart = new CrystalDragonBody();
        bodyPart.spriteY = index + 1;
        bodyPart.sharesHitCooldownWithNext = true;
        bodyPart.relaysBuffsToNext = true;
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return headCollisionDamage;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0f && super.canCollisionHit(target);
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CrystalDragonHead>(this, new CrystalDragonHeadAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.crystaldragonbegin, (SoundEffect)SoundEffect.effect(this).volume(0.8f).falloffDistance(4000));
        }
    }

    @Override
    public float getWaveHeight(float length) {
        return super.getWaveHeight(length);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isClient() && this.placeBombsEveryMoveUnits > 0.0f) {
            float nextBombPlace;
            while (this.distanceMoved >= (nextBombPlace = this.lastBombPlaceMoveUnits + this.placeBombsEveryMoveUnits)) {
                this.lastBombPlaceMoveUnits += this.placeBombsEveryMoveUnits;
                CrystalBombEvent event = new CrystalBombEvent(this, this.getX(), this.getY(), shardDamage, GameRandom.globalRandom);
                this.getLevel().entityManager.events.add(event);
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.windSound == null || this.windSound.isDone()) {
            this.windSound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this).falloffDistance(1200).volume(0.7f));
        }
        if (this.windSound != null) {
            this.windSound.refreshLooping(1.0f);
        }
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.DragonsHoard, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        float mod = Math.abs((float)Math.pow(healthPerc, 0.5) - 1.0f);
        this.setSpeed(120.0f + mod * 90.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        float mod = Math.abs((float)Math.pow(healthPerc, 0.5) - 1.0f);
        this.setSpeed(120.0f + mod * 90.0f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crystalDragon, 4, GameRandom.globalRandom.nextInt(6), 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.crystaldragonhurt, (SoundEffect)SoundEffect.effect(this).pitch(pitch).volume(0.3f).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.crystaldragondeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        MobDrawable shoulderDrawableShadow;
        MobDrawable shoulderDrawable;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 112;
        int drawY = camera.getDrawY(this.y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        final MobDrawable headDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon, 0, 0, 224), null, light.minLevelCopy(100.0f), (int)this.height, headAngle, drawX, drawY, 130);
        MobDrawable headDrawableShadow = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon_shadow, 0, 0, 224), null, light, (int)this.height, headAngle, drawX, drawY + 40, 130);
        ComputedObjectValue<Object, Double> shoulderLine = new ComputedObjectValue<Object, Double>(null, () -> 0.0);
        shoulderLine = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 70.0);
        if (shoulderLine.object != null) {
            Point2D.Double shoulderPos = WormMobHead.linePos(shoulderLine);
            GameLight shoulderLight = level.getLightLevel(CrystalDragonHead.getTileCoordinate(shoulderPos.x), CrystalDragonHead.getTileCoordinate(shoulderPos.y));
            int shoulderDrawX = camera.getDrawX((float)shoulderPos.x) - 112;
            int shoulderDrawY = camera.getDrawY((float)shoulderPos.y);
            float shoulderHeight = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)shoulderLine.object).object).movedDist + ((Double)shoulderLine.get()).floatValue());
            float shoulderAngle = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - shoulderPos.x, (double)(this.y - this.height) - (shoulderPos.y - (double)shoulderHeight))));
            shoulderDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon, 0, 1, 224), null, shoulderLight.minLevelCopy(100.0f), (int)shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY, 130);
            shoulderDrawableShadow = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon_shadow, 0, 1, 224), null, shoulderLight, (int)shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY + 40, 130);
        } else {
            shoulderDrawable = null;
            shoulderDrawableShadow = null;
        }
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (shoulderDrawable != null) {
                    shoulderDrawable.draw(tickManager);
                }
                headDrawable.draw(tickManager);
            }
        });
        tileList.add(tickManager1 -> {
            if (shoulderDrawableShadow != null) {
                shoulderDrawableShadow.draw(tickManager);
            }
            headDrawableShadow.draw(tickManager);
        });
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 24;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        MobRegistry.Textures.crystalDragonHead.initDraw().sprite(0, 0, 136).rotate(headAngle - 90.0f, 24, 24).size(48, 48).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-15, -15, 30, 30);
    }

    @Override
    public GameTooltips getMapTooltips() {
        if (!this.isVisible()) {
            return null;
        }
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            LocalMessage message = new LocalMessage("misc", "bossdefeat", "name", this.getLocalization());
            c.sendPacket(new PacketChatMessage(message));
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    @Override
    public void dispose() {
        if (this.dashSoundPlayer != null) {
            this.dashSoundPlayer.stop();
        }
        super.dispose();
    }

    public static class CrystalDragonHeadAI<T extends CrystalDragonHead>
    extends SequenceAINode<T> {
        public CrystalDragonHeadAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new CirclingStage(900, 4000));
            attackStages.addChild(new BroadsideBarrageStage(35.0f, 400.0f, 150.0f));
            attackStages.addChild(new CirclingStage(600, 4000));
            attackStages.addChild(new SpawnCrystalGolemsStage());
            attackStages.addChild(new CirclingStage(600, 5000));
            attackStages.addChild(new ChargeTargetStage(100.0f));
            attackStages.addChild(new CirclingStage(900, 4000));
            attackStages.addChild(new BroadsideBarrageStage(35.0f, 400.0f, 150.0f));
            attackStages.addChild(new CirclingStage(600, 4000));
            attackStages.addChild(new SpawnCrystalGolemsStage());
            attackStages.addChild(new LaserSpiralStage(200, 100, 300, 5000, 1000, 5000));
            attackStages.addChild(new CirclingStage(900, 4000));
            attackStages.addChild(new BroadsideBarrageStage(35.0f, 400.0f, 150.0f));
            attackStages.addChild(new CirclingStage(600, 5000));
            attackStages.addChild(new ChargeTargetStage(100.0f));
        }
    }

    public static class LaserSpiralStage<T extends CrystalDragonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public int circlingRange;
        public int minRandomTargetDistanceOffset;
        public int maxRandomTargetDistanceOffset;
        public int indicatorWaitTime;
        public int laserRange;
        public int laserAliveTime;
        public long startTime;
        public CrystalDragonLaserLevelEvent event;
        public int startMoveAccuracy;

        public LaserSpiralStage(int circlingRange, int minRandomTargetDistanceOffset, int maxRandomTargetDistanceOffset, int indicatorWaitTime, int laserRange, int laserAliveTime) {
            this.circlingRange = circlingRange;
            this.minRandomTargetDistanceOffset = minRandomTargetDistanceOffset;
            this.maxRandomTargetDistanceOffset = maxRandomTargetDistanceOffset;
            this.indicatorWaitTime = indicatorWaitTime;
            this.laserRange = laserRange;
            this.laserAliveTime = laserAliveTime;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            if (this.event != null) {
                this.event.over();
            }
            this.event = null;
            this.startMoveAccuracy = ((CrystalDragonHead)mob).moveAccuracy;
            ((CrystalDragonHead)mob).moveAccuracy = 5;
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            Point basePos = target != null ? new Point(target.getX(), target.getY()) : new Point(((Entity)mob).getX(), ((Entity)mob).getY());
            int randomAngle = GameRandom.globalRandom.nextInt(360);
            int randomDistance = GameRandom.globalRandom.getIntBetween(this.minRandomTargetDistanceOffset, this.maxRandomTargetDistanceOffset);
            Point2D.Float dir = GameMath.getAngleDir(randomAngle);
            Point2D.Float circleCenter = new Point2D.Float((float)basePos.x + dir.x * (float)randomDistance, (float)basePos.y + dir.y * (float)randomDistance);
            float circlingSpeed = MobMovementCircle.convertToRotSpeed(this.circlingRange, ((CrystalDragonHead)this.mob()).getSpeed()) * 1.1f;
            this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)this.mob(), circleCenter.x, circleCenter.y, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean()));
            this.startTime = mob.getTime();
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
            ((CrystalDragonHead)mob).moveAccuracy = this.startMoveAccuracy;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            long laserStartTime = this.startTime + (long)this.indicatorWaitTime;
            if (mob.getTime() >= laserStartTime) {
                if (this.event == null) {
                    this.event = new CrystalDragonLaserLevelEvent((Mob)mob, new GameRandom(), this.laserRange, laserDamage, 100, this.laserAliveTime);
                    ((Entity)mob).getLevel().entityManager.events.add(this.event);
                } else if (this.event.isOver()) {
                    this.event = null;
                    return AINodeResult.SUCCESS;
                }
            }
            return AINodeResult.RUNNING;
        }
    }

    public static class SpawnCrystalGolemsStage<T extends CrystalDragonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        static GameTileRange range = new GameTileRange(10, new Point[0]);

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            ArrayList<Point> validSpawnTiles = new ArrayList<Point>();
            for (Point tile : range.getValidTiles(((Entity)mob).getTileX(), ((Entity)mob).getTileY())) {
                if (((Entity)mob).getLevel().isSolidTile(tile.x, tile.y)) continue;
                validSpawnTiles.add(tile);
            }
            if (!validSpawnTiles.isEmpty()) {
                for (int i = 0; i < 3; ++i) {
                    Point spawnTile = (Point)GameRandom.globalRandom.getOneOf(validSpawnTiles);
                    int targetX = spawnTile.x * 32 + 16;
                    int targetY = spawnTile.y * 32 + 16;
                    int distance = (int)((Mob)mob).getDistance(targetX, targetY);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new CrystalGolemSpawnProjectile(((Entity)mob).getLevel(), (Mob)mob, targetX, targetY, 30.0f, distance, new GameDamage(0.0f), 50));
                }
            }
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
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class BroadsideBarrageStage<T extends CrystalDragonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public float angleOffset;
        public float oppositeAngleDistance;
        public float shootWhenPastTargetDistance;
        public Mob target;
        public float flightAngle;
        public float distanceMovedWhenPerpendicular;
        public boolean hasShot;

        public BroadsideBarrageStage(float angleOffset, float oppositeAngleDistance, float shootWhenPastTargetDistance) {
            this.angleOffset = angleOffset;
            this.oppositeAngleDistance = oppositeAngleDistance;
            this.shootWhenPastTargetDistance = shootWhenPastTargetDistance;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.hasShot = false;
            this.target = blackboard.getObject(Mob.class, "currentTarget");
            if (this.target != null) {
                float angle = GameMath.getAngle(GameMath.normalize(this.target.x - ((CrystalDragonHead)mob).x, this.target.y - ((CrystalDragonHead)mob).y));
                float targetAngle = angle + GameRandom.globalRandom.getOneOf(Float.valueOf(-this.angleOffset), Float.valueOf(this.angleOffset)).floatValue();
                Point2D.Float finalDir = GameMath.getAngleDir(targetAngle);
                float targetX = this.target.x + finalDir.x * this.oppositeAngleDistance;
                float targetY = this.target.y + finalDir.y * this.oppositeAngleDistance;
                blackboard.mover.directMoveTo(this, (int)targetX, (int)targetY);
                this.flightAngle = GameMath.getAngle(GameMath.normalize(targetX - ((CrystalDragonHead)mob).x, targetY - ((CrystalDragonHead)mob).y));
                this.distanceMovedWhenPerpendicular = 0.0f;
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.target != null && blackboard.mover.isMoving()) {
                if (!this.hasShot) {
                    if (this.distanceMovedWhenPerpendicular == 0.0f) {
                        float angle = GameMath.getAngle(GameMath.normalize(this.target.x - ((CrystalDragonHead)mob).x, this.target.y - ((CrystalDragonHead)mob).y));
                        float delta = GameMath.getAngleDifference(this.flightAngle, angle);
                        if (delta > 90.0f || delta < -90.0f) {
                            this.distanceMovedWhenPerpendicular = ((CrystalDragonHead)mob).distanceMoved;
                        }
                    } else {
                        float distanceMoved = ((CrystalDragonHead)mob).distanceMoved - this.distanceMovedWhenPerpendicular;
                        if (distanceMoved >= this.shootWhenPastTargetDistance) {
                            for (LevelMob lBodyPart : ((CrystalDragonHead)mob).bodyParts) {
                                CrystalDragonBody bodyPart = (CrystalDragonBody)lBodyPart.get(((Entity)mob).getLevel());
                                if (bodyPart == null || !bodyPart.isVisible()) continue;
                                WormMobBody next = bodyPart.next;
                                if (next == null) {
                                    next = mob;
                                }
                                Point2D.Float dir = new Point2D.Float(next.x - bodyPart.x, next.y - bodyPart.y);
                                Point2D.Float perpDir = GameMath.getPerpendicularDir(dir.x, dir.y);
                                ((Entity)mob).getLevel().entityManager.projectiles.add(new CrystalDragonShardProjectile(bodyPart.x, bodyPart.y, bodyPart.x + perpDir.x * 100.0f, bodyPart.y + perpDir.y * 100.0f, 100.0f, 500, broadsideBarrageDamage, 50, (Mob)mob));
                                ((Entity)mob).getLevel().entityManager.projectiles.add(new CrystalDragonShardProjectile(bodyPart.x, bodyPart.y, bodyPart.x - perpDir.x * 100.0f, bodyPart.y - perpDir.y * 100.0f, 100.0f, 500, broadsideBarrageDamage, 50, (Mob)mob));
                            }
                            this.hasShot = true;
                        }
                    }
                }
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }
    }

    public static class ChargeTargetStage<T extends CrystalDragonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public int startMoveAccuracy;
        public Mob chargingTarget;
        public float bombPlacePerUnits;

        public ChargeTargetStage(float bombPlacePerUnits) {
            this.bombPlacePerUnits = bombPlacePerUnits;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.startMoveAccuracy = ((CrystalDragonHead)mob).moveAccuracy;
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                ((CrystalDragonHead)mob).moveAccuracy = 5;
                this.chargingTarget = target;
                this.getBlackboard().mover.setCustomMovement(this, new MobMovementRelative(target, 0.0f, 0.0f));
            }
            ((CrystalDragonHead)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, (Mob)mob, 30.0f, null), true);
            ((CrystalDragonHead)mob).startPlaceBombs.runAndSend(this.bombPlacePerUnits);
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
            ((CrystalDragonHead)mob).moveAccuracy = this.startMoveAccuracy;
            ((CrystalDragonHead)mob).buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
            this.chargingTarget = null;
            ((CrystalDragonHead)mob).startPlaceBombs.runAndSend(0.0f);
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (!((CrystalDragonHead)mob).buffManager.hasBuff(BuffRegistry.SPIDER_CHARGE)) {
                return AINodeResult.SUCCESS;
            }
            if (this.chargingTarget != null && !this.chargingTarget.removed()) {
                float distance = ((Mob)mob).getDistance(this.chargingTarget);
                if (distance > 300.0f && distance < 400.0f) {
                    ((CrystalDragonHead)mob).dashSoundAbility.runAndSend();
                }
                if (distance < 100.0f) {
                    float currentAngle = GameMath.getAngle(new Point2D.Float(((CrystalDragonHead)mob).dx, ((CrystalDragonHead)mob).dy));
                    float targetAngle = GameMath.getAngle(new Point2D.Float(this.chargingTarget.x - ((CrystalDragonHead)mob).x, this.chargingTarget.y - ((CrystalDragonHead)mob).y));
                    float diff = GameMath.getAngleDifference(currentAngle, targetAngle);
                    float maxAngle = 30.0f;
                    if (Math.abs(diff) >= maxAngle && distance > 75.0f || ((CrystalDragonHead)mob).dx == 0.0f && ((CrystalDragonHead)mob).dy == 0.0f) {
                        ((CrystalDragonHead)mob).moveAccuracy = this.startMoveAccuracy;
                        return AINodeResult.SUCCESS;
                    }
                }
            } else {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }
    }

    public static class CirclingStage<T extends CrystalDragonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public long statTime;
        public int circlingRange;
        public int circlingTime;

        public CirclingStage(int circlingRange, int circlingTime) {
            this.circlingRange = circlingRange;
            this.circlingTime = circlingTime;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.statTime = mob.getTime();
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            float circlingSpeed = MobMovementCircle.convertToRotSpeed(this.circlingRange, ((CrystalDragonHead)this.mob()).getSpeed()) * 1.1f;
            MobMovementCircle movement = target != null ? new MobMovementCircleRelative((Mob)this.mob(), target, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean()) : new MobMovementCircleLevelPos((Mob)this.mob(), ((CrystalDragonHead)this.mob()).x, ((CrystalDragonHead)this.mob()).y, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean());
            this.getBlackboard().mover.setCustomMovement(this, movement);
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            long endTime = this.statTime + (long)this.circlingTime;
            if (mob.getTime() < endTime) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }
    }
}

