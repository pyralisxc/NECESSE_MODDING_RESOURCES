/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.StaticItemAttackSlot;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.ItemAttackAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;

public class ItemAttackerChaserAINode<T extends ItemAttackerMob>
extends DecoratorAINode<T> {
    public InventoryItem lastWeaponItem;
    public InventoryItem defaultWeaponItem;
    public String currentTargetKey = "currentTarget";

    public ItemAttackerChaserAINode(InventoryItem defaultWeaponItem) {
        super(null);
        this.defaultWeaponItem = defaultWeaponItem;
    }

    public ItemAttackerChaserAINode() {
        this(new InventoryItem("woodsword"));
    }

    @Override
    public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        super.onRootSet(root, mob, blackboard);
        blackboard.onGlobalTick(event -> {
            AttackHandler attackHandler = mob.getAttackHandler();
            if (attackHandler != null) {
                Mob currentTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
                attackHandler.setItemAttackerTarget(currentTarget);
            }
        });
    }

    public ItemAttackSlot getAttackSlot(T mob) {
        return ((ItemAttackerMob)mob).getCurrentSelectedAttackSlot();
    }

    @Override
    public AINodeResult tickChild(AINode<T> child, T mob, Blackboard<T> blackboard) {
        InventoryItem nextWeaponItem;
        ItemAttackSlot nextSlot;
        boolean shouldUpdate = false;
        ItemAttackSlot slot = this.getAttackSlot(mob);
        if (slot != null) {
            InventoryItem item = slot.getItem();
            if (item != null && item.item instanceof ItemAttackerWeaponItem) {
                nextSlot = slot;
                nextWeaponItem = item;
            } else {
                nextSlot = new StaticItemAttackSlot(this.defaultWeaponItem);
                nextWeaponItem = this.defaultWeaponItem;
            }
        } else {
            nextSlot = new StaticItemAttackSlot(this.defaultWeaponItem);
            nextWeaponItem = this.defaultWeaponItem;
        }
        if (nextWeaponItem == null && this.lastWeaponItem != null) {
            shouldUpdate = true;
        } else if (this.lastWeaponItem == null || nextWeaponItem.item != this.lastWeaponItem.item || ((ItemAttackerWeaponItem)((Object)nextWeaponItem.item)).shouldUpdateItemAttackerChaserAI((ItemAttackerMob)mob, this.lastWeaponItem, nextWeaponItem)) {
            shouldUpdate = true;
        }
        if (shouldUpdate) {
            this.lastWeaponItem = nextWeaponItem;
            child = this.getWeaponAI(mob, nextSlot, this.lastWeaponItem);
            this.setChild(child);
            this.runOnChaserUpdatedEvent();
        }
        if (child == null) {
            return AINodeResult.FAILURE;
        }
        return child.tick(mob, blackboard);
    }

    public void runOnChaserUpdatedEvent() {
        this.getBlackboard().submitEvent("itemAttackerUpdated", new AIEvent());
    }

    public void runOnAttackedEvent(ItemAttackerMob mob, InventoryItem item, int attackX, int attackY, int animAttack, GNDItemMap attackMap) {
        this.getBlackboard().submitEvent("itemAttack", new ItemAttackAIEvent(mob, item, attackX, attackY, animAttack, attackMap));
    }

    public ChaserAINode<T> getChaserAIIfExists() {
        AINode child = this.getChild();
        if (child instanceof ChaserAINode) {
            return (ChaserAINode)child;
        }
        return null;
    }

    public AINode<? super T> getWeaponAI(T mob, ItemAttackSlot slot, InventoryItem weapon) {
        if (weapon != null && weapon.item instanceof ItemAttackerWeaponItem) {
            return ((ItemAttackerWeaponItem)((Object)weapon.item)).getItemAttackerWeaponChaserAI(this, (ItemAttackerMob)mob, weapon, slot);
        }
        return null;
    }
}

