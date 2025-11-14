/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.AscendedVoidStarParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.splattingManager.SplattingOptions;

public class AscendedVoidTile
extends TerrainSplatterTile {
    private final GameRandom drawRandom;
    private GameTextureSection swirls;
    private GameTextureSection grime;
    private GameTextureSection stars;
    private GameTextureSection fog;

    public AscendedVoidTile() {
        super(false, "ascendedvoid");
        this.mapColor = new Color(0, 20, 70);
        this.canBeMined = false;
        this.drawRandom = new GameRandom();
        this.rarity = Item.Rarity.EPIC;
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.swirls = this.loadTexture("tiles/ascendedvoid_swirls");
        this.grime = this.loadTexture("tiles/ascendedvoid_grime");
        this.stars = this.loadTexture("tiles/ascendedvoid_stars");
        this.fog = this.loadTexture("tiles/ascendedvoid_fog");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips itemTooltips = super.getItemTooltips(item, perspective);
        itemTooltips.add(Localization.translate("tile", "ascendedvoidtip"), 400);
        return itemTooltips;
    }

    protected GameTextureSection loadTexture(String path) {
        GameTexture texture = GameTexture.fromFile(path);
        GameTexture paddingTexture = new GameTexture(path + "_padding", texture.getWidth() + 2, texture.getHeight() + 2);
        paddingTexture.copy(texture, 1, 1);
        paddingTexture.copy(texture, 0, 0, 0, 1, texture.getWidth(), 1);
        paddingTexture.copy(texture, 0, paddingTexture.getHeight() - 1, 0, texture.getHeight() - 1, texture.getWidth(), 1);
        paddingTexture.copy(texture, 0, 0, 1, 0, 1, texture.getHeight());
        paddingTexture.copy(texture, paddingTexture.getWidth() - 1, 0, texture.getWidth() - 1, 0, 1, texture.getHeight());
        paddingTexture.makeFinal();
        texture.makeFinal();
        return tileTextures.addTexture(paddingTexture);
    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        GameRandom random = GameRandom.globalRandom;
        if (random.getChance(0.001f)) {
            int posX = x * 32 + random.nextInt(32);
            int posY = y * 32 + random.nextInt(32);
            level.entityManager.addParticle(new AscendedVoidStarParticle(level, posX, posY, 1000L), Particle.GType.COSMETIC);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(AscendedVoidTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 501;
    }

    public float getParallaxOffset(int value, float divisor) {
        float offset = (float)value % divisor / divisor;
        if (offset < 0.0f) {
            offset += 1.0f;
        }
        return offset;
    }

    @Override
    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        SplattingOptions splat = level.regionManager.getSplatTiles(tileX, tileY);
        if (splat != null) {
            splat.addTileDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, this, level, tileX, tileY, camera, tickManager);
        } else {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameTextureSection terrainTexture = this.getTerrainTexture(level, tileX, tileY);
            underLiquidList.add(terrainTexture).pos(drawX, drawY);
            this.addParallaxDrawOptions(overLiquidList, terrainTexture, camera, tileX, tileY, drawX, drawY);
        }
    }

    public void addParallaxDrawOptions(LevelTileTerrainDrawOptions list, GameTextureSection terrainSection, GameCamera camera, int tileX, int tileY, int drawX, int drawY) {
        this.addParallaxDrawOptions(list, "swirls", 0, terrainSection, this.swirls, camera, tileX, tileY, 3000.0f, 3000.0f, drawX, drawY);
        this.addParallaxDrawOptions(list, "grime", 1, terrainSection, this.grime, camera, tileX, tileY, 2000.0f, 2000.0f, drawX, drawY);
        this.addParallaxDrawOptions(list, "stars", 2, terrainSection, this.stars, camera, tileX, tileY, 8000.0f, 7000.0f, drawX, drawY);
        this.addParallaxDrawOptions(list, "fog", 3, terrainSection, this.fog, camera, tileX, tileY, 1000.0f, 1000.0f, drawX, drawY);
    }

    public void addParallaxDrawOptions(LevelTileTerrainDrawOptions list, String stringID, int priority, GameTextureSection terrainSection, GameTextureSection parallaxTexture, GameCamera camera, int tileX, int tileY, float xDivisor, float yDivisor, int drawX, int drawY) {
        GameTextureSection parallaxSprite = parallaxTexture.section(1, parallaxTexture.getWidth() - 1, 1, parallaxTexture.getHeight() - 1);
        SharedTextureDrawOptions draws = list.specialDrawables.getOrCreate(stringID, priority, () -> {
            final float textureStartX = parallaxSprite.getStartXFloat();
            final float textureStartY = parallaxSprite.getStartYFloat();
            float textureEndX = parallaxSprite.getEndXFloat();
            float textureEndY = parallaxSprite.getEndYFloat();
            final float textureSizeX = textureEndX - textureStartX;
            final float textureSizeY = textureEndY - textureStartY;
            final float parallaxXOffset2 = this.getParallaxOffset(camera.getX(), xDivisor) * textureSizeX;
            final float parallaxYOffset2 = this.getParallaxOffset(camera.getY(), yDivisor) * textureSizeY;
            SharedTextureDrawOptions options = new SharedTextureDrawOptions(generatedTileTexture){

                @Override
                public void draw(int maxDrawsPerCall) {
                    GameResources.ascendedVoidShader.use();
                    GameResources.ascendedVoidShader.passOffset(textureStartX, textureStartY, textureSizeX, textureSizeY, parallaxXOffset2, parallaxYOffset2);
                    super.draw(maxDrawsPerCall);
                    GameResources.ascendedVoidShader.stop();
                }
            };
            options.addShaderBind(1, parallaxSprite.getTexture());
            return options;
        });
        int parallaxSpriteWidth = parallaxSprite.getWidth() / 32;
        int parallaxSpriteHeight = parallaxSprite.getHeight() / 32;
        int parallaxSpriteX = Math.floorMod(tileX, parallaxSpriteWidth);
        int parallaxSpriteY = Math.floorMod(tileY, parallaxSpriteHeight);
        GameTextureSection spriteSection = parallaxSprite.sprite(parallaxSpriteX, parallaxSpriteY, 32);
        draws.add(terrainSection).addShaderSprite(new ShaderSprite(1, spriteSection)).pos(drawX, drawY);
    }

    @Override
    public void addSplatDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager, int spriteX, int spriteY, int drawX, int drawY) {
        super.addSplatDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager, spriteX, spriteY, drawX, drawY);
        GameTextureSection sprite = this.getSplattingTexture(level, tileX, tileY).sprite(spriteX, spriteY, 32);
        this.addParallaxDrawOptions(overLiquidList, sprite, camera, tileX, tileY, drawX, drawY);
    }
}

