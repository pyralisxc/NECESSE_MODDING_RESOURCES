/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
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
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MaskedPressurePlateObject
extends GameObject {
    protected String maskTextureName;
    protected String tileTextureName;
    public GameTexture texture;

    public MaskedPressurePlateObject(String maskTextureName, String tileTextureName, Color mapColor) {
        super(new Rectangle(0, 0));
        this.maskTextureName = maskTextureName;
        this.tileTextureName = tileTextureName;
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
        GameTexture tileTexture;
        super.loadTextures();
        this.texture = new GameTexture(GameTexture.fromFile("objects/" + this.maskTextureName, true));
        Point tileSprite = new Point(0, 0);
        try {
            tileTexture = GameTexture.fromFileRaw("tiles/" + this.tileTextureName + "_splat", true);
            tileSprite = new Point(3, 0);
        }
        catch (FileNotFoundException e) {
            tileTexture = GameTexture.fromFile("tiles/" + this.tileTextureName, true);
        }
        MergeFunction mergeFunction = (currentColor, mergeColor) -> currentColor.equals(Color.WHITE) ? mergeColor : currentColor;
        this.texture.merge(tileTexture, 0, 0, tileSprite.x * 32, tileSprite.y * 32 + 2, 32, 30, mergeFunction);
        this.texture.merge(tileTexture, 32, 0, tileSprite.x * 32, tileSprite.y * 32, 32, 32, mergeFunction);
    }

    @Override
    public GameTexture generateItemTexture() {
        return new GameTexture(this.texture, 0, 0, 32);
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

