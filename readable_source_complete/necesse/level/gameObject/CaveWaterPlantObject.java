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

public class CaveWaterPlantObject
extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(450.0);
    private final GameRandom drawRandom;

    public CaveWaterPlantObject(String textureName, int undergroundPixels, Color mapColor) {
        super(textureName, undergroundPixels, 4);
        this.mapColor = mapColor;
        this.canPlaceOnLiquid = true;
        this.weaveTime = 1000;
        this.randomXOffset = 7.0f;
        this.randomYOffset = 4.0f;
        this.drawRandom = new GameRandom();
    }

    public GrassSpreadOptions getSpreadOptions(Level level) {
        return GrassSpreadOptions.init(this, level).maxSpread(3, 8, 1);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean isLilyPad2;
        boolean isLilyPad1;
        boolean draw2;
        boolean draw1;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.seeded(CaveWaterPlantObject.getTileSeed(tileX, tileY, 10));
            draw1 = this.drawRandom.getChance(0.6f);
            draw2 = this.drawRandom.getChance(0.6f);
            if (!draw1 && !draw2) {
                if (this.drawRandom.nextBoolean()) {
                    draw1 = true;
                } else {
                    draw2 = true;
                }
            }
            isLilyPad1 = this.drawRandom.getChance(0.6f);
            isLilyPad2 = this.drawRandom.getChance(0.6f);
        }
        if (draw1) {
            if (isLilyPad1) {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 18, -16, 0, 4, 7);
            } else {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 10, -7, 1, 0, 3);
            }
        }
        if (draw2) {
            if (isLilyPad2) {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 30, -16, 1, 4, 7);
            } else {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -7, 2, 0, 3);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean isLilyPad2;
        boolean isLilyPad1;
        boolean draw2;
        boolean draw1;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.seeded(CaveWaterPlantObject.getTileSeed(tileX, tileY, 10));
            draw1 = this.drawRandom.getChance(0.6f);
            draw2 = this.drawRandom.getChance(0.6f);
            if (!draw1 && !draw2) {
                if (this.drawRandom.nextBoolean()) {
                    draw1 = true;
                } else {
                    draw2 = true;
                }
            }
            isLilyPad1 = this.drawRandom.getChance(0.6f);
            isLilyPad2 = this.drawRandom.getChance(0.6f);
        }
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        OrderableDrawables tileList = new OrderableDrawables(new TreeMap<Integer, List<Drawable>>());
        if (draw1) {
            if (isLilyPad1) {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 18, -16, 0, 4, 7, 0.5f);
            } else {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 10, -7, 1, 0, 3, 0.5f);
            }
        }
        if (draw2) {
            if (isLilyPad2) {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 30, -16, 1, 4, 7, 0.5f);
            } else {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -7, 2, 0, 3, 0.5f);
            }
        }
        tileList.forEach(d -> d.draw(level.tickManager()));
        list.forEach(d -> d.draw(level.tickManager()));
    }

    @Override
    protected boolean shouldDrawUnderground(Level level, int tileX, int tileY) {
        return level.getTile((int)tileX, (int)tileY).isLiquid;
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

