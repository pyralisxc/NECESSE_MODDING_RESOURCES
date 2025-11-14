/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.util.function.Supplier;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class ItemInventoryContainerForm<T extends ItemInventoryContainer>
extends ContainerForm<T> {
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public LocalMessage renameTip;
    public FormContainerSlot[] slots;

    public ItemInventoryContainerForm(Client client, T container) {
        super(client, 408, 100, container);
        InventoryItem inventoryItem = ((ItemInventoryContainer)container).getInventoryItem();
        InternalInventoryItemInterface item = ((ItemInventoryContainer)container).inventoryItem;
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.addComponent(new FormLabelEdit("", labelOptions, this.getInterfaceStyle().activeTextColor, 5, 5, this.getWidth() - 10, 50), -1000);
        this.label.onMouseChangedTyping(e -> this.runEditUpdate());
        this.label.onSubmit(e -> this.runEditUpdate());
        this.label.allowCaretSetTyping = item.canChangePouchName();
        this.label.allowItemAppend = true;
        this.label.setParsers(OEInventoryContainerForm.getParsers(labelOptions));
        this.label.setText(inventoryItem == null ? "NULL" : inventoryItem.getItemDisplayName());
        FormFlow iconFlow = new FormFlow(this.getWidth() - 4);
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (item.canChangePouchName()) {
            this.edit = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_rename, this.renameTip));
            this.edit.onClicked(e -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }
        if (item.canQuickStackInventory()) {
            FormContentIconButton stackButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().inventory_quickstack_out, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    GameWindow window = WindowManager.getWindow();
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventoryquickstack"));
                    if (window.isKeyDown(340) || window.isKeyDown(344)) {
                        tooltips.add(Localization.translate("ui", "inventoryquickstackinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), GameColor.LIGHT_GRAY, 400);
                    } else {
                        tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                    }
                    return tooltips;
                }
            });
            stackButton.onClicked(e -> container.quickStackButton.runAndSend());
            stackButton.setCooldown(500);
            FormContentIconButton transferAllButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_loot_all, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    GameWindow window = WindowManager.getWindow();
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
                    if (window.isKeyDown(340) || window.isKeyDown(344)) {
                        tooltips.add(Localization.translate("ui", "inventorytransferallinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), GameColor.LIGHT_GRAY, 400);
                    } else {
                        tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                    }
                    return tooltips;
                }
            });
            transferAllButton.mirrorY();
            transferAllButton.onClicked(e -> container.transferAll.runAndSend());
            transferAllButton.setCooldown(500);
        }
        if (item.canRestockInventory()) {
            FormContentIconButton restockButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_quickstack_in, new LocalMessage("ui", "inventoryrestock")));
            restockButton.onClicked(e -> container.restockButton.runAndSend());
            restockButton.setCooldown(500);
        }
        FormContentIconButton lootAllButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "inventorylootall")));
        lootAllButton.onClicked(e -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        if (item.canSortInventory()) {
            FormContentIconButton sortButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_sort, new LocalMessage("ui", "inventorysort")));
            sortButton.onClicked(e -> container.sortButton.runAndSend());
            sortButton.setCooldown(500);
        }
        if (item.canDisablePickup()) {
            final Supplier<Boolean> isDisabled = () -> {
                InventoryItem invItem = container.getInventoryItem();
                return invItem != null && container.inventoryItem.isPickupDisabled(invItem);
            };
            FormContentIconButton disablePickupButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, null, new GameMessage[0], (ItemInventoryContainer)container){
                final /* synthetic */ ItemInventoryContainer val$container;
                {
                    this.val$container = itemInventoryContainer;
                    super(x, y, size, color, icon, tooltips);
                }

                @Override
                protected void drawContent(int x, int y, int width, int height) {
                    this.setIcon((Boolean)isDisabled.get() != false ? this.getInterfaceStyle().button_escaped_20 : this.getInterfaceStyle().button_checked_20);
                    super.drawContent(x, y, width, height);
                }

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    return this.val$container.inventoryItem.getPickupToggleTooltip((Boolean)isDisabled.get());
                }
            });
            disablePickupButton.onClicked(e -> container.setPickupDisabled.runAndSend((Boolean)isDisabled.get() == false));
        }
        this.label.setWidth(iconFlow.next() - 10);
        FormFlow flow = new FormFlow(34);
        this.addSlots(flow);
        flow.next(4);
        this.setHeight(flow.next());
    }

    protected void runEditUpdate() {
        InternalInventoryItemInterface item = ((ItemInventoryContainer)this.container).inventoryItem;
        if (!item.canChangePouchName()) {
            return;
        }
        if (this.label.isTyping()) {
            this.edit.setIcon(this.getInterfaceStyle().container_rename_save);
            this.renameTip = new LocalMessage("ui", "savebutton");
        } else {
            InventoryItem inventoryItem = ((ItemInventoryContainer)this.container).getInventoryItem();
            if (inventoryItem == null) {
                return;
            }
            if (!this.label.getText().equals(item.getPouchName(inventoryItem))) {
                item.setPouchName(inventoryItem, this.label.getText());
                ((ItemInventoryContainer)this.container).renameButton.runAndSend(this.label.getText());
            }
            this.edit.setIcon(this.getInterfaceStyle().container_rename);
            this.renameTip = new LocalMessage("ui", "renamebutton");
            this.label.setText(inventoryItem.getItemDisplayName());
        }
        this.edit.setTooltips(this.renameTip);
    }

    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((ItemInventoryContainer)this.container).INVENTORY_END - ((ItemInventoryContainer)this.container).INVENTORY_START + 1];
        int currentY = flow.next();
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((ItemInventoryContainer)this.container).INVENTORY_START;
            int x = i % 10;
            if (x == 0) {
                currentY = flow.next(40);
            }
            this.slots[i] = this.addComponent(new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, currentY));
        }
    }
}

