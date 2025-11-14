/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class ChromaKeyTile
extends GameTile {
    public ChromaKeyTile(Color color) {
        super(true);
        this.mapColor = color;
        this.tileHealth = 50;
        this.canBeMined = true;
        this.smartMinePriority = true;
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        underLiquidList.add(tileBlankTexture).size(32, 32).color(this.mapColor).pos(drawX, drawY);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("tile", "chromakeytile", "color", "0x" + Integer.toHexString(this.mapColor.getRGB()));
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture itemMask = GameTexture.fromFile("tiles/itemmask", true);
        GameTexture generatedTexture = new GameTexture(this.getStringID(), 32, 32);
        generatedTexture.fill(this.mapColor.getRed(), this.mapColor.getGreen(), this.mapColor.getBlue(), 255);
        generatedTexture.merge(itemMask, 0, 0, MergeFunction.MULTIPLY);
        generatedTexture.makeFinal();
        return generatedTexture;
    }
}

