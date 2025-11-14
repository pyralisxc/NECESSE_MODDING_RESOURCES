/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.FenceObjectInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.TorchHolderInterface;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionType;

public class FenceObject
extends GameObject
implements FenceObjectInterface,
TorchHolderInterface {
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected LinkedList<Integer> connections = new LinkedList();
    public int torchYOffset;

    public FenceObject(String textureName, Color mapColor, int collisionWidth, int collisionHeight, int torchYOffset) {
        super(new Rectangle((32 - collisionWidth) / 2, (32 - collisionHeight) / 2, collisionWidth, collisionHeight));
        this.setItemCategory("objects", "fencesandgates");
        this.setCraftingCategory("objects", "fencesandgates");
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.torchYOffset = torchYOffset;
        this.isFence = true;
        this.toolType = ToolType.ALL;
        this.regionType = RegionType.FENCE;
        this.isLightTransparent = true;
        this.canPlaceOnShore = true;
        this.stackSize = 500;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
        this.replaceCategories.add("fencegate");
        this.canReplaceCategories.add("fencegate");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("door");
        this.replaceRotations = false;
    }

    public FenceObject(String textureName, Color mapColor, int collisionWidth, int collisionHeight) {
        this(textureName, mapColor, collisionWidth, collisionHeight, -24);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public boolean attachesToObject(GameObject me, Level level, int tileX, int tileY, LevelObject other) {
        return other.object.isWall || other.object.isRock || other.object.getID() == me.getID() || this.connections.contains(other.object.getID());
    }

    @Override
    public DecorDrawOffset getTorchDrawOffset(Level level, int tileX, int tileY) {
        return new DecorDrawOffset(0, this.torchYOffset, 16, false);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        LevelObject topObject = level.getLevelObject(tileX, tileY - 1);
        LevelObject botObject = level.getLevelObject(tileX, tileY + 1);
        LevelObject leftObject = level.getLevelObject(tileX - 1, tileY);
        LevelObject rightObject = level.getLevelObject(tileX + 1, tileY);
        if (this.attachesToObject(this, level, tileX, tileY, topObject)) {
            draws.addSprite(1, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
            if (!topObject.object.isFence || !((FenceObjectInterface)((Object)topObject.object)).attachesToObject(topObject.object, topObject.level, topObject.tileX, topObject.tileY, level.getLevelObject(tileX, tileY))) {
                draws.addSprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - 32 + 8);
            }
        }
        draws.addSprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        if (this.attachesToObject(this, level, tileX, tileY, botObject)) {
            draws.addSprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        }
        if (this.attachesToObject(this, level, tileX, tileY, leftObject)) {
            draws.addSprite(3, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        }
        if (this.attachesToObject(this, level, tileX, tileY, rightObject)) {
            draws.addSprite(4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 14;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        LevelObject topObject = level.getLevelObject(tileX, tileY - 1);
        LevelObject botObject = level.getLevelObject(tileX, tileY + 1);
        LevelObject leftObject = level.getLevelObject(tileX - 1, tileY);
        LevelObject rightObject = level.getLevelObject(tileX + 1, tileY);
        texture.initDraw().sprite(0, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        if (this.attachesToObject(this, level, tileX, tileY, topObject)) {
            texture.initDraw().sprite(1, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
            if (!topObject.object.isFence || !((FenceObjectInterface)((Object)topObject.object)).attachesToObject(topObject.object, topObject.level, topObject.tileX, topObject.tileY, level.getLevelObject(tileX, tileY))) {
                texture.initDraw().sprite(2, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 32 + 8);
            }
        }
        if (this.attachesToObject(this, level, tileX, tileY, botObject)) {
            texture.initDraw().sprite(2, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        }
        if (this.attachesToObject(this, level, tileX, tileY, leftObject)) {
            texture.initDraw().sprite(3, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        }
        if (this.attachesToObject(this, level, tileX, tileY, rightObject)) {
            texture.initDraw().sprite(4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        }
    }

    @Override
    public List<Rectangle> getCollisions(Level level, int x, int y, int rotation) {
        LevelObject topObject = level.getLevelObject(x, y - 1);
        LevelObject botObject = level.getLevelObject(x, y + 1);
        LevelObject leftObject = level.getLevelObject(x - 1, y);
        LevelObject rightObject = level.getLevelObject(x + 1, y);
        LinkedList<Rectangle> collisions = new LinkedList<Rectangle>();
        collisions.add(new Rectangle(x * 32 + this.collision.x, y * 32 + this.collision.y, this.collision.width, this.collision.height));
        if (this.attachesToObject(this, level, x, y, topObject)) {
            collisions.add(new Rectangle(x * 32 + this.collision.x, y * 32, this.collision.width, 16 - this.collision.height / 2));
        }
        if (this.attachesToObject(this, level, x, y, botObject)) {
            collisions.add(new Rectangle(x * 32 + this.collision.x, y * 32 + 16 + this.collision.height / 2, this.collision.width, 16 - this.collision.height / 2));
        }
        if (this.attachesToObject(this, level, x, y, leftObject)) {
            collisions.add(new Rectangle(x * 32, y * 32 + this.collision.y, 16 - this.collision.width / 2, this.collision.height));
        }
        if (this.attachesToObject(this, level, x, y, rightObject)) {
            collisions.add(new Rectangle(x * 32 + 16 + this.collision.width / 2, y * 32 + this.collision.y, 16 - this.collision.width / 2, this.collision.height));
        }
        return collisions;
    }
}

