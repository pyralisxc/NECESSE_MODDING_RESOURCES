/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameRandomNoise;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.ClientClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.state.State;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldPresets.WorldPresetsRegion;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.GLDrawOptionsList;
import necesse.gfx.drawables.QuadDrawOptionsList;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ControllerInteractTarget;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.CursedCroneArenasLevelData;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.levelData.OneWorldPirateVillageData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.SourcedGameLight;
import necesse.level.maps.light.SourcedLightModifier;
import necesse.level.maps.managers.BiomeBlendingManager;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;
import necesse.level.maps.regionSystem.layers.BiomeBlendingValue;
import org.lwjgl.opengl.GL11;

public class HUD {
    public static boolean debugActive;
    public static DebugShow debugShow;
    public static boolean showRegionBounds;
    public static boolean showWorldPresetRegionBounds;
    private static final LinkedList<SubmittedPath> paths;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void draw(Level level, GameCamera camera, PlayerMob perspective, TickManager tickManager) {
        NetworkClient hoveringClient;
        Mob summonFocus;
        Object me;
        State currentState = GlobalData.getCurrentState();
        GameWindow window = WindowManager.getWindow();
        int mouseX = camera.getMouseLevelPosX();
        int mouseY = camera.getMouseLevelPosY();
        int mouseTileX = camera.getMouseLevelTilePosX();
        int mouseTileY = camera.getMouseLevelTilePosY();
        if (level.isClient() && (me = level.getClient().getClient()) != null && (summonFocus = ((ClientClient)me).playerMob.getSummonFocusMob()) != null) {
            Rectangle selectBox = summonFocus.getSelectBox();
            int size = 16;
            GameResources.particles.sprite(0, 0, 8).initDraw().size(size, size).color(new Color(250, 50, 50)).rotate((float)level.getWorldEntity().getLocalTime() / 5.0f, size / 2, size / 2).draw(camera.getDrawX(selectBox.x + selectBox.width / 2) - size / 2, camera.getDrawY(selectBox.y + selectBox.height / 2) - size / 2);
        }
        me = paths;
        synchronized (me) {
            paths.removeIf(p -> p.time + 5000L < level.getWorldEntity().getLocalTime());
        }
        switch (debugShow) {
            case NOTHING: {
                break;
            }
            case REGIONS: {
                Region region = level.regionManager.getRegionByTile(mouseTileX, mouseTileY, false);
                StringTooltips regionTip = new StringTooltips("Region:");
                if (region != null) {
                    regionTip.add("Pos: " + region.regionX + ", " + region.regionY + " (" + region.tileXOffset + ", " + region.tileYOffset + ")");
                    regionTip.add("Dim: " + region.tileWidth + ", " + region.tileHeight);
                    GameTooltipManager.addTooltip(regionTip, TooltipLocation.BOTTOM_LEFT);
                    SubRegion subRegion = region.subRegionData.getSubRegionByRegion(mouseTileX - region.tileXOffset, mouseTileY - region.tileYOffset);
                    if (subRegion == null) break;
                    HUD.drawCells(subRegion, new Color(255, 255, 0), level, camera);
                    for (SubRegion adjacentRegion : subRegion.getAdjacentRegions()) {
                        HUD.drawCells(adjacentRegion, new Color(0, 0, 255), level, camera);
                    }
                    StringTooltips tooltips = new StringTooltips("Subregion:");
                    tooltips.add("Type: " + (Object)((Object)subRegion.getType()));
                    tooltips.add("Region ID: " + subRegion.getRegionID());
                    tooltips.add("Room ID: " + subRegion.getRoomID());
                    tooltips.add("Size: " + subRegion.size());
                    tooltips.add("Listeners: " + subRegion.getListenersSize());
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.BOTTOM_LEFT);
                    break;
                }
                regionTip.add("No region found");
                break;
            }
            case CONNECTED_REGIONS: {
                int startRegionX = level.regionManager.getRegionCoordByTile(camera.getStartTileX());
                int startRegionY = level.regionManager.getRegionCoordByTile(camera.getStartTileY());
                int endRegionX = level.regionManager.getRegionCoordByTile(camera.getEndTileX());
                int endRegionY = level.regionManager.getRegionCoordByTile(camera.getEndTileY());
                ConnectedSubRegionsResult result = level.regionManager.getTypeConnectedByTile(mouseTileX, mouseTileY, Integer.MAX_VALUE);
                StringTooltips tooltips = new StringTooltips();
                if (result != null) {
                    for (SubRegion subregion : result.connectedRegions) {
                        if (subregion.region.regionX < startRegionX || subregion.region.regionX > endRegionX || subregion.region.regionY < startRegionY || subregion.region.regionY > endRegionY) continue;
                        HUD.drawCells(subregion, new Color(255, 255, 0), level, camera);
                    }
                    tooltips.add("Region ID: " + result.base.getRegionID());
                    tooltips.add("Region type: " + (Object)((Object)result.base.getType()));
                    tooltips.add("Connected size: " + result.size);
                } else {
                    tooltips.add("No region found");
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.BOTTOM_LEFT);
                break;
            }
            case ROOMS: {
                int startRegionX = level.regionManager.getRegionCoordByTile(camera.getStartTileX());
                int startRegionY = level.regionManager.getRegionCoordByTile(camera.getStartTileY());
                int endRegionX = level.regionManager.getRegionCoordByTile(camera.getEndTileX());
                int endRegionY = level.regionManager.getRegionCoordByTile(camera.getEndTileY());
                ConnectedSubRegionsResult result = level.regionManager.getRoomConnectedByTile(mouseTileX, mouseTileY, false, Integer.MAX_VALUE);
                StringTooltips tooltips = new StringTooltips();
                if (result != null) {
                    for (SubRegion subregion : result.connectedRegions) {
                        if (subregion.region.regionX < startRegionX || subregion.region.regionX > endRegionX || subregion.region.regionY < startRegionY || subregion.region.regionY > endRegionY) continue;
                        HUD.drawCells(subregion, new Color(255, 255, 0), level, camera);
                    }
                    tooltips.add("Room ID: " + result.base.getRoomID());
                    tooltips.add("Room type: " + (Object)((Object)result.base.getType()));
                    tooltips.add("Room size: " + result.size + " (" + level.regionManager.getRoomSize(result.base.getRoomID()) + ")");
                } else {
                    tooltips.add("No room found");
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.BOTTOM_LEFT);
                break;
            }
            case HOUSE: {
                int startRegionX = level.regionManager.getRegionCoordByTile(camera.getStartTileX());
                int startRegionY = level.regionManager.getRegionCoordByTile(camera.getStartTileY());
                int endRegionX = level.regionManager.getRegionCoordByTile(camera.getEndTileX());
                int endRegionY = level.regionManager.getRegionCoordByTile(camera.getEndTileY());
                ConnectedSubRegionsResult result = level.regionManager.getHouseConnectedByTile(mouseTileX, mouseTileY, Integer.MAX_VALUE);
                StringTooltips tooltips = new StringTooltips();
                if (result != null) {
                    int doors = 0;
                    int doorsOutside = 0;
                    for (SubRegion subRegion : result.connectedRegions) {
                        if (subRegion.region.regionX >= startRegionX && subRegion.region.regionX <= endRegionX && subRegion.region.regionY >= startRegionY && subRegion.region.regionY <= endRegionY) {
                            HUD.drawCells(subRegion, new Color(255, 255, 0), level, camera);
                        }
                        if (subRegion.getType() != RegionType.DOOR) continue;
                        ++doors;
                        if (!subRegion.streamAdjacentRegions().anyMatch(SubRegion::isOutside)) continue;
                        ++doorsOutside;
                    }
                    int[] roomIDs = result.connectedRegions.stream().mapToInt(SubRegion::getRoomID).distinct().toArray();
                    tooltips.add("House room IDs: " + Arrays.toString(roomIDs));
                    tooltips.add("House doors: " + doors + ", leading out: " + doorsOutside);
                    int n = Arrays.stream(roomIDs).map(level.regionManager::getRoomSize).sum();
                    tooltips.add("House size: " + n + " (" + result.size + ")");
                } else {
                    tooltips.add("No house found");
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.BOTTOM_LEFT);
                break;
            }
            case PATHS: {
                LinkedList<SubmittedPath> startRegionX = paths;
                synchronized (startRegionX) {
                    paths.stream().filter(path -> ((TilePathfinding)path.path.finder).level.isSamePlace(level)).forEach(path -> TilePathfinding.drawPathLine(path.path.path, camera));
                    break;
                }
            }
            case HEIGHT: {
                SharedTextureDrawOptions options = new SharedTextureDrawOptions(GameResources.empty);
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int drawY = camera.getTileDrawY(y);
                        int n = level.liquidManager.getHeight(x, y);
                        float red = n >= 0 ? (float)n / 10.0f : 1.0f;
                        float green = n <= 0 ? (float)n / -10.0f : 1.0f;
                        options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(red, green, 1.0f, 0.5f).pos(drawX, drawY);
                    }
                }
                options.draw();
                break;
            }
            case ADVANCED_HEIGHT: {
                ArrayList regions = new ArrayList(level.regionManager.getLoadedRegionsSize());
                level.regionManager.forEachLoadedRegions(regions::add);
                if (!regions.isEmpty()) {
                    int minTileXOffset = Integer.MAX_VALUE;
                    int minTileYOffset = Integer.MAX_VALUE;
                    int minRegionX = Integer.MAX_VALUE;
                    int minRegionY = Integer.MAX_VALUE;
                    for (Region region : regions) {
                        minTileXOffset = Math.min(minTileXOffset, region.tileXOffset);
                        minTileYOffset = Math.min(minTileYOffset, region.tileYOffset);
                        minRegionX = Math.min(minRegionX, region.regionX);
                        minRegionY = Math.min(minRegionY, region.regionY);
                    }
                    for (Region region : regions) {
                        GameTexture texture = region.liquidData.getSmoothTexture();
                        if (texture == null) continue;
                        int drawX = region.tileXOffset - minTileXOffset;
                        int n = region.tileYOffset - minTileYOffset;
                        texture.initDraw().section(1, texture.getWidth() - 1, 1, texture.getHeight() - 1).draw(drawX + 200, n + 200);
                    }
                }
                level.liquidManager.getAdvancedMobHeightPercentDrawTest(perspective, camera, mouseX - 100, mouseY - 100, 200, 200).draw();
                GameTooltipManager.addTooltip(level.liquidManager.getAdvancedHeightTooltips(perspective, mouseX, mouseY), TooltipLocation.INTERACT_FOCUS);
                break;
            }
            case VILLAGE_TILES: {
                if (level.isServer()) {
                    OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(level, false);
                    OneWorldPirateVillageData pirateData = OneWorldPirateVillageData.getPirateVillageData(level, false);
                    SharedTextureDrawOptions options = new SharedTextureDrawOptions(GameResources.empty);
                    int startX = camera.getStartTileX();
                    int startY = camera.getStartTileY();
                    int endX = camera.getEndTileX();
                    int endY = camera.getEndTileY();
                    for (int x = startX; x <= endX; ++x) {
                        for (int y = startY; y <= endY; ++y) {
                            int n = camera.getTileDrawX(x);
                            int drawY = camera.getTileDrawY(y);
                            if (villageData != null && villageData.isVillageTile(x, y)) {
                                options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(new Color(255, 255, 0)).alpha(0.4f).pos(n, drawY);
                            }
                            if (pirateData == null || !pirateData.isPirateTile(x, y)) continue;
                            options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(new Color(255, 0, 0)).alpha(0.4f).pos(n, drawY);
                        }
                    }
                    options.draw();
                    break;
                }
                FontManager.bit.drawString(100.0f, 100.0f, "Village tiles only work on server levels", new FontOptions(32).outline());
                break;
            }
            case CURSED_ARENA_TILES: {
                int startY;
                SharedTextureDrawOptions options;
                if (level.isServer()) {
                    CursedCroneArenasLevelData arenaData = CursedCroneArenasLevelData.getCursedCroneArenasData(level, false);
                    if (arenaData != null) {
                        options = new SharedTextureDrawOptions(GameResources.empty);
                        int startX = camera.getStartTileX();
                        startY = camera.getStartTileY();
                        int endX = camera.getEndTileX();
                        int endY = camera.getEndTileY();
                        for (int x = startX; x <= endX; ++x) {
                            for (int y = startY; y <= endY; ++y) {
                                int drawX = camera.getTileDrawX(x);
                                int n = camera.getTileDrawY(y);
                                if (!arenaData.isArenaTile(x, y)) continue;
                                options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(new Color(255, 0, 0)).alpha(0.4f).pos(drawX, n);
                            }
                        }
                        options.draw();
                        break;
                    }
                    FontManager.bit.drawString(100.0f, 100.0f, "No village data found", new FontOptions(32).outline());
                    break;
                }
                FontManager.bit.drawString(100.0f, 100.0f, "Arena tiles only work on server levels", new FontOptions(32).outline());
                break;
            }
            case WATER_TYPE: {
                SharedTextureDrawOptions options = new SharedTextureDrawOptions(GameResources.empty);
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int drawY = camera.getTileDrawY(y);
                        if (!level.getTile((int)x, (int)y).isLiquid) continue;
                        int n = level.liquidManager.getSaltWaterDepth(x, y);
                        float green = n >= 0 ? (float)n / 11.0f : 1.0f;
                        options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(1.0f, 1.0f - green, 1.0f, 0.5f).pos(drawX, drawY);
                        boolean isSaltWater = level.liquidManager.isSaltWater(x, y);
                        Color color = isSaltWater ? (n >= 11 ? new Color(255, 0, 0) : new Color(255, 100, 0)) : new Color(100, 255, 100);
                        options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(color).alpha(0.4f).pos(drawX, drawY);
                    }
                }
                options.draw();
                break;
            }
            case BIOME_BLENDING: {
                Biome showingBiome = level.getBiome(mouseTileX, mouseTileY);
                FontManager.bit.drawString(100.0f, 100.0f, "Showing biome blending for: " + showingBiome.getDisplayName(), new FontOptions(20).outline());
                SharedTextureDrawOptions options = new SharedTextureDrawOptions(GameResources.empty);
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int n = camera.getTileDrawY(y);
                        for (BiomeBlendingValue blendValue : level.biomeBlendingManager.getBlendValues(x, y)) {
                            if (blendValue.biomeID != showingBiome.getID()) continue;
                            float value2 = (float)blendValue.value / (float)BiomeBlendingManager.MAX_VALUE;
                            options.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(value2, value2, value2, 0.5f).pos(drawX, n);
                        }
                    }
                }
                options.draw();
                break;
            }
            case WIND_OVERLAY: {
                QuadDrawOptionsList quadList = new QuadDrawOptionsList();
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int drawY = camera.getTileDrawY(y);
                        float f = level.weatherLayer.getWindAmount(x, y);
                        if (f < 0.0f) {
                            quadList.add(drawX, drawY, 32, 32, -f, 0.0f, 0.0f, 0.4f);
                            continue;
                        }
                        quadList.add(drawX, drawY, 32, 32, 0.0f, f, 0.0f, 0.4f);
                    }
                }
                quadList.draw();
            }
            case WIND_STATS: {
                int startTileX = level.limitTileXToBounds(camera.getStartTileX() - 100);
                int startTileY = level.limitTileYToBounds(camera.getStartTileY() - 100);
                int endTileX = level.limitTileXToBounds(camera.getEndTileX() + 100);
                int endTileY = level.limitTileYToBounds(camera.getEndTileY() + 100);
                int tileWidth = endTileX - startTileX;
                int tileHeight = endTileY - startTileY;
                GameRandomNoise.get2DebugDrawFull(100, 100, tileWidth, tileHeight, value -> level.weatherLayer.getWindAmount(startTileX + value.x, startTileY + value.y)).draw();
                int cameraStartX = camera.getStartTileX() - startTileX;
                int cameraStartY = camera.getStartTileY() - startTileY;
                int cameraTileWidth = camera.getEndTileX() - camera.getStartTileX();
                int n = camera.getEndTileY() - camera.getStartTileY();
                Renderer.drawRectangleLines(new Rectangle(cameraStartX, cameraStartY, cameraTileWidth, n), 100.0f, 100.0f, 1.0f, 1.0f, 1.0f, 0.5f);
                if (perspective != null) {
                    int pTileX = perspective.getTileX() - startTileX;
                    int pTileY = perspective.getTileY() - startTileY;
                    Renderer.drawCircle(100 + pTileX, 100 + pTileY, 5, 10, 1.0f, 1.0f, 1.0f, 1.0f, true);
                    Renderer.drawCircle(100 + pTileX, 100 + pTileY, 4, 10, 0.0f, 0.0f, 1.0f, 1.0f, true);
                    Renderer.drawCircle(100 + tileWidth + 8, 107, 5, 10, 1.0f, 1.0f, 1.0f, 1.0f, true);
                    Renderer.drawCircle(100 + tileWidth + 8, 107, 4, 10, 0.0f, 0.0f, 1.0f, 1.0f, true);
                    FontManager.bit.drawString(100 + tileWidth + 15, 100.0f, "You", new FontOptions(16).outline());
                }
                int midX = 100 + cameraStartX + cameraTileWidth / 2;
                int midY = 100 + cameraStartY + n / 2;
                Renderer.drawCircle(midX, midY, 40, 20, 1.0f, 1.0f, 1.0f, 0.5f, false);
                Renderer.drawCircle(midX, midY, 2, 5, 1.0f, 1.0f, 1.0f, 1.0f, true);
                Point2D.Double dir = level.weatherLayer.getWindDirFull();
                Renderer.drawLineRGBA(midX, midY, midX + (int)(dir.x * 40.0), midY + (int)(dir.y * 40.0), 1.0f, 1.0f, 1.0f, 1.0f);
                FontManager.bit.drawString(100.0f, 100 + tileHeight + 5, "Wind speed: " + level.weatherLayer.getWindSpeedFull(), new FontOptions(16).outline());
                level.weatherLayer.getOffsetXDebugDrawOptions(100, 150 + tileHeight, 300, 100, 500000L).draw();
                level.weatherLayer.getOffsetYDebugDrawOptions(100, 150 + tileHeight + 150, 300, 100, 500000L).draw();
                break;
            }
            case HOSTILE_SPAWN_OVERLAY: {
                GLDrawOptionsList lineList = new GLDrawOptionsList();
                Mob zombie = MobRegistry.getMob("zombie", level);
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int n = camera.getTileDrawY(y);
                        boolean valid = new MobSpawnLocation(zombie, x * 32 + 16, y * 32 + 16).checkStaticLightThreshold(null).validAndApply();
                        if (!valid) continue;
                        lineList.add(drawX, n, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX + 32, n, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX + 32, n, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX + 32, n + 32, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX + 32, n + 32, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX, n + 32, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX, n + 32, 1.0f, 0.0f, 0.0f, 1.0f);
                        lineList.add(drawX, n, 1.0f, 0.0f, 0.0f, 1.0f);
                    }
                }
                lineList.draw(1);
                break;
            }
            case INSIDE_LIGHT_MODIFIER: {
                QuadDrawOptionsList quadList = new QuadDrawOptionsList();
                int startX = camera.getStartTileX();
                int startY = camera.getStartTileY();
                int endX = camera.getEndTileX();
                int endY = camera.getEndTileY();
                for (int x = startX; x <= endX; ++x) {
                    for (int y = startY; y <= endY; ++y) {
                        int drawX = camera.getTileDrawX(x);
                        int drawY = camera.getTileDrawY(y);
                        quadList.add(drawX, drawY, 32, 32, level.lightManager.getInsideModifier(x, y), 0.0f, 0.0f, 0.4f);
                    }
                }
                quadList.draw();
                break;
            }
        }
        if (showRegionBounds) {
            int regionStartX = level.regionManager.getRegionCoordByTile(camera.getStartTileX());
            int regionStartY = level.regionManager.getRegionCoordByTile(camera.getStartTileY());
            int regionEndX = level.regionManager.getRegionCoordByTile(camera.getEndTileX());
            int regionEndY = level.regionManager.getRegionCoordByTile(camera.getEndTileY());
            SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(level);
            for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
                int tileXOffset = level.regionManager.getTileCoordByRegion(regionX);
                int tileWidth = level.regionManager.getRegionTileWidth(regionX, tileXOffset);
                if (tileWidth <= 0) continue;
                for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                    int n = level.regionManager.getTileCoordByRegion(regionY);
                    int tileHeight = level.regionManager.getRegionTileHeight(regionY, n);
                    if (tileHeight <= 0) continue;
                    int drawX = camera.getTileDrawX(tileXOffset);
                    int drawY = camera.getTileDrawY(n);
                    Rectangle rectangle = new Rectangle(drawX, drawY, tileWidth * 32, tileHeight * 32);
                    Renderer.drawRectangleLines(rectangle, 1.0f, 1.0f, 1.0f, 0.5f);
                    FormFlow formFlow = new FormFlow(drawY + 5);
                    int settlementUniqueID = settlementsData.getSettlementUniqueIDAtRegion(level.getIdentifier(), regionX, regionY);
                    FontOptions fontOptions = new FontOptions(16).outline();
                    FontManager.bit.drawString(drawX, formFlow.next(20), "Region: " + regionX + "x" + regionY, fontOptions);
                    FontManager.bit.drawString(drawX, formFlow.next(20), "Tile: " + tileXOffset + "x" + n, fontOptions);
                    FontManager.bit.drawString(drawX, formFlow.next(20), "Size: " + tileWidth + "x" + tileHeight, fontOptions);
                    FontManager.bit.drawString(drawX, formFlow.next(20), "Settlement: " + settlementUniqueID, fontOptions);
                    if (!level.regionManager.isRegionLoaded(regionX, regionY)) {
                        FontManager.bit.drawString(drawX, formFlow.next(20), "Not loaded", fontOptions);
                        continue;
                    }
                    Region region = level.regionManager.getRegion(regionX, regionY, false);
                    if (region == null || !region.isPirateVillageRegion) continue;
                    FontManager.bit.drawString(drawX, formFlow.next(20), "- Pirate village region", fontOptions);
                }
            }
        }
        if (showWorldPresetRegionBounds) {
            int regionX = GameMath.getRegionCoordByTile(perspective == null ? camera.getStartTileX() : perspective.getTileX());
            int regionY = GameMath.getRegionCoordByTile(perspective == null ? camera.getStartTileY() : perspective.getTileY());
            int presetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionX);
            int presetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionY);
            int presetRegionTileSize = 1024;
            int drawX = camera.getTileDrawX(presetRegionX * presetRegionTileSize);
            int drawY = camera.getTileDrawY(presetRegionY * presetRegionTileSize);
            Rectangle rectangle = new Rectangle(drawX, drawY, presetRegionTileSize * 32, presetRegionTileSize * 32);
            Renderer.drawRectangleLines(rectangle, 1.0f, 1.0f, 1.0f, 0.5f);
        }
        if (perspective == null || currentState.getFormManager() == null || currentState.getFormManager().isMouseOver() && !Input.lastInputIsController) {
            return;
        }
        InventoryItem selectedItem = perspective.getSelectedItem();
        AttackHandler attackHandler = perspective.getAttackHandler();
        if (attackHandler != null) {
            attackHandler.drawHUDItemSelected(camera, level, perspective, selectedItem);
        } else if (selectedItem != null) {
            selectedItem.item.drawHUDItemSelected(camera, level, perspective, selectedItem);
        }
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            ControllerInteractTarget target = perspective.getControllerInteractTarget(false, perspective.getCurrentAttackHeight(), camera);
            if (target != null) {
                DrawOptions drawOptions = target.getDrawOptions();
                if (drawOptions != null) {
                    drawOptions.draw();
                }
                target.onCurrentlyFocused();
            }
            if (attackHandler != null) {
                attackHandler.drawControllerAimPos(camera, level, perspective, selectedItem);
            } else if (selectedItem != null) {
                selectedItem.item.drawControllerAimPos(camera, level, perspective, selectedItem);
            }
        }
        if (!debugActive) {
            PlayerMob p2;
            hoveringClient = HUD.streamClients(level).filter(c -> c != null && c.playerMob != null && c.hasSpawned() && !c.isDead() && c.isSamePlace(level) && c.playerMob != perspective).filter(c -> c.playerMob.getSelectBox().contains(mouseX, mouseY)).findFirst().orElse(null);
            if (hoveringClient != null && (p2 = hoveringClient.playerMob).onMouseHover(camera, perspective, false) | (selectedItem != null && selectedItem.item.onMouseHoverMob(selectedItem, camera, perspective, p2, false))) {
                if (debugShow == DebugShow.PATHS) {
                    LinkedList<SubmittedPath> presetRegionTileSize = paths;
                    synchronized (presetRegionTileSize) {
                        paths.stream().filter(path -> ((TilePathfinding)path.path.finder).mob.getUniqueID() == p2.getUniqueID()).findFirst().ifPresent(path -> TilePathfinding.drawPathProcess(path.path, camera));
                    }
                }
                return;
            }
            for (Mob mob : level.entityManager.mobs.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                if (!mob.getSelectBox().contains(mouseX, mouseY) || !(mob.onMouseHover(camera, perspective, false) | (selectedItem != null && selectedItem.item.onMouseHoverMob(selectedItem, camera, perspective, mob, false)))) continue;
                if (debugShow == DebugShow.PATHS) {
                    LinkedList<SubmittedPath> drawX = paths;
                    synchronized (drawX) {
                        paths.stream().filter(path -> ((TilePathfinding)path.path.finder).mob.getUniqueID() == mob.getUniqueID()).findFirst().ifPresent(path -> TilePathfinding.drawPathProcess(path.path, camera));
                    }
                }
                return;
            }
            for (PickupEntity pickup : level.entityManager.pickups.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                if (!pickup.shouldDraw() || !pickup.getSelectBox().contains(mouseX, mouseY) || !(pickup.onMouseHover(camera, perspective, false) | (selectedItem != null && selectedItem.item.onMouseHoverPickup(selectedItem, camera, perspective, pickup, false)))) continue;
                return;
            }
            for (Projectile projectile : level.entityManager.projectiles.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                Shape selectBox = projectile.getSelectBox();
                if (!selectBox.contains(mouseX, mouseY) || !projectile.onMouseHover(camera, perspective, false)) continue;
                return;
            }
            LevelObject interactObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, lo -> lo.canInteract(perspective), null);
            if (interactObject != null) {
                String controlMsg;
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                interactObject.onMouseHover(camera, perspective, false);
                if (Settings.showControlTips && (controlMsg = interactObject.getInteractTip(perspective, false)) != null) {
                    GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, controlMsg, interactObject.isInInteractRange(perspective) ? 1.0f : 0.7f), TooltipLocation.INTERACT_FOCUS);
                }
            }
            LevelObject hitObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, null);
            if (interactObject == null || interactObject.tileX != hitObject.tileX || interactObject.tileY != hitObject.tileY) {
                hitObject.onMouseHover(camera, perspective, false);
            }
            if (level.drawUtils.drawWire(perspective) && level.logicLayer.hasGate(mouseTileX, mouseTileY)) {
                level.logicLayer.getLogicGate(mouseTileX, mouseTileY).onMouseHover(level, mouseTileX, mouseTileY, perspective, false);
            }
            if (selectedItem != null) {
                selectedItem.item.onMouseHoverTile(selectedItem, camera, perspective, mouseX, mouseY, new TilePosition(level, mouseTileX, mouseTileY), false);
            }
        } else {
            PlayerMob p3;
            hoveringClient = HUD.streamClients(level).filter(c -> c != null && c.playerMob != null && c.hasSpawned() && !c.isDead() && c.isSamePlace(level)).filter(c -> c.playerMob.getSelectBox().contains(mouseX, mouseY)).findFirst().orElse(null);
            if (hoveringClient != null && (p3 = hoveringClient.playerMob).onMouseHover(camera, perspective, true) | (selectedItem != null && selectedItem.item.onMouseHoverMob(selectedItem, camera, perspective, p3, true))) {
                Rectangle col = p3.getCollision();
                Rectangle hit = p3.getHitBox();
                Renderer.initQuadDraw(col.width, col.height).color(1.0f, 0.0f, 0.0f, 0.3f).draw(camera.getDrawX(col.x), camera.getDrawY(col.y));
                Renderer.initQuadDraw(hit.width, hit.height).color(0.0f, 0.0f, 1.0f, 0.3f).draw(camera.getDrawX(hit.x), camera.getDrawY(hit.y));
                HUD.levelBoundOptions(camera, p3.getSelectBox()).draw();
                if (debugShow == DebugShow.PATHS) {
                    LinkedList<SubmittedPath> drawY = paths;
                    synchronized (drawY) {
                        paths.stream().filter(path -> ((TilePathfinding)path.path.finder).mob.getUniqueID() == p3.getUniqueID()).findFirst().ifPresent(path -> TilePathfinding.drawPathProcess(path.path, camera));
                    }
                }
                return;
            }
            for (Mob mob : level.entityManager.mobs.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                if (!mob.getSelectBox().contains(mouseX, mouseY) || !(mob.onMouseHover(camera, perspective, true) | (selectedItem != null && selectedItem.item.onMouseHoverMob(selectedItem, camera, perspective, mob, true)))) continue;
                Rectangle col = mob.getCollision();
                Rectangle hit = mob.getHitBox();
                Renderer.initQuadDraw(col.width, col.height).color(1.0f, 0.0f, 0.0f, 0.3f).draw(camera.getDrawX(col.x), camera.getDrawY(col.y));
                Renderer.initQuadDraw(hit.width, hit.height).color(0.0f, 0.0f, 1.0f, 0.3f).draw(camera.getDrawX(hit.x), camera.getDrawY(hit.y));
                HUD.levelBoundOptions(camera, mob.getSelectBox()).draw();
                if (debugShow == DebugShow.PATHS) {
                    LinkedList<SubmittedPath> rectangle = paths;
                    synchronized (rectangle) {
                        paths.stream().filter(p -> ((TilePathfinding)p.path.finder).mob.getUniqueID() == mob.getUniqueID()).findFirst().ifPresent(p -> TilePathfinding.drawPathProcess(p.path, camera));
                    }
                }
                return;
            }
            for (PickupEntity pickup : level.entityManager.pickups.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                if (!pickup.shouldDraw() || !pickup.getSelectBox().contains(mouseX, mouseY)) continue;
                Rectangle col = pickup.getCollision();
                Renderer.initQuadDraw(col.width, col.height).color(0.0f, 0.0f, 1.0f, 0.3f).draw(camera.getDrawX(col.x), camera.getDrawY(col.y));
                HUD.levelBoundOptions(camera, pickup.getSelectBox()).draw();
                pickup.onMouseHover(camera, perspective, true);
                if (selectedItem != null) {
                    selectedItem.item.onMouseHoverPickup(selectedItem, camera, perspective, pickup, true);
                }
                return;
            }
            for (Projectile projectile : level.entityManager.projectiles.getInRegionRangeByTile(mouseTileX, mouseTileY, 1)) {
                Shape selectBox = projectile.getSelectBox();
                if (!selectBox.contains(mouseX, mouseY)) continue;
                Renderer.drawShape(selectBox, camera, true, 1.0f, 0.0f, 0.0f, 0.3f);
                float hitLength = Math.max(projectile.getHitLength(), 16.0f);
                Line2D.Float hitLine = new Line2D.Float(projectile.x, projectile.y, projectile.x + projectile.dx * hitLength, projectile.y + projectile.dy * hitLength);
                Renderer.drawShape(projectile.toHitbox(hitLine), camera, true, 0.0f, 0.0f, 1.0f, 0.3f);
                projectile.onMouseHover(camera, perspective, true);
                return;
            }
            int mouseTileXDraw = camera.getTileDrawX(mouseTileX);
            int mouseTileYDraw = camera.getTileDrawY(mouseTileY);
            if (!window.isKeyDown(340)) {
                List list;
                MultiTile multiTile = level.getObject(mouseTileX, mouseTileY).getMultiTile(level, 0, mouseTileX, mouseTileY);
                HUD.tileBoundOptions(camera, multiTile.getTileRectangle(mouseTileX, mouseTileY)).draw();
                List<Rectangle> collisions = level.getObject(mouseTileX, mouseTileY).getCollisions(level, mouseTileX, mouseTileY, level.getObjectRotation(mouseTileX, mouseTileY));
                for (Rectangle col : collisions) {
                    Renderer.initQuadDraw(col.width, col.height).color(1.0f, 0.0f, 0.0f, 0.5f).draw(camera.getDrawX(col.x), camera.getDrawY(col.y));
                }
                List<ObjectHoverHitbox> hitBoxes = level.getObject(mouseTileX, mouseTileY).getHoverHitboxes(level, 0, mouseTileX, mouseTileY);
                for (Rectangle rectangle : hitBoxes) {
                    Renderer.initQuadDraw(rectangle.width, rectangle.height).color(0.0f, 0.0f, 1.0f, 0.5f).draw(camera.getDrawX(rectangle.x), camera.getDrawY(rectangle.y));
                }
                StringTooltips tooltips = new StringTooltips();
                tooltips.add("x: " + mouseTileX + ", y: " + mouseTileY + (level.isProtected(mouseTileX, mouseTileY) ? " (Protected)" : ""));
                tooltips.add("Biome: " + level.getBiome(mouseTileX, mouseTileY).getStringID() + " (" + level.biomeLayer.getBiomeID(mouseTileX, mouseTileY) + ")");
                tooltips.add("Tile: " + level.getTileName(mouseTileX, mouseTileY).translate() + " (" + level.getTileID(mouseTileX, mouseTileY) + "), Placed: " + level.tileLayer.isPlayerPlaced(mouseTileX, mouseTileY));
                level.objectLayer.addObjectsDebugTooltip(tooltips, mouseTileX, mouseTileY);
                if (level.logicLayer.hasGate(mouseTileX, mouseTileY)) {
                    GameLogicGate gameLogicGate = level.logicLayer.getLogicGate(mouseTileX, mouseTileY);
                    tooltips.add("Logic gate: " + gameLogicGate.getDisplayName() + " (" + gameLogicGate.getID() + ")");
                }
                StringBuilder stringBuilder = new StringBuilder();
                byte wireData = level.wireManager.getWireData(mouseTileX, mouseTileY);
                for (int i = 0; i < 8; ++i) {
                    stringBuilder.append(wireData >> i & 1);
                }
                tooltips.add("Wire: " + stringBuilder);
                tooltips.add("Light: " + level.lightManager.getLightLevel(mouseTileX, mouseTileY));
                tooltips.add("Static light: " + level.lightManager.getStaticLight(mouseTileX, mouseTileY));
                tooltips.add("Particle light: " + level.lightManager.getParticleLight(mouseTileX, mouseTileY));
                List<SourcedGameLight> lightSources = level.lightManager.getStaticLightSources(mouseTileX, mouseTileY);
                if (!lightSources.isEmpty()) {
                    tooltips.add("Light sources:");
                    for (SourcedGameLight sourcedGameLight : lightSources) {
                        tooltips.add("  " + sourcedGameLight);
                    }
                }
                if (debugShow == DebugShow.INSIDE_LIGHT_MODIFIER) {
                    tooltips.add("Inside light modifier: " + level.lightManager.getInsideModifier(mouseTileX, mouseTileY));
                    List<SourcedLightModifier> insideSources = level.lightManager.getInsideLightSources(mouseTileX, mouseTileY);
                    if (insideSources == null) {
                        tooltips.add("Inside light sources: null");
                    } else {
                        tooltips.add("Inside light sources: ");
                        for (SourcedLightModifier sourcedLightModifier : insideSources) {
                            tooltips.add("  " + sourcedLightModifier.sourceX + "x" + sourcedLightModifier.sourceY + ": " + sourcedLightModifier.getValue());
                        }
                    }
                }
                tooltips.add("Height: " + level.liquidManager.getHeight(mouseTileX, mouseTileY));
                tooltips.add("Salt water: " + level.liquidManager.getSaltWaterDepth(mouseTileX, mouseTileY));
                tooltips.add("RoomID: " + level.getRoomID(mouseTileX, mouseTileY) + ", size: " + level.getRoomSize(mouseTileX, mouseTileY));
                tooltips.add("RegionID: " + level.getRegionID(mouseTileX, mouseTileY));
                tooltips.add("Outside: " + level.isOutside(mouseTileX, mouseTileY));
                ObjectEntity ent = level.entityManager.getObjectEntity(mouseTileX, mouseTileY);
                if (ent != null) {
                    tooltips.add("Entity: " + ent.type);
                }
                if (!(list = level.jobsLayer.streamJobsInTile(mouseTileX, mouseTileY).collect(Collectors.toList())).isEmpty()) {
                    tooltips.add("Jobs:");
                    for (LevelJob job : list) {
                        tooltips.add("\t" + job);
                    }
                }
                level.regionManager.addSplattingDebugTooltips(mouseTileX, mouseTileY, tooltips);
                level.regionManager.addBiomeBlendingDebugTooltips(mouseTileX, mouseTileY, tooltips);
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.BOTTOM_LEFT);
                LevelObject levelObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, lo -> lo.canInteract(perspective), null);
                if (levelObject != null) {
                    String controlMsg;
                    Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    if (Settings.showControlTips && (controlMsg = levelObject.getInteractTip(perspective, true)) != null) {
                        GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, controlMsg, levelObject.isInInteractRange(perspective) ? 1.0f : 0.7f), TooltipLocation.INTERACT_FOCUS);
                    }
                }
                LevelObject hitObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, null);
                hitObject.onMouseHover(camera, perspective, true);
                if (level.drawUtils.drawWire(perspective) && level.logicLayer.hasGate(mouseTileX, mouseTileY)) {
                    level.logicLayer.getLogicGate(mouseTileX, mouseTileY).onMouseHover(level, mouseTileX, mouseTileY, perspective, true);
                }
                if (selectedItem != null) {
                    selectedItem.item.onMouseHoverTile(selectedItem, camera, perspective, mouseX, mouseY, new TilePosition(level, mouseTileX, mouseTileY), true);
                }
            } else {
                int drawY;
                int drawX;
                HUD.tileBoundOptions(camera, mouseTileX, mouseTileY, mouseTileX, mouseTileY).draw();
                FontOptions fontOptions = new FontOptions(12);
                int tileX = mouseTileX + 1;
                while ((drawX = camera.getTileDrawX(tileX)) <= window.getSceneWidth()) {
                    if (tileX % 2 == 0) {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 0.0f, 1.0f, 0.2f).draw(drawX, mouseTileYDraw);
                    } else {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f, 0.2f).draw(drawX, mouseTileYDraw);
                    }
                    FontManager.bit.drawString(drawX, mouseTileYDraw, "" + Math.abs(++tileX - mouseTileX - 1), fontOptions);
                }
                tileX = mouseTileX - 1;
                while ((drawX = camera.getTileDrawX(tileX)) + 32 >= 0) {
                    if (tileX % 2 == 0) {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 0.0f, 1.0f, 0.2f).draw(drawX, mouseTileYDraw);
                    } else {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f, 0.2f).draw(drawX, mouseTileYDraw);
                    }
                    FontManager.bit.drawString(drawX, mouseTileYDraw, "" + Math.abs(--tileX - mouseTileX + 1), fontOptions);
                }
                int tileY = mouseTileY + 1;
                while ((drawY = camera.getTileDrawY(tileY)) <= window.getSceneHeight()) {
                    if (tileY % 2 == 0) {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 0.0f, 1.0f, 0.2f).draw(mouseTileXDraw, drawY);
                    } else {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f, 0.2f).draw(mouseTileXDraw, drawY);
                    }
                    FontManager.bit.drawString(mouseTileXDraw, drawY, "" + Math.abs(++tileY - mouseTileY - 1), fontOptions);
                }
                tileY = mouseTileY - 1;
                while ((drawY = camera.getTileDrawY(tileY)) + 32 >= 0) {
                    if (tileY % 2 == 0) {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 0.0f, 1.0f, 0.2f).draw(mouseTileXDraw, drawY);
                    } else {
                        Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f, 0.2f).draw(mouseTileXDraw, drawY);
                    }
                    FontManager.bit.drawString(mouseTileXDraw, drawY, "" + Math.abs(--tileY - mouseTileY + 1), fontOptions);
                }
            }
        }
        if (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            Point mousePos;
            if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                Point2D.Float aimDir = perspective.getControllerAimDir();
                mousePos = new Point((int)(perspective.x + aimDir.x * 100.0f), (int)(perspective.y + aimDir.y * 100.0f));
            } else {
                mousePos = new Point(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
            }
            ToolDamageItem.SmartMineTarget tile = null;
            if (selectedItem != null && selectedItem.item instanceof ToolDamageItem) {
                tile = ((ToolDamageItem)selectedItem.item).getFirstSmartHitTile(level, perspective, selectedItem, mousePos.x, mousePos.y);
            }
            if (tile != null) {
                if (tile.isObject) {
                    GameObject object = level.getObject(tile.x, tile.y);
                    MultiTile multiTile = object.getMultiTile(level, tile.priorityObjectLayerID == -1 ? 0 : tile.priorityObjectLayerID, tile.x, tile.y);
                    Rectangle rect = multiTile.getTileRectangle(tile.x, tile.y);
                    HUD.tileBoundOptions(camera, new Color(255, 255, 255), true, rect).draw();
                } else {
                    HUD.tileBoundOptions(camera, new Color(255, 255, 255), true, tile.x, tile.y, tile.x, tile.y).draw();
                }
            }
        }
    }

    private static Stream<NetworkClient> streamClients(Level level) {
        if (level.isClient()) {
            return level.getClient().streamClients().map(c -> c);
        }
        if (level.isServer()) {
            return level.getServer().streamClients().map(c -> c);
        }
        return Stream.empty();
    }

    public static void reset() {
        debugActive = false;
    }

    private static void drawCells(SubRegion sr, Color color, Level level, GameCamera camera) {
        int startTileX = level.limitTileXToBounds(camera.getStartTileX());
        int startTileY = level.limitTileYToBounds(camera.getStartTileY());
        int endTileX = level.limitTileXToBounds(camera.getEndTileX() + 2);
        int endTileY = level.limitTileYToBounds(camera.getEndTileY() + 2);
        for (Point tile : sr.getLevelTiles()) {
            if (tile.x < startTileX || tile.x >= endTileX || tile.y < startTileY || tile.y >= endTileY) continue;
            Renderer.initQuadDraw(32, 32).color(color).alpha(0.15f).draw(camera.getTileDrawX(tile.x), camera.getTileDrawY(tile.y));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void submitPath(PathResult<Point, TilePathfinding> path) {
        if (!debugActive && debugShow != DebugShow.PATHS) {
            return;
        }
        LinkedList<SubmittedPath> linkedList = paths;
        synchronized (linkedList) {
            long time = ((TilePathfinding)path.finder).level.getWorldEntity().getLocalTime();
            paths.removeIf(p -> ((TilePathfinding)p.path.finder).mob.getUniqueID() == ((TilePathfinding)path.finder).mob.getUniqueID());
            paths.add(new SubmittedPath(path, time));
        }
    }

    public static DrawOptions selectBoundOptions(Color color, boolean outline, int startX, int startY, int endX, int endY) {
        DrawOptionsList drawOptions = new DrawOptionsList();
        GameTexture texture = outline ? Settings.UI.select_outline : Settings.UI.select;
        drawOptions.add(texture.initDraw().sprite(0, 0, 16).color(color).pos(startX, startY));
        drawOptions.add(texture.initDraw().sprite(1, 0, 16).color(color).pos(endX - 16, startY));
        drawOptions.add(texture.initDraw().sprite(0, 1, 16).color(color).pos(startX, endY - 16));
        drawOptions.add(texture.initDraw().sprite(1, 1, 16).color(color).pos(endX - 16, endY - 16));
        return drawOptions;
    }

    public static DrawOptions selectBoundOptions(Color color, boolean outline, Rectangle rectangle) {
        return HUD.selectBoundOptions(color, outline, rectangle.x, rectangle.y, rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1);
    }

    public static DrawOptions levelBoundOptions(GameCamera camera, Color color, boolean outline, int startX, int startY, int endX, int endY) {
        return HUD.selectBoundOptions(color, outline, camera.getDrawX(startX), camera.getDrawY(startY), camera.getDrawX(endX), camera.getDrawY(endY));
    }

    public static DrawOptions levelBoundOptions(GameCamera camera, int startX, int startY, int endX, int endY) {
        return HUD.levelBoundOptions(camera, Color.WHITE, false, startX, startY, endX, endY);
    }

    public static DrawOptions levelBoundOptions(GameCamera camera, Color color, boolean outline, Rectangle rectangle) {
        return HUD.levelBoundOptions(camera, color, outline, rectangle.x, rectangle.y, rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1);
    }

    public static DrawOptions levelBoundOptions(GameCamera camera, Rectangle rectangle) {
        return HUD.levelBoundOptions(camera, Color.WHITE, false, rectangle);
    }

    public static DrawOptions tileBoundOptions(GameCamera camera, Color color, boolean outline, int startX, int startY, int endX, int endY) {
        DrawOptionsList drawOptions = new DrawOptionsList();
        GameTexture texture = outline ? Settings.UI.select_outline : Settings.UI.select;
        drawOptions.add(texture.initDraw().sprite(0, 0, 16).color(color).pos(camera.getTileDrawX(startX), camera.getTileDrawY(startY)));
        drawOptions.add(texture.initDraw().sprite(1, 0, 16).color(color).pos(camera.getTileDrawX(endX) + 16, camera.getTileDrawY(startY)));
        drawOptions.add(texture.initDraw().sprite(0, 1, 16).color(color).pos(camera.getTileDrawX(startX), camera.getTileDrawY(endY) + 16));
        drawOptions.add(texture.initDraw().sprite(1, 1, 16).color(color).pos(camera.getTileDrawX(endX) + 16, camera.getTileDrawY(endY) + 16));
        return drawOptions;
    }

    public static DrawOptions tileBoundOptions(GameCamera camera, int startX, int startY, int endX, int endY) {
        return HUD.tileBoundOptions(camera, Color.WHITE, false, startX, startY, endX, endY);
    }

    public static DrawOptions tileBoundOptions(GameCamera camera, Color color, boolean outline, Rectangle rectangle) {
        return HUD.tileBoundOptions(camera, color, outline, rectangle.x, rectangle.y, rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1);
    }

    public static DrawOptions tileBoundOptions(GameCamera camera, Rectangle rectangle) {
        return HUD.tileBoundOptions(camera, Color.WHITE, false, rectangle);
    }

    public static DrawOptionsBox getDirectionIndicator(float fromX, float fromY, float targetX, float targetY, ArrayList<String> lines, int linesWidth, FontOptions fontOptions, GameCamera camera) {
        Rectangle inScreen = new Rectangle(camera.getX() + 25, camera.getY() + 25, camera.getWidth() - 50, camera.getHeight() - 50);
        if (!inScreen.contains(targetX, targetY)) {
            Point2D.Float dir = GameMath.normalize(targetX - fromX, targetY - fromY);
            int drawX = camera.getWidth() / 2 + (int)(dir.x * (float)camera.getWidth() / 3.0f);
            int drawY = camera.getHeight() / 2 + (int)(dir.y * (float)camera.getHeight() / 3.0f);
            String distance = "(" + (int)GameMath.pixelsToMeters((float)new Point2D.Float(fromX, fromY).distance(targetX, targetY)) + "m)";
            int distanceWidth = FontManager.bit.getWidthCeil(distance, fontOptions);
            int maxWidth = Math.max(linesWidth, distanceWidth);
            final Rectangle drawBounds = new Rectangle(drawX - maxWidth / 2, drawY, maxWidth - 16, 36);
            final DrawOptionsList drawOptions = new DrawOptionsList();
            for (int i = 0; i < lines.size(); ++i) {
                String text = lines.get(i);
                int width = FontManager.bit.getWidthCeil(text, fontOptions);
                drawOptions.add(new StringDrawOptions(fontOptions, text).pos(drawX - width / 2, drawBounds.y + i * 16));
            }
            drawOptions.add(new StringDrawOptions(fontOptions, distance).pos(drawX - distanceWidth / 2, drawBounds.y + lines.size() * 16));
            return new DrawOptionsBox(){

                @Override
                public void draw() {
                    drawOptions.draw();
                }

                @Override
                public Rectangle getBoundingBox() {
                    return drawBounds;
                }
            };
        }
        return null;
    }

    public static DrawOptionsBox getDirectionIndicator(float fromX, float fromY, Mob target, ArrayList<String> lines, FontOptions fontOptions, GameCamera camera) {
        int maxWidth = lines.stream().mapToInt(text -> FontManager.bit.getWidthCeil((String)text, fontOptions)).max().orElse(0);
        DrawOptionsBox directionIndicator = HUD.getDirectionIndicator(fromX, fromY, target.x, target.y, lines, maxWidth, fontOptions, camera);
        if (directionIndicator != null) {
            return directionIndicator;
        }
        int drawX = camera.getDrawX(target.getDrawX());
        int drawY = camera.getDrawY(target.getDrawY()) - 60;
        final Rectangle drawBounds = new Rectangle(drawX - maxWidth / 2, drawY, maxWidth, lines.size() * 20);
        if (target.isHealthBarVisible()) {
            Rectangle healthBarBounds = target.getHealthBarBounds(target.getDrawX(), target.getDrawY());
            Rectangle healthBarDrawBounds = new Rectangle(camera.getDrawX(healthBarBounds.x), camera.getDrawY(healthBarBounds.y), healthBarBounds.width, healthBarBounds.height);
            if (healthBarDrawBounds.intersects(drawBounds)) {
                drawBounds.y = healthBarDrawBounds.y - 16 - 4;
            }
        }
        final DrawOptionsList drawOptions = new DrawOptionsList();
        for (int i = 0; i < lines.size(); ++i) {
            String text2 = lines.get(i);
            int width = FontManager.bit.getWidthCeil(text2, fontOptions);
            drawOptions.add(new StringDrawOptions(fontOptions, text2).pos(drawX - width / 2, drawBounds.y + i * 16));
        }
        return new DrawOptionsBox(){

            @Override
            public void draw() {
                drawOptions.draw();
            }

            @Override
            public Rectangle getBoundingBox() {
                return drawBounds;
            }
        };
    }

    public static SharedTextureDrawOptions getOutlinesDrawOptions(GameTexture texture, int edgeResolution, int drawX, int drawY, int width, int height) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addOutlines(drawOptions, edgeResolution, drawX, drawY, width, height);
        return drawOptions;
    }

    public static DrawOptions getTrianglesDrawOptions(float[] xCoords, float[] yCoords, float[][] colors, boolean filled, float xOffset, float yOffset) {
        return HUD.getGLDrawOptions(4, xCoords, yCoords, colors, xOffset, yOffset);
    }

    public static DrawOptions getQuadsDrawOptions(float[] xCoords, float[] yCoords, float[][] colors, boolean filled, float xOffset, float yOffset) {
        return HUD.getGLDrawOptions(7, xCoords, yCoords, colors, xOffset, yOffset);
    }

    public static DrawOptions getPolygonDrawOptions(float[] xCoords, float[] yCoords, float[][] colors, boolean filled, float xOffset, float yOffset) {
        int drawMode = xCoords.length <= 2 ? 1 : (filled ? 9 : 2);
        return HUD.getGLDrawOptions(drawMode, xCoords, yCoords, colors, xOffset, yOffset);
    }

    public static DrawOptions getGLDrawOptions(int drawMode, float[] xCoords, float[] yCoords, float[][] colors, float xOffset, float yOffset) {
        if (xCoords.length != yCoords.length) {
            throw new IllegalArgumentException("x and y coords must be same length");
        }
        if (colors.length != xCoords.length) {
            throw new IllegalArgumentException("colors and coords must be same length");
        }
        return () -> {
            GameTexture.unbindTexture();
            GL11.glLoadIdentity();
            GL11.glBegin((int)drawMode);
            for (int i = 0; i < xCoords.length; ++i) {
                float[] color = colors[i];
                GL11.glColor4f((float)color[0], (float)color[1], (float)color[2], (float)color[3]);
                GL11.glVertex2f((float)(xCoords[i] + xOffset), (float)(yCoords[i] + yOffset));
            }
            GL11.glEnd();
        };
    }

    public static DrawOptions getPolygonDrawOptions(float[] xCoords, float[] yCoords, Color color, boolean filled, float xOffset, float yOffset) {
        return HUD.getPolygonDrawOptions(xCoords, yCoords, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f, filled, xOffset, yOffset);
    }

    public static DrawOptions getPolygonDrawOptions(float[] xCoords, float[] yCoords, float red, float green, float blue, float alpha, boolean filled, float xOffset, float yOffset) {
        int drawMode = xCoords.length <= 2 ? 1 : (filled ? 9 : 2);
        return HUD.getGLDrawOptions(drawMode, xCoords, yCoords, red, green, blue, alpha, xOffset, yOffset);
    }

    public static DrawOptions getGLDrawOptions(int drawMode, float[] xCoords, float[] yCoords, Color color, float xOffset, float yOffset) {
        return HUD.getGLDrawOptions(drawMode, xCoords, yCoords, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f, xOffset, yOffset);
    }

    public static DrawOptions getGLDrawOptions(int drawMode, float[] xCoords, float[] yCoords, float red, float green, float blue, float alpha, float xOffset, float yOffset) {
        if (xCoords.length != yCoords.length) {
            throw new IllegalArgumentException("x and y coords must be same length");
        }
        return () -> {
            GameTexture.unbindTexture();
            GL11.glLoadIdentity();
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
            GL11.glBegin((int)drawMode);
            for (int i = 0; i < xCoords.length; ++i) {
                GL11.glVertex2f((float)(xCoords[i] + xOffset), (float)(yCoords[i] + yOffset));
            }
            GL11.glEnd();
        };
    }

    public static DrawOptions getArrowHitboxIndicator(float fromX, float fromY, float dirX, float dirY, int range, int width, Color startColor, Color endColor, Color edgeColor) {
        DrawOptionsList out = new DrawOptionsList();
        float pointRange = (float)width / 3.0f;
        float quadRange = (float)range - pointRange;
        float halfWidth = (float)width / 2.0f;
        if (edgeColor != null) {
            float edgeQuadRange = quadRange - pointRange / 7.8539815f;
            Point2D.Float leftPos = GameMath.getPerpendicularPoint(fromX, fromY, halfWidth, dirX, dirY);
            Point2D.Float rightPos = GameMath.getPerpendicularPoint(fromX, fromY, -halfWidth, dirX, dirY);
            float edgeWidth = 5.0f;
            Point2D.Float leftEdgePos = GameMath.getPerpendicularPoint(fromX, fromY, halfWidth - edgeWidth, dirX, dirY);
            Point2D.Float rightEdgePos = GameMath.getPerpendicularPoint(fromX, fromY, -(halfWidth - edgeWidth), dirX, dirY);
            float[] xCoords = new float[]{leftEdgePos.x, leftEdgePos.x + dirX * edgeQuadRange, rightEdgePos.x, rightEdgePos.x, leftEdgePos.x + dirX * edgeQuadRange, rightEdgePos.x + dirX * edgeQuadRange, leftEdgePos.x + dirX * edgeQuadRange, fromX + dirX * ((float)range - edgeWidth), rightEdgePos.x + dirX * edgeQuadRange};
            float[] yCoords = new float[]{leftEdgePos.y, leftEdgePos.y + dirY * edgeQuadRange, rightEdgePos.y, rightEdgePos.y, leftEdgePos.y + dirY * edgeQuadRange, rightEdgePos.y + dirY * edgeQuadRange, leftEdgePos.y + dirY * edgeQuadRange, fromY + dirY * ((float)range - edgeWidth), rightEdgePos.y + dirY * edgeQuadRange};
            float[][] colors = HUD.toFloatColor2D(startColor, endColor, startColor, startColor, endColor, endColor, endColor, endColor, endColor);
            out.add(HUD.getTrianglesDrawOptions(xCoords, yCoords, colors, true, 0.0f, 0.0f));
            float[] edgeXCoords = new float[]{leftPos.x, leftPos.x + dirX * quadRange, leftEdgePos.x + dirX * edgeQuadRange, leftEdgePos.x, leftPos.x + dirX * quadRange, fromX + dirX * (float)range, fromX + dirX * ((float)range - edgeWidth), leftEdgePos.x + dirX * edgeQuadRange, fromX + dirX * (float)range, rightPos.x + dirX * quadRange, rightEdgePos.x + dirX * edgeQuadRange, fromX + dirX * ((float)range - edgeWidth), rightPos.x + dirX * quadRange, rightPos.x, rightEdgePos.x, rightEdgePos.x + dirX * edgeQuadRange};
            float[] edgeYCoords = new float[]{leftPos.y, leftPos.y + dirY * quadRange, leftEdgePos.y + dirY * edgeQuadRange, leftEdgePos.y, leftPos.y + dirY * quadRange, fromY + dirY * (float)range, fromY + dirY * ((float)range - edgeWidth), leftEdgePos.y + dirY * edgeQuadRange, fromY + dirY * (float)range, rightPos.y + dirY * quadRange, rightEdgePos.y + dirY * edgeQuadRange, fromY + dirY * ((float)range - edgeWidth), rightPos.y + dirY * quadRange, rightPos.y, rightEdgePos.y, rightEdgePos.y + dirY * edgeQuadRange};
            float[][] edgeColors = HUD.toFloatColor2D(startColor, edgeColor, edgeColor, startColor, edgeColor, edgeColor, edgeColor, edgeColor, edgeColor, edgeColor, edgeColor, edgeColor, edgeColor, startColor, startColor, edgeColor);
            out.add(HUD.getGLDrawOptions(7, edgeXCoords, edgeYCoords, edgeColors, 0.0f, 0.0f));
        } else {
            Point2D.Float leftPos = GameMath.getPerpendicularPoint(fromX, fromY, halfWidth, dirX, dirY);
            Point2D.Float rightPos = GameMath.getPerpendicularPoint(fromX, fromY, -halfWidth, dirX, dirY);
            float[] xCoords = new float[]{leftPos.x, leftPos.x + dirX * quadRange, rightPos.x, rightPos.x, leftPos.x + dirX * quadRange, rightPos.x + dirX * quadRange, leftPos.x + dirX * quadRange, fromX + dirX * (float)range, rightPos.x + dirX * quadRange};
            float[] yCoords = new float[]{leftPos.y, leftPos.y + dirY * quadRange, rightPos.y, rightPos.y, leftPos.y + dirY * quadRange, rightPos.y + dirY * quadRange, leftPos.y + dirY * quadRange, fromY + dirY * (float)range, rightPos.y + dirY * quadRange};
            float[][] colors = HUD.toFloatColor2D(startColor, endColor, startColor, startColor, endColor, endColor, endColor, endColor, endColor);
            out.add(HUD.getTrianglesDrawOptions(xCoords, yCoords, colors, true, 0.0f, 0.0f));
        }
        return out;
    }

    public static DrawOptions getArrowHitboxIndicator(float fromX, float fromY, float dirX, float dirY, int range, int width, Color startColor, Color endColor, Color edgeColor, GameCamera camera) {
        return HUD.getArrowHitboxIndicator(fromX - (float)camera.getX(), fromY - (float)camera.getY(), dirX, dirY, range, width, startColor, endColor, edgeColor);
    }

    public static float[] toFloatColor(Color ... colors) {
        float[] out = new float[colors.length * 4];
        for (int i = 0; i < colors.length; ++i) {
            Color color = colors[i];
            int outIndex = i * 4;
            out[outIndex] = (float)color.getRed() / 255.0f;
            out[outIndex + 1] = (float)color.getGreen() / 255.0f;
            out[outIndex + 2] = (float)color.getBlue() / 255.0f;
            out[outIndex + 3] = (float)color.getAlpha() / 255.0f;
        }
        return out;
    }

    public static float[][] toFloatColor2D(Color ... colors) {
        float[][] out = new float[colors.length][4];
        for (int i = 0; i < colors.length; ++i) {
            Color color = colors[i];
            float[] colorOut = out[i];
            colorOut[0] = (float)color.getRed() / 255.0f;
            colorOut[1] = (float)color.getGreen() / 255.0f;
            colorOut[2] = (float)color.getBlue() / 255.0f;
            colorOut[3] = (float)color.getAlpha() / 255.0f;
        }
        return out;
    }

    public static float[] getAdvColor(Color topLeft, Color topRight, Color botRight, Color botLeft) {
        return new float[]{(float)topLeft.getRed() / 255.0f, (float)topLeft.getGreen() / 255.0f, (float)topLeft.getBlue() / 255.0f, (float)topLeft.getAlpha() / 255.0f, (float)topRight.getRed() / 255.0f, (float)topRight.getGreen() / 255.0f, (float)topRight.getBlue() / 255.0f, (float)topRight.getAlpha() / 255.0f, (float)botRight.getRed() / 255.0f, (float)botRight.getGreen() / 255.0f, (float)botRight.getBlue() / 255.0f, (float)botRight.getAlpha() / 255.0f, (float)botLeft.getRed() / 255.0f, (float)botLeft.getGreen() / 255.0f, (float)botLeft.getBlue() / 255.0f, (float)botLeft.getAlpha() / 255.0f};
    }

    public static SharedTextureDrawOptions getCenterDrawOptions(GameTexture texture, int edgeResolution, int drawX, int drawY, int width, int height) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addCenter(drawOptions, texture, edgeResolution, drawX, drawY, width, height);
        return drawOptions;
    }

    public static SharedTextureDrawOptions getBackgroundDrawOptions(GameTexture texture, int edgeResolution, int drawX, int drawY, int width, int height) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addOutlines(drawOptions, edgeResolution, drawX, drawY, width, height);
        HUD.addCenter(drawOptions, texture, edgeResolution, drawX, drawY, width, height);
        return drawOptions;
    }

    protected static void addOutlines(SharedTextureDrawOptions drawOptions, int edgeResolution, int drawX, int drawY, int width, int height) {
        int leftWidth = Math.min(edgeResolution, width / 2);
        int rightWidth = Math.min(edgeResolution, width - leftWidth);
        int topHeight = Math.min(edgeResolution, height / 2);
        int botHeight = Math.min(edgeResolution, height - topHeight);
        int midWidth = width - edgeResolution * 2;
        int midHeight = height - edgeResolution * 2;
        drawOptions.addSpriteSection(1, 1, edgeResolution, edgeResolution - leftWidth, edgeResolution, edgeResolution - topHeight, edgeResolution).pos(drawX, drawY);
        drawOptions.addSpriteSection(1, 0, edgeResolution, edgeResolution - leftWidth, edgeResolution, 0, botHeight).pos(drawX, drawY + height - botHeight);
        drawOptions.addSpriteSection(0, 1, edgeResolution, 0, rightWidth, edgeResolution - topHeight, edgeResolution).pos(drawX + width - rightWidth, drawY);
        drawOptions.addSpriteSection(0, 0, edgeResolution, 0, rightWidth, 0, botHeight).pos(drawX + width - rightWidth, drawY + height - botHeight);
        if (midWidth > 0) {
            for (int x = 0; x < midWidth; x += edgeResolution) {
                int currentDrawWidth = Math.min(edgeResolution, midWidth - x);
                drawOptions.addSpriteSection(1, 1, edgeResolution * 2, edgeResolution, 0, currentDrawWidth, edgeResolution - topHeight, edgeResolution).pos(drawX + edgeResolution + x, drawY);
                drawOptions.addSpriteSection(1, 0, edgeResolution * 2, edgeResolution, 0, currentDrawWidth, 0, botHeight).pos(drawX + edgeResolution + x, drawY + height - botHeight);
            }
        }
        if (midHeight > 0) {
            for (int y = 0; y < midHeight; y += edgeResolution) {
                int currentDrawHeight = Math.min(edgeResolution, midHeight - y);
                drawOptions.addSpriteSection(1, 1, edgeResolution, edgeResolution * 2, edgeResolution - leftWidth, edgeResolution, 0, currentDrawHeight).pos(drawX, drawY + edgeResolution + y);
                drawOptions.addSpriteSection(0, 1, edgeResolution, edgeResolution * 2, 0, rightWidth, 0, currentDrawHeight).pos(drawX + width - rightWidth, drawY + edgeResolution + y);
            }
        }
    }

    protected static void addCenter(SharedTextureDrawOptions drawOptions, GameTexture texture, int edgeResolution, int drawX, int drawY, int width, int height) {
        int midWidth = width - edgeResolution * 2;
        int midHeight = height - edgeResolution * 2;
        int textureStartX = 0;
        int textureStartY = edgeResolution * 4;
        int textureWidth = texture.getWidth() - textureStartX;
        int textureHeight = texture.getHeight() - textureStartY;
        if (midWidth > 0 && midHeight > 0 && textureWidth > 0 && textureHeight > 0) {
            for (int x = 0; x < midWidth; x += textureWidth) {
                int currentDrawWidth = Math.min(textureWidth, midWidth - x);
                for (int y = 0; y < midHeight; y += textureHeight) {
                    int currentDrawHeight = Math.min(textureHeight, midHeight - y);
                    drawOptions.addSection(textureStartX, textureStartX + currentDrawWidth, textureStartY, textureStartY + currentDrawHeight).pos(drawX + edgeResolution + x, drawY + edgeResolution + y);
                }
            }
        }
    }

    public static boolean[][] toBooleans(int[][] ints) {
        boolean[][] out = new boolean[ints.length][0];
        for (int i = 0; i < ints.length; ++i) {
            int[] ints2 = ints[i];
            boolean[] out2 = new boolean[ints2.length];
            for (int j = 0; j < ints2.length; ++j) {
                out2[j] = ints2[j] > 0;
            }
            out[i] = out2;
        }
        return out;
    }

    public static SharedTextureDrawOptions getBackgroundEdged(GameTexture texture, int spriteRes, int xOverlap, int yOverlap, int drawX, int drawY, PointSetAbstract<?> set, int boxWidth, int boxHeight) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addBackgroundBoxed(drawOptions, spriteRes, xOverlap, yOverlap, drawX, drawY, set, boxWidth, boxHeight);
        return drawOptions;
    }

    public static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int xOverlap, int yOverlap, int drawX, int drawY, PointSetAbstract<?> set, int boxWidth, int boxHeight) {
        HUD.addBackgroundBoxed(drawOptions, spriteRes, yOverlap, 0, 0, xOverlap, drawX - xOverlap / 2, drawY - yOverlap / 2, set, boxWidth, boxHeight);
    }

    public static SharedTextureDrawOptions getBackgroundEdged(GameTexture texture, int spriteRes, int xOverlap, int yOverlap, int drawX, int drawY, boolean[][] box, int boxWidth, int boxHeight) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addBackgroundBoxed(drawOptions, spriteRes, xOverlap, yOverlap, drawX, drawY, box, boxWidth, boxHeight);
        return drawOptions;
    }

    public static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int xOverlap, int yOverlap, int drawX, int drawY, boolean[][] box, int boxWidth, int boxHeight) {
        HUD.addBackgroundBoxed(drawOptions, spriteRes, yOverlap, 0, 0, xOverlap, drawX - xOverlap / 2, drawY - yOverlap / 2, box, boxWidth, boxHeight);
    }

    protected static SharedTextureDrawOptions getBackgroundEdged(GameTexture texture, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, PointSetAbstract<?> set, int boxWidth, int boxHeight) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addBackgroundBoxed(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX, drawY, set, boxWidth, boxHeight);
        return drawOptions;
    }

    protected static SharedTextureDrawOptions getBackgroundEdged(GameTexture texture, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, boolean[][] box, int boxWidth, int boxHeight) {
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(texture);
        HUD.addBackgroundBoxed(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX, drawY, box, boxWidth, boxHeight);
        return drawOptions;
    }

    protected static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, PointSetAbstract<?> set, int boxWidth, int boxHeight) {
        HUD.addBackgroundBoxed(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX, drawY, set, (Point p) -> {
            boolean[] edge = new boolean[Level.adjacentGetters.length];
            for (int i = 0; i < Level.adjacentGetters.length; ++i) {
                Point pos = Level.adjacentGetters[i];
                edge[i] = set.contains(p.x + pos.x, p.y + pos.y);
            }
            return edge;
        }, boxWidth, boxHeight);
    }

    protected static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, boolean[][] box, int boxWidth, int boxHeight) {
        Iterable<Point> iterable = () -> {
            Stream.Builder<Point> builder = Stream.builder();
            for (int y = 0; y < box.length; ++y) {
                for (int x = 0; x < box[y].length; ++x) {
                    if (!box[y][x]) continue;
                    builder.accept(new Point(x, y));
                }
            }
            return builder.build().iterator();
        };
        HUD.addBackgroundBoxed(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX, drawY, iterable, (Point p) -> HUD.getAdjacent(box, p.x, p.y), boxWidth, boxHeight);
    }

    protected static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, Iterable<Point> points, Predicate<Point> isActive, int boxWidth, int boxHeight) {
        HUD.addBackgroundBoxed(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX, drawY, points, (Point p) -> {
            boolean[] edge = new boolean[Level.adjacentGetters.length];
            for (int i = 0; i < Level.adjacentGetters.length; ++i) {
                Point pos = Level.adjacentGetters[i];
                edge[i] = isActive.test(new Point(p.x + pos.x, p.y + pos.y));
            }
            return edge;
        }, boxWidth, boxHeight);
    }

    protected static void addBackgroundBoxed(SharedTextureDrawOptions drawOptions, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, Iterable<Point> points, Function<Point, boolean[]> adjacentBuilder, int boxWidth, int boxHeight) {
        for (Point p : points) {
            boolean[] edge = adjacentBuilder.apply(p);
            HUD.addBackgroundBoxTile(drawOptions, spriteRes, topOverlap, rightOverlap, botOverlap, leftOverlap, drawX + p.x * boxWidth, drawY + p.y * boxHeight, edge, boxWidth, boxHeight);
        }
    }

    public static void addBackgroundBoxTile(SharedTextureDrawOptions drawOptions, int spriteRes, int xOverlap, int yOverlap, int drawX, int drawY, boolean[] adjacent, int width, int height) {
        HUD.addBackgroundBoxTile(drawOptions, spriteRes, yOverlap, 0, 0, xOverlap, drawX - xOverlap / 2, drawY - yOverlap / 2, adjacent, width, height);
    }

    protected static void addBackgroundBoxTile(SharedTextureDrawOptions drawOptions, int spriteRes, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap, int drawX, int drawY, boolean[] adjacent, int width, int height) {
        BoxEdge botRight;
        BoxEdge botLeft;
        BoxEdge topRight;
        BoxEdge topLeft;
        if (width < spriteRes) {
            throw new IllegalArgumentException("boxWidth cannot be smaller than spriteRes");
        }
        if (height < spriteRes) {
            throw new IllegalArgumentException("boxHeight cannot be smaller than spriteRes");
        }
        GameTextureSection section = new GameTextureSection(drawOptions.texture);
        int halfWidth1 = width / 2;
        int halfHeight1 = height / 2;
        int halfWidth2 = width - halfWidth1;
        int halfHeight2 = height - halfHeight1;
        int currentDrawX = drawX;
        int currentDrawY = drawY;
        int currentWidth = width;
        int currentHeight = height;
        if (!adjacent[1]) {
            currentHeight -= topOverlap;
            currentDrawY += topOverlap;
        }
        if (!adjacent[3]) {
            currentWidth -= leftOverlap;
            currentDrawX += leftOverlap;
        }
        if (!adjacent[4]) {
            currentWidth -= rightOverlap;
        }
        if (!adjacent[6]) {
            currentHeight -= botOverlap;
        }
        if ((topLeft = HUD.getBoxTopLeft(section, spriteRes, adjacent, width, height, topOverlap, rightOverlap, botOverlap, leftOverlap)) != null) {
            int sectionDrawX = currentDrawX - spriteRes + topLeft.xOffset;
            int sectionDrawY = currentDrawY - spriteRes + topLeft.yOffset;
            if (topLeft.section != null) {
                drawOptions.add(topLeft.section).pos(sectionDrawX, sectionDrawY);
            }
            topLeft.addExtensionDrawsTopLeft(drawOptions, sectionDrawX, sectionDrawY, spriteRes, halfWidth1, halfHeight1);
        }
        if ((topRight = HUD.getBoxTopRight(section, spriteRes, adjacent, width, height, topOverlap, rightOverlap, botOverlap, leftOverlap)) != null) {
            int sectionDrawX = currentDrawX + width + topRight.xOffset;
            int sectionDrawY = currentDrawY - spriteRes + topRight.yOffset;
            if (topRight.section != null) {
                drawOptions.add(topRight.section).pos(sectionDrawX, sectionDrawY);
            }
            topRight.addExtensionDrawsTopRight(drawOptions, sectionDrawX, sectionDrawY, spriteRes, halfWidth2, halfHeight1);
        }
        if ((botLeft = HUD.getBoxBotLeft(section, spriteRes, adjacent, width, height, topOverlap, rightOverlap, botOverlap, leftOverlap)) != null) {
            int sectionDrawX = currentDrawX - spriteRes + botLeft.xOffset;
            int sectionDrawY = currentDrawY + height + botLeft.yOffset;
            if (botLeft.section != null) {
                drawOptions.add(botLeft.section).pos(sectionDrawX, sectionDrawY);
            }
            botLeft.addExtensionDrawsBotLeft(drawOptions, sectionDrawX, sectionDrawY, spriteRes, halfWidth1, halfHeight2);
        }
        if ((botRight = HUD.getBoxBotRight(section, spriteRes, adjacent, width, height, topOverlap, rightOverlap, botOverlap, leftOverlap)) != null) {
            int sectionDrawX = currentDrawX + width + botRight.xOffset;
            int sectionDrawY = currentDrawY + height + botRight.yOffset;
            if (botRight.section != null) {
                drawOptions.add(botRight.section).pos(sectionDrawX, sectionDrawY);
            }
            botRight.addExtensionDrawsBotRight(drawOptions, sectionDrawX, sectionDrawY, spriteRes, halfWidth2, halfHeight2);
        }
        if (!adjacent[0] && adjacent[1] && adjacent[3] && topOverlap > 0 && leftOverlap > 0) {
            HUD.addCenterBoxDraw(drawOptions, section.sprite(2, 0, spriteRes * 2), spriteRes * 2, currentDrawX + leftOverlap, currentDrawY, currentWidth - leftOverlap, currentHeight);
            HUD.addCenterBoxDraw(drawOptions, section.sprite(2, 0, spriteRes * 2), spriteRes * 2, currentDrawX, currentDrawY + topOverlap, width - (currentWidth - leftOverlap), currentHeight - topOverlap);
        } else {
            HUD.addCenterBoxDraw(drawOptions, section.sprite(2, 0, spriteRes * 2), spriteRes * 2, currentDrawX, currentDrawY, currentWidth, currentHeight);
        }
    }

    protected static void addCenterBoxDraw(SharedTextureDrawOptions drawOptions, GameTextureSection texture, int spriteRes, int drawX, int drawY, int width, int height) {
        for (int x = 0; x < width; x += spriteRes) {
            int currentWidth = Math.min(spriteRes, width - x);
            for (int y = 0; y < height; y += spriteRes) {
                int currentHeight = Math.min(spriteRes, height - y);
                drawOptions.add(texture.sprite(2, 2, spriteRes).section(0, currentWidth, 0, currentHeight)).pos(drawX + x, drawY + y);
            }
        }
    }

    protected static BoxEdge getBoxTopLeft(GameTextureSection texture, int spriteRes, boolean[] adj, int width, int height, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap) {
        int topLeft = 0;
        int top = 1;
        int left = 3;
        int bot = 6;
        int right = 4;
        if (adj[left]) {
            if (adj[top]) {
                if (adj[topLeft]) {
                    return null;
                }
                return new BoxEdge(texture.sprite(2, 2, spriteRes), leftOverlap, topOverlap);
            }
            return new BoxEdge(null, 0, 0).extend(texture.sprite(2, 1, spriteRes), true, 0, 0, 0, adj[topLeft], false);
        }
        if (adj[top]) {
            return new BoxEdge(null, 0, 0).extend(texture.sprite(1, 2, spriteRes), false, 0, 0, 0, adj[topLeft], false);
        }
        return new BoxEdge(texture.sprite(1, 1, spriteRes), 0, 0).extend(texture.sprite(2, 1, spriteRes), true, 0, 0, adj[right] ? 0 : -leftOverlap, false, false).extend(texture.sprite(1, 2, spriteRes), false, 0, 0, adj[bot] ? 0 : -topOverlap, false, false);
    }

    protected static BoxEdge getBoxTopRight(GameTextureSection texture, int spriteRes, boolean[] adj, int width, int height, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap) {
        int topRight = 2;
        int top = 1;
        int right = 4;
        int left = 3;
        int bot = 6;
        if (adj[right]) {
            if (adj[top]) {
                if (adj[topRight]) {
                    return null;
                }
                return new BoxEdge(texture.sprite(3, 2, spriteRes), -rightOverlap + (adj[left] ? 0 : -leftOverlap), topOverlap);
            }
            return new BoxEdge(null, 0, 0).extend(texture.sprite(3, 1, spriteRes), true, -width, 0, (adj[left] ? 0 : -leftOverlap) + (adj[topRight] ? leftOverlap : 0), false, adj[topRight]);
        }
        if (adj[top]) {
            return new BoxEdge(null, 0, 0).extend(texture.sprite(0, 2, spriteRes), false, adj[left] ? 0 : -leftOverlap, 0, 0, adj[topRight], false);
        }
        return new BoxEdge(texture.sprite(0, 1, spriteRes), adj[left] ? 0 : -leftOverlap, 0).extend(texture.sprite(3, 1, spriteRes), true, -width, 0, 0, false, false).extend(texture.sprite(0, 2, spriteRes), false, 0, 0, !adj[bot] ? -topOverlap : 0, false, false);
    }

    protected static BoxEdge getBoxBotLeft(GameTextureSection texture, int spriteRes, boolean[] adj, int width, int height, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap) {
        int botLeft = 5;
        int bot = 6;
        int left = 3;
        int top = 1;
        int right = 4;
        if (adj[left]) {
            if (adj[bot]) {
                if (adj[botLeft]) {
                    return null;
                }
                return new BoxEdge(texture.sprite(2, 3, spriteRes), leftOverlap, -botOverlap + (adj[top] ? 0 : -topOverlap));
            }
            return new BoxEdge(null, 0, 0).extend(texture.sprite(2, 0, spriteRes), true, 0, adj[top] ? 0 : -topOverlap, 0, adj[botLeft], false);
        }
        if (adj[bot]) {
            return new BoxEdge(null, 0, 0).extend(texture.sprite(1, 3, spriteRes), false, 0, -height, (adj[top] ? 0 : -topOverlap) + (adj[botLeft] ? topOverlap : 0), false, adj[botLeft]);
        }
        return new BoxEdge(texture.sprite(1, 0, spriteRes), 0, adj[top] ? 0 : -topOverlap).extend(texture.sprite(2, 0, spriteRes), true, 0, 0, adj[right] ? 0 : -leftOverlap, false, false).extend(texture.sprite(1, 3, spriteRes), false, 0, -height, 0, false, false);
    }

    protected static BoxEdge getBoxBotRight(GameTextureSection texture, int spriteRes, boolean[] adj, int width, int height, int topOverlap, int rightOverlap, int botOverlap, int leftOverlap) {
        int botRight = 7;
        int bot = 6;
        int right = 4;
        int left = 3;
        int top = 1;
        if (adj[right]) {
            if (adj[bot]) {
                if (adj[botRight]) {
                    return null;
                }
                return new BoxEdge(texture.sprite(3, 3, spriteRes), -rightOverlap + (adj[left] ? 0 : -leftOverlap), -botOverlap + (adj[top] ? 0 : -topOverlap));
            }
            return new BoxEdge(null, 0, 0).extend(texture.sprite(3, 0, spriteRes), true, -width, adj[top] ? 0 : -topOverlap, (adj[left] || adj[botRight] ? 0 : -leftOverlap) + (adj[left] && adj[botRight] ? leftOverlap : 0), false, adj[botRight]);
        }
        if (adj[bot]) {
            return new BoxEdge(null, 0, 0).extend(texture.sprite(0, 3, spriteRes), false, adj[left] ? 0 : -leftOverlap, -height, (!adj[botRight] && !adj[top] ? -topOverlap : 0) + (adj[botRight] && adj[top] ? topOverlap : 0), false, adj[botRight]);
        }
        return new BoxEdge(texture.sprite(0, 0, spriteRes), adj[left] ? 0 : -leftOverlap, adj[top] ? 0 : -topOverlap).extend(texture.sprite(3, 0, spriteRes), true, -width, 0, 0, false, false).extend(texture.sprite(0, 3, spriteRes), false, 0, -height, 0, false, false);
    }

    protected static boolean[] getAdjacent(boolean[][] box, int x, int y) {
        boolean[] adjacent = new boolean[8];
        if (y > 0) {
            if (x > 0) {
                adjacent[0] = box[y - 1][x - 1];
            }
            adjacent[1] = box[y - 1][x];
            if (x < box[y - 1].length - 1) {
                adjacent[2] = box[y - 1][x + 1];
            }
        }
        if (x > 0) {
            adjacent[3] = box[y][x - 1];
        }
        if (x < box[y].length - 1) {
            adjacent[4] = box[y][x + 1];
        }
        if (y < box.length - 1) {
            if (x > 0) {
                adjacent[5] = box[y + 1][x - 1];
            }
            adjacent[6] = box[y + 1][x];
            if (x < box[y + 1].length - 1) {
                adjacent[7] = box[y + 1][x + 1];
            }
        }
        return adjacent;
    }

    static {
        debugShow = DebugShow.NOTHING;
        showRegionBounds = false;
        showWorldPresetRegionBounds = false;
        paths = new LinkedList();
    }

    public static enum DebugShow {
        NOTHING,
        REGIONS,
        CONNECTED_REGIONS,
        ROOMS,
        HOUSE,
        PATHS,
        HEIGHT,
        ADVANCED_HEIGHT,
        VILLAGE_TILES,
        CURSED_ARENA_TILES,
        WATER_TYPE,
        BIOME_BLENDING,
        WIND_STATS,
        WIND_OVERLAY,
        HOSTILE_SPAWN_OVERLAY,
        INSIDE_LIGHT_MODIFIER;

    }

    private static class SubmittedPath {
        public final PathResult<Point, TilePathfinding> path;
        public final long time;

        public SubmittedPath(PathResult<Point, TilePathfinding> path, long time) {
            this.path = path;
            this.time = time;
        }
    }

    protected static class BoxEdge {
        public final GameTextureSection section;
        public final int xOffset;
        public final int yOffset;
        public LinkedList<BoxExtend> extensions = new LinkedList();

        public BoxEdge(GameTextureSection section, int xOffset, int yOffset) {
            this.section = section;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public BoxEdge extend(GameTextureSection section, boolean horizontal, int xOffset, int yOffset, int lengthOffset, boolean skipStart, boolean skipEnd) {
            this.extensions.add(new BoxExtend(section, horizontal, xOffset, yOffset, lengthOffset, skipStart, skipEnd));
            return this;
        }

        public void addExtensionDrawsTopLeft(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
            for (BoxExtend extension : this.extensions) {
                extension.addDrawableTopLeft(drawOptions, drawX, drawY, spriteRes, halfWidth, halfHeight);
            }
        }

        public void addExtensionDrawsTopRight(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
            for (BoxExtend extension : this.extensions) {
                extension.addDrawableTopRight(drawOptions, drawX, drawY, spriteRes, halfWidth, halfHeight);
            }
        }

        public void addExtensionDrawsBotLeft(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
            for (BoxExtend extension : this.extensions) {
                extension.addDrawableBotLeft(drawOptions, drawX, drawY, spriteRes, halfWidth, halfHeight);
            }
        }

        public void addExtensionDrawsBotRight(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
            for (BoxExtend extension : this.extensions) {
                extension.addDrawableBotRight(drawOptions, drawX, drawY, spriteRes, halfWidth, halfHeight);
            }
        }

        protected static class BoxExtend {
            public final GameTextureSection extendSection;
            public final boolean horizontal;
            public final int xOffset;
            public final int yOffset;
            public final int lengthOffset;
            public final boolean skipStart;
            public final boolean skipEnd;

            public BoxExtend(GameTextureSection section, boolean horizontal, int xOffset, int yOffset, int lengthOffset, boolean skipStart, boolean skipEnd) {
                this.extendSection = section;
                this.horizontal = horizontal;
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.lengthOffset = lengthOffset;
                this.skipStart = skipStart;
                this.skipEnd = skipEnd;
            }

            protected void addDrawable(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int boxHalfWidth, int boxHalfHeight, Color color) {
                int width = this.extendSection.getWidth();
                int height = this.extendSection.getHeight();
                if (this.horizontal) {
                    int i;
                    int end = boxHalfWidth - (this.skipEnd ? width : 0) + this.lengthOffset;
                    int n = i = this.skipStart ? width : 0;
                    while (i < end) {
                        int sectionSize = Math.min(width, end - i);
                        drawOptions.add(this.extendSection.section(0, sectionSize, 0, height)).pos(drawX + i + this.xOffset, drawY + this.yOffset);
                        i += width;
                    }
                } else {
                    int i;
                    int end = boxHalfHeight - (this.skipEnd ? height : 0) + this.lengthOffset;
                    int n = i = this.skipStart ? height : 0;
                    while (i < end) {
                        int sectionSize = Math.min(height, end - i);
                        drawOptions.add(this.extendSection.section(0, width, 0, sectionSize)).pos(drawX + this.xOffset, drawY + i + this.yOffset);
                        i += height;
                    }
                }
            }

            public void addDrawableTopLeft(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
                if (this.horizontal) {
                    drawX += spriteRes;
                } else {
                    drawY += spriteRes;
                }
                this.addDrawable(drawOptions, drawX, drawY, halfWidth, halfHeight, Color.RED);
            }

            public void addDrawableTopRight(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
                if (this.horizontal) {
                    drawX += halfWidth;
                } else {
                    drawY += spriteRes;
                }
                this.addDrawable(drawOptions, drawX, drawY, halfWidth, halfHeight, Color.GREEN);
            }

            public void addDrawableBotLeft(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
                if (this.horizontal) {
                    drawX += spriteRes;
                } else {
                    drawY += halfHeight;
                }
                this.addDrawable(drawOptions, drawX, drawY, halfWidth, halfHeight, Color.BLUE);
            }

            public void addDrawableBotRight(SharedTextureDrawOptions drawOptions, int drawX, int drawY, int spriteRes, int halfWidth, int halfHeight) {
                if (this.horizontal) {
                    drawX += halfWidth;
                } else {
                    drawY += halfHeight;
                }
                this.addDrawable(drawOptions, drawX, drawY, halfWidth, halfHeight, Color.WHITE);
            }
        }
    }
}

