/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ClockObject
extends FurnitureObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;

    public ClockObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.furnitureType = "clock";
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    public ClockObject(String textureName, Color mapColor, String ... category) {
        this(textureName, ToolType.ALL, mapColor, category);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 3, y * 32 + 16, 24, 16);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 3, 16, 24);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 3, y * 32, 24, 16);
        }
        return new Rectangle(x * 32 + 16, y * 32 + 3, 16, 24);
    }

    @Override
    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        super.onMouseHover(level, x, y, camera, perspective, debug);
        if (perspective != null) {
            WorldEntity worldEntity = perspective.getWorldEntity();
            StringTooltips tooltips = new StringTooltips();
            tooltips.add(Localization.translate("ui", "currenttime", "time", worldEntity.getDayTimeReadable()));
            tooltips.add(worldEntity.getTimeOfDay().displayName.translate());
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        if (perspective != null) {
            WorldEntity worldEntity = perspective.getWorldEntity();
            tooltips.add(Localization.translate("ui", "currenttime", "time", worldEntity.getDayTimeReadable()));
            tooltips.add(worldEntity.getTimeOfDay().displayName.translate());
        }
        return tooltips;
    }
}

