/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.MinecartLinePos;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.entity.mobs.summon.SummonedMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SawBladeMob
extends SummonedMob {
    public static GameDamage damage = new GameDamage(25.0f, 20.0f, 0.0f, 2.0f, 1.0f);
    public float sawSpeed;
    public float topSpeed = 100.0f;
    private float particleBuffer;
    public int sawDir;
    long startTime = 0L;
    long startupTime = 250L;
    long moveStartTime = 0L;
    long lifeTime = 10000L;
    private SoundPlayer movingSound;
    private SoundPlayer collisionSound;
    private SoundPlayer deathSound;
    boolean isAtEndOfTrack = false;

    public SawBladeMob() {
        super(1);
        this.isSummoned = true;
        this.setSpeed(250.0f);
        this.setFriction(0.0f);
        this.accelerationMod = 0.2f;
        this.setKnockbackModifier(0.1f);
        this.collision = new Rectangle(-10, -10, 20, 14);
        this.hitBox = new Rectangle(-14, -15, 28, 24);
        this.selectBox = new Rectangle(-14, -20, 28, 30);
        this.overrideMountedWaterWalking = true;
        this.staySmoothSnapped = true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("sawDir", this.sawDir);
        save.addFloat("sawSpeed", this.sawSpeed);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.sawDir = save.getInt("sawDir", this.sawDir);
        this.sawSpeed = save.getFloat("sawSpeed", this.sawSpeed);
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextFloat(this.sawSpeed);
        writer.putNextMaxValue(this.sawDir, 3);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.sawSpeed = reader.getNextFloat();
        this.sawDir = reader.getNextMaxValue(3);
    }

    @Override
    public void tickCurrentMovement(float delta) {
        super.tickCurrentMovement(delta);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
    }

    @Override
    protected void tickCollisionMovement(float delta, Mob rider) {
        int tileX = this.getTileX();
        int tileY = this.getTileY();
        GameObject object = this.getLevel().getObject(tileX, tileY);
        if (object instanceof TrapTrackObject) {
            TrapTrackObject trackObject = (TrapTrackObject)object;
            float colDx = this.colDx / 20.0f;
            float colDy = this.colDy / 20.0f;
            float moveX = this.moveX;
            float moveY = this.moveY;
            MinecartLines lines = trackObject.getMinecartLines(this.getLevel(), tileX, tileY, moveX, moveY, false);
            MinecartLinePos pos = lines.getMinecartPos(this.x, this.y, this.sawDir);
            if (pos != null) {
                float moving = 0.0f;
                switch (this.sawDir) {
                    case 0: {
                        if (moveY < 0.0f) {
                            moving = 1.0f;
                        }
                        moving -= colDy;
                        colDx = 0.0f;
                        break;
                    }
                    case 1: {
                        if (moveX > 0.0f) {
                            moving = 1.0f;
                        }
                        moving += colDx;
                        colDy = 0.0f;
                        break;
                    }
                    case 2: {
                        if (moveY > 0.0f) {
                            moving = 1.0f;
                        }
                        moving += colDy;
                        colDx = 0.0f;
                        break;
                    }
                    default: {
                        if (moveX < 0.0f) {
                            moving = 1.0f;
                        }
                        moving -= colDx;
                        colDy = 0.0f;
                    }
                }
                if (colDx != 0.0f || colDy != 0.0f) {
                    this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
                }
                float accMod = this.getAccelerationModifier();
                float speed = this.getSpeed();
                if (this.sawSpeed < this.topSpeed && this.moveStartTime > 0L) {
                    this.sawSpeed += (-(speed * moving) + this.sawSpeed) * delta / 250.0f * accMod;
                }
                if (this.sawSpeed < 0.0f) {
                    this.sawDir = (this.sawDir + 2) % 4;
                    this.sawSpeed = 0.0f;
                }
                if (moving == 0.0f && Math.abs(this.sawSpeed) < speed / 40.0f) {
                    this.sawSpeed = 0.0f;
                }
                if (this.startTime == 0L) {
                    this.startTime = this.getWorldEntity().getTime();
                }
                if (this.moveStartTime == 0L && this.getWorldEntity().getTime() - this.startTime > this.startupTime) {
                    this.moveStartTime = this.getWorldEntity().getTime();
                    this.sawSpeed = 30.0f;
                }
                if (this.sawSpeed > 0.0f) {
                    if (this.moveStartTime > 0L) {
                        this.particleBuffer += delta;
                        this.drawTrailParticles();
                    }
                    MinecartLinePos resultPos = pos.progressLines(this.sawDir, this.sawSpeed * delta / 250.0f, null);
                    this.x = resultPos.x;
                    this.y = resultPos.y;
                    this.sawDir = resultPos.dir;
                    this.setDir(this.sawDir);
                    int dir = this.getDir();
                    float f = dir == 1 || dir == 3 ? this.sawSpeed * (float)(dir == 1 ? 1 : -1) : (this.dx = 0.0f);
                    float f2 = dir == 0 || dir == 2 ? this.sawSpeed * (float)(dir == 2 ? 1 : -1) : (this.dy = 0.0f);
                    if (resultPos.distanceRemainingToTravel > 0.0f) {
                        this.isAtEndOfTrack = true;
                        this.sawSpeed = 0.0f;
                    } else if (!this.isServer()) {
                        if (this.movingSound == null || this.movingSound.isDone()) {
                            this.movingSound = SoundManager.playSound(GameResources.train, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(0.0f));
                        }
                        if (this.movingSound != null) {
                            this.movingSound.effect.volume(Math.min(this.sawSpeed / 200.0f, 1.0f) / 1.5f);
                            this.movingSound.refreshLooping(0.2f);
                        }
                    }
                } else {
                    this.moveStartTime = 0L;
                    this.x = pos.x;
                    this.y = pos.y;
                    if (pos.dir == 1 || pos.dir == 3) {
                        if (this.sawDir == 0 || this.sawDir == 2) {
                            this.sawDir = pos.dir;
                        }
                    } else if (this.sawDir == 1 || this.sawDir == 3) {
                        this.sawDir = pos.dir;
                    }
                }
            }
        } else {
            this.sawDir = this.getDir();
            this.dx = 0.0f;
            this.dy = 0.0f;
            if (this.colDx != 0.0f || this.colDy != 0.0f) {
                this.movementUpdateTime = Math.min(this.movementUpdateTime, this.getWorldEntity().getTime() - (long)(this.movementUpdateCooldown - 1000));
            }
            super.tickCollisionMovement(delta, rider);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.startTime == 0L) {
            this.startTime = this.getWorldEntity().getTime();
        } else if (this.startTime + this.lifeTime < this.getWorldEntity().getTime()) {
            this.isAtEndOfTrack = true;
        }
        if (this.isAtEndOfTrack) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.getLevel().isTrialRoom) {
            return new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() / 4.0f);
        }
        return damage;
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "sawtrapname");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("sawtrap", 2);
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        return false;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public void playHurtSound() {
        if (this.collisionSound == null) {
            this.collisionSound = SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(0.0f));
        }
        if (this.collisionSound != null) {
            this.collisionSound.effect.volume(Math.min(this.sawSpeed / 200.0f, 1.0f) / 1.5f);
        }
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
        if (this.deathSound == null) {
            this.deathSound = SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(0.0f));
        }
        if (this.deathSound != null) {
            this.deathSound.effect.volume(1.0f);
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        this.playDeathSound();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sawblade, i, 1, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    private long getTimeSinceStartedMoving() {
        return this.getWorldEntity().getTime() - this.moveStartTime;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    private void drawTrailParticles() {
        if (this.particleBuffer > 50.0f) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 3.0f, this.y - 4.0f + GameRandom.globalRandom.floatGaussian() * 3.0f, Particle.GType.IMPORTANT_COSMETIC).color(new Color(235, 193, 49)).height(0.0f).lifeTime(500);
            this.particleBuffer -= 50.0f;
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd spinOptions;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SawBladeMob.getTileCoordinate(x), SawBladeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 24;
        drawY += level.getTile(SawBladeMob.getTileCoordinate(x), SawBladeMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.moveStartTime > 0L) {
            int timePerFrame = 75;
            if (this.getTimeSinceStartedMoving() < (long)timePerFrame) {
                spinOptions = MobRegistry.Textures.sawblade.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
            } else if (this.getTimeSinceStartedMoving() < (long)(timePerFrame * 2)) {
                spinOptions = MobRegistry.Textures.sawblade.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY);
            } else {
                int frameIndex = (int)(this.getTimeSinceStartedMoving() / (long)timePerFrame) % 2;
                spinOptions = MobRegistry.Textures.sawblade.initDraw().sprite(2 + frameIndex, 0, 32).light(light).pos(drawX, drawY);
            }
        } else {
            spinOptions = MobRegistry.Textures.sawblade.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        }
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                spinOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }
}

