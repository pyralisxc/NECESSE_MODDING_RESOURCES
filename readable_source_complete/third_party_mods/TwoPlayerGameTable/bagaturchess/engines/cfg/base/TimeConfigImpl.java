/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.uci.api.ITimeConfig;

public class TimeConfigImpl
implements ITimeConfig {
    private int moveEvallDiff_MaxScoreDiff = 150;
    private double moveEvallDiff_MaxTotalTimeUsagePercent = 0.25;
    private double timeoptimization_ConsumedTimeVSRemainingTimePercent = 0.5;

    @Override
    public int getMoveEvallDiff_MaxScoreDiff() {
        return this.moveEvallDiff_MaxScoreDiff;
    }

    @Override
    public double getMoveEvallDiff_MaxTotalTimeUsagePercent() {
        return this.moveEvallDiff_MaxTotalTimeUsagePercent;
    }

    @Override
    public double getTimeoptimization_ConsumedTimeVSRemainingTimePercent() {
        return this.timeoptimization_ConsumedTimeVSRemainingTimePercent;
    }
}

