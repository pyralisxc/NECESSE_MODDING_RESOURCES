/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileLiquidRegionDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.layers.BiomeBlendingOptions;
import necesse.level.maps.splattingManager.SplattingOptions;

public abstract class LiquidTile
extends GameTile {
    public static Attacker LAVA_ATTACKER = new Attacker(){

        @Override
        public GameMessage getAttackerName() {
            return new LocalMessage("deaths", "lavaname");
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.getDeathMessages("fire", 3);
        }

        @Override
        public Mob getFirstAttackOwner() {
            return null;
        }
    };
    private static Color[] liquidColors;
    private final String[] splatTextureNames;
    protected boolean[] isUsingNewTerrainSplatting;
    public boolean preferLegacySplatting;
    private GameTextureSection[] splatTextures;
    private final GameRandom spriteRandom;
    public Color liquidColor;
    private final GameRandom bobbingRandom;

    public LiquidTile(Color liquidColor, String ... textureNames) {
        super(false);
        this.canBeMined = false;
        this.liquidColor = liquidColor;
        this.splatTextureNames = textureNames;
        this.mapColor = liquidColor;
        this.spriteRandom = new GameRandom();
        this.bobbingRandom = new GameRandom();
        this.stackSize = 250;
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture bucket = GameTexture.fromFile("tiles/bucket", true);
        GameTexture itemTexture = new GameTexture(bucket, 0, 0, 32, 32);
        GameTexture overlay = new GameTexture(bucket, 0, 32, 32, 32);
        overlay.applyColor(this.liquidColor, MergeFunction.GLBLEND);
        itemTexture.merge(overlay, 0, 0, MergeFunction.NORMAL);
        itemTexture.makeFinal();
        return itemTexture;
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        if (liquidColors == null) {
            GameTexture liquidColorsTexture = GameTexture.fromFile("tiles/liquidcolors", true);
            liquidColors = new Color[liquidColorsTexture.getWidth()];
            for (int i = 0; i < liquidColors.length; ++i) {
                LiquidTile.liquidColors[i] = liquidColorsTexture.getColor(i, 0);
            }
        }
        this.isUsingNewTerrainSplatting = new boolean[this.splatTextureNames.length];
        this.splatTextures = new GameTextureSection[this.splatTextureNames.length];
        if (!this.preferLegacySplatting) {
            for (int i = 0; i < this.splatTextureNames.length; ++i) {
                try {
                    GameTexture terrain = GameTexture.fromFileRaw("tiles/" + this.splatTextureNames[i] + "_splat");
                    this.splatTextures[i] = tileTextures.addTexture(terrain);
                    this.isUsingNewTerrainSplatting[i] = true;
                    continue;
                }
                catch (FileNotFoundException fileNotFoundException) {
                    // empty catch block
                }
            }
        }
    }

    public float getMinLiquidAlpha(Level level) {
        return 0.5f;
    }

    public float getMaxLiquidAlpha(Level level) {
        return 0.9f;
    }

    public TextureIndexes getTextureIndexes(Level level, int tileX, int tileY, Biome biome) {
        return new TextureIndexes(0, 0, 0, 0);
    }

    public boolean isUsingNewTerrainSplatting(Level level, int tileX, int tileY) {
        if (this.isUsingNewTerrainSplatting.length == 0) {
            return false;
        }
        return this.isUsingNewTerrainSplatting[this.getTextureIndexes((Level)level, (int)tileX, (int)tileY, (Biome)level.getBiome((int)tileX, (int)tileY)).freshShallow];
    }

    public GameTextureSection getNewSplattingSection(int index) {
        return this.splatTextures[index];
    }

    public int getNewSplattingFrame(GameTextureSection texture, Level level, int animTime) {
        int frames = texture.getWidth() / 224;
        int frame = 0;
        if (frames > 1) {
            return GameUtils.getAnim(level.getLocalTime(), frames, animTime * frames);
        }
        return frame;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameTextureSection getNewSplattingTexture(GameTextureSection texture, int frame, Level level, int tileX, int tileY) {
        int section;
        GameRandom gameRandom = this.spriteRandom;
        synchronized (gameRandom) {
            section = this.spriteRandom.seeded(LiquidTile.getTileSeed(tileX, tileY, 5)).nextInt(texture.getHeight() / 96);
        }
        return texture.sprite(frame, section, 224, 96);
    }

    @Override
    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        return Collections.singletonList(new FishingPositionLevelJob(tileX, tileY));
    }

    protected Color getLiquidColor(int index) {
        if (index >= liquidColors.length) {
            return this.liquidColor;
        }
        return liquidColors[index];
    }

    public Color getNewSplattingLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return Color.WHITE;
    }

    public abstract Color getLiquidColor(Level var1, int var2, int var3, Biome var4);

    public final Color getLiquidColor(Level level, int tileX, int tileY) {
        return this.getLiquidColor(level, tileX, tileY, level.getBiome(tileX, tileY));
    }

    @Override
    public double getPathCost(Level level, int tileX, int tileY, Mob mob) {
        return 1.0 / (double)mob.getSwimSpeed();
    }

    @Override
    public float getItemSinkingRate(float currentSinking) {
        return TickManager.getTickDelta(10.0f);
    }

    @Override
    public float getItemMaxSinking() {
        return 0.25f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Point getTileSprite(Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.spriteRandom;
        synchronized (gameRandom) {
            tile = this.spriteRandom.seeded(LiquidTile.getTileSeed(tileX, tileY, 9)).nextInt(TerrainSplatterTile.NEW_FULL_TILE_SPRITES.length);
        }
        return TerrainSplatterTile.NEW_FULL_TILE_SPRITES[tile];
    }

    @Override
    public Color getMapColor(Level level, int tileX, int tileY) {
        GameTile underLiquidTile = level.getUnderLiquidTile(tileX, tileY);
        Color currentColor = underLiquidTile.getMapColor(level, tileX, tileY);
        for (BiomeBlendingOptions.BiomeBlendingOption blendOption : level.biomeBlendingManager.getBlendOptions(tileX, tileY)) {
            float alpha = blendOption.centerAlpha;
            Color color = this.getLiquidMapColor(level, tileX, tileY, BiomeRegistry.getBiome(blendOption.biomeID));
            Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * alpha * 0.9f));
            currentColor = MergeFunction.NORMAL.merge(currentColor, alphaColor);
        }
        return currentColor;
    }

    public Color getLiquidMapColor(Level level, int tileX, int tileY, Biome biome) {
        return this.getLiquidColor(level, tileX, tileY, biome);
    }

    public void addFullDrawables(LevelTileLiquidDrawOptions liquidList, Level level, int tileX, int tileY, int drawX, int drawY) {
        Point sprite = this.getTileSprite(level, tileX, tileY);
        Biome baseBiome = level.getBiome(tileX, tileY);
        BiomeBlendingOptions.BiomeBlendingOption[] blendOptions = level.biomeBlendingManager.getBlendOptions(tileX, tileY);
        if (blendOptions == null) {
            this.addFullDrawables(liquidList, level, tileX, tileY, sprite.x, sprite.y, baseBiome, drawX, drawY);
        } else {
            for (BiomeBlendingOptions.BiomeBlendingOption blendOption : blendOptions) {
                if (blendOption.biomeID == baseBiome.getID()) {
                    this.addFullDrawables(liquidList, level, tileX, tileY, sprite.x, sprite.y, baseBiome, drawX, drawY);
                    continue;
                }
                this.addFullDrawables(liquidList, level, tileX, tileY, sprite.x, sprite.y, BiomeRegistry.getBiome(blendOption.biomeID), blendOption, drawX, drawY);
            }
        }
    }

    public void addFullDrawables(LevelTileLiquidDrawOptions liquidList, Level level, int tileX, int tileY, int spriteX, int spriteY, Biome biome, BiomeBlendingOptions.BiomeBlendingOption blendOption, int drawX, int drawY) {
        Color color = this.getNewSplattingLiquidColor(level, tileX, tileY, biome);
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        float[] topLeftAdvColor = this.getAdvColor(red, green, blue, alpha, blendOption.topLeftAlpha, blendOption.topMidAlpha, blendOption.centerAlpha, blendOption.leftMidAlpha);
        this.addFullDrawables(liquidList, level, tileX, tileY, spriteX, spriteY, biome, topLeftAdvColor, 0, 0, drawX, drawY);
        float[] topRightAdvColor = this.getAdvColor(red, green, blue, alpha, blendOption.topMidAlpha, blendOption.topRightAlpha, blendOption.rightMidAlpha, blendOption.centerAlpha);
        this.addFullDrawables(liquidList, level, tileX, tileY, spriteX, spriteY, biome, topRightAdvColor, 1, 0, drawX + 16, drawY);
        float[] botRightAdvColor = this.getAdvColor(red, green, blue, alpha, blendOption.centerAlpha, blendOption.rightMidAlpha, blendOption.bottomRightAlpha, blendOption.bottomMidAlpha);
        this.addFullDrawables(liquidList, level, tileX, tileY, spriteX, spriteY, biome, botRightAdvColor, 1, 1, drawX + 16, drawY + 16);
        float[] botLeftAdvColor = this.getAdvColor(red, green, blue, alpha, blendOption.leftMidAlpha, blendOption.centerAlpha, blendOption.bottomMidAlpha, blendOption.bottomLeftAlpha);
        this.addFullDrawables(liquidList, level, tileX, tileY, spriteX, spriteY, biome, botLeftAdvColor, 0, 1, drawX, drawY + 16);
    }

    protected float[] getAdvColor(float red, float green, float blue, float alpha, float alphaTopLeft, float alphaTopRight, float alphaBotRight, float alphaBotLeft) {
        return new float[]{red, green, blue, alpha * alphaTopLeft, red, green, blue, alpha * alphaTopRight, red, green, blue, alpha * alphaBotRight, red, green, blue, alpha * alphaBotLeft};
    }

    public void addFullDrawables(LevelTileLiquidDrawOptions liquidList, Level level, int tileX, int tileY, int spriteX, int spriteY, Biome biome, float[] advColor, int subSpriteX, int subSpriteY, int drawX, int drawY) {
        TextureIndexes indexes = this.getTextureIndexes(level, tileX, tileY, biome);
        GameTextureSection shallowFreshSection = this.getNewSplattingSection(indexes.freshShallow);
        int shallowFreshFrame = this.getNewSplattingFrame(shallowFreshSection, level, indexes.freshAnimTime);
        GameTextureSection shallowFreshTexture = this.getNewSplattingTexture(shallowFreshSection, shallowFreshFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32).sprite(subSpriteX, subSpriteY, 16);
        GameTextureSection deepFreshSection = this.getNewSplattingSection(indexes.freshDeep);
        int deepFreshFrame = this.getNewSplattingFrame(deepFreshSection, level, indexes.freshAnimTime);
        GameTextureSection deepFreshTexture = this.getNewSplattingTexture(deepFreshSection, deepFreshFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32).sprite(subSpriteX, subSpriteY, 16);
        GameTextureSection shallowSaltSection = this.getNewSplattingSection(indexes.saltShallow);
        int shallowSaltFrame = this.getNewSplattingFrame(shallowSaltSection, level, indexes.saltAnimTime);
        GameTextureSection shallowSaltTexture = this.getNewSplattingTexture(shallowSaltSection, shallowSaltFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32).sprite(subSpriteX, subSpriteY, 16);
        GameTextureSection deepSaltSection = this.getNewSplattingSection(indexes.saltDeep);
        int deepSaltFrame = this.getNewSplattingFrame(deepSaltSection, level, indexes.saltAnimTime);
        GameTextureSection deepSaltTexture = this.getNewSplattingTexture(deepSaltSection, deepSaltFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32).sprite(subSpriteX, subSpriteY, 16);
        LevelTileLiquidRegionDrawOptions drawOptions = liquidList.getByTile(level, tileX, tileY);
        if (drawOptions != null) {
            drawOptions.add(shallowFreshTexture).addShaderSprite(new ShaderSprite(1, deepFreshTexture)).addShaderSprite(new ShaderSprite(2, shallowSaltTexture)).addShaderSprite(new ShaderSprite(3, deepSaltTexture)).addShaderSprite(drawOptions.getSubShaderSprite(tileX, tileY, subSpriteX, subSpriteY)).advColor(advColor).pos(drawX, drawY);
        }
    }

    public void addFullDrawables(LevelTileLiquidDrawOptions liquidList, Level level, int tileX, int tileY, int spriteX, int spriteY, Biome biome, int drawX, int drawY) {
        TextureIndexes indexes = this.getTextureIndexes(level, tileX, tileY, biome);
        GameTextureSection shallowFreshSection = this.getNewSplattingSection(indexes.freshShallow);
        int shallowFreshFrame = this.getNewSplattingFrame(shallowFreshSection, level, indexes.freshAnimTime);
        GameTextureSection shallowFreshTexture = this.getNewSplattingTexture(shallowFreshSection, shallowFreshFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32);
        GameTextureSection deepFreshSection = this.getNewSplattingSection(indexes.freshDeep);
        int deepFreshFrame = this.getNewSplattingFrame(deepFreshSection, level, indexes.freshAnimTime);
        GameTextureSection deepFreshTexture = this.getNewSplattingTexture(deepFreshSection, deepFreshFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32);
        GameTextureSection shallowSaltSection = this.getNewSplattingSection(indexes.saltShallow);
        int shallowSaltFrame = this.getNewSplattingFrame(shallowSaltSection, level, indexes.saltAnimTime);
        GameTextureSection shallowSaltTexture = this.getNewSplattingTexture(shallowSaltSection, shallowSaltFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32);
        GameTextureSection deepSaltSection = this.getNewSplattingSection(indexes.saltDeep);
        int deepSaltFrame = this.getNewSplattingFrame(deepSaltSection, level, indexes.saltAnimTime);
        GameTextureSection deepSaltTexture = this.getNewSplattingTexture(deepSaltSection, deepSaltFrame, level, tileX, tileY).sprite(spriteX, spriteY, 32);
        LevelTileLiquidRegionDrawOptions drawOptions = liquidList.getByTile(level, tileX, tileY);
        if (drawOptions != null) {
            drawOptions.add(shallowFreshTexture).addShaderSprite(new ShaderSprite(1, deepFreshTexture)).addShaderSprite(new ShaderSprite(2, shallowSaltTexture)).addShaderSprite(new ShaderSprite(3, deepSaltTexture)).addShaderSprite(drawOptions.getShaderSprite(tileX, tileY)).color(this.getNewSplattingLiquidColor(level, tileX, tileY, biome)).pos(drawX, drawY);
        }
    }

    @Override
    public final void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        Performance.record((PerformanceTimerManager)tickManager, "liquidSetup", () -> {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            if (this.isUsingNewTerrainSplatting(level, tileX, tileY)) {
                level.getUnderLiquidTile(tileX, tileY).addDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager);
                SplattingOptions splat = level.regionManager.getSplatTiles(tileX, tileY);
                if (splat != null) {
                    splat.addLiquidDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, this, level, tileX, tileY, camera, tickManager);
                } else {
                    this.addFullDrawables(liquidList, level, tileX, tileY, drawX, drawY);
                }
                if (tileY != 0) {
                    GameTile tileAbove = level.getTile(tileX, tileY - 1);
                    if (!tileAbove.isLiquid) {
                        tileAbove.addBridgeDrawables(overLiquidList, sortedList, level, tileX, tileY, camera, tickManager);
                    }
                }
            } else {
                underLiquidList.add(tileBlankTexture).size(32, 32).color(this.getLiquidColor(level, tileX, tileY)).pos(drawX, drawY);
                if (tileY != 0) {
                    GameTile tileAbove = level.getTile(tileX, tileY - 1);
                    if (!tileAbove.isLiquid) {
                        tileAbove.addBridgeDrawables(overLiquidList, sortedList, level, tileX, tileY, camera, tickManager);
                    }
                }
                this.addLiquidTopDrawables(overLiquidList, sortedList, level, tileX, tileY, camera, tickManager);
            }
        });
    }

    protected abstract void addLiquidTopDrawables(LevelTileTerrainDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7);

    public GameTextureSection getShoreTexture() {
        return tileShoreTexture;
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Renderer.initQuadDraw(32, 32).color(this.getLiquidColor(level, tileX, tileY)).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public String canPlace(Level level, int x, int y, boolean byPlayer) {
        String canPlace = super.canPlace(level, x, y, byPlayer);
        if (canPlace != null) {
            return canPlace;
        }
        if (!this.overridesCannotPlaceOnLiquid && Arrays.stream(level.getAdjacentObjects(x, y)).anyMatch(o -> o.isWall || o.isRock)) {
            return "objectadjacent";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLiquidBobbing(Level level, int tileX, int tileY) {
        GameRandom gameRandom = this.bobbingRandom;
        synchronized (gameRandom) {
            int offset = (int)((level.getWorldEntity().getTime() + (long)Math.abs(this.bobbingRandom.seeded(LiquidTile.getTileSeed(tileX, tileY)).nextInt())) % 1600L) / 200;
            if (offset > 4) {
                offset = 4 - offset % 4;
            }
            return offset;
        }
    }

    public static class TextureIndexes {
        public int freshShallow;
        public int freshDeep;
        public int saltShallow;
        public int saltDeep;
        public int freshAnimTime;
        public int saltAnimTime;

        public TextureIndexes(int freshShallow, int freshDeep, int saltShallow, int saltDeep, int freshAnimTime, int saltAnimTime) {
            this.freshShallow = freshShallow;
            this.freshDeep = freshDeep;
            this.saltShallow = saltShallow;
            this.saltDeep = saltDeep;
            this.freshAnimTime = freshAnimTime;
            this.saltAnimTime = saltAnimTime;
        }

        public TextureIndexes(int freshShallow, int freshDeep, int saltShallow, int saltDeep) {
            this(freshShallow, freshDeep, saltShallow, saltDeep, 250, 250);
        }
    }
}

