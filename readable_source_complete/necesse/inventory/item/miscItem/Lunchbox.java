/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.Iterator;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.SlotPriority;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodMatItem;
import necesse.level.maps.Level;

public class Lunchbox
extends PouchItem
implements AdventurePartyConsumableItem {
    public Lunchbox() {
        this.rarity = Item.Rarity.UNCOMMON;
        this.canEatFoodFromPouch = true;
        this.canUseBuffPotionsFromPouch = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "lunchboxtip1"));
        tooltips.add(Localization.translate("itemtooltip", "lunchboxtip2"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedfood", "items", (Object)this.getStoredItemAmounts(item)));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotationInv(attackProgress);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob instanceof HungerMob) {
            boolean hungerEnabled;
            HungerMob hungerMob = (HungerMob)((Object)attackerMob);
            boolean bl = hungerEnabled = level.getWorldSettings() == null || level.getWorldSettings().playerHunger();
            if (hungerMob.getHungerLevel() < 1.0f && hungerEnabled || FoodConsumableItem.doesMobHaveEmptyFoodSlot(attackerMob)) {
                Inventory internalInventory = this.getInternalInventory(item);
                boolean used = false;
                for (int i = 0; i < internalInventory.getSize(); ++i) {
                    FoodConsumableItem foodItem;
                    if (internalInventory.isSlotClear(i)) continue;
                    InventoryItem internalItem = internalInventory.getItem(i);
                    if (!internalItem.item.isFoodItem() || !(foodItem = (FoodConsumableItem)internalItem.item).consume(level, hungerMob, internalItem)) continue;
                    foodItem.playConsumeSound(level, attackerMob, internalItem);
                    internalInventory.setItem(i, internalItem);
                    used = true;
                    break;
                }
                if (used) {
                    this.saveInternalInventory(item, internalInventory);
                }
            }
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestItem(item.item);
    }

    @Override
    public boolean isValidAddItem(InventoryItem item) {
        if (item.item.isFoodItem()) {
            FoodConsumableItem foodItem = (FoodConsumableItem)item.item;
            return !foodItem.isDebuff;
        }
        return false;
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        return item != null && item.isFoodItem() || item instanceof FoodMatItem;
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return false;
    }

    @Override
    public int getInternalInventorySize() {
        return 10;
    }

    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        Inventory inventory = super.getInternalInventory(item);
        inventory.spoilRateModifier = 0.5f;
        return inventory;
    }

    @Override
    public boolean canAndShouldPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        return true;
    }

    @Override
    public InventoryItem onPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        Inventory inventory = this.getInternalInventory(item);
        Iterator<SlotPriority> iterator = AdventurePartyConsumableItem.getPartyPriorityList(level, mob, partyClient, inventory, purpose).iterator();
        if (iterator.hasNext()) {
            SlotPriority slotPriority = iterator.next();
            InventoryItem invItem = inventory.getItem(slotPriority.slot);
            AdventurePartyConsumableItem partyItem = (AdventurePartyConsumableItem)((Object)invItem.item);
            InventoryItem out = invItem.copy();
            partyItem.onPartyConsume(mob.getLevel(), mob, partyClient, invItem, purpose);
            if (invItem.getAmount() <= 0) {
                inventory.clearSlot(slotPriority.slot);
            }
            return out;
        }
        return null;
    }

    @Override
    public ComparableSequence<Integer> getPartyPriority(Level level, HumanMob mob, ServerClient partyClient, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return AdventurePartyConsumableItem.super.getPartyPriority(level, mob, partyClient, inventory, inventorySlot, item, purpose).beforeBy(-10000);
    }
}

