/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.level.maps.levelData.jobs.LevelJob;

public class EntityLevelJob<T extends Entity>
extends LevelJob {
    public T target;

    public EntityLevelJob(T target) {
        super(((Entity)target).getTileX(), ((Entity)target).getTileY());
        this.target = target;
    }

    public EntityLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValid() {
        if (this.isRemoved()) {
            return false;
        }
        return !((Entity)this.target).removed();
    }
}

