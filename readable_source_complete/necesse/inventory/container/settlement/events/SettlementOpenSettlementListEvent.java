/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementOpenSettlementListEvent
extends ContainerEvent {
    public final int mobUniqueID;
    public final ArrayList<SettlementOption> options = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SettlementOpenSettlementListEvent(ServerSettlementData data, ServerClient from, int prevSettlementUniqueID, int mobUniqueID) {
        SettlementsWorldData settlementsData;
        this.mobUniqueID = mobUniqueID;
        SettlementsWorldData settlementsWorldData = settlementsData = SettlementsWorldData.getSettlementsData(from.getServer());
        synchronized (settlementsWorldData) {
            settlementsData.streamSettlements().filter(cachedData -> cachedData.uniqueID != prevSettlementUniqueID && SettlementContainer.hasAccess(data.networkData, cachedData, from) && cachedData.getName() != null).map(cachedData -> new SettlementOption(cachedData.uniqueID, cachedData.getName())).forEach(this.options::add);
        }
    }

    public SettlementOpenSettlementListEvent(PacketReader reader) {
        super(reader);
        this.mobUniqueID = reader.getNextInt();
        int count = reader.getNextShortUnsigned();
        for (int i = 0; i < count; ++i) {
            int settlementUniqueID = reader.getNextInt();
            GameMessage name = GameMessage.fromContentPacket(reader.getNextContentPacket());
            this.options.add(new SettlementOption(settlementUniqueID, name));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.mobUniqueID);
        writer.putNextShortUnsigned(this.options.size());
        for (SettlementOption settlement : this.options) {
            writer.putNextInt(settlement.settlementUniqueID);
            writer.putNextContentPacket(settlement.name.getContentPacket());
        }
    }

    public static class SettlementOption {
        public final int settlementUniqueID;
        public final GameMessage name;

        public SettlementOption(int settlementUniqueID, GameMessage name) {
            this.settlementUniqueID = settlementUniqueID;
            this.name = name;
        }
    }
}

