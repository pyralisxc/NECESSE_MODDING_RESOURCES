/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.function.Supplier;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonState;
import necesse.level.maps.mapData.GameMapIcon;

public class ButtonIconGameMapIcon
extends GameMapIcon {
    protected Supplier<ButtonIcon> iconSupplier;
    protected int size;

    public ButtonIconGameMapIcon(Supplier<ButtonIcon> iconSupplier, int size) {
        this.iconSupplier = iconSupplier;
        this.size = size;
    }

    public ButtonIconGameMapIcon(Supplier<ButtonIcon> iconSupplier) {
        this(iconSupplier, 0);
    }

    @Override
    public Rectangle getDrawBoundingBox() {
        if (this.size <= 0) {
            ButtonIcon icon = this.iconSupplier.get();
            return new Rectangle(-icon.texture.getWidth() / 2, -icon.texture.getHeight() / 2, icon.texture.getWidth(), icon.texture.getHeight());
        }
        return new Rectangle(-this.size / 2, -this.size / 2, this.size, this.size);
    }

    @Override
    public void drawIcon(int drawX, int drawY, Color color) {
        ButtonIcon icon = this.iconSupplier.get();
        Color iconColor = (Color)icon.colorGetter.apply(ButtonState.ACTIVE);
        Color merge = MergeFunction.GLBLEND.merge(iconColor, color);
        icon.texture.initDraw().color(merge).posMiddle(drawX, drawY).draw();
    }
}

