/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketHitObject;
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
import necesse.level.gameObject.GrassObject;
import necesse.level.gameObject.GrassSpreadOptions;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class CattailObject
extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(900.0);

    public CattailObject(String textureName, int undergroundPixels, Color mapColor) {
        super(textureName, undergroundPixels, 2);
        this.mapColor = mapColor;
        this.canPlaceOnLiquid = true;
        this.weaveTime = 1000;
        this.randomXOffset = 5.0f;
        this.randomYOffset = 4.0f;
    }

    public GrassSpreadOptions getSpreadOptions(Level level) {
        return GrassSpreadOptions.init(this, level).maxSpread(5, 9, 2);
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
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 14, -7, 0);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        OrderableDrawables tileList = new OrderableDrawables(new TreeMap<Integer, List<Drawable>>());
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 14, -7, 0, 0.5f);
        tileList.forEach(d -> d.draw(level.tickManager()));
        list.forEach(d -> d.draw(level.tickManager()));
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (level.getTileID(x, y) != TileRegistry.waterID) {
            return "wrongtile";
        }
        if (level.liquidManager.getHeight(x, y) < -6) {
            return "toodeep";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        }
        return level.getTileID(x, y) == TileRegistry.waterID;
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        level.makeGrassWeave(x, y, this.weaveTime, false);
    }
}

