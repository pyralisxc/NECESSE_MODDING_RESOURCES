/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderIntVariable;
import necesse.level.maps.Level;

public class LiquidShader
extends GameShader {
    public LiquidShader() {
        super("vertLiquid", "fragLiquid");
        this.addVariable(new ShaderIntVariable("showLiquidTexture", 0, 6));
    }

    public void use(Level level) {
        super.use();
        this.pass1i("smoothTexture", 1);
        this.pass1i("nearestTexture", 2);
        this.pass1f("saltWaterSinkRate", level.getLiquidSaltWaterSinkRate());
        this.pass1f("freshWaterSinkRate", level.getLiquidFreshWaterSinkRate());
    }
}

