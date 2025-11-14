/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventory;

public class PlayerEquipmentInventory
extends PlayerInventory {
    public final int setIndex;
    protected final ArrayList<PlayerEquipmentInventory> setInventories;
    protected final HashMap<Integer, Integer> slotProxies = new HashMap();
    protected final HashSet<Integer> dirtySlotProxies = new HashSet();

    public PlayerEquipmentInventory(PlayerMob player, int size, boolean sizeCanChange, boolean canBeUsedForCrafting, boolean canLock, ArrayList<PlayerEquipmentInventory> setInventories, int setIndex) {
        super(player, size, sizeCanChange, canBeUsedForCrafting, canLock);
        this.setInventories = setInventories;
        this.setIndex = setIndex;
    }

    public void addSaveData(SaveData save, String inventorySaveName) {
        save.addSaveData(InventorySave.getSave(this, inventorySaveName));
        ArrayList<Integer> proxies = new ArrayList<Integer>(this.slotProxies.size() * 2);
        for (Map.Entry<Integer, Integer> entry : this.slotProxies.entrySet()) {
            if (entry.getValue() == this.setIndex) continue;
            proxies.add(entry.getKey());
            proxies.add(entry.getValue());
        }
        if (!proxies.isEmpty()) {
            save.addIntObjectArray(inventorySaveName + "Proxy", proxies.toArray(new Integer[0]));
        }
    }

    public void applyLoadData(LoadData save, String inventorySaveName, boolean overrideSize) {
        LoadData invSave = save.getFirstLoadDataByName(inventorySaveName);
        if (invSave != null) {
            this.override(InventorySave.loadSave(invSave), overrideSize, false);
        }
        this.slotProxies.clear();
        int[] proxy = save.getIntArray(inventorySaveName + "Proxy", new int[0], false);
        for (int i = 0; i < proxy.length && i + 1 < proxy.length; i += 2) {
            int slot = proxy[i];
            int index = proxy[i + 1];
            this.slotProxies.put(slot, index);
        }
    }

    public void cleanInvalidProxies() {
        HashSet<Integer> removes = new HashSet<Integer>();
        for (Map.Entry<Integer, Integer> entry : this.slotProxies.entrySet()) {
            if (entry.getValue() < this.setInventories.size()) continue;
            removes.add(entry.getKey());
        }
        for (Integer key : removes) {
            this.slotProxies.remove(key);
        }
    }

    public void writePlayerEquipmentContent(PacketWriter writer) {
        this.writeContent(writer);
        this.writeEquipmentProxies(writer);
    }

    public void writeEquipmentProxies(PacketWriter writer) {
        writer.putNextShortUnsigned(this.slotProxies.size());
        for (Map.Entry<Integer, Integer> entry : this.slotProxies.entrySet()) {
            writer.putNextShortUnsigned(entry.getKey());
            writer.putNextShortUnsigned(entry.getValue());
        }
    }

    public void readPlayerEquipmentContent(PacketReader reader) {
        this.override(Inventory.getInventory(reader));
        this.readEquipmentProxies(reader);
    }

    public void readEquipmentProxies(PacketReader reader) {
        this.slotProxies.clear();
        int proxies = reader.getNextShortUnsigned();
        for (int i = 0; i < proxies; ++i) {
            int slot = reader.getNextShortUnsigned();
            int value = reader.getNextShortUnsigned();
            this.slotProxies.put(slot, value);
        }
    }

    public void setProxy(int slot, int setIndex) {
        if (slot < 0 || slot >= this.getSize()) {
            return;
        }
        if (setIndex == this.setIndex) {
            this.slotProxies.remove(slot);
            this.updateSlot(slot);
        } else {
            this.slotProxies.put(slot, setIndex);
            InventoryItem currentItem = this.getItem(slot);
            if (currentItem != null) {
                this.player.getInv().addItemsDropRemaining(currentItem, "addback", this.player, false, true);
                this.setItem(slot, null);
            } else {
                this.updateSlot(slot);
            }
        }
        this.dirtySlotProxies.add(slot);
    }

    public boolean isCurrentlySelected() {
        return this.player.getInv().equipment.getSelectedSet() == this.setIndex;
    }

    public int getProxy(int slot) {
        int proxy = this.slotProxies.getOrDefault(slot, this.setIndex);
        if (proxy < 0 || proxy >= this.setInventories.size()) {
            this.slotProxies.remove(slot);
            return this.setIndex;
        }
        return proxy;
    }

    public PlayerEquipmentInventory getCurrentUsedInventory(int slot) {
        int next;
        int proxy = this.getProxy(slot);
        if (proxy == this.setIndex) {
            return this;
        }
        HashSet<Integer> visited = new HashSet<Integer>();
        visited.add(this.setIndex);
        visited.add(proxy);
        while (visited.add(next = this.setInventories.get(proxy).getProxy(slot))) {
            proxy = next;
        }
        return this.setInventories.get(proxy);
    }

    public InventorySlot getCurrentUsedSlot(int slot) {
        return new InventorySlot(this.getCurrentUsedInventory(slot), slot);
    }

    public InventoryItem getCurrentUsedItem(int slot) {
        return this.getCurrentUsedInventory(slot).getItem(slot);
    }
}

