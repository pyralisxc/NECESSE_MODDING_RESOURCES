/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.packet.PacketRequestMobData
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.Mob
 *  necesse.entity.particle.Particle$GType
 *  necesse.level.maps.Level
 */
package aphorea.packets;

import java.awt.Color;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class AphCustomPushPacket
extends Packet {
    public final int mobUniqueID;
    public final float dirX;
    public final float dirY;
    public final float strength;
    public final Color color;

    public AphCustomPushPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.mobUniqueID = reader.getNextInt();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.strength = reader.getNextFloat();
        int colorRGB = reader.getNextInt();
        this.color = colorRGB == 0 ? null : new Color(colorRGB, true);
    }

    public AphCustomPushPacket(Mob mob, float dirX, float dirY, float strength, Color color) {
        this.mobUniqueID = mob.getUniqueID();
        this.dirX = dirX;
        this.dirY = dirY;
        this.strength = strength;
        this.color = color;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(dirX);
        writer.putNextFloat(dirY);
        writer.putNextFloat(strength);
        writer.putNextInt(color == null ? 0 : color.getRGB());
    }

    public AphCustomPushPacket(Mob mob, float dirX, float dirY, float strength) {
        this(mob, dirX, dirY, strength, null);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob target = GameUtils.getLevelMob((int)this.mobUniqueID, (Level)client.getLevel());
            if (target != null) {
                AphCustomPushPacket.applyToMob(target.getLevel(), target, this.dirX, this.dirY, this.strength, this.color);
            } else {
                client.network.sendPacket((Packet)new PacketRequestMobData(this.mobUniqueID));
            }
        }
    }

    public static void applyToMob(Level level, Mob mob, float dirX, float dirY, float strength, Color color) {
        float forceX = dirX * strength;
        float forceY = dirY * strength;
        if (Math.abs(mob.dx) < Math.abs(forceX)) {
            mob.dx = forceX;
        }
        if (Math.abs(mob.dy) < Math.abs(forceY)) {
            mob.dy = forceY;
        }
        if (color != null && level != null && level.isClient()) {
            for (int i = 0; i < 30; ++i) {
                level.entityManager.addParticle(mob.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 10.0f, mob.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 10.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 10.0f).color(color).height(18.0f).lifeTime(700);
            }
        }
    }
}

