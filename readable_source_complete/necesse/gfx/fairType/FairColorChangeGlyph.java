/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairGlyph;

public class FairColorChangeGlyph
implements FairGlyph {
    private final Supplier<Color> colorSupplier;
    private final String parseString;

    public FairColorChangeGlyph(String parseString, Supplier<Color> colorSupplier) {
        this.parseString = parseString;
        this.colorSupplier = colorSupplier;
    }

    public FairColorChangeGlyph(Color color) {
        this(null, () -> color);
    }

    public FairColorChangeGlyph(GameColor color) {
        this(color.getColorCode(), color.color);
    }

    @Override
    public FloatDimension getDimensions() {
        return new FloatDimension();
    }

    @Override
    public void updateDimensions() {
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public Supplier<Supplier<Color>> getDefaultColor() {
        return () -> this.colorSupplier;
    }

    @Override
    public String getParseString() {
        if (this.parseString != null) {
            return this.parseString;
        }
        if (this.colorSupplier == null) {
            return String.valueOf('\u00a7') + String.valueOf(GameColor.NO_COLOR.codeChar);
        }
        Color color = this.colorSupplier.get();
        return '\u00a7' + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}

