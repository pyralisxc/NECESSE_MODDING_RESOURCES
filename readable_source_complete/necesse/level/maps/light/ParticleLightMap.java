/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightArea;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.ParticleLightCompute;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;

public class ParticleLightMap {
    public boolean printDebug;
    protected final LightManager manager;
    protected final Level level;
    protected final int startTileX;
    protected final int startTileY;
    protected final int endTileX;
    protected final int endTileY;
    protected final RegionBoundsExecutor regions;
    protected final LightArea area;

    public ParticleLightMap(LightManager manager, int startTileX, int startTileY, int endTileX, int endTileY) {
        this.manager = manager;
        this.level = manager.level;
        this.startTileX = this.level.limitTileXToBounds(startTileX);
        this.startTileY = this.level.limitTileYToBounds(startTileY);
        this.endTileX = this.level.limitTileXToBounds(endTileX);
        this.endTileY = this.level.limitTileYToBounds(endTileY);
        this.regions = new RegionBoundsExecutor(this.level.regionManager, this.level.limitTileXToBounds(startTileX - 15), this.level.limitTileYToBounds(startTileY - 15), this.level.limitTileXToBounds(endTileX + 15), this.level.limitTileYToBounds(endTileY + 15), false);
        this.area = new LightArea(startTileX, startTileY, endTileX, endTileY);
        this.area.initLights();
        for (int i = 0; i < this.area.lights.length; ++i) {
            this.area.lights[i] = manager.newLight(0.0f);
        }
    }

    public int getLightModifier(int tileX, int tileY) {
        return this.regions.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> Math.max(10, region.objectLayer.getObjectByRegion(0, regionTileX, regionTileY).getLightLevelMod(this.level, regionTileX + region.tileXOffset, regionTileY + region.tileYOffset)), 10);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLight getLight(int tileX, int tileY) {
        tileX = GameMath.limit(tileX, this.area.startX, this.area.endX);
        tileY = GameMath.limit(tileY, this.area.startY, this.area.endY);
        LightArea lightArea = this.area;
        synchronized (lightArea) {
            return this.area.lights[this.area.getIndex(tileX, tileY)];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(boolean concurrently) {
        LightArea updateArea;
        if (!this.level.isLoadingComplete()) {
            return;
        }
        long time = System.nanoTime();
        LinkedList<LightComputeFuture> computes = new LinkedList<LightComputeFuture>();
        ParticleLightCompute edgeCompute = new ParticleLightCompute(this, this.startTileX, this.startTileY, this.endTileX, this.endTileY);
        computes.add(new LightComputeFuture(edgeCompute));
        LightArea lightArea = updateArea = concurrently ? new LightArea(this.startTileX, this.startTileY, this.endTileX, this.endTileY) : this.area;
        synchronized (lightArea) {
            updateArea.initLights();
            for (int tileX = this.startTileX; tileX <= this.endTileX; ++tileX) {
                for (int tileY = this.startTileY; tileY <= this.endTileY; ++tileY) {
                    if (!this.regions.isTileLoaded(tileX, tileY)) continue;
                    if (tileX == this.startTileX || tileX == this.endTileX || tileY == this.startTileY || tileY == this.endTileY) {
                        GameLight currentLight;
                        Object object = this.manager.particlesLock;
                        synchronized (object) {
                            currentLight = this.regions.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.lightLayer.getParticleLightByRegion(regionTileX, regionTileY), null);
                        }
                        if (currentLight == null) {
                            currentLight = this.manager.newLight(0.0f);
                        }
                        if (currentLight.getLevel() > 0.0f) {
                            edgeCompute.addSource(tileX, tileY, currentLight);
                        }
                    } else {
                        GameLight newLight;
                        Object object = this.manager.particlesLock;
                        synchronized (object) {
                            newLight = this.regions.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.lightLayer.getParticleLightByRegion(regionTileX, regionTileY), null);
                        }
                        if (newLight != null && newLight.getLevel() > 0.0f) {
                            int maxLightDistance = (int)(newLight.getLevel() / 10.0f) + 1;
                            int cStartX = Math.max(this.startTileX, tileX - maxLightDistance);
                            int cStartY = Math.max(this.startTileY, tileY - maxLightDistance);
                            int cEndX = Math.min(this.endTileX, tileX + maxLightDistance);
                            int cEndY = Math.min(this.endTileY, tileY + maxLightDistance);
                            ParticleLightCompute compute = new ParticleLightCompute(this, cStartX, cStartY, cEndX, cEndY);
                            compute.addSource(tileX, tileY, newLight);
                            computes.add(new LightComputeFuture(compute));
                        }
                    }
                    updateArea.lights[updateArea.getIndex((int)tileX, (int)tileY)] = this.manager.newLight(0.0f);
                }
            }
            AtomicInteger computeIterations = new AtomicInteger();
            AtomicInteger applyIterations = new AtomicInteger();
            ExecutorService updateExecutor = this.manager.updateExecutor;
            if (updateExecutor == null) {
                return;
            }
            int deltaX = this.endTileX - this.startTileX;
            int deltaY = this.endTileY - this.startTileY;
            if (concurrently) {
                for (LightComputeFuture lcf : computes) {
                    lcf.future = updateExecutor.submit(lcf.compute::compute);
                }
                updateExecutor.submit(() -> {
                    this.waitForComputes(computes, computeIterations);
                    for (LightComputeFuture lcf : computes) {
                        if (!lcf.complete) continue;
                        applyIterations.addAndGet(lcf.compute.apply(updateArea));
                    }
                    LightArea lightArea = this.area;
                    synchronized (lightArea) {
                        updateArea.overwriteArea(this.area);
                    }
                    if (this.printDebug) {
                        System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + computes.size() + ", DONE, I: " + computeIterations + ", a: " + applyIterations + ", " + deltaX + "x" + deltaY + ", " + GameUtils.getTimeStringNano(System.nanoTime() - time));
                    }
                    return null;
                });
                if (this.printDebug) {
                    System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + computes.size() + ", WAITING, " + deltaX + "x" + deltaY + ", " + GameUtils.getTimeStringNano(System.nanoTime() - time));
                }
            } else {
                if (this.printDebug) {
                    System.out.println((this.level.isServer() ? "S" : "C") + " START, C: " + computes.size() + ", " + deltaX + "x" + deltaY + ", " + GameUtils.getTimeStringNano(System.nanoTime() - time));
                }
                for (LightComputeFuture lcf : computes) {
                    computeIterations.addAndGet(lcf.compute.compute());
                    lcf.complete = true;
                }
                for (LightComputeFuture lcf : computes) {
                    if (!lcf.complete) continue;
                    applyIterations.addAndGet(lcf.compute.apply(updateArea));
                }
                if (this.printDebug) {
                    System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + computes.size() + ", I: " + computeIterations + ", a: " + applyIterations + ", " + deltaX + "x" + deltaY + ", " + GameUtils.getTimeStringNano(System.nanoTime() - time));
                }
            }
        }
    }

    private void waitForComputes(LinkedList<LightComputeFuture> futures, AtomicInteger iterations) {
        for (LightComputeFuture lcf : futures) {
            try {
                iterations.addAndGet(lcf.future.get(5L, TimeUnit.SECONDS));
                lcf.complete = true;
            }
            catch (InterruptedException e) {
                lcf.complete = false;
            }
            catch (ExecutionException e) {
                lcf.complete = false;
                System.err.println("Error executing light update");
                e.printStackTrace();
            }
            catch (TimeoutException e) {
                lcf.complete = false;
                System.err.println("Timed out executing light update");
                e.printStackTrace();
            }
        }
    }

    protected static class LightComputeFuture {
        public final ParticleLightCompute compute;
        public boolean complete;
        public Future<Integer> future;

        public LightComputeFuture(ParticleLightCompute compute) {
            this.compute = compute;
            this.complete = false;
        }
    }
}

