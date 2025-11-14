/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class CoinPileObjectEntity
extends ObjectEntity {
    public int coinAmount;

    public CoinPileObjectEntity(Level level, int x, int y) {
        super(level, "coinpile", x, y);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("coinAmount", this.coinAmount);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.coinAmount = save.getInt("coinAmount", 1);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextInt(this.coinAmount);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.coinAmount = reader.getNextInt();
    }
}

