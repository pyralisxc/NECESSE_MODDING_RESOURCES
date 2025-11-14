/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.incursions.SpiderCastleBiome;
import necesse.level.maps.biomes.incursions.SwampDeepCaveBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class SpiderNestTile
extends TerrainSplatterTile {
    public static MobSpawnTable nestSpawnTable = new MobSpawnTable().add(100, (level, client, tile) -> {
        Biome biome = level.getBiome(tile.x, tile.y);
        if (biome instanceof SpiderCastleBiome) {
            return null;
        }
        String variant = "giantcavespider";
        if (biome instanceof SnowBiome) {
            variant = "blackcavespider";
        } else if (biome instanceof SwampBiome) {
            variant = level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER) ? "smallswampcavespider" : "swampcavespider";
        } else if (biome instanceof SwampDeepCaveBiome) {
            variant = "smallswampcavespider";
        }
        return MobRegistry.getMob(variant, level);
    });
    public static double webGrowChance = GameMath.getAverageSuccessRuns(2500.0);
    private final GameRandom drawRandom;

    public SpiderNestTile() {
        super(false, "spidernest", "splattingmaskwide");
        this.mapColor = new Color(51, 51, 51);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        GrassTile.addSimulateGrow(level, x, y, webGrowChance, ticks, "cobweb", (object, l, tileX, tileY, rotation) -> object.canPlace(level, x, y, 0, false) == null && level.getObjectID(x, y - 1) != ObjectRegistry.getObjectID("royaleggobject"), list, sendChanges);
    }

    @Override
    public void tick(Level level, int x, int y) {
        GameObject cobweb;
        if (!level.isServer()) {
            return;
        }
        if (level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(webGrowChance) && (cobweb = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cobweb"))).canPlace(level, x, y, 0, false) == null && level.getObjectID(x, y - 1) != ObjectRegistry.getObjectID("royaleggobject")) {
            cobweb.placeObject(level, x, y, 0, false);
            level.sendObjectUpdatePacket(x, y);
        }
    }

    @Override
    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(SpiderNestTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 200;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(TilePosition pos, MobSpawnTable defaultTable) {
        if (pos.objectID() == ObjectRegistry.cobWebID) {
            return nestSpawnTable;
        }
        int cobWeb = 0;
        Integer[] integerArray = pos.level.getAdjacentObjectsInt(pos.tileX, pos.tileY);
        int n = integerArray.length;
        for (int i = 0; i < n; ++i) {
            int objectID = integerArray[i];
            if (objectID != ObjectRegistry.cobWebID) continue;
            ++cobWeb;
        }
        if (cobWeb >= 2) {
            return nestSpawnTable;
        }
        return super.getMobSpawnTable(pos, defaultTable);
    }
}

