/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.dlc.DLC;
import necesse.engine.dlc.DLCProvider;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketShowPickupText;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerEquipmentSetInventoryManager;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.PlayerTempInventoryConstructor;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class PlayerInventoryManager {
    public static final int EQUIPMENT_MOUNT_SLOT = 0;
    public static final int EQUIPMENT_TRINKET_ABILITY_SLOT = 1;
    private ArrayList<PlayerInventory> inventories = new ArrayList();
    public final PlayerInventory drag;
    public final PlayerInventory main;
    public final PlayerInventory trash;
    public final PlayerEquipmentSetInventoryManager equipment;
    public final PlayerInventory cloud;
    public final PlayerInventory party;
    protected boolean updatePartySize;
    protected boolean hasPartyItems;
    public static int MAX_PARTY_INVENTORY_SIZE = 20;
    private final ArrayList<PlayerTempInventory> tempInvs;
    public final PlayerMob player;

    public PlayerInventoryManager(PlayerMob player) {
        this.player = player;
        this.drag = new PlayerInventory(player, 1, false, false, true);
        this.main = new PlayerInventory(player, 50, false, true, true){

            @Override
            public void updateSlot(int slot) {
                ServerClient serverClient;
                super.updateSlot(slot);
                if (!this.isSlotClear(slot) && this.player.getLevel() != null && this.player.isServer() && (serverClient = this.player.getServerClient()).achievementsLoaded() && !serverClient.achievements().HOARDER.isCompleted()) {
                    HashSet<Integer> items = new HashSet<Integer>();
                    boolean hoarder = true;
                    for (int i = 0; i < this.getSize(); ++i) {
                        if (this.isSlotClear(i)) {
                            hoarder = false;
                            break;
                        }
                        int itemID = this.getItemID(i);
                        if (items.contains(itemID)) {
                            hoarder = false;
                            break;
                        }
                        items.add(itemID);
                    }
                    if (hoarder) {
                        serverClient.achievements().HOARDER.markCompleted(serverClient);
                    }
                }
            }
        };
        this.equipment = new PlayerEquipmentSetInventoryManager(this);
        this.trash = new PlayerInventory(player, 1, false, false, false);
        this.cloud = new PlayerInventory(player, 0, true, false, true);
        this.party = new PlayerInventory(player, 5, true, false, false){

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                PlayerInventoryManager.this.updatePartySize = true;
            }
        };
        this.refreshInventoryIDs();
        this.tempInvs = new ArrayList();
    }

    public void refreshInventoryIDs() {
        this.inventories = new ArrayList();
        this.drag.setIDAndAddToList(this.inventories);
        this.main.setIDAndAddToList(this.inventories);
        this.equipment.refreshInventoryIDs(this.inventories);
        this.trash.setIDAndAddToList(this.inventories);
        this.cloud.setIDAndAddToList(this.inventories);
        this.party.setIDAndAddToList(this.inventories);
    }

    public PlayerInventory getInventoryByID(int id) {
        if (id < this.inventories.size()) {
            return this.inventories.get(id);
        }
        for (PlayerInventory playerInventory : this.tempInvs) {
            if (playerInventory.getInventoryID() != id) continue;
            return playerInventory;
        }
        return null;
    }

    public void tick() {
        Performance.record((PerformanceTimerManager)this.player.getLevel().tickManager(), "tickItems", () -> {
            for (PlayerInventory playerInventory : this.inventories) {
                playerInventory.tickItems(this.player);
            }
            for (PlayerTempInventory playerTempInventory : this.tempInvs) {
                if (playerTempInventory.shouldDispose()) continue;
                playerTempInventory.tickItems(this.player);
            }
        });
        if (this.updatePartySize) {
            if (this.adjustPartyInventorySize()) {
                // empty if block
            }
            this.updatePartySize = false;
            this.hasPartyItems = false;
            for (int i = 0; i < this.party.getSize(); ++i) {
                if (this.party.isSlotClear(i)) continue;
                this.hasPartyItems = true;
                break;
            }
        }
    }

    public void tickSync() {
        boolean serverLevel = this.player.isServer();
        if (serverLevel) {
            boolean fullDirty = this.inventories.stream().allMatch(Inventory::isFullDirty);
            if (fullDirty) {
                this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventory(this.player.getServerClient()));
                this.clean();
            } else {
                for (PlayerInventory inventory : this.inventories) {
                    inventory.tickSync();
                }
            }
        }
        for (int i = 0; i < this.tempInvs.size(); ++i) {
            PlayerTempInventory inv = this.tempInvs.get(i);
            if (serverLevel) {
                inv.tickSync();
            }
            if (!inv.shouldDispose()) continue;
            inv.dispose();
            this.tempInvs.remove(i);
            --i;
        }
    }

    public void giveLookArmor(boolean forceItems) {
        PlayerEquipmentInventory inventory = this.equipment.cosmetic.get(0);
        Item shirt = inventory.getItemSlot(1);
        if (forceItems || shirt != null && shirt.getStringID().equals("shirt")) {
            inventory.setItem(1, ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.player.look.getShirtColor()));
        }
        Item shoes = inventory.getItemSlot(2);
        if (forceItems || shoes != null && shoes.getStringID().equals("shoes")) {
            inventory.setItem(2, ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.player.look.getShoesColor()));
        }
    }

    public void giveLookArmor() {
        this.giveLookArmor(true);
    }

    public void giveStarterItems() {
        boolean isSupporter;
        this.giveLookArmor();
        if (this.getAmount(ItemRegistry.getItem("woodaxe"), false, false, false, false, "startitem") == 0) {
            this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("woodaxe"), "startitem", null);
        }
        if (this.getAmount(ItemRegistry.getItem("woodsword"), false, false, false, false, "startitem") == 0) {
            this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("woodsword"), "startitem", null);
        }
        if (this.getAmount(ItemRegistry.getItem("craftingguide"), false, false, false, false, "startitem") == 0) {
            this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("craftingguide"), "startitem", null);
        }
        if (isSupporter = this.player.getNetworkClient() != null ? this.player.getNetworkClient().isSupporter() : DLCProvider.getInstalledDLCs().contains(DLC.SUPPORTER_PACK)) {
            if (this.getAmount(ItemRegistry.getItem("supporterhelmet"), false, false, false, false, "startitem") == 0) {
                this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("supporterhelmet"), "startitem", null);
            }
            if (this.getAmount(ItemRegistry.getItem("supporterchestplate"), false, false, false, false, "startitem") == 0) {
                this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("supporterchestplate"), "startitem", null);
            }
            if (this.getAmount(ItemRegistry.getItem("supporterboots"), false, false, false, false, "startitem") == 0) {
                this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("supporterboots"), "startitem", null);
            }
        }
    }

    @Deprecated
    public boolean addItem(InventoryItem item, boolean ignoreHotbarLocked, String purpose) {
        return this.addItem(item, ignoreHotbarLocked, purpose, null);
    }

    public boolean addItem(InventoryItem item, boolean ignoreHotbarLocked, String purpose, InventoryAddConsumer addConsumer) {
        return this.addItem(item, false, ignoreHotbarLocked, purpose, addConsumer);
    }

    @Deprecated
    public boolean addItem(InventoryItem item, boolean combineIsNew, boolean ignoreHotbarLocked, String purpose) {
        return this.addItem(item, combineIsNew, ignoreHotbarLocked, purpose, null);
    }

    public boolean addItem(InventoryItem item, boolean combineIsNew, boolean ignoreHotbarLocked, String purpose, InventoryAddConsumer addConsumer) {
        if (this.player.hotbarLocked && !ignoreHotbarLocked) {
            boolean addedAny = this.main.addItemOnlyCombine(this.player.getLevel(), this.player, item, 0, 9, combineIsNew, purpose, false, false, addConsumer);
            if (item.getAmount() > 0) {
                addedAny = this.main.addItem(this.player.getLevel(), this.player, item, 10, this.main.getSize() - 1, combineIsNew, purpose, false, false, addConsumer) || addedAny;
            }
            return addedAny;
        }
        return this.main.addItem(this.player.getLevel(), this.player, item, 0, this.main.getSize() - 1, combineIsNew, purpose, false, false, addConsumer);
    }

    public int canAddItem(InventoryItem item, boolean ignoreHotbarLocked, String purpose) {
        if (this.player.hotbarLocked && !ignoreHotbarLocked) {
            int canAddHotbar = this.main.canAddItemOnlyCombine(this.player.getLevel(), this.player, item, 0, 9, purpose, false, false);
            if (canAddHotbar > 0) {
                if (canAddHotbar >= item.getAmount()) {
                    return canAddHotbar;
                }
                item = item.copy(item.getAmount() - canAddHotbar);
            }
            return this.main.canAddItem(this.player.getLevel(), this.player, item, 10, this.main.getSize() - 1, purpose, false, false) + canAddHotbar;
        }
        return this.main.canAddItem(this.player.getLevel(), this.player, item, 0, this.main.getSize() - 1, purpose, false, false);
    }

    @Deprecated
    public boolean addItem(InventoryItem item, PlayerInventorySlot preferredSlot, boolean isLocked, String purpose) {
        return this.addItem(item, preferredSlot, isLocked, purpose, null);
    }

    public boolean addItem(InventoryItem item, PlayerInventorySlot preferredSlot, boolean isLocked, String purpose, InventoryAddConsumer addConsumer) {
        PlayerInventory inventory = preferredSlot.getInv(this);
        if (inventory == null) {
            return this.addItem(item, false, purpose, addConsumer);
        }
        return inventory.addItem(this.player.getLevel(), this.player, item, preferredSlot.slot, isLocked, purpose, addConsumer, this.main);
    }

    public ItemPickupEntity addItemsDropRemaining(InventoryItem item, String purpose, Entity dropPos, boolean addItemPickupText, boolean ignoreHotbarLocked) {
        return this.addItemsDropRemaining(item, purpose, dropPos, addItemPickupText, addItemPickupText, ignoreHotbarLocked);
    }

    public ItemPickupEntity addItemsDropRemaining(InventoryItem item, String purpose, Entity dropPos, boolean addItemPickupText, boolean playPickupSound, boolean ignoreHotbarLocked) {
        return this.addItemsDropRemaining(item, purpose, dropPos, addItemPickupText, playPickupSound, ignoreHotbarLocked, null);
    }

    public ItemPickupEntity addItemsDropRemaining(InventoryItem item, String purpose, Entity dropPos, boolean addItemPickupText, boolean playPickupSound, boolean ignoreHotbarLocked, InventoryAddConsumer addConsumer) {
        int startAmount = item.getAmount();
        this.addItem(item, ignoreHotbarLocked, purpose, addConsumer);
        int pickedUp = startAmount - item.getAmount();
        if (pickedUp > 0 && this.player != null && this.player.getLevel() != null && addItemPickupText) {
            if (this.player.isServerClient()) {
                this.player.getServerClient().sendPacket(new PacketShowPickupText(item.item, pickedUp, playPickupSound));
            } else {
                if (Settings.showPickupText) {
                    this.player.getLevel().hudManager.addElement(new ItemPickupText(this.player, new InventoryItem(item.item, pickedUp)));
                }
                if (playPickupSound) {
                    SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(this.player));
                }
            }
        }
        if (dropPos != null && dropPos.getLevel() != null && dropPos.isServer() && item.getAmount() > 0) {
            ItemPickupEntity out = item.getPickupEntity(dropPos.getLevel(), dropPos.x, dropPos.y);
            dropPos.getLevel().entityManager.pickups.add(out);
            return out;
        }
        return null;
    }

    public int getAmount(Item item, boolean includeInactiveSets, boolean includeCloud, boolean includeTrash, boolean includeTemp, String purpose) {
        int amount = 0;
        amount += this.drag.getAmount(this.player.getLevel(), this.player, item, purpose);
        amount += this.main.getAmount(this.player.getLevel(), this.player, item, purpose);
        amount += this.equipment.getAmount(item, includeInactiveSets, purpose);
        if (includeCloud) {
            amount += this.cloud.getAmount(this.player.getLevel(), this.player, item, purpose);
        }
        if (includeTrash) {
            amount += this.trash.getAmount(this.player.getLevel(), this.player, item, purpose);
        }
        if (includeTemp) {
            for (PlayerTempInventory inv : this.tempInvs) {
                amount += inv.getAmount(this.player.getLevel(), this.player, item, purpose);
            }
        }
        return amount;
    }

    public void addSaveData(SaveData save) {
        save.addSaveData(InventorySave.getSave(this.drag, "DRAG"));
        save.addSaveData(InventorySave.getSave(this.main, "MAIN"));
        this.equipment.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.cloud, "CLOUD"));
        save.addSaveData(InventorySave.getSave(this.party, "PARTY"));
    }

    public void applyLoadData(LoadData save) {
        LoadData partySave;
        LoadData mainSave;
        LoadData dragSave = save.getFirstLoadDataByName("DRAG");
        if (dragSave != null) {
            this.drag.override(InventorySave.loadSave(dragSave), false, false);
        }
        if ((mainSave = save.getFirstLoadDataByName("MAIN")) != null) {
            this.main.override(InventorySave.loadSave(mainSave), false, false);
        }
        this.equipment.applyLoadData(save);
        LoadData cloudSave = save.getFirstLoadDataByName("CLOUD");
        if (cloudSave != null) {
            this.cloud.override(InventorySave.loadSave(cloudSave), true, false);
            this.cloud.adjustSize(0, -1, 0);
        }
        if ((partySave = save.getFirstLoadDataByName("PARTY")) != null) {
            this.party.override(InventorySave.loadSave(partySave), true, false);
            this.party.compressItems();
            this.adjustPartyInventorySize();
        }
        this.refreshInventoryIDs();
    }

    public boolean adjustPartyInventorySize() {
        return this.party.adjustSize(5, MAX_PARTY_INVENTORY_SIZE, 1);
    }

    public boolean hasPartyItems() {
        return this.hasPartyItems;
    }

    public void setupContentPacket(PacketWriter writer) {
        this.drag.writeContent(writer);
        this.main.writeContent(writer);
        this.equipment.setupContentPacket(writer);
        this.trash.writeContent(writer);
        this.cloud.writeContent(writer);
        this.party.writeContent(writer);
    }

    public void applyContentPacket(PacketReader reader) {
        this.drag.override(Inventory.getInventory(reader));
        this.main.override(Inventory.getInventory(reader));
        this.equipment.applyContentPacket(reader);
        this.trash.override(Inventory.getInventory(reader));
        this.cloud.override(Inventory.getInventory(reader));
        this.party.override(Inventory.getInventory(reader));
    }

    public void setupLookContentPacket(PacketWriter writer) {
        this.equipment.setupContentPacket(writer);
    }

    public void applyLookContentPacket(PacketReader reader) {
        this.equipment.applyContentPacket(reader);
    }

    public void applyInventoryPartPacket(PacketPlayerInventoryPart packet) {
        PlayerInventory inv = this.getInventoryByID(packet.inventoryID);
        if (inv != null) {
            inv.override(Inventory.getInventory(packet.inventoryContent));
        }
    }

    public void clean() {
        this.inventories.forEach(Inventory::clean);
    }

    public void markFullDirty() {
        this.inventories.forEach(Inventory::markFullDirty);
    }

    public void clearInventories() {
        this.inventories.forEach(Inventory::clearInventory);
    }

    protected void dropInventory(PlayerInventory inv, int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; ++i) {
            if (inv.isSlotClear(i)) continue;
            PlayerInventorySlot slot = new PlayerInventorySlot(inv, i);
            boolean itemLocked = slot.isItemLocked(this);
            ItemPickupEntity entity = inv.getItem(i).getPickupEntity(this.player.getLevel(), this.player.x, this.player.y).setPlayerDeathAuth(this.player.getNetworkClient(), slot, itemLocked);
            entity.droppedByPlayer = true;
            this.player.getLevel().entityManager.pickups.add(entity);
            inv.clearSlot(i);
        }
    }

    protected void dropInventory(PlayerInventory inv) {
        this.dropInventory(inv, 0, inv.getSize() - 1);
    }

    public void dropInventory() {
        this.dropInventory(this.drag);
        this.dropInventory(this.main);
        this.equipment.dropInventory();
        this.trash.clearInventory();
        this.tempInvs.forEach(this::dropInventory);
    }

    public void dropMainInventory() {
        this.dropInventory(this.main, 10, this.main.getSize() - 1);
    }

    public Stream<PlayerInventory> streamInventories(boolean includeCloud, boolean includeTrash, boolean includeTemp) {
        Stream<PlayerInventory> stream = Stream.of(this.drag, this.main);
        stream = Stream.concat(stream, this.equipment.streamAllInventories());
        if (includeCloud) {
            stream = Stream.concat(stream, Stream.of(this.cloud));
        }
        if (includeTrash) {
            stream = Stream.concat(stream, Stream.of(this.trash));
        }
        if (includeTemp) {
            stream = Stream.concat(stream, this.tempInvs.stream());
        }
        return stream;
    }

    public Stream<InventorySlot> streamInventorySlots(boolean includeInactiveSets, boolean includeCloud, boolean includeTrash, boolean includeTemp) {
        Stream<InventorySlot> stream = Stream.concat(this.drag.streamSlots(), this.main.streamSlots());
        stream = Stream.concat(stream, includeInactiveSets ? this.equipment.streamAllInventories().flatMap(Inventory::streamSlots) : this.equipment.streamActiveSlots());
        if (includeCloud) {
            stream = Stream.concat(stream, this.cloud.streamSlots());
        }
        if (includeTrash) {
            stream = Stream.concat(stream, this.trash.streamSlots());
        }
        if (includeTemp) {
            stream = Stream.concat(stream, this.tempInvs.stream().flatMap(Inventory::streamSlots));
        }
        return stream;
    }

    public Stream<PlayerInventorySlot> streamPlayerSlots(boolean includeInactiveSets, boolean includeCloud, boolean includeTrash, boolean includeTemp) {
        return this.streamInventorySlots(includeInactiveSets, includeCloud, includeTrash, includeTemp).map(slot -> new PlayerInventorySlot((PlayerInventory)slot.inventory, slot.slot));
    }

    public void dropItem(PlayerInventory inv, int slot, int amount) {
        if (!inv.isSlotClear(slot)) {
            if (amount > inv.getAmount(slot)) {
                amount = inv.getAmount(slot);
            }
            this.player.dropItem(inv.getItem(slot).copy(amount));
            inv.getItem(slot).setAmount(inv.getAmount(slot) - amount);
            if (inv.getAmount(slot) <= 0) {
                inv.setItem(slot, null);
            }
            inv.markDirty(slot);
        }
    }

    public void dropItem(PlayerInventory inv, int slot) {
        this.dropItem(inv, slot, inv.getAmount(slot));
    }

    public void dropItem(int inventoryID, int slot, int amount) {
        PlayerInventory inv = this.getInventoryByID(inventoryID);
        if (inv != null) {
            this.dropItem(inv, slot, amount);
        } else {
            GameLog.warn.println(this.player.getDisplayName() + " tried to drop item from invalid inventory id " + inventoryID);
        }
    }

    public void setItem(PlayerInventorySlot slot, InventoryItem item) {
        slot.setItem(this, item);
    }

    public void setItem(PlayerInventorySlot slot, InventoryItem item, boolean overrideIsNew) {
        slot.setItem(this, item, overrideIsNew);
    }

    public InventoryItem getItem(PlayerInventorySlot slot) {
        return slot.getItem(this);
    }

    public boolean isSlotClear(PlayerInventorySlot slot) {
        return slot.isSlotClear(this);
    }

    public int removeItems(Item item, int amount, boolean includeInactiveSets, boolean includeCloud, boolean includeTrash, boolean includeTemp, String purpose) {
        int startAmount = amount;
        amount -= this.drag.removeItems(this.player.getLevel(), this.player, item, amount, purpose);
        amount -= this.main.removeItems(this.player.getLevel(), this.player, item, amount, purpose);
        amount = this.equipment.removeItems(item, amount, includeInactiveSets, purpose);
        if (includeCloud) {
            amount -= this.cloud.removeItems(this.player.getLevel(), this.player, item, amount, purpose);
        }
        if (includeTrash) {
            amount -= this.trash.removeItems(this.player.getLevel(), this.player, item, amount, purpose);
        }
        if (includeTemp) {
            for (PlayerTempInventory inv : this.tempInvs) {
                amount -= inv.removeItems(this.player.getLevel(), this.player, item, amount, purpose);
            }
        }
        return startAmount - amount;
    }

    public Iterable<PlayerTempInventory> getTempInventories() {
        return this.tempInvs;
    }

    public Packet getTempInventoryPacket(int size) {
        int nextID = this.findNextInvID(this.inventories.size());
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextByteUnsigned(nextID);
        writer.putNextShortUnsigned(size);
        return p;
    }

    public PlayerTempInventory applyTempInventoryPacket(Packet packet, final Function<PlayerInventoryManager, Boolean> shouldDispose) {
        return this.applyTempInventoryPacket(packet, (PlayerMob player, int size1, int invID) -> new PlayerTempInventory(player, size1, invID){

            @Override
            public boolean shouldDispose() {
                return (Boolean)shouldDispose.apply(this.player.getInv());
            }
        });
    }

    public PlayerTempInventory addTempInventory(int size, final Function<PlayerInventoryManager, Boolean> shouldDispose) {
        return this.addTempInventory(size, (PlayerMob player, int size1, int invID) -> new PlayerTempInventory(player, size1, invID){

            @Override
            public boolean shouldDispose() {
                return (Boolean)shouldDispose.apply(this.player.getInv());
            }
        });
    }

    public PlayerTempInventory applyTempInventoryPacket(Packet packet, PlayerTempInventoryConstructor constructor) {
        PacketReader reader = new PacketReader(packet);
        int id = reader.getNextByteUnsigned();
        int size = reader.getNextShortUnsigned();
        return this.addTempInventory(id, size, constructor);
    }

    public PlayerTempInventory addTempInventory(int size, PlayerTempInventoryConstructor constructor) {
        int nextID = this.findNextInvID(this.inventories.size());
        return this.addTempInventory(nextID, size, constructor);
    }

    private PlayerTempInventory addTempInventory(int id, int size, PlayerTempInventoryConstructor constructor) {
        if (this.tempInvs.size() > 200) {
            throw new NullPointerException("Too many work inventories on " + this.player.getDisplayName());
        }
        for (int i = 0; i < this.tempInvs.size(); ++i) {
            PlayerTempInventory inv = this.tempInvs.get(i);
            if (inv.getInventoryID() != id) continue;
            System.out.println("Overrode " + this.player.getDisplayName() + " temp inventory with id " + inv.getInventoryID());
            inv.dispose();
            this.tempInvs.remove(i);
            break;
        }
        PlayerTempInventory inv = constructor.create(this.player, size, id);
        this.tempInvs.add(inv);
        return inv;
    }

    private int findNextInvID(int next) {
        for (PlayerInventory playerInventory : this.tempInvs) {
            if (playerInventory.getInventoryID() != next) continue;
            return this.findNextInvID(++next);
        }
        return next;
    }

    public void closeTempInventories() {
        for (int i = 0; i < this.tempInvs.size(); ++i) {
            PlayerTempInventory inv = this.tempInvs.get(i);
            inv.dispose();
            this.tempInvs.remove(i);
            --i;
        }
    }

    public void dispose() {
        this.closeTempInventories();
    }
}

