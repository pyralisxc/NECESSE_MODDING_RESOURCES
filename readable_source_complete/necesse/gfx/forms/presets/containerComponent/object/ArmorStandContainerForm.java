/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorStandSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.armorItem.ArmorItem;

public class ArmorStandContainerForm
extends OEInventoryContainerForm<OEInventoryContainer> {
    public ArmorStandContainerForm(Client client, OEInventoryContainer container) {
        super(client, container);
    }

    @Override
    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
        if (this.slots.length != 3) {
            super.addSlots(flow);
        } else {
            int currentY = flow.next(40);
            this.slots[0] = this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START, 4, currentY, ArmorItem.ArmorType.HEAD));
            this.slots[1] = this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START + 1, 44, currentY, ArmorItem.ArmorType.CHEST));
            this.slots[2] = this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START + 2, 84, currentY, ArmorItem.ArmorType.FEET));
        }
    }
}

