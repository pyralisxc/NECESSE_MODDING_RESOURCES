/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketDeath
extends Packet {
    public final int mobUniqueID;
    public final float knockbackX;
    public final float knockbackY;
    public final boolean isDeath;

    public PacketDeath(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.knockbackX = reader.getNextFloat();
        this.knockbackY = reader.getNextFloat();
        this.isDeath = reader.getNextBoolean();
    }

    public PacketDeath(int uniqueID, float knockbackX, float knockbackY, boolean isDeath) {
        this.mobUniqueID = uniqueID;
        this.knockbackX = knockbackX;
        this.knockbackY = knockbackY;
        this.isDeath = isDeath;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(uniqueID);
        writer.putNextFloat(knockbackX);
        writer.putNextFloat(knockbackY);
        writer.putNextBoolean(isDeath);
    }

    public PacketDeath(Mob mob, float knockbackX, float knockbackY, boolean isDeath) {
        this(mob.getUniqueID(), knockbackX, knockbackY, isDeath);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob != null) {
            mob.remove(this.knockbackX, this.knockbackY, null, this.isDeath);
        }
    }
}

