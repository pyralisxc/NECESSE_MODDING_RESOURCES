/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;

public abstract class SettlementRaidStage {
    public final String stringID;
    public final SettlementRaidLevelEvent event;

    public SettlementRaidStage(String stringID, SettlementRaidLevelEvent event) {
        this.stringID = stringID;
        this.event = event;
    }

    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);

    public abstract void setupSpawnPacket(PacketWriter var1);

    public abstract void applySpawnPacket(PacketReader var1);

    public abstract void init(boolean var1, boolean var2);

    public abstract void afterLoadingInit();

    public abstract void onStarted();

    public abstract void clientTick();

    public abstract void serverTick();

    public abstract boolean isComplete();

    public abstract void onComplete();
}

