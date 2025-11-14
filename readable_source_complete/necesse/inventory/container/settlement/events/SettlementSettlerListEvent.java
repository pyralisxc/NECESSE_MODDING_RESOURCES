/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerListEvent
extends ContainerEvent {
    public final ArrayList<SettlementSettlerData> settlers;

    public SettlementSettlerListEvent(ServerSettlementData data) {
        this.settlers = new ArrayList();
        for (LevelSettler settler : data.settlers) {
            SettlerMob mob = settler.getMob();
            if (mob == null) continue;
            this.settlers.add(new SettlementSettlerData(settler));
        }
    }

    public SettlementSettlerListEvent(PacketReader reader) {
        super(reader);
        int settlersSize = reader.getNextShortUnsigned();
        this.settlers = new ArrayList(settlersSize);
        for (int i = 0; i < settlersSize; ++i) {
            this.settlers.add(new SettlementSettlerData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextShortUnsigned(this.settlers.size());
        for (SettlementSettlerData settler : this.settlers) {
            settler.writeContentPacket(writer);
        }
    }
}

