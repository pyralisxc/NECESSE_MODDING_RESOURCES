/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.MusicOptionsOffset;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.entity.objectEntity.MusicPlayerManager;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.LongCustomAction;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.InternalInventoryItemContainerSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.miscItem.PortableMusicPlayerItem;

public class PortableMusicPlayerContainer
extends ItemInventoryContainer {
    public final BooleanCustomAction setPaused = this.registerAction(new BooleanCustomAction(){

        @Override
        protected void run(boolean value) {
            MusicPlayerManager manager = PortableMusicPlayerContainer.this.getMusicManager();
            if (manager != null) {
                manager.setIsPaused(value);
            }
        }
    });
    public final LongCustomAction setOffset = this.registerAction(new LongCustomAction(){

        @Override
        protected void run(long value) {
            MusicPlayerManager manager = PortableMusicPlayerContainer.this.getMusicManager();
            if (manager != null) {
                manager.setOffset(value);
            }
        }
    });
    public final LongCustomAction forwardMilliseconds = this.registerAction(new LongCustomAction(){

        @Override
        protected void run(long value) {
            MusicPlayerManager manager = PortableMusicPlayerContainer.this.getMusicManager();
            if (manager != null) {
                manager.forwardMilliseconds(value);
            }
        }
    });
    protected MusicOptionsOffset lastMusic;
    protected InventoryItem lastItem;

    public PortableMusicPlayerContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed, content);
    }

    @Override
    public ContainerSlot getItemContainerSlot(Inventory inventory, int slot, InternalInventoryItemInterface internalInventoryItemInterface) {
        return new InternalInventoryItemContainerSlot(inventory, slot, internalInventoryItemInterface){

            @Override
            public void setItem(InventoryItem item) {
                MusicPlayerManager manager = PortableMusicPlayerContainer.this.getMusicManager();
                PortableMusicPlayerContainer.this.lastMusic = manager == null ? null : manager.getCurrentMusic();
                PortableMusicPlayerContainer.this.lastItem = PortableMusicPlayerContainer.this.getInventoryItem();
                super.setItem(item);
            }

            @Override
            public void markDirty() {
                super.markDirty();
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isClient() && this.lastMusic != null && this.lastItem != this.getInventoryItem()) {
            MusicPlayerManager manager = this.getMusicManager();
            if (manager.setDesiredMusic(this.lastMusic.options.music, this.lastMusic.offset)) {
                this.setOffset.runAndSend(manager.getMusicPlayingOffset());
            } else {
                this.setOffset.runAndSend(0L);
            }
            this.lastItem = null;
            this.lastMusic = null;
        }
    }

    public MusicPlayerManager getMusicManager() {
        InventoryItem inventoryItem = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
        if (inventoryItem != null && inventoryItem.item instanceof PortableMusicPlayerItem) {
            return ((PortableMusicPlayerItem)inventoryItem.item).getMusicManager(inventoryItem);
        }
        return null;
    }
}

