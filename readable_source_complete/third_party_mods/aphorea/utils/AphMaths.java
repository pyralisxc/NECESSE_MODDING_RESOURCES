/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package aphorea.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AphMaths {
    @NotNull
    @Contract(pure=true)
    public static float[] perpendicularVector(float p1x, float p1y, float p2x, float p2y) {
        float Vx = p1y - p2y;
        float Vy = -p1x + p2x;
        float[] vector = new float[]{Vx, Vy};
        return vector;
    }

    @NotNull
    @Contract(value="_, _ -> new", pure=true)
    public static float[] normalVector(float Vx, float Vy) {
        float magnitude = (float)Math.sqrt(Vx * Vx + Vy * Vy);
        if (magnitude == 0.0f) {
            return new float[]{0.0f, 0.0f};
        }
        return new float[]{Vx / magnitude, Vy / magnitude};
    }

    @NotNull
    public static float[] perpendicularNormalVector(float p1x, float p1y, float p2x, float p2y) {
        float[] perpendicular = AphMaths.perpendicularVector(p1x, p1y, p2x, p2y);
        return AphMaths.normalVector(perpendicular[0], perpendicular[1]);
    }
}

