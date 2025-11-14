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
import medievalsim.packets.PacketCreateZone;
import medievalsim.zones.AdminZonesLevelData;
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

public class ZoneCreationTool
implements GameTool {
    private final Client client;
    private final Level level;
    private final boolean isProtectedZone;
    private HudDrawElement hudElement;
    private Point mouseDownTile;
    private int colorHue;

    public ZoneCreationTool(Client client, boolean isProtectedZone) {
        this.client = client;
        this.level = client.getLevel();
        this.isProtectedZone = isProtectedZone;
        GameRandom random = new GameRandom();
        this.colorHue = random.getIntBetween(0, 360);
    }

    public void init() {
        this.hudElement = new HudDrawElement(){

            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (ZoneCreationTool.this.mouseDownTile != null) {
                    int currentTileX = ZoneCreationTool.this.getMouseTileX();
                    int currentTileY = ZoneCreationTool.this.getMouseTileY();
                    int startX = Math.min(ZoneCreationTool.this.mouseDownTile.x, currentTileX);
                    int startY = Math.min(ZoneCreationTool.this.mouseDownTile.y, currentTileY);
                    int endX = Math.max(ZoneCreationTool.this.mouseDownTile.x, currentTileX);
                    int endY = Math.max(ZoneCreationTool.this.mouseDownTile.y, currentTileY);
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    Zoning tempZoning = new Zoning(true);
                    tempZoning.addRectangle(selection);
                    Color edgeColor = Color.getHSBColor((float)ZoneCreationTool.this.colorHue / 360.0f, 0.8f, 1.0f);
                    Color fillColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 50);
                    final SharedTextureDrawOptions options = tempZoning.getDrawOptions(edgeColor, fillColor, camera);
                    if (options != null) {
                        list.add(new SortedDrawable(){

                            public int getPriority() {
                                return -100000;
                            }

                            public void draw(TickManager tickManager) {
                                options.draw();
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
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                this.createZone(selection);
                event.use();
                this.mouseDownTile = null;
                return true;
            }
        }
        if (!(event.isMoveUsed() || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(event) || event.isUsed() || !event.state || event.getID() != -100)) {
            this.mouseDownTile = new Point(tileX, tileY);
            event.use();
            return true;
        }
        return false;
    }

    public boolean controllerEvent(ControllerEvent event) {
        State currentState = GlobalData.getCurrentState();
        InputPosition mousePos = WindowManager.getWindow().mousePos();
        int tileX = currentState.getCamera().getMouseLevelTilePosX(mousePos);
        int tileY = currentState.getCamera().getMouseLevelTilePosY(mousePos);
        if (this.mouseDownTile != null && event.isButton && !event.buttonState) {
            int startX = Math.min(this.mouseDownTile.x, tileX);
            int startY = Math.min(this.mouseDownTile.y, tileY);
            int endX = Math.max(this.mouseDownTile.x, tileX);
            int endY = Math.max(this.mouseDownTile.y, tileY);
            if (event.getState() == ControllerInput.MENU_NEXT) {
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                this.createZone(selection);
                event.use();
                this.mouseDownTile = null;
                return true;
            }
        }
        if (event.isButton && event.buttonState && event.getState() == ControllerInput.MENU_NEXT) {
            this.mouseDownTile = new Point(tileX, tileY);
            event.use();
            return true;
        }
        return false;
    }

    private void createZone(Rectangle selection) {
        if (selection.width <= 0 || selection.height <= 0) {
            return;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(this.level, true);
        if (zoneData == null) {
            return;
        }
        int zoneNumber = this.isProtectedZone ? zoneData.getProtectedZones().size() + 1 : zoneData.getPvPZones().size() + 1;
        String defaultName = (this.isProtectedZone ? "Protected Zone " : "PVP Zone ") + zoneNumber;
        this.client.network.sendPacket((Packet)new PacketCreateZone(this.isProtectedZone, defaultName, selection));
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
        tooltips.add((Object)new InputTooltip(-100, Localization.translate((String)"ui", (String)"createzone")));
        tooltips.add((Object)new InputTooltip(256, Localization.translate((String)"ui", (String)"cancel")));
        return tooltips;
    }
}

