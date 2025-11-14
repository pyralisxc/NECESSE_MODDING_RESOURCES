/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.util.Arrays;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class EdgedTiledTexture
extends GameTile {
    protected GameTextureSection[][] textures;
    protected String textureName;
    protected int tilesWidth = -1;
    protected int tilesHeight = -1;

    public EdgedTiledTexture(boolean isFloor, String textureName) {
        super(isFloor);
        this.textureName = textureName;
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        GameTexture texture = GameTexture.fromFile("tiles/" + this.textureName);
        int tilesWidth = this.tilesWidth == -1 ? texture.getWidth() / 32 - 2 : this.tilesWidth;
        int tilesHeight = this.tilesHeight == -1 ? texture.getHeight() / 32 : this.tilesHeight;
        this.textures = new GameTextureSection[tilesWidth][tilesHeight];
        for (int tileX = 0; tileX < this.textures.length; ++tileX) {
            for (int tileY = 0; tileY < this.textures[tileX].length; ++tileY) {
                GameTexture tileTexture = new GameTexture("tiles/" + this.textureName + " tile" + tileX + "x" + tileY, 64, 96);
                for (int tileTextureX = 0; tileTextureX < tileTexture.getWidth() / 32; ++tileTextureX) {
                    for (int tileTextureY = 0; tileTextureY < tileTexture.getHeight() / 32; ++tileTextureY) {
                        tileTexture.copy(texture, tileTextureX * 32, tileTextureY * 32, 64 + tileX * 32, tileY * 32, 32, 32);
                    }
                }
                tileTexture.merge(texture, 0, 0, this.getEdgeMergeFunction());
                this.textures[tileX][tileY] = tileTextures.addTexture(tileTexture);
                tileTexture.makeFinal();
            }
        }
        texture.makeFinal();
    }

    protected MergeFunction getEdgeMergeFunction() {
        return MergeFunction.NORMAL;
    }

    protected GameTextureSection getTopLeftDrawOptions(GameTextureSection texture, Boolean[] adj) {
        int topLeft = 0;
        int top = 1;
        int left = 3;
        if (adj[left].booleanValue()) {
            if (adj[top].booleanValue()) {
                if (adj[topLeft].booleanValue()) {
                    return texture.sprite(0, 0, 16);
                }
                return texture.sprite(3, 3, 16);
            }
            return texture.sprite(2, 4, 16);
        }
        if (adj[top].booleanValue()) {
            return texture.sprite(0, 4, 16);
        }
        return texture.sprite(0, 2, 16);
    }

    protected GameTextureSection getTopRightDrawOptions(GameTextureSection texture, Boolean[] adj) {
        int topRight = 2;
        int top = 1;
        int right = 4;
        if (adj[right].booleanValue()) {
            if (adj[top].booleanValue()) {
                if (adj[topRight].booleanValue()) {
                    return texture.sprite(1, 0, 16);
                }
                return texture.sprite(2, 3, 16);
            }
            return texture.sprite(3, 4, 16);
        }
        if (adj[top].booleanValue()) {
            return texture.sprite(1, 4, 16);
        }
        return texture.sprite(1, 2, 16);
    }

    protected GameTextureSection getBotLeftDrawOptions(GameTextureSection texture, Boolean[] adj) {
        int left = 3;
        int botLeft = 5;
        int bot = 6;
        if (adj[left].booleanValue()) {
            if (adj[bot].booleanValue()) {
                if (adj[botLeft].booleanValue()) {
                    return texture.sprite(0, 1, 16);
                }
                return texture.sprite(3, 2, 16);
            }
            return texture.sprite(2, 5, 16);
        }
        if (adj[bot].booleanValue()) {
            return texture.sprite(0, 3, 16);
        }
        return texture.sprite(0, 5, 16);
    }

    protected GameTextureSection getBotRightDrawOptions(GameTextureSection texture, Boolean[] adj) {
        int right = 4;
        int bot = 6;
        int botRight = 7;
        if (adj[right].booleanValue()) {
            if (adj[bot].booleanValue()) {
                if (adj[botRight].booleanValue()) {
                    return texture.sprite(1, 1, 16);
                }
                return texture.sprite(2, 2, 16);
            }
            return texture.sprite(3, 5, 16);
        }
        if (adj[bot].booleanValue()) {
            return texture.sprite(1, 3, 16);
        }
        return texture.sprite(1, 5, 16);
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Boolean[] mergeTile = level.getRelative(tileX, tileY, Level.adjacentGetters, (x, y) -> this.isMergeTile(level, (int)x, (int)y), Boolean[]::new);
        boolean isSameTile = Arrays.stream(mergeTile).allMatch(b -> b);
        int tileSpriteX = Math.floorMod(tileX, this.textures.length);
        int tileSpriteY = Math.floorMod(tileY, this.textures[0].length);
        GameTextureSection texture = this.textures[tileSpriteX][tileSpriteY];
        if (isSameTile) {
            underLiquidList.add(texture.sprite(0, 0, 32)).pos(drawX, drawY);
        } else {
            underLiquidList.add(this.getTopLeftDrawOptions(texture, mergeTile)).pos(drawX, drawY);
            underLiquidList.add(this.getTopRightDrawOptions(texture, mergeTile)).pos(drawX + 16, drawY);
            underLiquidList.add(this.getBotLeftDrawOptions(texture, mergeTile)).pos(drawX, drawY + 16);
            underLiquidList.add(this.getBotRightDrawOptions(texture, mergeTile)).pos(drawX + 16, drawY + 16);
        }
    }

    protected boolean isMergeTile(Level level, int tileX, int tileY) {
        return level.getTileID(tileX, tileY) == this.getID();
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Boolean[] mergeTile = level.getRelative(tileX, tileY, Level.adjacentGetters, (x, y) -> this.isMergeTile(level, (int)x, (int)y), Boolean[]::new);
        boolean isSameTile = Arrays.stream(mergeTile).allMatch(b -> b);
        int tileSpriteX = Math.floorMod(tileX, this.textures.length);
        int tileSpriteY = Math.floorMod(tileY, this.textures[0].length);
        GameTextureSection texture = this.textures[tileSpriteX][tileSpriteY];
        if (isSameTile) {
            texture.sprite(0, 0, 32).initDraw().alpha(alpha).draw(drawX, drawY);
        } else {
            this.getTopLeftDrawOptions(texture, mergeTile).initDraw().alpha(alpha).draw(drawX, drawY);
            this.getTopRightDrawOptions(texture, mergeTile).initDraw().alpha(alpha).draw(drawX + 16, drawY);
            this.getBotLeftDrawOptions(texture, mergeTile).initDraw().alpha(alpha).draw(drawX, drawY + 16);
            this.getBotRightDrawOptions(texture, mergeTile).initDraw().alpha(alpha).draw(drawX + 16, drawY + 16);
        }
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture itemTexture = GameTexture.fromFile("tiles/" + this.textureName, true);
        GameTexture itemMask = GameTexture.fromFile("tiles/itemmask", true);
        GameTexture generatedTexture = new GameTexture("tiles/" + this.textureName + " item", 32, 32);
        generatedTexture.copy(itemTexture, 0, 0, 32, 0, 32, 32);
        generatedTexture.merge(itemMask, 0, 0, MergeFunction.MULTIPLY);
        generatedTexture.makeFinal();
        itemTexture.makeFinal();
        return generatedTexture;
    }
}

