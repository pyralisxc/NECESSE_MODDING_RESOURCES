/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import necesse.gfx.gameTexture.GameTexture;

public interface TextureBinder {
    public static final TextureBinder NO_TEXTURE = GameTexture::unbindTexture;

    default public void bindTexture() {
        this.bindTexture(33984);
    }

    public void bindTexture(int var1);
}

