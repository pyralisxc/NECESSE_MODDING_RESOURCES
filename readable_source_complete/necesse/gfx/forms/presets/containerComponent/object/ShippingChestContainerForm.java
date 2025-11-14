/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.containerSlot.FormContainerBrokerSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.ShippingChestInventoryContainer;

public class ShippingChestContainerForm<T extends ShippingChestInventoryContainer>
extends ContainerFormList<T> {
    protected OEInventoryContainerForm<T> inventoryContainerForm;
    protected FormFairTypeLabel valueLabel;

    public ShippingChestContainerForm(Client client, T container) {
        super(client, container);
        this.inventoryContainerForm = this.addComponent(new OEInventoryContainerForm<T>(client, (ShippingChestInventoryContainer)container){

            @Override
            protected void addSlots(FormFlow flow) {
                this.inventoryForm.addComponent(flow.nextY(new FormLocalLabel("ui", "shippingchesttip", new FontOptions(16), -1, 8, 0, this.inventoryForm.getWidth() - 16), 8));
                this.inventoryForm.addComponent(flow.nextY(new FormLocalLabel("ui", "shippingcheststartwhenabove", new FontOptions(16), -1, 8, 0, this.inventoryForm.getWidth() - 16), 8));
                FormSlider slider = new FormSlider("", 8, 0, ((ShippingChestInventoryContainer)this.container).shippingChestObjectEntity.startMissionWhenCarryingAtLeastStacks, 1, ((ShippingChestInventoryContainer)this.container).shippingChestObjectEntity.slots, this.inventoryForm.getWidth() - 16, new FontOptions(16));
                slider.drawValueInPercent = false;
                this.inventoryForm.addComponent(flow.nextY(slider, 4));
                slider.onChanged(e -> ((ShippingChestInventoryContainer)this.container).setStartMissionWhenAboveStacks.runAndSend(slider.getValue()));
                ShippingChestContainerForm.this.valueLabel = this.inventoryForm.addComponent(new FormFairTypeLabel(new StaticMessage(), new FontOptions(16), FairType.TextAlign.LEFT, 8, flow.next(28)));
                ShippingChestContainerForm.this.updateValueLabel();
                super.addSlots(flow);
            }

            @Override
            protected FormContainerSlot getSlotComponent(int slotIndex, int x, int y) {
                return new FormContainerBrokerSlot(this.client, this.container, slotIndex, x, y);
            }
        });
    }

    public void updateValueLabel() {
        float value = 0.0f;
        Inventory inventory = ((ShippingChestInventoryContainer)this.container).oeInventory.getInventory();
        if (inventory != null) {
            value = (float)inventory.streamSlots().mapToDouble(slot -> {
                InventoryItem item = slot.getItem();
                return item == null ? 0.0 : (double)item.getBrokerValue();
            }).sum();
        }
        FairType fairType = new FairType();
        FontOptions fontOptions = this.valueLabel.getFontOptions();
        fairType.append(fontOptions, Localization.translate("ui", "shippingchestvalue"));
        fairType.append(new FairItemGlyph(fontOptions.getSize(), new InventoryItem("coin")).dontShowTooltip());
        fairType.append(fontOptions, Integer.toString((int)value));
        this.valueLabel.setCustomFairType(fairType);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inventoryContainerForm.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateValueLabel();
        super.draw(tickManager, perspective, renderBox);
    }
}

