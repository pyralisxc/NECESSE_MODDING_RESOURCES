/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.TimerSettlementRaidStage;

public class ApproachSettlementRaidStage
extends TimerSettlementRaidStage {
    protected LinkedList<Point> potentialSpawnTiles = new LinkedList();
    protected HashSet<Point> foundSpawnTiles = new HashSet();

    public ApproachSettlementRaidStage(String stringID, SettlementRaidLevelEvent event, int seconds) {
        super(stringID, event, seconds);
    }

    @Override
    public void init(boolean loadedFromSave, boolean hasCompletedStage) {
        if (this.event.centerSpawnTile != null) {
            this.potentialSpawnTiles.add(this.event.centerSpawnTile);
            for (int x = this.event.centerSpawnTile.x - 5; x < this.event.centerSpawnTile.x + 5; ++x) {
                for (int y = this.event.centerSpawnTile.y - 5; y < this.event.centerSpawnTile.y + 5; ++y) {
                    if (!this.event.getLevel().isTileWithinBounds(x, y, 3) || !this.event.serverData.networkData.isTileWithinLoadedRegionBounds(x, y)) continue;
                    this.potentialSpawnTiles.add(new Point(x, y));
                }
            }
            if (hasCompletedStage) {
                while (!this.potentialSpawnTiles.isEmpty()) {
                    this.processPotentialTile(this.potentialSpawnTiles.removeFirst());
                }
                this.event.spawnTiles = new ArrayList<Point>(this.foundSpawnTiles);
                this.foundSpawnTiles.clear();
            }
        }
    }

    @Override
    public void afterLoadingInit() {
        if (!this.event.isServer()) {
            return;
        }
        GameMessage approachMessage = this.event.getApproachMessage(this.event.networkData.getSettlementName(), false);
        if (approachMessage != null) {
            this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(approachMessage));
        }
    }

    @Override
    public void onStarted() {
        GameMessage approachMessage;
        if (this.event.isServer() && (approachMessage = this.event.getApproachMessage(this.event.networkData.getSettlementName(), true)) != null) {
            this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(approachMessage));
        }
    }

    @Override
    public void serverTick() {
        GameMessage approachMessage;
        super.serverTick();
        float mod = this.getTimerDecreaseMod();
        int tilesToProcess = Math.min((int)Math.ceil((double)this.potentialSpawnTiles.size() / ((double)this.remainingTime / 50.0) * (double)mod), this.potentialSpawnTiles.size());
        for (int i = 0; i < tilesToProcess; ++i) {
            this.processPotentialTile(this.potentialSpawnTiles.removeFirst());
        }
        if ((float)this.remainingTime >= mod && (float)(this.remainingTime % 60000) < mod && (approachMessage = this.event.getApproachMessage(this.event.networkData.getSettlementName(), false)) != null) {
            this.event.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(approachMessage));
        }
    }

    @Override
    protected float getTimerDecreaseMod() {
        return this.event.getLevel().presentPlayers > 0 ? 3.0f : 1.0f;
    }

    @Override
    public void onComplete() {
        if (!this.event.isServer()) {
            return;
        }
        while (!this.potentialSpawnTiles.isEmpty()) {
            this.processPotentialTile(this.potentialSpawnTiles.removeFirst());
        }
        this.event.spawnTiles = new ArrayList<Point>(this.foundSpawnTiles);
        this.foundSpawnTiles.clear();
    }

    protected void processPotentialTile(Point tile) {
        if (this.event.getLevel().isSolidTile(tile.x, tile.y)) {
            return;
        }
        this.foundSpawnTiles.add(tile);
    }
}

