/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.engine.window.WindowManager;
import necesse.gfx.shader.GameShader;

public class RectangleShader
extends GameShader {
    public RectangleShader() {
        super("vert", "fragRectangle");
    }

    public void use(int x, int y, int width, int height) {
        this.use();
        y = Math.abs(y - WindowManager.getWindow().getCurrentBuffer().getHeight() + height);
        this.pass1i("x", x);
        this.pass1i("y", y);
        this.pass1i("w", width);
        this.pass1i("h", height);
    }
}

