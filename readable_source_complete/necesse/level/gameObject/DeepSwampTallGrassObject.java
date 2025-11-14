/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.GrassObject;
import necesse.level.gameObject.GrassSpreadOptions;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class DeepSwampTallGrassObject
extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(300.0);

    public DeepSwampTallGrassObject() {
        super("deepswamptallgrass", -1);
        this.canPlaceOnShore = true;
        this.mapColor = new Color(47, 73, 44);
        this.displayMapTooltip = true;
        this.weaveAmount = 0.05f;
        this.extraWeaveSpace = 32;
        this.randomYOffset = 3.0f;
        this.randomXOffset = 10.0f;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "tallswampgrass");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        float baitChance = 30.0f;
        if (level.weatherLayer.isRaining()) {
            baitChance = 10.0f;
        }
        if (level.isCave) {
            baitChance = 25.0f;
        }
        return new LootTable(new ChanceLootItem(1.0f / baitChance, "swamplarva"));
    }

    @Override
    public boolean canPlaceOn(Level level, int layerID, int x, int y, GameObject other) {
        return other.getID() == 0 || !other.getValidObjectLayers().contains(ObjectLayerRegistry.TILE_LAYER);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return "occupied";
        }
        if (byPlayer && level.getTile((int)x, (int)y).isOrganic) {
            return null;
        }
        if (level.getTileID(x, y) != TileRegistry.deepSwampRockID) {
            return "notswamprock";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        }
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return false;
        }
        if (level.objectLayer.isPlayerPlaced(layerID, x, y) && level.getTile((int)x, (int)y).isOrganic) {
            return true;
        }
        return level.getTileID(x, y) == TileRegistry.deepSwampRockID;
    }

    @Override
    public int getLightLevelMod(Level level, int x, int y) {
        return 30;
    }

    public GrassSpreadOptions getSpreadOptions(Level level) {
        return GrassSpreadOptions.init(this, level).maxSpread(2, 8, 1);
    }

    @Override
    public void tick(Level level, int x, int y) {
        super.tick(level, x, y);
        if (level.isServer() && GameRandom.globalRandom.getChance(spreadChance)) {
            this.getSpreadOptions(level).tickSpread(x, y, true);
        }
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        super.addSimulateLogic(level, x, y, ticks, list, sendChanges);
        this.getSpreadOptions(level).addSimulateSpread(x, y, spreadChance, ticks, list, sendChanges);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        level.makeGrassWeave(x, y, this.weaveTime, false);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "thornsSetup", () -> {
            int maxTextureIndex;
            int minTextureIndex;
            Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
            int objs = 0;
            for (Integer id : adj) {
                if (id.intValue() != this.getID()) continue;
                ++objs;
            }
            if (objs < 4) {
                minTextureIndex = 4;
                maxTextureIndex = 7;
            } else {
                minTextureIndex = 0;
                maxTextureIndex = 3;
            }
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameLight light = level.getLightLevel(tileX, tileY);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 6, -5, 0, minTextureIndex, maxTextureIndex);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 12, -5, 1);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -5, 2, minTextureIndex, maxTextureIndex);
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int maxTextureIndex;
        int minTextureIndex;
        Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
        int objs = 0;
        for (Integer id : adj) {
            if (id.intValue() != this.getID()) continue;
            ++objs;
        }
        if (objs < 4) {
            minTextureIndex = 4;
            maxTextureIndex = 7;
        } else {
            minTextureIndex = 0;
            maxTextureIndex = 3;
        }
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        OrderableDrawables tileList = new OrderableDrawables(new TreeMap<Integer, List<Drawable>>());
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, null, 6, -5, 0, minTextureIndex, maxTextureIndex, 0.5f);
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, null, 12, -5, 1, 0.5f);
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, null, 26, -5, 2, minTextureIndex, maxTextureIndex, 0.5f);
        tileList.forEach(d -> d.draw(level.tickManager()));
        list.forEach(d -> d.draw(level.tickManager()));
    }

    @Override
    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return true;
    }
}

