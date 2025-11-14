/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture.doubleBed;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedHeadBaseObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SidedRotationMultiTile;

class DoubleBed2HeadObject
extends DoubleBedHeadBaseObject {
    protected int head1ID;
    protected int foot1ID;
    protected int foot2ID;

    protected DoubleBed2HeadObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(textureName, toolType, mapColor);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SidedRotationMultiTile(0, 1, 2, 2, rotation, false, this.foot2ID, this.foot1ID, this.getID(), this.head1ID);
    }

    public void modifyHumanDrawOptions(Level level, int tileX, int tileY, HumanDrawOptions options) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        options.dir(rotation).sprite(0, (int)rotation);
        options.blinking(true);
        switch (rotation) {
            case 0: {
                options.drawOffset(0, 6);
                options.mask(this.maskTextures[0], -16, 39);
                break;
            }
            case 1: {
                int yOffset = -4;
                options.rotate(-90.0f, 32, 32).drawOffset(6, 9 + yOffset);
                options.mask(this.maskTextures[1], 10 - yOffset, -14);
                break;
            }
            case 2: {
                options.drawOffset(0, 7);
                options.mask(this.maskTextures[2], 16, 6);
                break;
            }
            case 3: {
                int yOffset = -6;
                options.rotate(90.0f, 32, 32).drawOffset(-6, 9 + yOffset);
                options.mask(this.maskTextures[3], 10 - yOffset, -14);
                break;
            }
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 2, y * 32, 30, 30);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 30, 26);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32, y * 32 + 6, 30, 26);
        }
        return new Rectangle(x * 32, y * 32, 30, 30);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        List<ObjectUserMob> users = this.getObjectUsers(level, tileX, tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture baseTexture = this.baseTexture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(baseTexture.initDraw().sprite(2, 5, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(baseTexture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(baseTexture.initDraw().sprite(0, 4, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(baseTexture.initDraw().sprite(3, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(baseTexture.initDraw().sprite(3, 1, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(baseTexture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY));
        }
        for (ObjectUserMob user : users) {
            Point offset = this.getMobPosSleepOffset(level, tileX, tileY);
            options.add(user.getUserDrawOptions(level, tileX * 32 + offset.x, tileY * 32 + offset.y, tickManager, camera, perspective, humanOptions -> {
                if (humanOptions != null) {
                    this.modifyHumanDrawOptions(level, tileX, tileY, (HumanDrawOptions)humanOptions);
                }
            }));
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
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
        GameTexture baseTexture = this.baseTexture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            baseTexture.initDraw().sprite(2, 5, 32).alpha(alpha).draw(drawX, drawY);
        } else if (rotation == 1) {
            baseTexture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX, drawY - 32);
            baseTexture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX, drawY);
        } else if (rotation == 2) {
            baseTexture.initDraw().sprite(3, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            baseTexture.initDraw().sprite(3, 1, 32).alpha(alpha).draw(drawX, drawY);
        } else {
            baseTexture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY);
        }
    }
}

