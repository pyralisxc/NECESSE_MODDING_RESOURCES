/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerData;

public class AvailableExpeditionSettlersResponseEvent
extends ContainerEvent {
    public final int missionUniqueID;
    public final ArrayList<SettlementSettlerData> validSettlerUniqueIDs;

    public AvailableExpeditionSettlersResponseEvent(int missionUniqueID, ArrayList<SettlementSettlerData> validSettlerUniqueIDs) {
        this.missionUniqueID = missionUniqueID;
        this.validSettlerUniqueIDs = validSettlerUniqueIDs;
    }

    public AvailableExpeditionSettlersResponseEvent(PacketReader reader) {
        super(reader);
        this.missionUniqueID = reader.getNextInt();
        this.validSettlerUniqueIDs = reader.getNextCollection(size -> new ArrayList(), () -> new SettlementSettlerData(reader));
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.missionUniqueID);
        writer.putNextCollection(this.validSettlerUniqueIDs, e -> e.writeContentPacket(writer));
    }
}

