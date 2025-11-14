/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.tween.Easings;
import necesse.engine.util.tween.FloatTween;
import necesse.engine.util.tween.Playable;
import necesse.engine.util.tween.PlayableSequence;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ConfusedWandererAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MosquitoMob
extends HostileMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage collisionDamage = new GameDamage(65.0f);
    public final int searchDistance = 320;
    protected final PlayableSequence landSequence = new PlayableSequence();
    protected final PlayableSequence flySequence = new PlayableSequence();
    protected final EmptyMobAbility landAbility;
    protected final EmptyMobAbility flyAbility;
    protected final FloatTween greenTween = (FloatTween)new FloatTween(1000.0, 1.0f, 0.0f).setEase(Easings.ExpoOut);
    protected final int animationFPS = 11;
    protected final int randomAnimationOffset;
    protected final float flySpeed = 50.0f;
    protected final FloatTween flySpeedTween = (FloatTween)new FloatTween(50.0f).onValueChanged(this::setSpeed);
    public float healOnAttackPercent = 0.15f;
    public float attackDistance = 10.0f;
    protected boolean isFlying = true;
    protected final FloatTween flyOffsetTween = new FloatTween(-16.0f);
    protected float attackAngle;
    protected long spawnTime;
    protected boolean shouldPlaySound = false;
    protected boolean allowedToPlaySound = false;
    protected SoundPlayer buzzSoundPlayer;

    public MosquitoMob() {
        super(300);
        this.setSpeed(50.0f);
        this.setArmor(25);
        this.setFriction(2.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-9, -7, 18, 14);
        this.selectBox = new Rectangle(-20, -34, 40, 32);
        this.hitBox = new Rectangle(-13, -16, 26, 30);
        this.randomAnimationOffset = GameRandom.globalRandom.nextInt(900);
        double landTime = 500.0;
        ((PlayableSequence)((PlayableSequence)this.landSequence.addAt(0.0, this.flyOffsetTween.newTween(landTime, Float.valueOf(0.0f))).addAtTheSameTime(this.flySpeedTween.newTween(landTime, Float.valueOf(0.0f))).onComplete(() -> {
            this.isFlying = false;
            this.shouldPlaySound = false;
        })).onPlay(this.flySequence::kill)).setEase(Easings.SineOut);
        this.flySequence.addAt(0.0, (Playable<?>)this.flyOffsetTween.newTween(landTime, Float.valueOf(-16.0f)).setEase(Easings.SineOut)).addAtTheSameTime((Playable<?>)this.flySpeedTween.newTween(landTime, Float.valueOf(50.0f)).setEase(Easings.QuadIn)).onPlay(() -> {
            this.landSequence.kill();
            this.isFlying = true;
            this.shouldPlaySound = true;
        });
        this.landAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                MosquitoMob.this.landSequence.play(MosquitoMob.this.getTime(), MosquitoMob.this.getTime());
            }
        });
        this.flyAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                MosquitoMob.this.flySequence.play(MosquitoMob.this.getTime(), MosquitoMob.this.getTime());
            }
        });
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("isFlying", this.isFlying);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.isFlying = save.getBoolean("isFlying", true);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.spawnTime);
        writer.putNextBoolean(this.isFlying);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnTime = reader.getNextLong();
        this.isFlying = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.spawnTime == 0L ? this.getTime() : this.spawnTime;
        this.attackAngle = this.attackAngle == 0.0f ? GameRandom.globalRandom.nextFloat() * 360.0f : this.attackAngle;
        this.ai = new BehaviourTreeAI<MosquitoMob>(this, new MosquitoMobAI<MosquitoMob>(this), new AIMover());
        this.greenTween.play(this.spawnTime, this.getTime());
        if (this.isFlying) {
            this.shouldPlaySound = true;
        } else {
            this.greenTween.complete(true);
            this.landSequence.play(this.getTime(), this.getTime());
            this.landSequence.complete(true);
        }
    }

    protected void playBuzzSound() {
        if (this.buzzSoundPlayer == null || this.buzzSoundPlayer.isDone()) {
            this.buzzSoundPlayer = SoundManager.playSound(GameResources.mosquitoBuzz, (SoundEffect)SoundEffect.effect(this).volume(GameRandom.globalRandom.getFloatBetween(0.8f, 1.0f)).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
            if (this.buzzSoundPlayer != null) {
                this.buzzSoundPlayer.setPosition(GameRandom.globalRandom.getFloatBetween(0.0f, GameResources.mosquitoBuzz.getLengthInSeconds()));
                this.buzzSoundPlayer.refreshLooping(1.0f);
            }
        } else {
            this.buzzSoundPlayer.refreshLooping(1.0f);
        }
    }

    @Override
    public void clientTick() {
        PlayerMob player;
        super.clientTick();
        this.landSequence.update(this.getTime());
        this.flySequence.update(this.getTime());
        if (this.isClient() && (player = this.getClient().getPlayer()) != null) {
            boolean bl = this.allowedToPlaySound = GameMath.squareDistance(player.x, player.y, this.x, this.y) < 800.0f;
        }
        if (this.shouldPlaySound && this.allowedToPlaySound) {
            this.playBuzzSound();
        } else {
            this.buzzSoundPlayer = null;
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.landSequence.update(this.getTime());
        this.flySequence.update(this.getTime());
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(MosquitoMob.getTileCoordinate(x), MosquitoMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = (int)((float)(camera.getDrawY(y) - 32) + ((Float)this.flyOffsetTween.getValue()).floatValue());
        int spriteCount = 4;
        int sprite = (int)((double)(this.getTime() + (long)this.randomAnimationOffset) / 1000.0 * 11.0) % spriteCount;
        if (!this.isFlying) {
            sprite = -2;
            drawY -= 15;
        }
        float rotate = this.dx / 10.0f;
        this.greenTween.update(this.getTime());
        Color color = new Color((int)(255.0f * (1.0f - ((Float)this.greenTween.getValue()).floatValue())), 255, (int)(255.0f * (1.0f - ((Float)this.greenTween.getValue()).floatValue())));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.mosquito.initDraw().sprite(sprite + 2, this.getDir(), 64).color(color).light(light).rotate(rotate, 32, 32).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        int spriteCount = 4;
        int sprite = (int)((double)(this.getTime() + (long)this.randomAnimationOffset) / 1000.0 * 11.0) % spriteCount;
        if (!this.isFlying) {
            sprite = -2;
        }
        GameTexture shadowTexture = MobRegistry.Textures.mosquito_shadow;
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 32 - 22;
        return shadowTexture.initDraw().sprite(sprite + 2, this.getDir(), 64).light(light).pos(drawX, drawY);
    }

    @Override
    public boolean canPushMob(Mob other) {
        return other instanceof MosquitoMob;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.mosquito, i, 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return DeathMessageTable.fromRange("mosquito", 3);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    static class MosquitoMobAI<T extends MosquitoMob>
    extends SelectorAINode<T> {
        public MosquitoMobAI(T mob) {
            final LandAINode landAINode = new LandAINode(6000);
            ConfusedWandererAINode confusedWandererAINode = new ConfusedWandererAINode();
            this.addChild(confusedWandererAINode);
            confusedWandererAINode.hitsCausesConfusionChance = 0.25f;
            SequenceAINode chaserSequence = new SequenceAINode();
            this.addChild(chaserSequence);
            chaserSequence.addChild(new SucceederAINode(new LooseTargetTimerAINode()));
            Objects.requireNonNull(mob);
            TargetFinderAINode targetFinderAINode = new TargetFinderAINode<T>(320){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
                }
            };
            targetFinderAINode.noTargetFoundMinCooldown = 500;
            targetFinderAINode.noTargetFoundMaxCooldown = 1000;
            chaserSequence.addChild(targetFinderAINode);
            chaserSequence.addChild(new FlyToTargetThenToDirectionAINode(((MosquitoMob)mob).attackCooldown));
            chaserSequence.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    landAINode.resetTimeToLand();
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(landAINode);
            this.addChild(new WandererAINode(5000));
        }

        static class LandAINode<T extends MosquitoMob>
        extends AINode<T> {
            protected final int timeToLand;
            protected float timeToNextLand;
            protected boolean hasLanded;

            public LandAINode(int timeToLand) {
                this.timeToLand = timeToLand;
            }

            public void resetTimeToLand() {
                this.timeToNextLand = (float)((MosquitoMob)this.mob()).getTime() + GameRandom.globalRandom.getFloatBetween((float)this.timeToLand * 0.5f, (float)this.timeToLand * 1.5f);
                if (this.hasLanded) {
                    ((MosquitoMob)this.mob()).flyAbility.runAndSend();
                }
                this.hasLanded = false;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                if (!((MosquitoMob)this.mob()).isServer()) {
                    return;
                }
                if (!((MosquitoMob)mob).isFlying) {
                    this.hasLanded = true;
                } else {
                    this.resetTimeToLand();
                }
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.hasLanded) {
                    return AINodeResult.SUCCESS;
                }
                if ((float)mob.getTime() >= this.timeToNextLand && !this.hasLanded) {
                    this.hasLanded = true;
                    ((MosquitoMob)mob).landAbility.runAndSend();
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.FAILURE;
            }
        }

        static class FlyToTargetThenToDirectionAINode<T extends MosquitoMob>
        extends MoveTaskAINode<T> {
            public final MobHitCooldowns hitCooldowns;
            public long nextMoveUpdateTime = 0L;
            public Mob lastTarget;

            public FlyToTargetThenToDirectionAINode(int attackCooldown) {
                this.hitCooldowns = new MobHitCooldowns(attackCooldown);
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                float distance;
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                if (target != null && (distance = ((Mob)mob).getDistance(target)) < 32.0f && this.hitCooldowns.canHit(target) && target.canBeHit((Attacker)mob) && ((Mob)mob).getCollision().intersects(target.getCollision())) {
                    target.isServerHit(collisionDamage, target.x - ((MosquitoMob)mob).x, target.y - ((MosquitoMob)mob).y, 0.0f, (Attacker)mob);
                    ((Mob)mob).setHealth((int)((float)((Mob)mob).getHealth() * (1.0f + ((MosquitoMob)mob).healOnAttackPercent)));
                    this.hitCooldowns.startCooldown(target);
                    if (GameRandom.globalRandom.getChance((0.4f - (float)((Mob)mob).getHealth() / (float)((Mob)mob).getMaxHealth()) / 2.0f)) {
                        blackboard.submitEvent("confuseWander", new ConfuseWanderAIEvent(GameRandom.globalRandom.getIntBetween(3000, 6000), GameMath.getAngleDir(((MosquitoMob)mob).attackAngle - 180.0f)));
                    }
                }
                return super.tick(mob, blackboard);
            }

            @Override
            public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                if (target != this.lastTarget) {
                    this.nextMoveUpdateTime = 0L;
                    this.lastTarget = target;
                }
                if (mob.getTime() >= this.nextMoveUpdateTime && target != null) {
                    boolean shouldUsePathFinding = true;
                    float distance = ((Mob)mob).getDistance(target);
                    Point2D.Float angleDir = GameMath.getAngleDir(((MosquitoMob)mob).attackAngle);
                    Point2D.Float desiredPosition = new Point2D.Float(target.x + angleDir.x * ((MosquitoMob)mob).attackDistance, target.y + angleDir.y * ((MosquitoMob)mob).attackDistance);
                    if (distance < 100.0f) {
                        MovedRectangle movedRectangle = new MovedRectangle((Mob)mob, (int)desiredPosition.x, (int)desiredPosition.y);
                        if (!((Entity)mob).getLevel().collides((Shape)movedRectangle, ((Mob)mob).modifyChasingCollisionFilter(((Mob)mob).getLevelCollisionFilter(), target))) {
                            blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, angleDir.x * ((MosquitoMob)mob).attackDistance, angleDir.y * ((MosquitoMob)mob).attackDistance));
                            this.nextMoveUpdateTime = mob.getTime() + 1000L;
                            shouldUsePathFinding = false;
                        }
                    }
                    if (shouldUsePathFinding) {
                        return this.moveToTileTask(target.getTileX(), target.getTileY(), null, aiPathResult -> {
                            if (aiPathResult.moveIfWithin(-1, -1, () -> {
                                this.nextMoveUpdateTime = 0L;
                            })) {
                                int pathTime = aiPathResult.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 500, 0.1f);
                                this.nextMoveUpdateTime = mob.getTime() + (long)pathTime;
                                return AINodeResult.SUCCESS;
                            }
                            this.nextMoveUpdateTime = mob.getTime() + (long)GameRandom.globalRandom.getIntBetween(5000, 8000);
                            return AINodeResult.FAILURE;
                        });
                    }
                }
                if (blackboard.mover.isCurrentlyMovingFor(this)) {
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.FAILURE;
            }
        }
    }
}

