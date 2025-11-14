/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.client.ClientDebugMapHudDrawElement;
import necesse.engine.network.client.ClientLevelManager;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.DrawOnMapEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormTextureMapBox;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.floatMenu.ContinueComponentFloatMenu;
import necesse.gfx.forms.presets.EditMapMarkerForm;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawOnMap;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.mapData.ClientDiscoveredMapRegion;
import necesse.level.maps.mapData.DiscoveredMapBoundsExecutor;
import necesse.level.maps.mapData.MapDrawElement;

public class FormMapBox
extends FormTextureMapBox {
    public final Client client;
    public final boolean isMinimap;
    private DiscoveredMapBoundsExecutor mapTextureGetter;
    private int mouseDownX;
    private int mouseDownY;
    private long mouseDownTime;

    public FormMapBox(Client client, int x, int y, int width, int height, int[] zoomLevels, int zoomLevel, boolean allowControllerFocus, boolean isMinimap) {
        super(x, y, width, height, 256, zoomLevels, zoomLevel, allowControllerFocus);
        this.client = client;
        this.isMinimap = isMinimap;
    }

    @Override
    public int getTileScale() {
        return 32;
    }

    @Override
    public Rectangle getTileBounds() {
        Level level = this.client.getLevel();
        if (level != null) {
            return new Rectangle(0, 0, level.tileWidth, level.tileHeight);
        }
        return null;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isMouseOver(event) && !event.isUsed() && event.getID() == -100) {
            if (event.state && (this.client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel() || this.client.worldSettings.creativeMode) && WindowManager.getWindow().isKeyDown(340)) {
                if (this.client.worldSettings.cheatsAllowedOrHidden() || this.client.worldSettings.creativeMode) {
                    int posX = this.getMouseMapPosX(event.pos.hudX);
                    int posY = this.getMouseMapPosY(event.pos.hudY);
                    PlayerMob player = this.client.getPlayer();
                    Level level = this.client.getLevel();
                    if (level != null) {
                        Rectangle edge = new Rectangle(level.tileWidth * 32, level.tileHeight * 32);
                        if (edge.width <= 0) {
                            edge.x = posX - 16;
                            edge.width = 32;
                        }
                        if (edge.height <= 0) {
                            edge.y = posY - 16;
                            edge.height = 32;
                        }
                        if (edge.contains(player.getCollision(posX, posY))) {
                            player.setPos(posX, posY, true);
                            this.client.sendMovementPacket(true);
                        }
                    }
                } else {
                    this.client.chat.addMessage(Localization.translate("misc", "allowcheats"));
                }
                event.use();
                this.mouseDown = false;
                return;
            }
            if (event.state) {
                this.mouseDownX = event.pos.hudX;
                this.mouseDownY = event.pos.hudY;
                this.mouseDownTime = System.currentTimeMillis();
            } else {
                long timeSinceMouseDown = System.currentTimeMillis() - this.mouseDownTime;
                if (this.mouseDownX == event.pos.hudX && this.mouseDownY == event.pos.hudY && timeSinceMouseDown <= 500L) {
                    this.mouseDown = false;
                    FormManager formManager = GlobalData.getCurrentState().getFormManager();
                    Level level = this.client.getLevel();
                    if (!formManager.hasFloatMenu() && level != null && level.isOneWorldLevel()) {
                        InputEvent offsetEvent = InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -this.getX(), -this.getY());
                        int mouseHudX = offsetEvent.pos.hudX;
                        int mouseHudY = offsetEvent.pos.hudY;
                        int scale = this.zoomLevels[this.zoomLevel];
                        double resHalfX = (double)this.getWidth() / 2.0;
                        double resHalfY = (double)this.getHeight() / 2.0;
                        Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
                        for (ClientLevelManager.MapMarker mapMarker : this.client.levelManager.getMapMarkers()) {
                            int drawX = this.getHudPosX(resHalfX, scale, mapMarker.tileX * 32 + 16);
                            int drawY = this.getHudPosY(resHalfY, scale, mapMarker.tileY * 32 + 16);
                            Rectangle drawBox = new Rectangle(mapMarker.icon.getDrawBoundingBox());
                            drawBox.x += drawX;
                            drawBox.y += drawY;
                            if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                            formManager.openFloatMenu(new ContinueComponentFloatMenu(this, new EditMapMarkerForm(this.client, 400, mapMarker, false)));
                            offsetEvent.use();
                            return;
                        }
                        if (!formManager.hasFloatMenu()) {
                            int posX = this.getMouseMapPosX(event.pos.hudX);
                            int posY = this.getMouseMapPosY(event.pos.hudY);
                            int tileX = GameMath.getTileCoordinate(posX);
                            int tileY = GameMath.getTileCoordinate(posY);
                            ClientLevelManager.MapMarker mapMarker = this.client.levelManager.addMapMarker(MapIconRegistry.defaultIcon, (GameMessage)new StaticMessage(""), this.client.getLevel().getIdentifier(), tileX, tileY);
                            formManager.openFloatMenu(new ContinueComponentFloatMenu(this, new EditMapMarkerForm(this.client, 400, mapMarker, true)));
                        }
                        event.use();
                        return;
                    }
                }
            }
        }
        super.handleInputEvent(event, tickManager, perspective);
        if (this.isMouseOver(event) && !event.isUsed() && event.state && event.getID() == -99) {
            Rectangle drawBox;
            int drawX;
            int drawY;
            int scale = this.zoomLevels[this.zoomLevel];
            double tileScale = 32.0 / (double)scale;
            double resHalfX = (double)this.getWidth() / 2.0;
            double resHalfY = (double)this.getHeight() / 2.0;
            InputEvent offsetEvent = InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -this.getX(), -this.getY());
            Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
            int mouseHudX = offsetEvent.pos.hudX;
            int mouseHudY = offsetEvent.pos.hudY;
            Level level = this.client.getLevel();
            if (level != null) {
                for (DrawOnMapEntity e : level.entityManager.getEntityDrawOnMap()) {
                    if (offsetEvent.isUsed()) {
                        return;
                    }
                    if (!e.isVisibleOnMap(this.client, this.client.levelManager.getMap())) continue;
                    Point mapPos = e.getMapPos();
                    int drawX2 = this.getHudPosX(resHalfX, scale, mapPos.x);
                    drawY = this.getHudPosY(resHalfY, scale, mapPos.y);
                    Rectangle drawBox2 = new Rectangle(e.drawOnMapBox(tileScale, this.isMinimap));
                    drawBox2.x += drawX2;
                    drawBox2.y += drawY;
                    if (!drawBounds.intersects(drawBox2) || !drawBox2.contains(mouseHudX, mouseHudY)) continue;
                    e.onMapInteract(offsetEvent, perspective);
                }
            }
            if (offsetEvent.isUsed()) {
                return;
            }
            ClientClient me = this.client.getClient();
            for (int i = 0; i < this.client.getSlots(); ++i) {
                if (offsetEvent.isUsed()) {
                    return;
                }
                ClientClient cClient = this.client.getClient(i);
                if (cClient == null || !cClient.loadedPlayer || !cClient.hasSpawned() || cClient.isDead() || cClient != me && !cClient.isSameTeam(me) || !cClient.isSamePlace(level)) continue;
                PlayerMob player = cClient.playerMob;
                drawX = this.getHudPosX(resHalfX, scale, player.getX());
                int drawY2 = this.getHudPosY(resHalfY, scale, player.getY());
                drawBox = new Rectangle(player.drawOnMapBox(tileScale, this.isMinimap));
                drawBox.x += drawX;
                drawBox.y += drawY2;
                if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                player.onMapInteract(offsetEvent, perspective);
            }
            if (offsetEvent.isUsed()) {
                return;
            }
            for (MapDrawElement element : this.client.levelManager.getMap().getDrawElements()) {
                if (offsetEvent.isUsed()) {
                    return;
                }
                int drawX3 = this.getHudPosX(resHalfX, scale, element.getX());
                drawY = this.getHudPosY(resHalfY, scale, element.getY());
                Rectangle drawBox3 = new Rectangle(element.getBoundingBox());
                drawBox3.x += drawX3;
                drawBox3.y += drawY;
                if (!drawBounds.intersects(drawBox3) || !drawBox3.contains(mouseHudX, mouseHudY)) continue;
                element.onMapInteract(offsetEvent, perspective);
            }
            FormManager formManager = GlobalData.getCurrentState().getFormManager();
            if (!formManager.hasFloatMenu() && level != null && level.isOneWorldLevel()) {
                for (ClientLevelManager.MapMarker mapMarker : this.client.levelManager.getMapMarkers()) {
                    if (offsetEvent.isUsed()) {
                        return;
                    }
                    drawX = this.getHudPosX(resHalfX, scale, mapMarker.tileX * 32 + 16);
                    int drawY3 = this.getHudPosY(resHalfY, scale, mapMarker.tileY * 32 + 16);
                    drawBox = new Rectangle(mapMarker.icon.getDrawBoundingBox());
                    drawBox.x += drawX;
                    drawBox.y += drawY3;
                    if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                    this.client.levelManager.deleteMapIcon(mapMarker);
                    offsetEvent.use();
                    break;
                }
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (!event.isUsed() && event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU && this.isControllerSelected) {
            FormManager formManager = GlobalData.getCurrentState().getFormManager();
            Level level = this.client.getLevel();
            if (!formManager.hasFloatMenu() && level != null && level.isOneWorldLevel()) {
                InputEvent offsetEvent = InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), InputEvent.ControllerButtonEvent(event, tickManager), -this.getX(), -this.getY());
                int mouseHudX = this.getWidth() / 2;
                int mouseHudY = this.getHeight() / 2;
                int scale = this.zoomLevels[this.zoomLevel];
                double resHalfX = (double)this.getWidth() / 2.0;
                double resHalfY = (double)this.getHeight() / 2.0;
                Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
                for (ClientLevelManager.MapMarker mapMarker : this.client.levelManager.getMapMarkers()) {
                    int drawX = this.getHudPosX(resHalfX, scale, mapMarker.tileX * 32 + 16);
                    int drawY = this.getHudPosY(resHalfY, scale, mapMarker.tileY * 32 + 16);
                    Rectangle drawBox = new Rectangle(mapMarker.icon.getDrawBoundingBox());
                    drawBox.x += drawX;
                    drawBox.y += drawY;
                    if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                    EditMapMarkerForm editForm = new EditMapMarkerForm(this.client, 400, mapMarker, false);
                    editForm.onContinue(() -> {
                        this.isControllerSelected = true;
                    });
                    formManager.openFloatMenu(new ContinueComponentFloatMenu(this, editForm));
                    offsetEvent.use();
                    return;
                }
                int posX = this.getMouseMapPosX(this.getWidth() / 2 - this.getX());
                int posY = this.getMouseMapPosY(this.getHeight() / 2 - this.getY());
                int tileX = GameMath.getTileCoordinate(posX);
                int tileY = GameMath.getTileCoordinate(posY);
                ClientLevelManager.MapMarker mapMarker = this.client.levelManager.addMapMarker(MapIconRegistry.defaultIcon, (GameMessage)new StaticMessage(""), this.client.getLevel().getIdentifier(), tileX, tileY);
                EditMapMarkerForm editForm = new EditMapMarkerForm(this.client, 400, mapMarker, true);
                editForm.onContinue(() -> {
                    this.isControllerSelected = true;
                });
                formManager.openFloatMenu(new ContinueComponentFloatMenu(this, editForm));
                event.use();
                this.isControllerSelected = false;
                return;
            }
        }
        if (!event.isUsed() && event.getState() == ControllerInput.MENU_SELECT && this.isControllerFocus() && event.buttonState) {
            Rectangle drawBox;
            int drawX;
            int drawY;
            int scale = this.zoomLevels[this.zoomLevel];
            double tileScale = 32.0 / (double)scale;
            double resHalfX = (double)this.getWidth() / 2.0;
            double resHalfY = (double)this.getHeight() / 2.0;
            InputEvent offsetEvent = InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), InputEvent.ControllerButtonEvent(event, tickManager), -this.getX(), -this.getY());
            Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
            int mouseHudX = this.getWidth() / 2;
            int mouseHudY = this.getHeight() / 2;
            Level level = this.client.getLevel();
            if (level != null) {
                for (DrawOnMapEntity e : level.entityManager.getEntityDrawOnMap()) {
                    if (offsetEvent.isUsed()) {
                        return;
                    }
                    if (!e.isVisibleOnMap(this.client, this.client.levelManager.getMap())) continue;
                    Point mapPos = e.getMapPos();
                    int drawX2 = this.getHudPosX(resHalfX, scale, mapPos.x);
                    drawY = this.getHudPosY(resHalfY, scale, mapPos.y);
                    Rectangle drawBox2 = new Rectangle(e.drawOnMapBox(tileScale, this.isMinimap));
                    drawBox2.x += drawX2;
                    drawBox2.y += drawY;
                    if (!drawBounds.intersects(drawBox2) || !drawBox2.contains(mouseHudX, mouseHudY)) continue;
                    e.onMapInteract(offsetEvent, perspective);
                }
            }
            if (offsetEvent.isUsed()) {
                return;
            }
            ClientClient me = this.client.getClient();
            for (int i = 0; i < this.client.getSlots(); ++i) {
                if (offsetEvent.isUsed()) {
                    return;
                }
                ClientClient cClient = this.client.getClient(i);
                if (cClient == null || !cClient.loadedPlayer || !cClient.hasSpawned() || cClient.isDead() || cClient != me && !cClient.isSameTeam(me) || !cClient.isSamePlace(level)) continue;
                PlayerMob player = cClient.playerMob;
                drawX = this.getHudPosX(resHalfX, scale, player.getX());
                int drawY2 = this.getHudPosY(resHalfY, scale, player.getY());
                drawBox = new Rectangle(player.drawOnMapBox(tileScale, this.isMinimap));
                drawBox.x += drawX;
                drawBox.y += drawY2;
                if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                player.onMapInteract(offsetEvent, perspective);
            }
            if (offsetEvent.isUsed()) {
                return;
            }
            for (MapDrawElement element : this.client.levelManager.getMap().getDrawElements()) {
                if (offsetEvent.isUsed()) {
                    return;
                }
                int drawX3 = this.getHudPosX(resHalfX, scale, element.getX());
                drawY = this.getHudPosY(resHalfY, scale, element.getY());
                Rectangle drawBox3 = new Rectangle(element.getBoundingBox());
                drawBox3.x += drawX3;
                drawBox3.y += drawY;
                if (!drawBounds.intersects(drawBox3) || !drawBox3.contains(mouseHudX, mouseHudY)) continue;
                element.onMapInteract(offsetEvent, perspective);
            }
            FormManager formManager = GlobalData.getCurrentState().getFormManager();
            if (this.isControllerSelected && !formManager.hasFloatMenu() && level != null && level.isOneWorldLevel()) {
                for (ClientLevelManager.MapMarker mapMarker : this.client.levelManager.getMapMarkers()) {
                    if (offsetEvent.isUsed()) {
                        return;
                    }
                    drawX = this.getHudPosX(resHalfX, scale, mapMarker.tileX * 32 + 16);
                    int drawY3 = this.getHudPosY(resHalfY, scale, mapMarker.tileY * 32 + 16);
                    drawBox = new Rectangle(mapMarker.icon.getDrawBoundingBox());
                    drawBox.x += drawX;
                    drawBox.y += drawY3;
                    if (!drawBounds.intersects(drawBox) || !drawBox.contains(mouseHudX, mouseHudY)) continue;
                    this.client.levelManager.deleteMapIcon(mapMarker);
                    offsetEvent.use();
                    break;
                }
            }
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        FormManager formManager;
        super.drawControllerFocus(current);
        if (this.isControllerSelected && !(formManager = GlobalData.getCurrentState().getFormManager()).hasFloatMenu()) {
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "placemapmarker"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
        }
    }

    @Override
    public void setupMapDraw(int startTileX, int startTileY, int endTileX, int endTileY) {
        super.setupMapDraw(startTileX, startTileY, endTileX, endTileY);
        ClientDiscoveredMap map = this.client.levelManager.getMap();
        this.mapTextureGetter = map != null ? new DiscoveredMapBoundsExecutor(map, startTileX, startTileY, endTileX, endTileY, true, true) : null;
    }

    @Override
    public void drawMapTexture(int textureX, int textureY, double tileScale, int drawX, int drawY) {
        if (this.mapTextureGetter == null) {
            return;
        }
        ClientDiscoveredMapRegion region = (ClientDiscoveredMapRegion)this.mapTextureGetter.getRegion(textureX, textureY);
        if (region == null) {
            return;
        }
        GameTexture texture = region.getTexture();
        if (texture == null) {
            return;
        }
        int textureWidth = (int)(tileScale * (double)texture.getWidth());
        int textureHeight = (int)(tileScale * (double)texture.getHeight());
        texture.initDraw().size(textureWidth, textureHeight).draw(drawX, drawY);
        Renderer.drawShape(new Rectangle(textureWidth, textureHeight), drawX, drawY, false, 1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void drawMapOverlays(TickManager tickManager, PlayerMob perspective, int scale, double tileScale, double resHalfX, double resHalfY, int mouseHudX, int mouseHudY) {
        int mouseTileY;
        int mouseTileX;
        int drawY;
        int drawX;
        boolean isHovering = this.isHovering || this.isControllerSelected();
        Level level = Settings.serverPerspective && this.client.getLocalServer() != null ? this.client.getLocalServer().world.getLevel(this.client.getLevel().getIdentifier()) : this.client.getLevel();
        if (level == null) {
            return;
        }
        ClientDiscoveredMap map = this.client.levelManager.getMap();
        if (map == null) {
            return;
        }
        Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
        level.entityManager.getEntityDrawOnMap().stream().filter(e -> e.isVisibleOnMap(this.client, map)).forEach(e -> {
            Point mapPos = e.getMapPos();
            int drawX = this.getHudPosX(resHalfX, scale, mapPos.x);
            int drawY = this.getHudPosY(resHalfY, scale, mapPos.y);
            Rectangle drawBox = new Rectangle(e.drawOnMapBox(tileScale, this.isMinimap));
            drawBox.x += drawX;
            drawBox.y += drawY;
            if (drawBounds.intersects(drawBox)) {
                e.drawOnMap(tickManager, this.client, drawX, drawY, tileScale, drawBounds, this.isMinimap);
                if (isHovering && drawBox.contains(mouseHudX, mouseHudY)) {
                    String interactTip;
                    GameTooltips tooltips = e.getMapTooltips();
                    if (tooltips != null) {
                        GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                    }
                    if ((interactTip = e.getMapInteractTooltip()) != null) {
                        GameTooltipManager.addTooltip(new InputTooltip(-99, interactTip), TooltipLocation.FORM_FOCUS);
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }
        });
        ClientClient me = this.client.getClient();
        for (int i = 0; i < this.client.getSlots(); ++i) {
            String interactTip;
            ClientClient cClient = this.client.getClient(i);
            if (cClient == null || !cClient.loadedPlayer || !cClient.hasSpawned() || cClient.isDead() || cClient != me && !cClient.isSameTeam(me) || !cClient.isSamePlace(level)) continue;
            PlayerMob player = cClient.playerMob;
            drawX = this.getHudPosX(resHalfX, scale, player.getX());
            drawY = this.getHudPosY(resHalfY, scale, player.getY());
            Rectangle drawBox = new Rectangle(player.drawOnMapBox(tileScale, this.isMinimap));
            drawBox.x += drawX;
            drawBox.y += drawY;
            if (!drawBounds.intersects(drawBox)) continue;
            player.drawOnMap(tickManager, this.client, drawX, drawY, tileScale, drawBounds, this.isMinimap);
            if (!isHovering || !drawBox.contains(mouseHudX, mouseHudY)) continue;
            GameTooltips tooltips = player.getMapTooltips();
            if (tooltips != null) {
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
            if ((interactTip = player.getMapInteractTooltip()) == null) continue;
            GameTooltipManager.addTooltip(new InputTooltip(-99, interactTip), TooltipLocation.FORM_FOCUS);
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
        }
        for (HudDrawOnMap hudMapDraw : level.hudManager.getMapDraws()) {
            if (!hudMapDraw.shouldDrawOnMap(this.client, map)) continue;
            Point levelPos = hudMapDraw.getMapLevelPos();
            drawX = this.getHudPosX(resHalfX, scale, levelPos.x);
            drawY = this.getHudPosY(resHalfY, scale, levelPos.y);
            Rectangle elementDrawBounds = hudMapDraw.getMapLevelDrawBounds();
            if (elementDrawBounds != null) {
                elementDrawBounds = new Rectangle(elementDrawBounds);
                elementDrawBounds.x += drawX;
                elementDrawBounds.y += drawY;
            }
            if (elementDrawBounds != null && !drawBounds.intersects(elementDrawBounds)) continue;
            hudMapDraw.drawOnMap(tickManager, this.client, drawX, drawY, tileScale, drawBounds, this.isMinimap);
        }
        ClientDebugMapHudDrawElement debugMapDrawElement = this.client.levelManager.debugMapDrawElement;
        if (debugMapDrawElement != null && debugMapDrawElement.shouldDrawOnMap(this.client, map)) {
            Point levelPos = debugMapDrawElement.getMapLevelPos();
            int drawX2 = this.getHudPosX(resHalfX, scale, levelPos.x);
            int drawY2 = this.getHudPosY(resHalfY, scale, levelPos.y);
            debugMapDrawElement.drawOnMap(tickManager, this.client, drawX2, drawY2, tileScale, drawBounds, this.isMinimap);
        }
        ControllerFocus currentFocus = this.getManager().getCurrentFocus();
        for (MapDrawElement element : map.getDrawElements()) {
            int drawX3 = this.getHudPosX(resHalfX, scale, element.getX());
            int drawY3 = this.getHudPosY(resHalfY, scale, element.getY());
            Rectangle drawBox = new Rectangle(element.getBoundingBox());
            drawBox.x += drawX3;
            drawBox.y += drawY3;
            if (!drawBounds.intersects(drawBox)) continue;
            if (isHovering && drawBox.contains(mouseHudX, mouseHudY)) {
                String interactTip;
                GameTooltips tooltips = element.getTooltips(drawX3, drawY3, perspective);
                if (tooltips != null) {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
                if ((interactTip = element.getMapInteractTooltip()) != null) {
                    if (Input.lastInputIsController && this.isControllerSelected() && currentFocus != null) {
                        GameTooltipManager.addTooltip(new InputTooltip(ControllerInput.MENU_SELECT, interactTip), TooltipLocation.FORM_FOCUS);
                    } else {
                        GameTooltipManager.addTooltip(new InputTooltip(-99, interactTip), TooltipLocation.FORM_FOCUS);
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }
            element.draw(drawX3, drawY3, perspective);
        }
        if (level.isOneWorldLevel()) {
            boolean hoveringMarker = false;
            for (ClientLevelManager.MapMarker mapMarker : this.client.levelManager.getMapMarkers()) {
                boolean sameLevel = level.getIdentifier().equals(mapMarker.levelIdentifier);
                int drawX4 = this.getHudPosX(resHalfX, scale, mapMarker.tileX * 32 + 16);
                int drawY4 = this.getHudPosY(resHalfY, scale, mapMarker.tileY * 32 + 16);
                Rectangle drawBox = new Rectangle(mapMarker.icon.getDrawBoundingBox());
                drawBox.x += drawX4;
                drawBox.y += drawY4;
                if (!drawBounds.intersects(drawBox)) continue;
                if (isHovering && drawBox.contains(mouseHudX, mouseHudY)) {
                    ListGameTooltips tooltips = new ListGameTooltips();
                    if (!mapMarker.name.isEmpty()) {
                        tooltips.add(mapMarker.name);
                    }
                    if (!sameLevel) {
                        tooltips.add("(" + mapMarker.levelIdentifier.getDisplayName().translate() + ")");
                    }
                    hoveringMarker = true;
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
                mapMarker.icon.drawIcon(drawX4, drawY4, sameLevel ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.5f));
            }
            if (hoveringMarker) {
                FormManager formManager = GlobalData.getCurrentState().getFormManager();
                ListGameTooltips tooltips = new ListGameTooltips();
                if (Input.lastInputIsController && this.isControllerSelected() && currentFocus != null) {
                    if (!formManager.hasFloatMenu()) {
                        tooltips.add(new InputTooltip(ControllerInput.MENU_ITEM_ACTIONS_MENU, Localization.translate("controls", "edittip")));
                        tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("controls", "deletetip")));
                    }
                } else {
                    if (!formManager.hasFloatMenu()) {
                        tooltips.add(new InputTooltip(-100, Localization.translate("controls", "edittip")));
                        tooltips.add(new InputTooltip(-99, Localization.translate("controls", "deletetip")));
                    }
                    Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
        }
        if (isHovering && map.isTileKnown(mouseTileX = GameMath.getTileCoordinate(this.getMouseMapPosX(mouseHudX + this.getX())), mouseTileY = GameMath.getTileCoordinate(this.getMouseMapPosY(mouseHudY + this.getY()))) && level.regionManager.isTileLoaded(mouseTileX, mouseTileY)) {
            GameTooltips objectTooltips;
            GameTooltips tileTooltips = level.getLevelTile(mouseTileX, mouseTileY).getMapTooltips();
            if (tileTooltips != null) {
                GameTooltipManager.addTooltip(tileTooltips, TooltipLocation.FORM_FOCUS);
            }
            if ((objectTooltips = level.getLevelObject(mouseTileX, mouseTileY).getMapTooltips()) != null) {
                GameTooltipManager.addTooltip(objectTooltips, TooltipLocation.FORM_FOCUS);
            }
        }
    }
}

