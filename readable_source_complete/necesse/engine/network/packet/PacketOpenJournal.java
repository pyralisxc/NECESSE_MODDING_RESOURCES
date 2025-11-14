/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;

public class PacketOpenJournal
extends Packet {
    public PacketOpenJournal(byte[] data) {
        super(data);
    }

    public PacketOpenJournal() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, new PacketOpenContainer(ContainerRegistry.JOURNAL_CONTAINER));
        client.hasNewJournalEntry = false;
    }
}

