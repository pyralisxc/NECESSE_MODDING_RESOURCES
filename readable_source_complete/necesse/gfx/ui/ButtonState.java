/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.awt.Color;
import java.util.function.Function;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonStateTextures;
import necesse.gfx.ui.GameInterfaceStyle;

public enum ButtonState {
    ACTIVE(t -> t.active, t -> t.downActive, s -> s.activeElementColor, s -> s.activeButtonTextColor),
    HIGHLIGHTED(t -> t.highlighted, t -> t.downHighlighted, s -> s.highlightElementColor, s -> s.highlightButtonTextColor),
    INACTIVE(t -> t.inactive, t -> t.downInactive, s -> s.inactiveElementColor, s -> s.inactiveButtonTextColor);

    public final Function<ButtonStateTextures, GameTexture> textureGetter;
    public final Function<ButtonStateTextures, GameTexture> downTextureGetter;
    public final Function<GameInterfaceStyle, Color> elementColorGetter;
    public final Function<GameInterfaceStyle, Color> textColorGetter;

    private ButtonState(Function<ButtonStateTextures, GameTexture> textureGetter, Function<ButtonStateTextures, GameTexture> downTextureGetter, Function<GameInterfaceStyle, Color> elementColorGetter, Function<GameInterfaceStyle, Color> textColorGetter) {
        this.textureGetter = textureGetter;
        this.downTextureGetter = downTextureGetter;
        this.elementColorGetter = elementColorGetter;
        this.textColorGetter = textColorGetter;
    }
}

