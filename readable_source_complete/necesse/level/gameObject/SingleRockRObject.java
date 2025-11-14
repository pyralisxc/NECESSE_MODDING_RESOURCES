/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

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
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class SingleRockRObject
extends GameObject {
    protected SingleRockObject counterObject;
    protected final GameRandom drawRandom;

    protected SingleRockRObject(SingleRockObject counterObject) {
        super(new Rectangle(0, 14, 18, 10));
        this.mapColor = counterObject.mapColor;
        this.toolTier = counterObject.toolTier;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnLiquid = true;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "singlerock", "rock", this.counterObject.type.getNewLocalization());
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(1, 0, 2, 1, false, this.counterObject.getID(), this.getID());
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRandomYOffset(int tileX, int tileY) {
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            return (int)((this.drawRandom.seeded(SingleRockRObject.getTileSeed(tileX - 1, tileY, 1)).nextFloat() * 2.0f - 1.0f) * 8.0f) - 4;
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
        GameTexture texture = this.counterObject.texture.getDamagedTexture(this, level, tileX, tileY);
        final int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleRockRObject.getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light).pos(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
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
        GameTexture texture = this.counterObject.texture.getDamagedTexture(0.0f);
        int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleRockRObject.getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }
        texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light).alpha(alpha).draw(drawX, (drawY += randomYOffset) - texture.getHeight() + 32);
    }
}

