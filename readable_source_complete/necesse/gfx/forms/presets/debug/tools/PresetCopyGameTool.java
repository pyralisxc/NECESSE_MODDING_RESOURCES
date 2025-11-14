/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Dimension;
import necesse.engine.GlobalData;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.State;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.presets.PresetPreviewForm;
import necesse.gfx.forms.presets.debug.tools.LevelSelectTilesGameTool;
import necesse.gfx.forms.presets.debug.tools.PresetPasteGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetCopyFilter;

public class PresetCopyGameTool
extends LevelSelectTilesGameTool {
    protected Object caller;
    protected final Client client;
    protected boolean openEditor;

    public PresetCopyGameTool(Object caller, Client client, boolean openEditor) {
        super(-100, ControllerInput.MENU_NEXT);
        this.caller = caller;
        this.client = client;
        this.openEditor = openEditor;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.getPlayer().isInventoryExtended()) {
            GameToolManager.clearGameTool(this);
        }
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        boolean superSuccess = super.inputEvent(event);
        if (superSuccess) {
            return superSuccess;
        }
        if (event.getID() == -99) {
            if (this.isSelecting()) {
                this.clearSelecting();
            } else {
                GameToolManager.clearGameTool(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        boolean superSuccess = super.controllerEvent(event);
        if (superSuccess) {
            return superSuccess;
        }
        if (event.getState() == ControllerInput.MENU_PREV && this.isSelecting()) {
            this.clearSelecting();
            return true;
        }
        return false;
    }

    @Override
    public void onTileSelection(int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
        Level level = this.client.getLevel();
        int width = tileEndX - tileStartX + 1;
        int height = tileEndY - tileStartY + 1;
        Preset preset = new Preset(width, height);
        PresetCopyFilter filter = new PresetCopyFilter();
        filter.acceptObjectEntities = false;
        preset.copyFromLevel(level, tileStartX, tileStartY, filter);
        preset.replaceTile(TileRegistry.emptyID, -1);
        preset.replaceTile(TileRegistry.snowRockID, -1);
        preset.replaceObject(ObjectRegistry.getObjectID("rock"), -1);
        if (this.openEditor) {
            State currentState = GlobalData.getCurrentState();
            FormManager formManager = currentState.getFormManager();
            if (formManager instanceof ContinueComponentManager) {
                ContinueComponentManager manager = (ContinueComponentManager)((Object)formManager);
                int hudHeight = WindowManager.getWindow().getHudHeight();
                int maxPreviewHeight = Math.max(40, Math.min(500, hudHeight - 300));
                manager.addContinueForm("presetPreview", new PresetPreviewForm(this.client, 800, maxPreviewHeight, level, preset, submissionForm -> manager.addContinueForm("presetSubmission", (ContinueComponent)submissionForm), newPreset -> GameToolManager.setGameTool(new PresetPasteGameTool(this.client, (Preset)newPreset), this.client)));
            }
            GameToolManager.clearGameTool(this);
        } else {
            WindowManager.getWindow().putClipboardDefault(preset.getCompressedBase64Script());
            GameToolManager.clearGameTool(this);
            if (this.caller != null) {
                GameToolManager.setGameTool(new PresetPasteGameTool(this.client, preset), this.caller);
            } else {
                GameToolManager.setGameTool(new PresetPasteGameTool(this.client, preset));
            }
        }
    }

    @Override
    public void drawTileSelection(GameCamera camera, PlayerMob perspective, int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
        int drawX = camera.getTileDrawX(tileStartX);
        int drawY = camera.getTileDrawY(tileStartY);
        int tileWidth = tileEndX - tileStartX + 1;
        int tileHeight = tileEndY - tileStartY + 1;
        Renderer.initQuadDraw(tileWidth * 32, tileHeight * 32).color(1.0f, 1.0f, 0.0f, 0.2f).draw(drawX, drawY);
        HUD.tileBoundOptions(camera, tileStartX, tileStartY, tileEndX, tileEndY).draw();
        FontOptions fontOptions = new FontOptions(20).outline();
        String widthString = "" + tileWidth;
        int widthStringWidth = FontManager.bit.getWidthCeil(widthString, fontOptions);
        FontManager.bit.drawString(drawX + tileWidth * 32 / 2 - widthStringWidth / 2, drawY, widthString, fontOptions);
        String heightString = "" + tileHeight;
        int heightStringHeight = FontManager.bit.getHeightCeil(heightString, fontOptions);
        FontManager.bit.drawString(drawX, drawY + tileHeight * 32 / 2 - heightStringHeight / 2, heightString, fontOptions);
    }

    @Override
    public Dimension getMaxTileSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Level getLevel() {
        return this.client.getLevel();
    }

    @Override
    public GameTooltips getTooltips() {
        if (Input.lastInputIsController) {
            return new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "presetselectarea"));
        }
        return new InputTooltip(-100, Localization.translate("ui", "presetselectarea"));
    }

    @Override
    public boolean shouldShowWires() {
        return true;
    }
}

