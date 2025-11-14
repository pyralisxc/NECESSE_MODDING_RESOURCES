/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.entity.Entity;
import necesse.gfx.drawables.LevelSortedDrawable;

public abstract class EntityDrawable
extends LevelSortedDrawable {
    private final Entity entity;

    public EntityDrawable(Entity entity) {
        super((Object)entity, false);
        this.entity = entity;
        this.init();
    }

    @Override
    public int getSortY() {
        return this.entity.getY();
    }
}

