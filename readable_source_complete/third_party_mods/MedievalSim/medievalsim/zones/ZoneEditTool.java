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
 *  necesse.engine.util.Zoning
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
import medievalsim.packets.PacketExpandZone;
import medievalsim.packets.PacketShrinkZone;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.ProtectedZone;
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
import necesse.engine.util.Zoning;
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

public class ZoneEditTool
implements GameTool {
    private final Client client;
    private final Level level;
    private final AdminZone zone;
    private Point mouseDownTile;
    private boolean isRemoving;
    private HudDrawElement hudElement;

    public ZoneEditTool(Client client, AdminZone zone) {
        this.client = client;
        this.level = client.getLevel();
        this.zone = zone;
    }

    public void init() {
        this.hudElement = new HudDrawElement(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                Zoning zoning;
                Color edgeColor = Color.getHSBColor((float)ZoneEditTool.this.zone.colorHue / 360.0f, 0.8f, 1.0f);
                Color fillColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 30);
                Zoning zoning2 = zoning = ZoneEditTool.this.zone.zoning;
                synchronized (zoning2) {
                    final SharedTextureDrawOptions zoneOptions = ZoneEditTool.this.zone.zoning.getDrawOptions(edgeColor, fillColor, camera);
                    if (zoneOptions != null) {
                        list.add(new SortedDrawable(){

                            public int getPriority() {
                                return -100001;
                            }

                            public void draw(TickManager tickManager) {
                                zoneOptions.draw();
                            }
                        });
                    }
                }
                if (ZoneEditTool.this.mouseDownTile != null) {
                    State currentState = GlobalData.getCurrentState();
                    if (currentState == null || currentState.getCamera() == null) {
                        return;
                    }
                    int currentTileX = currentState.getCamera().getMouseLevelTilePosX();
                    int currentTileY = currentState.getCamera().getMouseLevelTilePosY();
                    int startX = Math.min(ZoneEditTool.this.mouseDownTile.x, currentTileX);
                    int startY = Math.min(ZoneEditTool.this.mouseDownTile.y, currentTileY);
                    int endX = Math.max(ZoneEditTool.this.mouseDownTile.x, currentTileX);
                    int endY = Math.max(ZoneEditTool.this.mouseDownTile.y, currentTileY);
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    Color selectionEdge = ZoneEditTool.this.isRemoving ? Color.RED : Color.GREEN;
                    Color selectionFill = new Color(selectionEdge.getRed(), selectionEdge.getGreen(), selectionEdge.getBlue(), 50);
                    Zoning tempZoning = new Zoning(true);
                    tempZoning.addRectangle(selection);
                    final SharedTextureDrawOptions selectionOptions = tempZoning.getDrawOptions(selectionEdge, selectionFill, camera);
                    if (selectionOptions != null) {
                        list.add(new SortedDrawable(){

                            public int getPriority() {
                                return -100000;
                            }

                            public void draw(TickManager tickManager) {
                                selectionOptions.draw();
                            }
                        });
                    }
                }
            }
        };
        if (this.level != null) {
            this.level.hudManager.addElement(this.hudElement);
        }
    }

    public boolean inputEvent(InputEvent event) {
        State currentState = GlobalData.getCurrentState();
        if (currentState == null || currentState.getCamera() == null) {
            return false;
        }
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
                    this.onExpandedZone(selection);
                    event.use();
                    this.mouseDownTile = null;
                }
            } else if (event.getID() == -99 && this.isRemoving) {
                this.mouseDownTile = null;
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                this.onShrankZone(selection);
                event.use();
            }
        }
        if (!(event.isMoveUsed() || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(event) || event.isUsed() || !event.state)) {
            if (event.getID() == -100) {
                if (this.mouseDownTile != null && this.isRemoving) {
                    this.mouseDownTile = null;
                } else {
                    this.mouseDownTile = new Point(tileX, tileY);
                    this.isRemoving = false;
                }
                event.use();
                return true;
            }
            if (event.getID() == -99) {
                if (this.mouseDownTile != null && !this.isRemoving) {
                    this.mouseDownTile = null;
                } else {
                    this.mouseDownTile = new Point(tileX, tileY);
                    this.isRemoving = true;
                }
                event.use();
                return true;
            }
        }
        return false;
    }

    public boolean controllerEvent(ControllerEvent event) {
        State currentState = GlobalData.getCurrentState();
        if (currentState == null || currentState.getCamera() == null) {
            return false;
        }
        InputPosition mousePos = WindowManager.getWindow().mousePos();
        int tileX = currentState.getCamera().getMouseLevelTilePosX(mousePos);
        int tileY = currentState.getCamera().getMouseLevelTilePosY(mousePos);
        if (this.mouseDownTile != null && event.isButton && !event.buttonState) {
            int startX = Math.min(this.mouseDownTile.x, tileX);
            int startY = Math.min(this.mouseDownTile.y, tileY);
            int endX = Math.max(this.mouseDownTile.x, tileX);
            int endY = Math.max(this.mouseDownTile.y, tileY);
            if (event.getState() == ControllerInput.MENU_NEXT) {
                if (!this.isRemoving) {
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    this.onExpandedZone(selection);
                    event.use();
                    this.mouseDownTile = null;
                }
            } else if (event.getState() == ControllerInput.MENU_PREV && this.isRemoving) {
                this.mouseDownTile = null;
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                this.onShrankZone(selection);
                event.use();
            }
        }
        if (!event.isUsed() && event.isButton && event.buttonState && (currentState.getFormManager() == null || !currentState.getFormManager().isMouseOver(mousePos))) {
            if (event.getState() == ControllerInput.MENU_NEXT) {
                if (this.mouseDownTile != null && this.isRemoving) {
                    this.mouseDownTile = null;
                } else {
                    this.mouseDownTile = new Point(tileX, tileY);
                    this.isRemoving = false;
                }
                event.use();
                return true;
            }
            if (event.getState() == ControllerInput.MENU_PREV) {
                if (this.mouseDownTile != null && !this.isRemoving) {
                    this.mouseDownTile = null;
                } else {
                    this.mouseDownTile = new Point(tileX, tileY);
                    this.isRemoving = true;
                }
                event.use();
                return true;
            }
        }
        return false;
    }

    private void onExpandedZone(Rectangle rectangle) {
        boolean isProtectedZone = this.zone instanceof ProtectedZone;
        this.client.network.sendPacket((Packet)new PacketExpandZone(this.zone.uniqueID, isProtectedZone, rectangle));
        ModLogger.info("Sent expand zone request for '%s' with %s", this.zone.name, rectangle);
    }

    private void onShrankZone(Rectangle rectangle) {
        boolean isProtectedZone = this.zone instanceof ProtectedZone;
        this.client.network.sendPacket((Packet)new PacketShrinkZone(this.zone.uniqueID, isProtectedZone, rectangle));
        ModLogger.info("Sent shrink zone request for '%s' with %s", this.zone.name, rectangle);
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

    public GameTooltips getTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add((Object)new InputTooltip(-100, Localization.translate((String)"ui", (String)"expandzone")));
        tooltips.add((Object)new InputTooltip(-99, Localization.translate((String)"ui", (String)"shrinkzone")));
        tooltips.add((Object)new InputTooltip(256, Localization.translate((String)"ui", (String)"cancel")));
        return tooltips;
    }
}

