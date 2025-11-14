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
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PacketOpenPvPTeams
extends Packet {
    public PacketOpenPvPTeams(byte[] data) {
        super(data);
    }

    public PacketOpenPvPTeams() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, new PacketOpenContainer(ContainerRegistry.PVP_TEAMS_CONTAINER, PvPTeamsContainer.getContainerContent(client)));
    }
}

