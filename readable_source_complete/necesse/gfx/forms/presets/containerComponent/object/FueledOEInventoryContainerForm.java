/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.FueledOEInventoryContainer;

public class FueledOEInventoryContainerForm<T extends FueledOEInventoryContainer>
extends ContainerFormSwitcher<T> {
    private Form inventoryForm = this.addComponent(new Form("inventoryForm", 120, 100));
    private FormLocalCheckBox keepRunningCheckbox;
    public SettlementObjectStatusFormManager settlementObjectFormManager;

    public FueledOEInventoryContainerForm(Client client, T container) {
        super(client, container);
        int columns = 2;
        int inventorySize = ((FueledOEInventoryContainer)container).INVENTORY_END - ((FueledOEInventoryContainer)container).INVENTORY_START + 1;
        int rows = (inventorySize + columns - 1) / columns;
        this.inventoryForm.setHeight(rows * 40 + 70);
        FormLocalLabel label = this.inventoryForm.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(20), -1, 4, 6));
        Rectangle labelBoundingBox = label.getBoundingBox();
        if (labelBoundingBox.width > this.inventoryForm.getWidth() - 8 - 28) {
            this.inventoryForm.setWidth(labelBoundingBox.width + 8 + 28);
        } else {
            label.setX((this.inventoryForm.getWidth() - 8 - 28 - labelBoundingBox.width) / 2);
        }
        if (!((FueledOEInventoryContainer)container).objectEntity.alwaysOn) {
            this.keepRunningCheckbox = this.inventoryForm.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.inventoryForm.getHeight() - 10, ((FueledOEInventoryContainer)container).objectEntity.keepRunning, this.inventoryForm.getWidth() - 10));
            this.keepRunningCheckbox.onClicked(e -> container.setKeepRunning.runAndSend(((FormCheckBox)e.from).checked));
            Rectangle boundingBox = this.keepRunningCheckbox.getBoundingBox();
            this.keepRunningCheckbox.setPosition(this.inventoryForm.getWidth() / 2 - boundingBox.width / 2, this.keepRunningCheckbox.getY());
            this.inventoryForm.setHeight(this.inventoryForm.getHeight() + boundingBox.height);
        }
        this.inventoryForm.addComponent(new FormCustomDraw(this.inventoryForm.getWidth() / 2 - 40, 36, 80, this.getInterfaceStyle().progressbar_small_empty.getHeight(), (FueledOEInventoryContainer)container){
            final /* synthetic */ FueledOEInventoryContainer val$container;
            {
                this.val$container = fueledOEInventoryContainer;
                super(x, y, width, height);
            }

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 0, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 1, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
                float progress = 0.0f;
                if (this.val$container.objectEntity.isFueled()) {
                    progress = this.val$container.objectEntity.getFuelProgressLeft();
                    int progressWidth = (int)(progress * (float)this.width);
                    FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_full, 0, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_full, 1, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), this.getX(), this.getY(), progressWidth);
                }
                if (this.isHovering()) {
                    GameTooltipManager.addTooltip(new StringTooltips((int)(progress * 100.0f) + "%"), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        for (int i = ((FueledOEInventoryContainer)container).INVENTORY_START; i <= ((FueledOEInventoryContainer)container).INVENTORY_END; ++i) {
            int index = i - ((FueledOEInventoryContainer)container).INVENTORY_START;
            int column = index % columns;
            int row = index / columns;
            int slotsInRow = Math.min(inventorySize - row * columns, columns);
            int xOffset = slotsInRow * 20;
            this.inventoryForm.addComponent(new FormContainerSlot(client, (Container)container, i, this.inventoryForm.getWidth() / 2 + column * 40 - xOffset, row * 40 + 50)).setDecal(this.getInterfaceStyle().inventoryslot_icon_fuel);
        }
        this.settlementObjectFormManager = ((FueledOEInventoryContainer)container).settlementObjectManager.getFormManager(this, this.inventoryForm, client);
        this.settlementObjectFormManager.addConfigButtonRow(this.inventoryForm, new FormFlow(this.inventoryForm.getWidth() - 4), 4, -1);
        this.makeCurrent(this.inventoryForm);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.inventoryForm);
        this.settlementObjectFormManager.onWindowResized();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.settlementObjectFormManager.updateButtons();
        if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != ((FueledOEInventoryContainer)this.container).objectEntity.keepRunning) {
            this.keepRunningCheckbox.checked = ((FueledOEInventoryContainer)this.container).objectEntity.keepRunning;
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

