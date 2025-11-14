/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;

public class SharpenShader
extends GameShader {
    public SharpenShader() {
        super("vert", "fragSharpen");
    }

    public void use(int width, int height, float amount) {
        this.use();
        this.pass1i("renderWidth", width);
        this.pass1i("renderHeight", height);
        this.pass1f("amount", amount);
    }
}

