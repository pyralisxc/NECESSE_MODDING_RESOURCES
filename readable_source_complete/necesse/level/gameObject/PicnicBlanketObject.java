/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.PicnicBlanket2Object;
import necesse.level.gameObject.PicnicBlanket3Object;
import necesse.level.gameObject.PicnicBlanketExtraObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class PicnicBlanketObject
extends PicnicBlanketExtraObject {
    public static int[] registerPicnicBlanketObject(String stringID, String textureName, ToolType toolType, Color mapColor, float brokerValue, boolean isObtainable) {
        PicnicBlanketObject obj1 = new PicnicBlanketObject(textureName, toolType, mapColor);
        PicnicBlanket2Object obj2 = new PicnicBlanket2Object(textureName, toolType, mapColor);
        PicnicBlanket3Object obj3 = new PicnicBlanket3Object(textureName, toolType, mapColor);
        int i1 = ObjectRegistry.registerObject(stringID, obj1, brokerValue, isObtainable);
        int i2 = ObjectRegistry.registerObject(stringID + "2", obj2, 0.0f, false);
        int i3 = ObjectRegistry.registerObject(stringID + "3", obj3, 0.0f, false);
        obj1.setCounterIDs(i1, i2, i3);
        obj2.setCounterIDs(i1, i2, i3);
        obj3.setCounterIDs(i1, i2, i3);
        return new int[]{i1, i2, i3};
    }

    protected PicnicBlanketObject(String textureName, ToolType toolType, Color mapColor) {
        super(textureName, toolType, mapColor);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable();
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 3, 1, rotation, true, this.getID(), this.counterIDCenter, this.counterIDRight);
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
            blanketOptions.addSprite(0, 0, texture.getWidth() / 6, texture.getHeight()).light(light).pos(drawX, drawY - 32);
        } else if (rotation == 1) {
            blanketOptions.addSprite(1, 0, texture.getWidth() / 2, texture.getHeight() / 3).light(light).pos(drawX - 32, drawY);
        } else if (rotation == 2) {
            blanketOptions.addSprite(0, 0, texture.getWidth() / 6, texture.getHeight()).light(light).pos(drawX - 64, drawY - 32);
        } else if (rotation == 3) {
            blanketOptions.addSprite(1, 0, texture.getWidth() / 2, texture.getHeight() / 3).light(light).pos(drawX - 32, drawY - 64);
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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.blanketTexture.getDamagedTexture(0.0f);
        GameTexture basketTexture = this.basketTexture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            texture.initDraw().sprite(0, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 32);
            basketTexture.initDraw().sprite(0, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            texture.initDraw().sprite(1, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 32, drawY);
            basketTexture.initDraw().sprite(1, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 32, drawY);
        } else if (rotation == 2) {
            texture.initDraw().sprite(0, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 64, drawY - 32);
            basketTexture.initDraw().sprite(0, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 64, drawY - 32);
        } else {
            texture.initDraw().sprite(1, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 32, drawY - 64);
            basketTexture.initDraw().sprite(1, 0, texture.getWidth() / 2, texture.getHeight()).alpha(alpha).draw(drawX - 32, drawY - 64);
        }
    }

    @Override
    protected void setCounterIDs(int leftID, int centerID, int rightID) {
        this.counterIDCenter = centerID;
        this.counterIDRight = rightID;
    }
}

