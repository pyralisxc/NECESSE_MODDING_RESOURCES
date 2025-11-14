/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketCreativeCheck;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.Level;

public class PacketCreativeEndRaid
extends PacketCreativeCheck {
    public PacketCreativeEndRaid(byte[] data) {
        super(data);
    }

    public PacketCreativeEndRaid() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!PacketCreativeEndRaid.checkCreativeAndSendUpdate(server, client)) {
            return;
        }
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        boolean raidEnded = false;
        int playerRegionX = level.regionManager.getRegionCoordByTile(client.playerMob.getTileX());
        int playerRegionY = level.regionManager.getRegionCoordByTile(client.playerMob.getTileY());
        for (LevelEvent event : level.entityManager.events.regionList.getInRegion(playerRegionX, playerRegionY)) {
            if (!(event instanceof SettlementRaidLevelEvent) || event.isOver()) continue;
            event.over();
            raidEnded = true;
        }
        if (raidEnded) {
            server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeendraidsuccessful", "player", client.getName())));
        } else {
            client.sendChatMessage(new LocalMessage("ui", "creativeendraidfailed"));
        }
    }
}

