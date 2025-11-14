/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.pickup.PickupEntity;

public class PacketPickupEntityTarget
extends Packet {
    public final int pickupUniqueID;
    public final Packet content;

    public PacketPickupEntityTarget(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.pickupUniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketPickupEntityTarget(PickupEntity pickupEntity) {
        this.pickupUniqueID = pickupEntity.getUniqueID();
        this.content = new Packet();
        pickupEntity.writeTargetUpdatePacket(new PacketWriter(this.content), true);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.pickupUniqueID);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        PickupEntity ent = client.getLevel().entityManager.pickups.get(this.pickupUniqueID, false);
        if (ent != null) {
            ent.readTargetUpdatePacket(new PacketReader(this.content), true);
        }
    }
}

