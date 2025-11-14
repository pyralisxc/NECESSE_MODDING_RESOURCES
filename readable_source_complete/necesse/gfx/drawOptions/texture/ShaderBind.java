/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.gameTexture.GameTexture;

public class ShaderBind {
    public final int glPos;
    public final GameTexture texture;
    public static final int[] glTexturePositions = new int[]{33984, 33985, 33986, 33987, 33988, 33989, 33990, 33991, 33992, 33993, 33994, 33995, 33996, 33997, 33998, 33999, 34000, 34001, 34002, 34003, 34004, 34005, 34006, 34007, 34008, 34009, 34010, 34011, 34012, 34013, 34014, 34015};

    public ShaderBind(int pos, GameTexture texture) {
        this.glPos = ShaderBind.toGlPos(pos);
        this.texture = texture;
    }

    public static int toGlPos(int pos) {
        if (pos >= 0 && pos < glTexturePositions.length) {
            return glTexturePositions[pos];
        }
        System.err.println("Could not find shader texture position " + pos);
        return 33984;
    }

    public void bind() {
        this.texture.bindTexture(this.glPos);
    }
}

