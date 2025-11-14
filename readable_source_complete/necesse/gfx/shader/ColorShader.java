/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderFloatVariable;

public class ColorShader
extends GameShader {
    public ColorShader() {
        super("vert", "fragColor");
        this.addVariable(new ShaderFloatVariable("brightness", 0.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("contrast", 0.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("gamma", 0.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("vibrance", -2.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("vibranceRedBalance", 0.0f, 1.0f, 100));
        this.addVariable(new ShaderFloatVariable("vibranceGreenBalance", 0.0f, 1.0f, 100));
        this.addVariable(new ShaderFloatVariable("vibranceBlueBalance", 0.0f, 1.0f, 100));
        this.addVariable(new ShaderFloatVariable("red", 0.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("green", 0.0f, 2.0f, 100));
        this.addVariable(new ShaderFloatVariable("blue", 0.0f, 2.0f, 100));
    }
}

