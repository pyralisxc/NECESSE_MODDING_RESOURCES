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
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class PacketForceOfWind
extends Packet {
    public final int mobUniqueID;
    public final float dirX;
    public final float dirY;
    public final float strength;

    public PacketForceOfWind(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.strength = reader.getNextFloat();
    }

    public PacketForceOfWind(Mob mob, float dirX, float dirY, float strength) {
        this.mobUniqueID = mob.getUniqueID();
        this.dirX = dirX;
        this.dirY = dirY;
        this.strength = strength;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(dirX);
        writer.putNextFloat(dirY);
        writer.putNextFloat(strength);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (target != null) {
            PacketForceOfWind.applyToMob(target.getLevel(), target, this.dirX, this.dirY, this.strength);
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

    public static boolean isOnCooldown(Mob mob) {
        return mob.buffManager.getStacks(BuffRegistry.Debuffs.DASH_COOLDOWN) >= mob.buffManager.getModifier(BuffModifiers.DASH_STACKS);
    }

    public static void addCooldownStack(Mob mob, float seconds, boolean sendUpdatePacket) {
        seconds = seconds * mob.buffManager.getModifier(BuffModifiers.DASH_COOLDOWN).floatValue() + mob.buffManager.getModifier(BuffModifiers.DASH_COOLDOWN_FLAT).floatValue();
        seconds = Math.max(seconds, 0.5f);
        float secondsLeft = 0.0f;
        ActiveBuff prev = mob.buffManager.getBuff(BuffRegistry.Debuffs.DASH_COOLDOWN);
        if (prev != null) {
            secondsLeft = (float)prev.getStackTimes().getLast().getDurationLeft() / 1000.0f;
        }
        ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.DASH_COOLDOWN, mob, secondsLeft + seconds, null);
        mob.buffManager.addBuff(ab, sendUpdatePacket);
    }

    public static void applyToMob(Level level, Mob mob, float dirX, float dirY, float strength) {
        float forceX = dirX * strength;
        float forceY = dirY * strength;
        if (Math.abs(mob.dx) < Math.abs(forceX)) {
            mob.dx = forceX;
        }
        if (Math.abs(mob.dy) < Math.abs(forceY)) {
            mob.dy = forceY;
        }
        if (level != null && level.isClient()) {
            for (int i = 0; i < 30; ++i) {
                level.entityManager.addParticle(mob.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 10.0f, mob.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 10.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f).color(new Color(200, 200, 220)).height(18.0f).lifeTime(700);
            }
        }
    }
}

