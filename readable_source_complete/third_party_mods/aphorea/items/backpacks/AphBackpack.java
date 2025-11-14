/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.ComparableSequence
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.InventoryItemsRemoved
 *  necesse.inventory.InventorySlot
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Type
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 *  necesse.inventory.item.miscItem.PouchItem
 *  necesse.inventory.recipe.Ingredient
 *  necesse.inventory.recipe.IngredientCounter
 *  necesse.inventory.recipe.IngredientUser
 *  necesse.level.maps.Level
 */
package aphorea.items.backpacks;

import java.util.Collection;
import necesse.engine.localization.Localization;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.level.maps.Level;

public abstract class AphBackpack
extends PouchItem {
    public AphBackpack() {
        this.canUseHealthPotionsFromPouch = true;
        this.canUseManaPotionsFromPouch = true;
        this.canEatFoodFromPouch = true;
        this.canUseBuffPotionsFromPouch = true;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"backpackslots", (String)"slots", (Object)this.getInternalInventorySize()));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"backpack"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"backpackcraft"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"rclickinvopentip"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stored", (String)"items", (Object)this.getStoredItemAmounts(item)));
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public boolean isValidPouchItem(InventoryItem item) {
        if (item == null || item.item == null) {
            return false;
        }
        return this.isValidRequestItem(item.item);
    }

    public boolean isValidRequestItem(Item item) {
        if (item == null) {
            return false;
        }
        return !(item instanceof InternalInventoryItemInterface);
    }

    public boolean isValidRequestType(Item.Type type) {
        return false;
    }

    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, String purpose) {
        int amount = super.getInventoryAmount(level, player, item, requestType, purpose);
        if (this.isValidRequestItem(item.item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            amount += internalInventory.getAmount(level, player, requestType, purpose);
        }
        return amount;
    }

    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, String purpose) {
        Inventory internalInventory;
        Item firstItem;
        if (this.isValidRequestItem(item.item) && (firstItem = (internalInventory = this.getInternalInventory(item)).getFirstItem(level, player, requestType, purpose)) != null) {
            return firstItem;
        }
        return super.getInventoryFirstItem(level, player, item, requestType, purpose);
    }

    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, int amount, String purpose) {
        Inventory internalInventory;
        int removed = 0;
        if (this.isValidRequestItem(item.item) && (removed = (internalInventory = this.getInternalInventory(item)).removeItems(level, player, requestType, amount, purpose)) > 0) {
            this.saveInternalInventory(item, internalInventory);
        }
        return removed < amount ? removed + super.removeInventoryAmount(level, player, item, requestType, amount, purpose) : removed;
    }

    public boolean ignoreCombineStackLimit(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        return false;
    }

    public ComparableSequence<Integer> getInventoryAddPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, InventoryItem input, String purpose) {
        boolean inInventory = inventory.streamSlots().anyMatch(slot -> slot != null && slot.getItem() != null && slot.getItem().item.getID() == item.item.getID());
        if (inInventory) {
            return new ComparableSequence((Comparable)Integer.valueOf(inventorySlot));
        }
        return super.getInventoryAddPriority(level, player, inventory, inventorySlot, item, input, purpose);
    }

    public boolean canBeUsedForCrafting(InventoryItem item) {
        Inventory internalInventory = this.getInternalInventory(item);
        return internalInventory.streamSlots().allMatch(InventorySlot::isSlotClear);
    }

    public void countIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientCounter handler) {
        if (this.canBeUsedForCrafting(item)) {
            super.countIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
        }
    }

    public void useIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientUser handler) {
        if (this.canBeUsedForCrafting(item)) {
            super.useIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
        }
    }

    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Inventory inventory, int inventorySlot, Ingredient ingredient, int amount, Collection<InventoryItemsRemoved> collect) {
        return this.canBeUsedForCrafting(item) ? super.removeInventoryAmount(level, player, item, inventory, inventorySlot, ingredient, amount, collect) : 0;
    }

    public int getStackSize() {
        return 1;
    }
}

