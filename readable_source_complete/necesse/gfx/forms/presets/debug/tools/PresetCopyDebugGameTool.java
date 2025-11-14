/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.GlobalData;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.LevelSelectTilesDebugGameTool;
import necesse.gfx.forms.presets.debug.tools.PresetPasteDebugGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;

public class PresetCopyDebugGameTool
extends LevelSelectTilesDebugGameTool {
    public PresetCopyDebugGameTool(DebugForm parent) {
        super(parent, "Copy preset");
    }

    @Override
    public void init() {
        super.init();
        this.onRightClick(e -> {
            if (this.levelSelectGameTool.isSelecting()) {
                this.levelSelectGameTool.clearSelecting();
            } else {
                GameToolManager.clearGameTool(this);
            }
            return true;
        }, null);
    }

    @Override
    public void onTileSelection(int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
        Level level = this.getServerLevel();
        if (level == null) {
            level = this.getLevel();
        }
        if (level != null) {
            int width = tileEndX - tileStartX + 1;
            int height = tileEndY - tileStartY + 1;
            Preset preset = new Preset(width, height);
            preset.copyFromLevel(level, tileStartX, tileStartY);
            preset.replaceTile(TileRegistry.emptyID, -1);
            preset.replaceTile(TileRegistry.snowRockID, -1);
            preset.replaceTile(TileRegistry.grassID, -1);
            preset.replaceTile(TileRegistry.overgrownGrassID, -1);
            preset.replaceTile(TileRegistry.sandID, -1);
            preset.replaceObject(ObjectRegistry.getObjectID("rock"), -1);
            State currentState = GlobalData.getCurrentState();
            if (currentState instanceof MainGame) {
                MainGame mainGame = (MainGame)currentState;
                mainGame.formManager.addContinueForm("presetPreview", new PresetDebugPreviewForm(this.parent.client, preset, newPreset -> GameToolManager.setGameTool(new PresetPasteDebugGameTool(this.parent), this.parent)));
            }
            GameToolManager.clearGameTool(this);
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
    public boolean shouldShowWires() {
        return true;
    }
}

