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

public class PacketRemovePickupEntity
extends Packet {
    public final int pickupUniqueID;

    public PacketRemovePickupEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.pickupUniqueID = reader.getNextInt();
    }

    public PacketRemovePickupEntity(int uniqueID) {
        this.pickupUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.pickupUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PickupEntity pickupEntity;
        if (client.getLevel() != null && (pickupEntity = client.getLevel().entityManager.pickups.get(this.pickupUniqueID, false)) != null) {
            pickupEntity.remove();
        }
    }
}

