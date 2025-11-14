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
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobMana
extends Packet {
    public final int mobUniqueID;
    public final boolean isFull;
    private final PacketReader reader;

    public PacketMobMana(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.isFull = reader.getNextBoolean();
        this.reader = reader;
    }

    public PacketMobMana(Mob mob, boolean isFull) {
        this.mobUniqueID = mob.getUniqueID();
        this.isFull = isFull;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextBoolean(isFull);
        this.reader = new PacketReader(writer);
        mob.setupManaPacket(writer, isFull);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob == null) {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        } else {
            mob.applyManaPacket(new PacketReader(this.reader), this.isFull);
        }
    }
}

