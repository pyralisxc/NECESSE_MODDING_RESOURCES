/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderFloatVariable;

public class WhiteShader
extends GameShader {
    public WhiteShader() {
        super("vert", "fragWhite");
        this.addVariable(new ShaderFloatVariable("white", 0.0f, 1.0f, 100));
    }
}

