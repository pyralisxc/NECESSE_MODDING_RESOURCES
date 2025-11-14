/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairSpacerGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.SalvageStationContainer;

public class SalvageStationContainerForm<T extends SalvageStationContainer>
extends ContainerFormSwitcher<T> {
    public Form mainForm = this.addComponent(new Form(408, 120));
    public int rewardsStartY;
    public Collection<FormComponent> rewardComponents;
    public FormLocalTextButton salvageButton;
    private InventoryUpdateListener inventoryUpdateListener;

    public SalvageStationContainerForm(Client client, T container) {
        super(client, container);
        FormFlow flow = new FormFlow(5);
        this.mainForm.addComponent(flow.nextY(new FormLocalLabel(((SalvageStationContainer)container).salvageEntity.getInventoryName(), new FontOptions(20), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 5));
        this.mainForm.addComponent(flow.nextY(new FormLocalLabel("ui", "salvageplaceitems", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 10));
        int slotWidth = 40;
        int edgePadding = 4;
        int usableWidth = this.mainForm.getWidth() - edgePadding * 2;
        int slotsPerRow = Math.max(usableWidth / slotWidth, 1);
        int totalSlots = ((SalvageStationContainer)container).salvageEntity.inventory.getSize();
        int rows = 1 + totalSlots / slotsPerRow;
        if (totalSlots % slotsPerRow == 0) {
            --rows;
        }
        for (int row = 0; row < rows; ++row) {
            int rowY = flow.next(40);
            int slotsInRow = Math.min(totalSlots - row * slotsPerRow, slotsPerRow);
            int startX = this.mainForm.getWidth() / 2 - slotWidth * slotsInRow / 2;
            for (int col = 0; col < slotsInRow; ++col) {
                int inventorySlot = row * slotsPerRow + col;
                FormContainerSlot slot = new FormContainerSlot(client, (Container)container, ((SalvageStationContainer)container).SALVAGE_INVENTORY_START + inventorySlot, startX + col * slotWidth, rowY).setDecal(this.getInterfaceStyle().inventoryslot_icon_trinket);
                this.mainForm.addComponent(slot);
            }
        }
        flow.next(10);
        this.mainForm.addComponent(flow.nextY(new FormLocalLabel("ui", "salvageresults", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 2));
        this.rewardsStartY = flow.next();
        int buttonWidth = Math.min(this.mainForm.getWidth() - 8, 300);
        this.salvageButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "salvagebutton", this.mainForm.getWidth() / 2 - buttonWidth / 2, 0, buttonWidth, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.salvageButton.onClicked(e -> container.salvageButton.runAndSend());
        this.updateRewards();
        this.makeCurrent(this.mainForm);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryUpdateListener = ((SalvageStationContainer)this.container).salvageEntity.inventory.addSlotUpdateListener(new InventoryUpdateListener(){

            @Override
            public void onSlotUpdate(int slot) {
                SalvageStationContainerForm.this.updateRewards();
            }

            @Override
            public boolean isDisposed() {
                return SalvageStationContainerForm.this.isDisposed();
            }
        });
    }

    public void updateRewards() {
        if (this.rewardComponents != null) {
            this.rewardComponents.forEach(this.mainForm::removeComponent);
        }
        this.rewardComponents = new ArrayList<FormComponent>();
        FormFlow flow = new FormFlow(this.rewardsStartY);
        ArrayList<InventoryItem> rewards = ((SalvageStationContainer)this.container).getCurrentSalvageRewards(false);
        if (rewards == null || rewards.isEmpty()) {
            rewards = new ArrayList<InventoryItem>(Collections.singleton(new InventoryItem("upgradeshard", 0)));
        }
        for (InventoryItem reward : rewards) {
            FontOptions fontOptions = new FontOptions(16);
            FairType fairType = new FairType();
            fairType.append(new FairItemGlyph(fontOptions.getSize(), reward));
            fairType.append(new FairSpacerGlyph(5.0f, 2.0f));
            fairType.append(fontOptions, reward.getAmount() + " " + reward.getItemDisplayName());
            FormFairTypeLabel label = new FormFairTypeLabel("", this.mainForm.getWidth() / 2, 0);
            label.setTextAlign(FairType.TextAlign.CENTER);
            label.setMaxWidth(this.mainForm.getWidth() - 20);
            label.setCustomFairType(fairType);
            this.rewardComponents.add(flow.nextY(this.mainForm.addComponent(label), 2));
        }
        flow.next(10);
        flow.nextY(this.salvageButton, 4);
        this.mainForm.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.mainForm);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.inventoryUpdateListener != null) {
            this.inventoryUpdateListener.dispose();
        }
    }
}

