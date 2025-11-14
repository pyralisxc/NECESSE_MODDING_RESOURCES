/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InvisibleTriggerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public abstract class InvisibleTriggerObject
extends GameObject {
    public final GameTileRange tileRange;
    public final GameTileRange triggerNearbyTileRange;
    public final boolean detectHostileMobs;
    public final boolean detectPassiveMobs;
    public final boolean detectPlayers;
    public final boolean detectSettlers;
    public final boolean detectAllHumans;
    public final boolean destroyOnTrigger;
    public final boolean requirePath;
    public final TriggerFunction onTrigger;

    public InvisibleTriggerObject(int tileRange, TriggerFunction onTrigger) {
        this(tileRange, 0, onTrigger);
    }

    public InvisibleTriggerObject(int tileRange, int triggerNearbyTileRange, TriggerFunction onTrigger) {
        this(tileRange, triggerNearbyTileRange, false, false, false, true, false, true, true, onTrigger);
    }

    public InvisibleTriggerObject(int tileRange, int triggerNearbyTileRange, boolean detectHostileMobs, boolean detectPassiveMobs, boolean detectAllHumans, boolean detectPlayers, boolean detectSettlers, boolean destroyOnTrigger, boolean requirePath, TriggerFunction onTrigger) {
        this.tileRange = new GameTileRange(tileRange, new Point[0]);
        this.triggerNearbyTileRange = new GameTileRange(triggerNearbyTileRange, new Point[0]);
        this.detectHostileMobs = detectHostileMobs;
        this.detectPassiveMobs = detectPassiveMobs;
        this.detectPlayers = detectPlayers;
        this.detectSettlers = detectSettlers;
        this.detectAllHumans = detectAllHumans;
        this.destroyOnTrigger = destroyOnTrigger;
        this.requirePath = requirePath;
        this.onTrigger = onTrigger;
        this.toolType = ToolType.UNBREAKABLE;
        this.mapColor = null;
        this.isSolid = false;
        this.validObjectLayers.add(0);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        InvisibleTriggerObjectEntity objectEntity;
        super.tick(mob, level, x, y);
        if (level.isServer() && this.tileRange.maxRange == 0 && InvisibleTriggerObjectEntity.canTriggerByMob(mob, this.detectHostileMobs, this.detectPassiveMobs, this.detectAllHumans, this.detectPlayers, this.detectSettlers) && (objectEntity = this.getCurrentObjectEntity(level, x, y, InvisibleTriggerObjectEntity.class)) != null) {
            objectEntity.triggerFromNearby(mob);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, final Level level, final int tileX, final int tileY, TickManager tickManager, final GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, level, tileX, tileY, tickManager, camera, perspective);
        if (!GlobalData.debugActive()) {
            return;
        }
        list.add(new LevelSortedDrawable(this){

            @Override
            public int getSortY() {
                return Integer.MAX_VALUE;
            }

            @Override
            public void draw(TickManager tickManager) {
                ObjectEntity currentObjectEntity = InvisibleTriggerObject.this.getCurrentObjectEntity(level, tileX, tileY);
                if (currentObjectEntity instanceof InvisibleTriggerObjectEntity) {
                    InvisibleTriggerObjectEntity triggerEntity = (InvisibleTriggerObjectEntity)currentObjectEntity;
                    triggerEntity.range.getDrawOptions(triggerEntity.isBeingTriggered() ? Color.green : Color.red, new Color(0, 0, 0, 0), tileX, tileY, camera).draw();
                }
                InvisibleTriggerObject.this.drawPreview(level, tileX, tileY, 0, 1.0f, null, camera);
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameResources.error.initDraw().pos(drawX, drawY).draw();
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new InvisibleTriggerObjectEntity(level, x, y, this.tileRange, this.triggerNearbyTileRange, this.detectHostileMobs, this.detectPassiveMobs, this.detectAllHumans, this.detectPlayers, this.detectSettlers, this.destroyOnTrigger, this.requirePath, this.onTrigger);
    }

    @FunctionalInterface
    public static interface TriggerFunction {
        public void onTrigger(Level var1, int var2, int var3, Mob var4);
    }
}

