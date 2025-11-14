/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;

public class SpriteTooltip
implements GameTooltips {
    public final GameTexture texture;
    public final int spriteX;
    public final int spriteY;
    public final int spriteRes;
    public final int width;
    public final int height;

    public SpriteTooltip(GameTexture texture, int spriteX, int spriteY, int spriteRes, int width, int height) {
        this.texture = texture;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.spriteRes = spriteRes;
        this.width = width;
        this.height = height;
    }

    public SpriteTooltip(GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        this(texture, spriteX, spriteY, spriteRes, spriteRes, spriteRes);
    }

    public SpriteTooltip(GameTexture texture) {
        this(texture, 0, 0, texture.getWidth());
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).draw(x, y);
    }

    @Override
    public int getDrawOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean matchesSearch(String search) {
        return false;
    }
}

