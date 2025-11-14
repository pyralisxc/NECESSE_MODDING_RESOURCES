/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
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
import necesse.level.maps.splattingManager.SplattingOptions;

public abstract class TerrainSplatterTile
extends GameTile {
    public static final Point[] NEW_FULL_TILE_SPRITES = new Point[]{new Point(3, 0), new Point(4, 0), new Point(5, 0), new Point(6, 0)};
    public static final int PRIORITY_TERRAIN_BOT = 0;
    public static final int PRIORITY_TERRAIN = 100;
    public static final int PRIORITY_TERRAIN_TOP = 200;
    public static final int PRIORITY_FLOOR_BOT = 300;
    public static final int PRIORITY_FLOOR = 400;
    public static final int PRIORITY_FLOOR_TOP = 500;
    protected String alphaMaskTextureName;
    protected String terrainTextureName;
    protected boolean isUsingNewTerrainSplatting;
    public boolean preferLegacySplatting;
    public GameTextureSection[][] splattingTextures;
    public GameTextureSection terrainTexture;
    private final GameRandom spriteRandom;

    public TerrainSplatterTile(boolean isFloor, String terrainTextureName, String alphaMaskTextureName) {
        super(isFloor);
        this.terrainTextureName = terrainTextureName;
        this.alphaMaskTextureName = alphaMaskTextureName;
        this.spriteRandom = new GameRandom();
    }

    public TerrainSplatterTile(boolean isFloor, String terrainTextureName) {
        this(isFloor, terrainTextureName, "splattingmask");
    }

    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        return new Point(0, 0);
    }

    public abstract int getTerrainPriority();

    public static int comparePriority(TerrainSplatterTile tile1, TerrainSplatterTile tile2) {
        int pri2;
        int pri1 = tile1.getTerrainPriority();
        int compare = Integer.compare(pri1, pri2 = tile2.getTerrainPriority());
        return compare != 0 ? compare : Integer.compare(tile1.getID(), tile2.getID());
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture itemTexture;
        Point tile = new Point(0, 0);
        try {
            itemTexture = GameTexture.fromFileRaw("tiles/" + this.terrainTextureName + "_splat", true);
            tile = new Point(3, 0);
        }
        catch (FileNotFoundException e) {
            itemTexture = GameTexture.fromFile("tiles/" + this.terrainTextureName, true);
        }
        GameTexture itemMask = GameTexture.fromFile("tiles/itemmask", true);
        GameTexture generatedTexture = new GameTexture("tiles/" + this.terrainTextureName + " item", 32, 32);
        generatedTexture.copy(itemTexture, 0, 0, tile.x * 32, tile.y * 32, 32, 32);
        generatedTexture.merge(itemMask, 0, 0, MergeFunction.MULTIPLY);
        generatedTexture.makeFinal();
        itemTexture.makeFinal();
        return generatedTexture;
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.generateSplattingTextures();
    }

    public boolean isUsingNewTerrainSplatting() {
        return this.isUsingNewTerrainSplatting;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameTextureSection getSplattingTexture(Level level, int tileX, int tileY) {
        if (this.isUsingNewTerrainSplatting) {
            int section;
            int frames = this.terrainTexture.getWidth() / 224;
            int frame = 0;
            if (frames > 1) {
                int animSpeed = frames * 400;
                frame = GameUtils.getAnim(level.getLocalTime(), frames * 2 - 2, animSpeed);
                if (frame >= frames) {
                    frame = frames * 2 - frame - 2;
                }
            }
            GameRandom gameRandom = this.spriteRandom;
            synchronized (gameRandom) {
                section = this.spriteRandom.seeded(TerrainSplatterTile.getTileSeed(tileX, tileY, 5)).nextInt(this.terrainTexture.getHeight() / 96);
            }
            return this.terrainTexture.sprite(frame, section, 224, 96);
        }
        Point sprite = this.getTerrainSprite(this.terrainTexture, level, tileX, tileY);
        if (this.splattingTextures == null) {
            return null;
        }
        return this.splattingTextures[sprite.x][sprite.y];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameTextureSection getTerrainTexture(Level level, int tileX, int tileY) {
        if (this.isUsingNewTerrainSplatting) {
            int tile;
            GameRandom gameRandom = this.spriteRandom;
            synchronized (gameRandom) {
                tile = this.spriteRandom.seeded(TerrainSplatterTile.getTileSeed(tileX, tileY, 9)).nextInt(NEW_FULL_TILE_SPRITES.length);
            }
            Point sprite = NEW_FULL_TILE_SPRITES[tile];
            return this.getSplattingTexture(level, tileX, tileY).sprite(sprite.x, sprite.y, 32);
        }
        Point sprite = this.getTerrainSprite(this.terrainTexture, level, tileX, tileY);
        return this.terrainTexture.sprite(sprite.x, sprite.y, 32);
    }

    public void generateSplattingTextures() {
        GameTexture terrain;
        if (this.preferLegacySplatting) {
            try {
                terrain = GameTexture.fromFileRaw("tiles/" + this.terrainTextureName, true);
                this.generateOldTerrainSplatting(terrain);
            }
            catch (FileNotFoundException e) {
                try {
                    terrain = GameTexture.fromFileRaw("tiles/" + this.terrainTextureName + "_splat");
                    this.terrainTexture = tileTextures.addTexture(terrain);
                    this.isUsingNewTerrainSplatting = true;
                }
                catch (FileNotFoundException e2) {
                    terrain = GameTexture.fromFile("tiles/" + this.terrainTextureName, true);
                    this.generateOldTerrainSplatting(terrain);
                }
            }
        } else {
            try {
                terrain = GameTexture.fromFileRaw("tiles/" + this.terrainTextureName + "_splat");
                this.terrainTexture = tileTextures.addTexture(terrain);
                this.isUsingNewTerrainSplatting = true;
            }
            catch (FileNotFoundException e) {
                terrain = GameTexture.fromFile("tiles/" + this.terrainTextureName, true);
                this.generateOldTerrainSplatting(terrain);
            }
        }
        terrain.makeFinal();
    }

    private void generateOldTerrainSplatting(GameTexture texture) {
        this.terrainTexture = tileTextures.addTexture(texture);
        GameTexture mask = GameTexture.fromFile("tiles/" + this.alphaMaskTextureName, true);
        if (mask.getWidth() != mask.getHeight()) {
            throw new IllegalStateException("Terrain splatting alpha mask must have same width and height");
        }
        this.splattingTextures = new GameTextureSection[texture.getWidth() / 32][texture.getHeight() / 32];
        for (int i = 0; i < texture.getWidth() / 32; ++i) {
            for (int j = 0; j < texture.getHeight() / 32; ++j) {
                GameTexture splatting = new GameTexture("tiles/" + this.terrainTextureName + " splat" + i + "x" + j, mask.getWidth(), mask.getHeight());
                for (int x = 0; x < mask.getWidth() / 32; ++x) {
                    for (int y = 0; y < mask.getHeight() / 32; ++y) {
                        splatting.mergeSprite(texture, i, j, 32, x * 32, y * 32);
                    }
                }
                splatting.merge(mask, 0, 0, MergeFunction.MULTIPLY);
                splatting.makeFinal();
                this.splattingTextures[i][j] = tileTextures.addTexture(splatting);
            }
        }
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        this.addTerrainDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.getTerrainTexture(level, tileX, tileY).initDraw().alpha(alpha).draw(drawX, drawY);
    }

    public void addTerrainDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        SplattingOptions splat = level.regionManager.getSplatTiles(tileX, tileY);
        if (splat != null) {
            splat.addTileDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, this, level, tileX, tileY, camera, tickManager);
        } else {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameTextureSection terrainTexture = this.getTerrainTexture(level, tileX, tileY);
            underLiquidList.add(terrainTexture).pos(drawX, drawY);
        }
    }

    public void addSplatDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager, int spriteX, int spriteY, int drawX, int drawY) {
        Performance.record((PerformanceTimerManager)tickManager, "terrainDrawSetup", () -> underLiquidList.add(this.getSplattingTexture(level, tileX, tileY).sprite(spriteX, spriteY, 32)).pos(drawX, drawY));
    }
}

