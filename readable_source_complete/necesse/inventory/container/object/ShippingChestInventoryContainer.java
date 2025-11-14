/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.ShippingChestObjectEntity;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;

public class ShippingChestInventoryContainer
extends OEInventoryContainer {
    public final ShippingChestObjectEntity shippingChestObjectEntity;
    public IntCustomAction setStartMissionWhenAboveStacks;

    public ShippingChestInventoryContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, final ShippingChestObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, settlement, objectEntity, reader);
        this.shippingChestObjectEntity = objectEntity;
        this.setStartMissionWhenAboveStacks = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                objectEntity.startMissionWhenCarryingAtLeastStacks = value;
                objectEntity.markDirty();
            }
        });
    }
}

