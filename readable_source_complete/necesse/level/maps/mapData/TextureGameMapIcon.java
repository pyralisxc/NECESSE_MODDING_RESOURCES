/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.mapData.GameMapIcon;

public class TextureGameMapIcon
extends GameMapIcon {
    protected String texturePath;
    protected GameTexture texture;
    protected int drawYOffset;

    public TextureGameMapIcon(String texturePath, int drawYOffset) {
        this.texturePath = texturePath;
        this.drawYOffset = drawYOffset;
    }

    public TextureGameMapIcon(String texturePath) {
        this(texturePath, 0);
    }

    @Override
    public void loadTextures() {
        this.texture = GameTexture.fromFile(this.texturePath);
    }

    @Override
    public Rectangle getDrawBoundingBox() {
        return new Rectangle(-this.texture.getWidth() / 2, -this.texture.getHeight() / 2 + this.drawYOffset, this.texture.getWidth(), this.texture.getHeight());
    }

    @Override
    public void drawIcon(int drawX, int drawY, Color color) {
        this.texture.initDraw().color(color).posMiddle(drawX, drawY + this.drawYOffset).draw();
    }
}

