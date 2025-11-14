/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ProcessingInventoryObjectEntity;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerProcessingRecipeSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.object.FormProcessingProgressArrow;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.container.object.OEInventoryContainer;

public class ProcessingInventoryContainerForm
extends OEInventoryContainerForm<OEInventoryContainer> {
    private ProcessingInventoryObjectEntity processingObjectEntity;

    public ProcessingInventoryContainerForm(Client client, OEInventoryContainer container) {
        super(client, container);
        this.processingObjectEntity = (ProcessingInventoryObjectEntity)container.objectEntity;
        this.inventoryForm.addComponent(new FormProcessingProgressArrow(this.inventoryForm.getWidth() / 2 - 16, 30 + (this.inventoryForm.getHeight() - 30) / 2 - 16, this.processingObjectEntity.getProcessingHelp()));
    }

    @Override
    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
        ProcessingInventoryObjectEntity processingOE = (ProcessingInventoryObjectEntity)((OEInventoryContainer)this.container).objectEntity;
        int inputSlots = processingOE.inputSlots;
        ProcessingHelp processingHelp = processingOE.getProcessingHelp();
        int startY = flow.next();
        int centerWidth = 40;
        for (int i = 0; i < this.slots.length; ++i) {
            int y;
            int x;
            int slotIndex = i + ((OEInventoryContainer)this.container).INVENTORY_START;
            if (i < inputSlots) {
                int sideWidth = inputSlots * 40;
                x = this.inventoryForm.getWidth() / 2 - sideWidth - centerWidth / 2 + i % 4 * 40;
                y = i / 4 * 40;
            } else {
                int indexOffset = i - inputSlots;
                x = this.inventoryForm.getWidth() / 2 + centerWidth / 2 + indexOffset % 4 * 40;
                y = indexOffset / 4 * 40;
            }
            this.slots[i] = this.inventoryForm.addComponent(new FormContainerProcessingRecipeSlot(this.client, this.container, slotIndex, x, startY + y, processingHelp));
            if (flow.next() >= startY + y + 40) continue;
            flow.next(startY + y + 40 - flow.next());
        }
    }
}

