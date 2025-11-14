/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class CarpetRObject
extends FurnitureObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    private int counterIDTopLeft;
    private int counterIDBotLeft;
    private int counterIDBotRight;

    public CarpetRObject(String textureName, ToolType toolType, Color mapColor) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(1, 0, 2, 2, true, this.counterIDTopLeft, this.getID(), this.counterIDBotLeft, this.counterIDBotRight);
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
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptionsEnd options = texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    protected void setCounterIDs(int topLeft, int botLeft, int botRight) {
        this.counterIDTopLeft = topLeft;
        this.counterIDBotLeft = botLeft;
        this.counterIDBotRight = botRight;
    }
}

