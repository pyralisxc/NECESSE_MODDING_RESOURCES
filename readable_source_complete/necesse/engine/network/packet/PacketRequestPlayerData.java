/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestPlayerData
extends Packet {
    public final int slot;

    public PacketRequestPlayerData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
    }

    public PacketRequestPlayerData(int slot) {
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ServerClient requestClient = server.getClient(this.slot);
        if (requestClient == null) {
            server.network.sendPacket((Packet)new PacketDisconnect(this.slot, PacketDisconnect.Code.MISSING_CLIENT), client);
        } else {
            if (this.slot == client.slot) {
                client.requestSelf();
                client.sendPacket(new PacketAdventurePartyUpdate(client));
            }
            server.network.sendPacket((Packet)new PacketPlayerGeneral(requestClient), client);
        }
    }
}

