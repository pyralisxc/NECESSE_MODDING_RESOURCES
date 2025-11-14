/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public class SettlementWorkstationsEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<Point> workstations;

    public SettlementWorkstationsEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        Collection<SettlementWorkstation> stations = data.storageManager.getWorkstations();
        this.workstations = new ArrayList(stations.size());
        for (SettlementWorkstation station : stations) {
            this.workstations.add(new Point(station.tileX, station.tileY));
        }
    }

    public SettlementWorkstationsEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int size = reader.getNextShortUnsigned();
        this.workstations = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            int tileX = reader.getNextInt();
            int tileY = reader.getNextInt();
            this.workstations.add(new Point(tileX, tileY));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.workstations.size());
        for (Point point : this.workstations) {
            writer.putNextInt(point.x);
            writer.putNextInt(point.y);
        }
    }
}

