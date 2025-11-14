/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 */
package necesse.gfx.drawables;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceWrapper;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.chains.Chain;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ImpossibleDrawException;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawOptions.texture.StaticShaderSprite2f;
import necesse.gfx.drawOptions.texture.StaticShaderSprite4f;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.CustomGLDrawOptionsList;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDamageDrawOptions;
import necesse.gfx.drawables.LevelTileLightDrawOptions;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.drawables.WallShadowVariables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.RegionType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class LevelDrawUtils {
    private static final int DRAW_THREADS = 4;
    private static long lastSetupException;
    private final Level level;
    private TrackedThreadPoolExecutor executor;
    private LevelTileTerrainDrawOptions tileUnderLiquidDrawables;
    private LevelTileLiquidDrawOptions tileLiquidDrawables;
    private LevelTileTerrainDrawOptions tileOverLiquidDrawables;
    private LevelTileDamageDrawOptions tileDamageDrawables;
    private LevelTileLightDrawOptions tileLightDrawables;
    private SharedTextureDrawOptions logicDrawables;
    private SharedTextureDrawOptions wireDrawables;
    private OrderableDrawables objectTileDrawables;
    private OrderableDrawables entityTileDrawables;
    private OrderableDrawables entityTopDrawables;
    private OrderableDrawables overlayDrawables;
    private List<Drawable> wallShadowDrawables;
    private List<LevelSortedDrawable> sortedDrawables;
    private AtomicReference<Drawable> rainDrawable;
    private List<SortedDrawable> hudDrawables;
    private List<SortedDrawable> lastHudDrawables;
    private List<TrackedThreadPoolExecutor.TrackedFuture> setupLogic;
    private TickManager lastTickManager;

    public LevelDrawUtils(Level level) {
        this.level = level;
        if (!GlobalData.isServer()) {
            this.resetDrawables();
        }
        this.executor = new TrackedThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), this.defaultThreadFactory());
    }

    private void resetDrawables() {
        this.tileUnderLiquidDrawables = new LevelTileTerrainDrawOptions();
        this.tileLiquidDrawables = new LevelTileLiquidDrawOptions(this.level);
        this.tileOverLiquidDrawables = new LevelTileTerrainDrawOptions();
        this.tileDamageDrawables = new LevelTileDamageDrawOptions();
        this.tileLightDrawables = new LevelTileLightDrawOptions();
        this.logicDrawables = new SharedTextureDrawOptions(GameLogicGate.generatedLogicGateTexture);
        this.wireDrawables = new SharedTextureDrawOptions(GameResources.wire);
        this.objectTileDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> Collections.synchronizedList(new ArrayList()));
        this.wallShadowDrawables = Collections.synchronizedList(new ArrayList());
        this.entityTileDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> Collections.synchronizedList(new ArrayList()));
        this.entityTopDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> Collections.synchronizedList(new ArrayList()));
        this.overlayDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> Collections.synchronizedList(new ArrayList()));
        this.hudDrawables = Collections.synchronizedList(new ArrayList());
        this.sortedDrawables = Collections.synchronizedList(new ArrayList());
        this.rainDrawable = new AtomicReference<Drawable>(tm -> {});
        this.setupLogic = new ArrayList<TrackedThreadPoolExecutor.TrackedFuture>();
    }

    private ThreadFactory defaultThreadFactory() {
        AtomicInteger threadNum = new AtomicInteger(0);
        return r -> {
            Thread thread = new Thread(null, r, "level-" + this.level.getHostString() + "-" + this.level.getIdentifier() + "-draw-" + threadNum.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    public void dispose() {
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = null;
    }

    private TrackedThreadPoolExecutor.TrackedFuture executeLogic(TickManager tickManager, Consumer<TickManager> logic) {
        TickManager child = tickManager == null ? null : tickManager.getChild();
        return this.executor.submitTrackedTask(() -> logic.accept(child));
    }

    private TrackedThreadPoolExecutor.TrackedFuture addExecuteList(List<TrackedThreadPoolExecutor.TrackedFuture> list, TickManager tickManager, Consumer<TickManager> logic) {
        TrackedThreadPoolExecutor.TrackedFuture out = this.executeLogic(tickManager, logic);
        list.add(out);
        return out;
    }

    private void awaitExecuteList(List<TrackedThreadPoolExecutor.TrackedFuture> list) {
        Object exception = null;
        for (int index = 0; index < list.size(); ++index) {
            TrackedThreadPoolExecutor.TrackedFuture trackedFuture = list.get(index);
            try {
                trackedFuture.future.get(5L, TimeUnit.SECONDS);
                continue;
            }
            catch (InterruptedException | ExecutionException e) {
                if (exception != null) continue;
                exception = e;
                continue;
            }
            catch (TimeoutException e) {
                exception = new ThreadTimeOutException(trackedFuture.thread);
            }
        }
        if (exception != null) {
            throw new RuntimeException(exception.getClass().getName(), (Throwable)exception);
        }
    }

    @SafeVarargs
    private final void runParallel(TickManager tickManager, Consumer<TickManager> ... logic) {
        ArrayList<TrackedThreadPoolExecutor.TrackedFuture> futures = new ArrayList<TrackedThreadPoolExecutor.TrackedFuture>();
        for (Consumer<TickManager> f : logic) {
            this.addExecuteList(futures, tickManager, f);
        }
        this.awaitExecuteList(futures);
    }

    private void runParallel(TickManager tickManager, List<Consumer<TickManager>> logic) {
        this.runParallel(tickManager, logic.toArray(new Consumer[0]));
    }

    private void addEntityDrawProcesses(TickManager tickManager, GameCamera camera, DrawArea area, List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables, OrderableDrawables overlayDrawables, PlayerMob perspective) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "particleSetup", () -> this.level.entityManager.particleOptions.addDrawables(sortedDrawables, tileDrawables, topDrawables, this.level, tickManager, camera, perspective)));
        boolean isDebuggingOn = GlobalData.debugActive();
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "entitySetup", () -> {
            int startRegionY = this.level.regionManager.getRegionYByTileLimited(area.startTileY);
            int endRegionY = this.level.regionManager.getRegionYByTileLimited(area.endTileY);
            int startRegionX = this.level.regionManager.getRegionXByTileLimited(area.startTileX);
            int endRegionX = this.level.regionManager.getRegionXByTileLimited(area.endTileX);
            for (int j = endRegionY; j >= startRegionY; --j) {
                for (int i = startRegionX; i <= endRegionX; ++i) {
                    GameLinkedList<Entity> entities = this.level.entityManager.getRegionDrawEntities(i, j);
                    for (Entity entity : entities) {
                        if (!area.isIn(entity)) continue;
                        entity.addDrawables(sortedDrawables, tileDrawables, topDrawables, overlayDrawables, this.level, (TickManager)tm, camera, perspective);
                        if (!isDebuggingOn) continue;
                        entity.addDebugDrawables(sortedDrawables, tileDrawables, topDrawables, overlayDrawables, this.level, (TickManager)tm, camera, perspective);
                    }
                }
            }
        }));
    }

    private void addTrailDrawProcesses(TickManager tickManager, GameCamera camera, DrawArea area, List<LevelSortedDrawable> sortedDrawables, OrderableDrawables topDrawables) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "trailSetup", () -> {
            ArrayList<Trail> arrayList = this.level.entityManager.trails;
            synchronized (arrayList) {
                for (Trail t : this.level.entityManager.trails) {
                    if (t.drawOnTop) {
                        t.addDrawables(topDrawables, area.startTileY, area.endTileY, (TickManager)tm, camera);
                        continue;
                    }
                    t.addDrawables(sortedDrawables, area.startTileY, area.endTileY, (TickManager)tm, camera);
                }
            }
        }));
    }

    private void addChainDrawProcesses(TickManager tickManager, GameCamera camera, DrawArea area, List<LevelSortedDrawable> sortedDrawables, OrderableDrawables topDrawables) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "chainSetup", () -> {
            ArrayList<Chain> arrayList = this.level.entityManager.chains;
            synchronized (arrayList) {
                for (Chain c : this.level.entityManager.chains) {
                    if (c.drawOnTop) {
                        c.addDrawables(topDrawables, area.startTileY, area.endTileY, this.level, (TickManager)tm, camera);
                        continue;
                    }
                    c.addDrawables(sortedDrawables, area.startTileY, area.endTileY, this.level, (TickManager)tm, camera);
                }
            }
        }));
    }

    private void addGroundPillarHandlers(TickManager tickManager, GameCamera camera, DrawArea area, List<LevelSortedDrawable> sortedDrawables) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "groundPillarSetup", () -> {
            ArrayList<GroundPillarHandler<?>> arrayList = this.level.entityManager.pillarHandlers;
            synchronized (arrayList) {
                for (GroundPillarHandler<?> handler : this.level.entityManager.pillarHandlers) {
                    handler.addDrawables(sortedDrawables, area, this.level, tickManager, camera);
                }
            }
        }));
    }

    private void addLevelEventDrawProcesses(TickManager tickManager, GameCamera camera, DrawArea area, List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "eventSetup", () -> {
            Object object = this.level.entityManager.lock;
            synchronized (object) {
                for (LevelEvent event : this.level.entityManager.events) {
                    event.addDrawables(sortedDrawables, tileDrawables, topDrawables, area, this.level, tickManager, camera);
                }
            }
            WorldEntity worldEntity = this.level.getWorldEntity();
            if (worldEntity != null) {
                worldEntity.addWorldEventDrawables(tickManager, camera, area, this.level, sortedDrawables, tileDrawables, topDrawables);
            }
        }));
    }

    private void addTileBasedDrawProcesses(TickManager tickManager, GameCamera camera, DrawArea tileArea, DrawArea objectArea, LevelTileTerrainDrawOptions underLiquidDrawables, LevelTileLiquidDrawOptions liquidDrawables, LevelTileTerrainDrawOptions overLiquidDrawables, LevelTileDamageDrawOptions tileDamageDrawables, LevelTileLightDrawOptions lightDrawables, SharedTextureDrawOptions logicDrawables, SharedTextureDrawOptions wireDrawables, OrderableDrawables objectTileDrawables, List<LevelSortedDrawable> objectDrawables, boolean drawWire, PlayerMob perspective) {
        int rowSize = 16;
        int rows = (objectArea.endTileY - objectArea.startTileY) / rowSize;
        int i = 0;
        while (i <= rows) {
            int finalI = i++;
            this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "tileSetup", () -> {
                int rowY = objectArea.startTileY + finalI * rowSize;
                for (int y = Math.min(rowY + rowSize - 1, objectArea.endTileY); y >= rowY; --y) {
                    for (int x = objectArea.startTileX; x <= objectArea.endTileX; ++x) {
                        LogicGateEntity entity;
                        GameObject obj = this.level.objectLayer.addObjectDrawables(objectDrawables, objectTileDrawables, this.level, x, y, (TickManager)tm, camera, perspective);
                        if (x < tileArea.startTileX || x > tileArea.endTileX || y < tileArea.startTileY || y > tileArea.endTileY) continue;
                        GameTile tile = this.level.getTile(x, y);
                        if (!obj.drawsFullTile()) {
                            tile.addDrawables(underLiquidDrawables, liquidDrawables, overLiquidDrawables, objectTileDrawables, objectDrawables, this.level, x, y, camera, (TickManager)tm);
                            tileDamageDrawables.addDamage(tickManager, tile, this.level, x, y, camera);
                        }
                        tile.addLightDrawables(lightDrawables, this.level, x, y, camera, (TickManager)tm);
                        if (Settings.smoothLighting) {
                            if (x == tileArea.startTileX) {
                                tile.addLightDrawables(lightDrawables, this.level, x - 1, y, camera, (TickManager)tm);
                                if (y == tileArea.startTileY) {
                                    tile.addLightDrawables(lightDrawables, this.level, x - 1, y - 1, camera, (TickManager)tm);
                                }
                            }
                            if (y == tileArea.startTileY) {
                                tile.addLightDrawables(lightDrawables, this.level, x, y - 1, camera, (TickManager)tm);
                            }
                        }
                        if (!drawWire) continue;
                        if (this.level.logicLayer.hasGate(x, y) && (entity = this.level.logicLayer.getEntity(x, y)) != null) {
                            entity.getLogicGate().addDrawables(logicDrawables, this.level, x, y, entity, (TickManager)tm, camera);
                        }
                        this.level.wireManager.addWireDrawables(wireDrawables, x, y, camera, (TickManager)tm);
                    }
                }
            }));
        }
    }

    private void addWallShadowDrawables(TickManager tickManager, GameCamera camera, DrawArea tileArea, List<Drawable> wallShadowDrawables) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> {
            if (this.level.isCave) {
                return;
            }
            Performance.record((PerformanceTimerManager)tm, "wallShadowSetup", () -> {
                LinkedList wallShadows = this.level.getWallShadows().filter(s -> s.lightLevel > 0.0f && s.range > 0.0f).collect(Collectors.toCollection(LinkedList::new));
                if (wallShadows.isEmpty()) {
                    return;
                }
                int startTileX = tileArea.startTileX;
                int endTileX = tileArea.endTileX;
                int startTileY = tileArea.startTileY;
                int endTileY = tileArea.endTileY;
                for (WallShadowVariables wallShadow : wallShadows) {
                    wallShadow.calculate();
                    int dirXOffsetCeil = (int)Math.ceil(Math.abs(wallShadow.dirXOffset / 32.0f));
                    if (wallShadow.dirXOffset > 0.0f) {
                        startTileX = this.level.limitTileXToBounds(startTileX - dirXOffsetCeil);
                    }
                    if (wallShadow.dirXOffset < 0.0f) {
                        endTileX = this.level.limitTileXToBounds(endTileX + dirXOffsetCeil);
                    }
                    int dirYOffsetCeil = (int)Math.ceil(Math.abs(wallShadow.dirYOffset / 32.0f));
                    if (wallShadow.dirYOffset > 0.0f) {
                        startTileY = this.level.limitTileYToBounds(startTileY - dirYOffsetCeil);
                    }
                    if (!(wallShadow.dirYOffset < 0.0f)) continue;
                    endTileY = this.level.limitTileYToBounds(endTileY + dirYOffsetCeil);
                }
                HashSet<Point> roofs = new HashSet<Point>();
                for (int y = startTileY; y <= endTileY; ++y) {
                    for (int x = startTileX; x <= endTileX; ++x) {
                        if (this.level.isOutside(x, y)) continue;
                        roofs.add(new Point(x, y));
                    }
                }
                CustomGLDrawOptionsList fillDrawOptions = new CustomGLDrawOptionsList();
                fillDrawOptions.setupRunnable = () -> GL14.glBlendEquation((int)32776);
                for (WallShadowVariables wallShadow : wallShadows) {
                    for (Point tile : roofs) {
                        Point northTile;
                        Point westTile;
                        Point southTile;
                        Point eastTile;
                        int drawX = camera.getTileDrawX(tile.x);
                        int drawY = camera.getTileDrawY(tile.y);
                        int width = 32;
                        int height = 32;
                        fillDrawOptions.add(() -> {
                            GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.startAlpha);
                            GL11.glVertex2f((float)drawX, (float)drawY);
                            GL11.glVertex2f((float)(drawX + width), (float)drawY);
                            GL11.glVertex2f((float)(drawX + width), (float)(drawY + height));
                            GL11.glVertex2f((float)drawX, (float)(drawY + height));
                        });
                        if (wallShadow.east && !roofs.contains(eastTile = new Point(tile.x + 1, tile.y))) {
                            fillDrawOptions.add(() -> {
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.startAlpha);
                                GL11.glVertex2f((float)(drawX + width), (float)drawY);
                                GL11.glVertex2f((float)(drawX + width), (float)(drawY + height));
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.endAlpha);
                                GL11.glVertex2f((float)((float)(drawX + width) + wallShadow.dirXOffset), (float)((float)(drawY + height) + wallShadow.dirYOffset));
                                GL11.glVertex2f((float)((float)(drawX + width) + wallShadow.dirXOffset), (float)((float)drawY + wallShadow.dirYOffset));
                            });
                        }
                        if (wallShadow.south && !roofs.contains(southTile = new Point(tile.x, tile.y + 1))) {
                            fillDrawOptions.add(() -> {
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.startAlpha);
                                GL11.glVertex2f((float)drawX, (float)(drawY + height));
                                GL11.glVertex2f((float)(drawX + width), (float)(drawY + height));
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.endAlpha);
                                GL11.glVertex2f((float)((float)(drawX + width) + wallShadow.dirXOffset), (float)((float)(drawY + height) + wallShadow.dirYOffset));
                                GL11.glVertex2f((float)((float)drawX + wallShadow.dirXOffset), (float)((float)(drawY + height) + wallShadow.dirYOffset));
                            });
                        }
                        if (wallShadow.west && !roofs.contains(westTile = new Point(tile.x - 1, tile.y))) {
                            fillDrawOptions.add(() -> {
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.startAlpha);
                                GL11.glVertex2f((float)drawX, (float)drawY);
                                GL11.glVertex2f((float)drawX, (float)(drawY + height));
                                GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.endAlpha);
                                GL11.glVertex2f((float)((float)drawX + wallShadow.dirXOffset), (float)((float)(drawY + height) + wallShadow.dirYOffset));
                                GL11.glVertex2f((float)((float)drawX + wallShadow.dirXOffset), (float)((float)drawY + wallShadow.dirYOffset));
                            });
                        }
                        if (!wallShadow.north || roofs.contains(northTile = new Point(tile.x, tile.y - 1))) continue;
                        fillDrawOptions.add(() -> {
                            GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.startAlpha);
                            GL11.glVertex2f((float)drawX, (float)drawY);
                            GL11.glVertex2f((float)(drawX + width), (float)drawY);
                            GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)wallShadow.endAlpha);
                            GL11.glVertex2f((float)((float)(drawX + width) + wallShadow.dirXOffset), (float)((float)drawY + wallShadow.dirYOffset));
                            GL11.glVertex2f((float)((float)drawX + wallShadow.dirXOffset), (float)((float)drawY + wallShadow.dirYOffset));
                        });
                    }
                }
                CustomGLDrawOptionsList removeDrawOptions = new CustomGLDrawOptionsList();
                removeDrawOptions.setupRunnable = () -> {
                    GL14.glBlendEquation((int)32774);
                    GL11.glBlendFunc((int)770, (int)0);
                    GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
                };
                for (WallShadowVariables wallShadow : wallShadows) {
                    for (Point tile : roofs) {
                        RegionType northType;
                        int tileDrawX = camera.getTileDrawX(tile.x);
                        int tileDrawY = camera.getTileDrawY(tile.y);
                        if (!roofs.contains(new Point(tile.x, tile.y + 1)) && (!roofs.contains(new Point(tile.x, tile.y - 1)) || (northType = this.level.regionManager.getRegionTypeByTile(tile.x, tile.y - 1)) == RegionType.WALL || northType == RegionType.DOOR) || (wallShadow.west || wallShadow.east) && (!roofs.contains(new Point(tile.x - 1, tile.y)) || !roofs.contains(new Point(tile.x + 1, tile.y)))) continue;
                        removeDrawOptions.add(() -> {
                            GL11.glVertex2f((float)tileDrawX, (float)tileDrawY);
                            GL11.glVertex2f((float)(tileDrawX + 32), (float)tileDrawY);
                            GL11.glVertex2f((float)(tileDrawX + 32), (float)(tileDrawY + 32));
                            GL11.glVertex2f((float)tileDrawX, (float)(tileDrawY + 32));
                        });
                    }
                }
                wallShadowDrawables.add(tm2 -> {
                    fillDrawOptions.draw(7);
                    removeDrawOptions.draw(7);
                });
            });
        });
    }

    private void addRainDrawProcesses(TickManager tickManager, AtomicReference<Drawable> drawable, GameCamera camera, DrawArea area, long globalTime) {
        this.addExecuteList(this.setupLogic, tickManager, tm1 -> Performance.record((PerformanceTimerManager)tm1, "rainSetup", () -> {
            float rainAlpha = this.level.weatherLayer.getRainAlpha();
            if (rainAlpha <= 0.0f) {
                return;
            }
            ArrayList<Consumer<TickManager>> rainProcessList = new ArrayList<Consumer<TickManager>>();
            SharedTextureDrawOptions drawables = new SharedTextureDrawOptions(Biome.generatedRainTexture);
            int rowSize = 16;
            int rows = (area.endTileY - area.startTileY) / rowSize;
            int i = 0;
            while (i <= rows) {
                int finalI = i++;
                rainProcessList.add(tm2 -> {
                    int rowY = area.startTileY + finalI * rowSize;
                    double xSpeedDivider = 5000.0;
                    double ySpeedDivider = 1000.0;
                    double xOffset = (double)globalTime / xSpeedDivider % 1.0;
                    xOffset = 1.0 - xOffset;
                    double yOffset = (double)globalTime / ySpeedDivider % 1.0;
                    yOffset = 1.0 - yOffset;
                    float floatXOffset = (float)xOffset;
                    float floatYOffset = (float)yOffset;
                    for (int tileY = Math.min(rowY + rowSize - 1, area.endTileY); tileY >= rowY; --tileY) {
                        int drawY = camera.getTileDrawY(tileY);
                        for (int tileX = area.startTileX; tileX <= area.endTileX; ++tileX) {
                            if (!this.level.isOutside(tileX, tileY)) continue;
                            int drawX = camera.getTileDrawX(tileX);
                            Biome biome = this.level.getBiome(tileX, tileY);
                            if (!biome.canRain(this.level)) continue;
                            GameTextureSection texture = biome.getRainTexture(this.level, tileX, tileY);
                            Color rainColor = biome.getRainColor(this.level, tileX, tileY);
                            int textureTilesX = GameMath.getTileCoordinate(texture.getWidth());
                            int textureTilesY = GameMath.getTileCoordinate(texture.getHeight());
                            float red = (float)rainColor.getRed() / 255.0f;
                            float green = (float)rainColor.getGreen() / 255.0f;
                            float blue = (float)rainColor.getBlue() / 255.0f;
                            float alpha = (float)rainColor.getAlpha() / 255.0f * rainAlpha;
                            int rainTileY = Math.floorMod(tileY, textureTilesY);
                            int startY = rainTileY * 32;
                            int rainTileX = Math.floorMod(tileX, textureTilesX);
                            int startX = rainTileX * 32;
                            StaticShaderSprite4f textureBounds = new StaticShaderSprite4f(1, TextureDrawOptions.pixel(texture.getStartX(), Biome.generatedRainTexture.getWidth()), TextureDrawOptions.pixel(texture.getStartY(), Biome.generatedRainTexture.getHeight()), TextureDrawOptions.pixel(texture.getEndX(), Biome.generatedRainTexture.getWidth()), TextureDrawOptions.pixel(texture.getEndY(), Biome.generatedRainTexture.getHeight()));
                            drawables.add(texture.section(startX, startX + 32, startY, startY + 32)).addShaderSprite(textureBounds).addShaderSprite(new StaticShaderSprite2f(2, floatXOffset, floatYOffset)).colorLight(red, green, blue, alpha, this.level.getLightLevel(tileX, tileY)).size(32, 32).pos(drawX, drawY);
                        }
                    }
                });
            }
            this.runParallel((TickManager)tm1, (List<Consumer<TickManager>>)rainProcessList);
            drawable.set(tm -> {
                GameResources.rainShader.use();
                drawables.draw();
                GameResources.rainShader.stop();
            });
        }));
    }

    public void addHudDrawProcesses(TickManager tickManager, List<SortedDrawable> hudDrawables, final GameCamera camera, final PlayerMob perspective) {
        this.addExecuteList(this.setupLogic, tickManager, tm -> Performance.record((PerformanceTimerManager)tm, "hudSetup", () -> {
            PlayerInventorySlot slot;
            InventoryItem selectedItem;
            this.level.hudManager.addDrawables(hudDrawables, camera, perspective);
            final DrawOptions options = perspective != null ? ((selectedItem = (slot = perspective.getSelectedItemSlot()).getInv(perspective.getInv()).getItem(slot.slot)) != null && selectedItem.item.isPlaceable() ? () -> {
                Point levelPos;
                if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                    Point2D.Float aimDir = perspective.getControllerAimDir();
                    levelPos = selectedItem.item.getControllerAttackLevelPos(this.level, aimDir.x, aimDir.y, perspective, selectedItem);
                } else {
                    levelPos = new Point(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
                }
                selectedItem.item.getPlaceable().drawPlacePreview(this.level, levelPos.x, levelPos.y, camera, perspective, selectedItem, slot);
            } : null) : null;
            hudDrawables.add(new SortedDrawable(){

                @Override
                public int getPriority() {
                    return Integer.MIN_VALUE;
                }

                @Override
                public void draw(TickManager tickManager) {
                    if (options != null) {
                        options.draw();
                    }
                }
            });
            hudDrawables.add(new SortedDrawable(){

                @Override
                public int getPriority() {
                    return Integer.MAX_VALUE;
                }

                @Override
                public void draw(TickManager tickManager) {
                    HUD.draw(LevelDrawUtils.this.level, camera, perspective, tickManager);
                    Debug.drawHUD(LevelDrawUtils.this.level, camera, perspective);
                }
            });
        }));
    }

    public void draw(GameCamera camera, PlayerMob perspective, TickManager tickManager, boolean singleDraw) {
        GameCamera fCamera = new GameCamera(camera.getX(), camera.getY(), camera.getWidth(), camera.getHeight());
        Performance.record((PerformanceTimerManager)tickManager, "levelDraw", () -> {
            block10: {
                if (singleDraw || this.setupLogic.size() == 0) {
                    this.setupNextLogic(fCamera, perspective, tickManager, singleDraw);
                }
                PerformanceWrapper awaitTimer = Performance.wrapTimer(tickManager, "awaitLast");
                try {
                    this.awaitExecuteList(this.setupLogic);
                }
                catch (Exception e) {
                    Throwable next;
                    Throwable error = e;
                    while (error != null && !(error instanceof ConcurrentModificationException) && !(error instanceof NullPointerException) && (next = e.getCause()) != error) {
                        error = next;
                    }
                    if (error != null) {
                        long timeSinceLast = System.currentTimeMillis() - lastSetupException;
                        if (lastSetupException != 0L && timeSinceLast < 10000L || error instanceof ThreadTimeOutException) {
                            throw e;
                        }
                        System.err.println("Detected an error under rendering: " + error.getClass().getSimpleName());
                        e.printStackTrace(System.err);
                        lastSetupException = System.currentTimeMillis();
                        break block10;
                    }
                    throw e;
                }
                finally {
                    awaitTimer.end();
                }
            }
            this.lastTickManager = null;
            Performance.record((PerformanceTimerManager)tickManager, "sortDraws", () -> {
                try {
                    this.sortedDrawables.sort(null);
                    this.hudDrawables.sort(null);
                }
                catch (Exception e) {
                    ImpossibleDrawException.submitDrawError(e);
                }
            });
            LevelTileTerrainDrawOptions tileUnderLiquidDrawables = this.tileUnderLiquidDrawables;
            LevelTileLiquidDrawOptions tileLiquidDrawables = this.tileLiquidDrawables;
            LevelTileTerrainDrawOptions tileOverLiquidDrawables = this.tileOverLiquidDrawables;
            LevelTileDamageDrawOptions tileDamageDrawables = this.tileDamageDrawables;
            LevelTileLightDrawOptions tileLightDrawables = this.tileLightDrawables;
            SharedTextureDrawOptions logicDrawables = this.logicDrawables;
            SharedTextureDrawOptions wireDrawables = this.wireDrawables;
            OrderableDrawables objectTileDrawables = this.objectTileDrawables;
            List<Drawable> wallShadowDrawables = this.wallShadowDrawables;
            OrderableDrawables entityTileDrawables = this.entityTileDrawables;
            OrderableDrawables entityTopDrawables = this.entityTopDrawables;
            OrderableDrawables overlayDrawables = this.overlayDrawables;
            this.lastHudDrawables = this.hudDrawables;
            List<LevelSortedDrawable> sortedDrawables = this.sortedDrawables;
            AtomicReference<Drawable> rainDrawable = this.rainDrawable;
            if (!singleDraw) {
                this.setupNextLogic(fCamera, perspective, tickManager, singleDraw);
            }
            GameWindow window = WindowManager.getWindow();
            Performance.record((PerformanceTimerManager)tickManager, "draw", () -> {
                Performance.record((PerformanceTimerManager)tickManager, "tileDraw", () -> {
                    try {
                        tileUnderLiquidDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                    GameResources.liquidShader.use(this.level);
                    try {
                        tileLiquidDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                    finally {
                        GameResources.liquidShader.stop();
                    }
                    try {
                        tileOverLiquidDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                    try {
                        tileDamageDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                    GL14.glBlendFuncSeparate((int)0, (int)768, (int)0, (int)770);
                    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    tileLightDrawables.draw();
                    GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
                });
                Performance.record((PerformanceTimerManager)tickManager, "oTileDraw", () -> {
                    try {
                        objectTileDrawables.draw(tickManager);
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "eTileDraw", () -> {
                    try {
                        entityTileDrawables.forEach(e -> e.draw(tickManager));
                    }
                    catch (Exception e2) {
                        ImpossibleDrawException.submitDrawError(e2);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "wallShadowDraw", () -> window.applyDraw(() -> wallShadowDrawables.forEach(e -> e.draw(tickManager)), () -> {
                    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
                }, null));
                Performance.record((PerformanceTimerManager)tickManager, "sortedDraw", () -> {
                    try {
                        sortedDrawables.forEach(e -> e.draw(tickManager));
                    }
                    catch (Exception e2) {
                        ImpossibleDrawException.submitDrawError(e2);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "wireDraw", () -> {
                    try {
                        wireDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "logicDraw", () -> {
                    try {
                        logicDrawables.draw();
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "eTopDraw", () -> {
                    try {
                        entityTopDrawables.forEach(e -> e.draw(tickManager));
                        overlayDrawables.forEach(e -> e.draw(tickManager));
                    }
                    catch (Exception e2) {
                        ImpossibleDrawException.submitDrawError(e2);
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "rainDraw", () -> {
                    try {
                        if (rainDrawable.get() != null) {
                            ((Drawable)rainDrawable.get()).draw(tickManager);
                        }
                    }
                    catch (Exception e) {
                        ImpossibleDrawException.submitDrawError(e);
                    }
                });
            });
        });
    }

    public void drawLastHudDrawables(GameCamera camera, PlayerMob perspective, TickManager tickManager) {
        List<SortedDrawable> lastHudDrawables = this.lastHudDrawables;
        if (lastHudDrawables == null) {
            return;
        }
        Performance.record((PerformanceTimerManager)tickManager, "levelHud", () -> lastHudDrawables.forEach(e -> e.draw(tickManager)));
    }

    public boolean drawWire(PlayerMob perspective) {
        if (this.level.alwaysDrawWire) {
            return true;
        }
        if (GameToolManager.doesToolShowWires()) {
            return true;
        }
        if (this.level.isTrialRoom && !GlobalData.isDevMode() && !GlobalData.debugCheatActive()) {
            return false;
        }
        if (GlobalData.debugActive()) {
            return true;
        }
        return perspective != null && perspective.getSelectedItem() != null && perspective.getSelectedItem().item.showWires();
    }

    private void setupNextLogic(GameCamera camera, PlayerMob perspective, TickManager tickManager, boolean singleDraw) {
        this.lastTickManager = tickManager == null ? null : tickManager.getChild();
        this.level.entityManager.updateParticlesAllowed(camera);
        DrawArea tileArea = new DrawArea(this.level, camera);
        DrawArea objectArea = new DrawArea(this.level, tileArea.startTileX - 2, tileArea.endTileX + 2, tileArea.startTileY - 1, tileArea.endTileY + 3);
        DrawArea entityArea = new DrawArea(this.level, objectArea.startTileX - 2, objectArea.endTileX + 2, objectArea.startTileY - 3, objectArea.endTileY + 7);
        LinkedList<TrackedThreadPoolExecutor.TrackedFuture> pLightSetup = new LinkedList<TrackedThreadPoolExecutor.TrackedFuture>();
        this.addExecuteList(pLightSetup, tickManager, tm -> {
            this.level.regionManager.resetFinalLights();
            Performance.record((PerformanceTimerManager)this.lastTickManager, "pLight", () -> {
                if (this.level.tickManager().isGameTick() || singleDraw) {
                    this.level.lightManager.setDrawArea(objectArea.startTileX, objectArea.startTileY, objectArea.endTileX, objectArea.endTileY);
                }
            });
        });
        boolean drawWire = this.drawWire(perspective);
        this.resetDrawables();
        this.awaitExecuteList(pLightSetup);
        Performance.record((PerformanceTimerManager)this.lastTickManager, "setup", () -> {
            this.addTileBasedDrawProcesses(this.lastTickManager, camera, tileArea, objectArea, this.tileUnderLiquidDrawables, this.tileLiquidDrawables, this.tileOverLiquidDrawables, this.tileDamageDrawables, this.tileLightDrawables, this.logicDrawables, this.wireDrawables, this.objectTileDrawables, this.sortedDrawables, drawWire, perspective);
            this.addWallShadowDrawables(this.lastTickManager, camera, tileArea, this.wallShadowDrawables);
            this.addEntityDrawProcesses(this.lastTickManager, camera, entityArea, this.sortedDrawables, this.entityTileDrawables, this.entityTopDrawables, this.overlayDrawables, perspective);
            this.addTrailDrawProcesses(this.lastTickManager, camera, objectArea, this.sortedDrawables, this.entityTopDrawables);
            this.addChainDrawProcesses(this.lastTickManager, camera, objectArea, this.sortedDrawables, this.entityTopDrawables);
            this.addGroundPillarHandlers(this.lastTickManager, camera, objectArea, this.sortedDrawables);
            this.addLevelEventDrawProcesses(this.lastTickManager, camera, objectArea, this.sortedDrawables, this.entityTileDrawables, this.entityTopDrawables);
            this.addRainDrawProcesses(this.lastTickManager, this.rainDrawable, camera, tileArea, this.level.getWorldEntity().getTime());
            this.addHudDrawProcesses(this.lastTickManager, this.hudDrawables, camera, perspective);
        });
    }

    public static class TrackedThreadPoolExecutor
    extends ThreadPoolExecutor {
        private final ConcurrentHashMap<FutureTask<?>, TrackedFuture> trackedFutures = new ConcurrentHashMap();

        public TrackedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            if (r instanceof FutureTask && this.trackedFutures.containsKey(r)) {
                this.trackedFutures.get(r).thread = t;
            }
            super.beforeExecute(t, r);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            if (r instanceof FutureTask && this.trackedFutures.containsKey(r)) {
                this.trackedFutures.get(r).thread = null;
                this.trackedFutures.remove(r);
            }
            super.afterExecute(r, t);
        }

        public TrackedFuture submitTrackedTask(Runnable task) {
            FutureTask future = (FutureTask)super.submit(task);
            TrackedFuture trackedFuture = new TrackedFuture(future);
            this.trackedFutures.put(future, trackedFuture);
            return trackedFuture;
        }

        public static class TrackedFuture {
            public final FutureTask<?> future;
            private Thread thread = null;

            public TrackedFuture(FutureTask<?> future) {
                this.future = future;
            }

            public Thread getExecutingThread() {
                return this.thread;
            }
        }
    }

    public static class ThreadTimeOutException
    extends RuntimeException {
        public ThreadTimeOutException(Thread thread) {
            super(ThreadTimeOutException.formatMessage(thread));
        }

        private static String formatMessage(Thread thread) {
            StackTraceElement[] stackTraces;
            if (thread == null) {
                return "Unknown thread timed out";
            }
            StringBuilder sb = new StringBuilder("Thread timed out: \n");
            sb.append("\t Thread name: ").append(thread.getName()).append("\n");
            sb.append("\t Thread stack:\n");
            for (StackTraceElement stackTrace : stackTraces = thread.getStackTrace()) {
                sb.append("\t\t ").append(stackTrace).append("\n");
            }
            return sb.toString();
        }
    }

    public static class DrawArea {
        public final int startTileX;
        public final int endTileX;
        public final int startTileY;
        public final int endTileY;

        public DrawArea(Level level, GameCamera camera) {
            this(level, GameMath.getTileCoordinate(camera.getX() - 16), camera.getEndTileX() + 1, GameMath.getTileCoordinate(camera.getY() - 16), camera.getEndTileY() + 1);
        }

        public DrawArea(Level level, int startTileX, int endTileX, int startTileY, int endTileY) {
            this.startTileX = level.limitTileXToBounds(startTileX);
            this.endTileX = Math.max(level.limitTileXToBounds(endTileX), this.startTileX);
            this.startTileY = level.limitTileYToBounds(startTileY);
            this.endTileY = Math.max(level.limitTileYToBounds(endTileY), this.startTileY);
        }

        public boolean isIn(int tileX, int tileY) {
            return tileX >= this.startTileX && tileX <= this.endTileX && tileY >= this.startTileY && tileY <= this.endTileY;
        }

        public boolean isIn(Entity entity) {
            if (entity == null) {
                return false;
            }
            return this.isIn(entity.getTileX(), entity.getTileY());
        }

        public boolean isInPos(float x, float y) {
            return this.isIn(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        }
    }
}

