/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Rectangle;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveSettlementData;
import necesse.engine.network.packet.PacketSettlementNotificationFull;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class PacketSettlementRequestNotificationFull
extends Packet {
    public final int settlementUniqueID;

    public PacketSettlementRequestNotificationFull(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.settlementUniqueID = reader.getNextInt();
    }

    public PacketSettlementRequestNotificationFull(NetworkSettlementData settlement) {
        this.settlementUniqueID = settlement.uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.settlementUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
        CachedSettlementData cache = worldData.getCachedData(this.settlementUniqueID);
        if (cache != null) {
            Rectangle regionRectangle = cache.getRegionRectangle();
            if (cache.hasAccess(client) || client.hasAnyRegionLoaded(cache.levelIdentifier, p -> regionRectangle.contains(p.x, p.y))) {
                ServerSettlementData serverData = worldData.getOrLoadServerData(this.settlementUniqueID);
                client.sendPacket(new PacketSettlementNotificationFull(serverData.networkData.notifications));
            } else {
                client.sendPacket(new PacketRemoveSettlementData(this.settlementUniqueID));
            }
        } else {
            client.sendPacket(new PacketRemoveSettlementData(this.settlementUniqueID));
        }
    }
}

