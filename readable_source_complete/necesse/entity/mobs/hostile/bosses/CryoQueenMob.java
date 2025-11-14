/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
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
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.GroundPillar;
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
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoQuakeProjectile;
import necesse.entity.projectile.CryoShardProjectile;
import necesse.entity.projectile.CryoVolleyProjectile;
import necesse.entity.projectile.CryoWarningProjectile;
import necesse.entity.projectile.CryoWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.pathProjectile.CryoQuakeCirclingProjectile;
import necesse.entity.projectile.pathProjectile.CryoWarningCirclingProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoQueenMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "battleforthefrozenreignvinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("cryoquake"), new LootItem("cryospear"), new LootItem("cryoblaster"), new LootItem("cryoglaive"));
    public static LootTable privateLootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, new ConditionLootItem("cryoheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("cryoheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("cryoheart"), false, false, true, true, "have") == 0;
    }), uniqueDrops));
    public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(8000, 14000, 18000, 21000, 26000);
    public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(29000, 40000, 46000, 52000, 63000);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public LinkedList<Mob> spawnedMobs = new LinkedList();
    public boolean attackingAnimation;
    public final EmptyMobAbility magicSoundAbility;
    public final EmptyMobAbility spiralSpawnSoundAbility;
    public final EmptyMobAbility jingleSoundAbility;
    public final EmptyMobAbility roarSoundAbility;
    public final EmptyMobAbility moveSoundAbility;
    public final BooleanMobAbility attackingAnimationAbility;
    public GameDamage collisionDamage = new GameDamage(60.0f);
    public GameDamage cryoQuakeDamage = new GameDamage(80.0f);
    public GameDamage cryoShardDamage = new GameDamage(70.0f);
    public GameDamage cryoWaveDamage = new GameDamage(80.0f);
    public GameDamage cryoVolleyDamage = new GameDamage(70.0f);
    public static GameDamage baseCollisionDamage = new GameDamage(60.0f);
    public static GameDamage baseCryoQuakeDamage = new GameDamage(80.0f);
    public static GameDamage baseCryoShardDamage = new GameDamage(70.0f);
    public static GameDamage baseCryoWaveDamage = new GameDamage(80.0f);
    public static GameDamage baseCryoVolleyDamage = new GameDamage(70.0f);
    public static GameDamage incursionCollisionDamage = new GameDamage(90.0f);
    public static GameDamage incursionCryoQuakeDamage = new GameDamage(120.0f);
    public static GameDamage incursionCryoShardDamage = new GameDamage(120.0f);
    public static GameDamage incursionCryoWaveDamage = new GameDamage(120.0f);
    public static GameDamage incursionCryoVolleyDamage = new GameDamage(110.0f);

    public CryoQueenMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
        this.moveAccuracy = 60;
        this.setSpeed(110.0f);
        this.setArmor(35);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-40, -80, 80, 105);
        this.hitBox = new Rectangle(-40, -80, 80, 105);
        this.selectBox = new Rectangle(-40, -90, 80, 120);
        this.magicSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoQueenMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(CryoQueenMob.this).falloffDistance(2000));
                }
            }
        });
        this.spiralSpawnSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoQueenMob.this.isClient()) {
                    SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(CryoQueenMob.this).falloffDistance(2000));
                    SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(CryoQueenMob.this).falloffDistance(2000));
                }
            }
        });
        this.jingleSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoQueenMob.this.isClient()) {
                    SoundSettings jingle = new SoundSettings(GameResources.jingle).pitchVariance(0.04f).fallOffDistance(1200);
                    SoundManager.playSound(jingle, CryoQueenMob.this);
                    SoundSettings magicBolt = new SoundSettings(GameResources.magicbolt2).volume(0.2f).basePitch(1.3f).pitchVariance(0.04f).fallOffDistance(1200);
                    SoundManager.playSound(magicBolt, CryoQueenMob.this);
                }
            }
        });
        this.roarSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoQueenMob.this.isClient()) {
                    SoundManager.playSound(new SoundSettings(GameResources.roar).volume(0.7f).basePitch(1.3f).pitchVariance(0.04f).fallOffDistance(1200), CryoQueenMob.this);
                }
            }
        });
        this.moveSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoQueenMob.this.isClient()) {
                    SoundManager.playSound(new SoundSettings(GameResources.swoosh).volume(0.2f).basePitch(0.5f).pitchVariance(0.0f).fallOffDistance(1400), CryoQueenMob.this);
                }
            }
        });
        this.attackingAnimationAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                CryoQueenMob.this.attackingAnimation = value;
            }
        });
        this.ambientSoundCooldownMin = 5000;
        this.ambientSoundCooldownMax = 10000;
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.attackingAnimation);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.attackingAnimation = reader.getNextBoolean();
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
            this.cryoQuakeDamage = incursionCryoQuakeDamage;
            this.cryoShardDamage = incursionCryoShardDamage;
            this.cryoWaveDamage = incursionCryoWaveDamage;
            this.cryoVolleyDamage = incursionCryoVolleyDamage;
        } else {
            this.collisionDamage = baseCollisionDamage;
            this.cryoQuakeDamage = baseCryoQuakeDamage;
            this.cryoShardDamage = baseCryoShardDamage;
            this.cryoWaveDamage = baseCryoWaveDamage;
            this.cryoVolleyDamage = baseCryoVolleyDamage;
        }
        super.init();
        this.ai = new BehaviourTreeAI<CryoQueenMob>(this, new CryoQueenAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.cryoqueenbegin, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(4000));
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
    public boolean canBePushed(Mob other) {
        return false;
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
            this.setDir(1);
        } else if (deltaX > 0.0f) {
            this.setDir(0);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.BattleForTheFrozenReign, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(110.0f + healthPercInv * 70.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(110.0f + healthPercInv * 70.0f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.cryoQueen, i, 17, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.cryoqueenhurt).volume(0.5f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.cryoqueendeath).pitchVariance(0.0f).fallOffDistance(3000);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CryoQueenMob.getTileCoordinate(x), CryoQueenMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 100;
        int dir = this.getDir();
        int frame = GameUtils.getAnim(this.getWorldEntity().getTime(), 5, 750);
        TextureDrawOptionsEnd body = MobRegistry.Textures.cryoQueen.initDraw().sprite(frame, this.attackingAnimation ? 0 : 1, 128).size(128, 128).light(light).mirror(dir != 0, false).pos(drawX, drawY);
        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera);
        topList.add(tm -> {
            shadowOptions.draw();
            body.draw();
        });
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.reaper_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 20;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        MobRegistry.Textures.cryoQueen.initDraw().sprite(0, 7, 64).size(32, 32).mirror(this.getDir() != 0, false).draw(drawX, drawY);
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

    public boolean isSecondStage() {
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        return healthPerc < 0.5f;
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static class CryoQueenAI<T extends CryoQueenMob>
    extends SequenceAINode<T> {
        public CryoQueenAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            SequenceAINode rotations = new SequenceAINode();
            rotations.addChild(new MoveToRandomPosition(true, 300));
            rotations.addChild(new IdleTime(100));
            rotations.addChild(new CryoQuakeRotation());
            rotations.addChild(new MoveToRandomPosition(true, 300));
            rotations.addChild(new IdleTime(100));
            rotations.addChild(new CryoShardRotation());
            rotations.addChild(new MoveToRandomPosition(true, 300));
            rotations.addChild(new IdleTime(100));
            rotations.addChild(new CryoWaveRotation());
            for (int i = 0; i < 3; ++i) {
                rotations.addChild(new MoveToRandomPosition(false, 300));
                rotations.addChild(new IdleTime(1000));
                rotations.addChild(new CryoVolleyRotation());
            }
            this.addChild(new IsolateRunningAINode(rotations));
        }

        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.onRemoved(e -> mob.spawnedMobs.forEach(Mob::remove));
        }
    }

    public static class CryoPillar
    extends GroundPillar {
        public GameTextureSection texture;
        public boolean mirror = GameRandom.globalRandom.nextBoolean();

        public CryoPillar(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            this.texture = MobRegistry.Textures.cryoQueen == null ? null : GameRandom.globalRandom.getOneOf(new GameTextureSection(MobRegistry.Textures.cryoQueen).sprite(0, 6, 64), new GameTextureSection(MobRegistry.Textures.cryoQueen).sprite(1, 6, 64), new GameTextureSection(MobRegistry.Textures.cryoQueen).sprite(2, 6, 64));
            this.behaviour = new GroundPillar.TimedBehaviour(300, 200, 800);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }

    public static class CryoVolleyRotation<T extends CryoQueenMob>
    extends RunningAINode<T> {
        private int ticker;

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving((Mob)mob);
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            ++this.ticker;
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((CryoQueenMob)mob).getMaxHealth();
            int maxTicks = 10 + (int)(20.0f * healthPerc * 1.5f);
            return this.ticker < maxTicks ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(false);
            LinkedList firedAngles = new LinkedList();
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((CryoQueenMob)mob).getMaxHealth();
            int speed = 150 + (int)(Math.abs(healthPerc - 1.0f) * 170.0f);
            GameUtils.streamServerClients(((Entity)mob).getLevel()).map(c -> c.playerMob).filter(m -> mob.getDistance((Mob)m) < 1120.0f).forEach(target -> {
                float angleToTarget = Projectile.getAngleToTarget(mob.x, mob.y, target.getX(), target.getY());
                if (firedAngles.stream().noneMatch(f -> Math.abs(GameMath.getAngleDifference(angleToTarget, f.floatValue())) < 35.0f)) {
                    Point2D.Float dir = GameMath.getAngleDir(angleToTarget + 90.0f);
                    int missiles = 6;
                    int perpRange = 150;
                    int perpPerMissile = perpRange / missiles;
                    for (int i = 0; i < missiles; ++i) {
                        int offsetDistance = GameRandom.globalRandom.getIntBetween(50, 100);
                        Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                        int perp = i * perpPerMissile - perpRange / 2;
                        offset = GameMath.getPerpendicularPoint(offset, (float)perp, dir);
                        Point2D.Float targetPos = new Point2D.Float(target.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), target.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                        mob.getLevel().entityManager.projectiles.add(new CryoVolleyProjectile(mob.x + offset.x, mob.y + offset.y - 30.0f, targetPos.x, targetPos.y, speed, 1440, mob.cryoVolleyDamage, 100, (Mob)mob));
                    }
                    firedAngles.add(Float.valueOf(angleToTarget));
                }
            });
            ((CryoQueenMob)mob).jingleSoundAbility.runAndSend();
        }
    }

    public static class CryoWaveRotation<T extends CryoQueenMob>
    extends RunningAINode<T> {
        private Point2D.Float attackDir = new Point2D.Float(1.0f, 0.0f);
        private boolean reverseDir;
        private int timerBuffer;

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(true);
            this.reverseDir = !this.reverseDir;
            this.timerBuffer = 0;
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                float targetDistance = ((Mob)mob).getDistance(currentTarget);
                float perc = GameMath.limit((targetDistance - 500.0f) / 500.0f, 0.0f, 1.0f);
                float angle = 45.0f + perc * 35.0f;
                float travelDistance = GameMath.cos(angle) * targetDistance * 1.6f;
                float attackAngle = (float)Math.toDegrees(Math.atan2(currentTarget.y - ((CryoQueenMob)mob).y, currentTarget.x - ((CryoQueenMob)mob).x)) + (this.reverseDir ? angle : -angle);
                Point2D.Float dir = GameMath.getAngleDir(attackAngle);
                this.attackDir = GameMath.getAngleDir(attackAngle + (float)(this.reverseDir ? -90 : 90));
                blackboard.mover.directMoveTo(this, (int)(((CryoQueenMob)mob).x + dir.x * travelDistance), (int)(((CryoQueenMob)mob).y + dir.y * travelDistance));
            }
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.timerBuffer += 50;
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((CryoQueenMob)mob).getMaxHealth();
                int msPerWave = 250 + (int)(healthPerc * 230.0f);
                if (this.timerBuffer > msPerWave) {
                    ((CryoQueenMob)mob).magicSoundAbility.runAndSend();
                }
                while (this.timerBuffer > msPerWave) {
                    this.timerBuffer -= msPerWave;
                    int speed = 80 + (int)(Math.abs(healthPerc - 1.0f) * 100.0f);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoWaveProjectile(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y, ((CryoQueenMob)mob).x + this.attackDir.x * 100.0f, ((CryoQueenMob)mob).y + this.attackDir.y * 100.0f, speed, 1280, ((CryoQueenMob)mob).cryoWaveDamage, 100, (Mob)mob));
                    if (((Mob)mob).getHealth() < ((CryoQueenMob)mob).getMaxHealth() / 2) {
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoWaveProjectile(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y, ((CryoQueenMob)mob).x - this.attackDir.x * 100.0f, ((CryoQueenMob)mob).y - this.attackDir.y * 100.0f, speed, 1280, ((CryoQueenMob)mob).cryoWaveDamage, 100, (Mob)mob));
                    }
                    if (blackboard.mover.isMoving()) continue;
                    return AINodeResult.SUCCESS;
                }
            } else {
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(false);
        }
    }

    public static class CryoShardRotation<T extends CryoQueenMob>
    extends RunningAINode<T> {
        private int ticker;
        private int timerBuffer;
        private boolean reversed;

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving((Mob)mob);
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
            this.timerBuffer = 0;
            this.reversed = !this.reversed;
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                int distance = GameMath.limit((int)((Mob)mob).getDistance(currentTarget), 100, 500);
                ((Mob)mob).setMovement(new MobMovementCircleLevelPos((Mob)mob, currentTarget.x, currentTarget.y, distance, 2.0f, this.reversed));
            }
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.timerBuffer += 50;
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((CryoQueenMob)mob).getMaxHealth();
                int msPerShard = 150 + (int)(healthPerc * 200.0f);
                if (this.timerBuffer > msPerShard) {
                    ((CryoQueenMob)mob).jingleSoundAbility.runAndSend();
                }
                while (this.timerBuffer > msPerShard) {
                    int speed = 100 + (int)(Math.abs(healthPerc - 1.0f) * 160.0f);
                    this.timerBuffer -= msPerShard;
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoShardProjectile(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y, currentTarget.x + (float)GameRandom.globalRandom.getIntBetween(-20, 20), currentTarget.y + (float)GameRandom.globalRandom.getIntBetween(-20, 20), speed, 1440, ((CryoQueenMob)mob).cryoShardDamage, 100, (Mob)mob));
                }
            }
            ++this.ticker;
            if (this.ticker <= 100) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
            ((Mob)mob).stopMoving();
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(false);
        }
    }

    public static class CryoQuakeRotation<T extends CryoQueenMob>
    extends RunningAINode<T> {
        private int timer;
        private int index;
        private float startAngle;
        private boolean circling;
        private boolean clockwise;
        private Point2D.Float startPos;
        private boolean playedWarningSound;
        private boolean playedQuakeSound;
        public final int msPerProjectile = 30;
        public final int totalProjectiles = 18;
        public final int anglePerProjectile = 20;

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(true);
            this.circling = GameRandom.globalRandom.nextBoolean();
            this.clockwise = GameRandom.globalRandom.nextBoolean();
            this.timer = 0;
            this.index = 0;
            this.playedWarningSound = false;
            this.playedQuakeSound = false;
            this.startPos = new Point2D.Float(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y + 15.0f);
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.startAngle = (float)Math.toDegrees(Math.atan2(currentTarget.y - this.startPos.y, currentTarget.x - this.startPos.x)) + 90.0f - 40.0f;
            }
            this.startAngle += (float)GameRandom.globalRandom.getIntBetween(-5, 5);
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            this.timer += 50;
            while (this.timer >= 30) {
                float speed;
                this.timer -= 30;
                float angle = this.startAngle + (float)(this.index * 20);
                if (this.index < 18) {
                    if (!this.playedWarningSound) {
                        ((CryoQueenMob)mob).magicSoundAbility.runAndSend();
                        this.playedWarningSound = true;
                    }
                    speed = this.getProjectileSpeed(mob);
                    if (this.circling) {
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoWarningCirclingProjectile(this.startPos.x, this.startPos.y, 20.0f, angle, this.clockwise, speed, 960));
                    } else {
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoWarningProjectile(this.startPos.x, this.startPos.y, angle, speed, 1120));
                    }
                } else if (this.index < 36) {
                    if (!this.playedQuakeSound) {
                        ((CryoQueenMob)mob).spiralSpawnSoundAbility.runAndSend();
                        this.playedQuakeSound = true;
                    }
                    speed = this.getProjectileSpeed(mob);
                    if (this.circling) {
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoQuakeCirclingProjectile(this.startPos.x, this.startPos.y, 20.0f, angle, this.clockwise, speed, 960, ((CryoQueenMob)mob).cryoQuakeDamage, 100, (Mob)mob));
                    } else {
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new CryoQuakeProjectile(this.startPos.x, this.startPos.y, angle, speed, 1120, ((CryoQueenMob)mob).cryoQuakeDamage, 100, (Mob)mob));
                    }
                } else {
                    return AINodeResult.SUCCESS;
                }
                ++this.index;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
            ((CryoQueenMob)mob).attackingAnimationAbility.runAndSend(false);
        }

        protected float getProjectileSpeed(T mob) {
            float percInv = Math.abs((float)((Mob)mob).getHealth() / (float)((CryoQueenMob)mob).getMaxHealth() - 1.0f);
            if (this.circling) {
                return 80.0f + percInv * 140.0f;
            }
            return 100.0f + percInv * 180.0f;
        }
    }

    public static class MoveToRandomPosition<T extends CryoQueenMob>
    extends RunningAINode<T> {
        public int baseDistance;
        public boolean isRunningWhileMoving;

        public MoveToRandomPosition(boolean isRunningWhileMoving, int baseDistance) {
            this.isRunningWhileMoving = isRunningWhileMoving;
            this.baseDistance = baseDistance;
        }

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            Point2D.Float base = new Point2D.Float(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y);
            if (currentTarget != null) {
                base = new Point2D.Float(currentTarget.x, currentTarget.y);
            }
            Point2D.Float pos = new Point2D.Float(((CryoQueenMob)mob).x, ((CryoQueenMob)mob).y);
            for (int i = 0; i < 10; ++i) {
                int randomAngle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float angleDir = GameMath.getAngleDir(randomAngle);
                pos = new Point2D.Float(base.x + angleDir.x * (float)this.baseDistance, base.y + angleDir.y * (float)this.baseDistance);
                if (((Mob)mob).getDistance(pos.x, pos.y) >= (float)this.baseDistance / 4.0f) break;
            }
            blackboard.mover.directMoveTo(this, (int)pos.x, (int)pos.y);
            ((CryoQueenMob)mob).moveSoundAbility.runAndSend();
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            if (this.isRunningWhileMoving && blackboard.mover.isMoving()) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class IdleTime<T extends Mob>
    extends RunningAINode<T> {
        public int msToIdle;
        private int timer;

        public IdleTime(int msToIdle) {
            this.msToIdle = msToIdle;
        }

        @Override
        public void start(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
        }

        @Override
        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            this.timer += 50;
            return this.timer <= this.msToIdle ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        @Override
        public void end(T mob, Blackboard<T> blackboard) {
        }
    }
}

