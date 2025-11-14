/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTexture;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;

public class GameSprite {
    public final GameTexture texture;
    public final int spriteX;
    public final int spriteY;
    public final int spriteWidth;
    public final int spriteHeight;
    public final int width;
    public final int height;
    public final boolean mirrorX;
    public final boolean mirrorY;

    public GameSprite(GameTexture texture, int spriteX, int spriteY, int spriteWidth, int spriteHeight, int width, int height, boolean mirrorX, boolean mirrorY) {
        this.texture = texture;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.width = width;
        this.height = height;
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
    }

    public GameSprite(GameTexture texture, int spriteX, int spriteY, int spriteWidth, int spriteHeight, int width, int height) {
        this(texture, spriteX, spriteY, spriteWidth, spriteHeight, width, height, false, false);
    }

    public GameSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes, int width, int height) {
        this(texture, spriteX, spriteY, spriteRes, spriteRes, width, height);
    }

    public GameSprite(GameSprite copy, int width, int height) {
        this(copy.texture, copy.spriteX, copy.spriteY, copy.spriteWidth, copy.spriteHeight, width, height);
    }

    public GameSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes, int size) {
        this(texture, spriteX, spriteY, spriteRes, size, size);
    }

    public GameSprite(GameSprite copy, int size) {
        this(copy.texture, copy.spriteX, copy.spriteY, copy.spriteWidth, copy.spriteHeight, copy.width < copy.height ? (int)((float)size * ((float)copy.width / (float)copy.height)) : size, copy.height < copy.width ? (int)((float)size * ((float)copy.height / (float)copy.width)) : size);
    }

    public GameSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        this(texture, spriteX, spriteY, spriteRes, spriteRes, spriteRes);
    }

    public GameSprite(GameTexture texture, int size) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight(), texture.getWidth() < texture.getHeight() ? (int)((float)size * ((float)texture.getWidth() / (float)texture.getHeight())) : size, texture.getHeight() < texture.getWidth() ? (int)((float)size * ((float)texture.getHeight() / (float)texture.getWidth())) : size);
    }

    public GameSprite(GameTexture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight(), texture.getWidth(), texture.getHeight());
    }

    public GameSprite mirrorX() {
        return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, !this.mirrorX, this.mirrorY);
    }

    public GameSprite mirrorY() {
        return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, this.mirrorX, !this.mirrorY);
    }

    public GameSprite mirrored(boolean mirrorX, boolean mirrorY) {
        return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, mirrorX != this.mirrorX, mirrorY != this.mirrorY);
    }

    public TextureDrawOptionsEnd initDraw() {
        return this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
    }

    public TextureDrawOptionsEnd initDrawSection(int startX, int endX, int startY, int endY, boolean translatePos) {
        return this.texture.initDraw().spriteSection(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, startX, endX, startY, endY, translatePos).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
    }

    public TextureDrawOptionsEnd initDrawSection(int startX, int endX, int startY, int endY) {
        return this.texture.initDraw().spriteSection(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, startX, endX, startY, endY).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
    }

    public GameSprite subSprite(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        if (this.spriteWidth % spriteWidth != 0) {
            throw new IllegalArgumentException("Super spriteWidth must be divisible by new spriteWidth");
        }
        if (this.spriteHeight % spriteHeight != 0) {
            throw new IllegalArgumentException("Super spriteHeight must be divisible by new spriteHeight");
        }
        int spriteXRatio = this.spriteWidth / spriteWidth;
        int spriteYRatio = this.spriteHeight / spriteHeight;
        return new GameSprite(this.texture, this.spriteX * spriteXRatio + spriteX, this.spriteY * spriteYRatio + spriteY, spriteWidth, spriteHeight);
    }

    public GameSprite subSprite(int spriteX, int spriteY, int spriteRes) {
        return this.subSprite(spriteX, spriteY, spriteRes, spriteRes);
    }
}

