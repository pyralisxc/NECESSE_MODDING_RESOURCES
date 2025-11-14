/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class AscendedGauntletMob
extends FlyingBossMob {
    public final LevelMob<Mob> master = new LevelMob();
    public boolean leftHanded;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(800, 1200, 1500, 1700, 2000);
    protected int lifeTime = 0;
    protected int deathTime = 10000;
    protected SoundPlayer windSound;

    public AscendedGauntletMob() {
        super(2000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.isSummoned = true;
        this.dropsLoot = false;
        this.moveAccuracy = 10;
        this.accelerationMod = 0.5f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-20, -20, 40, 40);
        this.hitBox = new Rectangle(-30, -30, 60, 60);
        this.selectBox = new Rectangle(-50, -50, 100, 100);
        this.setKnockbackModifier(0.0f);
        this.setRegen(0.0f);
        this.setFriction(0.3f);
        this.setSpeed(600.0f);
        this.setArmor(40);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.leftHanded);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.leftHanded = reader.getNextBoolean();
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextShortUnsigned(this.moveAccuracy);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.moveAccuracy = reader.getNextShortUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.countStats = false;
        this.ai = new BehaviourTreeAI<AscendedGauntletMob>(this, new AscendedGauntletAI(), new FlyingAIMover());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.windSound == null || this.windSound.isDone()) {
            this.windSound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this).falloffDistance(400).volume(0.0f));
            if (this.windSound != null) {
                this.windSound.fadeIn(1.0f);
                this.windSound.effect.volume(0.2f);
            }
        }
        if (this.windSound != null) {
            this.windSound.refreshLooping(1.0f);
        }
        int colorRandomizer = GameRandom.globalRandom.getIntBetween(0, 30);
        this.getLevel().entityManager.addParticle(this.x, this.y - 20.0f, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + colorRandomizer, 120 + colorRandomizer, 110 + colorRandomizer)).sizeFades(50, 75).height(-20.0f).movesConstantAngle(GameRandom.globalRandom.getIntBetween(0, 360), 15.0f).lifeTime(500);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= this.deathTime) {
            this.remove(0.0f, 0.0f, null, true);
        }
        this.tickMaster();
    }

    @Override
    public void requestServerUpdate() {
    }

    public void tickMaster() {
        if (this.removed()) {
            return;
        }
        if (this.master.get(this.getLevel()) == null) {
            this.remove();
        }
    }

    @Override
    public int stoppingDistance(float friction, float currentSpeed) {
        return 0;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return AscendedWizardMob.sunGauntletDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 350;
    }

    @Override
    protected SoundSettings getHitSound() {
        return null;
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.fadedeath1);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameRandom random = GameRandom.globalRandom;
        float anglePerParticle = 36.0f;
        for (int i = 0; i < 10; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
            this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(10, 20).ignoreLight(true).heightMoves(0.0f, -30.0f).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).lifeTime(1500);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        float rotate = (float)Math.toDegrees(Math.atan2(this.dy, this.dx)) - 90.0f;
        int timePerFrame = 100;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame % 4L);
        final TextureDrawOptionsEnd gauntletOptions = MobRegistry.Textures.ascendedGauntlet.initDraw().sprite(0, 0, 64, 96).size(64, 96).mirror(this.leftHanded, false).rotate(rotate).pos(drawX - 32, drawY -= this.getFlyingHeight());
        final TextureDrawOptionsEnd jetOptions = MobRegistry.Textures.ascendedGauntletJet.initDraw().sprite(spriteIndex % 2, 0, 38, 81).size(38, 81).rotate(rotate, 19, 81).posMiddle(drawX, drawY);
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                jetOptions.draw();
                gauntletOptions.draw();
            }
        });
    }

    @Override
    public Mob getAttackOwner() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master;
        }
        return super.getAttackOwner();
    }

    @Override
    public GameMessage getAttackerName() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getAttackerName();
        }
        return super.getAttackerName();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getDeathMessages();
        }
        return super.getDeathMessages();
    }

    public static class AscendedGauntletAI
    extends SequenceAINode<AscendedGauntletMob> {
        public AscendedGauntletAI() {
            TargetFinderAINode<AscendedGauntletMob> targetFinder = new TargetFinderAINode<AscendedGauntletMob>(1600){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(AscendedGauntletMob mob, Point base, TargetFinderDistance<AscendedGauntletMob> distance) {
                    return 1.streamPlayers(mob, base, distance);
                }
            };
            this.addChild(targetFinder);
            targetFinder.loseTargetMinCooldown = 1000;
            targetFinder.loseTargetMaxCooldown = 4000;
            this.addChild(new AINode<AscendedGauntletMob>(){

                @Override
                protected void onRootSet(AINode<AscendedGauntletMob> root, AscendedGauntletMob mob, Blackboard<AscendedGauntletMob> blackboard) {
                }

                @Override
                public void init(AscendedGauntletMob mob, Blackboard<AscendedGauntletMob> blackboard) {
                }

                @Override
                public AINodeResult tick(AscendedGauntletMob mob, Blackboard<AscendedGauntletMob> blackboard) {
                    Mob target = blackboard.getObject(Mob.class, "currentTarget");
                    if (target != null) {
                        blackboard.mover.setMobTarget(this, target);
                    }
                    return AINodeResult.SUCCESS;
                }
            });
        }
    }
}

