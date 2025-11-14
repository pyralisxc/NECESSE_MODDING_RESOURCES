/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.containerSlot.FormContainerSlot
 *  necesse.gfx.forms.presets.containerComponent.ContainerForm
 *  necesse.inventory.container.Container
 */
package tomeofpower.containers.trinket;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.inventory.container.Container;
import tomeofpower.containers.trinket.TrinketInventoryContainer;

public class TrinketInventoryContainerForm
extends ContainerForm<TrinketInventoryContainer> {
    public TrinketInventoryContainerForm(Client client, TrinketInventoryContainer container) {
        super(client, 0, 100, (Container)container);
        int totalSlots = container.getInventorySize();
        int cols = (int)Math.ceil(Math.sqrt(totalSlots));
        int rows = (int)Math.ceil((double)totalSlots / (double)cols);
        for (int i = 0; i < totalSlots; ++i) {
            int actualSlotIndex = container.getTrinketSlotIndex(i);
            FormContainerSlot slot = new FormContainerSlot(client, (Container)container, actualSlotIndex, 0, 0);
            this.addComponent((FormComponent)slot);
            int row = i / cols;
            int col = i % cols;
            slot.setPosition(10 + col * 40, 10 + row * 40);
        }
        int formWidth = 20 + cols * 40;
        int formHeight = 20 + rows * 40;
        this.setWidth(formWidth);
        this.setHeight(formHeight);
    }
}

