/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.DecorationHolderInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.TorchHolderInterface;
import necesse.level.gameObject.furniture.DinnerTable2Object;
import necesse.level.gameObject.furniture.TableObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class DinnerTableObject
extends TableObject
implements TorchHolderInterface,
DecorationHolderInterface {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected DinnerTableObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32), mapColor);
        this.textureName = textureName;
        this.toolType = toolType;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
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
        return new MultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public DecorDrawOffset getTorchDrawOffset(Level level, int tileX, int tileY) {
        return new DecorDrawOffset(0, -18, 20, true);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 6, y * 32, 20, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 20, 26);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 0) {
            options.add(texture.initDraw().sprite(3, 3, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(0, 1, 32, 64).light(light).pos(drawX, drawY - 32));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(2, 1, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(2, 2, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(1, 0, 32, 64).light(light).pos(drawX, drawY - 32));
        }
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
        if (rotation == 0) {
            texture.initDraw().sprite(3, 1, 32).alpha(alpha).draw(drawX, drawY - 64);
            texture.initDraw().sprite(3, 2, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(3, 3, 32).alpha(alpha).draw(drawX, drawY);
        } else if (rotation == 1) {
            texture.initDraw().sprite(0, 1, 64).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 2) {
            texture.initDraw().sprite(2, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(2, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(2, 3, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            texture.initDraw().sprite(0, 0, 64).alpha(alpha).draw(drawX - 32, drawY - 32);
        }
    }

    public static int[] registerDinnerTable(String stringID, String textureName, ToolType toolType, Color mapColor, float brokerValue, String ... category) {
        int i2;
        DinnerTableObject obj1 = new DinnerTableObject(textureName, toolType, mapColor, category);
        DinnerTable2Object obj2 = new DinnerTable2Object(textureName, toolType, mapColor, category);
        int i1 = ObjectRegistry.registerObject(stringID, obj1, brokerValue, true);
        obj1.counterID = i2 = ObjectRegistry.registerObject(stringID + "2", obj2, 0.0f, false);
        obj2.counterID = i1;
        return new int[]{i1, i2};
    }

    public static int[] registerDinnerTable(String stringID, String textureName, Color mapColor, float brokerValue) {
        return DinnerTableObject.registerDinnerTable(stringID, textureName, ToolType.ALL, mapColor, brokerValue, new String[0]);
    }

    public static int[] registerDinnerTable(String stringID, String textureName, Color mapColor, float brokerValue, String ... category) {
        return DinnerTableObject.registerDinnerTable(stringID, textureName, ToolType.ALL, mapColor, brokerValue, category);
    }

    @Override
    public DecorDrawOffset getDecorationDrawOffset(Level level, int tileX, int tileY, GameObject decoration) {
        byte objectRotation = level.getObjectRotation(tileX, tileY);
        switch (objectRotation) {
            case 0: {
                return new DecorDrawOffset(0, -20, 20, true);
            }
            case 1: {
                return new DecorDrawOffset(decoration.isMultiTile() ? 0 : 2, -18, 20, true);
            }
            case 2: {
                return new DecorDrawOffset(0, -14, 20, true);
            }
            case 3: {
                return new DecorDrawOffset(decoration.isMultiTile() ? 0 : -2, -18, 20, true);
            }
        }
        return new DecorDrawOffset(0, -18, 20, true);
    }

    @Override
    public boolean canPlaceDecoration(Level level, int tileX, int tileY) {
        return !this.isTilePlaceOccupied(level, ObjectLayerRegistry.FENCE_AND_TABLE_DECOR, tileX, tileY, true);
    }

    @Override
    public Dimension getMaxDecorationSize(Level level, int tileX, int tileY) {
        return new Dimension(28, 28);
    }
}

