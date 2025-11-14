/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;

public class PixelScalingShader
extends GameShader {
    public PixelScalingShader() {
        super("vert", "fragPixelScaling");
    }

    public void use(float smoothingFactor, float textureWidth, float textureHeight) {
        this.use();
        this.pass1f("smoothing_factor", smoothingFactor);
        this.pass2f("texture_pixel_size", 1.0f / textureWidth, 1.0f / textureHeight);
    }
}

