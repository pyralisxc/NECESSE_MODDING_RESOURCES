/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Rectangle;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;

public class FurnitureObject
extends GameObject
implements RoomFurniture {
    public String furnitureType = null;

    public FurnitureObject() {
        this.construct();
    }

    public FurnitureObject(Rectangle collision) {
        super(collision);
        this.construct();
    }

    protected void construct() {
        this.roomProperties.add("furniture");
        this.setItemCategory("objects", "furniture");
        this.setCraftingCategory("objects", "furniture");
        this.displayMapTooltip = true;
        this.replaceCategories.add("furniture");
        this.canReplaceCategories.add("furniture");
        this.canReplaceCategories.add("column");
        this.canReplaceCategories.add("torch");
    }

    @Override
    public String getFurnitureType() {
        return this.furnitureType;
    }

    @Override
    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }
}

