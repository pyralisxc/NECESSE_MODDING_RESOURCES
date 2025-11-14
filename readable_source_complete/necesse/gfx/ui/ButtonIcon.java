/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.awt.Color;
import java.util.function.Function;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.ButtonTexture;
import necesse.gfx.ui.GameInterfaceStyle;

public class ButtonIcon
extends ButtonTexture {
    public ButtonIcon(GameInterfaceStyle style, String path, Function<ButtonState, Color> colorGetter) {
        super(style.fromFile(path), colorGetter);
    }

    public ButtonIcon(GameInterfaceStyle style, String path, Color color) {
        this(style, path, (ButtonState state) -> color);
    }

    public ButtonIcon(GameInterfaceStyle style, String path, boolean useTextColor) {
        this(style, path, useTextColor ? state -> state.textColorGetter.apply(style) : state -> state.elementColorGetter.apply(style));
    }

    public ButtonIcon(GameInterfaceStyle style, String path) {
        this(style, path, true);
    }
}

