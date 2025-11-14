/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class SpiritWaterTile
extends LiquidTile {
    private static long lastEffectTick;
    private static boolean lastWasSpiritCorrupted;
    private static long lastTimeSpiritCorruptedChanged;
    protected final GameRandom drawRandom = new GameRandom();
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;

    public static float getSpiritCorruptedFadeProgress(Level level) {
        long currentTick = level.tickManager().getTotalTicks();
        boolean isSpiritCorrupted = level.buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED);
        long fadeTime = 500L;
        if (lastEffectTick != currentTick) {
            lastEffectTick = currentTick;
            if (isSpiritCorrupted != lastWasSpiritCorrupted) {
                long previousTimeSinceCorruptedChanged = level.getLocalTime() - lastTimeSpiritCorruptedChanged;
                lastTimeSpiritCorruptedChanged = level.getLocalTime();
                lastWasSpiritCorrupted = isSpiritCorrupted;
                if (previousTimeSinceCorruptedChanged < fadeTime) {
                    lastTimeSpiritCorruptedChanged -= fadeTime - previousTimeSinceCorruptedChanged;
                }
            }
        }
        long timeSinceSpiritCorruptedChanged = Math.min(fadeTime, level.getLocalTime() - lastTimeSpiritCorruptedChanged);
        float fadeProgress = GameMath.limit((float)timeSinceSpiritCorruptedChanged / (float)fadeTime, 0.0f, 1.0f);
        return isSpiritCorrupted ? fadeProgress : 1.0f - fadeProgress;
    }

    public static Color getSpiritCorruptedFadeColor(Level level) {
        float fadeProgress = SpiritWaterTile.getSpiritCorruptedFadeProgress(level);
        return new Color(GameMath.lerp(fadeProgress, 255, 72), GameMath.lerp(fadeProgress, 255, 238), GameMath.lerp(fadeProgress, 255, 153));
    }

    public SpiritWaterTile() {
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
        if (level.isServer() && !mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY())) {
            if (level.buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED).booleanValue() && !mob.isOnGenericCooldown("spirithaunteddamage")) {
                if (mob.isHostile || mob.isCritter) {
                    return;
                }
                mob.startGenericCooldown("spirithaunteddamage", 500L);
                mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIRIT_HAUNTED, mob, 6.0f, null), true);
            } else {
                mob.buffManager.removeBuff("onfire", true);
            }
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
    public void tickEffect(Level level, int x, int y) {
        if (level.buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED).booleanValue() && GameRandom.globalRandom.getEveryXthChance(320) && level.getObjectID(x, y) == 0) {
            int spriteRes = 12;
            level.entityManager.addParticle(ParticleOption.base(x * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes), y * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes)), Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(new Color(92, 208, 174)).height(60.0f).movesConstant(GameRandom.globalRandom.getIntBetween(-1, 1), -3.0f).ignoreLight(true).minDrawLight(150).lifeTime(1500).sizeFades(6, 10);
        }
    }

    @Override
    public LiquidTile.TextureIndexes getTextureIndexes(Level level, int tileX, int tileY, Biome biome) {
        return new LiquidTile.TextureIndexes(0, 1, 2, 3);
    }

    @Override
    public Color getLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return this.getLiquidColor(0);
    }

    @Override
    public Color getNewSplattingLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return SpiritWaterTile.getSpiritCorruptedFadeColor(level);
    }

    @Override
    public Color getLiquidMapColor(Level level, int tileX, int tileY, Biome biome) {
        if (level.isCave && biome instanceof SwampBiome) {
            return new Color(33, 104, 61);
        }
        if (level.isCave && biome instanceof DesertBiome) {
            return new Color(16, 194, 188);
        }
        return this.getLiquidColor(level, tileX, tileY, biome);
    }

    @Override
    public void addFullDrawables(LevelTileLiquidDrawOptions liquidList, Level level, int tileX, int tileY, int drawX, int drawY) {
        super.addFullDrawables(liquidList, level, tileX, tileY, drawX, drawY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            addBobbing = this.drawRandom.seeded(SpiritWaterTile.getTileSeed(tileX, tileY)).getChance(0.15f);
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
                tile = this.drawRandom.seeded(SpiritWaterTile.getTileSeed(tileX, tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }
            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }
    }
}

