/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderBind;
import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class ShaderTexture {
    public final int pos;
    public final GameTexture texture;
    protected float spriteX1;
    protected float spriteY1;
    protected float spriteX2;
    protected float spriteY2;

    public ShaderTexture(int pos, GameTexture texture) {
        this.pos = pos;
        this.texture = texture;
        this.spriteX1 = 0.0f;
        this.spriteX2 = 1.0f;
        this.spriteY1 = 0.0f;
        this.spriteY2 = 1.0f;
    }

    public ShaderTexture(int pos, GameTexture texture, float spriteX1, float spriteX2, float spriteY1, float spriteY2) {
        this(pos, texture);
        this.spriteX1 = spriteX1;
        this.spriteX2 = spriteX2;
        this.spriteY1 = spriteY1;
        this.spriteY2 = spriteY2;
    }

    public ShaderTexture(int pos, GameSprite sprite) {
        this(pos, sprite.texture);
        this.spriteX1 = TextureDrawOptions.pixel(sprite.spriteX, sprite.spriteWidth, sprite.texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(sprite.spriteY, sprite.spriteHeight, sprite.texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(sprite.spriteX + 1, sprite.spriteWidth, sprite.texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(sprite.spriteY + 1, sprite.spriteHeight, sprite.texture.getHeight());
    }

    public ShaderTexture(int pos, GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        this(pos, texture);
        this.spriteX1 = TextureDrawOptions.pixel(spriteX, spriteRes, texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(spriteY, spriteRes, texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(spriteX + 1, spriteRes, texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(spriteY + 1, spriteRes, texture.getHeight());
    }

    public ShaderTexture(int pos, GameTexture texture, int spriteX, int spriteY, int spriteRes, int startX, int endX, int startY, int endY) {
        this(pos, texture);
        this.spriteX1 = TextureDrawOptions.pixel(spriteX, startX, spriteRes, texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(spriteY, startY, spriteRes, texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(spriteX, endX, spriteRes, texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(spriteY, endY, spriteRes, texture.getHeight());
    }

    public ShaderSprite toSprite() {
        return new ShaderSprite(this.pos, this.spriteX1, this.spriteX2, this.spriteY1, this.spriteY2);
    }

    public ShaderBind toBind() {
        return new ShaderBind(this.pos, this.texture);
    }
}

