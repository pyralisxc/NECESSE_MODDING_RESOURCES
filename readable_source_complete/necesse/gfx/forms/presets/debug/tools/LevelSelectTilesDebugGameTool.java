/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Dimension;
import necesse.engine.input.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.LevelSelectTilesGameTool;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;

public abstract class LevelSelectTilesDebugGameTool
extends MouseDebugGameTool {
    protected final LevelSelectTilesGameTool levelSelectGameTool;

    public LevelSelectTilesDebugGameTool(DebugForm parent, String name) {
        super(parent, name);
        this.onLeftEvent(e -> true, "Select area");
        this.levelSelectGameTool = new LevelSelectTilesGameTool(-100, null){

            @Override
            public Level getLevel() {
                return LevelSelectTilesDebugGameTool.this.getLevel();
            }

            @Override
            public void onTileSelection(int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
                LevelSelectTilesDebugGameTool.this.onTileSelection(tileStartX, tileStartY, tileEndX, tileEndY);
            }

            @Override
            public void drawTileSelection(GameCamera camera, PlayerMob perspective, int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
                LevelSelectTilesDebugGameTool.this.drawTileSelection(camera, perspective, tileStartX, tileStartY, tileEndX, tileEndY);
            }

            @Override
            public Dimension getMaxTileSize() {
                return LevelSelectTilesDebugGameTool.this.getMaxTileSize();
            }

            @Override
            public GameTooltips getTooltips() {
                return LevelSelectTilesDebugGameTool.this.getTooltips();
            }
        };
    }

    @Override
    public void init() {
        this.levelSelectGameTool.init();
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        if (this.levelSelectGameTool.inputEvent(event)) {
            return true;
        }
        return super.inputEvent(event);
    }

    public abstract void onTileSelection(int var1, int var2, int var3, int var4);

    public abstract void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);

    public Dimension getMaxTileSize() {
        return null;
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        this.levelSelectGameTool.isCancelled();
    }

    @Override
    public void isCleared() {
        super.isCleared();
        this.levelSelectGameTool.isCleared();
    }
}

