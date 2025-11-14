/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.TileRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.DeathMessageTable
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.projectile.BombProjectile
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.LevelTileTerrainDrawOptions
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTexture.GameTextureSection
 *  necesse.level.gameTile.GameTile
 *  necesse.level.gameTile.LavaTile
 *  necesse.level.gameTile.LiquidTile
 *  necesse.level.gameTile.LiquidTile$TextureIndexes
 *  necesse.level.maps.Level
 *  necesse.level.maps.biomes.Biome
 */
package aphorea.tiles;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
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

public class InfectedWaterTile
extends LiquidTile {
    public static Attacker INFECED_WATER_ATTACKER = new Attacker(){

        public GameMessage getAttackerName() {
            return new StaticMessage("Infected Water");
        }

        public DeathMessageTable getDeathMessages() {
            return new DeathMessageTable().add(new GameMessage[]{new LocalMessage("deaths", "default")});
        }

        public Mob getFirstAttackOwner() {
            return null;
        }
    };
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;
    protected final GameRandom drawRandom = new GameRandom();
    private static final Map<Integer, Long> lastHit = new HashMap<Integer, Long>();
    private static final Map<Integer, Integer> consecutiveHits = new HashMap<Integer, Integer>();

    public InfectedWaterTile() {
        super(AphColors.infected_light, new String[]{"infected_freshwater_shallow", "infected_freshwater_deep", "infected_saltwater_shallow", "infected_saltwater_deep"});
        this.lightLevel = 150;
        this.lightHue = 0.0f;
        this.lightSat = 0.6f;
    }

    protected void loadTextures() {
        super.loadTextures();
        this.deepTexture = tileTextures.addTexture(GameTexture.fromFile((String)"tiles/waterdeep"));
        this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile((String)"tiles/watershallow"));
    }

    public void tick(Mob mob, Level level, int x, int y) {
        if (!mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY())) {
            mob.buffManager.removeBuff("onfire", false);
            if (level.isServer() && mob.isPlayer && (level.isCave || mob.getMount() == null || !mob.getMount().getStringID().contains("boat"))) {
                long currentTime;
                float damageMultiplier = 0.0f;
                long lastHitTime = lastHit.getOrDefault(mob.getID(), 0L);
                if (lastHitTime + (long)(level.isCave ? 200 : 1000) < (currentTime = level.getTime())) {
                    int consecutiveHitsCount = consecutiveHits.getOrDefault(mob.getID(), 0);
                    consecutiveHitsCount = lastHitTime + (long)(level.isCave ? 300 : 1500) > currentTime ? ++consecutiveHitsCount : 0;
                    damageMultiplier = consecutiveHitsCount;
                    lastHit.put(mob.getID(), currentTime);
                    consecutiveHits.put(mob.getID(), consecutiveHitsCount);
                }
                if (damageMultiplier != 0.0f) {
                    float damage = (level.isCave ? 10.0f : 5.0f) * damageMultiplier;
                    mob.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0f, 0.0f, 0.0f, INFECED_WATER_ATTACKER);
                }
            }
        }
    }

    public void tickValid(Level level, int x, int y, boolean underGeneration) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                GameTile t = level.getTile(x + i, y + j);
                if (!t.isLiquid || !(t instanceof LavaTile)) continue;
                if (!underGeneration && level.isClient()) {
                    for (int k = 0; k < 10; ++k) {
                        BombProjectile.spawnFuseParticle((Level)level, (float)(x * 32 + GameRandom.globalRandom.nextInt(33)), (float)(y * 32 + GameRandom.globalRandom.nextInt(33)), (float)1.0f);
                    }
                    level.lightManager.refreshParticleLight(x, y, 0.0f, 0.3f);
                    SoundManager.playSound((GameSound)GameResources.fizz, (SoundEffect)SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)).volume(0.5f));
                }
                level.setTile(x, y, TileRegistry.getTileID((String)"rocktile"));
            }
        }
    }

    public LiquidTile.TextureIndexes getTextureIndexes(Level level, int tileX, int tileY, Biome biome) {
        return new LiquidTile.TextureIndexes(0, 1, 2, 3);
    }

    public Color getLiquidColor(Level level, int x, int y, Biome biome) {
        return AphColors.infected_light;
    }

    public Color getNewSplattingLiquidColor(Level level, int tileX, int tileY, Biome biome) {
        return AphColors.infected;
    }

    public Color getMapColor(Level level, int tileX, int tileY) {
        return this.getLiquidColor(level, tileX, tileY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            addBobbing = this.drawRandom.seeded(InfectedWaterTile.getTileSeed((int)tileX, (int)tileY)).getChance(0.15f);
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
                tile = this.drawRandom.seeded(InfectedWaterTile.getTileSeed((int)tileX, (int)tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }
            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }
    }
}

