/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;

public class TextureOffsetShader
extends GameShader {
    public TextureOffsetShader() {
        super("vert", "fragTextureOffset");
    }

    public void use(float xOffset, float yOffset) {
        this.use();
        this.pass1f("textureXOffset", xOffset);
        this.pass1f("textureYOffset", yOffset);
    }
}

