/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.position;

import necesse.gfx.forms.position.FormPosition;

public class FormFixedPosition
implements FormPosition {
    private int x;
    private int y;

    public FormFixedPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public FormFixedPosition() {
        this(0, 0);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}

