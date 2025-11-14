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
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
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

public class ThornsObject
extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(600.0);

    public ThornsObject() {
        super("thorns", -1);
        this.setItemCategory("materials", "flowers");
        this.canPlaceOnShore = true;
        this.mapColor = new Color(191, 90, 62);
        this.stackSize = 250;
        this.displayMapTooltip = true;
        this.weaveAmount = 0.05f;
        this.extraWeaveSpace = 32;
        this.randomYOffset = 3.0f;
        this.randomXOffset = 10.0f;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        if (level.getTileID(x, y) != TileRegistry.mudID) {
            return "notmud";
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return super.isValid(level, layerID, x, y) && level.getTileID(x, y) == TileRegistry.mudID;
    }

    @Override
    public int getLightLevelMod(Level level, int x, int y) {
        return 30;
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (level.isServer() && mob.canTakeDamage() && !mob.isBoss() && !mob.isHostile && !mob.isCritter) {
            int maxHealth;
            float damage;
            if (!mob.isOnGenericCooldown("thornsdamage") && (damage = Math.max((float)Math.pow(maxHealth = mob.getMaxHealth(), 0.5) + (float)maxHealth / 30.0f, 10.0f)) != 0.0f) {
                mob.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0f, 0.0f, 0.0f, new ThornsAttacker());
                mob.startGenericCooldown("thornsdamage", 500L);
            }
            level.sendObjectChangePacket(level.getServer(), x, y, 0, 0);
        }
    }

    public GrassSpreadOptions getSpreadOptions(Level level) {
        return GrassSpreadOptions.init(this, level).maxSpread(2, 10, 1);
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

    private static class ThornsAttacker
    implements Attacker {
        @Override
        public GameMessage getAttackerName() {
            return new LocalMessage("object", "thorns");
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.getDeathMessages("thorns", 2);
        }

        @Override
        public Mob getFirstAttackOwner() {
            return null;
        }
    }
}

