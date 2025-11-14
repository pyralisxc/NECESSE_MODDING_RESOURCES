/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;
import necesse.level.maps.light.FastLightCompute;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightArea;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.LightMapInterface;
import necesse.level.maps.light.SourcedGameLight;

public abstract class FastLightMap
implements LightMapInterface {
    public boolean printDebug;
    protected final LightManager manager;
    protected final Level level;
    protected final int maxLightDistance;
    protected final LightArea area;

    public FastLightMap(LightManager manager, int startX, int startY, int endX, int endY, int maxLightDistance) {
        this.manager = manager;
        this.level = manager.level;
        this.maxLightDistance = maxLightDistance;
        this.area = new LightArea(startX, startY, endX, endY);
        this.area.initLights();
        this.resetLights(manager);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void resetLights(LightManager manager) {
        LightArea lightArea = this.area;
        synchronized (lightArea) {
            for (int i = 0; i < this.area.lights.length; ++i) {
                this.area.lights[i] = manager.newLight(0.0f);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GameLight getLight(int x, int y) {
        x = GameMath.limit(x, this.area.startX, this.area.endX);
        y = GameMath.limit(y, this.area.startY, this.area.endY);
        LightArea lightArea = this.area;
        synchronized (lightArea) {
            return this.area.lights[this.area.getIndex(x, y)];
        }
    }

    @Override
    public List<SourcedGameLight> getLightSources(int x, int y) {
        return Collections.singletonList(new SourcedGameLight(x, y, this.getLight(x, y)));
    }

    @Override
    public void update(int tileX, int tileY, boolean concurrently) {
        this.update(tileX - this.maxLightDistance, tileY - this.maxLightDistance, tileX + this.maxLightDistance, tileY + this.maxLightDistance, concurrently);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update(int startX, int startY, int endX, int endY, boolean concurrently) {
        LightArea updateArea;
        if (!this.level.isLoadingComplete()) {
            return;
        }
        long time = System.nanoTime();
        boolean minX = false;
        boolean maxX = false;
        boolean minY = false;
        boolean maxY = false;
        if (startX < this.area.startY) {
            startX = this.area.startX;
            minX = true;
        }
        if (endX > this.area.endX) {
            endX = this.area.endX;
            maxX = true;
        }
        if (startY < this.area.startY) {
            startY = this.area.startY;
            minY = true;
        }
        if (endY > this.area.endY) {
            endY = this.area.endY;
            maxY = true;
        }
        LinkedList<LightComputeFuture> computes = new LinkedList<LightComputeFuture>();
        FastLightCompute edgeCompute = new FastLightCompute(this.level, this, startX, startY, endX, endY);
        computes.add(new LightComputeFuture(edgeCompute));
        LightArea lightArea = updateArea = concurrently ? new LightArea(startX, startY, endX, endY) : this.area;
        synchronized (lightArea) {
            updateArea.initLights();
            for (int i = startX; i <= endX; ++i) {
                for (int j = startY; j <= endY; ++j) {
                    if (i == startX && !minX || i == endX && !maxX || j == startY && !minY || j == endY && !maxY) {
                        GameLight currentLight = this.getLight(i, j);
                        if (currentLight.getLevel() > 0.0f) {
                            edgeCompute.addSource(i, j, currentLight);
                        }
                    } else {
                        GameLight newLight = this.getNewLight(i, j);
                        if (newLight.getLevel() > 0.0f) {
                            int maxLightDistance = (int)(newLight.getLevel() / 10.0f) + 1;
                            int cStartX = Math.max(startX, i - maxLightDistance);
                            int cStartY = Math.max(startY, j - maxLightDistance);
                            int cEndX = Math.min(endX, i + maxLightDistance);
                            int cEndY = Math.min(endY, j + maxLightDistance);
                            FastLightCompute compute = new FastLightCompute(this.level, this, cStartX, cStartY, cEndX, cEndY);
                            compute.addSource(i, j, newLight);
                            computes.add(new LightComputeFuture(compute));
                        }
                    }
                    updateArea.lights[updateArea.getIndex((int)i, (int)j)] = this.manager.newLight(0.0f);
                }
            }
            AtomicInteger computeIterations = new AtomicInteger();
            AtomicInteger applyIterations = new AtomicInteger();
            ExecutorService updateExecutor = this.manager.updateExecutor;
            if (updateExecutor == null) {
                return;
            }
            int deltaX = endX - startX;
            int deltaY = endY - startY;
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

    @Override
    public void update(Iterable<Point> tiles, boolean concurrently) {
        throw new UnsupportedOperationException("Cannot update using iterable and FastLightMap");
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

    protected abstract GameLight getNewLight(int var1, int var2);

    protected static class LightComputeFuture {
        public final FastLightCompute compute;
        public boolean complete;
        public Future<Integer> future;

        public LightComputeFuture(FastLightCompute compute) {
            this.compute = compute;
            this.complete = false;
        }
    }
}

