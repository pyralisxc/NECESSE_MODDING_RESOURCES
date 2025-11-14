/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerBasicData
extends SettlementSettlerData {
    public final boolean canMoveOut;
    public final boolean canBanish;

    public SettlementSettlerBasicData(LevelSettler settler) {
        super(settler);
        this.canMoveOut = settler.canMoveOut();
        this.canBanish = settler.canBanish();
    }

    public SettlementSettlerBasicData(PacketReader reader) {
        super(reader);
        this.canMoveOut = reader.getNextBoolean();
        this.canBanish = reader.getNextBoolean();
    }

    @Override
    public void writeContentPacket(PacketWriter writer) {
        super.writeContentPacket(writer);
        writer.putNextBoolean(this.canMoveOut);
        writer.putNextBoolean(this.canBanish);
    }
}

