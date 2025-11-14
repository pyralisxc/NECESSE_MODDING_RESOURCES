/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.regionSystem.Region;

public class LevelTileLiquidRegionDrawOptions
extends SharedTextureDrawOptions {
    private final int regionTexWidth;
    private final int regionTexHeight;
    private final int regionTileOffsetX;
    private final int regionTileOffsetY;

    public LevelTileLiquidRegionDrawOptions(Region region) {
        super(GameTile.generatedTileTexture);
        GameTexture nearestTexture;
        this.regionTexWidth = region.tileWidth + 2;
        this.regionTexHeight = region.tileHeight + 2;
        this.regionTileOffsetX = -region.tileXOffset + 1;
        this.regionTileOffsetY = -region.tileYOffset + 1;
        GameTexture smoothTexture = region.liquidData.getSmoothTexture();
        if (smoothTexture != null) {
            this.addShaderBind(1, smoothTexture);
        }
        if ((nearestTexture = region.liquidData.getNearestTexture()) != null) {
            this.addShaderBind(2, nearestTexture);
        }
    }

    public ShaderSprite getShaderSprite(int tileX, int tileY) {
        int textureX = tileX + this.regionTileOffsetX;
        int textureY = tileY + this.regionTileOffsetY;
        return new ShaderSprite(4, TextureDrawOptions.pixel(textureX, this.regionTexWidth), TextureDrawOptions.pixel(textureX + 1, this.regionTexWidth), TextureDrawOptions.pixel(textureY, this.regionTexHeight), TextureDrawOptions.pixel(textureY + 1, this.regionTexHeight));
    }

    public ShaderSprite getSubShaderSprite(float tileX, float tileY, int subSpriteX, int subSpriteY) {
        float textureX = tileX + (float)this.regionTileOffsetX + (float)subSpriteX * 0.5f;
        float textureY = tileY + (float)this.regionTileOffsetY + (float)subSpriteY * 0.5f;
        return new ShaderSprite(4, TextureDrawOptions.pixelFloat(textureX, this.regionTexWidth), TextureDrawOptions.pixelFloat(textureX + 0.5f, this.regionTexWidth), TextureDrawOptions.pixelFloat(textureY, this.regionTexHeight), TextureDrawOptions.pixelFloat(textureY + 0.5f, this.regionTexHeight));
    }
}

