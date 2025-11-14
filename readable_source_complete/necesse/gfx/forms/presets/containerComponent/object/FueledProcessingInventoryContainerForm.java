/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerProcessingRecipeSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.FormProcessingProgressArrow;
import necesse.gfx.forms.presets.containerComponent.object.FuelContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.container.object.FueledProcessingOEInventoryContainer;

public class FueledProcessingInventoryContainerForm
extends ContainerFormList<FueledProcessingOEInventoryContainer> {
    protected OEInventoryContainerForm<FueledProcessingOEInventoryContainer> containerForm;
    protected FuelContainerForm fuelForm;

    public FueledProcessingInventoryContainerForm(Client client, FueledProcessingOEInventoryContainer container) {
        super(client, container);
        final int fuelSlots = container.fueledProcessingObjectEntity.fuelSlots;
        final int inputSlots = container.fueledProcessingObjectEntity.inputSlots;
        this.containerForm = this.addComponent(new OEInventoryContainerForm<FueledProcessingOEInventoryContainer>(client, container){

            @Override
            protected void addSlots(FormFlow flow) {
                this.slots = new FormContainerSlot[((FueledProcessingOEInventoryContainer)this.container).INVENTORY_END - ((FueledProcessingOEInventoryContainer)this.container).INVENTORY_START + 1 - fuelSlots];
                ProcessingHelp processingHelp = ((FueledProcessingOEInventoryContainer)this.container).fueledProcessingObjectEntity.getProcessingHelp();
                int startY = flow.next();
                int centerWidth = 40;
                for (int i = 0; i < this.slots.length; ++i) {
                    int y;
                    int x;
                    int slotIndex = i + ((FueledProcessingOEInventoryContainer)this.container).INVENTORY_START + fuelSlots;
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
        });
        this.containerForm.inventoryForm.addComponent(new FormProcessingProgressArrow(this.containerForm.inventoryForm.getWidth() / 2 - 16, 30 + (this.containerForm.inventoryForm.getHeight() - 30) / 2 - 16, container.fueledProcessingObjectEntity.getProcessingHelp()));
        this.fuelForm = this.addComponent(new FuelContainerForm(client, container, container.INVENTORY_START, container.INVENTORY_START + fuelSlots - 1, this.getInterfaceStyle().inventoryslot_icon_fuel, container.fueledProcessingObjectEntity.fuelAlwaysOn, container.fueledProcessingObjectEntity.shouldBeAbleToChangeKeepFuelRunning() ? container.setKeepRunning : null, container.fueledProcessingObjectEntity::shouldKeepFuelRunning, container.fueledProcessingObjectEntity::getFuelProgress));
        this.fuelForm.setPosition(new FormRelativePosition((FormPositionContainer)this.containerForm.inventoryForm, -this.fuelForm.getWidth() - this.getInterfaceStyle().formSpacing, -Math.max(0, this.fuelForm.getHeight() - this.containerForm.inventoryForm.getHeight())));
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.fuelForm.setHidden(!this.containerForm.isCurrent(this.containerForm.inventoryForm));
        super.draw(tickManager, perspective, renderBox);
    }
}

