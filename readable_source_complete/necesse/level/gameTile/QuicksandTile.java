/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;

public class QuicksandTile
extends LiquidTile {
    public QuicksandTile() {
        super(new Color(124, 100, 33), "quicksand");
        this.overridesCannotPlaceOnLiquid = true;
    }

    @Override
    public float getMinLiquidAlpha(Level level) {
        return 1.0f;
    }

    @Override
    public float getMaxLiquidAlpha(Level level) {
        return 1.0f;
    }

    @Override
    public LiquidTile.TextureIndexes getTextureIndexes(Level level, int tileX, int tileY, Biome biome) {
        return new LiquidTile.TextureIndexes(0, 0, 0, 0, 250, 250);
    }

    @Override
    public float getLiquidMobHeightPercent(Level level, int tileX, int tileY, Mob perspective, int height) {
        ActiveBuff buff;
        if (perspective != null && (buff = perspective.buffManager.getBuff(BuffRegistry.QUICKSAND_STACKS)) != null) {
            return (float)buff.getStacks() / (float)buff.getMaxStacks();
        }
        return 0.0f;
    }

    @Override
    public double getPathCost(Level level, int tileX, int tileY, Mob mob) {
        return super.getPathCost(level, tileX, tileY, mob) * 2.0;
    }

    @Override
    public float getItemSinkingRate(float currentSinking) {
        return TickManager.getTickDelta(60.0f);
    }

    @Override
    public float getItemMaxSinking() {
        return 1.0f;
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        if (mob.canLevelInteract() && !mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY()) && !mob.isOnGenericCooldown("quicksandsink")) {
            int maxStacks = Integer.MAX_VALUE;
            if (mob.isHostile) {
                maxStacks = 20;
            } else if (mob.isAccelerating()) {
                maxStacks = 40;
            }
            ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.QUICKSAND_STACKS);
            if (buff == null || buff.getStacks() < maxStacks) {
                mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.QUICKSAND_STACKS, mob, 0.5f, null), false);
                mob.startGenericCooldown("quicksandsink", 100L);
            }
        }
    }

    @Override
    public Color getLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return this.getLiquidColor(5);
    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        if (GameRandom.globalRandom.getEveryXthChance(300) && level.getObjectID(x, y) == 0) {
            int spriteRes = 32;
            level.entityManager.addParticle(ParticleOption.base(x * 32 + GameRandom.globalRandom.getIntBetween(-5, 5), y * 32 + GameRandom.globalRandom.getIntBetween(-5, 5)), Particle.GType.COSMETIC).lifeTime(1500).sprite((options, lifeTime, timeAlive, lifePercent) -> {
                int frames = GameResources.quicksandBlob.getWidth() / spriteRes;
                return options.add(GameResources.quicksandBlob.sprite(Math.min((int)(lifePercent * (float)frames), frames - 1), 0, spriteRes));
            });
        }
    }

    @Override
    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
    }

    @Override
    public int getLiquidBobbing(Level level, int tileX, int tileY) {
        return 0;
    }
}

