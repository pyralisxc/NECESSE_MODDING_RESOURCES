/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.Form
 */
package medievalsim.ui;

import java.awt.Rectangle;
import necesse.gfx.forms.Form;

public abstract class ResizableForm
extends Form {
    protected final int minWidth;
    protected final int minHeight;
    protected final int maxWidth;
    protected final int maxHeight;

    public ResizableForm(String name, int minWidth, int minHeight, int maxWidth, int maxHeight, int defaultWidth, int defaultHeight) {
        super(name, defaultWidth, defaultHeight);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.setDraggingBox(new Rectangle(0, 0, defaultWidth, 30));
    }

    protected abstract void onResize(int var1, int var2, int var3, int var4);

    public void resize(int newWidth, int newHeight) {
        int oldWidth = this.getWidth();
        int oldHeight = this.getHeight();
        newWidth = Math.max(this.minWidth, Math.min(this.maxWidth, newWidth));
        newHeight = Math.max(this.minHeight, Math.min(this.maxHeight, newHeight));
        if (newWidth != oldWidth || newHeight != oldHeight) {
            this.setWidth(newWidth);
            this.setHeight(newHeight);
            this.onResize(oldWidth, oldHeight, newWidth, newHeight);
            this.setDraggingBox(new Rectangle(0, 0, newWidth, 30));
        }
    }

    public int getMinWidth() {
        return this.minWidth;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }
}

