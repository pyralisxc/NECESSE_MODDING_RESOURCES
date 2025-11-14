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
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class PacketGhostBoots
extends Packet {
    public final int slot;
    public final float dirX;
    public final float dirY;
    public final float strength;

    public PacketGhostBoots(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.strength = reader.getNextFloat();
    }

    public PacketGhostBoots(int slot, float dirX, float dirY, float strength) {
        this.slot = slot;
        this.dirX = dirX;
        this.dirY = dirY;
        this.strength = strength;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextFloat(dirX);
        writer.putNextFloat(dirY);
        writer.putNextFloat(strength);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ClientClient target = client.getClient(this.slot);
        if (target != null && target.isSamePlace(client.getLevel())) {
            PacketGhostBoots.applyToPlayer(target.playerMob.getLevel(), target.playerMob, this.dirX, this.dirY, this.strength);
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
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

    public static void applyToPlayer(Level level, Mob mob, float dirX, float dirY, float strength) {
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
                level.entityManager.addParticle(mob.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 10.0f, mob.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 10.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f).color(new Color(41, 41, 43)).height(18.0f).lifeTime(1000);
            }
        }
    }
}

