/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.MusicPlayerObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.LongCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.OEInventoryContainerSlot;

public class MusicPlayerContainer
extends OEInventoryContainer {
    public final MusicPlayerObjectEntity objectEntity;
    public final BooleanCustomAction setPaused;
    public final LongCustomAction setOffset;
    public final LongCustomAction forwardMilliseconds;
    protected boolean isDirty = false;

    public MusicPlayerContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, OEInventory oeInventory, PacketReader reader) {
        super(client, uniqueSeed, settlement, oeInventory, reader);
        this.objectEntity = (MusicPlayerObjectEntity)oeInventory;
        this.setPaused = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                MusicPlayerContainer.this.objectEntity.getMusicManager().setIsPaused(value);
            }
        });
        this.setOffset = this.registerAction(new LongCustomAction(){

            @Override
            protected void run(long value) {
                MusicPlayerContainer.this.objectEntity.getMusicManager().setOffset(value);
            }
        });
        this.forwardMilliseconds = this.registerAction(new LongCustomAction(){

            @Override
            protected void run(long value) {
                MusicPlayerContainer.this.objectEntity.getMusicManager().forwardMilliseconds(value);
            }
        });
    }

    @Override
    public ContainerSlot getOEContainerSlot(OEInventory oeInventory, int slot) {
        return new OEInventoryContainerSlot(oeInventory, slot){

            @Override
            public void markDirty() {
                super.markDirty();
                MusicPlayerContainer.this.isDirty = true;
            }
        };
    }

    @Override
    public ContainerActionResult applyContainerAction(int slotIndex, ContainerAction action) {
        long nextOffset;
        long deltaOffset;
        long lastOffset = this.objectEntity.getMusicManager().getMusicPlayingOffset();
        ContainerActionResult out = super.applyContainerAction(slotIndex, action);
        if (this.client.isClient() && this.isDirty && Math.abs(deltaOffset = (nextOffset = this.objectEntity.getMusicManager().getMusicPlayingOffset()) - lastOffset) >= 500L) {
            this.setOffset.runAndSend(-nextOffset);
        }
        return out;
    }
}

