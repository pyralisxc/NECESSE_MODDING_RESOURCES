/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;

public class FueledOEInventoryContainer
extends OEInventoryContainer {
    public final FueledInventoryObjectEntity objectEntity;
    public BooleanCustomAction setKeepRunning;

    public FueledOEInventoryContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, OEInventory oeInventory, PacketReader reader) {
        super(client, uniqueSeed, settlement, oeInventory, reader);
        this.objectEntity = (FueledInventoryObjectEntity)oeInventory;
        this.setKeepRunning = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                FueledOEInventoryContainer.this.objectEntity.keepRunning = value;
                FueledOEInventoryContainer.this.objectEntity.markFuelDirty();
            }
        });
    }
}

