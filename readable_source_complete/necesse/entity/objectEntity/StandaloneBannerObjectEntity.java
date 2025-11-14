/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.BannerObjectEntity;
import necesse.level.maps.Level;

public abstract class StandaloneBannerObjectEntity
extends BannerObjectEntity {
    public StandaloneBannerObjectEntity(Level level, String type, int x, int y) {
        super(level, type, x, y);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickBuffs();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickBuffs();
    }

    public boolean appliesBuffsToSettlers() {
        return true;
    }

    public abstract int getBuffRange();

    public abstract void applyBuffs(Mob var1);

    public void tickBuffs() {
        int range = this.getBuffRange();
        GameUtils.streamNetworkClients(this.getLevel()).filter(c -> c.playerMob.getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16) <= (float)range).forEach(c -> this.applyBuffs(c.playerMob));
        if (this.appliesBuffsToSettlers()) {
            this.getLevel().entityManager.mobs.streamInRegionsInRange(this.tileX * 32 + 16, this.tileY * 32 + 16, range).filter(m -> m.isHuman).filter(m -> m.getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16) <= (float)range).forEach(this::applyBuffs);
        }
    }
}

