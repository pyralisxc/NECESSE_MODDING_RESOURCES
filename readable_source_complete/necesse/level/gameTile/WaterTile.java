/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.incursions.SettlementRuinsBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

public class WaterTile
extends LiquidTile {
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;
    protected final GameRandom drawRandom = new GameRandom();

    public WaterTile() {
        super(new Color(31, 133, 170), "freshwater_shallow", "freshwater_deep", "saltwater_shallow", "saltwater_deep", "swampfreshwater_shallow", "swampfreshwater_deep", "swampsaltwater_shallow", "swampsaltwater_deep");
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
        this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        if (!mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY())) {
            mob.buffManager.removeBuff("onfire", false);
        }
    }

    @Override
    public void tickValid(Level level, int x, int y, boolean underGeneration) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                GameTile t = level.getTile(x + i, y + j);
                if (!t.isLiquid || !(t instanceof LavaTile)) continue;
                if (!underGeneration && level.isClient()) {
                    for (int k = 0; k < 10; ++k) {
                        BombProjectile.spawnFuseParticle(level, x * 32 + GameRandom.globalRandom.nextInt(33), y * 32 + GameRandom.globalRandom.nextInt(33), 1.0f);
                    }
                    level.lightManager.refreshParticleLight(x, y, 0.0f, 0.3f);
                    SoundManager.playSound(GameResources.fizz, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).volume(0.5f));
                }
                level.setTile(x, y, TileRegistry.getTileID("rocktile"));
            }
        }
    }

    @Override
    public LiquidTile.TextureIndexes getTextureIndexes(Level level, int tileX, int tileY, Biome biome) {
        if (biome instanceof SwampBiome) {
            return new LiquidTile.TextureIndexes(4, 5, 6, 7);
        }
        return new LiquidTile.TextureIndexes(0, 1, 2, 3);
    }

    @Override
    public Color getLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        if (biome instanceof SwampBiome) {
            return this.getLiquidColor(2);
        }
        if (biome instanceof DesertBiome && level.isCave) {
            return this.getLiquidColor(3);
        }
        return this.getLiquidColor(0);
    }

    @Override
    public Color getNewSplattingLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        if (level.isCave && biome instanceof SwampBiome) {
            return new Color(200, 200, 100);
        }
        if (level.isCave && biome instanceof SnowBiome) {
            return new Color(200, 240, 255);
        }
        if (level.baseBiome instanceof SettlementRuinsBiome) {
            return new Color(255, 25, 50);
        }
        if (level.isCave) {
            return new Color(180, 200, 150);
        }
        return super.getNewSplattingLiquidColor(level, tileX, tileY, biome);
    }

    @Override
    public Color getLiquidMapColor(Level level, int tileX, int tileY, Biome biome) {
        if (level.isCave && biome instanceof SwampBiome) {
            return new Color(33, 104, 61);
        }
        if (level.isCave && biome instanceof DesertBiome) {
            return new Color(16, 194, 188);
        }
        if (level.baseBiome instanceof SettlementRuinsBiome) {
            return new Color(150, 50, 75);
        }
        return this.getLiquidColor(level, tileX, tileY, biome);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            addBobbing = this.drawRandom.seeded(WaterTile.getTileSeed(tileX, tileY)).getChance(0.15f);
        }
        if (addBobbing) {
            int tile;
            GameTextureSection bobbingTexture;
            int yOffset;
            int xOffset;
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int offset = this.getLiquidBobbing(level, tileX, tileY);
            if (level.liquidManager.getHeight(tileX, tileY) <= -10) {
                xOffset = 0;
                yOffset = offset;
                bobbingTexture = this.deepTexture;
            } else {
                xOffset = offset;
                yOffset = 0;
                bobbingTexture = this.shallowTexture;
            }
            GameRandom gameRandom2 = this.drawRandom;
            synchronized (gameRandom2) {
                tile = this.drawRandom.seeded(WaterTile.getTileSeed(tileX, tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }
            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }
    }
}

