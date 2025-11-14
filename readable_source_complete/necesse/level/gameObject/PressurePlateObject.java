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
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PressurePlateObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PressurePlateObject
extends GameObject {
    public GameTexture texture;

    public PressurePlateObject(Color mapColor) {
        super(new Rectangle(0, 0));
        this.mapColor = mapColor;
        this.setItemCategory("wiring");
        this.setCraftingCategory("wiring");
        this.showsWire = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.isPressurePlate = true;
        this.replaceCategories.add("pressureplate");
        this.canReplaceCategories.add("pressureplate");
        this.canReplaceCategories.add("lever");
        this.replaceRotations = false;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.getStringID());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        TextureDrawOptionsEnd options = ent != null && ((PressurePlateObjectEntity)ent).isDown() ? this.texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY) : this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public boolean isWireActive(Level level, int x, int y, int wireID) {
        ObjectEntity ent = level.entityManager.getObjectEntity(x, y);
        if (ent != null) {
            return ((PressurePlateObjectEntity)ent).isDown();
        }
        return false;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new PressurePlateObjectEntity(level, x, y, new Rectangle(4, 4, 24, 24));
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "activewiretip"));
        tooltips.addAll(super.getItemTooltips(item, perspective));
        return tooltips;
    }
}

