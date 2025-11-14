/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class TeleportEvent
extends LevelEvent {
    private int x;
    private int y;
    private Mob mobTarget;
    private int mobUniqueID;
    private ServerClient clientTarget;
    private LevelIdentifier targetLevelIdentifier;
    private int delay;
    private float sicknessTime;
    private long teleportTime;
    private Function<LevelIdentifier, Level> destinationGenerator;
    private Function<Level, TeleportResult> destinationCheck;

    public TeleportEvent() {
    }

    public TeleportEvent(int x, int y, int mobUniqueID) {
        this.x = x;
        this.y = y;
        this.mobUniqueID = mobUniqueID;
    }

    public TeleportEvent(Mob target, int delay, LevelIdentifier targetLevelIdentifier, float sicknessTime, Function<LevelIdentifier, Level> destinationGenerator, Function<Level, TeleportResult> destinationCheck) {
        this(target.getX(), target.getY(), target.getUniqueID());
        this.mobTarget = target;
        this.delay = delay;
        this.targetLevelIdentifier = targetLevelIdentifier;
        this.sicknessTime = sicknessTime;
        this.destinationGenerator = destinationGenerator;
        this.destinationCheck = destinationCheck;
    }

    public TeleportEvent(ServerClient target, int delay, LevelIdentifier targetLevelIdentifier, float sicknessTime, Function<LevelIdentifier, Level> destinationGenerator, Function<Level, TeleportResult> destinationCheck) {
        this(target.playerMob, delay, targetLevelIdentifier, sicknessTime, destinationGenerator, destinationCheck);
        this.clientTarget = target;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
        writer.putNextInt(this.mobUniqueID);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            boolean isMe = this.mobUniqueID == this.level.getClient().getSlot();
            for (int i = 0; i < 10; ++i) {
                Particle.GType type = isMe ? null : (i <= 3 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC);
                this.level.entityManager.addParticle(this.x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), this.y, type).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 5.0f, (float)GameRandom.globalRandom.nextGaussian() * 5.0f).color(new Color(255, 245, 198)).height(GameRandom.globalRandom.nextInt(40)).givesLight(50.0f, 0.5f).lifeTime(600);
            }
            if (isMe) {
                SoundManager.playSound(GameResources.teleport, SoundEffect.globalEffect().volume(0.7f));
            } else {
                SoundManager.playSound(GameResources.teleport, (SoundEffect)SoundEffect.effect(this.x, this.y));
            }
            this.over();
        } else if (this.isServer()) {
            if (this.mobTarget == null && this.clientTarget == null) {
                this.over();
            } else {
                if (this.mobTarget != null && this.mobTarget.isPlayer && this.clientTarget == null) {
                    System.err.println("Cannot teleport player without knowing client");
                    this.over();
                    return;
                }
                if (this.delay <= 0) {
                    this.performTeleport();
                } else {
                    this.teleportTime = this.level.getWorldEntity().getTime() + (long)this.delay;
                }
            }
        } else {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.level.getWorldEntity().getTime() >= this.teleportTime) {
            this.performTeleport();
        }
    }

    private void performTeleport() {
        if (this.isServer()) {
            if (this.clientTarget != null) {
                AtomicBoolean success = new AtomicBoolean();
                this.clientTarget.changeLevelCheck(this.targetLevelIdentifier, this.destinationGenerator, level -> {
                    boolean isValid = this.destinationCheck == null;
                    LevelIdentifier levelIdentifier = null;
                    Point pos = null;
                    if (this.destinationCheck != null) {
                        TeleportResult result = this.destinationCheck.apply((Level)level);
                        isValid = result.isValid;
                        levelIdentifier = result.newDestination;
                        pos = result.targetPosition;
                    }
                    if (isValid) {
                        this.sendTeleportEvent(this.mobTarget);
                        success.set(true);
                    }
                    return new TeleportResult(isValid, levelIdentifier, pos);
                }, true);
                if (success.get()) {
                    this.sendTeleportEvent(this.mobTarget);
                }
            } else {
                Level newLevel = this.level.getServer().world.getLevel(this.targetLevelIdentifier, this.destinationGenerator == null ? null : () -> this.destinationGenerator.apply(this.targetLevelIdentifier));
                boolean isValid = this.destinationCheck == null;
                Point targetPos = null;
                if (this.destinationCheck != null) {
                    TeleportResult result = this.destinationCheck.apply(newLevel);
                    isValid = result.isValid;
                    targetPos = result.targetPosition;
                }
                if (isValid) {
                    this.sendTeleportEvent(this.mobTarget);
                    if (newLevel.isSamePlace(this.mobTarget.getLevel())) {
                        if (targetPos != null) {
                            this.mobTarget.setPos(targetPos.x, targetPos.y, true);
                            this.mobTarget.sendMovementPacket(true);
                        }
                    } else if (targetPos != null) {
                        this.level.entityManager.changeMobLevel(this.mobTarget, newLevel, targetPos.x, targetPos.y, true);
                    } else {
                        this.level.entityManager.changeMobLevel(this.mobTarget, newLevel, this.mobTarget.getX(), this.mobTarget.getY(), true);
                    }
                    this.sendTeleportEvent(this.mobTarget);
                }
            }
            if (this.sicknessTime > 0.0f) {
                this.mobTarget.addBuff(new ActiveBuff("teleportsickness", this.mobTarget, this.sicknessTime, null), true);
            }
        }
        this.over();
    }

    protected void sendTeleportEvent(Mob mob) {
        TeleportEvent e = new TeleportEvent(mob.getX(), mob.getY() + 5, mob.getUniqueID());
        mob.getLevel().getServer().network.sendToClientsWithTile(new PacketLevelEvent(e), mob.getLevel(), mob.getTileX(), mob.getTileY());
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.y)));
    }
}

