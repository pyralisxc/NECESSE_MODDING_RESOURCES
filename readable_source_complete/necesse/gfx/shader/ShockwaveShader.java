/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderFloatVariable;

public class ShockwaveShader
extends GameShader {
    public ShockwaveShader(boolean addVariables) {
        super("vert", "fragShockwave");
        if (addVariables) {
            this.addVariable(new ShaderFloatVariable("waveDistance", 0.0f, 1000.0f, 100));
            this.addVariable(new ShaderFloatVariable("waveSize", 0.0f, 200.0f, 100));
            this.addVariable(new ShaderFloatVariable("easingScale", -1.0f, 2.0f, 100));
            this.addVariable(new ShaderFloatVariable("easingPower", -5.0f, 5.0f, 100));
        }
    }

    @Override
    public void use() {
        super.use();
        GameWindow window = WindowManager.getWindow();
        this.pass1i("screenWidth", window.getSceneWidth());
        this.pass1i("screenHeight", window.getSceneHeight());
    }
}

