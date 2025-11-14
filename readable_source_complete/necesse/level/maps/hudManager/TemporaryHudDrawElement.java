/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager;

import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.HudManager;

public abstract class TemporaryHudDrawElement
extends HudDrawElement {
    public int lifeTime;
    public long spawnTime;

    public TemporaryHudDrawElement(int millisecondsLifeTime) {
        this.lifeTime = millisecondsLifeTime;
    }

    @Override
    public void init(HudManager manager) {
        super.init(manager);
        this.spawnTime = this.getLevel().getWorldEntity().getLocalTime();
    }

    @Override
    public boolean isRemoved() {
        return super.isRemoved() || this.getTimeSinceSpawned() > this.lifeTime;
    }

    public int getTimeSinceSpawned() {
        Level level = this.getLevel();
        if (level == null) {
            return 0;
        }
        return (int)(level.getWorldEntity().getLocalTime() - this.spawnTime);
    }

    public float getLifeProgressPercent() {
        return (float)this.getTimeSinceSpawned() / (float)this.lifeTime;
    }
}

