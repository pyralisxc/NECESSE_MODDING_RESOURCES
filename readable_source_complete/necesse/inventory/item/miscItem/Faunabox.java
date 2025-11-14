/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.level.maps.Level;

public class Faunabox
extends PouchItem {
    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestItem(item.item);
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        return item.getStringID().equals("honeybee");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return false;
    }

    @Override
    public int getInternalInventorySize() {
        return 10;
    }
}

