/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.level.maps.Level;

public abstract class PouchItem
extends Item
implements InternalInventoryItemInterface,
TickItem {
    public HashSet<String> combinePurposes = new HashSet();
    public boolean isCombinePurposesBlacklist = false;
    public HashSet<String> insertPurposes = new HashSet();
    public boolean isInsertPurposesBlacklist = false;
    public HashSet<String> requestPurposes = new HashSet();
    public boolean isRequestPurposeBlacklist = true;
    public HashSet<String> pickupDisabledPurposeIgnores = new HashSet();
    public boolean drawStoredItems = true;
    public boolean canUseHealthPotionsFromPouch = false;
    public boolean canUseManaPotionsFromPouch = false;
    public boolean canEatFoodFromPouch = false;
    public boolean canUseBuffPotionsFromPouch = false;
    public boolean allowRestockFrom = true;
    protected GameTexture fullTexture;

    protected boolean isValidPurpose(HashSet<String> purposes, boolean isBlacklist, String purpose, boolean isPickupDisabled) {
        if (isPickupDisabled && this.pickupDisabledPurposeIgnores.contains(purpose)) {
            return isBlacklist;
        }
        return isBlacklist != purposes.contains(purpose);
    }

    public PouchItem() {
        super(1);
        this.setItemCategory("misc", "pouches");
        this.combinePurposes.add("leftclick");
        this.combinePurposes.add("leftclickinv");
        this.combinePurposes.add("rightclick");
        this.combinePurposes.add("lootall");
        this.combinePurposes.add("restockfrom");
        this.insertPurposes.add("itempickup");
        this.insertPurposes.add("lootall");
        this.insertPurposes.add("restockfrom");
        this.requestPurposes.add("quickstackto");
        this.pickupDisabledPurposeIgnores.add("itempickup");
        this.pickupDisabledPurposeIgnores.add("lootall");
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
    }

    @Override
    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, Consumer<InventoryItem> setItem) {
        this.tickInternalInventory(item, clock, state, entity, tileEntity, worldSettings);
    }

    public abstract boolean isValidPouchItem(InventoryItem var1);

    public boolean isValidAddItem(InventoryItem item) {
        return this.isValidPouchItem(item);
    }

    public abstract boolean isValidRequestItem(Item var1);

    public abstract boolean isValidRequestType(Item.Type var1);

    protected int getStoredItemAmounts(InventoryItem item) {
        Inventory internalInventory = this.getInternalInventory(item);
        int count = 0;
        for (int i = 0; i < internalInventory.getSize(); ++i) {
            count += internalInventory.getAmount(i);
        }
        return count;
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        int itemsAmount;
        super.draw(item, perspective, x, y, inInventory);
        if (this.drawStoredItems && ((itemsAmount = this.getStoredItemAmounts(item)) > 0 || inInventory)) {
            if (itemsAmount > 999) {
                itemsAmount = 999;
            }
            String amountString = String.valueOf(itemsAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
        }
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (this.fullTexture != null) {
            Inventory internalInventory = this.getInternalInventory(item);
            for (int i = 0; i < internalInventory.getSize(); ++i) {
                if (internalInventory.isSlotClear(i)) continue;
                return new GameSprite(this.fullTexture);
            }
        }
        return super.getItemSprite(item, perspective);
    }

    @Override
    protected void loadItemTextures() {
        super.loadItemTextures();
        try {
            this.fullTexture = GameTexture.fromFileRaw("items/" + this.getStringID() + "_full");
        }
        catch (FileNotFoundException e) {
            this.fullTexture = null;
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        String pouchName = this.getPouchName(item);
        if (pouchName != null) {
            return new StaticMessage(pouchName);
        }
        return super.getLocalization(item);
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        float value = super.getBrokerValue(item);
        Inventory internalInventory = this.getInternalInventory(item);
        for (int i = 0; i < internalInventory.getSize(); ++i) {
            if (internalInventory.isSlotClear(i)) continue;
            value += internalInventory.getItem(i).getBrokerValue();
        }
        return value;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    this.openContainer(client, playerSlot);
                }
                return new ContainerActionResult(-1002911334);
            }
            return new ContainerActionResult(208675834, Localization.translate("itemtooltip", "rclickinvopenerror"));
        };
    }

    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ITEM_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent(this, inventorySlot));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (them == null) {
            return false;
        }
        return this.isSameItem(level, me, them, purpose) || this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, purpose, this.isPickupDisabled(me)) && this.isValidPouchItem(them);
    }

    @Override
    public boolean ignoreCombineStackLimit(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (them == null) {
            return false;
        }
        return this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, purpose, this.isPickupDisabled(me)) && this.isValidPouchItem(them);
    }

    @Override
    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        boolean valid = false;
        if (this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, purpose, this.isPickupDisabled(me))) {
            valid = purpose.equals("lootall") ? this.isValidAddItem(other) : this.isValidPouchItem(other);
        }
        if (valid) {
            Inventory internalInventory = this.getInternalInventory(me);
            if (purpose.equals("restockfrom")) {
                if (internalInventory.restockFrom(level, player, other, 0, internalInventory.getSize(), purpose, false, addConsumer)) {
                    this.saveInternalInventory(me, internalInventory);
                    return true;
                }
                return false;
            }
            int startAmount = Math.min(amount, other.getAmount());
            InventoryItem copy = other.copy(startAmount);
            internalInventory.addItem(level, player, copy, "pouchinsert", addConsumer);
            if (copy.getAmount() != startAmount) {
                if (copy.isNew()) {
                    me.setNew(true);
                }
                int diff = startAmount - copy.getAmount();
                other.setAmount(other.getAmount() - diff);
                this.saveInternalInventory(me, internalInventory);
                return true;
            }
            return false;
        }
        return super.onCombine(level, player, myInventory, mySlot, me, other, maxStackSize, amount, combineIsNew, purpose, addConsumer);
    }

    @Override
    public ComparableSequence<Integer> getInventoryAddPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, InventoryItem input, String purpose) {
        ComparableSequence<Integer> last = super.getInventoryAddPriority(level, player, inventory, inventorySlot, item, input, purpose);
        if (this.isValidAddItem(input) && (this.isValidPurpose(this.insertPurposes, this.isInsertPurposesBlacklist, purpose, this.isPickupDisabled(item)) || this.allowRestockFrom && purpose.equals("restockfrom"))) {
            last = last.beforeBy(-10000);
            Inventory internalInventory = this.getInternalInventory(item);
            ArrayList<SlotPriority> addList = internalInventory.getPriorityAddList(level, player, input, 0, internalInventory.getSize() - 1, purpose);
            for (SlotPriority slotPriority : addList) {
                boolean isValid = internalInventory.isItemValid(slotPriority.slot, input);
                int stackLimit = internalInventory.getItemStackLimit(slotPriority.slot, input);
                InventoryItem invItem = internalInventory.getItem(slotPriority.slot);
                if (invItem.item.inventoryCanAddItem(level, player, invItem, input, purpose, isValid, stackLimit) <= 0) continue;
                return last.thenBy(inventorySlot).thenBy((Integer)((Object)addList.get((int)0).comparable));
            }
            return last.thenBy(10000);
        }
        return last;
    }

    @Override
    public ComparableSequence<Integer> getInventoryPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose).beforeBy(-10000);
    }

    @Override
    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, String purpose) {
        int amount = super.getInventoryAmount(level, player, item, requestItem, purpose);
        if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, purpose, this.isPickupDisabled(item)) && this.isValidRequestItem(requestItem)) {
            Inventory internalInventory = this.getInternalInventory(item);
            amount += internalInventory.getAmount(level, player, requestItem, purpose);
        }
        return amount;
    }

    @Override
    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, String purpose) {
        int amount = super.getInventoryAmount(level, player, item, requestType, purpose);
        if (this.isValidRequestType(requestType)) {
            Inventory internalInventory = this.getInternalInventory(item);
            amount += internalInventory.getAmount(level, player, requestType, purpose);
        }
        return amount;
    }

    @Override
    public void countIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientCounter handler) {
        if (purpose.equals("buy") || purpose.equals("crafting")) {
            this.getInternalInventory(item).countIngredientAmount(level, player, purpose, handler);
        }
        super.countIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
    }

    @Override
    public void useIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientUser handler) {
        if (purpose.equals("buy") || purpose.equals("crafting")) {
            this.getInternalInventory(item).useIngredientAmount(level, player, purpose, handler);
        }
        super.useIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
    }

    @Override
    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Item[] requestItems, String purpose) {
        Inventory internalInventory;
        Item firstItem;
        if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, purpose, this.isPickupDisabled(item)) && Arrays.stream(requestItems).anyMatch(this::isValidRequestItem) && (firstItem = (internalInventory = this.getInternalInventory(item)).getFirstItem(level, player, requestItems, purpose)) != null) {
            return firstItem;
        }
        return super.getInventoryFirstItem(level, player, item, requestItems, purpose);
    }

    @Override
    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, String purpose) {
        Inventory internalInventory;
        Item firstItem;
        if (this.isValidRequestType(requestType) && (firstItem = (internalInventory = this.getInternalInventory(item)).getFirstItem(level, player, requestType, purpose)) != null) {
            return firstItem;
        }
        return super.getInventoryFirstItem(level, player, item, requestType, purpose);
    }

    @Override
    public boolean inventoryAddItem(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem input, String purpose, boolean isValid, int stackLimit, boolean combineIsValid, InventoryAddConsumer addConsumer) {
        Inventory internalInventory;
        boolean success;
        if (this.isValidAddItem(input) && this.isValidPurpose(this.insertPurposes, this.isInsertPurposesBlacklist, purpose, this.isPickupDisabled(me)) && (success = (internalInventory = this.getInternalInventory(me)).addItem(level, player, input, purpose, addConsumer))) {
            if (input.isNew()) {
                me.setNew(true);
            }
            this.saveInternalInventory(me, internalInventory);
            return true;
        }
        return super.inventoryAddItem(level, player, myInventory, mySlot, me, input, purpose, isValid, stackLimit, combineIsValid, addConsumer);
    }

    @Override
    public int inventoryCanAddItem(Level level, PlayerMob player, InventoryItem item, InventoryItem input, String purpose, boolean isValid, int stackLimit) {
        if (this.isValidAddItem(input)) {
            Inventory internalInventory = this.getInternalInventory(item);
            return internalInventory.canAddItem(level, player, input, purpose);
        }
        return super.inventoryCanAddItem(level, player, item, input, purpose, isValid, stackLimit);
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, int amount, String purpose) {
        Inventory internalInventory;
        int removed = 0;
        if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, purpose, this.isPickupDisabled(item)) && this.isValidRequestItem(requestItem) && (removed = (internalInventory = this.getInternalInventory(item)).removeItems(level, player, requestItem, amount, purpose)) > 0) {
            this.saveInternalInventory(item, internalInventory);
        }
        if (removed < amount) {
            return removed + super.removeInventoryAmount(level, player, item, requestItem, amount, purpose);
        }
        return removed;
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item.Type requestType, int amount, String purpose) {
        Inventory internalInventory;
        int removed = 0;
        if (this.isValidRequestType(requestType) && (removed = (internalInventory = this.getInternalInventory(item)).removeItems(level, player, requestType, amount, purpose)) > 0) {
            this.saveInternalInventory(item, internalInventory);
        }
        if (removed < amount) {
            return removed + super.removeInventoryAmount(level, player, item, requestType, amount, purpose);
        }
        return removed;
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Inventory inventory, int inventorySlot, Ingredient ingredient, int amount, Collection<InventoryItemsRemoved> collect) {
        Inventory internalInventory = this.getInternalInventory(item);
        int removed = internalInventory.removeItems(level, player, ingredient, amount, collect);
        if (removed > 0) {
            this.saveInternalInventory(item, internalInventory);
        }
        if (removed < amount) {
            return removed + super.removeInventoryAmount(level, player, item, inventory, inventorySlot, ingredient, amount, collect);
        }
        return removed;
    }

    @Override
    public boolean isValidItem(InventoryItem item) {
        if (item == null) {
            return true;
        }
        return this.isValidPouchItem(item);
    }

    @Override
    public ItemUsed useHealthPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        if (this.canUseHealthPotionsFromPouch) {
            Inventory internalInventory = this.getInternalInventory(item);
            for (SlotPriority slotPriority : internalInventory.getPriorityList(level, player, 0, internalInventory.getSize() - 1, "usehealthpotion")) {
                if (internalInventory.isSlotClear(slotPriority.slot)) continue;
                ItemUsed itemUsed = internalInventory.getItemSlot(slotPriority.slot).useHealthPotion(level, player, seed, internalInventory.getItem(slotPriority.slot));
                internalInventory.setItem(slotPriority.slot, itemUsed.item);
                if (!itemUsed.used) continue;
                this.saveInternalInventory(item, internalInventory);
                return new ItemUsed(true, item);
            }
        }
        return super.useHealthPotion(level, player, seed, item);
    }

    @Override
    public ItemUsed useManaPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        if (this.canUseManaPotionsFromPouch) {
            Inventory internalInventory = this.getInternalInventory(item);
            for (SlotPriority slotPriority : internalInventory.getPriorityList(level, player, 0, internalInventory.getSize() - 1, "usemanapotion")) {
                if (internalInventory.isSlotClear(slotPriority.slot)) continue;
                ItemUsed itemUsed = internalInventory.getItemSlot(slotPriority.slot).useManaPotion(level, player, seed, internalInventory.getItem(slotPriority.slot));
                internalInventory.setItem(slotPriority.slot, itemUsed.item);
                if (!itemUsed.used) continue;
                this.saveInternalInventory(item, internalInventory);
                return new ItemUsed(true, item);
            }
        }
        return super.useManaPotion(level, player, seed, item);
    }

    @Override
    public ItemUsed eatFood(Level level, PlayerMob player, int seed, InventoryItem item) {
        if (this.canEatFoodFromPouch) {
            Inventory internalInventory = this.getInternalInventory(item);
            for (SlotPriority slotPriority : internalInventory.getPriorityList(level, player, 0, internalInventory.getSize() - 1, "eatfood")) {
                if (internalInventory.isSlotClear(slotPriority.slot)) continue;
                ItemUsed itemUsed = internalInventory.getItemSlot(slotPriority.slot).eatFood(level, player, seed, internalInventory.getItem(slotPriority.slot));
                internalInventory.setItem(slotPriority.slot, itemUsed.item);
                if (!itemUsed.used) continue;
                this.saveInternalInventory(item, internalInventory);
                return new ItemUsed(true, item);
            }
        }
        return super.eatFood(level, player, seed, item);
    }

    @Override
    public ItemUsed useBuffPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        if (this.canUseBuffPotionsFromPouch) {
            Inventory internalInventory = this.getInternalInventory(item);
            boolean used = false;
            for (SlotPriority slotPriority : internalInventory.getPriorityList(level, player, 0, internalInventory.getSize() - 1, "usebuffpotion")) {
                if (internalInventory.isSlotClear(slotPriority.slot)) continue;
                ItemUsed itemUsed = internalInventory.getItemSlot(slotPriority.slot).useBuffPotion(level, player, seed, internalInventory.getItem(slotPriority.slot));
                used = used || itemUsed.used;
                internalInventory.setItem(slotPriority.slot, itemUsed.item);
            }
            if (used) {
                this.saveInternalInventory(item, internalInventory);
            }
            return new ItemUsed(used, item);
        }
        return super.useBuffPotion(level, player, seed, item);
    }
}

