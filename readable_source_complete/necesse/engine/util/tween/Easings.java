/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.awt.Color;
import java.awt.geom.Path2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.tween.EaseFunction;
import necesse.gfx.Renderer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class Easings {
    public static final EaseFunction Linear = x -> x;
    public static final EaseFunction SineIn = x -> 1.0 - Math.cos(x * Math.PI / 2.0);
    public static final EaseFunction SineOut = x -> Math.sin(x * Math.PI / 2.0);
    public static final EaseFunction SineInOut = x -> -(Math.cos(Math.PI * x) - 1.0) / 2.0;
    public static final EaseFunction QuadIn = x -> x * x;
    public static final EaseFunction QuadOut = x -> 1.0 - (1.0 - x) * (1.0 - x);
    public static final EaseFunction QuadInOut = x -> {
        if (x < 0.5) {
            return 2.0 * x * x;
        }
        return 1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0;
    };
    public static final EaseFunction CubicIn = x -> x * x * x;
    public static final EaseFunction CubicOut = x -> 1.0 - Math.pow(1.0 - x, 3.0);
    public static final EaseFunction CubicInOut = x -> {
        if (x < 0.5) {
            return 4.0 * x * x * x;
        }
        return 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0;
    };
    public static final EaseFunction QuartIn = x -> x * x * x * x;
    public static final EaseFunction QuartOut = x -> 1.0 - Math.pow(1.0 - x, 4.0);
    public static final EaseFunction QuartInOut = x -> x < 0.5 ? 8.0 * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 4.0) / 2.0;
    public static final EaseFunction QuintIn = x -> x * x * x * x * x;
    public static final EaseFunction QuintOut = x -> 1.0 - Math.pow(1.0 - x, 5.0);
    public static final EaseFunction QuintInOut = x -> {
        if (x < 0.5) {
            return 16.0 * x * x * x * x * x;
        }
        return 1.0 - Math.pow(-2.0 * x + 2.0, 5.0) / 2.0;
    };
    public static final EaseFunction ExpoIn = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        return Math.pow(2.0, 10.0 * x - 10.0);
    };
    public static final EaseFunction ExpoOut = x -> {
        if (x == 1.0) {
            return 1.0;
        }
        return 1.0 - Math.pow(2.0, -10.0 * x);
    };
    public static final EaseFunction ExpoInOut = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        if (x == 1.0) {
            return 1.0;
        }
        if (x < 0.5) {
            return Math.pow(2.0, 20.0 * x - 10.0) / 2.0;
        }
        return (2.0 - Math.pow(2.0, -20.0 * x + 10.0)) / 2.0;
    };
    public static final EaseFunction CircIn = x -> 1.0 - Math.sqrt(1.0 - Math.pow(x, 2.0));
    public static final EaseFunction CircOut = x -> Math.sqrt(1.0 - Math.pow(x - 1.0, 2.0));
    public static final EaseFunction CircInOut = x -> {
        if (x < 0.5) {
            return (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0;
        }
        return (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
    };
    public static final double bounceConstant1 = 7.5625;
    public static final double bounceConstant2 = 2.75;
    public static final EaseFunction BounceOut = x -> {
        if (x < 0.36363636363636365) {
            return 7.5625 * x * x;
        }
        if (x < 0.7272727272727273) {
            return 7.5625 * (x -= 0.5454545454545454) * x + 0.75;
        }
        if (x < 0.9090909090909091) {
            return 7.5625 * (x -= 0.8181818181818182) * x + 0.9375;
        }
        return 7.5625 * (x -= 0.9545454545454546) * x + 0.984375;
    };
    public static final EaseFunction BounceIn = x -> 1.0 - BounceOut.ease(1.0 - x);
    public static final EaseFunction BounceInOut = x -> {
        if (x < 0.5) {
            return (1.0 - BounceOut.ease(1.0 - 2.0 * x)) / 2.0;
        }
        return (1.0 + BounceOut.ease(2.0 * x - 1.0)) / 2.0;
    };
    private static final double backConstant1 = 1.70158;
    private static final double backConstant2 = 2.70158;
    public static final EaseFunction BackIn = x -> 2.70158 * x * x * x - 1.70158 * x * x;
    public static final EaseFunction BackOut = x -> 1.0 + 2.70158 * Math.pow(x - 1.0, 3.0) + 1.70158 * Math.pow(x - 1.0, 2.0);
    private static final double backConstant3 = 2.5949095;
    public static final EaseFunction BackInOut = x -> {
        if (x < 0.5) {
            return Math.pow(2.0 * x, 2.0) * (7.189819 * x - 2.5949095) / 2.0;
        }
        return (Math.pow(2.0 * x - 2.0, 2.0) * (3.5949095 * (x * 2.0 - 2.0) + 2.5949095) + 2.0) / 2.0;
    };
    private static final double elasticConstant1 = 2.0943951023931953;
    public static final EaseFunction ElasticIn = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        if (x == 1.0) {
            return 1.0;
        }
        return -Math.pow(2.0, 10.0 * x - 10.0) * Math.sin((x * 10.0 - 10.75) * 2.0943951023931953);
    };
    public static final EaseFunction ElasticOut = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        if (x == 1.0) {
            return 1.0;
        }
        return Math.pow(2.0, -10.0 * x) * Math.sin((x * 10.0 - 0.75) * 2.0943951023931953) + 1.0;
    };
    private static final double elasticConstant2 = 1.3962634015954636;
    public static final EaseFunction ElasticInOut = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        if (x == 1.0) {
            return 1.0;
        }
        if (x < 0.5) {
            return -(Math.pow(2.0, 20.0 * x - 10.0) * Math.sin((20.0 * x - 11.125) * 1.3962634015954636)) / 2.0;
        }
        return Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * 1.3962634015954636) / 2.0 + 1.0;
    };

    public static void drawEasing(EaseFunction ease, double percent, int steps, double posX, double posY, double size, Color color) {
        double y;
        double x;
        int i;
        Path2D.Double path = new Path2D.Double();
        ((Path2D)path).moveTo(0.0, size);
        for (i = 0; i <= steps; ++i) {
            x = (double)i / (double)steps;
            y = 1.0 - ease.ease(x);
            ((Path2D)path).lineTo(x * size, y * size);
        }
        for (i = steps; i >= 0; --i) {
            x = (double)i / (double)steps;
            y = 1.0 - ease.ease(x);
            ((Path2D)path).lineTo(x * size, y * size);
        }
        Renderer.drawShape(path, (float)posX, (float)posY, false, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Renderer.drawCircle((int)(posX + percent * size), (int)(posY + (1.0 - ease.ease(percent)) * size), GameMath.max(2, (int)size / 20), 12, color.darker(), false);
        Renderer.drawLine((int)posX, (int)(posY + size), (int)(posX + size), (int)(posY + size), color);
        Renderer.drawLine((int)posX, (int)posY, (int)posX, (int)(posY + size), color);
        FontManager.bit.drawString((float)posX, (float)(posY + size + 5.0), "Value in:    " + String.format("%.2f", percent), new FontOptions(10));
        FontManager.bit.drawString((float)posX, (float)(posY + size + 15.0), "Value out:  " + String.format("%.2f", ease.ease(percent)), new FontOptions(10));
    }
}

