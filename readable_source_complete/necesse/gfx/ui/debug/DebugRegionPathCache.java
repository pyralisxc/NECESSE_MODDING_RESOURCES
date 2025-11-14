/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import necesse.engine.Settings;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.TableContentDraw;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.debug.Debug;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SubRegion;

public class DebugRegionPathCache
extends Debug {
    public static ArrayList<Function<Level, PathDoorOption>> cacheGetters = new ArrayList();
    private PathDoorOption selectedCache;
    private PathDoorOption currentCache;
    private int selectedSourceID = -1;
    private int currentSourceID = -1;
    private int selectedDestinationID = -1;
    private int currentDestinationID = -1;
    private final ArrayList<SourcePaths> lastSourcePaths = new ArrayList();
    private final ArrayList<SourceDestinations> lastDestinations = new ArrayList();
    private final ArrayList<DestinationPath> lastDestinationPaths = new ArrayList();
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    @Override
    protected void onReset() {
        this.selectedCache = null;
        this.currentCache = null;
        this.selectedSourceID = -1;
        this.currentSourceID = -1;
        this.selectedDestinationID = -1;
        this.currentDestinationID = -1;
        this.lastSourcePaths.clear();
        this.lastDestinations.clear();
        this.lastDestinationPaths.clear();
    }

    @Override
    protected void submitDebugInputEvent(InputEvent event, Client client) {
        int scroll = 0;
        if (event.isMouseWheelEvent()) {
            this.wheelBuffer.add(event);
            scroll = this.wheelBuffer.useAllScrollY();
            event.use();
        }
        if (event.getID() == 265) {
            if (event.state) {
                scroll = 1;
            }
            event.use();
        } else if (event.getID() == 264) {
            if (event.state) {
                scroll = -1;
            }
            event.use();
        }
        while (scroll != 0) {
            int sign;
            int n = sign = scroll > 0 ? 1 : -1;
            if (this.currentDestinationID == -1) {
                int i;
                int lastIndex;
                if (this.currentSourceID != -1) {
                    if (this.lastDestinationPaths.isEmpty()) {
                        return;
                    }
                    lastIndex = -1;
                    for (i = 0; i < this.lastDestinationPaths.size(); ++i) {
                        if (this.lastDestinationPaths.get((int)i).destinationRegionID != this.selectedDestinationID) continue;
                        lastIndex = i;
                        break;
                    }
                    this.selectedDestinationID = lastIndex == -1 && sign > 0 ? this.lastDestinationPaths.get((int)(this.lastDestinationPaths.size() - 1)).destinationRegionID : this.lastDestinationPaths.get((int)Math.floorMod((int)(lastIndex - sign), (int)this.lastDestinationPaths.size())).destinationRegionID;
                } else if (this.currentCache != null) {
                    if (this.lastDestinations.isEmpty()) {
                        return;
                    }
                    lastIndex = -1;
                    for (i = 0; i < this.lastDestinations.size(); ++i) {
                        if (this.lastDestinations.get((int)i).sourceRegionID != this.selectedSourceID) continue;
                        lastIndex = i;
                        break;
                    }
                    this.selectedSourceID = lastIndex == -1 && sign > 0 ? this.lastDestinations.get((int)(this.lastDestinations.size() - 1)).sourceRegionID : this.lastDestinations.get((int)Math.floorMod((int)(lastIndex - sign), (int)this.lastDestinations.size())).sourceRegionID;
                } else {
                    if (this.lastSourcePaths.isEmpty()) {
                        return;
                    }
                    lastIndex = -1;
                    for (i = 0; i < this.lastSourcePaths.size(); ++i) {
                        if (this.lastSourcePaths.get((int)i).option != this.selectedCache) continue;
                        lastIndex = i;
                        break;
                    }
                    this.selectedCache = lastIndex == -1 && sign > 0 ? this.lastSourcePaths.get((int)(this.lastSourcePaths.size() - 1)).option : this.lastSourcePaths.get((int)Math.floorMod((int)(lastIndex - sign), (int)this.lastSourcePaths.size())).option;
                }
            }
            if (scroll > 0) {
                --scroll;
                continue;
            }
            ++scroll;
        }
        if (event.getID() == 257 && (this.selectedCache != null || this.selectedSourceID != -1 || this.selectedDestinationID != -1)) {
            if (event.state) {
                if (this.selectedDestinationID != -1) {
                    this.currentDestinationID = this.selectedDestinationID;
                    this.selectedDestinationID = -1;
                } else if (this.selectedSourceID != -1) {
                    this.currentSourceID = this.selectedSourceID;
                    this.selectedSourceID = -1;
                } else {
                    this.currentCache = this.selectedCache;
                    this.selectedCache = null;
                }
            }
            event.use();
        } else if (event.getID() == 259 && (this.currentCache != null || this.currentSourceID != -1 || this.currentDestinationID != -1)) {
            if (event.state) {
                if (this.currentDestinationID != -1) {
                    this.selectedDestinationID = -1;
                    this.currentDestinationID = -1;
                } else if (this.currentSourceID != -1) {
                    this.selectedSourceID = -1;
                    this.currentSourceID = -1;
                } else {
                    this.selectedCache = null;
                    this.currentCache = null;
                }
            }
            event.use();
        }
    }

    private String getSelectedString() {
        ArrayList<String> out = new ArrayList<String>();
        if (this.currentCache != null) {
            out.add(this.currentCache.debugName);
        }
        if (this.currentSourceID != -1) {
            if (this.currentCache == null) {
                out.add("NULL");
            }
            out.add("" + this.currentSourceID);
        }
        if (this.currentDestinationID != -1) {
            if (this.currentCache == null) {
                out.add("NULL");
            }
            if (this.currentSourceID == -1) {
                out.add("NULL");
            }
            out.add("" + this.currentDestinationID);
        }
        return GameUtils.join(out.toArray(), " > ");
    }

    @Override
    protected void drawDebug(Client client) {
        this.drawString("Use scroll wheel to change selection");
        this.drawString("Press '" + Input.getName(257) + "' to select");
        this.drawString("Press '" + Input.getName(259) + "' to go back");
        this.drawString("Current: " + this.getSelectedString());
        if (this.currentDestinationID == -1) {
            if (this.currentSourceID != -1) {
                this.lastDestinationPaths.clear();
                for (int destinationRegionID : this.currentCache.getDestinationPathRegionIDs(this.currentSourceID)) {
                    this.lastDestinationPaths.add(new DestinationPath(this.currentCache, this.currentSourceID, destinationRegionID));
                }
                this.lastDestinationPaths.sort(Comparator.comparingInt(d -> d.path.size()));
                float hue = 0.0f;
                TableContentDraw tableDraw = new TableContentDraw();
                for (DestinationPath destination : this.lastDestinationPaths) {
                    Color color = Color.getHSBColor(hue += 0.15f, 1.0f, 1.0f);
                    FontOptions options = new FontOptions(16).outline().color(color);
                    tableDraw.newRow().addTextColumn(this.selectedDestinationID == destination.destinationRegionID ? ">" : " ", new FontOptions(16).outline()).addTextColumn("ID " + destination.sourceRegionID + " > " + destination.destinationRegionID + ":", options, 10, 0).addTextColumn(destination.path.size() + " regions", options, 10, 0);
                }
                tableDraw.draw(10, this.skipY(tableDraw.getHeight()));
            } else if (this.currentCache != null) {
                this.lastDestinations.clear();
                for (int sourceRegionID : this.currentCache.getSourcePathRegionIDs()) {
                    this.lastDestinations.add(new SourceDestinations(this.currentCache, sourceRegionID));
                }
                this.lastDestinations.sort(Comparator.comparingInt(s -> s.destinations));
                float hue = 0.0f;
                TableContentDraw tableDraw = new TableContentDraw();
                for (SourceDestinations destinations : this.lastDestinations) {
                    Color color = Color.getHSBColor(hue += 0.15f, 1.0f, 1.0f);
                    FontOptions options = new FontOptions(16).outline().color(color);
                    tableDraw.newRow().addTextColumn(this.selectedSourceID == destinations.sourceRegionID ? ">" : " ", new FontOptions(16).outline()).addTextColumn("ID " + destinations.sourceRegionID + ":", options, 10, 0).addTextColumn(destinations.destinations + " destinations", options, 10, 0);
                }
                tableDraw.draw(10, this.skipY(tableDraw.getHeight()));
            } else {
                this.lastSourcePaths.clear();
                Level level = client.getLevel();
                if (client.getLocalServer() != null) {
                    Level serverLevel = client.getLocalServer().world.getLevel(level.getIdentifier());
                    if (Settings.serverPerspective) {
                        level = serverLevel;
                    }
                }
                if (level != null) {
                    for (Function<Level, PathDoorOption> getter : cacheGetters) {
                        PathDoorOption option = getter.apply(level);
                        if (option == null) continue;
                        this.lastSourcePaths.add(new SourcePaths(option));
                    }
                    this.lastSourcePaths.sort(Comparator.comparingInt(s -> s.paths));
                    float hue = 0.0f;
                    TableContentDraw tableDraw = new TableContentDraw();
                    for (SourcePaths sources : this.lastSourcePaths) {
                        Color color = Color.getHSBColor(hue += 0.15f, 1.0f, 1.0f);
                        FontOptions options = new FontOptions(16).outline().color(color);
                        tableDraw.newRow().addTextColumn(this.selectedCache == sources.option ? ">" : " ", new FontOptions(16).outline()).addTextColumn(sources.option.debugName, options, 10, 0).addTextColumn("" + sources.paths, options, 10, 0);
                    }
                    tableDraw.draw(10, this.skipY(tableDraw.getHeight()));
                } else {
                    this.drawString("--- No level found ---");
                }
            }
        }
    }

    @Override
    protected void drawDebugHUD(Level level, GameCamera camera, PlayerMob perspective) {
        if (this.currentDestinationID != -1) {
            Collection<SubRegion> path = this.currentCache.getCachedPath(this.currentSourceID, this.currentDestinationID);
            SubRegion last = null;
            int i = 0;
            for (SubRegion current : path) {
                Point p2 = current.getAverageLevelTile();
                int x2 = camera.getTileDrawX(p2.x) + 16;
                int y2 = camera.getTileDrawY(p2.y) + 16;
                Color col = Color.getHSBColor((float)i / (float)path.size(), 1.0f, 1.0f);
                if (last != null) {
                    Point p1 = last.getAverageLevelTile();
                    int x1 = camera.getTileDrawX(p1.x) + 16;
                    int y1 = camera.getTileDrawY(p1.y) + 16;
                    Renderer.drawLineRGBA(x1, y1, x2, y2, (float)col.getRed() / 255.0f, (float)col.getGreen() / 255.0f, (float)col.getBlue() / 255.0f, 1.0f);
                }
                Renderer.drawCircle(camera.getTileDrawX(p2.x) + 16, camera.getTileDrawY(p2.y) + 16, 8, 10, (float)col.getRed() / 255.0f, (float)col.getGreen() / 255.0f, (float)col.getBlue() / 255.0f, 1.0f, true);
                ++i;
                last = current;
            }
        }
    }

    static {
        cacheGetters.add(level -> level.regionManager.BASIC_DOOR_OPTIONS);
        cacheGetters.add(level -> level.regionManager.CAN_OPEN_DOORS_OPTIONS);
        cacheGetters.add(level -> level.regionManager.CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS);
        cacheGetters.add(level -> level.regionManager.CANNOT_PASS_DOORS_OPTIONS);
        cacheGetters.add(level -> level.regionManager.CAN_BREAK_OBJECTS_OPTIONS);
    }

    private static class DestinationPath {
        public final PathDoorOption option;
        public final int sourceRegionID;
        public final int destinationRegionID;
        public final Collection<SubRegion> path;

        public DestinationPath(PathDoorOption option, int sourceRegionID, int destinationRegionID) {
            this.option = option;
            this.sourceRegionID = sourceRegionID;
            this.destinationRegionID = destinationRegionID;
            this.path = option.getCachedPath(sourceRegionID, destinationRegionID);
        }
    }

    private static class SourceDestinations {
        public final PathDoorOption option;
        public final int sourceRegionID;
        public final int destinations;

        public SourceDestinations(PathDoorOption option, int sourceRegionID) {
            this.option = option;
            this.sourceRegionID = sourceRegionID;
            this.destinations = option.getDestinationPathRegionIDs(sourceRegionID).size();
        }
    }

    private static class SourcePaths {
        public final PathDoorOption option;
        public final int paths;

        public SourcePaths(PathDoorOption option) {
            this.option = option;
            this.paths = option.getTotalCachedPaths();
        }
    }
}

