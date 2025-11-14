/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerDietsData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerDietsEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<SettlementSettlerDietsData> settlers;

    public SettlementSettlerDietsEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.settlers = new ArrayList();
        for (LevelSettler settler : data.settlers) {
            SettlerMob mob = settler.getMob();
            if (mob == null || !mob.doesEatFood()) continue;
            this.settlers.add(new SettlementSettlerDietsData(settler));
        }
    }

    public SettlementSettlerDietsEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int settlersSize = reader.getNextShortUnsigned();
        this.settlers = new ArrayList(settlersSize);
        for (int i = 0; i < settlersSize; ++i) {
            this.settlers.add(new SettlementSettlerDietsData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.settlers.size());
        for (SettlementSettlerDietsData settler : this.settlers) {
            settler.writeContentPacket(writer);
        }
    }
}

