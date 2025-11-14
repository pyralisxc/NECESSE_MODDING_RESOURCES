/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.fairType.FairGlyph;

public class FairInvisibleTextParseGlyph
implements FairGlyph {
    public String parsedString;

    public FairInvisibleTextParseGlyph(String parsedString) {
        this.parsedString = parsedString;
    }

    @Override
    public FloatDimension getDimensions() {
        return new FloatDimension(0.0f, 0.0f);
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
        return new FairInvisibleTextParseGlyph(this.parsedString);
    }

    @Override
    public String getParseString() {
        return this.parsedString;
    }
}

