/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.EdgeMaskOptions;
import necesse.gfx.shader.EdgeMaskShader;

public class EdgeMaskTextureOptions
implements EdgeMaskOptions {
    public final GameTexture texture;
    public final int xOffset;
    public final int yOffset;

    public EdgeMaskTextureOptions(GameTexture texture, int xOffset, int yOffset) {
        this.texture = texture;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public TextureDrawOptionsEnd apply(TextureDrawOptionsEnd drawOptions, int texturePos) {
        return drawOptions.addShaderTextureFitCenterX(this.texture, texturePos);
    }

    @Override
    public void use(EdgeMaskShader shader, int pos) {
        shader.passOffset(pos, this.xOffset, this.yOffset);
        shader.passSprite(pos, 0.0f, 0.0f, 1.0f, 1.0f);
        if (this.texture != null) {
            shader.passSize(pos, this.texture.getWidth(), this.texture.getHeight());
        } else {
            shader.passSize(pos, 1, 1);
        }
    }
}

