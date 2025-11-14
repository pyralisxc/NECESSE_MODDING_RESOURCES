/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager;

import java.util.List;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudManager;

public abstract class HudDrawElement {
    private Level level;
    private boolean removed;

    protected void setLevel(Level level) {
        this.level = level;
    }

    protected final Level getLevel() {
        return this.level;
    }

    public void init(HudManager manager) {
    }

    public final void remove() {
        if (this.removed) {
            return;
        }
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    protected void onRemove() {
    }

    public final long getTime() {
        return this.level.getWorldEntity().getLocalTime();
    }

    public abstract void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3);
}

