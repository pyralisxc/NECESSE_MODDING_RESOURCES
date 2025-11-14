/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.shader.EdgeMaskShader;

public interface EdgeMaskOptions {
    public TextureDrawOptionsEnd apply(TextureDrawOptionsEnd var1, int var2);

    public void use(EdgeMaskShader var1, int var2);
}

