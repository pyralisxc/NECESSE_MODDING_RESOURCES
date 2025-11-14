/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.PicnicBlanketExtraObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class PicnicBlanket3Object
extends PicnicBlanketExtraObject {
    protected PicnicBlanket3Object(String textureName, ToolType toolType, Color mapColor) {
        super(textureName, toolType, mapColor);
    }

    @Override
    protected void setCounterIDs(int leftID, int centerID, int rightID) {
        this.counterIDLeft = leftID;
        this.counterIDCenter = centerID;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(2, 0, 3, 1, rotation, false, this.counterIDLeft, this.counterIDCenter, this.getID());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.blanketTexture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions blanketOptions = new SharedTextureDrawOptions(texture);
        if (rotation == 0) {
            blanketOptions.addSprite(2, 0, texture.getWidth() / 6, texture.getHeight()).light(light).pos(drawX, drawY - 32);
        } else if (rotation == 1) {
            blanketOptions.addSprite(1, 2, texture.getWidth() / 2, texture.getHeight() / 3).light(light).pos(drawX - 32, drawY);
        } else if (rotation == 2) {
            blanketOptions.addSprite(2, 0, texture.getWidth() / 6, texture.getHeight()).light(light).pos(drawX + 64, drawY - 32);
        } else {
            blanketOptions.addSprite(1, 2, texture.getWidth() / 2, texture.getHeight() / 3).light(light).pos(drawX - 32, drawY + 64);
        }
        tileList.add(new Drawable(){

            @Override
            public void draw(TickManager tickManager) {
                blanketOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }
}

