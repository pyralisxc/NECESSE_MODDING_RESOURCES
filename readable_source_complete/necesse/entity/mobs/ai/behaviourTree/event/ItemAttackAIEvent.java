/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public class ItemAttackAIEvent
extends AIEvent {
    public final ItemAttackerMob mob;
    public final InventoryItem item;
    public final int attackX;
    public final int attackY;
    public final int animAttack;
    public final GNDItemMap attackMap;

    public ItemAttackAIEvent(ItemAttackerMob mob, InventoryItem item, int attackX, int attackY, int animAttack, GNDItemMap attackMap) {
        this.mob = mob;
        this.item = item;
        this.attackX = attackX;
        this.attackY = attackY;
        this.animAttack = animAttack;
        this.attackMap = attackMap;
    }
}

