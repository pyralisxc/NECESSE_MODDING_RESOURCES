/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;

public class FueledProcessingOEInventoryContainer
extends OEInventoryContainer {
    public final FueledProcessingInventoryObjectEntity fueledProcessingObjectEntity;
    public BooleanCustomAction setKeepRunning;

    public FueledProcessingOEInventoryContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, FueledProcessingInventoryObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, settlement, objectEntity, reader);
        this.fueledProcessingObjectEntity = objectEntity;
        this.setKeepRunning = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                if (FueledProcessingOEInventoryContainer.this.fueledProcessingObjectEntity.shouldBeAbleToChangeKeepFuelRunning()) {
                    FueledProcessingOEInventoryContainer.this.fueledProcessingObjectEntity.setKeepFuelRunning(value);
                }
            }
        });
    }
}

