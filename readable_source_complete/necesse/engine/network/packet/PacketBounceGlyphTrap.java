/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketBounceGlyphTrap
extends Packet {
    public final int mobUniqueID;
    public final float posX;
    public final float posY;
    public final float angle;
    public final float strength;

    public PacketBounceGlyphTrap(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.posX = reader.getNextFloat();
        this.posY = reader.getNextFloat();
        this.angle = reader.getNextFloat();
        this.strength = reader.getNextFloat();
    }

    public PacketBounceGlyphTrap(Mob mob, float posX, float posY, float angle, float strength) {
        this.mobUniqueID = mob.getUniqueID();
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
        this.strength = strength;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(posX);
        writer.putNextFloat(posY);
        writer.putNextFloat(angle);
        writer.putNextFloat(strength);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (target != null) {
            PacketBounceGlyphTrap.applyToMob(target, this.posX, this.posY, this.angle, this.strength);
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }

    public static void applyToMob(Mob mob, float posX, float posY, float angle, float strength) {
        mob.x = posX;
        mob.y = posY;
        mob.dx = GameMath.cos(angle) * strength;
        mob.dy = GameMath.sin(angle) * strength;
    }
}

