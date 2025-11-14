/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.awt.Color;
import necesse.engine.util.GameMath;
import necesse.engine.util.tween.ValueTween;
import necesse.gfx.Renderer;

public class ColorTween
extends ValueTween<Color, ColorTween> {
    private InterpolationMode interpolationMode = InterpolationMode.Linear;

    public ColorTween(double duration, Color initialValue, Color endValue) {
        super(duration, initialValue, endValue);
    }

    public ColorTween(Color initialValue) {
        super(initialValue);
    }

    public ColorTween(ColorTween existingTween, double duration, Color endValue) {
        super(existingTween, duration, endValue);
        this.interpolationMode = existingTween.interpolationMode;
    }

    public static void drawTweenDebug(ColorTween tween, double posX, double posY, int width, int height) {
        for (int i = 0; i < width; ++i) {
            Color color = (Color)tween.updateAndGet((double)i / (double)width);
            Renderer.drawLine((int)posX + i, (int)posY, (int)(posX + (double)i), (int)(posY + (double)height), color);
        }
    }

    public InterpolationMode getInterpolationMode() {
        return this.interpolationMode;
    }

    public ColorTween setInterpolationMode(InterpolationMode mode) {
        this.interpolationMode = mode;
        return this;
    }

    @Override
    protected void tween(double percent) {
        switch (this.interpolationMode) {
            case Linear: {
                this.tweenLinear(percent);
                break;
            }
            case Hue: {
                this.tweenHue(percent, false);
                break;
            }
            case HueLong: {
                this.tweenHue(percent, true);
            }
        }
    }

    protected void tweenLinear(double percent) {
        int startRed = ((Color)this.startValue).getRed();
        int startGreen = ((Color)this.startValue).getGreen();
        int startBlue = ((Color)this.startValue).getBlue();
        int startAlpha = ((Color)this.startValue).getAlpha();
        int endRed = ((Color)this.endValue).getRed();
        int endGreen = ((Color)this.endValue).getGreen();
        int endBlue = ((Color)this.endValue).getBlue();
        int endAlpha = ((Color)this.endValue).getAlpha();
        int red = GameMath.limit((int)Math.round((double)startRed + percent * (double)(endRed - startRed)), 0, 255);
        int green = GameMath.limit((int)Math.round((double)startGreen + percent * (double)(endGreen - startGreen)), 0, 255);
        int blue = GameMath.limit((int)Math.round((double)startBlue + percent * (double)(endBlue - startBlue)), 0, 255);
        int alpha = GameMath.limit((int)Math.round((double)startAlpha + percent * (double)(endAlpha - startAlpha)), 0, 255);
        this.setValue(new Color(red, green, blue, alpha));
    }

    protected void tweenHue(double percent, boolean forceLong) {
        float[] startHSB = Color.RGBtoHSB(((Color)this.startValue).getRed(), ((Color)this.startValue).getGreen(), ((Color)this.startValue).getBlue(), null);
        float[] endHSB = Color.RGBtoHSB(((Color)this.endValue).getRed(), ((Color)this.endValue).getGreen(), ((Color)this.endValue).getBlue(), null);
        float startHue = startHSB[0];
        float endHue = endHSB[0];
        float deltaHue = endHue - startHue;
        if (Math.abs(startHue + 1.0f - endHue) < Math.abs(deltaHue)) {
            deltaHue = endHue - (startHue += 1.0f);
        } else if (Math.abs(startHue - endHue - 1.0f) < Math.abs(deltaHue)) {
            deltaHue = (endHue += 1.0f) - startHue;
        }
        if (startHue > endHue) {
            deltaHue = -(startHue - endHue);
        }
        if (forceLong) {
            deltaHue = deltaHue < 0.0f ? (deltaHue += 1.0f) : (deltaHue -= 1.0f);
        }
        float hue = (float)((double)startHue + (double)deltaHue * percent);
        float saturation = (float)((double)startHSB[1] + percent * (double)(endHSB[1] - startHSB[1]));
        float brightness = (float)((double)startHSB[2] + percent * (double)(endHSB[2] - startHSB[2]));
        int startAlpha = ((Color)this.startValue).getAlpha();
        int alpha = GameMath.limit((int)Math.round((double)startAlpha + percent * (double)(((Color)this.endValue).getAlpha() - startAlpha)), 0, 255);
        int colorValue = Color.HSBtoRGB(hue, saturation, brightness);
        this.setValue(new Color(colorValue >> 16 & 0xFF, colorValue >> 8 & 0xFF, colorValue & 0xFF, alpha));
    }

    @Override
    public ColorTween newTween(double duration, Color endValue) {
        return new ColorTween(this, duration, endValue);
    }

    public static enum InterpolationMode {
        Linear,
        Hue,
        HueLong;

    }
}

