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
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;

public class SlimeLiquidTile
extends LiquidTile {
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;
    protected final GameRandom drawRandom;

    public SlimeLiquidTile() {
        super(new Color(50, 200, 50), "slime");
        this.lightLevel = 50;
        this.lightHue = 130.0f;
        this.lightSat = 0.6f;
        this.drawRandom = new GameRandom();
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
        this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
    }

    @Override
    public double getPathCost(Level level, int tileX, int tileY, Mob mob) {
        if (!mob.isLavaImmune()) {
            return 1000.0;
        }
        return super.getPathCost(level, tileX, tileY, mob);
    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        if (GameRandom.globalRandom.getEveryXthChance(200) && level.getObjectID(x, y) == 0) {
            int spriteRes = 12;
            Color particleColor = this.getLiquidColor(level, x, y).brighter();
            level.entityManager.addParticle(ParticleOption.base(x * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes), y * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes)), Particle.GType.COSMETIC).lifeTime(1000).sprite((options, lifeTime, timeAlive, lifePercent) -> {
                int frames = GameResources.liquidBlobParticle.getWidth() / spriteRes;
                return options.add(GameResources.liquidBlobParticle.sprite((int)(lifePercent * (float)frames), 0, spriteRes));
            }).color(particleColor);
        }
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        if (mob.canLevelInteract() && !mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY()) && level.isServer() && mob.canTakeDamage() && !mob.isSlimeImmune() && !mob.isOnGenericCooldown("slimedamage")) {
            mob.startGenericCooldown("slimedamage", 500L);
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_POISON, mob, 10.0f, null), true);
        }
    }

    @Override
    public Color getLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return this.getLiquidColor(4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            addBobbing = this.drawRandom.seeded(SlimeLiquidTile.getTileSeed(tileX, tileY)).getChance(0.15f);
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
                tile = this.drawRandom.seeded(SlimeLiquidTile.getTileSeed(tileX, tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }
            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }
    }
}

