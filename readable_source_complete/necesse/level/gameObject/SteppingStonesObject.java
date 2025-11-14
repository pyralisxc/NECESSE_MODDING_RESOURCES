/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SteppingStonesObject
extends GameObject {
    protected String textureName;
    GameTexture texture;
    protected int yOffset = -3;
    protected final GameRandom drawRandom;

    public SteppingStonesObject(String textureName, Color mapColor, ToolType toolType, int collisionWidth, int collisionHeight) {
        super(new Rectangle((32 - collisionWidth) / 2, (32 - collisionHeight) / 2, collisionWidth, collisionHeight));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.isLightTransparent = true;
        this.stackSize = 500;
        this.drawRandom = new GameRandom();
        this.replaceRotations = false;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.canPlaceOnLiquid = true;
        this.canPlaceOnShore = true;
        this.overridesInLiquid = true;
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    public SteppingStonesObject(String textureName, Color mapColor, ToolType toolType) {
        this(textureName, mapColor, toolType, 0, 0);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips itemTooltips = super.getItemTooltips(item, perspective);
        itemTooltips.add(Localization.translate("itemtooltip", "waterplacetip"));
        return itemTooltips;
    }

    @Override
    public void loadTextures() {
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteRes = 32;
        int spritesWidth = this.texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(SteppingStonesObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        final TextureDrawOptionsEnd options = this.texture.initDraw().section(spriteX * spriteRes, spriteX * spriteRes + spriteRes, 0, 32).light(light).mirror(mirror, false).pos(drawX, drawY);
        tileList.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 0;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteRes = 32;
        int spritesWidth = this.texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(SteppingStonesObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        this.texture.initDraw().sprite(spriteX, 0, spriteRes).light(light).mirror(mirror, false).alpha(alpha).draw(drawX, drawY);
    }
}

