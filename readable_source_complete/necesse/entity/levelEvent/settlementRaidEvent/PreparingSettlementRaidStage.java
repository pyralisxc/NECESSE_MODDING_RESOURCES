/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.TimerSettlementRaidStage;

public class PreparingSettlementRaidStage
extends TimerSettlementRaidStage {
    protected int totalSpawnTime;
    protected int totalIdleTime;
    protected int preparingMessageTimer;
    protected float additionalRaiderBuffer = 0.0f;
    protected float nextSpawnTick = 1.0f;
    protected int startAttackingTimer;
    protected int raidGroupCounter;

    public PreparingSettlementRaidStage(String stringID, SettlementRaidLevelEvent event, int spawnSeconds, int idleSeconds) {
        super(stringID, event, spawnSeconds + idleSeconds);
        this.totalSpawnTime = spawnSeconds * 1000;
        this.totalIdleTime = idleSeconds * 1000;
        this.startAttackingTimer = this.totalSpawnTime + this.totalIdleTime;
    }

    public void changeIdleTile(int idleTimeMillis) {
        idleTimeMillis = Math.min(idleTimeMillis, Integer.MAX_VALUE - this.totalSpawnTime);
        this.remainingTime = this.totalSpawnTime + idleTimeMillis;
        this.totalIdleTime = idleTimeMillis;
        this.startAttackingTimer = this.totalSpawnTime + this.totalIdleTime;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addFloat("nextSpawnTick", this.nextSpawnTick);
        save.addInt("raidGroupCounter", this.raidGroupCounter);
        save.addInt("totalSpawnTime", this.totalSpawnTime);
        save.addInt("totalIdleTime", this.totalIdleTime);
        save.addInt("startAttackingTimer", this.startAttackingTimer);
        save.addInt("preparingMessageTimer", this.preparingMessageTimer);
        save.addFloat("additionalRaiderBuffer", this.additionalRaiderBuffer);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.nextSpawnTick = save.getFloat("nextSpawnTick", this.nextSpawnTick);
        this.raidGroupCounter = save.getInt("raidGroupCounter", this.raidGroupCounter);
        this.totalSpawnTime = save.getInt("totalSpawnTime", this.totalSpawnTime);
        this.totalIdleTime = save.getInt("totalIdleTime", this.totalIdleTime);
        this.startAttackingTimer = save.getInt("startAttackingTimer", this.startAttackingTimer);
        this.preparingMessageTimer = save.getInt("preparingMessageTimer", this.preparingMessageTimer);
        this.additionalRaiderBuffer = save.getFloat("additionalRaiderBuffer", this.additionalRaiderBuffer);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.totalSpawnTime);
        writer.putNextInt(this.totalIdleTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.totalSpawnTime = reader.getNextInt();
        this.totalIdleTime = reader.getNextInt();
    }

    @Override
    public void init(boolean loadedFromSave, boolean hasCompletedStage) {
    }

    @Override
    public void afterLoadingInit() {
        if (!this.event.isServer()) {
            return;
        }
        GameMessage preparingMessage = this.event.getPreparingMessage(this.event.networkData.getSettlementName());
        if (preparingMessage != null) {
            this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(preparingMessage));
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.remainingTime >= this.totalIdleTime) {
            this.tickRaidSpawning();
        }
        this.tickPreparingMessage();
    }

    @Override
    public boolean isComplete() {
        return super.isComplete() || this.remainingTime < this.totalIdleTime && this.event.started;
    }

    @Override
    public void onStarted() {
    }

    @Override
    public void onComplete() {
    }

    public void tickRaidSpawning() {
        if (this.event.isServer()) {
            float spawnRate = this.event.getSpawnWaves();
            this.nextSpawnTick += spawnRate / ((float)this.totalSpawnTime / 50.0f);
            if (this.nextSpawnTick >= 1.0f) {
                this.nextSpawnTick -= 1.0f;
                ++this.raidGroupCounter;
                this.additionalRaiderBuffer += this.event.getTotalSpawns() / spawnRate;
                boolean success = false;
                while (this.additionalRaiderBuffer >= 1.0f) {
                    this.additionalRaiderBuffer -= 1.0f;
                    Point tile = GameRandom.globalRandom.getOneOf(this.event.spawnTiles);
                    success = this.event.spawnRaider(tile, this.startAttackingTimer, this.raidGroupCounter);
                }
                if (!success) {
                    this.nextSpawnTick += 0.5f;
                }
            }
        }
    }

    public void tickPreparingMessage() {
        if (!this.event.started) {
            GameMessage preparingMessage;
            if (this.preparingMessageTimer % 60000 == 0 && (preparingMessage = this.event.getPreparingMessage(this.event.networkData.getSettlementName())) != null) {
                this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(preparingMessage));
            }
            this.preparingMessageTimer += 50;
        }
    }

    public void onRaidStartedPrematurely(int minStartRaidTimer) {
        this.startAttackingTimer -= minStartRaidTimer;
    }
}

