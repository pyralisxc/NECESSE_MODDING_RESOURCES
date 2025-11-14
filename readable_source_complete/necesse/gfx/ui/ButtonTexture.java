/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.awt.Color;
import java.util.function.Function;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.GameInterfaceStyle;

public class ButtonTexture {
    public GameTexture texture;
    public Function<ButtonState, Color> colorGetter;

    public ButtonTexture(GameTexture texture, Function<ButtonState, Color> colorGetter) {
        this.texture = texture;
        this.colorGetter = colorGetter;
    }

    public ButtonTexture(GameTexture texture, Color color) {
        this(texture, (ButtonState state) -> color);
    }

    public ButtonTexture(GameInterfaceStyle style, GameTexture texture, boolean useTextColor) {
        this(texture, useTextColor ? state -> state.textColorGetter.apply(style) : state -> state.elementColorGetter.apply(style));
    }

    public ButtonTexture(GameInterfaceStyle style, GameTexture texture) {
        this(style, texture, true);
    }
}

