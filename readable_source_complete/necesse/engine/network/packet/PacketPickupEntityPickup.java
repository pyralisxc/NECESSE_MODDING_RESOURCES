/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPickupEntity;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.entity.pickup.PickupEntity;

public class PacketPickupEntityPickup
extends Packet {
    public final int pickupUniqueID;
    public final int targetSlot;
    public final Packet content;

    public PacketPickupEntityPickup(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.pickupUniqueID = reader.getNextInt();
        this.targetSlot = reader.getNextByteUnsigned();
        this.content = reader.getNextContentPacket();
    }

    public PacketPickupEntityPickup(PickupEntity entity, Packet content) {
        this.pickupUniqueID = entity.getUniqueID();
        this.targetSlot = entity.getTarget().slot;
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.pickupUniqueID);
        writer.putNextByteUnsigned(this.targetSlot);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ClientClient target = client.getClient(this.targetSlot);
        if (target != null) {
            PickupEntity entity = client.getLevel().entityManager.pickups.get(this.pickupUniqueID, true);
            if (entity != null) {
                entity.onPickup(target, this.content);
                entity.refreshClientUpdateTime();
            } else {
                client.network.sendPacket(new PacketRequestPickupEntity(this.pickupUniqueID));
            }
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.targetSlot));
        }
    }
}

