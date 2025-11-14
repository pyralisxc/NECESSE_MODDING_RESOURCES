/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class PacketBlinkScepter
extends Packet {
    public final int mobUniqueID;
    public final float dirX;
    public final float dirY;
    public final float range;

    public PacketBlinkScepter(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.range = reader.getNextFloat();
    }

    public PacketBlinkScepter(Mob mob, float dirX, float dirY, float range) {
        this.mobUniqueID = mob.getUniqueID();
        this.dirX = dirX;
        this.dirY = dirY;
        this.range = range;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(dirX);
        writer.putNextFloat(dirY);
        writer.putNextFloat(range);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (target != null) {
            PacketBlinkScepter.applyToMob(target.getLevel(), target, this.dirX, this.dirY, this.range);
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }

    public static Point2D.Float getMobDir(Mob mob) {
        if (mob.moveX != 0.0f || mob.moveY != 0.0f) {
            return GameMath.normalize(mob.moveX, mob.moveY);
        }
        int dir = mob.getDir();
        if (dir == 0) {
            return new Point2D.Float(0.0f, -1.0f);
        }
        if (dir == 1) {
            return new Point2D.Float(1.0f, 0.0f);
        }
        if (dir == 2) {
            return new Point2D.Float(0.0f, 1.0f);
        }
        if (dir == 3) {
            return new Point2D.Float(-1.0f, 0.0f);
        }
        return new Point2D.Float(0.0f, 0.0f);
    }

    public static void applyToMob(Level level, Mob mob, float dirX, float dirY, float range) {
        int newPosY;
        int newPosX;
        int lastPosY;
        int lastPosX;
        block5: {
            if (level == null) {
                return;
            }
            lastPosX = mob.getX();
            lastPosY = mob.getY();
            do {
                newPosX = mob.getX() + (int)(dirX * range);
                newPosY = mob.getY() + (int)(dirY * range);
                MovedRectangle moveRect = new MovedRectangle(mob, mob.getX(), mob.getY(), newPosX, newPosY);
                if (!level.collides((Shape)moveRect, mob.getLevelCollisionFilter())) break block5;
            } while (!((range -= 4.0f) <= 0.0f));
            newPosX = mob.getX();
            newPosY = mob.getY();
        }
        mob.setPos(newPosX, newPosY, true);
        if (level.isClient()) {
            if (lastPosX != newPosX || lastPosY != newPosY) {
                PacketBlinkScepter.spawnPoofParticles(level, mob, lastPosX, lastPosY);
            }
            PacketBlinkScepter.spawnPoofParticles(level, mob, mob.x, mob.y);
            if (mob == level.getClient().getPlayer()) {
                level.getClient().sendMovementPacket(true);
            }
        }
    }

    protected static void spawnPoofParticles(Level level, Mob tpMob, float x, float y) {
        for (int i = 0; i < 11; ++i) {
            level.entityManager.addParticle(x + (float)GameRandom.globalRandom.nextGaussian() * 5.0f + tpMob.dx / 2.0f, y + (float)GameRandom.globalRandom.nextGaussian() * 10.0f + tpMob.dy / 2.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(tpMob.dx * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f, tpMob.dy * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(25, 40).fadesAlpha(0.1f, 0.35f).color(new Color(236, 221, 197)).height(18.0f).lifeTime(700);
        }
    }
}

