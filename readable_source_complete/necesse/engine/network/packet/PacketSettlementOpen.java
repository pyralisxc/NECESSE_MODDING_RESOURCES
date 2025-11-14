/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class PacketSettlementOpen
extends Packet {
    public PacketSettlementOpen(byte[] data) {
        super(data);
    }

    public PacketSettlementOpen() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            client.sendChatMessage(new LocalMessage("ui", "settlementsurface"));
            return;
        }
        ServerSettlementData serverData = SettlementsWorldData.getSettlementsData(level).getOrLoadServerDataAtTile(level.getIdentifier(), client.playerMob.getTileX(), client.playerMob.getTileY());
        if (serverData != null && (!serverData.networkData.isDisbandingPrevented() || serverData.hasFlag())) {
            PacketOpenContainer openPacket = PacketOpenContainer.Settlement(ContainerRegistry.SETTLEMENT_CONTAINER, serverData);
            ContainerRegistry.openAndSendContainer(client, openPacket);
        } else {
            client.sendChatMessage(new LocalMessage("ui", "settlementnone"));
        }
    }
}

