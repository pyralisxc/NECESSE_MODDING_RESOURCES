/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.objectEntity.TempleEntranceObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;

public class TempleEntranceObject
extends StaticMultiObject {
    protected TempleEntranceObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "templeentrance");
        this.mapColor = new Color(122, 102, 60);
        this.displayMapTooltip = true;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd options;
        TempleEntranceObjectEntity oe = this.getMultiTile(level, 0, tileX, tileY).getMasterLevelObject(level, 0, tileX, tileY).map(o -> o.getCurrentObjectEntity(TempleEntranceObjectEntity.class)).orElse(null);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        float animationProgress = oe == null ? 1.0f : oe.getRevealAnimationProgress();
        int tileProgress = (int)((float)this.multiWidth * (1.0f - animationProgress) * 32.0f);
        int offset = Math.max(tileProgress - this.multiX * 32, 0);
        int startX = this.multiX * 32 + offset;
        int endX = startX + 32 - offset;
        drawX += offset;
        if (endX <= startX) {
            return;
        }
        int yOffset = texture.getHeight() - this.multiHeight * 32;
        if (this.multiY == 0) {
            options = texture.initDraw().section(startX, endX, 0, 32 + yOffset).light(light).pos(drawX, drawY - yOffset);
        } else {
            int startY = this.multiY * 32 + yOffset;
            options = texture.initDraw().section(startX, endX, startY, startY + 32).light(light).pos(drawX, drawY);
        }
        tileList.add(tm -> options.draw());
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        ObjectEntity objectEntity;
        LevelObject master;
        if (level.isServer() && player.isServerClient() && (master = (LevelObject)this.getMultiTile(level, 0, x, y).getMasterLevelObject(level, 0, x, y).orElse(null)) != null && (objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY)) instanceof PortalObjectEntity) {
            ((PortalObjectEntity)objectEntity).use(level.getServer(), player.getServerClient());
        }
        super.interact(level, x, y, player);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new TempleEntranceObjectEntity(level, x, y);
        }
        return super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerTempleEntrance() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(96, 64);
        ids[0] = ObjectRegistry.registerObject("templeentrance", new TempleEntranceObject(0, 0, 3, 2, ids, collision), 0.0f, false);
        ids[1] = ObjectRegistry.registerObject("templeentrance2", new TempleEntranceObject(1, 0, 3, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("templeentrance3", new TempleEntranceObject(2, 0, 3, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("templeentrance4", new TempleEntranceObject(0, 1, 3, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject("templeentrance5", new TempleEntranceObject(1, 1, 3, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject("templeentrance6", new TempleEntranceObject(2, 1, 3, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

