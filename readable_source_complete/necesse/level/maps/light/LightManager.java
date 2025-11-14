/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.Settings;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.GameLightColor;
import necesse.level.maps.light.InsideLightUpdater;
import necesse.level.maps.light.LightArea;
import necesse.level.maps.light.ParticleLightMap;
import necesse.level.maps.light.SourcedGameLight;
import necesse.level.maps.light.SourcedLightModifier;
import necesse.level.maps.light.StaticLightUpdater;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;

public class LightManager {
    public static final int fullLight = 255;
    public static final int maxLight = 150;
    public static final int oneThirdsLight = 50;
    public static final int twoThirdsLight = 100;
    public static final int solidObjectMod = 40;
    public static final int airMod = 10;
    public static final int lightDistance = 25;
    public static final float shadowLightMod = 0.7f;
    public static final float insideLightMod = 0.5f;
    public static final int particleLightFadeTime = 750;
    public static final int particleLightStartLevel = 255;
    public static final int particleLightDistance = 15;
    public Settings.LightSetting setting = Settings.lights;
    protected ExecutorService updateExecutor;
    private static final int COMPUTE_THREADS = 4;
    protected ThreadPoolExecutor computeExecutor;
    public final Object sourcesLock = new Object();
    public final Object lightsLock = new Object();
    public final Object particlesLock = new Object();
    public final Level level;
    private RegionBoundsExecutor drawRegionBounds;
    private LightArea drawLights;
    private LightArea drawWallLights;
    private ParticleLightMap particleLightLevels;
    public GameLight ambientLightOverride = null;
    public GameLight ambientLight = this.newLight(0.0f);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LightManager(Level level) {
        LightManager lightManager = this;
        synchronized (lightManager) {
            this.level = level;
            this.updateExecutor = Executors.newSingleThreadExecutor(this.defaultThreadFactory("update", false));
            this.computeExecutor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), this.defaultThreadFactory("compute", true));
        }
    }

    public Settings.LightSetting getCurrentSetting() {
        return this.setting;
    }

    public void ensureSetting(Settings.LightSetting setting) {
        if (this.setting != setting) {
            this.setting = setting;
            this.level.regionManager.forEachLoadedRegions(Region::updateLight);
        }
    }

    private ThreadFactory defaultThreadFactory(String type, boolean appendCount) {
        AtomicInteger threadNum = new AtomicInteger(0);
        return r -> {
            Thread thread = new Thread(null, r, "level-" + this.level.getHostString() + "-" + this.level.getIdentifier() + "-light-" + type + (appendCount ? "-" + threadNum.incrementAndGet() : ""));
            thread.setDaemon(true);
            return thread;
        };
    }

    public GameLight newLight(float level) {
        if (this.setting == Settings.LightSetting.Color) {
            return new GameLightColor(level);
        }
        return new GameLight(level);
    }

    public GameLight newLight(float hue, float saturation, float level) {
        if (this.setting == Settings.LightSetting.Color) {
            return new GameLightColor(hue, saturation, level);
        }
        return new GameLight(level);
    }

    public GameLight newLight(Color color, float saturation, float level) {
        if (this.setting == Settings.LightSetting.Color) {
            return GameLightColor.fromColor(color, saturation, level);
        }
        return new GameLight(level);
    }

    public float getAmbientLight() {
        return this.ambientLight.getLevel();
    }

    public void updateAmbientLight() {
        if (this.ambientLightOverride != null) {
            this.ambientLight = this.ambientLightOverride;
            return;
        }
        if (Settings.alwaysLight) {
            this.ambientLight = this.newLight(150.0f);
            return;
        }
        if (this.level.isCave) {
            this.ambientLight = this.newLight(0.0f, 0.0f, 0.0f);
        } else {
            float minLight;
            float ambientLight = this.level.getWorldEntity().getAmbientLight();
            float minLightMod = 1.0f;
            if (Settings.brightness > 0.7f) {
                minLightMod *= GameMath.lerp((float)Math.pow(Settings.brightness - 0.7f, 0.3f), 1.0f, 0.4f);
            }
            if (ambientLight < (minLight = 150.0f / (10.0f * minLightMod))) {
                ambientLight = minLight;
            }
            float ambientFloat = Math.abs(ambientLight / 150.0f - 1.0f);
            ambientFloat = (float)Math.pow(ambientFloat, 2.0);
            this.ambientLight = this.newLight(240.0f, ambientFloat * 0.85f, ambientLight);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public float getInsideModifier(int tileX, int tileY) {
        RegionBoundsExecutor regions = new RegionBoundsExecutor(this.level.regionManager, tileX - 1, tileY - 1, tileX + 1, tileY + 1, false);
        Region region = (Region)regions.getRegionByTile(tileX, tileY);
        if (region == null) {
            return 0.0f;
        }
        LightManager lightManager = this;
        synchronized (lightManager) {
            return region.lightLayer.getInsideModifierByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, regions);
        }
    }

    public List<SourcedLightModifier> getInsideLightSources(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return Collections.emptyList();
        }
        return region.lightLayer.getInsideSourcesByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<SourcedGameLight> getStaticLightSources(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return Collections.emptyList();
        }
        Object object = this.sourcesLock;
        synchronized (object) {
            LinkedList<SourcedGameLight> sources = region.lightLayer.getStaticSourcesByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
            if (sources == null) {
                return Collections.emptyList();
            }
            return new LinkedList<SourcedGameLight>(sources);
        }
    }

    public GameLight getStaticLightByRegion(Region region, int regionTileX, int regionTileY) {
        GameLight light = region.lightLayer.getStaticLight(regionTileX, regionTileY);
        if (light == null) {
            return this.newLight(0.0f);
        }
        return light;
    }

    public GameLight getStaticLight(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return this.newLight(0.0f);
        }
        return this.getStaticLightByRegion(region, tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public GameLight getParticleLight(int tileX, int tileY) {
        if (this.particleLightLevels == null) {
            return this.newLight(0.0f);
        }
        return this.particleLightLevels.getLight(tileX, tileY);
    }

    public void refreshParticleLight(int tileX, int tileY) {
        this.refreshParticleLight(tileX, tileY, 100);
    }

    public void refreshParticleLight(int tileX, int tileY, int lightLevel) {
        this.refreshParticleLight(tileX, tileY, this.newLight(0.0f), lightLevel);
    }

    public void refreshParticleLight(int tileX, int tileY, float hue, float saturation) {
        this.refreshParticleLight(tileX, tileY, hue, saturation, 100);
    }

    public void refreshParticleLight(int tileX, int tileY, float hue, float saturation, int lightLevel) {
        this.refreshParticleLight(tileX, tileY, this.newLight(hue, saturation, 0.0f), lightLevel);
    }

    public void refreshParticleLight(int tileX, int tileY, Color color, float saturation) {
        this.refreshParticleLight(tileX, tileY, color, saturation, 100);
    }

    public void refreshParticleLight(int tileX, int tileY, Color color, float saturation, int lightLevel) {
        this.refreshParticleLight(tileX, tileY, this.newLight(color, saturation, 0.0f), lightLevel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void refreshParticleLight(int tileX, int tileY, GameLight color, int lightLevel) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return;
        }
        Object object = this.particlesLock;
        synchronized (object) {
            region.lightLayer.refreshParticleLightByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, this, color, lightLevel);
        }
    }

    public void refreshParticleLightFloat(float x, float y) {
        this.refreshParticleLightFloat(x, y, 100);
    }

    public void refreshParticleLightFloat(float x, float y, int lightLevel) {
        this.refreshParticleLightFloat(x, y, this.newLight(0.0f), lightLevel);
    }

    public void refreshParticleLightFloat(float x, float y, float hue, float saturation) {
        this.refreshParticleLightFloat(x, y, hue, saturation, 100);
    }

    public void refreshParticleLightFloat(float x, float y, float hue, float saturation, int lightLevel) {
        this.refreshParticleLightFloat(x, y, this.newLight(hue, saturation, 0.0f), lightLevel);
    }

    public void refreshParticleLightFloat(float x, float y, Color color, float saturation) {
        this.refreshParticleLightFloat(x, y, color, saturation, 100);
    }

    public void refreshParticleLightFloat(float x, float y, Color color, float saturation, int lightLevel) {
        this.refreshParticleLightFloat(x, y, this.newLight(color, saturation, 0.0f), lightLevel);
    }

    private void refreshParticleLightFloat(float x, float y, GameLight color, int lightLevel) {
        this.refreshParticleLight(GameMath.getTileCoordinate((int)x), GameMath.getTileCoordinate((int)y), color, lightLevel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private GameLight getAmbientAndStaticLightLevelByRegion(Region region, int regionTileX, int regionTileY, RegionBoundsExecutor regions) {
        GameLight light = this.ambientLight.copy();
        if (!this.level.isCave && !region.subRegionData.isOutsideByRegion(regionTileX, regionTileY)) {
            float newLightLevel = light.getLevel() * 0.5f;
            float deltaLightLevel = newLightLevel - light.getLevel();
            LightManager lightManager = this;
            synchronized (lightManager) {
            }
            light.setLevel(light.getLevel() + (deltaLightLevel *= 1.0f - region.lightLayer.getInsideModifierByRegion(regionTileX, regionTileY, regions)));
        }
        light.combine(this.getStaticLightByRegion(region, regionTileX, regionTileY));
        return light;
    }

    public GameLight getAmbientAndStaticLightLevel(int tileX, int tileY) {
        RegionBoundsExecutor regions = new RegionBoundsExecutor(this.level.regionManager, tileX - 1, tileY - 1, tileX + 1, tileY + 1, false);
        Region region = (Region)regions.getRegionByTile(tileX, tileY);
        if (region == null) {
            return this.newLight(0.0f);
        }
        return this.getAmbientAndStaticLightLevelByRegion(region, tileX - region.tileXOffset, tileY - region.tileYOffset, regions);
    }

    public float getAmbientAndStaticLightLevelFloat(int tileX, int tileY) {
        float lightLevel = this.ambientLight.level;
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region != null) {
            int regionTileX = tileX - region.tileXOffset;
            int regionTileY = tileY - region.tileYOffset;
            GameLight staticLight = this.getStaticLightByRegion(region, regionTileX, regionTileY);
            lightLevel = GameMath.max(lightLevel, staticLight.level);
        }
        return lightLevel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLight getLightLevel(int tileX, int tileY) {
        if (this.drawLights == null) {
            return this.newLight(0.0f);
        }
        tileX = GameMath.limit(tileX, this.drawLights.startX, this.drawLights.endX);
        tileY = GameMath.limit(tileY, this.drawLights.startY, this.drawLights.endY);
        LightManager lightManager = this;
        synchronized (lightManager) {
            GameLight light = this.drawLights.lights[this.drawLights.getIndex(tileX, tileY)];
            if (light == null) {
                Region region = (Region)this.drawRegionBounds.getRegionByTile(tileX, tileY);
                if (region != null) {
                    int regionTileX = tileX - region.tileXOffset;
                    int regionTileY = tileY - region.tileYOffset;
                    light = this.getAmbientAndStaticLightLevelByRegion(region, regionTileX, regionTileY, this.drawRegionBounds);
                    light.combine(this.particleLightLevels.getLight(tileX, tileY));
                } else {
                    light = this.newLight(0.0f);
                }
                this.drawLights.lights[this.drawLights.getIndex((int)tileX, (int)tileY)] = light;
            }
            return light;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLight getLightLevelWall(int tileX, int tileY) {
        if (this.drawWallLights == null) {
            return this.newLight(0.0f);
        }
        tileX = GameMath.limit(tileX, this.drawWallLights.startX, this.drawWallLights.endX);
        tileY = GameMath.limit(tileY, this.drawWallLights.startY, this.drawWallLights.endY);
        LightManager lightManager = this;
        synchronized (lightManager) {
            GameLight light = this.drawWallLights.lights[this.drawWallLights.getIndex(tileX, tileY)];
            if (light == null) {
                Region region = (Region)this.drawRegionBounds.getRegionByTile(tileX, tileY);
                if (region != null) {
                    int regionTileX = tileX - region.tileXOffset;
                    int regionTileY = tileY - region.tileYOffset;
                    light = this.ambientLight.copy();
                    if (!(this.level.isCave || region.subRegionData.isOutsideByRegion(regionTileX, regionTileY) || this.isOutside(this.drawRegionBounds, tileX - 1, tileY) || this.isOutside(this.drawRegionBounds, tileX + 1, tileY) || this.isOutside(this.drawRegionBounds, tileX, tileY - 1) || this.isOutside(this.drawRegionBounds, tileX, tileY + 1))) {
                        light.setLevel(light.getLevel() * 0.5f);
                    }
                    light.combine(this.getStaticLightByRegion(region, regionTileX, regionTileY));
                    light.combine(this.particleLightLevels.getLight(tileX, tileY));
                } else {
                    light = this.newLight(0.0f);
                }
                this.drawWallLights.lights[this.drawWallLights.getIndex((int)tileX, (int)tileY)] = light;
            }
            return light;
        }
    }

    public void setDrawArea(int startTileX, int startTileY, int endTileX, int endTileY) {
        ParticleLightMap particleLightMap = new ParticleLightMap(this.level.lightManager, startTileX - 15, startTileY - 15, endTileX + 15, endTileY + 15);
        if (!this.level.isServer()) {
            particleLightMap.update(false);
        }
        this.particleLightLevels = particleLightMap;
        int drawRegionBoundsStartTileX = this.level.limitTileXToBounds(startTileX - 1);
        int drawRegionBoundsStartTileY = this.level.limitTileYToBounds(startTileY - 1);
        int drawRegionBoundsEndTileX = this.level.limitTileXToBounds(endTileX + 1);
        int drawRegionBoundsEndTileY = this.level.limitTileYToBounds(endTileY + 1);
        RegionBoundsExecutor drawRegionBounds = new RegionBoundsExecutor(this.level.regionManager, drawRegionBoundsStartTileX, drawRegionBoundsStartTileY, drawRegionBoundsEndTileX, drawRegionBoundsEndTileY, false);
        drawRegionBounds.calculateAllRegions();
        LightArea drawLights = new LightArea(startTileX, startTileY, endTileX, endTileY);
        drawLights.initLights();
        LightArea drawWallLights = new LightArea(startTileX, startTileY, endTileX, endTileY);
        drawWallLights.initLights();
        this.drawRegionBounds = drawRegionBounds;
        this.drawLights = drawLights;
        this.drawWallLights = drawWallLights;
    }

    private boolean isOutside(RegionBoundsExecutor executor, int tileX, int tileY) {
        if (!executor.isInsideBounds(tileX, tileY)) {
            return true;
        }
        return executor.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.subRegionData.isOutsideByRegion(regionTileX, regionTileY), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateStaticLight(int tileX, int tileY) {
        StaticLightUpdater staticUpdater = new StaticLightUpdater(this, tileX, tileY, tileX, tileY);
        LightManager lightManager = this;
        synchronized (lightManager) {
            staticUpdater.runUpdate(true);
        }
        if (!this.level.isCave) {
            InsideLightUpdater insideUpdater = new InsideLightUpdater(this, tileX, tileY, tileX, tileY);
            LightManager lightManager2 = this;
            synchronized (lightManager2) {
                insideUpdater.runUpdate();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateStaticLight(int startTileX, int startTileY, int endTileX, int endTileY, boolean concurrently) {
        StaticLightUpdater updater = new StaticLightUpdater(this, startTileX, startTileY, endTileX, endTileY);
        LightManager lightManager = this;
        synchronized (lightManager) {
            updater.runUpdate(concurrently);
        }
        if (!this.level.isCave) {
            InsideLightUpdater insideUpdater = new InsideLightUpdater(this, startTileX, startTileY, endTileX, endTileY);
            LightManager lightManager2 = this;
            synchronized (lightManager2) {
                insideUpdater.runUpdate();
            }
        }
    }

    public void dispose() {
        if (this.updateExecutor != null) {
            this.updateExecutor.shutdownNow();
        }
        this.updateExecutor = null;
        if (this.computeExecutor != null) {
            this.computeExecutor.shutdownNow();
        }
        this.computeExecutor = null;
    }

    public boolean isDisposed() {
        return this.updateExecutor == null || this.computeExecutor == null;
    }
}

