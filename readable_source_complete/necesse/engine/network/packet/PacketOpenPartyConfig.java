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

public class PacketOpenPartyConfig
extends Packet {
    public PacketOpenPartyConfig(byte[] data) {
        super(data);
    }

    public PacketOpenPartyConfig() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, new PacketOpenContainer(ContainerRegistry.PARTY_CONFIG_CONTAINER));
    }
}

