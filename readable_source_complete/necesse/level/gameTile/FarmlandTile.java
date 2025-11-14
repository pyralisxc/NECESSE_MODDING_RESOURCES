/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FarmlandTile
extends GameTile {
    public GameTextureSection texture;

    public FarmlandTile() {
        super(false);
        this.mapColor = new Color(99, 77, 44);
        this.canBeMined = true;
        this.smartMinePriority = true;
        this.tileHealth = 50;
        this.isOrganic = true;
        this.setItemCategory("tiles", "terrain");
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.texture = tileTextures.addTexture(GameTexture.fromFile("tiles/farmland"));
    }

    protected GameTextureSection getTopLeftDrawOptions(GameTextureSection texture, boolean[] adj) {
        int topLeft = 0;
        int top = 1;
        int left = 3;
        if (adj[left]) {
            if (adj[top]) {
                if (adj[topLeft]) {
                    return texture.sprite(2, 4, 16);
                }
                return texture.sprite(3, 1, 16);
            }
            return texture.sprite(2, 4, 16);
        }
        if (adj[top]) {
            return texture.sprite(0, 2, 16);
        }
        return texture.sprite(0, 2, 16);
    }

    protected GameTextureSection getTopRightDrawOptions(GameTextureSection texture, boolean[] adj) {
        int topRight = 2;
        int top = 1;
        int right = 4;
        if (adj[right]) {
            if (adj[top]) {
                if (adj[topRight]) {
                    return texture.sprite(3, 4, 16);
                }
                return texture.sprite(2, 1, 16);
            }
            return texture.sprite(3, 4, 16);
        }
        if (adj[top]) {
            return texture.sprite(1, 2, 16);
        }
        return texture.sprite(1, 2, 16);
    }

    protected GameTextureSection getBotLeftDrawOptions(GameTextureSection texture, boolean[] adj) {
        int left = 3;
        int botLeft = 5;
        int bot = 6;
        if (adj[left]) {
            if (adj[bot]) {
                if (adj[botLeft]) {
                    return texture.sprite(2, 5, 16);
                }
                return texture.sprite(3, 0, 16);
            }
            return texture.sprite(2, 3, 16);
        }
        if (adj[bot]) {
            return texture.sprite(0, 1, 16);
        }
        return texture.sprite(0, 3, 16);
    }

    protected GameTextureSection getBotRightDrawOptions(GameTextureSection texture, boolean[] adj) {
        int right = 4;
        int bot = 6;
        int botRight = 7;
        if (adj[right]) {
            if (adj[bot]) {
                if (adj[botRight]) {
                    return texture.sprite(3, 5, 16);
                }
                return texture.sprite(2, 0, 16);
            }
            return texture.sprite(3, 3, 16);
        }
        if (adj[bot]) {
            return texture.sprite(1, 1, 16);
        }
        return texture.sprite(1, 3, 16);
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, final int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int myID = this.getID();
        Integer[] adj = level.getAdjacentTilesInt(tileX, tileY);
        boolean isSameTile = true;
        boolean[] sameTile = new boolean[adj.length];
        for (int i = 0; i < adj.length; ++i) {
            sameTile[i] = adj[i] == myID;
            isSameTile = isSameTile && sameTile[i];
        }
        if (isSameTile) {
            underLiquidList.add(this.texture.sprite(1, 2, 32)).pos(drawX, drawY);
        } else {
            underLiquidList.add(this.getTopLeftDrawOptions(this.texture, sameTile)).pos(drawX, drawY);
            underLiquidList.add(this.getTopRightDrawOptions(this.texture, sameTile)).pos(drawX + 16, drawY);
            underLiquidList.add(this.getBotLeftDrawOptions(this.texture, sameTile)).pos(drawX, drawY + 16);
            underLiquidList.add(this.getBotRightDrawOptions(this.texture, sameTile)).pos(drawX + 16, drawY + 16);
        }
        if (level.getTileID(tileX, tileY - 1) != myID) {
            GameLight light = level.getLightLevel(tileX, tileY);
            final TextureDrawOptionsEnd option1 = level.getTileID(tileX - 1, tileY) == myID ? this.texture.sprite(2, 2, 16).initDraw().light(light).pos(drawX, drawY - 12) : this.texture.sprite(0, 0, 16).initDraw().light(light).pos(drawX, drawY - 12);
            final TextureDrawOptionsEnd option2 = level.getTileID(tileX + 1, tileY) == myID ? this.texture.sprite(3, 2, 16).initDraw().light(light).pos(drawX + 16, drawY - 12) : this.texture.sprite(1, 0, 16).initDraw().light(light).pos(drawX + 16, drawY - 12);
            sortedList.add(new LevelSortedDrawable(this, tileX){

                @Override
                public int getSortY() {
                    return tileY * 32;
                }

                @Override
                public void draw(TickManager tickManager) {
                    option1.draw();
                    option2.draw();
                }
            });
        }
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int myID = this.getID();
        Integer[] adj = level.getAdjacentTilesInt(tileX, tileY);
        boolean isSameTile = true;
        boolean[] sameTile = new boolean[adj.length];
        for (int i = 0; i < adj.length; ++i) {
            sameTile[i] = adj[i] == myID;
            isSameTile = isSameTile && sameTile[i];
        }
        this.getTopLeftDrawOptions(this.texture, sameTile).initDraw().alpha(alpha).draw(drawX, drawY);
        this.getTopRightDrawOptions(this.texture, sameTile).initDraw().alpha(alpha).draw(drawX + 16, drawY);
        this.getBotLeftDrawOptions(this.texture, sameTile).initDraw().alpha(alpha).draw(drawX, drawY + 16);
        this.getBotRightDrawOptions(this.texture, sameTile).initDraw().alpha(alpha).draw(drawX + 16, drawY + 16);
        if (level.getTileID(tileX, tileY - 1) != myID) {
            this.texture.sprite(0, 0, 16).initDraw().alpha(alpha).draw(drawX, drawY - 12);
            this.texture.sprite(1, 0, 16).initDraw().alpha(alpha).draw(drawX + 16, drawY - 12);
        }
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture itemTexture = GameTexture.fromFile("tiles/farmland", true);
        GameTexture itemMask = GameTexture.fromFile("tiles/itemmask", true);
        GameTexture generatedTexture = new GameTexture("tiles/farmland item", 32, 32);
        generatedTexture.copy(itemTexture, 0, 0, 0, 64, 32, 32);
        generatedTexture.merge(itemMask, 0, 0, MergeFunction.MULTIPLY);
        generatedTexture.makeFinal();
        itemTexture.makeFinal();
        return generatedTexture;
    }

    @Override
    public String canPlace(Level level, int x, int y, boolean byPlayer) {
        String error = super.canPlace(level, x, y, byPlayer);
        if (error != null) {
            return error;
        }
        if (level.isShore(x, y)) {
            return "isshore";
        }
        return null;
    }

    @Override
    public boolean canBePlacedOn(Level level, int tileX, int tileY, GameTile placing) {
        return false;
    }

    @Override
    public boolean isValid(Level level, int x, int y) {
        return !level.isShore(x, y);
    }
}

