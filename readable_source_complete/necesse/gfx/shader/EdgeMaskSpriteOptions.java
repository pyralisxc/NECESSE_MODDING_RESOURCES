/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.shader.EdgeMaskOptions;
import necesse.gfx.shader.EdgeMaskShader;

public class EdgeMaskSpriteOptions
implements EdgeMaskOptions {
    public final GameSprite sprite;
    public final int xOffset;
    public final int yOffset;

    public EdgeMaskSpriteOptions(GameSprite sprite, int xOffset, int yOffset) {
        this.sprite = sprite;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public TextureDrawOptionsEnd apply(TextureDrawOptionsEnd drawOptions, int texturePos) {
        return drawOptions.addShaderTextureFit(this.sprite, texturePos);
    }

    @Override
    public void use(EdgeMaskShader shader, int pos) {
        shader.passOffset(pos, this.xOffset, this.yOffset);
        float spriteX1 = TextureDrawOptions.pixel(this.sprite.spriteX, this.sprite.spriteWidth, this.sprite.texture.getWidth());
        float spriteY1 = TextureDrawOptions.pixel(this.sprite.spriteY, this.sprite.spriteHeight, this.sprite.texture.getHeight());
        float spriteX2 = TextureDrawOptions.pixel(this.sprite.spriteX + 1, this.sprite.spriteWidth, this.sprite.texture.getWidth()) - 1.0f / (float)this.sprite.texture.getWidth();
        float spriteY2 = TextureDrawOptions.pixel(this.sprite.spriteY + 1, this.sprite.spriteHeight, this.sprite.texture.getHeight()) - 1.0f / (float)this.sprite.texture.getHeight();
        shader.passSprite(pos, spriteX1, spriteY1, spriteX2, spriteY2);
        shader.passSize(pos, this.sprite.texture.getWidth(), this.sprite.texture.getHeight());
    }
}

