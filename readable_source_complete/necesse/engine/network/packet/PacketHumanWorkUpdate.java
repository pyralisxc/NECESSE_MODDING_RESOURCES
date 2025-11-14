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
import necesse.entity.mobs.friendly.human.HumanMob;

public class PacketHumanWorkUpdate
extends Packet {
    public final int mobUniqueID;
    private final PacketReader reader;

    public PacketHumanWorkUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.reader = reader;
    }

    public PacketHumanWorkUpdate(HumanMob mob) {
        this.mobUniqueID = mob.getUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        this.reader = new PacketReader(writer);
        mob.setupWorkPacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof HumanMob) {
            ((HumanMob)mob).applyWorkPacket(new PacketReader(this.reader));
            mob.refreshClientUpdateTime();
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

