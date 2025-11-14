/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import necesse.engine.save.LoadData;
import necesse.engine.util.PointHashSet;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.mapData.DiscoveredMapData;

public class ClientDiscoveredMapRegion {
    public static final int TILES_PER_MAP_BITS = 8;
    public static final int TILES_PER_MAP = 256;
    public final int mapRegionX;
    public final int mapRegionY;
    public final int tileXOffset;
    public final int tileYOffset;
    private final DiscoveredMapData data;
    private int cacheColorsBuffer;
    private boolean shouldSave;
    private final String texturePrefix;
    private GameTexture texture;
    private PointHashSet textureUpdates;

    public ClientDiscoveredMapRegion(int mapRegionX, int mapRegionY, int tileXOffset, int tileYOffset, int tileWidth, int tileHeight, String texturePrefix) {
        this.mapRegionX = mapRegionX;
        this.mapRegionY = mapRegionY;
        this.tileXOffset = tileXOffset;
        this.tileYOffset = tileYOffset;
        this.data = new DiscoveredMapData(tileWidth, tileHeight){

            @Override
            public boolean ensureNotFinal() {
                ClientDiscoveredMapRegion.this.cacheColorsBuffer = 0;
                if (super.ensureNotFinal()) {
                    ClientDiscoveredMapRegion.this.createTextureIfNeeded(true);
                    return true;
                }
                return false;
            }
        };
        this.texturePrefix = texturePrefix;
        this.createTextureIfNeeded(false);
    }

    public int getTileWidth() {
        return this.data.getTileWidth();
    }

    public int getTileHeight() {
        return this.data.getTileHeight();
    }

    public void tickCache() {
        if (this.data.isFinal()) {
            return;
        }
        ++this.cacheColorsBuffer;
        if (this.cacheColorsBuffer >= 1200) {
            this.cacheColorsBuffer = 0;
            this.data.makeFinal();
            if (this.texture != null) {
                this.texture.delete();
            }
            this.texture = null;
            this.textureUpdates = null;
        }
    }

    protected void createTextureIfNeeded(boolean forceTextureUpdate) {
        if (this.texturePrefix != null) {
            if (this.texture != null) {
                this.texture.delete();
            }
            this.texture = new GameTexture("discoveredMap_" + this.texturePrefix + "_" + this.mapRegionX + "x" + this.mapRegionY, this.getTileWidth(), this.getTileHeight());
            this.texture.setBlendQuality(GameTexture.BlendQuality.NEAREST);
            this.textureUpdates = null;
            if (forceTextureUpdate) {
                this.tickTextureUpdate();
            }
        }
    }

    public void saveToFileSystem(File file) throws IOException {
        this.data.saveToFileSystem(file, this.mapRegionX, this.mapRegionY);
    }

    public void loadFromCache(File file) throws DataFormatException, IOException {
        if (!file.exists()) {
            return;
        }
        LoadData save = new LoadData(file);
        this.data.applySaveData(save, false);
        this.textureUpdates = null;
    }

    public boolean setRGB(int mapRegionTileX, int mapRegionTileY, int rgb) {
        this.data.ensureNotFinal();
        if (this.data.setRGB(mapRegionTileX, mapRegionTileY, rgb)) {
            if (rgb != 0) {
                this.shouldSave = true;
            }
            if (this.textureUpdates != null) {
                this.textureUpdates.add(mapRegionTileX, mapRegionTileY);
            }
            return true;
        }
        return false;
    }

    public int getRGB(int mapRegionTileX, int mapRegionTileY) {
        this.data.ensureNotFinal();
        return this.data.getRGB(mapRegionTileX, mapRegionTileY);
    }

    public void tickTextureUpdate() {
        if (this.texture == null) {
            return;
        }
        if (this.textureUpdates == null) {
            this.textureUpdates = new PointHashSet();
            for (int x = 0; x < this.texture.getWidth(); ++x) {
                for (int y = 0; y < this.texture.getHeight(); ++y) {
                    Color color = new Color(this.getRGB(x, y));
                    this.texture.setColor(x, y, color);
                }
            }
        } else {
            for (Point p : this.textureUpdates) {
                int rgb = this.getRGB(p.x, p.y);
                Color newColor = new Color(rgb);
                Color textureColor = this.texture.getColor(p.x, p.y);
                if (textureColor.equals(newColor)) continue;
                this.texture.setColor(p.x, p.y, newColor);
            }
        }
        this.textureUpdates.clear();
    }

    public boolean combine(DiscoveredMapData data) {
        if (data.combine(data)) {
            this.createTextureIfNeeded(false);
            return true;
        }
        return false;
    }

    public GameTexture getTexture() {
        this.data.ensureNotFinal();
        return this.texture;
    }

    public boolean shouldSave() {
        return this.shouldSave;
    }

    public void markSaved() {
        this.shouldSave = false;
    }

    public void dispose() {
        if (this.texture != null) {
            this.texture.delete();
            this.texture = null;
        }
    }
}

