/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.gameTool.GameTool
 *  necesse.engine.input.InputEvent
 *  necesse.engine.input.InputPosition
 *  necesse.engine.input.controller.ControllerEvent
 *  necesse.engine.input.controller.ControllerInput
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.State
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.Zoning
 *  necesse.engine.window.GameWindow
 *  necesse.engine.window.WindowManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.SharedTextureDrawOptions
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.InputTooltip
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.level.maps.Level
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.zones;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import medievalsim.packets.PacketCreateZone;
import medievalsim.packets.PacketExpandZone;
import medievalsim.packets.PacketShrinkZone;
import medievalsim.zones.AdminZone;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.state.State;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class CreateOrExpandZoneTool
implements GameTool {
    private final Client client;
    private final Level level;
    private final boolean isProtectedZone;
    private final Supplier<Map<Integer, ? extends AdminZone>> zonesSupplier;
    private HudDrawElement hudElement;
    private Point mouseDownTile;
    private boolean isRemoving;
    private AdminZone lastHoverZone;
    private int colorHue;

    public CreateOrExpandZoneTool(Client client, boolean isProtectedZone, Supplier<Map<Integer, ? extends AdminZone>> zonesSupplier) {
        this.client = client;
        this.level = client.getLevel();
        this.isProtectedZone = isProtectedZone;
        this.zonesSupplier = zonesSupplier;
        GameRandom random = new GameRandom();
        this.colorHue = random.getIntBetween(0, 360);
    }

    public void init() {
        this.hudElement = new HudDrawElement(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                Map<Integer, ? extends AdminZone> zones;
                Color fillColor;
                Color edgeColor;
                Point mouseDownTile = CreateOrExpandZoneTool.this.mouseDownTile;
                if (mouseDownTile != null) {
                    int tileX = camera.getMouseLevelTilePosX();
                    int n = camera.getMouseLevelTilePosY();
                    Rectangle rectangle = new Rectangle(Math.min(mouseDownTile.x, tileX) * 32, Math.min(mouseDownTile.y, n) * 32, (Math.abs(mouseDownTile.x - tileX) + 1) * 32, (Math.abs(mouseDownTile.y - n) + 1) * 32);
                    if (CreateOrExpandZoneTool.this.isRemoving) {
                        edgeColor = new Color(255, 0, 0, 170);
                        fillColor = new Color(255, 0, 0, 100);
                    } else {
                        edgeColor = new Color(0, 255, 0, 170);
                        fillColor = new Color(0, 255, 0, 100);
                    }
                    final SharedTextureDrawOptions drawOptions = Zoning.getRectangleDrawOptions((Rectangle)rectangle, (Color)edgeColor, (Color)fillColor, (GameCamera)camera);
                    list.add(new SortedDrawable(){

                        public int getPriority() {
                            return -2000000;
                        }

                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
                if ((zones = CreateOrExpandZoneTool.this.zonesSupplier.get()) != null) {
                    for (AdminZone adminZone : zones.values()) {
                        Zoning zoning;
                        if (adminZone == null || adminZone.zoning == null) continue;
                        edgeColor = Color.getHSBColor((float)adminZone.colorHue / 360.0f, 0.8f, 1.0f);
                        fillColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 40);
                        Zoning zoning2 = zoning = adminZone.zoning;
                        synchronized (zoning2) {
                            SharedTextureDrawOptions options = adminZone.zoning.getDrawOptions(edgeColor, fillColor, camera);
                            if (options != null) {
                                final SharedTextureDrawOptions finalOptions = options;
                                list.add(new SortedDrawable(){

                                    public int getPriority() {
                                        return -100000;
                                    }

                                    public void draw(TickManager tickManager) {
                                        finalOptions.draw();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        };
        this.level.hudManager.addElement(this.hudElement);
    }

    private AdminZone getHoverZone(State currentState, InputPosition inputPosition) {
        int tileX = currentState.getCamera().getMouseLevelTilePosX(inputPosition);
        int tileY = currentState.getCamera().getMouseLevelTilePosY(inputPosition);
        return this.streamEditZones().filter(z -> z.containsTile(tileX, tileY)).findFirst().orElse(null);
    }

    private Stream<? extends AdminZone> streamEditZones() {
        Map<Integer, ? extends AdminZone> zones = this.zonesSupplier.get();
        if (zones == null) {
            return Stream.empty();
        }
        return zones.values().stream().filter(z -> z != null);
    }

    public boolean inputEvent(InputEvent event) {
        State currentState = GlobalData.getCurrentState();
        int tileX = currentState.getCamera().getMouseLevelTilePosX(event);
        int tileY = currentState.getCamera().getMouseLevelTilePosY(event);
        if (this.mouseDownTile != null && !event.state) {
            int startX = Math.min(this.mouseDownTile.x, tileX);
            int startY = Math.min(this.mouseDownTile.y, tileY);
            int endX = Math.max(this.mouseDownTile.x, tileX);
            int endY = Math.max(this.mouseDownTile.y, tileY);
            if (event.getID() == -100) {
                if (!this.isRemoving) {
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    Point anchor = this.mouseDownTile;
                    AdminZone editZone = this.streamEditZones().filter(z -> z.containsTile(this.mouseDownTile.x, this.mouseDownTile.y)).findFirst().orElse(null);
                    if (editZone == null) {
                        anchor = null;
                        editZone = this.streamEditZones().filter(z -> {
                            Rectangle intersection;
                            Rectangle bounds = z.zoning.getTileBounds();
                            if (bounds != null && !(intersection = bounds.intersection(selection)).isEmpty()) {
                                for (int x = 0; x < intersection.width; ++x) {
                                    for (int y = 0; y < intersection.height; ++y) {
                                        if (!z.containsTile(intersection.x + x, intersection.y + y)) continue;
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }).findFirst().orElse(null);
                    }
                    if (editZone != null) {
                        this.onExpandedZone(editZone, selection, anchor);
                    } else {
                        this.onCreatedNewZone(selection, this.mouseDownTile);
                    }
                }
                event.use();
                this.mouseDownTile = null;
            } else if (event.getID() == -99) {
                if (this.isRemoving) {
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    Map<Integer, ? extends AdminZone> allZones = this.zonesSupplier.get();
                    if (allZones != null) {
                        for (AdminZone adminZone : allZones.values()) {
                            Rectangle bounds = adminZone.zoning.getTileBounds();
                            if (bounds == null || !bounds.intersects(selection)) continue;
                            this.onRemovedZone(adminZone, selection);
                        }
                    }
                }
                event.use();
                this.mouseDownTile = null;
            }
        } else if (event.state && (currentState.getFormManager() == null || !currentState.getFormManager().isMouseOver(event))) {
            if (event.getID() == -100) {
                this.mouseDownTile = new Point(tileX, tileY);
                this.isRemoving = false;
                event.use();
            } else if (event.getID() == -99) {
                this.mouseDownTile = new Point(tileX, tileY);
                this.isRemoving = true;
                event.use();
            }
        }
        return false;
    }

    public boolean controllerEvent(ControllerEvent event) {
        State currentState = GlobalData.getCurrentState();
        GameWindow window = WindowManager.getWindow();
        int tileX = this.getMouseTileX();
        int tileY = this.getMouseTileY();
        if (this.mouseDownTile != null && event.isButton && !event.buttonState) {
            int startX = Math.min(this.mouseDownTile.x, tileX);
            int startY = Math.min(this.mouseDownTile.y, tileY);
            int endX = Math.max(this.mouseDownTile.x, tileX);
            int endY = Math.max(this.mouseDownTile.y, tileY);
            if (event.getState() == ControllerInput.MENU_NEXT) {
                if (!this.isRemoving) {
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    AdminZone editZone = this.streamEditZones().filter(z -> z.containsTile(this.mouseDownTile.x, this.mouseDownTile.y)).findFirst().orElse(null);
                    if (editZone != null) {
                        this.onExpandedZone(editZone, selection, this.mouseDownTile);
                    } else {
                        this.onCreatedNewZone(selection, this.mouseDownTile);
                    }
                }
                event.use();
                this.mouseDownTile = null;
            }
        } else if (!event.isUsed() && event.isButton && event.buttonState && (currentState.getFormManager() == null || !currentState.getFormManager().isMouseOver(window.mousePos())) && event.getState() == ControllerInput.MENU_NEXT) {
            this.mouseDownTile = new Point(tileX, tileY);
            this.isRemoving = false;
            event.use();
        }
        return false;
    }

    private void onCreatedNewZone(Rectangle selection, Point anchor) {
        this.client.network.sendPacket((Packet)new PacketCreateZone(this.isProtectedZone, "New Zone", selection));
    }

    private void onExpandedZone(AdminZone zone, Rectangle selection, Point anchor) {
        this.client.network.sendPacket((Packet)new PacketExpandZone(zone.uniqueID, this.isProtectedZone, selection));
    }

    private void onRemovedZone(AdminZone zone, Rectangle selection) {
        this.client.network.sendPacket((Packet)new PacketShrinkZone(zone.uniqueID, this.isProtectedZone, selection));
    }

    private int generateUniqueID() {
        Map<Integer, ? extends AdminZone> zones = this.zonesSupplier.get();
        if (zones == null || zones.isEmpty()) {
            return 1;
        }
        int maxID = zones.keySet().stream().max(Integer::compare).orElse(0);
        return maxID + 1;
    }

    public GameTooltips getTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (this.mouseDownTile != null) {
            if (this.isRemoving) {
                tooltips.add(Localization.translate((String)"ui", (String)"shrinkzonetooltip"));
            } else {
                tooltips.add(Localization.translate((String)"ui", (String)"expandzonetooltip"));
            }
        } else {
            tooltips.add(Localization.translate((String)"ui", (String)"clickdragtocreate"));
            tooltips.add((Object)new InputTooltip(-99, Localization.translate((String)"ui", (String)"shrinkzonetooltip")));
        }
        return tooltips;
    }

    public void isCancelled() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    public void isCleared() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }
}

