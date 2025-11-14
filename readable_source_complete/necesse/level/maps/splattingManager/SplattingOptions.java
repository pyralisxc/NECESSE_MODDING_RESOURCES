/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.splattingManager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.layers.BiomeBlendingOptions;

public class SplattingOptions {
    private final Point[][] newTerrainSprites = new Point[][]{null, {new Point(1, 2)}, {new Point(0, 1)}, {new Point(4, 1)}, {new Point(1, 0)}, {new Point(1, 0), new Point(1, 2)}, {new Point(4, 2)}, {new Point(6, 1)}, {new Point(2, 1)}, {new Point(3, 1)}, {new Point(0, 1), new Point(2, 1)}, {new Point(5, 1)}, {new Point(3, 2)}, {new Point(6, 2)}, {new Point(5, 2)}, {new Point(1, 1)}};
    public final SplattingOption[] splats;

    public SplattingOptions(Level level, int tileX, int tileY) {
        GameTile currentTile = level.getTile(tileX, tileY);
        GameTile[] adjTile = level.getAdjacentTiles(tileX, tileY);
        GameObject[] adjObject = level.getAdjacentObjects(tileX, tileY);
        GameTile underLiquidTile = null;
        GameTile[] adjUnderLiquidTiles = null;
        if (currentTile.isLiquid) {
            underLiquidTile = level.getUnderLiquidTile(tileX, tileY);
            adjUnderLiquidTiles = level.getAdjacentUnderLiquidTiles(tileX, tileY);
        }
        boolean[] splatsInto = new boolean[adjTile.length];
        boolean[] underLiquidSplatsInto = new boolean[adjTile.length];
        for (int i = 0; i < splatsInto.length; ++i) {
            splatsInto[i] = this.splatsInto(currentTile, adjTile[i], adjObject[i]);
            underLiquidSplatsInto[i] = underLiquidTile != null && this.splatsInto(underLiquidTile, adjUnderLiquidTiles[i], adjObject[i]);
        }
        boolean[] newSplatsInto = new boolean[splatsInto.length];
        boolean[] newUnderLiquidSplatsInto = new boolean[splatsInto.length];
        boolean[] oldSplatsInto = new boolean[splatsInto.length];
        for (int i = 0; i < splatsInto.length; ++i) {
            GameTile tile;
            if (splatsInto[i]) {
                tile = adjTile[i];
                if (tile != null && tile.isLiquid) {
                    if (((LiquidTile)tile).isUsingNewTerrainSplatting(level, tileX, tileY)) {
                        newSplatsInto[i] = true;
                    }
                } else if (tile != null && ((TerrainSplatterTile)tile).isUsingNewTerrainSplatting()) {
                    newSplatsInto[i] = true;
                }
            } else if (underLiquidSplatsInto[i] && (tile = adjUnderLiquidTiles[i]) != null && ((TerrainSplatterTile)tile).isUsingNewTerrainSplatting()) {
                newUnderLiquidSplatsInto[i] = true;
            }
            oldSplatsInto[i] = splatsInto[i] && !newSplatsInto[i];
        }
        ArrayList<SplattingOption> underLiquidSplats = new ArrayList<SplattingOption>();
        if (underLiquidTile != null) {
            this.generateNewSplattingOptions(underLiquidTile, adjUnderLiquidTiles, adjObject, newUnderLiquidSplatsInto, underLiquidSplats);
            underLiquidSplats.sort(null);
        }
        ArrayList<SplattingOption> standardSplats = new ArrayList<SplattingOption>();
        this.generateNewSplattingOptions(currentTile, adjTile, adjObject, newSplatsInto, standardSplats);
        this.generateSplattingTopLeft(adjTile, adjObject, oldSplatsInto, standardSplats);
        this.generateSplattingTopRight(adjTile, adjObject, oldSplatsInto, standardSplats);
        this.generateSplattingBotRight(adjTile, adjObject, oldSplatsInto, standardSplats);
        this.generateSplattingBotLeft(adjTile, adjObject, oldSplatsInto, standardSplats);
        standardSplats.sort(null);
        underLiquidSplats.addAll(standardSplats);
        this.splats = underLiquidSplats.isEmpty() ? null : underLiquidSplats.toArray(new SplattingOption[0]);
    }

    public boolean isNull() {
        return this.splats == null;
    }

    public void addTileDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, TerrainSplatterTile tile, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        Performance.record((PerformanceTimerManager)tickManager, "drawSplatSetup", () -> {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameTextureSection terrainTexture = tile.getTerrainTexture(level, tileX, tileY);
            underLiquidList.add(terrainTexture).pos(drawX, drawY);
            this.addSplatDrawOptions(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, this.splats, level, tileX, tileY, drawX, drawY, camera, tickManager);
        });
    }

    public void addLiquidDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, LiquidTile tile, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        boolean renderedFull = false;
        for (SplattingOption option : this.splats) {
            if (option.isLiquid && option.tile < tile.getID() && !renderedFull) {
                tile.addFullDrawables(liquidList, level, tileX, tileY, drawX, drawY);
                renderedFull = true;
            }
            option.addDrawOptions(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, drawX, drawY, camera, tickManager);
        }
        if (!renderedFull) {
            tile.addFullDrawables(liquidList, level, tileX, tileY, drawX, drawY);
        }
    }

    public void addSplatDrawOptions(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, SplattingOption[] options, Level level, int tileX, int tileY, int drawX, int drawY, GameCamera camera, TickManager tickManager) {
        for (SplattingOption option : options) {
            option.addDrawOptions(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, drawX, drawY, camera, tickManager);
        }
    }

    private static int getMarchValue(boolean topLeft, boolean topRight, boolean botRight, boolean botLeft) {
        int out = 0;
        if (topLeft) {
            out = GameMath.setBit(out, 0, true);
        }
        if (topRight) {
            out = GameMath.setBit(out, 1, true);
        }
        if (botRight) {
            out = GameMath.setBit(out, 2, true);
        }
        if (botLeft) {
            out = GameMath.setBit(out, 3, true);
        }
        return out;
    }

    private static String getMarchValueDebugText(int marchValue) {
        return (GameMath.getBit(marchValue, 0) ? "1" : "0") + (GameMath.getBit(marchValue, 1) ? "1" : "0") + (GameMath.getBit(marchValue, 2) ? "1" : "0") + (GameMath.getBit(marchValue, 3) ? "1" : "0");
    }

    private void generateNewSplattingOptions(GameTile currentTile, GameTile[] tiles, GameObject[] objects, boolean[] splats, ArrayList<SplattingOption> options) {
        HashSet<Integer> tileIDsHandled = new HashSet<Integer>();
        if (splats[1]) {
            this.addNewMarchSplattingOptions(tiles, splats, tiles[1], tileIDsHandled, options);
        }
        if (splats[3]) {
            this.addNewMarchSplattingOptions(tiles, splats, tiles[3], tileIDsHandled, options);
        }
        if (splats[4]) {
            this.addNewMarchSplattingOptions(tiles, splats, tiles[4], tileIDsHandled, options);
        }
        if (splats[6]) {
            this.addNewMarchSplattingOptions(tiles, splats, tiles[6], tileIDsHandled, options);
        }
        if (splats[0] && tiles[1] != tiles[0] && tiles[3] != tiles[0]) {
            options.add(this.getNewSplattingOption(tiles[0], 2, 2, 0, 0));
        }
        if (splats[2] && tiles[1] != tiles[2] && tiles[4] != tiles[2]) {
            options.add(this.getNewSplattingOption(tiles[2], 0, 2, 0, 0));
        }
        if (splats[5] && tiles[3] != tiles[5] && tiles[6] != tiles[5]) {
            options.add(this.getNewSplattingOption(tiles[5], 2, 0, 0, 0));
        }
        if (splats[7] && tiles[4] != tiles[7] && tiles[6] != tiles[7]) {
            options.add(this.getNewSplattingOption(tiles[7], 0, 0, 0, 0));
        }
    }

    private void addNewMarchSplattingOptions(GameTile[] tiles, boolean[] splats, GameTile tile, HashSet<Integer> tileIDsHandled, ArrayList<SplattingOption> options) {
        if (tileIDsHandled.contains(tile.getID())) {
            return;
        }
        int marchValue = SplattingOptions.getMarchValue(splats[1] && tiles[1] == tile, splats[4] && tiles[4] == tile, splats[6] && tiles[6] == tile, splats[3] && tiles[3] == tile);
        Point[] newTerrainSplatTiles = this.newTerrainSprites[marchValue];
        if (newTerrainSplatTiles != null) {
            for (Point sprite : newTerrainSplatTiles) {
                options.add(this.getNewSplattingOption(tile, sprite.x, sprite.y, 0, 0));
            }
        }
        tileIDsHandled.add(tile.getID());
    }

    private SplattingOption getNewSplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
        if (tile.isLiquid) {
            return new NewLiquidSplattingOption(tile, spriteX, spriteY, drawXOffset, drawYOffset);
        }
        return new NewTerrainSplattingOption(tile, spriteX, spriteY, drawXOffset, drawYOffset);
    }

    private void generateSplattingTopLeft(GameTile[] tiles, GameObject[] objects, boolean[] splats, ArrayList<SplattingOption> options) {
        int left = 3;
        int topLeft = 0;
        int top = 1;
        if (splats[left]) {
            if (this.sameSplat(tiles, objects, left, top)) {
                this.addOption(options, tiles[left], 2, 2, 0, 0);
            } else {
                this.addOption(options, tiles[left], 0, 2, 0, 0);
            }
        }
        if (splats[top] && !this.sameSplat(tiles, objects, top, left)) {
            this.addOption(options, tiles[top], 2, 0, 0, 0);
        }
        if (splats[topLeft] && !this.sameSplat(tiles, objects, topLeft, top) && !this.sameSplat(tiles, objects, topLeft, left)) {
            this.addOption(options, tiles[topLeft], 0, 0, 0, 0);
        }
    }

    private void generateSplattingTopRight(GameTile[] tiles, GameObject[] objects, boolean[] splats, ArrayList<SplattingOption> options) {
        int top = 1;
        int topRight = 2;
        int right = 4;
        if (splats[top]) {
            if (this.sameSplat(tiles, objects, top, right)) {
                this.addOption(options, tiles[top], 3, 2, 16, 0);
            } else {
                this.addOption(options, tiles[top], 3, 0, 16, 0);
            }
        }
        if (splats[right] && !this.sameSplat(tiles, objects, right, top)) {
            this.addOption(options, tiles[right], 1, 2, 16, 0);
        }
        if (splats[topRight] && !this.sameSplat(tiles, objects, topRight, top) && !this.sameSplat(tiles, objects, topRight, right)) {
            this.addOption(options, tiles[topRight], 1, 0, 16, 0);
        }
    }

    private void generateSplattingBotRight(GameTile[] tiles, GameObject[] objects, boolean[] splats, ArrayList<SplattingOption> options) {
        int right = 4;
        int botRight = 7;
        int bot = 6;
        if (splats[right]) {
            if (this.sameSplat(tiles, objects, right, bot)) {
                this.addOption(options, tiles[right], 3, 3, 16, 16);
            } else {
                this.addOption(options, tiles[right], 1, 3, 16, 16);
            }
        }
        if (splats[bot] && !this.sameSplat(tiles, objects, bot, right)) {
            this.addOption(options, tiles[bot], 3, 1, 16, 16);
        }
        if (splats[botRight] && !this.sameSplat(tiles, objects, botRight, bot) && !this.sameSplat(tiles, objects, botRight, right)) {
            this.addOption(options, tiles[botRight], 1, 1, 16, 16);
        }
    }

    private void generateSplattingBotLeft(GameTile[] tiles, GameObject[] objects, boolean[] splats, ArrayList<SplattingOption> options) {
        int bot = 6;
        int botLeft = 5;
        int left = 3;
        if (splats[bot]) {
            if (this.sameSplat(tiles, objects, bot, left)) {
                this.addOption(options, tiles[bot], 2, 3, 0, 16);
            } else {
                this.addOption(options, tiles[bot], 2, 1, 0, 16);
            }
        }
        if (splats[left] && !this.sameSplat(tiles, objects, left, bot)) {
            this.addOption(options, tiles[left], 0, 3, 0, 16);
        }
        if (splats[botLeft] && !this.sameSplat(tiles, objects, botLeft, left) && !this.sameSplat(tiles, objects, botLeft, bot)) {
            this.addOption(options, tiles[botLeft], 0, 1, 0, 16);
        }
    }

    private boolean sameSplat(GameTile[] tiles, GameObject[] objects, int index1, int index2) {
        if (tiles[index1] == tiles[index2]) {
            return !objects[index1].stopsTerrainSplatting() && !objects[index2].stopsTerrainSplatting();
        }
        return false;
    }

    private boolean splatsInto(GameTile current, GameTile adjacent, GameObject adjacentObject) {
        if (current == adjacent || adjacentObject.stopsTerrainSplatting()) {
            return false;
        }
        if (current.isLiquid && adjacent.isLiquid) {
            return true;
        }
        if (current.isLiquid && adjacent.terrainSplatting) {
            return true;
        }
        if (!current.terrainSplatting) {
            return false;
        }
        if (adjacent.isLiquid) {
            return true;
        }
        if (adjacent.terrainSplatting) {
            return TerrainSplatterTile.comparePriority((TerrainSplatterTile)current, (TerrainSplatterTile)adjacent) < 0;
        }
        return false;
    }

    public void addDebugTooltips(StringTooltips tooltips) {
        tooltips.add("Splats: " + this.getDebugTooltips(this.splats));
    }

    private String getDebugTooltips(SplattingOption[] options) {
        if (options == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < options.length; ++i) {
            out.append(options[i].getDebugInfo());
            if (i >= options.length - 1) continue;
            out.append(", ");
        }
        return out.toString();
    }

    private void addOption(ArrayList<SplattingOption> list, GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
        if (tile.isLiquid) {
            list.add(new LiquidSplattingOption(tile, spriteX, spriteY, drawXOffset, drawYOffset));
        } else if (tile.terrainSplatting) {
            list.add(new TerrainSplattingOption(tile, spriteX, spriteY, drawXOffset, drawYOffset));
        }
    }

    private static abstract class SplattingOption
    implements Comparable<SplattingOption> {
        public final short tile;
        public final boolean isLiquid;
        public final byte spriteX;
        public final byte spriteY;
        public final byte drawXOffset;
        public final byte drawYOffset;

        public SplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
            this.tile = (short)tile.getID();
            this.isLiquid = tile.isLiquid;
            this.spriteX = (byte)spriteX;
            this.spriteY = (byte)spriteY;
            this.drawXOffset = (byte)drawXOffset;
            this.drawYOffset = (byte)drawYOffset;
        }

        public abstract void addDrawOptions(LevelTileTerrainDrawOptions var1, LevelTileLiquidDrawOptions var2, LevelTileTerrainDrawOptions var3, OrderableDrawables var4, List<LevelSortedDrawable> var5, Level var6, int var7, int var8, int var9, int var10, GameCamera var11, TickManager var12);

        public String getDebugInfo() {
            return TileRegistry.getTile(this.tile).getStringID() + "(" + this.spriteX + "," + this.spriteY + ")";
        }
    }

    private static class NewLiquidSplattingOption
    extends SplattingOption {
        public NewLiquidSplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
            super(tile, spriteX, spriteY, drawXOffset, drawYOffset);
        }

        @Override
        public void addDrawOptions(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, int drawX, int drawY, GameCamera camera, TickManager tickManager) {
            Performance.record((PerformanceTimerManager)tickManager, "terrainDrawSetup", () -> {
                LiquidTile liquidTile = (LiquidTile)TileRegistry.getTile(this.tile);
                Biome baseBiome = level.getBiome(tileX, tileY);
                BiomeBlendingOptions.BiomeBlendingOption[] blendOptions = level.biomeBlendingManager.getBlendOptions(tileX, tileY);
                if (blendOptions == null) {
                    liquidTile.addFullDrawables(liquidList, level, tileX, tileY, this.spriteX, this.spriteY, baseBiome, drawX + this.drawXOffset, drawY + this.drawYOffset);
                } else {
                    for (BiomeBlendingOptions.BiomeBlendingOption blendOption : blendOptions) {
                        if (blendOption.biomeID == baseBiome.getID()) {
                            liquidTile.addFullDrawables(liquidList, level, tileX, tileY, this.spriteX, this.spriteY, baseBiome, drawX + this.drawXOffset, drawY + this.drawYOffset);
                            continue;
                        }
                        liquidTile.addFullDrawables(liquidList, level, tileX, tileY, this.spriteX, this.spriteY, BiomeRegistry.getBiome(blendOption.biomeID), blendOption, drawX + this.drawXOffset, drawY + this.drawYOffset);
                    }
                }
            });
        }

        @Override
        public int compareTo(SplattingOption o) {
            return Integer.compare(o.tile, this.tile);
        }
    }

    private static class NewTerrainSplattingOption
    extends SplattingOption {
        public NewTerrainSplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
            super(tile, spriteX, spriteY, drawXOffset, drawYOffset);
        }

        @Override
        public void addDrawOptions(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, int drawX, int drawY, GameCamera camera, TickManager tickManager) {
            ((TerrainSplatterTile)TileRegistry.getTile(this.tile)).addSplatDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager, this.spriteX, this.spriteY, drawX + this.drawXOffset, drawY + this.drawYOffset);
        }

        @Override
        public int compareTo(SplattingOption o) {
            GameTile me = TileRegistry.getTile(this.tile);
            GameTile him = TileRegistry.getTile(o.tile);
            if (him.isLiquid) {
                return -1;
            }
            return TerrainSplatterTile.comparePriority((TerrainSplatterTile)me, (TerrainSplatterTile)him);
        }
    }

    private static class LiquidSplattingOption
    extends SplattingOption {
        public LiquidSplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
            super(tile, spriteX, spriteY, drawXOffset, drawYOffset);
        }

        @Override
        public void addDrawOptions(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, int drawX, int drawY, GameCamera camera, TickManager tickManager) {
            Performance.record((PerformanceTimerManager)tickManager, "shoreDrawSetup", () -> {
                LiquidTile liquidTile = (LiquidTile)TileRegistry.getTile(this.tile);
                underLiquidList.add(liquidTile.getShoreTexture().sprite(this.spriteX, this.spriteY, 16)).color(liquidTile.getLiquidColor(level, tileX, tileY)).pos(drawX + this.drawXOffset, drawY + this.drawYOffset);
            });
        }

        @Override
        public int compareTo(SplattingOption o) {
            return 1;
        }
    }

    private static class TerrainSplattingOption
    extends SplattingOption {
        public TerrainSplattingOption(GameTile tile, int spriteX, int spriteY, int drawXOffset, int drawYOffset) {
            super(tile, spriteX, spriteY, drawXOffset, drawYOffset);
        }

        @Override
        public void addDrawOptions(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, int drawX, int drawY, GameCamera camera, TickManager tickManager) {
            Performance.record((PerformanceTimerManager)tickManager, "terrainDrawSetup", () -> underLiquidList.add(((TerrainSplatterTile)TileRegistry.getTile(this.tile)).getSplattingTexture(level, tileX, tileY).sprite(this.spriteX, this.spriteY, 16)).pos(drawX + this.drawXOffset, drawY + this.drawYOffset));
        }

        @Override
        public int compareTo(SplattingOption o) {
            GameTile me = TileRegistry.getTile(this.tile);
            GameTile him = TileRegistry.getTile(o.tile);
            if (him.isLiquid) {
                return -1;
            }
            return TerrainSplatterTile.comparePriority((TerrainSplatterTile)me, (TerrainSplatterTile)him);
        }
    }
}

