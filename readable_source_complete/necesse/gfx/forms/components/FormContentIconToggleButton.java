/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentToggleButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconToggleButton
extends FormContentToggleButton {
    protected GameMessage[] tooltips;
    protected ButtonIcon onIcon;
    protected ButtonIcon offIcon;
    protected boolean onMirrorX;
    protected boolean onMirrorY;
    protected boolean offMirrorX;
    protected boolean offMirrorY;
    protected int onRightAngles;
    protected int offRightAngles;

    public FormContentIconToggleButton(int x, int y, int width, FormInputSize size, ButtonColor color, ButtonIcon onIcon, ButtonIcon offIcon, GameMessage ... tooltips) {
        super(x, y, width, size, color);
        this.onIcon = onIcon;
        this.offIcon = offIcon;
        this.tooltips = tooltips;
    }

    public FormContentIconToggleButton(int x, int y, FormInputSize size, ButtonColor color, ButtonIcon onIcon, ButtonIcon offIcon, GameMessage ... tooltips) {
        this(x, y, size.height, size, color, onIcon, offIcon, tooltips);
    }

    public FormContentIconToggleButton(int x, int y, int width, FormInputSize size, ButtonColor color, ButtonIcon icon, GameMessage ... tooltips) {
        this(x, y, width, size, color, icon, icon, tooltips);
    }

    public FormContentIconToggleButton(int x, int y, FormInputSize size, ButtonColor color, ButtonIcon icon, GameMessage ... tooltips) {
        this(x, y, size.height, size, color, icon, tooltips);
    }

    protected int getIconDrawX(ButtonIcon icon, int x, int width) {
        return x + width / 2 - icon.texture.getWidth() / 2;
    }

    protected int getIconDrawY(ButtonIcon icon, int y, int height) {
        return y + height / 2 - icon.texture.getHeight() / 2;
    }

    public Color getContentColor(ButtonIcon icon) {
        return (Color)icon.colorGetter.apply(this.getButtonState());
    }

    @Override
    protected void drawContent(int x, int y, int width, int height) {
        ButtonIcon icon;
        boolean toggled = this.isToggled();
        ButtonIcon buttonIcon = icon = toggled ? this.onIcon : this.offIcon;
        if (icon == null) {
            return;
        }
        icon.texture.initDraw().color(this.getContentColor(icon)).mirror(toggled ? this.onMirrorX : this.offMirrorX, toggled ? this.onMirrorY : this.offMirrorY).rotateTexture(toggled ? this.onRightAngles : this.offRightAngles).draw(this.getIconDrawX(icon, x, width), this.getIconDrawY(icon, y, height));
    }

    @Override
    protected void addTooltips(PlayerMob perspective) {
        super.addTooltips(perspective);
        GameTooltips tooltips = this.getTooltips();
        if (tooltips != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public GameTooltips getTooltips() {
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

    public void setIcon(ButtonIcon onIcon, ButtonIcon offIcon) {
        this.onIcon = onIcon;
        this.offIcon = offIcon;
    }

    public void setIcon(ButtonIcon icon) {
        this.setIcon(icon, icon);
    }

    public FormContentIconToggleButton onMirror(boolean mirrorX, boolean mirrorY) {
        this.onMirrorX = mirrorX;
        this.onMirrorY = mirrorY;
        return this;
    }

    public FormContentIconToggleButton onMirrorX() {
        this.onMirrorX = true;
        return this;
    }

    public FormContentIconToggleButton onMirrorY() {
        this.onMirrorY = true;
        return this;
    }

    public FormContentIconToggleButton offMirror(boolean mirrorX, boolean mirrorY) {
        this.offMirrorX = mirrorX;
        this.offMirrorY = mirrorY;
        return this;
    }

    public FormContentIconToggleButton offMirrorX() {
        this.offMirrorX = true;
        return this;
    }

    public FormContentIconToggleButton offMirrorY() {
        this.offMirrorY = true;
        return this;
    }

    public FormContentIconToggleButton rightAngles(int rightAngles) {
        this.onRightAngles = Math.floorMod(this.onRightAngles + rightAngles, 4);
        this.offRightAngles = Math.floorMod(this.offRightAngles + rightAngles, 4);
        return this;
    }

    public FormContentIconToggleButton onRightAngles(int rightAngles) {
        this.onRightAngles = Math.floorMod(this.onRightAngles + rightAngles, 4);
        return this;
    }

    public FormContentIconToggleButton offRightAngles(int rightAngles) {
        this.offRightAngles = Math.floorMod(this.offRightAngles + rightAngles, 4);
        return this;
    }
}

