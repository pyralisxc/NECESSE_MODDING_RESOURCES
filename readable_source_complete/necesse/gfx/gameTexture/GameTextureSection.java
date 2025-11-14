/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTexture;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;

public class GameTextureSection {
    protected GameTexture texture;
    protected int startX;
    protected int startY;
    protected int endX;
    protected int endY;

    protected GameTextureSection() {
        this.texture = null;
    }

    public GameTextureSection(GameTexture texture, int startX, int endX, int startY, int endY) {
        this.texture = texture;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    public GameTextureSection(GameTexture texture) {
        this(texture, 0, texture.getWidth(), 0, texture.getHeight());
    }

    public boolean isGenerated() {
        return this.texture != null;
    }

    public GameTexture getTexture() {
        return this.texture;
    }

    public String toString() {
        return super.toString() + "[" + this.texture + ", " + this.startX + ", " + this.endX + ", " + this.startY + ", " + this.endY + "]";
    }

    public TextureDrawOptionsEnd initDraw() {
        return this.texture.initDraw().section(this.startX, this.endX, this.startY, this.endY);
    }

    public GameTextureSection sprite(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        int spriteStartX = this.startX + spriteX * spriteWidth;
        int spriteStartY = this.startY + spriteY * spriteHeight;
        return new GameTextureSection(this.texture, spriteStartX, spriteStartX + spriteWidth, spriteStartY, spriteStartY + spriteHeight);
    }

    public GameTextureSection sprite(int spriteX, int spriteY, int spriteRes) {
        return this.sprite(spriteX, spriteY, spriteRes, spriteRes);
    }

    public GameTextureSection section(int startX, int endX, int startY, int endY) {
        return new GameTextureSection(this.texture, this.startX + startX, this.startX + endX, this.startY + startY, this.startY + endY);
    }

    public GameTexture createNewTexture() {
        GameTexture newTexture = new GameTexture(this.texture.debugName + " section copy", this.getWidth(), this.getHeight());
        newTexture.copy(this.texture, 0, 0, this.startX, this.startY, newTexture.getWidth(), newTexture.getHeight());
        return newTexture;
    }

    public int getWidth() {
        return this.endX - this.startX;
    }

    public int getHeight() {
        return this.endY - this.startY;
    }

    public int getStartX() {
        return this.startX;
    }

    public int getStartY() {
        return this.startY;
    }

    public int getEndX() {
        return this.endX;
    }

    public int getEndY() {
        return this.endY;
    }

    public float getStartXFloat() {
        return (float)this.startX / (float)this.texture.getWidth();
    }

    public float getStartYFloat() {
        return (float)this.startY / (float)this.texture.getHeight();
    }

    public float getEndXFloat() {
        return (float)this.endX / (float)this.texture.getWidth();
    }

    public float getEndYFloat() {
        return (float)this.endY / (float)this.texture.getHeight();
    }
}

