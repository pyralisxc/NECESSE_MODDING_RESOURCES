/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;

public class TextureDrawOptionsStart
extends TextureDrawOptionsEnd {
    TextureDrawOptionsStart(TextureDrawOptions other) {
        super(other);
    }

    @Override
    public TextureDrawOptionsStart copy() {
        return new TextureDrawOptionsStart(super.copy());
    }

    public TextureDrawOptionsEnd section(int startX, int endX, int startY, int endY) {
        this.opts.spriteX1 = TextureDrawOptionsStart.pixel(startX, this.texture.getWidth());
        this.opts.spriteX3 = this.opts.spriteX2 = TextureDrawOptionsStart.pixel(endX, this.texture.getWidth());
        this.opts.spriteX4 = this.opts.spriteX1;
        this.opts.spriteY2 = this.opts.spriteY1 = TextureDrawOptionsStart.pixel(startY, this.texture.getHeight());
        this.opts.spriteY4 = this.opts.spriteY3 = TextureDrawOptionsStart.pixel(endY, this.texture.getHeight());
        this.opts.width = Math.abs(endX - startX);
        this.opts.height = Math.abs(endY - startY);
        return new TextureDrawOptionsEnd(this);
    }

    public TextureDrawOptionsEnd sprite(int spriteX, int spriteY, int spriteRes) {
        return this.sprite(spriteX, spriteY, spriteRes, spriteRes);
    }

    public TextureDrawOptionsEnd sprite(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        this.opts.spriteX1 = TextureDrawOptionsStart.pixel(spriteX, spriteWidth, this.texture.getWidth());
        this.opts.spriteX3 = this.opts.spriteX2 = TextureDrawOptionsStart.pixel(spriteX + 1, spriteWidth, this.texture.getWidth());
        this.opts.spriteX4 = this.opts.spriteX1;
        this.opts.spriteY2 = this.opts.spriteY1 = TextureDrawOptionsStart.pixel(spriteY, spriteHeight, this.texture.getHeight());
        this.opts.spriteY4 = this.opts.spriteY3 = TextureDrawOptionsStart.pixel(spriteY + 1, spriteHeight, this.texture.getHeight());
        this.opts.width = spriteWidth;
        this.opts.height = spriteHeight;
        return new TextureDrawOptionsEnd(this);
    }

    public TextureDrawOptionsEnd spriteSection(int spriteX, int spriteY, int spriteWidth, int spriteHeight, int startX, int endX, int startY, int endY, boolean translatePos) {
        this.opts.spriteX1 = TextureDrawOptionsStart.pixel(spriteX, startX, spriteWidth, this.texture.getWidth());
        this.opts.spriteX3 = this.opts.spriteX2 = TextureDrawOptionsStart.pixel(spriteX, endX, spriteWidth, this.texture.getWidth());
        this.opts.spriteX4 = this.opts.spriteX1;
        this.opts.spriteY2 = this.opts.spriteY1 = TextureDrawOptionsStart.pixel(spriteY, startY, spriteHeight, this.texture.getHeight());
        this.opts.spriteY4 = this.opts.spriteY3 = TextureDrawOptionsStart.pixel(spriteY, endY, spriteHeight, this.texture.getHeight());
        if (translatePos) {
            this.opts.translateX = startX;
            this.opts.translateY = startY;
        }
        this.opts.width = Math.abs(endX - startX);
        this.opts.height = Math.abs(endY - startY);
        return new TextureDrawOptionsEnd(this);
    }

    public TextureDrawOptionsEnd spriteSection(int spriteX, int spriteY, int spriteRes, int startX, int endX, int startY, int endY, boolean translatePos) {
        return this.spriteSection(spriteX, spriteY, spriteRes, spriteRes, startX, endX, startY, endY, translatePos);
    }

    public TextureDrawOptionsEnd spriteSection(int spriteX, int spriteY, int spriteWidth, int spriteHeight, int startX, int endX, int startY, int endY) {
        return this.spriteSection(spriteX, spriteY, spriteWidth, spriteHeight, startX, endX, startY, endY, true);
    }

    public TextureDrawOptionsEnd spriteSection(int spriteX, int spriteY, int spriteRes, int startX, int endX, int startY, int endY) {
        return this.spriteSection(spriteX, spriteY, spriteRes, spriteRes, startX, endX, startY, endY);
    }

    public TextureDrawOptionsEnd next() {
        return new TextureDrawOptionsEnd(this);
    }
}

