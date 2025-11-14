/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.state.State;
import necesse.engine.util.ObjectValue;
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
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public abstract class CreateOrExpandWorkZoneGameTool
implements GameTool {
    public final Level level;
    private HudDrawElement hudElement;
    private SettlementWorkZone lastHoverZone;
    private Point mouseDownTile;
    private boolean isRemoving;

    public CreateOrExpandWorkZoneGameTool(Level level) {
        this.level = level;
    }

    @Override
    public void init() {
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                Point mouseDownTile = CreateOrExpandWorkZoneGameTool.this.mouseDownTile;
                if (mouseDownTile != null) {
                    Color fillColor;
                    Color edgeColor;
                    int tileX = camera.getMouseLevelTilePosX();
                    int tileY = camera.getMouseLevelTilePosY();
                    Rectangle rectangle = new Rectangle(Math.min(mouseDownTile.x, tileX) * 32, Math.min(mouseDownTile.y, tileY) * 32, (Math.abs(mouseDownTile.x - tileX) + 1) * 32, (Math.abs(mouseDownTile.y - tileY) + 1) * 32);
                    if (CreateOrExpandWorkZoneGameTool.this.isRemoving) {
                        edgeColor = new Color(255, 0, 0, 170);
                        fillColor = new Color(255, 0, 0, 100);
                    } else {
                        edgeColor = new Color(0, 255, 0, 170);
                        fillColor = new Color(0, 255, 0, 100);
                    }
                    final SharedTextureDrawOptions drawOptions = Zoning.getRectangleDrawOptions(rectangle, edgeColor, fillColor, camera);
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -2000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
            }
        };
        this.level.hudManager.addElement(this.hudElement);
    }

    protected SettlementWorkZone getHoverZone(State currentState, InputPosition inputPosition) {
        int tileX = currentState.getCamera().getMouseLevelTilePosX(inputPosition);
        int tileY = currentState.getCamera().getMouseLevelTilePosY(inputPosition);
        return this.streamEditZones().filter(z -> z.containsTile(tileX, tileY)).findFirst().orElse(null);
    }

    @Override
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
                    SettlementWorkZone editZone = this.streamEditZones().filter(z -> z.containsTile(this.mouseDownTile.x, this.mouseDownTile.y)).findFirst().orElse(null);
                    if (editZone == null) {
                        anchor = null;
                        editZone = this.streamEditZones().filter(z -> {
                            Rectangle intersection;
                            Rectangle bounds = z.getTileBounds();
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
                    event.use();
                    this.mouseDownTile = null;
                }
            } else if (event.getID() == -99 && this.isRemoving) {
                this.mouseDownTile = null;
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                List collect = this.streamEditZones().map(zone -> {
                    Rectangle bounds = zone.getTileBounds();
                    if (bounds != null) {
                        Rectangle intersection = bounds.intersection(selection);
                        if (intersection.width > 0 && intersection.height > 0) {
                            return new ObjectValue<SettlementWorkZone, Rectangle>((SettlementWorkZone)zone, intersection);
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                for (ObjectValue obj : collect) {
                    this.onRemovedZone((SettlementWorkZone)obj.object, (Rectangle)obj.value);
                }
                event.use();
            }
        }
        if (!event.isMoveUsed()) {
            if (currentState.getFormManager() == null || !currentState.getFormManager().isMouseOver(event)) {
                this.lastHoverZone = this.getHoverZone(currentState, event.pos);
                if (this.lastHoverZone != null) {
                    event.useMove();
                }
                if (!event.isUsed() && event.state) {
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
            }
        } else {
            this.lastHoverZone = null;
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        State currentState = GlobalData.getCurrentState();
        GameWindow window = WindowManager.getWindow();
        int tileX = currentState.getCamera().getMouseLevelTilePosX(window.mousePos());
        int tileY = currentState.getCamera().getMouseLevelTilePosY(window.mousePos());
        if (this.mouseDownTile != null && event.isButton && !event.buttonState) {
            int startX = Math.min(this.mouseDownTile.x, tileX);
            int startY = Math.min(this.mouseDownTile.y, tileY);
            int endX = Math.max(this.mouseDownTile.x, tileX);
            int endY = Math.max(this.mouseDownTile.y, tileY);
            if (event.getState() == ControllerInput.MENU_NEXT) {
                if (!this.isRemoving) {
                    Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                    Point anchor = this.mouseDownTile;
                    SettlementWorkZone editZone = this.streamEditZones().filter(z -> z.containsTile(this.mouseDownTile.x, this.mouseDownTile.y)).findFirst().orElse(null);
                    if (editZone == null) {
                        anchor = null;
                        editZone = this.streamEditZones().filter(z -> {
                            Rectangle intersection;
                            Rectangle bounds = z.getTileBounds();
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
                    event.use();
                    this.mouseDownTile = null;
                }
            } else if (event.getState() == ControllerInput.MENU_PREV && this.isRemoving) {
                this.mouseDownTile = null;
                Rectangle selection = new Rectangle(startX, startY, endX - startX + 1, endY - startY + 1);
                List collect = this.streamEditZones().map(zone -> {
                    Rectangle bounds = zone.getTileBounds();
                    if (bounds != null) {
                        Rectangle intersection = bounds.intersection(selection);
                        if (intersection.width > 0 && intersection.height > 0) {
                            return new ObjectValue<SettlementWorkZone, Rectangle>((SettlementWorkZone)zone, intersection);
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                for (ObjectValue obj : collect) {
                    this.onRemovedZone((SettlementWorkZone)obj.object, (Rectangle)obj.value);
                }
                event.use();
            }
        }
        if (!event.isUsed() && event.isButton && event.buttonState) {
            if (currentState.getFormManager() == null || !currentState.getFormManager().isMouseOver(window.mousePos())) {
                this.lastHoverZone = this.getHoverZone(currentState, window.mousePos());
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
        } else {
            this.lastHoverZone = null;
        }
        return false;
    }

    @Override
    public void isCancelled() {
        this.hudElement.remove();
    }

    @Override
    public void isCleared() {
        this.hudElement.remove();
    }

    @Override
    public GameTooltips getTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (this.mouseDownTile == null) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcreateorexpandzone")));
                tooltips.add(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementshrinkzone")));
            } else {
                tooltips.add(new InputTooltip(-100, Localization.translate("ui", "settlementcreateorexpandzone")));
                tooltips.add(new InputTooltip(-99, Localization.translate("ui", "settlementshrinkzone")));
            }
        }
        return tooltips;
    }

    public abstract Stream<SettlementWorkZone> streamEditZones();

    public abstract void onCreatedNewZone(Rectangle var1, Point var2);

    public abstract void onRemovedZone(SettlementWorkZone var1, Rectangle var2);

    public abstract void onExpandedZone(SettlementWorkZone var1, Rectangle var2, Point var3);
}

