/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.objectEntity.MusicPlayerManager;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.item.miscItem.VinylItem;

public class GNDMusicPlayerInventory
extends GNDItem {
    public Inventory inventory;
    public IntMusicPlayerManager manager;
    public boolean isMusicManagerDirty;
    public boolean shouldUpdateClientPlaying;

    public GNDMusicPlayerInventory(final Inventory inventory) {
        this.inventory = inventory;
        inventory.addSlotUpdateListener(new InventoryUpdateListener(){

            @Override
            public void onSlotUpdate(int slot) {
                GNDMusicPlayerInventory.this.manager.updatePlaying(inventory, slot, true);
            }

            @Override
            public boolean isDisposed() {
                return false;
            }
        });
        this.manager = new IntMusicPlayerManager(inventory.getSize());
    }

    public GNDMusicPlayerInventory(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDMusicPlayerInventory(LoadData data) {
        this.inventory = InventorySave.loadSave(data.getFirstLoadDataByName("inventory"));
        this.manager = new IntMusicPlayerManager(this.inventory.getSize());
        this.updatePlayingAll();
        LoadData musicData = data.getFirstLoadDataByName("music");
        if (musicData != null) {
            this.manager.applyLoadData(musicData);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("music[").append(this.inventory.getSize()).append("]{");
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            s.append("[").append(i).append(":");
            s.append(this.toString(this.inventory.getItem(i)));
            s.append("]");
        }
        s.append("}");
        return s.toString();
    }

    private String toString(InventoryItem item) {
        return item.item.getStringID() + ":" + item.getAmount() + ":" + item.isLocked() + ":" + item.isNew() + ":" + item.getGndData().toString();
    }

    @Override
    public boolean isDefault() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getAmount(i) <= 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDMusicPlayerInventory) {
            GNDMusicPlayerInventory other = (GNDMusicPlayerInventory)item;
            if (this.inventory.getSize() != other.inventory.getSize()) {
                return false;
            }
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                if (this.inventory.isSlotClear(i) != other.inventory.isSlotClear(i)) {
                    return false;
                }
                if (this.inventory.isSlotClear(i) || this.inventory.getItem(i).equals(null, other.inventory.getItem(i), "equals")) continue;
                return false;
            }
            return this.manager.isSame(other.manager);
        }
        return false;
    }

    @Override
    public GNDMusicPlayerInventory copy() {
        GNDMusicPlayerInventory out = new GNDMusicPlayerInventory(Inventory.getInventory(this.inventory.getContentPacket()));
        Packet managerPacket = new Packet();
        this.manager.writeContentPacket(new PacketWriter(managerPacket), true);
        out.manager.readContentPacket(new PacketReader(managerPacket), true);
        return out;
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addSaveData(InventorySave.getSave(this.inventory, "inventory"));
        SaveData musicData = new SaveData("music");
        this.manager.addSaveData(musicData);
        data.addSaveData(musicData);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        this.inventory.writeContent(writer);
        this.manager.writeContentPacket(writer, true);
    }

    @Override
    public void readPacket(PacketReader reader) {
        if (this.inventory == null) {
            this.inventory = Inventory.getInventory(reader);
        } else {
            this.inventory.override(Inventory.getInventory(reader), true, true);
        }
        if (this.manager == null) {
            this.manager = new IntMusicPlayerManager(this.inventory.getSize());
        }
        this.manager.readContentPacket(reader, true);
        this.updatePlayingAll();
        this.shouldUpdateClientPlaying = true;
    }

    public void fixSlots() {
        this.manager.fixSlots(this.inventory, true);
    }

    public void updatePlaying(int slot) {
        this.manager.updatePlaying(this.inventory, slot, true);
    }

    public void updatePlayingAll() {
        this.manager.updatePlayingAll(this.inventory, true);
    }

    public void updateHasAnyVinyls() {
        this.manager.updateHasAnyVinyls(true);
    }

    public void updateClientPlaying() {
        if (this.shouldUpdateClientPlaying) {
            this.manager.superUpdateClientPlaying();
            this.shouldUpdateClientPlaying = false;
        }
    }

    public boolean hasAnyVinyls() {
        return this.manager.hasAnyVinyls();
    }

    public MusicOptionsOffset getCurrentMusic() {
        return this.manager.getCurrentMusic();
    }

    public MusicOptions getPreviousMusic() {
        return this.manager.getPreviousMusic();
    }

    public void setIsPaused(boolean isPaused) {
        this.manager.setIsPaused(isPaused);
    }

    public boolean isPaused() {
        return this.manager.isPaused();
    }

    public void forwardMilliseconds(long milliseconds) {
        this.manager.forwardMilliseconds(milliseconds);
    }

    public void setOffset(long milliseconds) {
        this.manager.setOffset(milliseconds);
    }

    public long getMusicPlayingOffset() {
        return this.manager.getMusicPlayingOffset();
    }

    public MusicList getCurrentMusicList() {
        return this.manager.getCurrentMusicList();
    }

    private class IntMusicPlayerManager
    extends MusicPlayerManager {
        public IntMusicPlayerManager(int slots) {
            super(slots);
        }

        @Override
        public void markDirty() {
            GNDMusicPlayerInventory.this.isMusicManagerDirty = true;
        }

        @Override
        public void updateClientPlaying() {
            GNDMusicPlayerInventory.this.shouldUpdateClientPlaying = true;
        }

        public void superUpdateClientPlaying() {
            this.currentMusicList = new MusicList();
            for (int slot = 0; slot < this.currentVinyls.length; ++slot) {
                VinylItem item = this.currentVinyls[slot];
                if (item == null) continue;
                MusicOptions next = new MusicPlayerManager.ItemMusicOptions(item, slot).fadeInTime(1000).volume(1.5f);
                this.currentMusicList.addMusic(next);
            }
        }
    }
}

