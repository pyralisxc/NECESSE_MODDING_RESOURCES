/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Dimension;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.tools.LevelSelectGameTool;

public abstract class LevelSelectTilesGameTool
extends LevelSelectGameTool {
    public LevelSelectTilesGameTool(int selectEventID, ControllerButtonState ... selectControllerStates) {
        super(selectEventID, selectControllerStates);
    }

    public LevelSelectTilesGameTool() {
    }

    @Override
    public void onSelection(int startX, int startY, int endX, int endY) {
        int tileStartX = GameMath.getTileCoordinate(startX);
        int tileStartY = GameMath.getTileCoordinate(startY);
        int tileEndX = GameMath.getTileCoordinate(endX);
        int tileEndY = GameMath.getTileCoordinate(endY);
        this.onTileSelection(tileStartX, tileStartY, tileEndX, tileEndY);
    }

    @Override
    public void drawSelection(GameCamera camera, PlayerMob perspective, int startX, int startY, int endX, int endY) {
        int tileStartX = GameMath.getTileCoordinate(startX);
        int tileStartY = GameMath.getTileCoordinate(startY);
        int tileEndX = GameMath.getTileCoordinate(endX);
        int tileEndY = GameMath.getTileCoordinate(endY);
        this.drawTileSelection(camera, perspective, tileStartX, tileStartY, tileEndX, tileEndY);
    }

    @Override
    public Dimension getMaxSize() {
        Dimension maxTileSize = this.getMaxTileSize();
        if (maxTileSize == null) {
            return null;
        }
        return new Dimension(maxTileSize.width * 32 - 32, maxTileSize.height * 32 - 32);
    }

    public abstract void onTileSelection(int var1, int var2, int var3, int var4);

    public abstract void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);

    public Dimension getMaxTileSize() {
        return null;
    }
}

