/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class PacketVoidPhasingStaff
extends Packet {
    public final int mobUniqueID;
    public final float levelX;
    public final float levelY;
    public final boolean success;

    public PacketVoidPhasingStaff(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.levelX = reader.getNextFloat();
        this.levelY = reader.getNextFloat();
        this.success = reader.getNextBoolean();
    }

    public PacketVoidPhasingStaff(Mob mob, float levelX, float levelY, boolean success) {
        this.mobUniqueID = mob.getUniqueID();
        this.levelX = levelX;
        this.levelY = levelY;
        this.success = success;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(levelX);
        writer.putNextFloat(levelY);
        writer.putNextBoolean(success);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (target != null) {
            PacketVoidPhasingStaff.applyToMob(target.getLevel(), target, this.levelX, this.levelY, this.success);
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }

    public static void applyToMob(Level level, Mob mob, float levelX, float levelY, boolean success) {
        if (level == null) {
            return;
        }
        int lastPosX = mob.getX();
        int lastPosY = mob.getY();
        Point2D.Float dir = GameMath.normalize((float)lastPosX - levelX, (float)lastPosY - levelY);
        if (!success) {
            PacketVoidPhasingStaff.spawnFailedParticles(level, levelX, levelY, dir.x, dir.y);
            return;
        }
        mob.setPos(levelX, levelY, true);
        if (level.isClient()) {
            if ((float)lastPosX != levelX || (float)lastPosY != levelY) {
                PacketVoidPhasingStaff.spawnPoofParticles(level, mob, lastPosX, lastPosY, dir.x, dir.y, false);
                PacketVoidPhasingStaff.spawnBridgeParticles(level, lastPosX, lastPosY, levelX, levelY);
            }
            PacketVoidPhasingStaff.spawnPoofParticles(level, mob, mob.x, mob.y, -dir.x, -dir.y, true);
            SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(lastPosX, lastPosY).volume(0.5f));
            SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(mob).volume(0.5f));
            if (mob == level.getClient().getPlayer()) {
                level.getClient().sendMovementPacket(true);
            }
        }
    }

    public static void spawnPoofParticles(Level level, Mob tpMob, float x, float y, float dirX, float dirY, boolean extra) {
        int i;
        GameRandom random = GameRandom.globalRandom;
        for (i = 0; i < 11; ++i) {
            level.entityManager.addParticle(x + (float)random.nextGaussian() * 5.0f + tpMob.dx / 2.0f, y + (float)random.nextGaussian() * 10.0f + tpMob.dy / 2.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(tpMob.dx * random.getFloatBetween(0.8f, 1.2f) / 10.0f, tpMob.dy * random.getFloatBetween(0.8f, 1.2f) / 10.0f).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).sizeFades(25, 40).fadesAlpha(0.1f, 0.35f).color(new Color(223, 124, 255)).height(18.0f).lifeTime(700);
        }
        if (!extra) {
            return;
        }
        for (i = 0; i < 5; ++i) {
            float anglePerParticle = 72.0f;
            int angle = (int)((float)i * anglePerParticle * random.getFloatBetween(1.0f, 3.0f));
            float dx = (float)(Math.sin(Math.toRadians(angle)) + (double)dirX) * 8.0f;
            float dy = (float)(Math.cos(Math.toRadians(angle)) + (double)dirY) * 8.0f;
            level.entityManager.addParticle(x, y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(8, 16).ignoreLight(true).heightMoves(30.0f, 20.0f).movesFriction(dx * random.getFloatBetween(2.0f, 3.0f), dy * random.getFloatBetween(2.0f, 3.0f), 0.8f).lifeTime(800);
        }
    }

    public static void spawnBridgeParticles(Level level, float fromX, float fromY, float toX, float toY) {
        GameRandom random = new GameRandom();
        Point2D.Float normalizedDir = GameMath.normalize(toX - fromX, toY - fromY);
        float distance = GameMath.preciseDistance(fromX, fromY, toX, toY);
        float baseAngle = (float)Math.toDegrees(Math.atan2(normalizedDir.y, normalizedDir.x));
        int totalParticles = 16;
        for (int i = 0; i < totalParticles; ++i) {
            float maxSpeed = 35.0f * distance * 0.01f;
            float speed = random.getFloatBetween(0.0f, 15.0f) * distance * 0.01f;
            float spread = (1.0f - speed / maxSpeed) * 35.0f;
            float angleOffset = random.getFloatBetween(-spread / 2.0f, spread / 2.0f);
            float angle = baseAngle + angleOffset;
            float dx = (float)Math.cos(Math.toRadians(angle)) * speed;
            float dy = (float)Math.sin(Math.toRadians(angle)) * speed;
            level.entityManager.addParticle(fromX, fromY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).color(new Color(223, 124, 255)).sizeFades(12, 18).heightMoves(20.0f, 10.0f).movesFriction(dx, dy, 0.6f).lifeTime(700);
        }
    }

    public static void spawnFailedParticles(Level level, float x, float y, float dirX, float dirY) {
        GameRandom random = GameRandom.globalRandom;
        for (int i = 0; i < 5; ++i) {
            float anglePerParticle = 72.0f;
            int angle = (int)((float)i * anglePerParticle * random.getFloatBetween(1.0f, 3.0f));
            float dx = (float)(Math.sin(Math.toRadians(angle)) + (double)dirX) * 8.0f;
            float dy = (float)(Math.cos(Math.toRadians(angle)) + (double)dirY) * 8.0f;
            level.entityManager.addParticle(x, y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(8, 16).color(Color.RED).ignoreLight(true).heightMoves(16.0f, 10.0f).movesFriction(dx * random.getFloatBetween(2.0f, 3.0f), dy * random.getFloatBetween(2.0f, 3.0f), 0.8f).lifeTime(800);
        }
    }
}

