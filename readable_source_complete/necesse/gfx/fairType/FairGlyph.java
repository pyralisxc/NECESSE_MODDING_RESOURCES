/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;

public interface FairGlyph {
    public FloatDimension getDimensions();

    public void updateDimensions();

    public void handleInputEvent(float var1, float var2, InputEvent var3);

    public void draw(float var1, float var2, Color var3);

    default public void drawShadow(float x, float y) {
    }

    public FairGlyph getTextBoxCharacter();

    default public boolean isWhiteSpaceGlyph() {
        return false;
    }

    default public boolean isNewLineGlyph() {
        return false;
    }

    default public Supplier<Supplier<Color>> getDefaultColor() {
        return null;
    }

    default public char getCharacter() {
        return '\ufffe';
    }

    default public String getParseString() {
        return String.valueOf(this.getCharacter());
    }

    default public boolean canBeParsed() {
        return false;
    }
}

