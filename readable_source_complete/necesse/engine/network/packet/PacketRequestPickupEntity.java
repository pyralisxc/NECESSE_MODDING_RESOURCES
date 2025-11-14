/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemovePickupEntity;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.PickupEntity;

public class PacketRequestPickupEntity
extends Packet {
    public final int pickupUniqueID;

    public PacketRequestPickupEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.pickupUniqueID = reader.getNextInt();
    }

    public PacketRequestPickupEntity(int uniqueID) {
        this.pickupUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.pickupUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        PickupEntity ent = server.world.getLevel((ServerClient)client).entityManager.pickups.get(this.pickupUniqueID, false);
        if (ent != null) {
            server.network.sendPacket((Packet)new PacketSpawnPickupEntity(ent), client);
        } else {
            server.network.sendPacket((Packet)new PacketRemovePickupEntity(this.pickupUniqueID), client);
        }
    }
}

