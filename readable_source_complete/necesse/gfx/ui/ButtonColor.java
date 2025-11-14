/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.util.function.Function;
import necesse.gfx.ui.ButtonColorTextures;
import necesse.gfx.ui.ButtonStateTextures;

public enum ButtonColor {
    BASE(t -> t.base),
    GREEN(t -> t.green),
    YELLOW(t -> t.yellow),
    RED(t -> t.red);

    public final Function<ButtonColorTextures, ButtonStateTextures> colorGetter;

    private ButtonColor(Function<ButtonColorTextures, ButtonStateTextures> colorGetter) {
        this.colorGetter = colorGetter;
    }
}

