/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.ArrayList;

public class DPSTracker {
    public int DPS_TRACKING_TIME = 5000;
    public int DPS_RESET_TIME = 2000;
    private ArrayList<DamageHit> hits = new ArrayList();
    private boolean updateDPS = false;
    private int dps = 0;

    public void tick(long currentTime) {
        this.tickHits(currentTime);
    }

    public void addHit(long time, float damage) {
        this.hits.add(new DamageHit(time, damage));
        this.updateDPS = true;
    }

    private void tickHits(long currentTime) {
        boolean update = false;
        if (this.hits.size() > 0) {
            long latestHitTime = this.hits.get((int)(this.hits.size() - 1)).time;
            if (currentTime - latestHitTime > (long)this.DPS_RESET_TIME) {
                this.hits.clear();
                update = true;
            } else {
                long minTime = currentTime - (long)this.DPS_TRACKING_TIME;
                while (this.hits.size() > 0) {
                    DamageHit hit = this.hits.get(0);
                    if (hit.time >= minTime) break;
                    this.hits.remove(0);
                    update = true;
                }
            }
        }
        if (update) {
            this.updateDPS = true;
        }
    }

    public boolean isLastHitBeforeReset(long currentTime) {
        if (this.hits.isEmpty()) {
            return false;
        }
        long latestHitTime = this.hits.get((int)(this.hits.size() - 1)).time;
        return currentTime - latestHitTime < (long)this.DPS_RESET_TIME;
    }

    public int getDPS(long currentTime) {
        if (this.updateDPS) {
            this.dps = 0;
            float totalDamage = 0.0f;
            for (DamageHit hit : this.hits) {
                totalDamage += hit.damage;
            }
            long firstTime = this.hits.size() > 0 ? this.hits.get((int)0).time : currentTime;
            double seconds = (double)(currentTime - firstTime) / 1000.0;
            if (seconds < 1.0) {
                seconds = 1.0;
            }
            this.dps = (int)((double)totalDamage / seconds);
            this.updateDPS = false;
        }
        return this.dps;
    }

    private static class DamageHit {
        public final long time;
        public final float damage;

        public DamageHit(long time, float damage) {
            this.time = time;
            this.damage = damage;
        }
    }
}

