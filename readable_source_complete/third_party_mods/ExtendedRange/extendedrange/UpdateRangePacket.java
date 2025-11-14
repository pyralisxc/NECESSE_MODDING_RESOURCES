/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.inventory.container.object.CraftingStationContainer
 */
package extendedrange;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.inventory.container.object.CraftingStationContainer;

public class UpdateRangePacket
extends Packet {
    public final int newRange;

    public UpdateRangePacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.newRange = reader.getNextInt();
    }

    public UpdateRangePacket(int newRange) {
        this.newRange = newRange;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(newRange);
    }

    public void processClient(NetworkPacket packet, Client client) {
        PacketReader reader = new PacketReader(packet.getTypePacket());
        CraftingStationContainer.nearbyCraftTileRange = reader.getNextInt();
    }
}

