/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.List;
import necesse.engine.dlc.DLC;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketClientInstalledDLC
extends Packet {
    public final int slot;
    public final int[] installedDLC;

    public PacketClientInstalledDLC(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        int installedDLCCount = reader.getNextInt();
        this.installedDLC = reader.getNextInts(installedDLCCount);
    }

    public PacketClientInstalledDLC(int slot, List<DLC> installedDLC) {
        this.installedDLC = installedDLC.stream().mapToInt(DLC::getID).toArray();
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(installedDLC.size());
        writer.putNextInts(this.installedDLC);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.applyClientInstalledDLCPacket(this);
        server.network.sendToAllClients(this);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.applyClientInstalledDLCPacket(this);
    }
}

