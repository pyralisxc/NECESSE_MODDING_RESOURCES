/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.io.IOException;
import necesse.engine.util.PointSetAbstract;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.HUD;

public class GameBackgroundTextures {
    public int edgeResolution;
    public int edgeMargin;
    public int contentPadding;
    public TextureGetterRaw backgroundTextureLoader;
    public TextureGetterRaw edgeTextureLoader;
    public GameTexture backgroundTexture;
    public GameTexture edgeTexture;
    public Color centerColor;

    public GameBackgroundTextures(int edgeResolution, int edgeMargin, int contentPadding, TextureGetterRaw backgroundTextureLoader, TextureGetterRaw edgeTextureLoader) {
        this.edgeResolution = edgeResolution;
        this.edgeMargin = edgeMargin;
        this.contentPadding = contentPadding;
        this.backgroundTextureLoader = backgroundTextureLoader;
        this.edgeTextureLoader = edgeTextureLoader;
    }

    public void loadTextures() {
        try {
            this.backgroundTexture = this.backgroundTextureLoader.get();
        }
        catch (IOException e) {
            this.backgroundTexture = null;
        }
        try {
            this.edgeTexture = this.edgeTextureLoader.get();
        }
        catch (IOException e) {
            this.edgeTexture = null;
        }
    }

    public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
        if (this.backgroundTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getOutlinesDrawOptions(this.backgroundTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
        if (this.backgroundTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getCenterDrawOptions(this.backgroundTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
        if (this.backgroundTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getBackgroundDrawOptions(this.backgroundTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
        if (this.edgeTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getOutlinesDrawOptions(this.edgeTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
        if (this.edgeTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getCenterDrawOptions(this.edgeTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
        if (this.edgeTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        return HUD.getBackgroundDrawOptions(this.edgeTexture, this.edgeResolution, x - this.edgeMargin, y - this.edgeMargin, width + this.edgeMargin * 2, height + this.edgeMargin * 2);
    }

    public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
        if (this.backgroundTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        int offset = this.edgeResolution * 2 - this.edgeMargin * 2;
        return HUD.getBackgroundEdged(this.backgroundTexture, this.edgeResolution, offset - xPadding * 2, offset - yPadding * 2, x + xPadding, y + yPadding, tiles, tileWidth, tileHeight);
    }

    public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
        if (this.edgeTexture == null) {
            return new SharedTextureDrawOptions(GameResources.error);
        }
        int offset = this.edgeResolution * 2 - this.edgeMargin * 2;
        return HUD.getBackgroundEdged(this.edgeTexture, this.edgeResolution, offset - xPadding * 2, offset - yPadding * 2, x + xPadding, y + yPadding, tiles, tileWidth, tileHeight);
    }

    public Color getCenterColor() {
        if (this.centerColor != null) {
            return this.centerColor;
        }
        if (this.backgroundTexture.isFinal()) {
            this.backgroundTexture.restoreFinal();
        }
        int textureStartX = 0;
        int textureStartY = this.edgeResolution * 4;
        int textureWidth = this.backgroundTexture.getWidth() - textureStartX;
        int textureHeight = this.backgroundTexture.getHeight() - textureStartY;
        return this.backgroundTexture.getColor(textureStartX + textureWidth / 2, textureStartY + textureHeight / 2);
    }

    @FunctionalInterface
    public static interface TextureGetterRaw {
        public GameTexture get() throws IOException;
    }
}

