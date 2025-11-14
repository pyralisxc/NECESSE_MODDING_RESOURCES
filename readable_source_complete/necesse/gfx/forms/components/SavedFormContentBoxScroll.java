/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import necesse.gfx.forms.components.FormContentBox;

public class SavedFormContentBoxScroll {
    protected boolean set;
    protected int scrollX;
    protected int scrollY;

    public void save(FormContentBox contentBox) {
        this.scrollX = contentBox.getScrollX();
        this.scrollY = contentBox.getScrollY();
        this.set = true;
    }

    public void load(FormContentBox contentBox) {
        if (this.set) {
            contentBox.setScrollX(this.scrollX);
            contentBox.setScrollY(this.scrollY);
            this.set = false;
        }
    }
}

