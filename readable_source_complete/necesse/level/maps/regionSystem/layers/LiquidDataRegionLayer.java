/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import necesse.engine.GameRandomNoise;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;
import necesse.level.maps.regionSystem.layers.RegionLayer;

public class LiquidDataRegionLayer
extends RegionLayer {
    public byte[][] heights;
    public boolean[][] shores;
    public byte[][] saltWaterDepth;
    private GameTexture smoothTexture;
    private GameTexture nearestTexture;

    public LiquidDataRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
        this.heights = new byte[this.region.tileWidth][this.region.tileHeight];
        this.shores = new boolean[this.region.tileWidth][this.region.tileHeight];
        this.saltWaterDepth = new byte[this.region.tileWidth][this.region.tileHeight];
    }

    @Override
    public void onLayerLoaded() {
        this.generateTextures();
        this.calculateFull();
    }

    @Override
    public void onLoadingComplete() {
        this.generateTextures();
        this.calculateFull();
    }

    @Override
    public void onLayerUnloaded() {
        this.disposeTextures();
    }

    @Override
    public void onDispose() {
        super.onDispose();
        this.disposeTextures();
    }

    public void disposeTextures() {
        GameTexture ref;
        if (this.smoothTexture != null) {
            ref = this.smoothTexture;
            this.level.addGLContextRunnable(ref::delete);
        }
        this.smoothTexture = null;
        if (this.nearestTexture != null) {
            ref = this.nearestTexture;
            this.level.addGLContextRunnable(ref::delete);
        }
        this.nearestTexture = null;
    }

    public int getHeightByRegion(int regionTileX, int regionTileY) {
        return this.heights[regionTileX][regionTileY];
    }

    public boolean isFreshWaterByRegion(int regionTileX, int regionTileY) {
        return !this.isSaltWaterByRegion(regionTileX, regionTileY);
    }

    public boolean isSaltWaterByRegion(int regionTileX, int regionTileY) {
        return this.saltWaterDepth[regionTileX][regionTileY] > 0;
    }

    public int getSaltWaterDepthByRegion(int regionTileX, int regionTileY) {
        return this.saltWaterDepth[regionTileX][regionTileY];
    }

    public boolean isShoreByRegion(int regionTileX, int regionTileY) {
        return this.shores[regionTileX][regionTileY];
    }

    protected void generateTextures() {
        if (this.level.isServer()) {
            return;
        }
        this.disposeTextures();
        this.smoothTexture = new GameTexture("liquidManagerSmooth " + this.level.getIdentifier().stringID + "_" + this.region.regionX + "_" + this.region.regionY, this.region.tileWidth + 2, this.region.tileHeight + 2);
        this.nearestTexture = new GameTexture("liquidManagerNearest " + this.level.getIdentifier().stringID + "_" + this.region.regionX + "_" + this.region.regionY, this.region.tileWidth + 2, this.region.tileHeight + 2);
        this.nearestTexture.setBlendQuality(GameTexture.BlendQuality.NEAREST);
        for (int x = 0; x < this.region.tileWidth + 2; ++x) {
            for (int y = 0; y < this.region.tileHeight + 2; ++y) {
                this.smoothTexture.setAlpha(x, y, 255);
                this.nearestTexture.setAlpha(x, y, 255);
            }
        }
        this.generateTextureNoise(this.level.getSeed(), 10.0f);
    }

    private void generateTextureNoise(long seed, float resolution) {
        GameRandom random = new GameRandom(seed);
        double offset = random.nextDouble() * (double)resolution * (double)random.nextInt(1000);
        GameRandomNoise noise = new GameRandomNoise(random.nextInt());
        for (int regionTileX = -1; regionTileX < this.region.tileWidth + 1; ++regionTileX) {
            for (int regionTileY = -1; regionTileY < this.region.tileHeight + 1; ++regionTileY) {
                float cutOff;
                int tileX = regionTileX + this.region.tileXOffset;
                int tileY = regionTileY + this.region.tileYOffset;
                double perlinValue = noise.perlin2(offset + (double)tileX / 300.0 * (double)resolution, offset + (double)tileY / 300.0 * (double)resolution);
                double percent = GameMath.map(perlinValue, -1.0, 1.0, 0.0, 1.0);
                if (percent > (double)(cutOff = 0.6f)) {
                    percent = GameMath.map(percent, (double)cutOff, 1.0, 0.0, 1.0);
                    percent = Math.pow(percent, 0.5);
                } else {
                    percent = 0.0;
                }
                int gray = (int)GameMath.lerp(percent, 0L, 255L);
                this.smoothTexture.setRed(regionTileX + 1, regionTileY + 1, gray);
            }
        }
    }

    public void calculateFull() {
        if (!this.level.isServer() && this.smoothTexture == null) {
            this.generateTextures();
        }
        this.level.liquidManager.updateLevel(this.region, this.region.tileXOffset - 1, this.region.tileYOffset - 1, this.region.tileXOffset + this.region.tileWidth + 1, this.region.tileYOffset + this.region.tileHeight + 1, true, false);
    }

    public void updateShoresFull() {
        this.updateShoresByRegion(0, 0, this.region.tileWidth - 1, this.region.tileHeight - 1);
    }

    public void updateShoresByRegion(int regionStartTileX, int regionStartTileY, int regionEndTileX, int regionEndTileY) {
        boolean recordConstant = this.level.debugLoadingPerformance != null;
        PerformanceTimerManager tickManager = this.level.debugLoadingPerformance != null ? this.level.debugLoadingPerformance : this.level.tickManager();
        Performance.record(tickManager, "updateShores", recordConstant, () -> {
            for (int regionTileX = regionStartTileX; regionTileX <= regionEndTileX; ++regionTileX) {
                for (int regionTileY = regionStartTileY; regionTileY <= regionEndTileY; ++regionTileY) {
                    this.updateIsShoreByRegion(regionTileX, regionTileY);
                }
            }
        });
    }

    public boolean updateIsShoreByRegion(int regionTileX, int regionTileY) {
        boolean isShore;
        int tileX = regionTileX + this.region.tileXOffset;
        int tileY = regionTileY + this.region.tileYOffset;
        if (this.region.tileLayer.isTileLiquidByRegion(regionTileX, regionTileY)) {
            isShore = false;
        } else {
            this.saltWaterDepth[regionTileX][regionTileY] = 0;
            RegionBoundsExecutor executor = new RegionBoundsExecutor(this.manager, tileX - 1, tileY - 1, tileX + 1, tileY + 1, false);
            isShore = executor.streamCoordinates().anyMatch(tilePosition -> {
                if (tilePosition.regionTileX == regionTileX && tilePosition.regionTileY == regionTileY) {
                    return false;
                }
                return ((Region)tilePosition.region).tileLayer.isTileLiquidByRegion(tilePosition.regionTileX, tilePosition.regionTileY);
            });
        }
        if (this.shores[regionTileX][regionTileY] != isShore) {
            this.shores[regionTileX][regionTileY] = isShore;
            this.level.liquidManager.queueTextureUpdate(tileX, tileY);
        }
        return isShore;
    }

    public void updateTextureByRegion(int textureX, int textureY, int smoothGreen, int smoothBlue, int nearestRed, int nearestGreen, int nearestBlue) {
        if (this.smoothTexture == null || this.nearestTexture == null) {
            return;
        }
        if (this.smoothTexture.getGreen(textureX, textureY) != smoothGreen) {
            this.smoothTexture.setGreen(textureX, textureY, smoothGreen);
        }
        if (this.smoothTexture.getBlue(textureX, textureY) != smoothBlue) {
            this.smoothTexture.setBlue(textureX, textureY, smoothBlue);
        }
        if (this.nearestTexture.getRed(textureX, textureY) != nearestRed) {
            this.nearestTexture.setRed(textureX, textureY, nearestRed);
        }
        if (this.nearestTexture.getGreen(textureX, textureY) != nearestGreen) {
            this.nearestTexture.setGreen(textureX, textureY, nearestGreen);
        }
        if (this.nearestTexture.getBlue(textureX, textureY) != nearestBlue) {
            this.nearestTexture.setBlue(textureX, textureY, nearestBlue);
        }
    }

    public GameTexture getSmoothTexture() {
        return this.smoothTexture;
    }

    public GameTexture getNearestTexture() {
        return this.nearestTexture;
    }
}

