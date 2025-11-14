/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import java.awt.Color;
import necesse.gfx.gameFont.FontBasicOptions;

public class FontOptions
extends FontBasicOptions {
    private boolean outline;

    public FontOptions(int size) {
        super(size);
    }

    public FontOptions(FontOptions copy) {
        super(copy);
        this.outline = copy.outline;
    }

    @Override
    public FontOptions copy() {
        return new FontOptions(this);
    }

    @Override
    public FontOptions size(int size) {
        super.size(size);
        return this;
    }

    @Override
    public FontOptions forcePixelFont() {
        super.forcePixelFont();
        return this;
    }

    @Override
    public FontOptions forceNonPixelFont() {
        super.forceNonPixelFont();
        return this;
    }

    @Override
    public FontOptions color(float[] color) {
        super.color(color);
        return this;
    }

    @Override
    public FontOptions colorf(float red, float green, float blue, float alpha) {
        super.colorf(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions colorf(float red, float green, float blue) {
        super.colorf(red, green, blue);
        return this;
    }

    @Override
    public FontOptions color(Color color) {
        super.color(color);
        return this;
    }

    @Override
    public FontOptions color(int red, int green, int blue, int alpha) {
        super.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions color(int red, int green, int blue) {
        super.color(red, green, blue);
        return this;
    }

    @Override
    public FontOptions alphaf(float alpha) {
        super.alphaf(alpha);
        return this;
    }

    @Override
    public FontOptions alpha(int alpha) {
        super.alpha(alpha);
        return this;
    }

    @Override
    public FontOptions defaultColor(float[] color) {
        super.defaultColor(color);
        return this;
    }

    @Override
    public FontOptions defaultColorf(float red, float green, float blue, float alpha) {
        super.defaultColorf(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions defaultColorf(float red, float green, float blue) {
        super.defaultColorf(red, green, blue);
        return this;
    }

    @Override
    public FontOptions defaultColor(Color color) {
        super.defaultColor(color);
        return this;
    }

    @Override
    public FontOptions defaultColor(int red, int green, int blue, int alpha) {
        super.defaultColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions defaultColor(int red, int green, int blue) {
        super.defaultColor(red, green, blue);
        return this;
    }

    public boolean getOutline() {
        return this.outline;
    }

    public FontOptions outline(boolean outline) {
        this.outline = outline;
        return this;
    }

    public FontOptions outline() {
        return this.outline(true);
    }

    public FontOptions clearOutline() {
        return this.outline(false);
    }

    @Override
    public FontOptions outline(float[] color) {
        super.outline(color);
        return this;
    }

    @Override
    public FontOptions clearOutlineColor() {
        super.clearOutlineColor();
        return this;
    }

    @Override
    public FontOptions outlinef(float red, float green, float blue, float alpha) {
        super.outlinef(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions outlinef(float red, float green, float blue) {
        super.outlinef(red, green, blue);
        return this;
    }

    @Override
    public FontOptions outline(Color color) {
        super.outline(color);
        return this;
    }

    @Override
    public FontOptions outline(int red, int green, int blue, int alpha) {
        super.outline(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions outline(int red, int green, int blue) {
        super.outline(red, green, blue);
        return this;
    }

    @Override
    public FontOptions clearShadow() {
        super.clearShadow();
        return this;
    }

    @Override
    public FontOptions shadow(float[] color) {
        super.shadow(color);
        return this;
    }

    @Override
    public FontOptions shadowOffset(int xOffset, int yOffset) {
        super.shadowOffset(xOffset, yOffset);
        return this;
    }

    @Override
    public FontOptions shadow(float[] color, int xOffset, int yOffset) {
        super.shadow(color, xOffset, yOffset);
        return this;
    }

    @Override
    public FontOptions shadow(float red, float green, float blue, float alpha, int xOffset, int yOffset) {
        super.shadow(red, green, blue, alpha, xOffset, yOffset);
        return this;
    }

    @Override
    public FontOptions shadow(Color color, int xOffset, int yOffset) {
        super.shadow(color, xOffset, yOffset);
        return this;
    }

    @Override
    public FontOptions shadow(float alpha, int xOffset, int yOffset) {
        super.shadow(alpha, xOffset, yOffset);
        return this;
    }

    @Override
    public FontOptions shadow(float red, float green, float blue, float alpha) {
        super.shadow(red, green, blue, alpha);
        return this;
    }

    @Override
    public FontOptions shadow(Color color) {
        super.shadow(color);
        return this;
    }

    @Override
    public FontOptions shadow(float alpha) {
        super.shadow(alpha);
        return this;
    }
}

