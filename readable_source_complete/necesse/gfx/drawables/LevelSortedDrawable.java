/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.gfx.drawables.Drawable;
import necesse.level.gameObject.GameObject;

public abstract class LevelSortedDrawable
implements Drawable,
Comparable<LevelSortedDrawable> {
    private final Object object;
    private final int uniqueDecider;
    private int sort;

    protected LevelSortedDrawable(Object object, int uniqueDecider, boolean initiate) {
        this.object = object;
        this.uniqueDecider = uniqueDecider;
        if (initiate) {
            this.init();
        }
    }

    protected LevelSortedDrawable(Object object, boolean initiate) {
        this(object, 0, initiate);
    }

    public LevelSortedDrawable(Object object, int uniqueDecider) {
        this(object, uniqueDecider, true);
    }

    public LevelSortedDrawable(Object object) {
        this(object, true);
    }

    protected void init() {
        this.sort = this.getSortY();
    }

    public LevelSortedDrawable(GameObject object, int tileX, int tileY) {
        this.object = object;
        this.uniqueDecider = tileX;
        this.sort = tileY * 32 + this.getSortY();
    }

    public abstract int getSortY();

    @Override
    public int compareTo(LevelSortedDrawable o) {
        if (o == null) {
            return -1;
        }
        int out = Integer.compare(this.sort, o.sort);
        if (out == 0 && (out = Integer.compare(System.identityHashCode(this.object), System.identityHashCode(o.object))) == 0) {
            out = Integer.compare(this.uniqueDecider, this.uniqueDecider);
        }
        return out;
    }
}

