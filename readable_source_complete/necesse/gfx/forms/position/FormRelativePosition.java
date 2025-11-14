/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.position;

import java.util.function.Supplier;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormRelativePosition
implements FormPosition {
    private final FormPositionContainer parent;
    public Supplier<Integer> x;
    public Supplier<Integer> y;

    public FormRelativePosition(FormPositionContainer parent, Supplier<Integer> x, Supplier<Integer> y) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public FormRelativePosition(FormPositionContainer parent, int x, int y) {
        this(parent, () -> x, () -> y);
    }

    @Override
    public int getX() {
        return this.parent.getX() + this.x.get();
    }

    @Override
    public int getY() {
        return this.parent.getY() + this.y.get();
    }
}

