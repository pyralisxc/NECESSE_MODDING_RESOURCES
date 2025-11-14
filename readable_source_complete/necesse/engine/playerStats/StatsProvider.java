/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import necesse.engine.playerStats.GameGlobalStats;

public abstract class StatsProvider {
    public void initialize() {
    }

    public void dispose() {
    }

    public void tick() {
    }

    public abstract void resetStatsAndAchievements(boolean var1);

    public abstract void forceStoreStatsAndAchievements();

    public abstract void storeStatsAndAchievements();

    public abstract void updateGlobalStats();

    public abstract GameGlobalStats getGlobalStats();

    public abstract long getGlobalStat(String var1, long var2);

    public abstract double getGlobalStat(String var1, double var2);

    public abstract void setAchievement(String var1);

    public abstract int getStat(String var1, int var2);

    public abstract void setStat(String var1, int var2, boolean var3);

    public abstract void setStat(String var1, int var2);
}

