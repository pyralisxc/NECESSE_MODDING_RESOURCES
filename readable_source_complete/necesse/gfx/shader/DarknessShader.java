/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderFloatVariable;
import necesse.gfx.shader.shaderVariable.ShaderIntVariable;

public class DarknessShader
extends GameShader {
    public int midScreenX;
    public int midScreenY;

    public DarknessShader() {
        super("vert", "fragDarkness");
        this.addVariable(new ShaderFloatVariable("intensity", 0.0f, 1.0f, 100));
        this.addVariable(new ShaderIntVariable("range", 0, 1000));
    }

    @Override
    public void use() {
        super.use();
        GameWindow window = WindowManager.getWindow();
        this.pass1i("screenWidth", window.getSceneWidth());
        this.pass1i("screenHeight", window.getSceneHeight());
        this.pass1i("midX", this.midScreenX);
        this.pass1i("midY", this.midScreenY);
    }
}

