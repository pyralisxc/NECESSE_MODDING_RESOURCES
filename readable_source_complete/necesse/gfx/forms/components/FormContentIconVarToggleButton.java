/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentVarToggleButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconVarToggleButton
extends FormContentVarToggleButton {
    private GameMessage[] tooltips;
    private ButtonIcon onIcon;
    private ButtonIcon offIcon;
    private boolean onMirrorX;
    private boolean onMirrorY;
    private boolean offMirrorX;
    private boolean offMirrorY;

    public FormContentIconVarToggleButton(int x, int y, int width, FormInputSize size, ButtonColor color, Supplier<Boolean> isToggled, ButtonIcon onIcon, ButtonIcon offIcon, GameMessage ... tooltips) {
        super(x, y, width, size, color, isToggled);
        this.onIcon = onIcon;
        this.offIcon = offIcon;
        this.tooltips = tooltips;
    }

    public FormContentIconVarToggleButton(int x, int y, FormInputSize size, ButtonColor color, Supplier<Boolean> isToggled, ButtonIcon onIcon, ButtonIcon offIcon, GameMessage ... tooltips) {
        this(x, y, size.height, size, color, isToggled, onIcon, offIcon, tooltips);
    }

    public FormContentIconVarToggleButton(int x, int y, int width, FormInputSize size, ButtonColor color, Supplier<Boolean> isToggled, ButtonIcon icon, GameMessage ... tooltips) {
        this(x, y, width, size, color, isToggled, icon, icon, tooltips);
    }

    public FormContentIconVarToggleButton(int x, int y, FormInputSize size, ButtonColor color, Supplier<Boolean> isToggled, ButtonIcon icon, GameMessage ... tooltips) {
        this(x, y, size.height, size, color, isToggled, icon, tooltips);
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
        icon.texture.initDraw().color(this.getContentColor(icon)).mirror(toggled ? this.onMirrorX : this.offMirrorX, toggled ? this.onMirrorY : this.offMirrorY).draw(this.getIconDrawX(icon, x, width), this.getIconDrawY(icon, y, height));
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
                out.add(tooltip.translate());
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

    public FormContentIconVarToggleButton onMirror(boolean mirrorX, boolean mirrorY) {
        this.onMirrorX = mirrorX;
        this.onMirrorY = mirrorY;
        return this;
    }

    public FormContentIconVarToggleButton onMirrorX() {
        this.onMirrorX = true;
        return this;
    }

    public FormContentIconVarToggleButton onMirrorY() {
        this.onMirrorY = true;
        return this;
    }

    public FormContentIconVarToggleButton offMirror(boolean mirrorX, boolean mirrorY) {
        this.offMirrorX = mirrorX;
        this.offMirrorY = mirrorY;
        return this;
    }

    public FormContentIconVarToggleButton offMirrorX() {
        this.offMirrorX = true;
        return this;
    }

    public FormContentIconVarToggleButton offMirrorY() {
        this.offMirrorY = true;
        return this;
    }
}

