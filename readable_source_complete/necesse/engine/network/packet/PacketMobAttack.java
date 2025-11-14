/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;

public class PacketMobAttack
extends Packet {
    public final int mobUniqueID;
    public final int x;
    public final int y;
    public final boolean showAllDirections;

    public PacketMobAttack(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.showAllDirections = reader.getNextBoolean();
    }

    public PacketMobAttack(Mob mob, int x, int y, boolean showAllDirections) {
        this.mobUniqueID = mob.getUniqueID();
        this.x = x;
        this.y = y;
        this.showAllDirections = showAllDirections;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextBoolean(showAllDirections);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
        if (mob != null) {
            mob.showAttack(this.x, this.y, this.showAllDirections);
        }
    }
}

