/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.SnowTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.light.GameLight;

public class SnowPileObject
extends GameObject {
    public GameTexture texture;
    public int number;
    public int nextPile;
    protected final GameRandom drawRandom;
    protected final GameRandom drawRandom1;
    protected final GameRandom drawRandom2;

    public SnowPileObject(int number, int nextPile) {
        super(new Rectangle(0, 0));
        this.number = number;
        this.nextPile = nextPile;
        this.setItemCategory("objects", "landscaping", "snowrocksandores");
        this.setCraftingCategory("objects", "landscaping", "snowrocksandores");
        this.mapColor = new Color(198, 233, 255);
        this.isGrass = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.drawRandom1 = new GameRandom();
        this.drawRandom2 = new GameRandom();
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/snowpile");
    }

    @Override
    public GameMessage getNewLocalization() {
        return new GameMessageBuilder().append("object", "snowpile").append(" " + (this.number + 1));
    }

    @Override
    public void tick(Level level, int x, int y) {
        if (level.isServer() && this.nextPile != -1 && level.getBiome(x, y) instanceof SnowBiome && level.weatherLayer.isRaining() && GameRandom.globalRandom.getChance(SnowTile.snowChance)) {
            GameObject snow = ObjectRegistry.getObject(this.nextPile);
            snow.placeObject(level, x, y, 0, false);
            level.sendObjectUpdatePacket(x, y);
        }
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(LootItem.between("snowball", 2, 5).splitItems(5));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "snowPileSetup", () -> {
            boolean mirror2;
            int pile2;
            int drawX2;
            boolean mirror1;
            int pile1;
            int drawX1;
            double nextGaussian;
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                nextGaussian = this.drawRandom.seeded(SnowPileObject.getTileSeed(tileX, tileY)).nextGaussian();
            }
            final int sortY1 = 3 + (int)(nextGaussian * 2.0) - 4;
            final int sortY2 = 15 + (int)(nextGaussian * 2.0) - 4;
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int drawY1 = drawY - 22 + (int)(nextGaussian * 2.0);
            GameRandom gameRandom2 = this.drawRandom1;
            synchronized (gameRandom2) {
                drawX1 = drawX + (int)(this.drawRandom1.seeded(SnowPileObject.getTileSeed(tileX, tileY, 9000)).nextGaussian() * 4.0);
                pile1 = this.drawRandom1.nextInt(4);
                mirror1 = this.drawRandom1.nextBoolean();
            }
            final TextureDrawOptionsEnd options1 = this.texture.initDraw().sprite(this.number, pile1, 32).light(light).mirror(mirror1, false).pos(drawX1, drawY1);
            int drawY2 = drawY - 10 + (int)(nextGaussian * 2.0);
            GameRandom gameRandom3 = this.drawRandom2;
            synchronized (gameRandom3) {
                drawX2 = drawX + (int)(this.drawRandom2.seeded(SnowPileObject.getTileSeed(tileX, tileY, 8000)).nextGaussian() * 4.0);
                pile2 = this.drawRandom2.nextInt(4);
                mirror2 = this.drawRandom2.nextBoolean();
            }
            final TextureDrawOptionsEnd options2 = this.texture.initDraw().sprite(this.number, pile2, 32).light(light).mirror(mirror2, false).pos(drawX2, drawY2);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return sortY1;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "snowPileDraw", options1::draw);
                }
            });
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return sortY2;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "snowPileDraw", options2::draw);
                }
            });
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror2;
        int pile2;
        int drawX2;
        boolean mirror1;
        int pile1;
        int drawX1;
        double nextGaussian;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            nextGaussian = this.drawRandom.seeded(SnowPileObject.getTileSeed(tileX, tileY)).nextGaussian();
        }
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int drawY1 = drawY - 22 + (int)(nextGaussian * 2.0);
        GameRandom gameRandom2 = this.drawRandom1;
        synchronized (gameRandom2) {
            drawX1 = drawX + (int)(this.drawRandom1.seeded(SnowPileObject.getTileSeed(tileX, tileY, 9000)).nextGaussian() * 4.0);
            pile1 = this.drawRandom1.nextInt(4);
            mirror1 = this.drawRandom1.nextBoolean();
        }
        this.texture.initDraw().sprite(this.number, pile1, 32).light(light).alpha(0.5f).mirror(mirror1, false).draw(drawX1, drawY1);
        int drawY2 = drawY - 10 + (int)(nextGaussian * 2.0);
        GameRandom gameRandom3 = this.drawRandom2;
        synchronized (gameRandom3) {
            drawX2 = drawX + (int)(this.drawRandom2.seeded(SnowPileObject.getTileSeed(tileX, tileY, 8000)).nextGaussian() * 4.0);
            pile2 = this.drawRandom2.nextInt(4);
            mirror2 = this.drawRandom2.nextBoolean();
        }
        this.texture.initDraw().sprite(this.number, pile2, 32).light(light).alpha(0.5f).mirror(mirror2, false).draw(drawX2, drawY2);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        Integer[] adj = level.getAdjacentObjectsInt(x, y);
        int objs = 0;
        Integer[] integerArray = adj;
        int n = integerArray.length;
        for (int i = 0; i < n; ++i) {
            int obj = integerArray[i];
            if (obj != 0) {
                ++objs;
            }
            if (objs <= 2) continue;
            return "snowspace";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return super.isValid(level, layerID, x, y) && level.getTileID(x, y) == TileRegistry.snowID;
    }
}

