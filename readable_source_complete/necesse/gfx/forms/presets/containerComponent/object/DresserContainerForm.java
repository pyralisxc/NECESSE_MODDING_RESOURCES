/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.DresserObjectEntity;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorStandSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.armorItem.ArmorItem;

public class DresserContainerForm
extends OEInventoryContainerForm<OEInventoryContainer> {
    public DresserContainerForm(Client client, OEInventoryContainer container) {
        super(client, container);
    }

    @Override
    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
        int currentY = flow.next();
        for (int i = 0; i < this.slots.length; ++i) {
            ArmorItem.ArmorType armorType;
            int slotIndex = i + ((OEInventoryContainer)this.container).INVENTORY_START;
            int x = i % 10;
            if (x == 0) {
                currentY = flow.next(40);
            }
            this.slots[i] = (armorType = DresserObjectEntity.getArmorType(i)) != null ? (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, slotIndex, 4 + x * 40, currentY, armorType)) : this.inventoryForm.addComponent(new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, currentY));
        }
    }
}

