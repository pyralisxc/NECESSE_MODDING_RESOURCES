/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairURLLinkGlyph
implements FairGlyph {
    public final FairGlyph glyph;
    public URI uri;
    public boolean parsedGlyph;
    private boolean isHovering;

    public FairURLLinkGlyph(FairGlyph glyph, URI uri, boolean parsedGlyph) {
        Objects.requireNonNull(glyph);
        Objects.requireNonNull(uri);
        this.glyph = glyph;
        this.uri = uri;
        this.parsedGlyph = parsedGlyph;
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
        if (this.isHovering && event.getID() == -100) {
            if (!event.state) {
                GameUtils.openURL(this.uri);
            }
            event.use();
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        this.glyph.draw(x, y, defaultColor);
        if (this.isHovering) {
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("misc", "openurl", "url", this.uri.toString())), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public void drawShadow(float x, float y) {
        this.glyph.drawShadow(x, y);
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return new FairURLLinkGlyph(this.glyph.getTextBoxCharacter(), this.uri, this.parsedGlyph);
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

