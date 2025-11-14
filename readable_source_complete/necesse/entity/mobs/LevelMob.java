/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.function.Consumer;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class LevelMob<T extends Mob> {
    public int uniqueID;
    private T mob;

    public LevelMob(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public LevelMob(T mob) {
        this.uniqueID = ((Entity)mob).getUniqueID();
        this.mob = mob;
    }

    public LevelMob() {
        this(-1);
    }

    public T get(Level level) {
        if (this.uniqueID == -1) {
            if (this.mob != null) {
                T oldMob = this.mob;
                this.mob = null;
                this.onMobChanged(oldMob, this.mob);
            }
        } else {
            if (this.mob != null && ((Entity)this.mob).getLevel() != level) {
                level = this.onMobChangedLevel(this.mob, level);
            }
            if (this.mob == null || ((Entity)this.mob).getUniqueID() != this.uniqueID || ((Entity)this.mob).getLevel() != level || ((Entity)this.mob).removed()) {
                T oldMob = this.mob;
                this.mob = null;
                try {
                    this.mob = GameUtils.getLevelMob(this.uniqueID, level, false);
                    if (this.mob != null) {
                        this.onMobChanged(oldMob, this.mob);
                    }
                }
                catch (ClassCastException e) {
                    this.mob = null;
                }
                if (this.mob == null && oldMob != null && ((Entity)oldMob).removed()) {
                    this.onMobRemoved(oldMob);
                }
            }
        }
        return this.mob;
    }

    public void computeIfPresent(Level level, Consumer<T> consumer) {
        T mob = this.get(level);
        if (mob != null) {
            consumer.accept(mob);
        }
    }

    public void onMobChanged(T oldMob, T newMob) {
    }

    public Level onMobChangedLevel(T mob, Level currentLevel) {
        return currentLevel;
    }

    public void onMobRemoved(T mob) {
    }
}

