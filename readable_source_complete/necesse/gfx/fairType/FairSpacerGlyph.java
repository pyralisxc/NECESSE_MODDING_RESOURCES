/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.fairType.FairGlyph;

public class FairSpacerGlyph
implements FairGlyph {
    public final float width;
    public final float height;

    public FairSpacerGlyph(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public FloatDimension getDimensions() {
        return new FloatDimension(this.width, this.height);
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
}

