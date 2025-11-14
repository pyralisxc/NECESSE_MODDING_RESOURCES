/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
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
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SpawnProjectilesOnHealthLossAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.SwampGuardianBody;
import necesse.entity.mobs.hostile.bosses.SwampGuardianTail;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SwampBoulderProjectile;
import necesse.entity.projectile.SwampRazorProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampGuardianHead
extends BossWormMobHead<SwampGuardianBody, SwampGuardianHead> {
    public static LootTable lootTable = new LootTable(new ChanceLootItem(0.2f, "rumbleoftheswampguardianvinyl"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("razorbladeboomerang"), new LootItem("guardianshell"), new LootItem("dredgingstaff"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("guardianheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("guardianheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("guardianheart"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    public static float lengthPerBodyPart = 25.0f;
    public static float waveLength = 500.0f;
    public static final int totalBodyParts = 70;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public static GameDamage headCollisionDamage = new GameDamage(58.0f);
    public static GameDamage bodyCollisionDamage = new GameDamage(42.0f);
    public static GameDamage razorDamage = new GameDamage(42.0f);
    public static GameDamage boulderExplosionDamage = new GameDamage(70.0f);
    public static int boulderExplosionRange = 80;
    public static int totalRazorProjectiles = 250;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(6000, 12000, 15000, 18000, 24000);
    public final CoordinateMobAbility flickSound;
    public final CoordinateMobAbility swingSound;

    public SwampGuardianHead() {
        super(100, waveLength, 80.0f, 70, 36.0f, -8.0f);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 160;
        this.setSpeed(150.0f);
        this.setArmor(15);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-20, -15, 40, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -60, 64, 64);
        this.flickSound = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (SwampGuardianHead.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(x, y).pitch(1.5f).volume(0.5f));
                }
            }
        });
        this.swingSound = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (SwampGuardianHead.this.isClient()) {
                    SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(x, y).pitch(0.8f));
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
    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2f));
        }
    }

    @Override
    protected float getDistToBodyPart(SwampGuardianBody bodyPart, int index, float lastDistance) {
        return lengthPerBodyPart;
    }

    @Override
    protected SwampGuardianBody createNewBodyPart(int index) {
        SwampGuardianBody bodyPart = index == 69 ? new SwampGuardianTail() : new SwampGuardianBody();
        bodyPart.sharesHitCooldownWithNext = index % 3 < 2;
        boolean bl = bodyPart.relaysBuffsToNext = index % 3 < 2;
        if (index == 0 || index == 68) {
            bodyPart.sprite = new Point(4, 0);
            bodyPart.shadowSprite = 1;
        } else {
            bodyPart.sprite = new Point(index % 4, 0);
            bodyPart.shadowSprite = 0;
        }
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
        SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this).falloffDistance(1000));
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SwampGuardianHead>(this, new SwampGuardianAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swampguardianbegin, (SoundEffect)SoundEffect.effect(this).volume(1.3f).falloffDistance(4000));
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
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.swampguardianhurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.swampguardiandeath, (SoundEffect)SoundEffect.effect(this).volume(1.3f).falloffDistance(3000));
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return headCollisionDamage;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.RumbleOfTheSwampGuardian, SoundManager.MusicPriority.EVENT, 1.5f);
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
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampGuardian, GameRandom.globalRandom.nextInt(6), 6, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 48;
        int drawY = camera.getDrawY(y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        WormMobHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.swampGuardian, 0, 1, 96), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, headAngle, drawX, drawY, 64);
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(2, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
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
        MobRegistry.Textures.swampGuardian.initDraw().sprite(2, 2, 96).rotate(headAngle + 90.0f, 24, 24).size(48, 48).draw(drawX, drawY);
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

    public static class SwampGuardianAI<T extends SwampGuardianHead>
    extends SelectorAINode<T> {
        public SwampGuardianAI() {
            SequenceAINode chaserSequence = new SequenceAINode();
            this.addChild(chaserSequence);
            chaserSequence.addChild(new RemoveOnNoTargetNode(100));
            final TargetFinderAINode targetFinder = new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            };
            chaserSequence.addChild(targetFinder);
            targetFinder.moveToAttacker = false;
            ChargingCirclingChaserAINode chaserAI = new ChargingCirclingChaserAINode(500, 40);
            chaserSequence.addChild(chaserAI);
            chaserSequence.addChild(new SpawnProjectilesOnHealthLossAINode<T>(totalRazorProjectiles){

                @Override
                public void shootProjectile(T mob) {
                    WormMobHead.BodyPartTarget t = ((SwampGuardianHead)mob).getRandomTargetFromBodyPart(this, targetFinder, (m, bp) -> {
                        if (bp.getDistance((Mob)m) > 500.0f) {
                            return false;
                        }
                        CollisionFilter collisionFilter = bp.modifyChasingCollisionFilter(new CollisionFilter().mobCollision(), (Mob)m);
                        return !mob.getLevel().collides(new Line2D.Float(m.x, m.y, bp.x, bp.y), collisionFilter);
                    });
                    if (t != null) {
                        ((Entity)t.bodyPart).getLevel().entityManager.projectiles.add(new SwampRazorProjectile(((Entity)t.bodyPart).getLevel(), (Mob)mob, ((WormMobBody)t.bodyPart).x, ((WormMobBody)t.bodyPart).y, t.target.x, t.target.y, 70.0f, 1750, razorDamage, 50));
                        ((SwampGuardianHead)mob).flickSound.runAndSend(((Entity)t.bodyPart).getX(), ((Entity)t.bodyPart).getY());
                    }
                }
            });
            chaserSequence.addChild(new SpawnBouldersAI(targetFinder));
            chaserSequence.addChild(new DiveChargeRotationAI(chaserAI));
            this.addChild(new WandererAINode(0));
        }
    }

    public static class DiveChargeRotationAI<T extends SwampGuardianHead>
    extends AINode<T> {
        private int ticker;
        private final ChargingCirclingChaserAINode<T> chaserAI;

        public DiveChargeRotationAI(ChargingCirclingChaserAINode<T> chaserAI) {
            this.chaserAI = chaserAI;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = 100;
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                --this.ticker;
                if (this.ticker <= 0) {
                    if (!((SwampGuardianHead)mob).dive && !((SwampGuardianHead)mob).isUnderground) {
                        ((SwampGuardianHead)mob).diveAbility.runAndSend();
                        this.chaserAI.startCircling(mob, blackboard, target, 100);
                        this.ticker = (int)(20.0f * GameRandom.globalRandom.getFloatBetween(2.0f, 3.0f));
                    } else {
                        this.chaserAI.startCharge(mob, blackboard, target);
                        float currentAngle = GameMath.getAngle(new Point2D.Float(((SwampGuardianHead)mob).x - target.x, ((SwampGuardianHead)mob).y - target.y));
                        Point2D.Float dir = GameMath.getAngleDir(currentAngle);
                        ((SwampGuardianHead)mob).appearAbility.runAndSend(((SwampGuardianHead)mob).x, ((SwampGuardianHead)mob).y, -dir.x, -dir.y);
                        this.ticker = (int)(20.0f * GameRandom.globalRandom.getFloatBetween(8.0f, 9.0f));
                    }
                }
            }
            return AINodeResult.SUCCESS;
        }
    }

    public static class SpawnBouldersAI<T extends SwampGuardianHead>
    extends AINode<T> {
        public int ticker;
        public TargetFinderAINode<T> targetFinder;

        public SpawnBouldersAI(TargetFinderAINode<T> targetFinder) {
            this.targetFinder = targetFinder;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = (int)(20.0f * GameRandom.globalRandom.getFloatBetween(0.8f, 2.0f));
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            --this.ticker;
            if (this.ticker <= 0) {
                WormMobHead.BodyPartTarget t = ((SwampGuardianHead)mob).getRandomTargetFromBodyPart(this, this.targetFinder, (m, bp) -> {
                    float dist = m.getDistance((Mob)bp);
                    return dist > 350.0f && dist < 400.0f;
                });
                if (t != null) {
                    Point2D.Float targetPos = new Point2D.Float(t.target.x + GameRandom.globalRandom.floatGaussian() * 30.0f, t.target.y + GameRandom.globalRandom.floatGaussian() * 30.0f);
                    int dist = (int)((Mob)t.bodyPart).getDistance(targetPos.x, targetPos.y);
                    ((Entity)t.bodyPart).getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(((Entity)t.bodyPart).getLevel(), (Mob)mob, ((WormMobBody)t.bodyPart).x, ((WormMobBody)t.bodyPart).y, targetPos.x, targetPos.y, 40.0f, dist, new GameDamage(0.0f), 50));
                    ((SwampGuardianHead)mob).swingSound.runAndSend(((Entity)t.bodyPart).getX(), ((Entity)t.bodyPart).getY());
                }
                this.ticker = (int)(20.0f * GameRandom.globalRandom.getFloatBetween(0.8f, 2.0f));
            }
            return AINodeResult.SUCCESS;
        }
    }
}

