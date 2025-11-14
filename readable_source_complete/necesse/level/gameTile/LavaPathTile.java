/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.maps.Level;

public class LavaPathTile
extends PathTiledTile {
    private GameTextureSection lightTexture;

    public LavaPathTile() {
        super("lavapath", new Color(177, 46, 3));
        this.lightHue = 0.0f;
        this.lightSat = 0.6f;
        this.lightLevel = 50;
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.lightTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/lavapath_light"));
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        super.addDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager);
        int tileSpriteX = tileX % this.textures.length;
        int tileSpriteY = tileY % this.textures[0].length;
        underLiquidList.add(this.lightTexture.sprite(2 + tileSpriteX, tileSpriteY, 32)).light(level.getLightLevel(tileX, tileY).minLevelCopy(100.0f)).pos(camera.getTileDrawX(tileX), camera.getTileDrawY(tileY));
    }
}

