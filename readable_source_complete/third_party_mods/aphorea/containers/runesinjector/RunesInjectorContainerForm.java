/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormFlow
 *  necesse.gfx.forms.components.FormLabelEdit
 *  necesse.gfx.forms.components.containerSlot.FormContainerSlot
 *  necesse.gfx.forms.presets.containerComponent.ContainerForm
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 */
package aphorea.containers.runesinjector;

import aphorea.containers.runesinjector.RunesInjectorContainer;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class RunesInjectorContainerForm<T extends RunesInjectorContainer>
extends ContainerForm<T> {
    public FormLabelEdit label;
    public FormContainerSlot[] slots;

    public RunesInjectorContainerForm(Client client, T container) {
        super(client, 408, 100, container);
        InventoryItem inventoryItem = ((RunesInjectorContainer)((Object)container)).getInventoryItem();
        InternalInventoryItemInterface item = ((RunesInjectorContainer)((Object)container)).inventoryItem;
        FontOptions labelOptions = new FontOptions(20);
        this.label = (FormLabelEdit)this.addComponent((FormComponent)new FormLabelEdit(inventoryItem == null ? "NULL" : inventoryItem.getItemDisplayName(), labelOptions, Settings.UI.activeTextColor, 5, 5, this.getWidth() - 10, 50), -1000);
        FormFlow flow = new FormFlow(34);
        this.addSlots(flow);
        flow.next(4);
        this.setHeight(flow.next());
    }

    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((RunesInjectorContainer)this.container).INVENTORY_END - ((RunesInjectorContainer)this.container).INVENTORY_START + 1];
        int currentY = flow.next();
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((RunesInjectorContainer)this.container).INVENTORY_START;
            int x = i % 10;
            if (x == 0) {
                currentY = flow.next(40);
            }
            this.slots[i] = (FormContainerSlot)this.addComponent((FormComponent)new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, currentY));
        }
    }
}

