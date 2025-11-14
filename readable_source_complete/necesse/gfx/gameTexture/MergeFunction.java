/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTexture;

import java.awt.Color;
import necesse.engine.util.GameMath;

@FunctionalInterface
public interface MergeFunction {
    public static final MergeFunction GLBLEND = (curCol, mergeCol) -> {
        int red = (int)((float)curCol.getRed() / 255.0f * ((float)mergeCol.getRed() / 255.0f) * 255.0f);
        int green = (int)((float)curCol.getGreen() / 255.0f * ((float)mergeCol.getGreen() / 255.0f) * 255.0f);
        int blue = (int)((float)curCol.getBlue() / 255.0f * ((float)mergeCol.getBlue() / 255.0f) * 255.0f);
        return new Color(red, green, blue, curCol.getAlpha());
    };
    public static final MergeFunction NORMAL = (curCol, mergeCol) -> {
        float cRed = (float)curCol.getRed() / 255.0f;
        float cGreen = (float)curCol.getGreen() / 255.0f;
        float cBlue = (float)curCol.getBlue() / 255.0f;
        float cAlpha = (float)curCol.getAlpha() / 255.0f;
        float mRed = (float)mergeCol.getRed() / 255.0f;
        float mGreen = (float)mergeCol.getGreen() / 255.0f;
        float mBlue = (float)mergeCol.getBlue() / 255.0f;
        float mAlpha = (float)mergeCol.getAlpha() / 255.0f;
        float ai = cAlpha * (1.0f - mAlpha);
        float ao = mAlpha + ai;
        int red = (int)((mRed * mAlpha + cRed * ai) / ao * 255.0f);
        int green = (int)((mGreen * mAlpha + cGreen * ai) / ao * 255.0f);
        int blue = (int)((mBlue * mAlpha + cBlue * ai) / ao * 255.0f);
        int alpha = (int)(ao * 255.0f);
        return new Color(red, green, blue, alpha);
    };
    public static final MergeFunction ALPHA_OVERRIDE = (curCol, mergeCol) -> new Color(curCol.getRed(), curCol.getGreen(), curCol.getBlue(), mergeCol.getAlpha());
    public static final MergeFunction MULTIPLY = (curCol, mergeCol) -> {
        float cRed = (float)curCol.getRed() / 255.0f;
        float cGreen = (float)curCol.getGreen() / 255.0f;
        float cBlue = (float)curCol.getBlue() / 255.0f;
        float cAlpha = (float)curCol.getAlpha() / 255.0f;
        float mRed = (float)mergeCol.getRed() / 255.0f;
        float mGreen = (float)mergeCol.getGreen() / 255.0f;
        float mBlue = (float)mergeCol.getBlue() / 255.0f;
        float mAlpha = (float)mergeCol.getAlpha() / 255.0f;
        return new Color(cRed * mRed, cGreen * mGreen, cBlue * mBlue, cAlpha * mAlpha);
    };
    public static final MergeFunction ALPHA_MASK = (curCol, mergeCol) -> {
        float brightness = (float)GameMath.max(mergeCol.getRed(), mergeCol.getGreen(), mergeCol.getBlue()) / 255.0f;
        return new Color(curCol.getRed(), curCol.getGreen(), curCol.getBlue(), (int)((float)curCol.getAlpha() * brightness));
    };

    public Color merge(Color var1, Color var2);
}

