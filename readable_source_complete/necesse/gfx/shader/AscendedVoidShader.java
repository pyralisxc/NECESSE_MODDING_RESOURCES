/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;

public class AscendedVoidShader
extends GameShader {
    public AscendedVoidShader() {
        super("vertAscendedVoid", "fragAscendedVoid");
    }

    @Override
    public void use() {
        super.use();
        this.pass1i("terrainTexture", 0);
        this.pass1i("parallaxTexture", 1);
    }

    public void passOffset(float textureStartX, float textureStartY, float sizeX, float sizeY, float xOffset, float yOffset) {
        this.pass2f("parallaxMin", textureStartX, textureStartY);
        this.pass2f("parallaxSize", sizeX, sizeY);
        this.pass2f("parallaxOffset", xOffset, yOffset);
    }
}

