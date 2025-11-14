/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.fairType.FairGlyph;

public abstract class FairButtonGlyph
implements FairGlyph {
    public final int width;
    public final int height;
    private boolean isHovering;

    public FairButtonGlyph(int width, int height) {
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
    public void draw(float x, float y, Color defaultColor) {
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX + 2, (int)drawY - dim.height - 2, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
        if (this.isHovering) {
            this.handleEvent(drawX, drawY, event);
        }
    }

    public abstract void handleEvent(float var1, float var2, InputEvent var3);

    public boolean isHovering() {
        return this.isHovering;
    }
}

