/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.TileRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CobwebObject
extends GrassObject {
    public CobwebObject() {
        super("cobweb", -1);
        this.mapColor = new Color(120, 120, 120);
        this.displayMapTooltip = true;
        this.weaveAmount = 0.05f;
        this.extraWeaveSpace = 32;
        this.randomYOffset = 4.0f;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable();
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (byPlayer && !level.isLiquidTile(x, y)) {
            return null;
        }
        if (level.getTileID(x, y) != TileRegistry.spiderNestID) {
            return "wrongtile";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        }
        if (level.objectLayer.isPlayerPlaced(layerID, x, y) && !level.isLiquidTile(x, y)) {
            return true;
        }
        return true;
    }

    @Override
    public int getLightLevelMod(Level level, int x, int y) {
        return 30;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "cobwebSetup", () -> {
            int maxTextureIndex;
            int minTextureIndex;
            Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
            int objs = 0;
            for (Integer id : adj) {
                if (id.intValue() != this.getID()) continue;
                ++objs;
            }
            if (objs < 5) {
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
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -5, 1, minTextureIndex, maxTextureIndex);
        });
    }

    @Override
    public ModifierValue<Float> getSlowModifier(Mob mob) {
        if (mob.isHostile || mob.isFlying()) {
            return super.getSpeedModifier(mob);
        }
        return new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.5f));
    }
}

