/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.TimerSettlementRaidStage;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;

public class ActiveSettlementRaidStage
extends TimerSettlementRaidStage {
    public double shouldStopBuffer;

    public ActiveSettlementRaidStage(String stringID, SettlementRaidLevelEvent event, int maxActiveSeconds) {
        super(stringID, event, maxActiveSeconds);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addDouble("shouldStopBuffer", this.shouldStopBuffer);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.shouldStopBuffer = save.getDouble("shouldStopBuffer", this.shouldStopBuffer, false);
    }

    @Override
    public void init(boolean loadedFromSave, boolean hasCompletedStage) {
    }

    @Override
    public void afterLoadingInit() {
        if (!this.event.isServer()) {
            return;
        }
        GameMessage startMessage = this.event.getStartMessage(this.event.networkData.getSettlementName());
        if (startMessage != null) {
            this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(startMessage));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
    }

    @Override
    public void serverTick() {
        float raidersThatLikeLootingPercentage;
        float aliveRaidersPercentage;
        super.serverTick();
        int currentSettlers = this.event.getCurrentSettlerMobs();
        int killedSettlers = this.event.startSettlers - currentSettlers;
        float exponent = 0.5f;
        switch (this.event.getLevel().getWorldSettings().difficulty) {
            case HARD: {
                exponent = 0.65f;
                break;
            }
            case BRUTAL: {
                exponent = 0.75f;
            }
        }
        int maxKilledSettlers = Math.max(1, (int)Math.floor(Math.pow(this.event.startSettlers - 3, exponent)));
        if (currentSettlers <= 0 || killedSettlers >= maxKilledSettlers) {
            this.shouldStopBuffer += 1.0;
        }
        float f = aliveRaidersPercentage = this.event.spawnedRaiders <= 0 ? 0.0f : (float)this.event.raiders.size() / (float)this.event.spawnedRaiders;
        if (aliveRaidersPercentage <= 0.5f) {
            int minSecondsUntilFull = 0;
            int maxSecondsUntilFull = 40;
            float percentage = GameMath.clamp(aliveRaidersPercentage, 0.5f, 0.0f);
            int secondsUntilFull = GameMath.lerp(percentage = (float)Math.pow(percentage, 0.5), maxSecondsUntilFull, minSecondsUntilFull);
            double bufferIncrease = 1.0f / ((float)secondsUntilFull / 0.02f);
            if (bufferIncrease == Double.POSITIVE_INFINITY) {
                bufferIncrease = 1.0;
            }
            this.shouldStopBuffer += bufferIncrease;
        }
        long raidersThatLikeLooting = this.event.raiders.stream().filter(ItemAttackerRaiderMob::wouldLikeToStartLooting).count();
        float f2 = raidersThatLikeLootingPercentage = this.event.raiders.isEmpty() ? 0.0f : (float)raidersThatLikeLooting / (float)this.event.raiders.size();
        if (raidersThatLikeLootingPercentage >= 0.5f) {
            int minSecondsUntilFull = 0;
            int maxSecondsUntilFull = 40;
            float percentage = GameMath.clamp(raidersThatLikeLootingPercentage, 0.5f, 1.0f);
            int secondsUntilFull = GameMath.lerp(percentage = (float)Math.pow(percentage, 0.5), maxSecondsUntilFull, minSecondsUntilFull);
            double bufferIncrease = 1.0f / ((float)secondsUntilFull / 0.02f);
            if (bufferIncrease == Double.POSITIVE_INFINITY) {
                bufferIncrease = 1.0;
            }
            this.shouldStopBuffer += bufferIncrease;
        }
    }

    @Override
    public boolean isComplete() {
        return super.isComplete() || !this.event.isClient() && (this.event.raiders.isEmpty() || this.shouldStopBuffer >= 1.0);
    }

    @Override
    public void onStarted() {
        if (!this.event.isServer()) {
            return;
        }
        this.event.startRaid(false);
    }

    @Override
    public void onComplete() {
    }
}

