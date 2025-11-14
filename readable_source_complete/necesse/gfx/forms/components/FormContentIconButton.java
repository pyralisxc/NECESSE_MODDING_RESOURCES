/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonTexture;

public class FormContentIconButton
extends FormContentButton {
    private GameMessage[] tooltips;
    private ButtonTexture icon;
    private boolean mirrorX;
    private boolean mirrorY;
    private int rightAngles;

    public FormContentIconButton(int x, int y, int width, FormInputSize size, ButtonColor color, ButtonTexture icon, GameMessage ... tooltips) {
        super(x, y, width, size, color);
        this.icon = icon;
        this.tooltips = tooltips;
    }

    public FormContentIconButton(int x, int y, FormInputSize size, ButtonColor color, ButtonTexture icon, GameMessage ... tooltips) {
        this(x, y, size.height, size, color, icon, tooltips);
    }

    protected int getIconDrawX(ButtonTexture icon, int x, int width) {
        return x + width / 2 - icon.texture.getWidth() / 2;
    }

    protected int getIconDrawY(ButtonTexture icon, int y, int height) {
        return y + height / 2 - icon.texture.getHeight() / 2;
    }

    public Color getContentColor() {
        return this.icon.colorGetter.apply(this.getButtonState());
    }

    @Override
    protected void drawContent(int x, int y, int width, int height) {
        if (this.icon == null) {
            return;
        }
        this.icon.texture.initDraw().color(this.getContentColor()).mirror(this.mirrorX, this.mirrorY).rotateTexture(this.rightAngles).draw(this.getIconDrawX(this.icon, x, width), this.getIconDrawY(this.icon, y, height));
    }

    @Override
    protected void addTooltips(PlayerMob perspective) {
        super.addTooltips(perspective);
        GameTooltips tooltips = this.getTooltips(perspective);
        if (tooltips != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public GameTooltips getTooltips(PlayerMob perspective) {
        if (this.tooltips.length != 0) {
            StringTooltips out = new StringTooltips();
            for (GameMessage tooltip : this.tooltips) {
                out.add(tooltip.translate(), 350);
            }
            return out;
        }
        return null;
    }

    public void setTooltips(GameMessage ... tooltips) {
        this.tooltips = tooltips;
    }

    public void setIcon(ButtonIcon icon) {
        this.icon = icon;
    }

    public FormContentIconButton mirror(boolean mirrorX, boolean mirrorY) {
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
        return this;
    }

    public FormContentIconButton mirrorX() {
        this.mirrorX = true;
        return this;
    }

    public FormContentIconButton mirrorY() {
        this.mirrorY = true;
        return this;
    }

    public FormContentIconButton rotate(int rightAngles) {
        this.rightAngles = rightAngles;
        return this;
    }

    @Override
    public void drawDraggingElement(int mouseX, int mouseY) {
        GameTooltips tooltips = this.getTooltips(null);
        if (tooltips != null) {
            tooltips.draw(mouseX, mouseY - tooltips.getHeight() - 4, GameColor.DEFAULT_COLOR);
        }
    }
}

