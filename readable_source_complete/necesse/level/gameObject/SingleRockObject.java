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
import necesse.engine.registries.ObjectRegistry;
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
import necesse.level.gameObject.SingleRockRObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class SingleRockObject
extends GameObject {
    protected RockObject type;
    protected int minDropAmount;
    protected int maxDropAmount;
    protected int placedDropAmount;
    protected SingleRockRObject counterObject;
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public static int[] registerSurfaceRock(RockObject type, String textureName, Color debrisColor, int minDropAmount, int maxDropAmount, int placedDropAmount, float brokerValue, boolean isObtainable, String ... category) {
        SingleRockObject o1 = new SingleRockObject(type, textureName, debrisColor, minDropAmount, maxDropAmount, placedDropAmount, category);
        int id1 = ObjectRegistry.registerObject(textureName, o1, brokerValue, isObtainable);
        SingleRockRObject o2 = new SingleRockRObject(o1);
        int id2 = ObjectRegistry.registerObject(textureName + "r", o2, 0.0f, false);
        o1.counterObject = o2;
        o2.counterObject = o1;
        return new int[]{id1, id2};
    }

    public static int[] registerSurfaceRock(RockObject type, String textureName, Color debrisColor, float brokerValue, boolean isObtainable, String ... category) {
        return SingleRockObject.registerSurfaceRock(type, textureName, debrisColor, 25, 35, 30, brokerValue, isObtainable, category);
    }

    protected SingleRockObject(RockObject type, String textureName, Color mapColor, int minDropAmount, int maxDropAmount, int placedDropAmount, String ... category) {
        super(new Rectangle(14, 14, 18, 10));
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

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "singlerock", "rock", this.type.getNewLocalization());
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, this.getID(), this.counterObject.getID());
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
            return (int)((this.drawRandom.seeded(SingleRockObject.getTileSeed(tileX, tileY, 1)).nextFloat() * 2.0f - 1.0f) * 8.0f) - 4;
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
            sprite = this.drawRandom.seeded(SingleRockObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 64);
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite * 2, 0, 32, texture.getHeight()).light(light).pos(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
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
            sprite = this.drawRandom.seeded(SingleRockObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 64);
        }
        texture.initDraw().sprite(sprite * 2, 0, 32, texture.getHeight()).light(light).alpha(alpha).draw(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
    }
}

