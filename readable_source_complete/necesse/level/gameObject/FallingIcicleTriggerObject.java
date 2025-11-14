/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.FallingIcicleEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.gameObject.FallenIcicleObject;
import necesse.level.gameObject.InvisibleTriggerObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallingIcicleTriggerObject
extends InvisibleTriggerObject {
    public FallingIcicleTriggerObject() {
        super(0, 2, false, false, false, true, false, true, true, FallingIcicleTriggerObject::onTriggered);
    }

    protected static void onTriggered(Level level, int tileX, int tileY, Mob mob) {
        int fallTime = GameRandom.globalRandom.getIntBetween(1000, 1500);
        FallingIcicleEvent event = new FallingIcicleEvent(tileX * 32, tileY * 32, (long)fallTime, (long)mob.getDistance(tileX * 32, tileY * 32) * 10L);
        level.entityManager.events.add(event);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        float shadowSize = FallenIcicleObject.getStartShadowSize(tileX, tileY);
        Point startOffset = FallenIcicleObject.getStartOffset(tileX, tileY);
        TextureDrawOptionsEnd shadowOptions = FallenIcicleObject.shadowTexture.initDraw().section(18, 47, 42, 59).size((int)(29.0f * shadowSize), (int)(17.0f * shadowSize)).posMiddle((int)((double)((float)(drawX + 16) + 0.5f) + ((Point2D)startOffset).getX()), (int)((double)((float)(drawY + 23) + 0.5f) + ((Point2D)startOffset).getY())).light(light).alpha(0.75f);
        tileList.add(tm -> shadowOptions.draw());
    }
}

