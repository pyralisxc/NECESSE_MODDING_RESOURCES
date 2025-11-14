/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;

abstract class PicnicBlanketExtraObject
extends GameObject {
    protected String textureName;
    protected ObjectDamagedTextureArray blanketTexture;
    protected ObjectDamagedTextureArray basketTexture;
    protected int counterIDLeft;
    protected int counterIDCenter;
    protected int counterIDRight;

    protected PicnicBlanketExtraObject(String textureName, ToolType toolType, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.displayMapTooltip = true;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    protected abstract void setCounterIDs(int var1, int var2, int var3);

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.blanketTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        try {
            this.basketTexture = ObjectDamagedTextureArray.loadAndApplyOverlayRaw(this, "objects/" + this.textureName + "basket");
        }
        catch (FileNotFoundException e) {
            this.basketTexture = null;
        }
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        return new Rectangle(x * 32, y * 32, 0, 0);
    }
}

