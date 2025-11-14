/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GamePoint3D;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InverseKinematics;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
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
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.entity.projectile.QueenSpiderSpitProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenSpiderMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(LootItem.between("cavespidergland", 10, 20, MultiTextureMatItem.getGNDData(1)), new ChanceLootItem(0.2f, "queenspidersdancevinyl"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("spidercharm"), new LootItem("spiderclaw"), new LootItemList(new LootItem("webbedgun"), LootItem.between("simplebullet", 50, 100)), new LootItem("frostpiercer"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("spiderheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("spiderheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("spiderheart"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    private ArrayList<SpiderLeg> frontLegs;
    private ArrayList<SpiderLeg> backLegs;
    public float currentHeight;
    public EmptyMobAbility roarAbility;
    public IntMobAbility startLaunchAnimation;
    public long launchStartTime;
    public int launchAnimationTime;
    public EmptyMobAbility playSpitSoundAbility;
    public int jumpStartX;
    public int jumpStartY;
    public int jumpEndX;
    public int jumpEndY;
    public boolean isJumping;
    public CoordinateMobAbility startJumpAbility;
    public static GameDamage collisionDamage = new GameDamage(26.0f);
    public static GameDamage spitDamage = new GameDamage(22.0f);
    public static GameDamage landDamage = new GameDamage(55.0f);
    public static GameDamage hatchlingDamage = new GameDamage(22.0f);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(4000, 4500, 5000, 5500, 6000);
    public static float SPIT_LINGER_SECONDS = 60.0f;

    public QueenSpiderMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 20;
        this.setSpeed(75.0f);
        this.setArmor(5);
        this.setFriction(2.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-60, -60, 120, 90);
        this.hitBox = new Rectangle(-60, -60, 120, 90);
        this.selectBox = new Rectangle(-70, -45, 140, 100);
        this.roarAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (QueenSpiderMob.this.isClient()) {
                    SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7f).pitch(1.3f));
                }
            }
        });
        this.startLaunchAnimation = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                QueenSpiderMob.this.launchStartTime = QueenSpiderMob.this.getWorldEntity().getLocalTime();
                QueenSpiderMob.this.launchAnimationTime = value;
                if (QueenSpiderMob.this.isClient()) {
                    SoundManager.playSound(GameResources.spit, (SoundEffect)SoundEffect.effect(QueenSpiderMob.this));
                }
            }
        });
        this.playSpitSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (QueenSpiderMob.this.isClient()) {
                    SoundManager.playSound(GameResources.spit, (SoundEffect)SoundEffect.effect(QueenSpiderMob.this));
                }
            }
        });
        this.startJumpAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                QueenSpiderMob.this.jumpStartX = QueenSpiderMob.this.getX();
                QueenSpiderMob.this.jumpStartY = QueenSpiderMob.this.getY();
                QueenSpiderMob.this.jumpEndX = x;
                QueenSpiderMob.this.jumpEndY = y;
                QueenSpiderMob.this.isJumping = true;
            }
        });
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isJumping);
        if (this.isJumping) {
            writer.putNextInt(this.jumpStartX);
            writer.putNextInt(this.jumpStartY);
            writer.putNextInt(this.jumpEndX);
            writer.putNextInt(this.jumpEndY);
        }
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isJumping = reader.getNextBoolean();
        if (this.isJumping) {
            this.jumpStartX = reader.getNextInt();
            this.jumpStartY = reader.getNextInt();
            this.jumpEndX = reader.getNextInt();
            this.jumpEndY = reader.getNextInt();
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
    public void setPos(float x, float y, boolean isDirect) {
        super.setPos(x, y, isDirect);
        if (isDirect && this.backLegs != null && this.frontLegs != null) {
            for (SpiderLeg leg : this.backLegs) {
                leg.snapToPosition();
            }
            for (SpiderLeg leg : this.frontLegs) {
                leg.snapToPosition();
            }
        }
    }

    @Override
    public void init() {
        super.init();
        this.frontLegs = new ArrayList();
        this.backLegs = new ArrayList();
        int legCount = 8;
        this.currentHeight = this.getDesiredHeight();
        float[] angles = new float[]{65.0f, 95.0f, 125.0f, 155.0f, -65.0f, -95.0f, -125.0f, -155.0f};
        for (int i = 0; i < legCount; ++i) {
            final float angle = angles[i] - 90.0f;
            float offsetPercent = (float)(i + (i % 2 == 0 ? 4 : 0)) / (float)legCount % 1.0f;
            final Point2D.Float dir = GameMath.getAngleDir(angle);
            float maxLeftAngle = 170.0f;
            float maxRightAngle = 170.0f;
            if (dir.x < 0.0f) {
                maxRightAngle = 0.0f;
            } else if (dir.x > 0.0f) {
                maxLeftAngle = 0.0f;
            }
            SpiderLeg leg = new SpiderLeg(this, 125.0f, offsetPercent, maxLeftAngle, maxRightAngle){

                @Override
                public GamePoint3D getCenterPosition() {
                    int dist = 50;
                    return new GamePoint3D((float)QueenSpiderMob.this.getDrawX() + dir.x * (float)dist, (float)QueenSpiderMob.this.getDrawY() + dir.y * (float)dist * 0.5f, QueenSpiderMob.this.getFlyingHeight());
                }

                @Override
                public GamePoint3D getDesiredPosition() {
                    Point2D.Float dir2 = GameMath.getAngleDir(angle);
                    int dist = 130;
                    float moveMod = Math.min(QueenSpiderMob.this.getCurrentSpeed() / 250.0f, 1.0f);
                    Point2D.Float moveDir = GameMath.normalize(QueenSpiderMob.this.dx, QueenSpiderMob.this.dy);
                    if (moveDir.y < 0.0f) {
                        moveMod *= 1.0f + -moveDir.y * 30.0f / QueenSpiderMob.this.getSpeed();
                    }
                    return new GamePoint3D((float)QueenSpiderMob.this.getDrawX() + dir2.x * (float)dist + moveDir.x * (float)dist * moveMod, (float)QueenSpiderMob.this.getDrawY() + dir2.y * (float)dist + moveDir.y * (float)dist * moveMod, 0.0f);
                }

                @Override
                public float getJumpHeight() {
                    return QueenSpiderMob.this.getCurrentJumpHeight();
                }
            };
            if (dir.y < 0.0f) {
                this.backLegs.add(leg);
                continue;
            }
            this.frontLegs.add(leg);
        }
        this.frontLegs.sort(Comparator.comparingDouble(l -> l.y));
        this.backLegs.sort(Comparator.comparingDouble(l -> l.y));
        this.ai = new BehaviourTreeAI<QueenSpiderMob>(this, new SpiderMotherAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.queenspiderbegin, (SoundEffect)SoundEffect.effect(this).volume(1.3f).falloffDistance(4000));
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
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.9f), Float.valueOf(0.95f), Float.valueOf(1.0f)).floatValue();
        SoundManager.playSound(GameResources.queenspiderhurt, (SoundEffect)SoundEffect.effect(this).pitch(pitch).volume(0.2f).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.queenspiderdeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
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
    public void tickMovement(float delta) {
        float desiredHeight = this.getDesiredHeight();
        float heightDelta = desiredHeight - this.currentHeight;
        float heightSpeed = Math.abs(heightDelta) * 2.0f + 10.0f;
        float heightToMove = heightSpeed * delta / 250.0f;
        this.currentHeight = Math.abs(heightDelta) < heightToMove ? desiredHeight : (this.currentHeight += Math.signum(heightDelta) * heightToMove);
        if (this.isJumping) {
            float distToMove;
            float distToEnd = this.getDistance(this.jumpEndX, this.jumpEndY);
            if (distToEnd < (distToMove = this.getSpeed() * 1.3f * delta / 250.0f)) {
                int particles = 200;
                float anglePerParticle = 360.0f / (float)particles;
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(100, 200);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(100, 200) * 0.8f;
                    this.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8f).color(new Color(50, 50, 50)).heightMoves(0.0f, 30.0f).lifeTime(1000);
                }
                this.x = this.jumpEndX;
                this.y = this.jumpEndY;
                this.isJumping = false;
                this.stopMoving();
                if (this.isServer()) {
                    int size = 300;
                    int halfSize = size / 2;
                    Ellipse2D.Float hitBox = new Ellipse2D.Float(this.x - (float)halfSize, this.y - (float)halfSize * 0.8f, size, (float)size * 0.8f);
                    GameUtils.streamTargets(this, GameUtils.rangeTileBounds(this.getX(), this.getY(), 8)).filter(m -> m.canBeHit(this) && hitBox.intersects(m.getHitBox())).forEach(m -> m.isServerHit(landDamage, (float)m.getX() - this.x, (float)m.getY() - this.y, 150.0f, this));
                }
            } else {
                Point2D.Float dir = GameMath.normalize((float)this.jumpEndX - this.x, (float)this.jumpEndY - this.y);
                this.x += dir.x * distToMove;
                this.y += dir.y * distToMove;
            }
            this.calcNetworkSmooth(delta);
            for (SpiderLeg leg : this.backLegs) {
                leg.snapToPosition();
            }
            for (SpiderLeg leg : this.frontLegs) {
                leg.snapToPosition();
            }
        } else {
            super.tickMovement(delta);
            for (SpiderLeg leg : this.backLegs) {
                leg.tickMovement(delta);
            }
            for (SpiderLeg leg : this.frontLegs) {
                leg.tickMovement(delta);
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.QueenSpidersDance, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(70.0f + healthPercInv * 40.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(70.0f + healthPercInv * 40.0f);
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.currentHeight;
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
        float height = GameMath.sin(perc * 360.0f) * 10.0f;
        long localTime = this.getWorldEntity().getLocalTime();
        if (this.isJumping) {
            height = 0.0f;
        } else if (localTime < this.launchStartTime + (long)this.launchAnimationTime) {
            float progress = (float)(localTime - this.launchStartTime) / (float)this.launchAnimationTime;
            float endPref = 0.4f;
            float slope = 1.5f;
            height = 10.0f - (float)Math.pow(Math.sin(Math.pow((double)progress * Math.PI, endPref) / Math.pow(Math.PI, endPref - 1.0f)), slope) * 25.0f;
        }
        return 20 + (int)height;
    }

    public float getCurrentJumpHeight() {
        if (!this.isJumping) {
            return 0.0f;
        }
        float totalDist = (float)new Point2D.Float(this.jumpStartX, this.jumpStartY).distance(this.jumpEndX, this.jumpEndY);
        float distToEnd = (float)new Point2D.Float(this.x, this.y).distance(this.jumpEndX, this.jumpEndY);
        float distPerc = distToEnd / totalDist;
        return GameMath.sin(distPerc * 180.0f) * totalDist / 1.2f;
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y -= this.getFlyingHeight();
        return selectBox;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 7; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.queenSpiderDebris, i, 0, 32, this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 15.0f, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(QueenSpiderMob.getTileCoordinate(x), QueenSpiderMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        drawY -= this.getFlyingHeight();
        drawY = (int)((float)drawY - this.getCurrentJumpHeight());
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        TextureDrawOptionsEnd body = MobRegistry.Textures.queenSpiderBody.initDraw().light(light).rotate(rotate, MobRegistry.Textures.queenSpiderBody.getWidth() / 2, (int)((float)MobRegistry.Textures.queenSpiderBody.getHeight() * 0.6f)).posMiddle(drawX, drawY);
        TextureDrawOptionsEnd head = MobRegistry.Textures.queenSpiderHead.initDraw().light(light).rotate(rotate, MobRegistry.Textures.queenSpiderHead.getWidth() / 2, (int)((float)MobRegistry.Textures.queenSpiderHead.getHeight() * 0.6f)).posMiddle(drawX, drawY + 24);
        DrawOptionsList legsShadows = new DrawOptionsList();
        DrawOptionsList backLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList backLegsDrawsTop = new DrawOptionsList();
        DrawOptionsList frontLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList frontLegsDrawsTop = new DrawOptionsList();
        for (SpiderLeg leg : this.backLegs) {
            leg.addDrawOptions(legsShadows, backLegsDrawBottom, backLegsDrawsTop, level, camera);
        }
        for (SpiderLeg leg : this.frontLegs) {
            leg.addDrawOptions(legsShadows, frontLegsDrawBottom, frontLegsDrawsTop, level, camera);
        }
        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera);
        topList.add(tm -> {
            shadowOptions.draw();
            legsShadows.draw();
            backLegsDrawBottom.draw();
            backLegsDrawsTop.draw();
            body.draw();
            frontLegsDrawBottom.draw();
            frontLegsDrawsTop.draw();
            head.draw();
        });
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.queenSpider_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 24;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        MobRegistry.Textures.queenSpiderHead.initDraw().size(MobRegistry.Textures.queenSpiderHead.getWidth() / 2, MobRegistry.Textures.queenSpiderHead.getHeight() / 2).posMiddle(x, y).draw();
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

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static abstract class SpiderLeg {
        public final Mob mob;
        public float startX;
        public float startY;
        public float x;
        public float y;
        public float nextX;
        public float nextY;
        public boolean isMoving;
        private InverseKinematics ik;
        private List<Line2D.Float> shadowLines;
        public float maxLeftAngle;
        public float maxRightAngle;
        private final float moveAtDist;
        private float checkX;
        private float checkY;
        private float distBuffer;

        public SpiderLeg(Mob mob, float moveAtDist, float offsetPercent, float maxLeftAngle, float maxRightAngle) {
            this.mob = mob;
            this.moveAtDist = moveAtDist;
            this.distBuffer = moveAtDist * offsetPercent;
            this.maxLeftAngle = maxLeftAngle;
            this.maxRightAngle = maxRightAngle;
            this.snapToPosition();
            this.checkX = this.x;
            this.checkY = this.y;
        }

        public void snapToPosition() {
            GamePoint3D desiredPosition = this.getDesiredPosition();
            this.x = desiredPosition.x;
            this.y = desiredPosition.y;
            this.nextX = desiredPosition.x;
            this.nextY = desiredPosition.y;
            this.updateIK();
        }

        public void tickMovement(float delta) {
            GamePoint3D centerPos = this.getCenterPosition();
            double checkDist = new Point2D.Float(centerPos.x, centerPos.y).distance(this.checkX, this.checkY);
            this.distBuffer = (float)((double)this.distBuffer + checkDist);
            this.checkX = centerPos.x;
            this.checkY = centerPos.y;
            if (checkDist == 0.0) {
                this.distBuffer += delta / (this.moveAtDist / 20.0f);
            }
            if (!this.isMoving) {
                GamePoint3D desiredPos = this.getDesiredPosition();
                double desiredDist = new Point2D.Float(desiredPos.x, desiredPos.y).distance(this.x, this.y);
                if (desiredDist > 175.0) {
                    this.distBuffer += this.moveAtDist;
                }
                if (this.distBuffer >= this.moveAtDist) {
                    this.distBuffer -= this.moveAtDist;
                    if (this.x != desiredPos.x || this.y != desiredPos.y) {
                        this.startX = this.x;
                        this.startY = this.y;
                        this.nextX = desiredPos.x;
                        this.nextY = desiredPos.y;
                        this.isMoving = true;
                    }
                }
            }
            if (this.isMoving) {
                float speed;
                float distToMove;
                double nextDist = new Point2D.Float(this.x, this.y).distance(this.nextX, this.nextY);
                if (nextDist < (double)(distToMove = (speed = (float)nextDist * 2.0f + this.mob.getSpeed() * 1.2f) * delta / 250.0f)) {
                    if (this.mob.isClient()) {
                        int particles = 20;
                        float anglePerParticle = 360.0f / (float)particles;
                        for (int i = 0; i < particles; ++i) {
                            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(10, 40);
                            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(10, 40) * 0.8f;
                            this.mob.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8f).color(new Color(50, 50, 50)).heightMoves(0.0f, 10.0f).lifeTime(500);
                        }
                        SoundManager.playSound(GameResources.punch, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.4f).pitch(0.8f));
                    }
                    this.x = this.nextX;
                    this.y = this.nextY;
                    this.isMoving = false;
                } else {
                    Point2D.Float dir = GameMath.normalize(this.nextX - this.x, this.nextY - this.y);
                    this.x += dir.x * distToMove;
                    this.y += dir.y * distToMove;
                }
            }
            this.updateIK();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void updateIK() {
            if (this.mob.isServer()) {
                return;
            }
            GamePoint3D centerPos = this.getCenterPosition();
            Point2D.Float dir = GameMath.normalize(this.x - centerPos.x, this.y - centerPos.y);
            float jointDistMod = 0.5f;
            float dist = (float)new Point2D.Float(centerPos.x, centerPos.y).distance(this.x, this.y) * jointDistMod;
            float jointHeight = 40.0f;
            GamePoint3D jointPos = centerPos.dirFromLength(centerPos.x + dir.x * dist, centerPos.y + dir.y * dist, jointHeight, 80.0f);
            GamePoint3D footPos = jointPos.dirFromLength(this.x, this.y, 0.0f, 100.0f);
            float jumpHeight = this.getJumpHeight();
            float perspectiveMod = 0.6f;
            SpiderLeg spiderLeg = this;
            synchronized (spiderLeg) {
                this.shadowLines = Collections.synchronizedList(new LinkedList());
                this.shadowLines.add(new Line2D.Float(centerPos.x, centerPos.y + 18.0f, jointPos.x, jointPos.y - jointPos.height * perspectiveMod));
                this.shadowLines.add(new Line2D.Float(jointPos.x, jointPos.y - jointPos.height * perspectiveMod, this.x, this.y));
            }
            this.ik = InverseKinematics.startFromPoints(centerPos.x, centerPos.y - centerPos.height * perspectiveMod - jumpHeight, jointPos.x, jointPos.y - jointPos.height * perspectiveMod - jumpHeight, this.maxLeftAngle, this.maxRightAngle);
            this.ik.addJointPoint(footPos.x, footPos.y - footPos.height * perspectiveMod - jumpHeight);
            this.ik.apply(this.x, this.y - this.getCurrentLegLift() - Math.max(jumpHeight - 150.0f, jumpHeight / 4.0f), 0.0f, 2.0f, 500);
        }

        public float getCurrentLegLift() {
            double startDist = new Point2D.Float(this.startX, this.startY).distance(this.nextX, this.nextY);
            float lift = Math.min((float)startDist / 40.0f, 1.0f) * 20.0f + 5.0f;
            double currentDist = new Point2D.Float(this.x, this.y).distance(this.nextX, this.nextY);
            double progress = GameMath.limit(currentDist / startDist, 0.0, 1.0);
            return GameMath.sin((float)progress * 180.0f) * lift;
        }

        public abstract float getJumpHeight();

        public abstract GamePoint3D getDesiredPosition();

        public abstract GamePoint3D getCenterPosition();

        private static Shape generateShadowShape(Iterable<Line2D.Float> lines, float radius) {
            Object lastDir;
            Point2D intersectionPoint;
            Point2D.Float lastP2;
            Point2D.Float lastP1;
            Point2D.Float lastDir2;
            Point2D.Float p2;
            Point2D.Float p1;
            Point2D.Float dir;
            LinkedList<Line2D.Float> reverse = new LinkedList<Line2D.Float>();
            Path2D.Float path = new Path2D.Float();
            Line2D.Float first = null;
            Line2D.Float last = null;
            for (Line2D.Float line : lines) {
                if (first == null) {
                    first = line;
                }
                dir = GameMath.normalize(line.x1 - line.x2, line.y1 - line.y2);
                p1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, dir);
                p2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, dir);
                if (last != null) {
                    lastDir2 = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                    lastP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir2);
                    intersectionPoint = GameMath.getIntersectionPoint(new Line2D.Float(p1, p2), new Line2D.Float(lastP1, lastP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir2)), false);
                    if (intersectionPoint != null) {
                        path.lineTo(intersectionPoint.getX(), intersectionPoint.getY());
                    } else {
                        path.lineTo(lastP2.x, lastP2.y);
                        path.lineTo(p1.x, p1.y);
                    }
                } else {
                    path.moveTo(p1.x, p1.y);
                }
                last = line;
                reverse.addFirst(line);
            }
            if (last != null) {
                lastDir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                Point2D.Float lastP22 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, (Point2D.Float)lastDir);
                path.lineTo(lastP22.x, lastP22.y);
            }
            last = null;
            for (Line2D.Float line : reverse) {
                dir = GameMath.normalize(line.x2 - line.x1, line.y2 - line.y1);
                p1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, dir);
                p2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, dir);
                if (last != null) {
                    lastDir2 = GameMath.normalize(last.x2 - last.x1, last.y2 - last.y1);
                    lastP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir2);
                    lastP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir2);
                    intersectionPoint = GameMath.getIntersectionPoint(new Line2D.Float(p2, p1), new Line2D.Float(lastP2, lastP1), false);
                    if (intersectionPoint != null) {
                        path.lineTo(intersectionPoint.getX(), intersectionPoint.getY());
                    } else {
                        path.lineTo(lastP1.x, lastP1.y);
                        path.lineTo(p2.x, p2.y);
                    }
                } else {
                    path.lineTo(p2.x, p2.y);
                }
                last = line;
            }
            if (last != null) {
                lastDir = GameMath.normalize(last.x2 - last.x1, last.y2 - last.y1);
                Point2D.Float lastP12 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, (Point2D.Float)lastDir);
                path.lineTo(lastP12.x, lastP12.y);
            }
            path.closePath();
            return path;
        }

        private static LinkedList<Point2D.Float> generateShadowTriangles(Iterable<Line2D.Float> lines, float radius) {
            LinkedList<Point2D.Float> out = new LinkedList<Point2D.Float>();
            Line2D.Float last = null;
            for (Line2D.Float line : lines) {
                Point2D.Float dir = GameMath.normalize(line.x1 - line.x2, line.y1 - line.y2);
                Point2D.Float leftP1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, dir);
                Point2D.Float leftP2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, dir);
                Point2D.Float rightP1 = GameMath.getPerpendicularPoint(line.x1, line.y1, -radius, dir);
                Point2D.Float rightP2 = GameMath.getPerpendicularPoint(line.x2, line.y2, -radius, dir);
                if (last != null) {
                    Point2D.Float lastDir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                    Point2D.Float lastLeftP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir);
                    Point2D.Float lastLeftP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir);
                    Point2D leftIntersection = GameMath.getIntersectionPoint(new Line2D.Float(leftP1, leftP2), new Line2D.Float(lastLeftP1, lastLeftP2), false);
                    Point2D.Float lastRightP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, -radius, lastDir);
                    Point2D.Float lastRightP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, -radius, lastDir);
                    Point2D rightIntersection = GameMath.getIntersectionPoint(new Line2D.Float(rightP1, rightP2), new Line2D.Float(lastRightP1, lastRightP2), false);
                    if (leftIntersection != null) {
                        out.add(new Point2D.Float((float)leftIntersection.getX(), (float)leftIntersection.getY()));
                        out.add(lastRightP2);
                        out.add(new Point2D.Float((float)leftIntersection.getX(), (float)leftIntersection.getY()));
                        out.add(rightP1);
                    } else if (rightIntersection != null) {
                        out.add(lastLeftP2);
                        out.add(new Point2D.Float((float)rightIntersection.getX(), (float)rightIntersection.getY()));
                        out.add(leftP1);
                        out.add(new Point2D.Float((float)rightIntersection.getX(), (float)rightIntersection.getY()));
                    } else {
                        out.add(leftP1);
                        out.add(rightP1);
                    }
                } else {
                    out.add(leftP1);
                    out.add(rightP1);
                }
                last = line;
            }
            if (last != null) {
                Point2D.Float dir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                Point2D.Float leftP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, dir);
                Point2D.Float rightP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, -radius, dir);
                out.add(leftP2);
                out.add(rightP2);
            }
            return out;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addDrawOptions(DrawOptionsList legsShadows, DrawOptionsList legsDrawBottom, DrawOptionsList legsDrawsTop, Level level, GameCamera camera) {
            if (this.mob.isServer()) {
                return;
            }
            int legShadowWidth = MobRegistry.Textures.queenSpiderLeg_shadow.getWidth();
            SpiderLeg spiderLeg = this;
            synchronized (spiderLeg) {
                for (Line2D.Float shadowLine : this.shadowLines) {
                    GameLight light = level.getLightLevel(Entity.getTileCoordinate((shadowLine.x1 + shadowLine.x2) / 2.0f), Entity.getTileCoordinate((shadowLine.y1 + shadowLine.y2) / 2.0f));
                    float angle = GameMath.getAngle(new Point2D.Float(shadowLine.x1 - shadowLine.x2, shadowLine.y1 - shadowLine.y2));
                    float length = (float)shadowLine.getP1().distance(shadowLine.getP2());
                    TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.queenSpiderLeg_shadow.initDraw().rotate(angle + 90.0f, legShadowWidth / 2, 6).light(light).size(legShadowWidth, (int)length + 16).pos(camera.getDrawX(shadowLine.x1 - (float)legShadowWidth / 2.0f), camera.getDrawY(shadowLine.y1));
                    legsShadows.add(drawOptions);
                }
            }
            float jumpHeight = this.getJumpHeight();
            int legTextureWidth = MobRegistry.Textures.queenSpiderLeg.getWidth();
            for (InverseKinematics.Limb limb : this.ik.limbs) {
                GameLight light = level.getLightLevel(Entity.getTileCoordinate((limb.inboundX + limb.outboundX) / 2.0f), Entity.getTileCoordinate((limb.inboundY + limb.outboundY) / 2.0f + jumpHeight));
                TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.queenSpiderLeg.initDraw().rotate(limb.angle - 90.0f, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0f), camera.getDrawY(limb.inboundY) - 8);
                if (this.ik.limbs.getLast() == limb) {
                    legsDrawBottom.add(drawOptions);
                    continue;
                }
                legsDrawsTop.add(drawOptions);
            }
            if (GlobalData.debugActive()) {
                legsDrawsTop.add(() -> Renderer.drawCircle(camera.getDrawX(this.x), camera.getDrawY(this.y - this.getCurrentLegLift()), 4, 12, 1.0f, 0.0f, 0.0f, 1.0f, false));
                legsDrawsTop.add(() -> this.ik.drawDebug(camera, Color.RED, Color.GREEN));
            }
        }
    }

    public static class SpiderMotherAI<T extends QueenSpiderMob>
    extends SequenceAINode<T> {
        public SpiderMotherAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode<QueenSpiderMob> attackStages = new AttackStageManagerNode<QueenSpiderMob>();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new LaunchEggsStage());
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new FlyToOppositeDirectionAttackStage(true, 300.0f, 20.0f));
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new SpitStage());
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 500)));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new ChargeTargetStage());
            attackStages.addChild((AINode<QueenSpiderMob>)new IdleTimeAttackStage<QueenSpiderMob>(m -> this.getIdleTime(m, 1000)));
            attackStages.addChild(new ChargeTargetStage());
        }

        private int getIdleTime(T mob, int maxTime) {
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((QueenSpiderMob)mob).getMaxHealth();
            return (int)((float)maxTime * healthPerc);
        }
    }

    public static class JumpTargetStage<T extends QueenSpiderMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public boolean jumped = false;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.jumped = false;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target == null) {
                return AINodeResult.SUCCESS;
            }
            if (!this.jumped) {
                ((QueenSpiderMob)mob).startJumpAbility.runAndSend((int)(target.x + GameRandom.globalRandom.floatGaussian() * 30.0f), (int)(target.y + GameRandom.globalRandom.floatGaussian() * 30.0f));
                this.jumped = true;
            }
            if (((QueenSpiderMob)mob).isJumping) {
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }
    }

    public static class SpitStage<T extends QueenSpiderMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        private int timer;
        private float shootBuffer;
        private boolean reversed;
        private int radius;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            this.timer += 50;
            if (currentTarget != null) {
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((QueenSpiderMob)mob).getMaxHealth();
                float secondsPerSpit = 0.25f + healthPerc * 0.4f;
                this.shootBuffer += 1.0f / secondsPerSpit / 20.0f;
                if (this.shootBuffer >= 1.0f) {
                    this.shootBuffer -= 1.0f;
                    float healthPercInv = Math.abs(healthPerc - 1.0f);
                    float mod = GameRandom.globalRandom.nextFloat();
                    int targetDist = (int)(140.0f * mod);
                    float angle = GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(currentTarget.x + dir.x * (float)targetDist, currentTarget.y + dir.y * (float)targetDist);
                    int dist = Math.min((int)targetPos.distance(((QueenSpiderMob)mob).x, ((QueenSpiderMob)mob).y), 960);
                    float speed = 60.0f + healthPercInv * 40.0f;
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new QueenSpiderSpitProjectile(((Entity)mob).getLevel(), (Mob)mob, ((QueenSpiderMob)mob).x, ((QueenSpiderMob)mob).y, targetPos.x, targetPos.y, speed, dist, spitDamage, 50));
                    ((QueenSpiderMob)mob).playSpitSoundAbility.runAndSend();
                }
            }
            if (this.timer >= 4000) {
                blackboard.mover.stopMoving((Mob)mob);
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.RUNNING;
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
            this.shootBuffer = 0.0f;
            this.reversed = !this.reversed;
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.radius = GameMath.limit((int)((Mob)mob).getDistance(currentTarget), 200, 400);
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((QueenSpiderMob)mob).getMaxHealth();
                float speedMod = 0.5f + Math.abs(healthPerc - 1.0f) / 2.0f;
                float speed = MobMovementCircleLevelPos.convertToRotSpeed(this.radius, ((Mob)mob).getSpeed() * speedMod);
                blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)mob, currentTarget.x, currentTarget.y, this.radius, speed, this.reversed));
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class ChargeTargetStage<T extends QueenSpiderMob>
    extends FlyToOppositeDirectionAttackStage<T> {
        public ChargeTargetStage() {
            super(true, 250.0f, 0.0f);
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            super.onStarted(mob, blackboard);
            if (blackboard.mover.isMoving()) {
                ((QueenSpiderMob)mob).roarAbility.runAndSend();
                ((QueenSpiderMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, (Mob)mob, 5.0f, null), true);
            }
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
            super.onEnded(mob, blackboard);
            ((QueenSpiderMob)mob).buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
        }
    }

    public static class LaunchEggsStage<T extends QueenSpiderMob>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public float buffer;
        public float eggsPerLaunch;
        public int launchCounter;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.buffer = 0.0f;
            this.eggsPerLaunch = 1.0f;
            this.launchCounter = 0;
        }

        @Override
        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            float launchesPerSecond = 0.7f;
            this.buffer += 1.0f / launchesPerSecond / 20.0f;
            if (this.buffer >= 1.0f) {
                ((QueenSpiderMob)mob).startLaunchAnimation.runAndSend((int)(launchesPerSecond * 1000.0f));
                for (int i = 0; i < (int)this.eggsPerLaunch; ++i) {
                    float mod = GameRandom.globalRandom.getFloatBetween(0.7f, 1.0f);
                    int dist = (int)(300.0f * mod);
                    float angle = GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(((QueenSpiderMob)mob).x + dir.x * (float)dist, ((QueenSpiderMob)mob).y + dir.y * (float)dist);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new QueenSpiderEggProjectile(((Entity)mob).getLevel(), (Mob)mob, ((QueenSpiderMob)mob).x, ((QueenSpiderMob)mob).y, targetPos.x, targetPos.y, 30.0f, dist, new GameDamage(0.0f), 50));
                }
                this.buffer -= 1.0f;
                float healthPercInv = Math.abs(GameMath.limit((float)((Mob)mob).getHealth() / (float)((QueenSpiderMob)mob).getMaxHealth(), 0.0f, 1.0f) - 1.0f);
                float eggsIncPerLaunch = healthPercInv * 1.3f;
                long clients = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && mob.getDistance(c.playerMob) < 1280.0f).count();
                float clientsMod = Math.min(1.0f + (float)(clients - 1L) / 2.0f, 4.0f);
                this.eggsPerLaunch += eggsIncPerLaunch * clientsMod;
                ++this.launchCounter;
                if (this.launchCounter >= 4) {
                    return AINodeResult.SUCCESS;
                }
            }
            return AINodeResult.RUNNING;
        }
    }
}

