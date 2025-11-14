/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.util.GameRandom;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;

abstract class FireChaliceAbstractObject
extends GameObject {
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public FireChaliceAbstractObject(String textureName, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.lightLevel = 200;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "lighting");
        this.setCraftingCategory("objects", "lighting");
        this.roomProperties.add("lights");
        this.drawRandom = new GameRandom();
        this.displayMapTooltip = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    protected abstract void setCounterIDs(int var1, int var2, int var3, int var4);

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        return this.isActive(level, tileX, tileY) ? this.lightLevel : 0;
    }

    public boolean isActive(Level level, int x, int y) {
        byte rotation = level.getObjectRotation(x, y);
        return this.getMultiTile(rotation).streamIDs(x, y).noneMatch(c -> level.wireManager.isWireActiveAny(c.tileX, c.tileY));
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        Rectangle rect = this.getMultiTile(rotation).getTileRectangle(tileX, tileY);
        level.lightManager.updateStaticLight(rect.x, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1, true);
    }
}

