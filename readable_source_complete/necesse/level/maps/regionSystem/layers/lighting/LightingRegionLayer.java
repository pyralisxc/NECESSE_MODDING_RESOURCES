/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers.lighting;

import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.GameParticleLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.SourcedGameLight;
import necesse.level.maps.light.SourcedLightModifier;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;
import necesse.level.maps.regionSystem.layers.RegionLayer;

public class LightingRegionLayer
extends RegionLayer {
    protected final LinkedList<SourcedGameLight>[][] staticSources;
    protected final GameLight[][] staticLights;
    protected final GameParticleLight[][] particleLights;
    protected final LinkedList<SourcedLightModifier>[][] insideSources;
    protected final HashMap<Integer, Boolean> insideSourceIsValid;
    protected byte[][] finalInsideValues;

    public LightingRegionLayer(Region region) {
        super(region);
        this.staticSources = new LinkedList[region.tileWidth][region.tileHeight];
        this.staticLights = new GameLight[region.tileWidth][region.tileHeight];
        if (!region.manager.level.isCave) {
            this.insideSources = new LinkedList[region.tileWidth][region.tileHeight];
            this.finalInsideValues = new byte[region.tileWidth][region.tileHeight];
            this.insideSourceIsValid = new HashMap();
        } else {
            this.insideSources = null;
            this.finalInsideValues = null;
            this.insideSourceIsValid = null;
        }
        this.particleLights = !region.manager.level.isServer() ? new GameParticleLight[region.tileWidth][region.tileHeight] : null;
    }

    @Override
    public void init() {
    }

    @Override
    public void onLayerLoaded() {
        this.region.updateLight();
    }

    @Override
    public void onLoadingComplete() {
        this.region.updateLight();
    }

    @Override
    public void onLayerUnloaded() {
    }

    public void resetFinalLights() {
        if (this.finalInsideValues != null) {
            this.insideSourceIsValid.clear();
            this.finalInsideValues = new byte[this.region.tileWidth][this.region.tileHeight];
        }
    }

    public GameLight getStaticLight(int regionTileX, int regionTileY) {
        return this.staticLights[regionTileX][regionTileY];
    }

    public void setStaticLightByRegion(int regionTileX, int regionTileY, GameLight light) {
        this.staticLights[regionTileX][regionTileY] = light;
    }

    public LinkedList<SourcedGameLight> getStaticSourcesByRegion(int regionTileX, int regionTileY) {
        return this.staticSources[regionTileX][regionTileY];
    }

    public void clearStaticSourcesByRegion(int regionTileX, int regionTileY) {
        this.staticSources[regionTileX][regionTileY] = null;
    }

    public boolean hasNoBetterSameStaticSourceByRegion(int regionTileX, int regionTileY, SourcedGameLight source) {
        LinkedList<SourcedGameLight> sources = this.staticSources[regionTileX][regionTileY];
        if (sources == null) {
            return true;
        }
        return sources.stream().filter(e -> e.sourceTileX == source.sourceTileX && e.sourceTileY == source.sourceTileY).noneMatch(e -> e.light.getLevel() >= source.light.getLevel());
    }

    public boolean hasNoBetterSameStaticColorByRegion(int regionTileX, int regionTileY, SourcedGameLight source) {
        LinkedList<SourcedGameLight> sources = this.staticSources[regionTileX][regionTileY];
        if (sources == null) {
            return true;
        }
        return sources.stream().filter(e -> e.sourceTileX != source.sourceTileX || e.sourceTileY != source.sourceTileY).noneMatch(e -> e.light.getColorHash() == source.light.getColorHash() && e.light.getLevel() >= source.light.getLevel());
    }

    public void addStaticSourcedLightByRegion(int regionTileX, int regionTileY, SourcedGameLight sourcedLight, PointHashSet shouldUpdates) {
        int tileX = regionTileX + this.region.tileXOffset;
        int tileY = regionTileY + this.region.tileYOffset;
        LinkedList<SourcedGameLight> sources = this.staticSources[regionTileX][regionTileY];
        if (sources == null) {
            sources = new LinkedList();
            this.staticSources[regionTileX][regionTileY] = sources;
        }
        sources.removeIf(e -> {
            if (e.light.getColorHash() != sourcedLight.light.getColorHash()) {
                return false;
            }
            if (e.light.getLevel() > sourcedLight.light.getLevel()) {
                return false;
            }
            if (sourcedLight.sourceTileX == tileX && sourcedLight.sourceTileY == tileY) {
                return true;
            }
            return e.sourceTileX != tileX || e.sourceTileY != tileY;
        });
        sources.add(sourcedLight);
        shouldUpdates.add(tileX, tileY);
    }

    public LinkedList<SourcedLightModifier> getInsideSourcesByRegion(int regionTileX, int regionTileY) {
        return this.insideSources[regionTileX][regionTileY];
    }

    public LinkedList<SourcedLightModifier> getOrCreateInsideSourcesByRegion(int regionTileX, int regionTileY) {
        LinkedList<SourcedLightModifier> sources = this.insideSources[regionTileX][regionTileY];
        if (sources == null) {
            this.insideSources[regionTileX][regionTileY] = sources = new LinkedList();
        }
        return sources;
    }

    public void clearInsideSourcesByRegion(int regionTileX, int regionTileY) {
        this.insideSources[regionTileX][regionTileY] = null;
    }

    public float getInsideModifierByRegion(int regionTileX, int regionTileY, RegionBoundsExecutor bounds) {
        if (this.level.isCave) {
            return 1.0f;
        }
        return (float)this.getOrCalculateInsideValueByRegion(regionTileX, regionTileY, bounds) / 150.0f;
    }

    private int getOrCalculateInsideValueByRegion(int regionTileX, int regionTileY, RegionBoundsExecutor bounds) {
        byte value = this.finalInsideValues[regionTileX][regionTileY];
        if (value == 0) {
            LinkedList<SourcedLightModifier> sources = this.insideSources[regionTileX][regionTileY];
            if (sources == null || sources.isEmpty()) {
                value = 1;
            } else {
                int bestValue = 0;
                for (SourcedLightModifier source : sources) {
                    int currentValue = source.getValue();
                    if (currentValue <= bestValue || !this.isSourceValidByTile(source.sourceX, source.sourceY, bounds)) continue;
                    bestValue = currentValue;
                }
                value = (byte)(bestValue + 1);
            }
            this.finalInsideValues[regionTileX][regionTileY] = value;
        }
        return value - 1 & 0xFF;
    }

    private boolean isSourceValidByTile(int tileX, int tileY, RegionBoundsExecutor bounds) {
        int regionTileX = tileX - this.region.tileXOffset;
        int regionTileY = tileY - this.region.tileYOffset;
        return this.insideSourceIsValid.compute(GameMath.getUniqueIntKey(regionTileX, regionTileY), (key, last) -> {
            if (last == null) {
                return this.isOutside(bounds, tileX - 1, tileY) || this.isOutside(bounds, tileX + 1, tileY) || this.isOutside(bounds, tileX, tileY - 1) || this.isOutside(bounds, tileX, tileY + 1);
            }
            return last;
        });
    }

    protected boolean isOutside(RegionBoundsExecutor regions, int tileX, int tileY) {
        if (!regions.isInsideBounds(tileX, tileY)) {
            return true;
        }
        Region region = (Region)regions.getRegionByTile(tileX, tileY);
        if (region == null) {
            return true;
        }
        return region.subRegionData.isOutsideByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public GameLight getParticleLightByRegion(int regionTileX, int regionTileY) {
        GameParticleLight light = this.particleLights[regionTileX][regionTileY];
        if (light != null) {
            light.updateLevel(this.level.getLocalTime());
            return light.light;
        }
        return null;
    }

    public void refreshParticleLightByRegion(int regionTileX, int regionTileY, LightManager manager, GameLight color, int lightLevel) {
        long currentTime = this.level.getLocalTime();
        int fadeTime = (int)(750.0f * ((float)lightLevel / 255.0f));
        long endTime = currentTime + (long)fadeTime;
        GameParticleLight light = this.particleLights[regionTileX][regionTileY];
        color.setLevel(lightLevel);
        if (light == null) {
            light = new GameParticleLight(manager.newLight(0.0f));
            light.light.combine(color);
            light.endTime = Math.max(light.endTime, endTime);
            this.particleLights[regionTileX][regionTileY] = light;
        } else {
            light.updateLevel(currentTime);
            light.light.combine(color);
            light.endTime = Math.max(light.endTime, endTime);
        }
    }
}

