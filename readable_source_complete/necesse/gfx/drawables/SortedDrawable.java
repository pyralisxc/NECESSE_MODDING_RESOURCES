/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.gfx.drawables.Drawable;

public abstract class SortedDrawable
implements Drawable,
Comparable<SortedDrawable> {
    private int priority = this.getPriority();

    public abstract int getPriority();

    @Override
    public int compareTo(SortedDrawable o) {
        return Integer.compare(this.priority, o.priority);
    }
}

