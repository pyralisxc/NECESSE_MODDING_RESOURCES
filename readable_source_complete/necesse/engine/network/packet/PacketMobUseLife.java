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

public class PacketMobUseLife
extends Packet {
    public final int mobUniqueID;
    private final int currentLife;
    private final int usedLife;

    public PacketMobUseLife(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.currentLife = reader.getNextInt();
        this.usedLife = reader.getNextInt();
    }

    public PacketMobUseLife(Mob mob, int usedLife) {
        this.mobUniqueID = mob.getUniqueID();
        this.currentLife = mob.getHealth();
        this.usedLife = usedLife;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextInt(this.currentLife);
        writer.putNextInt(usedLife);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (mob == null) {
                client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
            } else {
                mob.useLife(this.currentLife, this.usedLife, null, null);
            }
        }
    }
}

