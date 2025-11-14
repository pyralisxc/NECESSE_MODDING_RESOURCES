/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.position;

import java.util.function.Supplier;
import necesse.gfx.forms.position.FormPosition;

public class FormPositionDynamic
implements FormPosition {
    public int x;
    public int y;
    public Supplier<Integer> xOffset;
    public Supplier<Integer> yOffset;

    public FormPositionDynamic(int x, int y, Supplier<Integer> xOffset, Supplier<Integer> yOffset) {
        this.x = x;
        this.y = y;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public FormPositionDynamic(Supplier<Integer> xOffset, Supplier<Integer> yOffset) {
        this(0, 0, xOffset, yOffset);
    }

    @Override
    public int getX() {
        return this.x + this.xOffset.get();
    }

    @Override
    public int getY() {
        return this.y + this.yOffset.get();
    }
}

