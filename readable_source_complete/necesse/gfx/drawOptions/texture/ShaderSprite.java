/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import org.lwjgl.opengl.GL13;

public class ShaderSprite
extends ShaderSpriteAbstract {
    protected float spriteX1;
    protected float spriteY1;
    protected float spriteX2;
    protected float spriteY2;

    public ShaderSprite(int pos, float spriteX1, float spriteX2, float spriteY1, float spriteY2) {
        super(pos);
        this.spriteX1 = spriteX1;
        this.spriteX2 = spriteX2;
        this.spriteY1 = spriteY1;
        this.spriteY2 = spriteY2;
    }

    public ShaderSprite(int pos, GameTexture texture, int startX, int endX, int startY, int endY) {
        super(pos);
        this.spriteX1 = TextureDrawOptions.pixel(startX, texture.getWidth());
        this.spriteX2 = TextureDrawOptions.pixel(endX, texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(startY, texture.getHeight());
        this.spriteY2 = TextureDrawOptions.pixel(endY, texture.getHeight());
    }

    public ShaderSprite(int pos, GameTextureSection section) {
        super(pos);
        GameTexture texture = section.getTexture();
        this.spriteX1 = TextureDrawOptions.pixel(section.getStartX(), texture.getWidth());
        this.spriteX2 = TextureDrawOptions.pixel(section.getEndX(), texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(section.getStartY(), texture.getHeight());
        this.spriteY2 = TextureDrawOptions.pixel(section.getEndY(), texture.getHeight());
    }

    public ShaderSprite(int pos, GameSprite sprite) {
        super(pos);
        this.spriteX1 = TextureDrawOptions.pixel(sprite.spriteX, sprite.spriteWidth, sprite.texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(sprite.spriteY, sprite.spriteHeight, sprite.texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(sprite.spriteX + 1, sprite.spriteWidth, sprite.texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(sprite.spriteY + 1, sprite.spriteHeight, sprite.texture.getHeight());
    }

    public ShaderSprite(int pos, GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        super(pos);
        this.spriteX1 = TextureDrawOptions.pixel(spriteX, spriteRes, texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(spriteY, spriteRes, texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(spriteX + 1, spriteRes, texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(spriteY + 1, spriteRes, texture.getHeight());
    }

    public ShaderSprite(int pos, GameTexture texture, int spriteX, int spriteY, int spriteRes, int startX, int endX, int startY, int endY) {
        super(pos);
        this.spriteX1 = TextureDrawOptions.pixel(spriteX, startX, spriteRes, texture.getWidth());
        this.spriteY1 = TextureDrawOptions.pixel(spriteY, startY, spriteRes, texture.getHeight());
        this.spriteX2 = TextureDrawOptions.pixel(spriteX, endX, spriteRes, texture.getWidth());
        this.spriteY2 = TextureDrawOptions.pixel(spriteY, endY, spriteRes, texture.getHeight());
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord2f((int)this.glPos, (float)this.spriteX1, (float)this.spriteY1);
    }

    @Override
    public void startTopRight() {
        GL13.glMultiTexCoord2f((int)this.glPos, (float)this.spriteX2, (float)this.spriteY1);
    }

    @Override
    public void startBotRight() {
        GL13.glMultiTexCoord2f((int)this.glPos, (float)this.spriteX2, (float)this.spriteY2);
    }

    @Override
    public void startBotLeft() {
        GL13.glMultiTexCoord2f((int)this.glPos, (float)this.spriteX1, (float)this.spriteY2);
    }
}

