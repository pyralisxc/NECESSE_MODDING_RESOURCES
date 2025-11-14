/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Point;
import java.util.SortedSet;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.PointSortedSet;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public abstract class SettlementTileTickZone
extends SettlementWorkZone {
    protected Point lastProcessingTile;
    protected float iterations;

    @Override
    public void tickSecond() {
        Performance.record((PerformanceTimerManager)this.manager.data.getLevel().tickManager(), "forestryJobs", () -> {
            SortedSet<Point> subSet = this.lastProcessingTile == null ? new PointSortedSet((SortedSet)this.zoning.getTiles().getUnderlyingSet()) : this.zoning.getTiles().tailSet(this.lastProcessingTile, false);
            this.iterations += (float)this.size() / 10.0f;
            for (Point tile : subSet) {
                if (this.iterations < 1.0f) break;
                this.iterations -= 1.0f;
                this.lastProcessingTile = tile;
                this.handleTile(tile);
            }
            if (this.iterations > 1.0f) {
                for (Point tile : this.zoning.getTiles()) {
                    if (this.iterations < 1.0f) break;
                    this.iterations -= 1.0f;
                    this.lastProcessingTile = tile;
                    this.handleTile(tile);
                }
            }
        });
    }

    protected abstract void handleTile(Point var1);
}

