/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;

public class InputTooltip
implements GameTooltips {
    public final Control control;
    public final ControllerInputState controllerState;
    private TextureDrawOptionsEnd controllerGlyph;
    private boolean controllerNotBound;
    public final String modifierKey;
    public final int inputKey;
    public final String tooltip;
    public final int width;
    public final int height;
    public final String keyName;
    public final FontOptions fontOptions;

    protected InputTooltip(Control control, ControllerInputState controllerState, String modifierKey, int inputKey, String tooltip, float alpha) {
        this.control = control;
        this.controllerState = control != null && Input.lastInputIsController ? control.controllerState : controllerState;
        this.modifierKey = modifierKey;
        this.inputKey = inputKey;
        this.tooltip = tooltip;
        this.fontOptions = new FontOptions(Settings.tooltipTextSize).outline().alphaf(alpha);
        this.keyName = Input.getName(inputKey);
        this.width = this.calcWidth();
        this.height = 20;
    }

    public InputTooltip(String modifierKey, int inputKey, String tooltip, float alpha) {
        this(null, null, modifierKey, inputKey, tooltip, alpha);
    }

    public InputTooltip(int inputKey, String tooltip, float alpha) {
        this(null, inputKey, tooltip, alpha);
    }

    public InputTooltip(String modifierKey, int inputKey, String tooltip) {
        this(modifierKey, inputKey, tooltip, 1.0f);
    }

    public InputTooltip(int inputKey, String tooltip) {
        this(null, inputKey, tooltip);
    }

    public InputTooltip(String modifierKey, Control control, String tooltip, float alpha) {
        this(control, null, modifierKey, control.getKey(), tooltip, alpha);
    }

    public InputTooltip(Control control, String tooltip, float alpha) {
        this(null, control, tooltip, alpha);
    }

    public InputTooltip(String modifierKey, Control control, String tooltip) {
        this(modifierKey, control, tooltip, 1.0f);
    }

    public InputTooltip(Control control, String tooltip) {
        this(null, control, tooltip);
    }

    public InputTooltip(String modifierKey, ControllerInputState controllerState, String tooltip, float alpha) {
        this(null, controllerState, modifierKey, -1, tooltip, alpha);
    }

    public InputTooltip(ControllerInputState controllerState, String tooltip, float alpha) {
        this(null, controllerState, tooltip, alpha);
    }

    public InputTooltip(String modifierKey, ControllerInputState controllerState, String tooltip) {
        this(modifierKey, controllerState, tooltip, 1.0f);
    }

    public InputTooltip(ControllerInputState controllerState, String tooltip) {
        this(null, controllerState, tooltip);
    }

    private int calcWidth() {
        if (this.controllerState != null) {
            GameTexture glyph = ControllerInput.getStateGlyph(this.controllerState);
            if (glyph != null) {
                this.controllerGlyph = glyph.initDraw().size(16, false);
            } else {
                this.controllerNotBound = true;
            }
        }
        if (this.controllerGlyph != null) {
            return this.controllerGlyph.getWidth() + (this.tooltip == null ? 0 : 4 + FontManager.bit.getWidthCeil(this.tooltip, this.fontOptions));
        }
        if (this.controllerNotBound) {
            return Control.getControlIconWidth(this.fontOptions, this.modifierKey, null, "?", this.tooltip);
        }
        if (this.control != null) {
            return Control.getControlIconWidth(this.fontOptions, this.modifierKey, this.control, this.keyName, this.tooltip);
        }
        return Control.getControlIconWidth(this.fontOptions, this.modifierKey, this.inputKey, this.keyName, this.tooltip);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        FontOptions fontOptions = new FontOptions(this.fontOptions);
        if (defaultColor != null) {
            fontOptions.defaultColor(defaultColor.get());
        }
        if (this.controllerGlyph != null) {
            this.controllerGlyph.draw(x, y);
            if (this.tooltip != null) {
                FontManager.bit.drawString(x + this.controllerGlyph.getWidth() + 4, y, this.tooltip, fontOptions);
            }
        } else if (this.controllerNotBound) {
            Control.drawControlIcon(fontOptions, x, y, this.modifierKey, null, "?", this.tooltip);
        } else if (this.control != null) {
            Control.drawControlIcon(fontOptions, x, y, this.modifierKey, this.control, this.keyName, this.tooltip);
        } else {
            Control.drawControlIcon(fontOptions, x, y, this.modifierKey, this.inputKey, this.keyName, this.tooltip);
        }
    }

    @Override
    public int getDrawOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean matchesSearch(String search) {
        if (this.controllerState != null && this.controllerState.getDisplayName().translate().toLowerCase().contains(search)) {
            return true;
        }
        if (this.control != null && this.control.text.translate().toLowerCase().contains(search)) {
            return true;
        }
        if (this.tooltip.toLowerCase().contains(search)) {
            return true;
        }
        return this.keyName.toLowerCase().contains(search);
    }
}

