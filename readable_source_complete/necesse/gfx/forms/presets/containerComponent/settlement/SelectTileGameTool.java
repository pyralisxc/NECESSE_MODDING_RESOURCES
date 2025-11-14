/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.state.State;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class SelectTileGameTool
implements GameTool {
    public final Level level;
    public GameMessage tooltip;
    public boolean displayErrorTip;
    protected TilePosition lastHoverPos;
    public Rectangle lastHoverBounds;
    protected GameMessage lastHoverErr;
    protected HudDrawElement hudElement;

    public SelectTileGameTool(Level level, GameMessage tooltip, boolean displayErrorTip) {
        this.level = level;
        this.tooltip = tooltip;
        this.displayErrorTip = displayErrorTip;
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                final DrawOptionsList drawOptions = new DrawOptionsList();
                TilePosition lastHoverPos = SelectTileGameTool.this.lastHoverPos;
                if (lastHoverPos != null) {
                    Color color = SelectTileGameTool.this.lastHoverErr == null ? new Color(50, 200, 50) : new Color(200, 50, 50);
                    Rectangle lastHoverBounds = SelectTileGameTool.this.lastHoverBounds;
                    if (lastHoverBounds != null) {
                        drawOptions.add(HUD.tileBoundOptions(camera, color, false, lastHoverBounds));
                        DrawOptions icon = SelectTileGameTool.this.getIconTexture(color, camera.getDrawX((int)(lastHoverBounds.getCenterX() * 32.0)), camera.getDrawY((int)(lastHoverBounds.getCenterY() * 32.0)));
                        if (icon != null) {
                            drawOptions.add(icon);
                        }
                    } else {
                        drawOptions.add(HUD.tileBoundOptions(camera, color, false, lastHoverPos.tileX, lastHoverPos.tileY, lastHoverPos.tileX, lastHoverPos.tileY));
                        DrawOptions icon = SelectTileGameTool.this.getIconTexture(color, camera.getTileDrawX(lastHoverPos.tileX) + 16, camera.getTileDrawY(lastHoverPos.tileY) + 16);
                        if (icon != null) {
                            drawOptions.add(icon);
                        }
                    }
                }
                if (!drawOptions.isEmpty()) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
            }
        };
        level.hudManager.addElement(this.hudElement);
    }

    public SelectTileGameTool(Level level, GameMessage tooltip) {
        this(level, tooltip, true);
    }

    public abstract DrawOptions getIconTexture(Color var1, int var2, int var3);

    @Override
    public boolean inputEvent(InputEvent event) {
        State currentState = GlobalData.getCurrentState();
        if (currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(event)) {
            return false;
        }
        if (event.isMouseMoveEvent()) {
            int tileX = currentState.getCamera().getMouseLevelTilePosX(event);
            int tileY = currentState.getCamera().getMouseLevelTilePosY(event);
            this.lastHoverPos = new TilePosition(this.level, tileX, tileY);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            return true;
        }
        if (event.getID() == -100 && event.state) {
            int tileX = currentState.getCamera().getMouseLevelTilePosX(event);
            int tileY = currentState.getCamera().getMouseLevelTilePosY(event);
            this.lastHoverPos = new TilePosition(this.level, tileX, tileY);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            if (this.lastHoverErr == null) {
                boolean out = this.onSelected(event, this.lastHoverPos);
                if (out) {
                    this.hudElement.remove();
                    GameToolManager.clearGameTool(this);
                    SoundManager.playSound(GameResources.tick, SoundEffect.ui());
                }
                return out;
            }
        } else if (event.getID() == -99 && event.state) {
            GameToolManager.clearGameTool(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        State currentState = GlobalData.getCurrentState();
        if (currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(WindowManager.getWindow().getInput().mousePos())) {
            return false;
        }
        if (event.getState() == ControllerInput.CURSOR || event.getState() == ControllerInput.AIM) {
            int tileX = currentState.getCamera().getMouseLevelTilePosX();
            int tileY = currentState.getCamera().getMouseLevelTilePosY();
            this.lastHoverPos = new TilePosition(this.level, tileX, tileY);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            return true;
        }
        if (event.getState() == ControllerInput.MENU_SELECT && event.buttonState) {
            int tileX = currentState.getCamera().getMouseLevelTilePosX();
            int tileY = currentState.getCamera().getMouseLevelTilePosY();
            this.lastHoverPos = new TilePosition(this.level, tileX, tileY);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            if (this.lastHoverErr == null) {
                boolean out = this.onSelected(InputEvent.ControllerButtonEvent(event, GlobalData.getCurrentGameLoop()), this.lastHoverPos);
                if (out) {
                    this.hudElement.remove();
                    GameToolManager.clearGameTool(this);
                    SoundManager.playSound(GameResources.tick, SoundEffect.ui());
                }
                return out;
            }
        } else if (event.getState() == ControllerInput.MENU_BACK && event.buttonState) {
            GameToolManager.clearGameTool(this);
            return true;
        }
        return false;
    }

    @Override
    public void isCancelled() {
        this.hudElement.remove();
        this.onSelected(null, null);
    }

    @Override
    public void isCleared() {
        this.hudElement.remove();
        this.onSelected(null, null);
    }

    @Override
    public GameTooltips getTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (this.lastHoverErr != null && this.displayErrorTip) {
            tooltips.add(this.lastHoverErr.translate());
        }
        if (this.tooltip != null) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, this.tooltip.translate()));
                tooltips.add(new InputTooltip(ControllerInput.MENU_BACK, Localization.translate("ui", "cancelbutton")));
            } else {
                tooltips.add(new InputTooltip(-100, this.tooltip.translate()));
                tooltips.add(new InputTooltip(-99, Localization.translate("ui", "cancelbutton")));
            }
        }
        return tooltips.size() == 0 ? null : tooltips;
    }

    public abstract boolean onSelected(InputEvent var1, TilePosition var2);

    public abstract GameMessage isValidTile(TilePosition var1);
}

