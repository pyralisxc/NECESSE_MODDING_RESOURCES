/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidStage;

public abstract class TimerSettlementRaidStage
extends SettlementRaidStage {
    public int remainingTime;

    public TimerSettlementRaidStage(String stringID, SettlementRaidLevelEvent event, int seconds) {
        super(stringID, event);
        this.remainingTime = seconds * 1000;
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addInt("remainingApproachTimer", this.remainingTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.remainingTime = save.getInt("remainingApproachTimer", this.remainingTime);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.remainingTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        this.remainingTime = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        int mod = this.event.getLevel().presentPlayers > 0 ? 3 : 1;
        this.remainingTime -= (int)(50.0f * this.getTimerDecreaseMod());
    }

    @Override
    public void serverTick() {
        this.remainingTime -= (int)(50.0f * this.getTimerDecreaseMod());
    }

    protected float getTimerDecreaseMod() {
        return 1.0f;
    }

    @Override
    public boolean isComplete() {
        return this.remainingTime <= 0;
    }
}

