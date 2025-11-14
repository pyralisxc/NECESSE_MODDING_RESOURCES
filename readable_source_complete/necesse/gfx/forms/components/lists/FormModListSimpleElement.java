/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modLoader.ModProvider;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;

public class FormModListSimpleElement
extends Form {
    public FormModListSimpleElement(String text, int width, final ModProvider modProvider, final boolean enabled, Color color, final GameTooltips tooltip) {
        super(width, 24);
        this.drawBase = false;
        this.shouldLimitDrawArea = false;
        this.addComponent(new FormCustomDraw(4, 0, 20, 20){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                Color color;
                HoverStateTextures icon = modProvider.getIcon();
                GameTexture texture = icon.active;
                Color color2 = color = enabled ? this.getInterfaceStyle().activeTextColor : this.getInterfaceStyle().inactiveTextColor;
                if (this.isHovering()) {
                    color = this.getInterfaceStyle().highlightTextColor;
                    texture = icon.highlighted;
                }
                texture.initDraw().size(24).color(color).draw(this.getX(), this.getY());
                if (this.isHovering()) {
                    GameTooltipManager.addTooltip(new StringTooltips(modProvider.getGameMessage().translate()), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        int startX = 28;
        this.addComponent(new FormLabel(text, new FontOptions(16).color(color), -1, startX, 4, width - startX));
        if (tooltip != null) {
            this.addComponent(new FormMouseHover(startX, 0, width - startX, this.getHeight()){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    return tooltip;
                }
            }, 1000);
        }
    }

    public FormModListSimpleElement(String text, int width, ModProvider modProvider, boolean enabled, Color color, String tooltip) {
        this(text, width, modProvider, enabled, color, tooltip == null ? null : new StringTooltips(tooltip));
    }

    public FormModListSimpleElement(String text, int width, ModProvider modProvider, boolean enabled, Color color) {
        this(text, width, modProvider, enabled, color, (GameTooltips)null);
    }
}

