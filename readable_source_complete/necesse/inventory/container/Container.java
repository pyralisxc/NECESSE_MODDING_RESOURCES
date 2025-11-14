/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.journal.listeners.CraftedRecipeJournalChallengeListener;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.ContainerComponent;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.ContainerCustomActionRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.events.ContainerEventHandler;
import necesse.inventory.container.events.ContainerEventSubscription;
import necesse.inventory.container.slots.ArmorContainerSlot;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.MountContainerSlot;
import necesse.inventory.container.slots.TrashContainerSlot;
import necesse.inventory.container.slots.TrinketAbilityContainerSlot;
import necesse.inventory.container.slots.TrinketContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.ContainerRecipeCraftedEvent;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public class Container {
    public static final int DROP_ITEM_SLOT = -1;
    public static final int SORT_INVENTORY_SLOT = -2;
    public static final int QUICK_STACK_SLOT = -3;
    public static final int RESTOCK_SLOT = -4;
    protected boolean shouldClose;
    protected boolean isClosed;
    private final ArrayList<ContainerSlot> slots = new ArrayList();
    private final HashSet<Integer> lockedSlots = new HashSet();
    private final ArrayList<ContainerRecipe> recipes;
    private final ContainerCustomActionRegistry actionRegistry;
    protected LinkedHashSet<Inventory> craftInventories;
    protected LinkedList<QuickTransferOption> quickTransferOptions;
    protected LinkedList<ContainerEventSubscription<?>> eventSubscriptions;
    protected HashMapGameLinkedList<Class<?>, ContainerEventHandler<?>> eventHandlers;
    public final NetworkClient client;
    public final int uniqueSeed;
    public int CLIENT_DRAGGING_SLOT = -1;
    public int CLIENT_HOTBAR_START = -1;
    public int CLIENT_HOTBAR_END = -1;
    public int CLIENT_INVENTORY_START = -1;
    public int CLIENT_INVENTORY_END = -1;
    public int CLIENT_HELMET_SLOT = -1;
    public int CLIENT_CHEST_SLOT = -1;
    public int CLIENT_FEET_SLOT = -1;
    public int CLIENT_COSM_HELMET_SLOT = -1;
    public int CLIENT_COSM_CHEST_SLOT = -1;
    public int CLIENT_COSM_FEET_SLOT = -1;
    public int CLIENT_MOUNT_SLOT = -1;
    public int CLIENT_TRINKET_ABILITY_SLOT = -1;
    public int CLIENT_TRINKET_START = -1;
    public int CLIENT_TRINKET_END = -1;
    public int CLIENT_TRASH_SLOT = -1;
    public ContainerComponent<?> form;

    public Container(final NetworkClient client, int uniqueSeed) {
        int index;
        int i;
        this.client = client;
        this.uniqueSeed = uniqueSeed;
        this.recipes = new ArrayList();
        this.craftInventories = new LinkedHashSet();
        this.quickTransferOptions = new LinkedList();
        this.eventSubscriptions = new LinkedList();
        this.eventHandlers = new HashMapGameLinkedList();
        this.actionRegistry = new ContainerCustomActionRegistry(this);
        this.isClosed = false;
        this.CLIENT_DRAGGING_SLOT = this.addSlot(new ContainerSlot(client.playerMob.getInv().drag, 0));
        PlayerInventory main = client.playerMob.getInv().main;
        for (i = 0; i < 10; ++i) {
            index = this.addSlot(new ContainerSlot(main, i));
            if (this.CLIENT_HOTBAR_START == -1) {
                this.CLIENT_HOTBAR_START = index;
            }
            if (this.CLIENT_HOTBAR_END == -1) {
                this.CLIENT_HOTBAR_END = index;
            }
            this.CLIENT_HOTBAR_START = Math.min(this.CLIENT_HOTBAR_START, index);
            this.CLIENT_HOTBAR_END = Math.max(this.CLIENT_HOTBAR_END, index);
        }
        for (i = 10; i < main.getSize(); ++i) {
            index = this.addSlot(new ContainerSlot(main, i));
            if (this.CLIENT_INVENTORY_START == -1) {
                this.CLIENT_INVENTORY_START = index;
            }
            if (this.CLIENT_INVENTORY_END == -1) {
                this.CLIENT_INVENTORY_END = index;
            }
            this.CLIENT_INVENTORY_START = Math.min(this.CLIENT_INVENTORY_START, index);
            this.CLIENT_INVENTORY_END = Math.max(this.CLIENT_INVENTORY_END, index);
        }
        this.CLIENT_HELMET_SLOT = this.addSlot(new ArmorContainerSlot(null, 0, ArmorItem.ArmorType.HEAD){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedArmorInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(null, 1, ArmorItem.ArmorType.CHEST){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedArmorInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_FEET_SLOT = this.addSlot(new ArmorContainerSlot(null, 2, ArmorItem.ArmorType.FEET){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedArmorInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_COSM_HELMET_SLOT = this.addSlot(new ArmorContainerSlot(null, 0, ArmorItem.ArmorType.HEAD){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedCosmeticInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_COSM_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(null, 1, ArmorItem.ArmorType.CHEST){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedCosmeticInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_COSM_FEET_SLOT = this.addSlot(new ArmorContainerSlot(null, 2, ArmorItem.ArmorType.FEET){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedCosmeticInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_MOUNT_SLOT = this.addSlot(new MountContainerSlot(null, 0){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedEquipmentInventory(this.getInventorySlot());
            }
        });
        this.CLIENT_TRINKET_ABILITY_SLOT = this.addSlot(new TrinketAbilityContainerSlot(null, 1){

            @Override
            public Inventory getInventory() {
                return client.playerMob.getInv().equipment.getSelectedEquipmentInventory(this.getInventorySlot());
            }
        });
        int trinketSlotCount = client.playerMob.getInv().equipment.getTrinketSlotsSize();
        for (int i2 = 0; i2 < trinketSlotCount; ++i2) {
            int index2 = this.addSlot(new TrinketContainerSlot(null, i2){

                @Override
                public Inventory getInventory() {
                    return client.playerMob.getInv().equipment.getSelectedTrinketsInventory(this.getInventorySlot());
                }
            });
            if (this.CLIENT_TRINKET_START == -1) {
                this.CLIENT_TRINKET_START = index2;
            }
            if (this.CLIENT_TRINKET_END == -1) {
                this.CLIENT_TRINKET_END = index2;
            }
            this.CLIENT_TRINKET_START = Math.min(this.CLIENT_TRINKET_START, index2);
            this.CLIENT_TRINKET_END = Math.max(this.CLIENT_TRINKET_END, index2);
        }
        this.CLIENT_TRASH_SLOT = this.addSlot(new TrashContainerSlot(client.playerMob.getInv().trash, 0));
        Recipes.streamRecipes().filter(r -> r.matchTech(RecipeTechRegistry.NONE)).forEach(r -> this.addRecipe((Recipe)r, true));
        this.quickTransferOptions.addFirst(new QuickTransferOption(null, true, new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)));
        this.quickTransferOptions.addFirst(new QuickTransferOption(null, true, new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END), new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END)));
    }

    public void init() {
        this.actionRegistry.closeRegistry();
    }

    public void tick() {
        if (this.client.isClient()) {
            boolean updateCraftable = false;
            for (Inventory inv : this.craftInventories) {
                if (inv.isDirty()) {
                    updateCraftable = true;
                }
                inv.clean();
            }
            if (updateCraftable) {
                GlobalData.updateCraftable();
            }
        }
    }

    public void lootAllControlPressed() {
    }

    public void sortInventoryControlPressed() {
        ClientClient clientClient;
        if (this.client.isClient() && (clientClient = this.client.getClientClient()).isLocalClient()) {
            clientClient.getClient().network.sendPacket(new PacketContainerAction(-2, ContainerAction.LEFT_CLICK, 1));
        }
    }

    public void quickStackControlPressed() {
        ClientClient clientClient;
        if (this.client.isClient() && (clientClient = this.client.getClientClient()).isLocalClient()) {
            clientClient.getClient().network.sendPacket(new PacketContainerAction(-3, ContainerAction.LEFT_CLICK, 1));
        }
    }

    public ContainerActionResult applyContainerAction(int slotIndex, ContainerAction action) {
        if (this.isSlotLocked(slotIndex)) {
            return new ContainerActionResult(255);
        }
        if (slotIndex == -4) {
            this.restockPlayerInventory();
            return new ContainerActionResult(1);
        }
        if (slotIndex == -3) {
            this.quickStackPlayerInventory();
            return new ContainerActionResult(1);
        }
        if (slotIndex == -2) {
            PlayerInventory main = this.getClientInventory().main;
            main.sortItems(this.client.playerMob.getLevel(), this.client.playerMob, 10, main.getSize() - 1);
            return new ContainerActionResult(1);
        }
        if (slotIndex == -1) {
            ContainerSlot draggingSlot = this.getClientDraggingSlot();
            if (!draggingSlot.isClear()) {
                if (draggingSlot.isItemLocked()) {
                    return new ContainerActionResult(Localization.translate("misc", "cannotdroplocked"));
                }
                switch (action) {
                    case RIGHT_CLICK: {
                        int itemAmount = draggingSlot.getItemAmount();
                        if (this.client.isServer()) {
                            this.client.playerMob.dropDraggingItem(itemAmount);
                        }
                        return new ContainerActionResult(itemAmount);
                    }
                    case TAKE_ONE: {
                        if (this.client.isServer()) {
                            this.client.playerMob.dropDraggingItem(1);
                        }
                        return new ContainerActionResult(1);
                    }
                }
            }
            return new ContainerActionResult(null);
        }
        ContainerSlot slot = this.getSlot(slotIndex);
        if (slot == null) {
            GameLog.warn.println("Tried to apply container action on invalid slot.");
            return new ContainerActionResult(null);
        }
        switch (action) {
            case LEFT_CLICK: {
                return this.applyLeftClick(slotIndex, slot);
            }
            case RIGHT_CLICK: {
                return this.applyRightClick(slotIndex, slot);
            }
            case QUICK_MOVE: {
                return this.applyQuickMove(slotIndex, slot);
            }
            case QUICK_TRASH: {
                return this.applyQuickTrash(slotIndex, slot);
            }
            case QUICK_TRASH_ONE: {
                return this.applyQuickTrashOne(slotIndex, slot);
            }
            case QUICK_DROP: {
                if (!slot.isClear() && !slot.isItemLocked()) {
                    int itemAmount = slot.getItemAmount();
                    if (this.client.isServer()) {
                        this.client.playerMob.dropItem(slot.getItem());
                    }
                    slot.setItem(null);
                    return new ContainerActionResult(itemAmount);
                }
                return new ContainerActionResult(null);
            }
            case QUICK_DROP_ONE: {
                if (!slot.isClear() && !slot.isItemLocked()) {
                    int itemAmount = 1;
                    if (this.client.isServer()) {
                        this.client.playerMob.dropItem(slot.getItem().copy(itemAmount));
                    }
                    slot.setAmount(slot.getItemAmount() - itemAmount);
                    if (slot.getItemAmount() <= 0) {
                        slot.setItem(null);
                    }
                    return new ContainerActionResult(itemAmount);
                }
                return new ContainerActionResult(null);
            }
            case TOGGLE_LOCKED: {
                if (!slot.isClear() && slot.canLockItem()) {
                    slot.setItemLocked(!slot.isItemLocked());
                    return new ContainerActionResult(slot.getItem().isLocked() ? 1 : 2);
                }
                return new ContainerActionResult(null);
            }
            case TAKE_ONE: {
                return this.applyTakeOne(slotIndex, slot);
            }
            case QUICK_MOVE_ONE: {
                return this.applyQuickMoveOne(slotIndex, slot);
            }
            case QUICK_GET_ONE: {
                return this.applyQuickGetOne(slotIndex, slot);
            }
            case RIGHT_CLICK_ACTION: {
                InventoryItem item;
                Supplier<ContainerActionResult> rAction;
                if (!slot.isClear() && (rAction = item.item.getInventoryRightClickAction(this, item = slot.getItem(), slotIndex, slot)) != null) {
                    return rAction.get();
                }
                return new ContainerActionResult(null);
            }
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult applyLeftClick(int slotIndex, ContainerSlot slot) {
        if (this.getClientDraggingSlot().isClear()) {
            if (!slot.isClear()) {
                this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, true, false, "leftclick");
                return new ContainerActionResult(1);
            }
        } else {
            String error;
            if (slot.isClear() && (error = slot.getItemInvalidError(this.getClientDraggingSlot().getItem())) != null) {
                return new ContainerActionResult(5, error);
            }
            ItemCombineResult combineResult1 = slot.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, this.getClientDraggingSlot(), true, false, "leftclick");
            if (!combineResult1.success) {
                ItemCombineResult combineResult2 = this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, true, false, "leftclickinv");
                if (!combineResult2.success) {
                    ItemCombineResult swapResult = slot.swapItems(this.getClientDraggingSlot());
                    return new ContainerActionResult(3, swapResult.error);
                }
                return new ContainerActionResult(4, combineResult2.error);
            }
            return new ContainerActionResult(2, combineResult1.error);
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult applyRightClick(int slotIndex, ContainerSlot slot) {
        if (this.getClientDraggingSlot().isClear()) {
            if (!slot.isClear()) {
                if (slot.getItemAmount() <= 1) {
                    this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, true, false, "leftclick");
                    return new ContainerActionResult(1);
                }
                int halfItems = (slot.getItemAmount() + 1) / 2;
                this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, halfItems, false, false, "rightclick");
                return new ContainerActionResult(2);
            }
        } else {
            ItemCombineResult combineResult = slot.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, this.getClientDraggingSlot(), 1, false, false, "rightclick");
            if (!combineResult.success) {
                return new ContainerActionResult(4, combineResult.error);
            }
            return new ContainerActionResult(3, combineResult.error);
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult transferFromAmount(int slotIndex, ContainerSlot slot, int amount) {
        if (!slot.isClear()) {
            LinkedList<SlotIndexRange> toIndexRanges = new LinkedList<SlotIndexRange>();
            boolean foundAnyValid = false;
            for (QuickTransferOption option : this.quickTransferOptions) {
                if (option.filter != null && !option.filter.test(slot)) continue;
                if (option.onlyIfNoOtherFilterValid) {
                    if (foundAnyValid) {
                        continue;
                    }
                } else {
                    foundAnyValid = true;
                }
                if (slotIndex < option.input.fromIndex || slotIndex > option.input.toIndex) continue;
                toIndexRanges.addAll(Arrays.asList(option.outputs));
            }
            ContainerTransferResult transferResult = this.transferToSlots(slot, toIndexRanges, amount);
            return new ContainerActionResult(amount - transferResult.amount, transferResult.error);
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult transferIntoAmount(int slotIndex, ContainerSlot slot, int amount) {
        if (!slot.isClear()) {
            int startAmount = amount;
            boolean foundAnyValid = false;
            for (QuickTransferOption option : this.quickTransferOptions) {
                if (option.filter != null && !option.filter.test(slot)) continue;
                if (option.onlyIfNoOtherFilterValid) {
                    if (foundAnyValid) {
                        continue;
                    }
                } else {
                    foundAnyValid = true;
                }
                if (slotIndex < option.input.fromIndex || slotIndex > option.input.toIndex) continue;
                String error = null;
                for (SlotIndexRange output : option.outputs) {
                    ContainerTransferResult transferResult = this.transferFromSlots(slot, output.fromIndex, output.toIndex, amount);
                    amount = transferResult.amount;
                    if (transferResult.error == null) continue;
                    error = transferResult.error;
                }
                return new ContainerActionResult(startAmount - amount, error);
            }
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult applyQuickMove(int slotIndex, ContainerSlot slot) {
        return this.transferFromAmount(slotIndex, slot, Integer.MAX_VALUE);
    }

    public ContainerActionResult applyTakeOne(int slotIndex, ContainerSlot slot) {
        if (!slot.isClear()) {
            ItemCombineResult combineResult = this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, 1, slot.getItemAmount() == 1, false, "rightclick");
            return new ContainerActionResult(combineResult.success ? 2 : 1, combineResult.error);
        }
        return new ContainerActionResult(null);
    }

    public ContainerActionResult applyQuickMoveOne(int slotIndex, ContainerSlot slot) {
        return this.transferFromAmount(slotIndex, slot, 1);
    }

    public ContainerActionResult applyQuickGetOne(int slotIndex, ContainerSlot slot) {
        return this.transferIntoAmount(slotIndex, slot, 1);
    }

    public ContainerActionResult applyQuickTrash(int slotIndex, ContainerSlot slot) {
        if (!slot.isClear() && !slot.isItemLocked()) {
            if (slotIndex == this.CLIENT_TRASH_SLOT) {
                this.transferToSlots(slot, Arrays.asList(new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)));
                return new ContainerActionResult(1);
            }
            this.getSlot(this.CLIENT_TRASH_SLOT).combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, true, false, "trash");
            return new ContainerActionResult(2);
        }
        return new ContainerActionResult(0);
    }

    public ContainerActionResult applyQuickTrashOne(int slotIndex, ContainerSlot slot) {
        if (!slot.isClear() && !slot.isItemLocked()) {
            if (slotIndex == this.CLIENT_TRASH_SLOT) {
                this.transferToSlots(slot, Arrays.asList(new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)), 1);
                return new ContainerActionResult(1);
            }
            this.getSlot(this.CLIENT_TRASH_SLOT).combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, 1, true, false, "trash");
            return new ContainerActionResult(2);
        }
        return new ContainerActionResult(0);
    }

    public void addQuickTransferOption(Predicate<ContainerSlot> filter, int inputFromIndex, int inputToIndex, int outputFromIndex, int outputToIndex) {
        this.quickTransferOptions.addFirst(new QuickTransferOption(filter, false, new SlotIndexRange(inputFromIndex, inputToIndex), new SlotIndexRange(outputFromIndex, outputToIndex)));
    }

    public void addQuickTransferOption(int inputFromIndex, int inputToIndex, int outputFromIndex, int outputToIndex) {
        this.addQuickTransferOption(null, inputFromIndex, inputToIndex, outputFromIndex, outputToIndex);
    }

    public void addQuickTransferOption(Predicate<ContainerSlot> filter, int inputFromIndex, int inputToIndex, SlotIndexRange ... outputs) {
        this.quickTransferOptions.addFirst(new QuickTransferOption(filter, false, new SlotIndexRange(inputFromIndex, inputToIndex), outputs));
    }

    public void addQuickTransferOption(int inputFromIndex, int inputToIndex, SlotIndexRange ... outputs) {
        this.addQuickTransferOption(null, inputFromIndex, inputToIndex, outputs);
    }

    public void addInventoryQuickTransfer(Predicate<ContainerSlot> filter, int fromIndex, int toIndex) {
        this.addQuickTransferOption(filter, this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END, fromIndex, toIndex);
        this.addQuickTransferOption(filter, this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END, fromIndex, toIndex);
        this.addQuickTransferOption(filter, fromIndex, toIndex, new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END));
    }

    public void addInventoryQuickTransfer(int fromIndex, int toIndex) {
        this.addInventoryQuickTransfer(null, fromIndex, toIndex);
    }

    public int addSlot(ContainerSlot slot) {
        int index = this.slots.size();
        this.craftInventories.add(slot.getInventory());
        this.slots.add(slot);
        slot.init(this, index);
        return index;
    }

    public ContainerSlot getSlot(int index) {
        if (index < 0 || index >= this.slots.size()) {
            return null;
        }
        return this.slots.get(index);
    }

    public void lockSlot(int index) {
        this.lockedSlots.add(index);
    }

    public void lockSlot(PlayerInventorySlot slot) {
        for (int i = 0; i < this.slots.size(); ++i) {
            PlayerInventory inv;
            ContainerSlot s = this.slots.get(i);
            if (!(s.getInventory() instanceof PlayerInventory) || (inv = (PlayerInventory)s.getInventory()).getInventoryID() != slot.inventoryID || s.getInventorySlot() != slot.slot) continue;
            this.lockSlot(i);
        }
    }

    public boolean isSlotLocked(int index) {
        return this.lockedSlots.contains(index);
    }

    public boolean isSlotLocked(ContainerSlot slot) {
        return this.isSlotLocked(slot.getContainerIndex());
    }

    public PlayerInventoryManager getClientInventory() {
        return this.client.playerMob.getInv();
    }

    public ContainerSlot getClientDraggingSlot() {
        return this.getSlot(this.CLIENT_DRAGGING_SLOT);
    }

    public ContainerSlot getClientTrashSlot() {
        return this.getSlot(this.CLIENT_TRASH_SLOT);
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, int startIndex, int stopIndex) {
        return this.transferToSlots(slot, startIndex, stopIndex, Integer.MAX_VALUE);
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, int startIndex, int stopIndex, String purpose) {
        return this.transferToSlots(slot, startIndex, stopIndex, Integer.MAX_VALUE, purpose);
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, Iterable<SlotIndexRange> ranges) {
        return this.transferToSlots(slot, ranges, "transfer");
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, Iterable<SlotIndexRange> ranges, String purpose) {
        return this.transferToSlots(slot, ranges, Integer.MAX_VALUE, purpose);
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, int startIndex, int stopIndex, int amount) {
        return this.transferToSlots(slot, startIndex, stopIndex, amount, "transfer");
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, int startIndex, int stopIndex, int amount, String purpose) {
        return this.transferToSlots(slot, Collections.singleton(new SlotIndexRange(startIndex, stopIndex)), amount, purpose);
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, Iterable<SlotIndexRange> ranges, int amount) {
        return this.transferToSlots(slot, ranges, amount, "transfer");
    }

    public ContainerTransferResult transferToSlots(ContainerSlot slot, Iterable<SlotIndexRange> ranges, int amount, String purpose) {
        String error = null;
        boolean success = false;
        for (int j = 0; j < 2; ++j) {
            for (SlotIndexRange range : ranges) {
                for (int i = range.fromIndex; i <= range.toIndex; ++i) {
                    if (slot.isClear() || amount <= 0) {
                        return new ContainerTransferResult(amount, error);
                    }
                    ContainerSlot toSlot = this.getSlot(i);
                    if (j == 0 && toSlot.isClear()) continue;
                    int startAmount = slot.getItemAmount();
                    ItemCombineResult combineResult = toSlot.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, slot, amount, true, false, purpose);
                    if (combineResult.success) {
                        int amountMoved = startAmount - slot.getItemAmount();
                        amount -= amountMoved;
                        success = true;
                        error = null;
                        continue;
                    }
                    if (success || combineResult.error == null) continue;
                    error = combineResult.error;
                }
            }
        }
        return new ContainerTransferResult(amount, error);
    }

    public ContainerTransferResult transferFromSlots(ContainerSlot slot, int startIndex, int stopIndex, int amount) {
        return this.transferFromSlots(slot, startIndex, stopIndex, amount, "transfer");
    }

    public ContainerTransferResult transferFromSlots(ContainerSlot slot, int startIndex, int stopIndex, int amount, String purpose) {
        String error = null;
        for (int i = stopIndex; i >= startIndex; --i) {
            if (amount <= 0) {
                return new ContainerTransferResult(amount, error);
            }
            ContainerSlot fromSlot = this.getSlot(i);
            if (fromSlot.isClear()) continue;
            int startAmount = fromSlot.getItemAmount();
            ItemCombineResult combineResult = slot.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, fromSlot, amount, true, false, purpose);
            if (combineResult.success) {
                int amountMoved = startAmount - fromSlot.getItemAmount();
                amount -= amountMoved;
                continue;
            }
            if (combineResult.error == null) continue;
            error = combineResult.error;
        }
        return new ContainerTransferResult(amount, error);
    }

    public ArrayList<InventoryRange> getNearbyInventories(Level level, int levelCenterX, int levelCenterY, int range) {
        return this.getNearbyInventories(level, levelCenterX, levelCenterY, range, null);
    }

    public ArrayList<InventoryRange> getNearbyInventories(Level level, int levelCenterX, int levelCenterY, int range, Predicate<OEInventory> filter) {
        if (level == null) {
            return new ArrayList<InventoryRange>();
        }
        int tileRange = range / 32;
        int startX = GameMath.getTileCoordinate(levelCenterX) - tileRange - 1;
        int startY = GameMath.getTileCoordinate(levelCenterY) - tileRange - 1;
        int endX = GameMath.getTileCoordinate(levelCenterX) + tileRange + 1;
        int endY = GameMath.getTileCoordinate(levelCenterY) + tileRange + 1;
        ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>();
        for (int y = startY; y <= endY; ++y) {
            if (!level.isTileYWithinBounds(y)) continue;
            for (int x = startX; x <= endX; ++x) {
                ObjectEntity ent;
                if (!level.isTileXWithinBounds(x)) continue;
                Point point = new Point(levelCenterX, levelCenterY);
                if (!(point.distance(x * 32 + 16, y * 32 + 16) <= (double)range) || !((ent = level.entityManager.getObjectEntity(x, y)) instanceof OEInventory) || filter != null && !filter.test((OEInventory)((Object)ent))) continue;
                Inventory inventory = ((OEInventory)((Object)ent)).getInventory();
                if (targets.stream().anyMatch(i -> i.inventory == inventory)) continue;
                targets.add(new InventoryRange(inventory));
            }
        }
        return targets;
    }

    public ArrayList<InventoryRange> getNearbyInventories(Level level, int centerTileX, int centerTileY, GameTileRange range, Predicate<OEInventory> filter) {
        if (level == null) {
            return new ArrayList<InventoryRange>();
        }
        ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>();
        for (Point tile : range.getValidTiles(centerTileX, centerTileY)) {
            ObjectEntity ent = level.entityManager.getObjectEntity(tile.x, tile.y);
            if (!(ent instanceof OEInventory) || filter != null && !filter.test((OEInventory)((Object)ent))) continue;
            Inventory inventory = ((OEInventory)((Object)ent)).getInventory();
            if (targets.stream().anyMatch(i -> i.inventory == inventory)) continue;
            targets.add(new InventoryRange(inventory));
        }
        return targets;
    }

    public void quickStackPlayerInventory() {
        ArrayList<InventoryRange> targets = this.getNearbyInventories(this.client.playerMob.getLevel(), this.client.playerMob.getX(), this.client.playerMob.getY(), 192, OEInventory::canQuickStackInventory);
        this.quickStackToInventories(targets, this.client.playerMob.getInv().main);
    }

    public void quickStackToInventories(ArrayList<InventoryRange> targets, InventoryRange inventory) {
        block0: for (int i = inventory.startSlot; i <= inventory.endSlot; ++i) {
            if (inventory.inventory.isSlotClear(i) || inventory.inventory.isItemLocked(i)) continue;
            for (InventoryRange target : targets) {
                if (target.inventory.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, inventory.inventory.getItemSlot(i), target.startSlot, target.endSlot, "quickstackto") <= 0) continue;
                int lastAmount = inventory.inventory.getAmount(i);
                target.inventory.addItem(this.client.playerMob.getLevel(), this.client.playerMob, inventory.inventory.getItem(i), target.startSlot, target.endSlot, "quickstackto", null);
                if (lastAmount != inventory.inventory.getAmount(i)) {
                    inventory.inventory.markDirty(i);
                }
                if (inventory.inventory.getAmount(i) > 0) continue;
                inventory.inventory.clearSlot(i);
                continue block0;
            }
        }
    }

    public void quickStackToInventories(ArrayList<InventoryRange> targets, Inventory inventory) {
        this.quickStackToInventories(targets, new InventoryRange(inventory));
    }

    public void restockPlayerInventory() {
        ArrayList<InventoryRange> targets = this.getNearbyInventories(this.client.playerMob.getLevel(), this.client.playerMob.getX(), this.client.playerMob.getY(), 192, OEInventory::canRestockInventory);
        this.restockFromInventories(targets, this.client.playerMob.getInv().main);
    }

    public void restockFromInventories(ArrayList<InventoryRange> targets, InventoryRange inventory) {
        for (InventoryRange target : targets) {
            for (int i = target.startSlot; i <= target.endSlot; ++i) {
                if (target.inventory.isSlotClear(i) || target.inventory.isItemLocked(i)) continue;
                InventoryItem item = target.inventory.getItem(i);
                if (!inventory.inventory.restockFrom(this.client.playerMob.getLevel(), this.client.playerMob, item, inventory.startSlot, inventory.endSlot, "restockfrom", false, null)) continue;
                if (item.getAmount() <= 0) {
                    target.inventory.setItem(i, null);
                }
                target.inventory.updateSlot(i);
            }
        }
    }

    public void restockFromInventories(ArrayList<InventoryRange> targets, Inventory inventory) {
        this.restockFromInventories(targets, new InventoryRange(inventory));
    }

    public final void runCustomAction(int id, PacketReader reader) {
        this.actionRegistry.runAction(id, reader);
    }

    public final <T extends ContainerCustomAction> T registerAction(T action) {
        return this.actionRegistry.registerAction(action);
    }

    public final <T extends ContainerEvent> void subscribeEvent(Class<T> eventClass, final Predicate<T> shouldReceiveEvent, final BooleanSupplier isActive) {
        this.eventSubscriptions.removeIf(s -> !s.isActive());
        this.eventSubscriptions.add(new ContainerEventSubscription<T>(eventClass){

            @Override
            public boolean shouldReceiveEvent(T event) {
                return shouldReceiveEvent.test(event);
            }

            @Override
            public boolean isActive() {
                return isActive.getAsBoolean();
            }
        });
    }

    public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> eventClass, ContainerEventHandler<T> handler) {
        if (handler.isDisposed()) {
            return handler;
        }
        handler.init(eventClass, this.eventHandlers.addLast(eventClass, handler));
        return handler;
    }

    public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> eventClass, final Consumer<T> handler, final BooleanSupplier isActive) {
        return this.onEvent(eventClass, new ContainerEventHandler<T>(){

            @Override
            public void handleEvent(T event) {
                if (!isActive.getAsBoolean()) {
                    this.dispose();
                } else {
                    handler.accept(event);
                }
            }
        });
    }

    public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> eventClass, final Consumer<T> handler) {
        return this.onEvent(eventClass, new ContainerEventHandler<T>(){

            @Override
            public void handleEvent(T event) {
                handler.accept(event);
            }
        });
    }

    public final boolean shouldReceiveEvent(ContainerEvent event) {
        boolean out = false;
        ListIterator li = this.eventSubscriptions.listIterator();
        while (li.hasNext()) {
            ContainerEventSubscription next = (ContainerEventSubscription)li.next();
            if (!next.isActive()) {
                li.remove();
                continue;
            }
            if (!next.testUntypedEvent(event)) continue;
            out = true;
        }
        return out;
    }

    public final void handleEvent(ContainerEvent event) {
        GameLinkedList handlers = (GameLinkedList)this.eventHandlers.get(event.getClass());
        GameLinkedList.Element current = handlers.getFirstElement();
        while (current != null) {
            GameLinkedList.Element next = current.next();
            ((ContainerEventHandler)current.object).handleEventUntyped(event);
            current = next;
        }
    }

    public int applyCraftingAction(int recipeID, int recipeHash, int craftAmount, boolean transferToInventory) {
        Recipe r = this.getRecipe(recipeID);
        if (r != null) {
            ContainerSlot draggingSlot = this.getClientDraggingSlot();
            if (recipeHash != r.getRecipeHash()) {
                return 0;
            }
            Collection<Inventory> invList = this.getCraftInventories();
            int crafted = 0;
            for (int i = 0; i < craftAmount && this.canCraftRecipe(r, invList, false).canCraft(); ++i) {
                int canAdd;
                InventoryItem resultItem;
                ContainerRecipeCraftedEvent event = new ContainerRecipeCraftedEvent(r, Recipe.craft(r.ingredients, this.client.playerMob.getLevel(), this.client.playerMob, invList), this);
                r.submitCraftedEvent(event);
                InventoryItem addDraggingItem = resultItem = event.resultItem;
                InventoryItem addInventoryItem = null;
                if (transferToInventory && (canAdd = this.client.playerMob.getInv().canAddItem(resultItem, true, "crafting")) >= 0) {
                    int remaining = resultItem.getAmount() - canAdd;
                    if (remaining > 0) {
                        addDraggingItem = resultItem.copy(remaining);
                        addInventoryItem = resultItem.copy(canAdd);
                    } else {
                        addInventoryItem = resultItem;
                        addDraggingItem = null;
                    }
                }
                if (addDraggingItem == null || draggingSlot.isClear() || draggingSlot.getItem().canCombine(this.client.playerMob.getLevel(), this.client.playerMob, addDraggingItem, "crafting") && draggingSlot.getItemAmount() + addDraggingItem.getAmount() <= draggingSlot.getItemStackLimit(draggingSlot.getItem())) {
                    boolean added = false;
                    ++crafted;
                    if (addInventoryItem != null) {
                        addInventoryItem.setNew(true);
                        this.client.playerMob.getInv().addItem(addInventoryItem, true, "crafting", null);
                        added = true;
                    }
                    if (addDraggingItem != null) {
                        if (draggingSlot.isClear()) {
                            draggingSlot.setItem(addDraggingItem);
                        } else {
                            draggingSlot.getItem().combine(this.client.playerMob.getLevel(), this.client.playerMob, draggingSlot.getInventory(), draggingSlot.getInventorySlot(), addDraggingItem, "crafting", null);
                        }
                        added = true;
                    }
                    if (!added) {
                        event.itemsUsed.forEach(InventoryItemsRemoved::revert);
                        continue;
                    }
                    draggingSlot.markDirty();
                    continue;
                }
                event.itemsUsed.forEach(InventoryItemsRemoved::revert);
                break;
            }
            if (crafted > 0 && this.client.isServer()) {
                ServerClient serverClient = this.client.getServerClient();
                serverClient.newStats.crafted_items.increment(crafted);
                int finalCrafted = crafted;
                JournalChallengeRegistry.handleListeners(serverClient, CraftedRecipeJournalChallengeListener.class, challenge -> challenge.onCraftedRecipe(serverClient, r, finalCrafted));
            }
            return crafted;
        }
        GameLog.warn.println(this.client.playerMob.getDisplayName() + " tried to craft a not existing recipe with id " + recipeID);
        return 0;
    }

    private int addRecipe(Recipe recipe, boolean isInventory) {
        ContainerRecipe cRecipe = new ContainerRecipe(this.recipes.size(), recipe, isInventory);
        this.recipes.add(cRecipe);
        return cRecipe.id;
    }

    public int addRecipe(Recipe recipe) {
        return this.addRecipe(recipe, false);
    }

    public void addRecipes(Collection<Recipe> recipeList) {
        recipeList.forEach(this::addRecipe);
    }

    public Recipe getRecipe(int id) {
        if (this.recipes.size() <= id) {
            return null;
        }
        return this.recipes.get((int)id).recipe;
    }

    public Stream<ContainerRecipe> streamRecipes(Tech ... techs) {
        return this.recipes.stream().filter(cRecipe -> Arrays.stream(techs).anyMatch(cRecipe.recipe::matchTech));
    }

    public CanCraft canCraftRecipe(Recipe recipe, Collection<Inventory> invList, boolean countAllIngredients) {
        return recipe.canCraft(this.client.playerMob.getLevel(), this.client.playerMob, invList, countAllIngredients);
    }

    public CanCraft canCraftRecipe(Ingredient[] ingredients, Collection<Inventory> invList, boolean countAllIngredients) {
        return Recipe.canCraft(ingredients, this.client.playerMob.getLevel(), this.client.playerMob, invList, countAllIngredients);
    }

    public boolean doesShowRecipe(Recipe recipe, Collection<Inventory> invList) {
        return recipe.doesShow(this.client.playerMob.getLevel(), this.client.playerMob, invList);
    }

    public void markFullDirty() {
        this.getCraftInventories().forEach(Inventory::markFullDirty);
    }

    public Collection<Inventory> getCraftInventories() {
        return this.craftInventories;
    }

    public boolean isValid(ServerClient client) {
        return !this.shouldClose;
    }

    public void close() {
        this.shouldClose = true;
    }

    public void onClose() {
        this.isClosed = true;
        if (this.client.isClient()) {
            GlobalData.updateCraftable();
        }
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public NetworkClient getClient() {
        return this.client;
    }

    protected static class QuickTransferOption {
        public final Predicate<ContainerSlot> filter;
        public final boolean onlyIfNoOtherFilterValid;
        public final SlotIndexRange input;
        public final SlotIndexRange[] outputs;

        public QuickTransferOption(Predicate<ContainerSlot> filter, boolean onlyIfNoOtherFilterValid, SlotIndexRange input, SlotIndexRange ... outputs) {
            this.filter = filter;
            this.onlyIfNoOtherFilterValid = onlyIfNoOtherFilterValid;
            this.input = input;
            this.outputs = outputs;
        }
    }
}

