/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LEDPanelObject
extends GameObject {
    public ObjectDamagedTextureArray texture;

    public LEDPanelObject() {
        this.setItemCategory("wiring");
        this.setCraftingCategory("wiring");
        this.mapColor = new Color(200, 200, 200);
        this.displayMapTooltip = true;
        this.showsWire = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.roomProperties.add("lights");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/ledpanel");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int sprite = 0;
        if (this.isLit(level, tileX, tileY)) {
            sprite = 1;
            light = new GameLight(150.0f);
        }
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - (texture.getHeight() - 32));
        tileList.add(new LevelSortedDrawable(this, tileX, tileY){

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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        if (this.isLit(level, tileX, tileY)) {
            texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY);
        } else {
            texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY);
        }
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        if (this.isLit(level, tileX, tileY)) {
            return 75;
        }
        return 0;
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        level.lightManager.updateStaticLight(tileX, tileY);
    }

    private boolean isLit(Level level, int x, int y) {
        return level.wireManager.isWireActiveAny(x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "activatedwiretip"));
        return tooltips;
    }
}

