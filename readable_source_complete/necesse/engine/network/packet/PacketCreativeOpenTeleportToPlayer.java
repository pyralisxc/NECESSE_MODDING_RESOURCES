/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.packet.PacketCreativeCheck;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;

public class PacketCreativeOpenTeleportToPlayer
extends PacketCreativeCheck {
    public PacketCreativeOpenTeleportToPlayer(byte[] data) {
        super(data);
    }

    public PacketCreativeOpenTeleportToPlayer() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!PacketCreativeOpenTeleportToPlayer.checkCreativeAndSendUpdate(server, client)) {
            return;
        }
        PacketOpenContainer openContainerPacket = new PacketOpenContainer(ContainerRegistry.CREATIVE_TELEPORT_TO_PLAYER_CONTAINER);
        ContainerRegistry.openAndSendContainer(client, openContainerPacket);
    }
}

