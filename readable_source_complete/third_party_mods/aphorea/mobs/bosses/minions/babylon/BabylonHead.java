/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.MusicRegistry
 *  necesse.engine.sound.GameMusic
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.SoundManager$MusicPriority
 *  necesse.engine.sound.SoundPlayer
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.ComputedObjectValue
 *  necesse.engine.util.GameLinkedList$Element
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.WormMobHead
 *  necesse.entity.mobs.WormMoveLine
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.AINodeResult
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode
 *  necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode
 *  necesse.entity.mobs.ai.behaviourTree.util.AIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.hostile.bosses.BossWormMobHead
 *  necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface
 *  necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode
 *  necesse.entity.mobs.mobMovement.MobMovement
 *  necesse.entity.mobs.mobMovement.MobMovementCircle
 *  necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos
 *  necesse.entity.mobs.mobMovement.MobMovementCircleRelative
 *  necesse.entity.mobs.mobMovement.MobMovementRelative
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.Drawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.bosses.minions.babylon;

import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.mobs.bosses.minions.babylon.BabylonBody;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabylonHead
extends BossWormMobHead<BabylonBody, BabylonHead> {
    private SoundPlayer sound;
    public static float lengthPerBodyPart = 60.0f;
    public static float waveLength = 800.0f;
    public static GameDamage headCollisionDamage = new GameDamage(60.0f);
    public static GameDamage bodyCollisionDamage = new GameDamage(40.0f);
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameTexture icon;

    public BabylonHead() {
        super(100, waveLength, 100.0f, 7, 0.0f, -5.0f);
        this.moveAccuracy = 100;
        this.movementUpdateCooldown = 2000;
        this.movePosTolerance = 100.0f;
        this.setSpeed(100.0f);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
    }

    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.globalEffect().pitch(1.2f));
        }
    }

    protected float getDistToBodyPart(BabylonBody bodyPart, int index, float lastDistance) {
        return index >= 1 ? lengthPerBodyPart + 10.0f : lengthPerBodyPart;
    }

    protected BabylonBody createNewBodyPart(int index) {
        BabylonBody bodyPart = new BabylonBody();
        bodyPart.spriteY = index + 1;
        bodyPart.sharesHitCooldownWithNext = true;
        bodyPart.relaysBuffsToNext = true;
        return bodyPart;
    }

    protected void playMoveSound() {
    }

    public GameDamage getCollisionDamage(Mob target) {
        return headCollisionDamage;
    }

    public boolean canCollisionHit(Mob target) {
        return Math.abs(this.height - (float)target.getFlyingHeight()) < 45.0f && super.canCollisionHit(target);
    }

    public boolean canTakeDamage() {
        return false;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, new BabylonHeadAI(), (AIMover)new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.globalEffect().pitch(1.2f));
        }
    }

    public float getWaveHeight(float length) {
        return super.getWaveHeight(length);
    }

    public void clientTick() {
        super.clientTick();
        if (this.sound == null || this.sound.isDone()) {
            this.sound = SoundManager.playSound((GameSound)GameResources.wind1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this).falloffDistance(1400).volume(0.8f));
        }
        if (this.sound != null) {
            this.sound.refreshLooping(1.0f);
        }
        SoundManager.setMusic((GameMusic)MusicRegistry.DragonsHoard, (SoundManager.MusicPriority)SoundManager.MusicPriority.EVENT, (float)1.5f);
        float mod = Math.abs((float)Math.pow(this.getBabylonTowerHealthPerc(), 0.5) - 1.0f);
        this.setSpeed(100.0f + mod * 60.0f);
    }

    public void serverTick() {
        super.serverTick();
        float mod = Math.abs((float)Math.pow(this.getBabylonTowerHealthPerc(), 0.5) - 1.0f);
        this.setSpeed(100.0f + mod * 60.0f);
    }

    public float getBabylonTowerHealthPerc() {
        BabylonTowerMob babylonTowerMob = this.getLevel().entityManager.mobs.stream().filter(m -> Objects.equals(m.getStringID(), "babylontower")).min(Comparator.comparingDouble(m -> m.getDistance((Mob)this))).orElse(null);
        if (babylonTowerMob == null) {
            return 1.0f;
        }
        return (float)babylonTowerMob.getHealth() / (float)babylonTowerMob.getMaxHealth();
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            MobDrawable shoulderDrawableShadow;
            MobDrawable shoulderDrawable;
            GameLight light = level.getLightLevel((Entity)this);
            int drawX = camera.getDrawX(this.x) - 112;
            int drawY = camera.getDrawY(this.y);
            float headAngle = GameMath.fixAngle((float)GameMath.getAngle((Point2D.Float)new Point2D.Float(this.dx, this.dy)));
            final MobDrawable headDrawable = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(texture, 0, 0, 224), null, (GameLight)light.minLevelCopy(100.0f), (int)((int)this.height), (float)headAngle, (int)drawX, (int)drawY, (int)130);
            MobDrawable headDrawableShadow = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(texture_shadow, 0, 0, 224), null, (GameLight)light, (int)((int)this.height), (float)headAngle, (int)drawX, (int)(drawY + 40), (int)130);
            new ComputedObjectValue(null, () -> 0.0);
            ComputedObjectValue shoulderLine = WormMobHead.moveDistance((GameLinkedList.Element)this.moveLines.getFirstElement(), (double)70.0);
            if (shoulderLine.object != null) {
                Point2D.Double shoulderPos = WormMobHead.linePos((ComputedObjectValue)shoulderLine);
                GameLight shoulderLight = level.getLightLevel((int)(shoulderPos.x / 32.0), (int)(shoulderPos.y / 32.0));
                int shoulderDrawX = camera.getDrawX((float)shoulderPos.x) - 112;
                int shoulderDrawY = camera.getDrawY((float)shoulderPos.y);
                float shoulderHeight = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)shoulderLine.object).object).movedDist + ((Double)shoulderLine.get()).floatValue());
                float shoulderAngle = GameMath.fixAngle((float)((float)GameMath.getAngle((Point2D.Double)new Point2D.Double((double)this.x - shoulderPos.x, (double)(this.y - this.height) - (shoulderPos.y - (double)shoulderHeight)))));
                shoulderDrawable = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(texture, 0, 1, 224), null, (GameLight)shoulderLight.minLevelCopy(100.0f), (int)((int)shoulderHeight), (float)shoulderAngle, (int)shoulderDrawX, (int)shoulderDrawY, (int)130);
                shoulderDrawableShadow = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(texture_shadow, 0, 1, 224), null, (GameLight)shoulderLight, (int)((int)shoulderHeight), (float)shoulderAngle, (int)shoulderDrawX, (int)(shoulderDrawY + 40), (int)130);
            } else {
                shoulderDrawable = null;
                shoulderDrawableShadow = null;
            }
            topList.add((Drawable)new MobDrawable(){

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
    }

    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 24;
        float headAngle = GameMath.fixAngle((float)GameMath.getAngle((Point2D.Float)new Point2D.Float(this.dx, this.dy)));
        icon.initDraw().sprite(0, 0, 152).rotate(headAngle - 90.0f, 24, 24).size(48, 48).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-15, -15, 30, 30);
    }

    public GameTooltips getMapTooltips() {
        return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue(BuffModifiers.SLOW, (Object)Float.valueOf(0.0f)).max((Object)Float.valueOf(0.0f)));
    }

    public static class BabylonHeadAI<T extends BabylonHead>
    extends SequenceAINode<T> {
        public BabylonHeadAI() {
            int i;
            this.addChild(new RemoveOnNoBabylonTowerNode());
            this.addChild((AINode)new TargetFinderAINode<T>(3200){

                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, (Point)base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            this.addChild((AINode)new IsolateRunningAINode((AINode)attackStages));
            for (i = 0; i < 6; ++i) {
                attackStages.addChild(new CirclingStage(600, 5000));
                attackStages.addChild(new ChargeTargetStage());
            }
            for (i = 0; i < 4; ++i) {
                attackStages.addChild(new CirclingStage(600, 500));
                attackStages.addChild(new ChargeTargetStage());
            }
        }
    }

    public static class CirclingStage<T extends BabylonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public long statTime;
        public int circlingRange;
        public int circlingTime;

        public CirclingStage(int circlingRange, int circlingTime) {
            this.circlingRange = circlingRange;
            this.circlingTime = circlingTime;
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.statTime = mob.getTime();
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            float circlingSpeed = MobMovementCircle.convertToRotSpeed((int)this.circlingRange, (float)((BabylonHead)this.mob()).getSpeed()) * 1.1f;
            Object movement = target != null ? new MobMovementCircleRelative(this.mob(), target, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean()) : new MobMovementCircleLevelPos(this.mob(), ((BabylonHead)this.mob()).x, ((BabylonHead)this.mob()).y, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean());
            this.getBlackboard().mover.setCustomMovement((AINode)this, (MobMovement)movement);
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            long endTime = this.statTime + (long)this.circlingTime;
            return mob.getTime() < endTime ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }
    }

    public static class ChargeTargetStage<T extends BabylonHead>
    extends AINode<T>
    implements AttackStageInterface<T> {
        public int startMoveAccuracy;
        public Mob chargingTarget;

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.startMoveAccuracy = ((BabylonHead)((Object)mob)).moveAccuracy;
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                ((BabylonHead)((Object)mob)).moveAccuracy = 5;
                this.chargingTarget = target;
                this.getBlackboard().mover.setCustomMovement((AINode)this, (MobMovement)new MobMovementRelative(target, 0.0f, 0.0f));
            }
            ((BabylonHead)((Object)mob)).buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, mob, 30.0f, null), true);
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
            ((BabylonHead)((Object)mob)).moveAccuracy = this.startMoveAccuracy;
            ((BabylonHead)((Object)mob)).buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
            this.chargingTarget = null;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (!((BabylonHead)((Object)mob)).buffManager.hasBuff(BuffRegistry.SPIDER_CHARGE)) {
                return AINodeResult.SUCCESS;
            }
            if (this.chargingTarget != null && !this.chargingTarget.removed()) {
                float distance = mob.getDistance(this.chargingTarget);
                if (distance < 100.0f) {
                    float currentAngle = GameMath.getAngle((Point2D.Float)new Point2D.Float(((BabylonHead)((Object)mob)).dx, ((BabylonHead)((Object)mob)).dy));
                    float targetAngle = GameMath.getAngle((Point2D.Float)new Point2D.Float(this.chargingTarget.x - ((BabylonHead)((Object)mob)).x, this.chargingTarget.y - ((BabylonHead)((Object)mob)).y));
                    float diff = GameMath.getAngleDifference((float)currentAngle, (float)targetAngle);
                    float maxAngle = 30.0f;
                    if (Math.abs(diff) >= maxAngle && distance > 75.0f || ((BabylonHead)((Object)mob)).dx == 0.0f && ((BabylonHead)((Object)mob)).dy == 0.0f) {
                        ((BabylonHead)((Object)mob)).moveAccuracy = this.startMoveAccuracy;
                        return AINodeResult.SUCCESS;
                    }
                }
                return AINodeResult.RUNNING;
            }
            return AINodeResult.SUCCESS;
        }
    }

    public static class RemoveOnNoBabylonTowerNode<T extends Mob>
    extends AINode<T> {
        protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (mob.getLevel().entityManager.mobs.stream().noneMatch(m -> Objects.equals(m.getStringID(), "babylontower"))) {
                mob.remove();
            }
            return AINodeResult.SUCCESS;
        }
    }
}

