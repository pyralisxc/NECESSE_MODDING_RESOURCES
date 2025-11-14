/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.util.FloatDimension;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairInputKeyGlyph
implements FairGlyph {
    public final FontOptions fontOptions;
    public final int inputKey;
    public final String keyName;
    public int width;
    public int height;
    private String tooltip;
    private boolean lastIsController;
    private TextureDrawOptionsEnd controllerGlyph;
    private boolean controllerNotBound;
    private boolean isHovering;

    public FairInputKeyGlyph(FontOptions fontOptions, int inputKey, String keyName, String tooltip) {
        this.fontOptions = fontOptions;
        this.inputKey = inputKey;
        this.keyName = keyName;
        this.updateDimensions();
        this.tooltip = tooltip;
    }

    public FairInputKeyGlyph(FontOptions fontOptions, int inputKey, String tooltip) {
        this(fontOptions, inputKey, Input.getName(inputKey), tooltip);
    }

    public FairInputKeyGlyph(FontOptions fontOptions, int inputKey) {
        this(fontOptions, inputKey, Input.getName(inputKey), null);
    }

    public FairInputKeyGlyph(int inputKey, String tooltip) {
        this(new FontOptions(16), inputKey, tooltip);
    }

    public FairInputKeyGlyph(int inputKey) {
        this(new FontOptions(16), inputKey);
    }

    @Override
    public FloatDimension getDimensions() {
        if (this.lastIsController != Input.lastInputIsController) {
            this.updateDimensions();
            this.lastIsController = Input.lastInputIsController;
        }
        return new FloatDimension(this.width, this.height);
    }

    @Override
    public void updateDimensions() {
        if (Input.lastInputIsController) {
            GameTexture glyph = null;
            switch (this.inputKey) {
                case -100: {
                    glyph = ControllerInput.getStateGlyph(ControllerInput.MENU_SELECT);
                    break;
                }
                case -99: {
                    glyph = ControllerInput.getStateGlyph(ControllerInput.MENU_INTERACT_ITEM);
                }
            }
            if (glyph != null) {
                this.controllerGlyph = glyph.initDraw().size(16, false);
            } else {
                this.controllerGlyph = null;
                this.controllerNotBound = true;
            }
        } else {
            this.controllerNotBound = false;
            this.controllerGlyph = null;
        }
        this.width = this.controllerGlyph != null ? this.controllerGlyph.getWidth() + (this.tooltip == null ? 0 : 4 + FontManager.bit.getWidthCeil(this.tooltip, this.fontOptions)) : (this.controllerNotBound ? Control.getControlIconWidth(this.fontOptions, null, null, "?", this.tooltip) : Control.getControlIconWidth(this.fontOptions, null, this.inputKey, this.keyName, this.tooltip));
        this.height = Math.max(16, this.fontOptions.getSize());
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX, (int)drawY - dim.height - 2, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        if (this.controllerGlyph != null) {
            this.controllerGlyph.draw((int)x, (int)y - this.height);
            if (this.tooltip != null) {
                FontManager.bit.drawString(x + (float)this.controllerGlyph.getWidth() + 4.0f, y, this.tooltip, this.fontOptions);
            }
        } else if (this.controllerNotBound) {
            Control.drawControlIcon(this.fontOptions, (int)x + 1, (int)y - this.height - 2, null, null, "?", this.tooltip);
        } else {
            Control.drawControlIcon(this.fontOptions, (int)x + 1, (int)y - this.height - 2, null, this.inputKey, this.keyName, this.tooltip);
        }
        if (this.isHovering) {
            if (this.tooltip != null) {
                GameTooltipManager.addTooltip(new StringTooltips(this.tooltip), TooltipLocation.FORM_FOCUS);
            } else {
                GameTooltipManager.addTooltip(new StringTooltips(this.keyName), TooltipLocation.FORM_FOCUS);
            }
        }
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public String getParseString() {
        return TypeParsers.getInputParseString(this.inputKey);
    }
}

