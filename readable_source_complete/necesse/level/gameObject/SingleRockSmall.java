/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SingleRockSmall
extends GameObject {
    protected RockObject type;
    protected int minDropAmount;
    protected int maxDropAmount;
    protected int placedDropAmount;
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public SingleRockSmall(RockObject type, String textureName, Color mapColor, int minDropAmount, int maxDropAmount, int placedDropAmount, String ... category) {
        super(new Rectangle(4, 16, 24, 12));
        this.type = type;
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.minDropAmount = minDropAmount;
        this.maxDropAmount = maxDropAmount;
        this.placedDropAmount = placedDropAmount;
        this.toolTier = type.toolTier;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnLiquid = true;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "landscaping", "rocksandores");
            this.setCraftingCategory("objects", "landscaping", "rocksandores");
        }
    }

    public SingleRockSmall(RockObject type, String textureName, Color mapColor, String ... category) {
        this(type, textureName, mapColor, 15, 25, 20, category);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "smallsinglerock", "rock", this.type.getNewLocalization());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        if (this.type.droppedStone != null && this.maxDropAmount > 0) {
            return new LootTable(LootItem.between(this.type.droppedStone, this.minDropAmount, this.maxDropAmount).splitItems(5));
        }
        return new LootTable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRandomYOffset(int tileX, int tileY) {
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            return (int)((this.drawRandom.seeded(SingleRockSmall.getTileSeed(tileX, tileY, 1)).nextFloat() * 2.0f - 1.0f) * 8.0f) - 4;
        }
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        Rectangle collision = super.getCollision(level, x, y, rotation);
        collision.y += this.getRandomYOffset(x, y);
        return collision;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sprite;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleRockSmall.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 32);
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite, 0, 32, texture.getHeight()).light(light).pos(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16 + randomYOffset;
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
        int sprite;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleRockSmall.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 32);
        }
        texture.initDraw().sprite(sprite, 0, 32, texture.getHeight()).light(light).alpha(alpha).draw(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return !level.isShore(x, y);
    }
}

