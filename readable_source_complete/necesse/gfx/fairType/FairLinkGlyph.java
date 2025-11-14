/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairLinkGlyph
implements FairGlyph {
    public final FairGlyph glyph;
    private final Function<InputEvent, Boolean> onEvent;
    private final Supplier<GameTooltips> tooltipsSupplier;
    private boolean isHovering;

    public FairLinkGlyph(FairGlyph glyph, Function<InputEvent, Boolean> onEvent, Supplier<GameTooltips> tooltipsSupplier) {
        this.glyph = glyph;
        this.onEvent = onEvent;
        this.tooltipsSupplier = tooltipsSupplier;
    }

    @Override
    public FloatDimension getDimensions() {
        return this.glyph.getDimensions();
    }

    @Override
    public void updateDimensions() {
        this.glyph.updateDimensions();
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX, (int)drawY - dim.height, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
        if (this.onEvent != null && this.isHovering && this.onEvent.apply(event).booleanValue()) {
            event.use();
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        this.glyph.draw(x, y, defaultColor);
        if (this.isHovering) {
            GameTooltips tooltips;
            if (this.onEvent != null) {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            }
            if (this.tooltipsSupplier != null && (tooltips = this.tooltipsSupplier.get()) != null) {
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
        }
    }

    @Override
    public void drawShadow(float x, float y) {
        this.glyph.drawShadow(x, y);
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return new FairLinkGlyph(this.glyph.getTextBoxCharacter(), this.onEvent, this.tooltipsSupplier);
    }

    @Override
    public boolean isWhiteSpaceGlyph() {
        return this.glyph.isWhiteSpaceGlyph();
    }

    @Override
    public boolean isNewLineGlyph() {
        return this.glyph.isNewLineGlyph();
    }

    @Override
    public Supplier<Supplier<Color>> getDefaultColor() {
        return this.glyph.getDefaultColor();
    }

    @Override
    public char getCharacter() {
        return this.glyph.getCharacter();
    }

    @Override
    public String getParseString() {
        return this.glyph.getParseString();
    }

    @Override
    public boolean canBeParsed() {
        return this.glyph.canBeParsed();
    }
}

