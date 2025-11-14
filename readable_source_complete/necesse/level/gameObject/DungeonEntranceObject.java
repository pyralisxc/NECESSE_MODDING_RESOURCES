/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DungeonEntranceObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DungeonEntranceObject
extends GameObject {
    public GameTexture texture;

    public DungeonEntranceObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(53, 54, 59);
        this.displayMapTooltip = true;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/dungeonentrance");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(0, 0, 32).light(light).alpha(alpha).draw(drawX, drawY);
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
        if (level.isServer() && player.isServerClient() && (objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof PortalObjectEntity) {
            ((PortalObjectEntity)objectEntity).use(level.getServer(), player.getServerClient());
        }
        super.interact(level, x, y, player);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new DungeonEntranceObjectEntity(level, x, y);
    }
}

