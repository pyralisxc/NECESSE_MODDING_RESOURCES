/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointHashMap;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPresetsRegion;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.sidebar.ShowPresetRegionSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.MapHudDrawElement;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.regionSystem.Region;

public class ClientDebugMapHudDrawElement
extends MapHudDrawElement {
    public final Client client;
    public final Level level;

    public ClientDebugMapHudDrawElement(Client client, Level level) {
        this.client = client;
        this.level = level;
    }

    @Override
    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean shouldDrawOnMap(Client client, ClientDiscoveredMap map) {
        return HUD.showRegionBounds || HUD.showWorldPresetRegionBounds;
    }

    @Override
    public Rectangle getMapLevelDrawBounds() {
        return null;
    }

    @Override
    public Point getMapLevelPos() {
        PlayerMob player = this.client.getPlayer();
        if (player != null) {
            return new Point(player.getX(), player.getY());
        }
        return new Point(0, 0);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int drawX, int drawY, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        PlayerMob player = client.getPlayer();
        if (player != null) {
            Level serverLevel = null;
            Server server = client.getLocalServer();
            if (server != null) {
                serverLevel = server.world.getLevel(this.getLevel().getIdentifier());
            }
            if (HUD.showRegionBounds) {
                if (serverLevel == null || !Settings.serverPerspective) {
                    ClientLevelLoading loading = client.levelManager.loading();
                    SettlementsWorldData settlements = SettlementsWorldData.getSettlementsData(client);
                    this.drawRegions(this.level, loading.getLoadedRegions(), settlements, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(0, 255, 0, 100), new Color(255, 0, 0, 100), 0);
                    this.drawRegions(this.level, loading.getRequestedRegions(), settlements, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(0, 255, 255, 100), new Color(0, 0, 255, 100), 2);
                    this.drawRegions(this.level, loading.getQueuedRegions(), settlements, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(255, 255, 0, 100), new Color(255, 0, 255, 100), 4);
                } else {
                    SettlementsWorldData settlements = SettlementsWorldData.getSettlementsData(serverLevel);
                    Iterable<Point> regions = GameUtils.mapIterable(serverLevel.regionManager.collectLoadedRegions().iterator(), r -> new Point(r.regionX, r.regionY));
                    this.drawRegions(serverLevel, regions, settlements, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(0, 255, 0, 100), new Color(255, 0, 0, 100), 0);
                }
            }
            if (HUD.showWorldPresetRegionBounds) {
                int regionX = GameMath.getRegionCoordByTile(player.getTileX());
                int regionY = GameMath.getRegionCoordByTile(player.getTileY());
                int presetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionX);
                int presetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionY);
                int presetRegionTileSize = 1024;
                Rectangle tileRectangle = new Rectangle(presetRegionX * presetRegionTileSize, presetRegionY * presetRegionTileSize, presetRegionTileSize, presetRegionTileSize);
                this.drawRectangle(tileRectangle, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(255, 255, 255), 0, null);
                if (serverLevel != null) {
                    WorldPresetsRegion worldPresetsRegion = serverLevel.getWorldEntity().getWorldPresets(regionX, regionY);
                    LevelPresetsRegion levelPresetsRegion = worldPresetsRegion.getLevelRegions(serverLevel.getIdentifier(), 0);
                    PointHashMap regionDrawCounts = new PointHashMap();
                    HashMap<Integer, Integer> generationUniqueIDsFound = new HashMap<Integer, Integer>();
                    String searchFilter = ShowPresetRegionSidebarForm.searchFilter.toLowerCase();
                    for (LevelPresetsRegion.PresetDebugData data : levelPresetsRegion.getDebugData()) {
                        String name = data.getDebugName();
                        if (!searchFilter.isEmpty() && !name.toLowerCase().contains(searchFilter)) continue;
                        int generationUniqueID = data.getGenerationRegionUniqueID();
                        int generationUniqueIDIndex = generationUniqueIDsFound.compute(data.getGenerationRegionUniqueID(), (id, last) -> {
                            if (last == null) {
                                return generationUniqueIDsFound.size();
                            }
                            return last;
                        });
                        Color color = Color.getHSBColor((float)generationUniqueIDIndex * 50.0f % 360.0f / 360.0f, 1.0f, 1.0f);
                        this.drawRegions(this.level, data.getOccupiedRegions(), player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, color, color, 0, (regionPos, region) -> {
                            int drawCount = regionDrawCounts.compute(regionPos.x, regionPos.y, (x, y, last) -> {
                                if (last == null) {
                                    return 0;
                                }
                                return last + 1;
                            });
                            StringBuilder stringBuilder = new StringBuilder("Region: " + regionPos.x + "x" + regionPos.y + "\n");
                            stringBuilder.append("Generation: ").append(generationUniqueID).append("\n");
                            for (int i = 0; i < drawCount; ++i) {
                                stringBuilder.append("\n");
                            }
                            stringBuilder.append(name);
                            return stringBuilder.toString();
                        });
                        Iterable<Rectangle> tileRectangles = data.getOccupiedTileRectangles();
                        for (Rectangle rectangle : tileRectangles) {
                            this.drawRectangle(rectangle, player.getX(), player.getY(), drawX, drawY, drawBounds, tileScale, new Color(255, 255, 255), 2, name);
                        }
                    }
                }
            }
        }
    }

    protected void drawRegions(Level level, Iterable<Point> regionPositions, SettlementsWorldData settlementsData, int playerX, int playerY, int drawX, int drawY, Rectangle drawBounds, double tileScale, Color foundColor, Color notFoundColor, int padding) {
        this.drawRegions(level, regionPositions, playerX, playerY, drawX, drawY, drawBounds, tileScale, foundColor, notFoundColor, padding, (regionPos, region) -> {
            if (region == null) {
                return regionPos.x + "x" + regionPos.y + "\nNot loaded";
            }
            int settlementUniqueID = settlementsData.getSettlementUniqueIDAtRegion(level.getIdentifier(), regionPos.x, regionPos.y);
            return "Region: " + regionPos.x + "x" + regionPos.y + "\nTile: " + region.tileXOffset + "x" + region.tileYOffset + "\nSize: " + region.tileWidth + "x" + region.tileHeight + "\nUnload: " + region.unloadRegionBuffer.getBuffer() + "\nSettlement: \n" + settlementUniqueID;
        });
    }

    protected void drawRegions(Level level, Iterable<Point> regionPositions, int playerX, int playerY, int drawX, int drawY, Rectangle drawBounds, double tileScale, Color foundColor, Color notFoundColor, int padding, BiFunction<Point, Region, String> getText) {
        for (Point regionPos : regionPositions) {
            Rectangle rectangle;
            String text;
            Region region = level.regionManager.getRegion(regionPos.x, regionPos.y, false);
            String string = text = getText == null ? null : getText.apply(regionPos, region);
            if (region != null) {
                rectangle = new Rectangle(region.tileXOffset, region.tileYOffset, region.tileWidth, region.tileHeight);
                this.drawRectangle(rectangle, playerX, playerY, drawX, drawY, drawBounds, tileScale, foundColor, padding, text);
                continue;
            }
            rectangle = new Rectangle(level.regionManager.getTileCoordByRegion(regionPos.x), level.regionManager.getTileCoordByRegion(regionPos.y), 16, 16);
            this.drawRectangle(rectangle, playerX, playerY, drawX, drawY, drawBounds, tileScale, notFoundColor, padding, text);
        }
    }

    protected void drawRectangle(Rectangle rectangle, int playerX, int playerY, int drawX, int drawY, Rectangle drawBounds, double tileScale, Color color, int padding, String text) {
        float playerTileY;
        float playerTileX = GameMath.getTileFloatCoordinate(playerX);
        Rectangle drawRectangle = new Rectangle((int)((double)rectangle.x * tileScale - (double)playerTileX * tileScale) + padding + drawX, (int)((double)rectangle.y * tileScale - (double)(playerTileY = GameMath.getTileFloatCoordinate(playerY)) * tileScale) + padding + drawY, (int)((double)rectangle.width * tileScale) - padding * 2, (int)((double)rectangle.height * tileScale) - padding * 2);
        if (drawBounds.intersects(drawRectangle)) {
            int fontSize;
            Renderer.drawRectangleLines(drawRectangle, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
            if (text != null && (fontSize = Math.min(12, (int)(1.5 * tileScale))) > 0) {
                String[] split = text.split("\n");
                for (int i = 0; i < split.length; ++i) {
                    FontManager.bit.drawString(drawRectangle.x + 4, drawRectangle.y + 4 + i * fontSize, split[i], new FontOptions(fontSize).outline());
                }
            }
        }
    }
}

