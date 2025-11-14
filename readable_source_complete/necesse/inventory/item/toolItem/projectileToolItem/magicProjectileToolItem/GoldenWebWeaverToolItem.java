/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.WebWeaverToolItem;
import necesse.level.maps.Level;

public class GoldenWebWeaverToolItem
extends WebWeaverToolItem {
    public GoldenWebWeaverToolItem() {
        super(null);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Golden Web Weaver");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }
}

