/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Rectangle;
import necesse.engine.input.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;

public abstract class MapDrawElement {
    private Level level;
    private boolean removed;
    protected int x;
    protected int y;
    protected Rectangle boundingBox;

    public MapDrawElement(int x, int y, Rectangle boundingBox) {
        this.x = x;
        this.y = y;
        this.boundingBox = boundingBox;
    }

    public void init(Level level) {
        if (this.level == level) {
            return;
        }
        if (this.level != null) {
            throw new IllegalStateException("Cannot add the same map draw element to different levels");
        }
        this.level = level;
    }

    protected final Level getLevel() {
        return this.level;
    }

    public final void remove() {
        if (this.removed) {
            return;
        }
        this.removed = true;
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    public boolean shouldRemove() {
        return false;
    }

    public void onRemove() {
    }

    public final long getTime() {
        return this.level.getWorldEntity().getLocalTime();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    public abstract void draw(int var1, int var2, PlayerMob var3);

    public abstract GameTooltips getTooltips(int var1, int var2, PlayerMob var3);

    public String getMapInteractTooltip() {
        return null;
    }

    public void onMapInteract(InputEvent event, PlayerMob perspective) {
    }
}

