/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;

public class MinecartObjectItem
extends ObjectItem {
    public MinecartObjectItem(GameObject object) {
        super(object);
        this.attackAnimTime.setBaseValue(100);
    }
}

