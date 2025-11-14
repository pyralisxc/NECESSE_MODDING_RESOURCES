/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class SpiderThroneObject
extends StaticMultiObject {
    public SpiderThroneObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "spiderthrone");
        this.stackSize = 1;
        this.rarity = Item.Rarity.LEGENDARY;
        this.mapColor = new Color(130, 105, 52);
        this.objectHealth = 500;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }

    public static int[] registerSpiderThrone() {
        int[] ids = new int[8];
        Rectangle collision = new Rectangle(0, 0, 128, 64);
        ids[0] = ObjectRegistry.registerObject("spiderthrone", new SpiderThroneObject(0, 0, 4, 2, ids, collision), 0.0f, true);
        ids[1] = ObjectRegistry.registerObject("spiderthrone2", new SpiderThroneObject(1, 0, 4, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("spiderthrone3", new SpiderThroneObject(2, 0, 4, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("spiderthrone4", new SpiderThroneObject(3, 0, 4, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject("spiderthrone5", new SpiderThroneObject(0, 1, 4, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject("spiderthrone6", new SpiderThroneObject(1, 1, 4, 2, ids, collision), 0.0f, false);
        ids[6] = ObjectRegistry.registerObject("spiderthrone7", new SpiderThroneObject(2, 1, 4, 2, ids, collision), 0.0f, false);
        ids[7] = ObjectRegistry.registerObject("spiderthrone8", new SpiderThroneObject(3, 1, 4, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

