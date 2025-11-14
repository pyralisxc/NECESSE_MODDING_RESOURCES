/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.container.object.OEInventoryContainer;

public class OEInventoryContainerForm<T extends OEInventoryContainer>
extends ContainerFormSwitcher<T> {
    public Form inventoryForm = this.addComponent(new Form(408, 100), (form, active) -> {
        if (!active.booleanValue()) {
            this.label.setTyping(false);
            this.runEditUpdate();
        }
    });
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public FormContainerSlot[] slots;
    public LocalMessage renameTip;

    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }

    public OEInventoryContainerForm(Client client, T container) {
        super(client, container);
        OEInventory oeInventory = ((OEInventoryContainer)container).oeInventory;
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.inventoryForm.addComponent(new FormLabelEdit("", labelOptions, this.getInterfaceStyle().activeTextColor, 4, 4, this.inventoryForm.getWidth() - 8, 50), -1000);
        this.label.onMouseChangedTyping(e -> this.runEditUpdate());
        this.label.onSubmit(e -> this.runEditUpdate());
        this.label.allowCaretSetTyping = oeInventory.canSetInventoryName();
        this.label.allowItemAppend = true;
        this.label.setParsers(OEInventoryContainerForm.getParsers(labelOptions));
        this.label.setText(oeInventory.getInventoryName().translate());
        FormFlow iconFlow = new FormFlow(this.inventoryForm.getWidth() - 4);
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (oeInventory.canSetInventoryName()) {
            this.edit = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_rename, this.renameTip));
            this.edit.onClicked(e -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }
        if (oeInventory.canQuickStackInventory()) {
            FormContentIconButton stackButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().inventory_quickstack_out, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventoryquickstack"));
                    GameWindow window = WindowManager.getWindow();
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
            FormContentIconButton transferAllButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_loot_all, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
                    GameWindow window = WindowManager.getWindow();
                    if (window.isKeyDown(340) || window.isKeyDown(344)) {
                        tooltips.add(Localization.translate("ui", "inventorytransferallinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), GameColor.LIGHT_GRAY, 400);
                    } else {
                        tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                    }
                    return tooltips;
                }
            }).mirrorY();
            transferAllButton.onClicked(e -> container.transferAll.runAndSend());
            transferAllButton.setCooldown(500);
        }
        if (oeInventory.canRestockInventory()) {
            FormContentIconButton restockButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_quickstack_in, new LocalMessage("ui", "inventoryrestock")));
            restockButton.onClicked(e -> container.restockButton.runAndSend());
            restockButton.setCooldown(500);
        }
        FormContentIconButton lootAllButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "inventorylootall")));
        lootAllButton.onClicked(e -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        if (oeInventory.canSortInventory()) {
            FormContentIconButton sortButton = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_sort, new LocalMessage("ui", "inventorysort")));
            sortButton.onClicked(e -> container.sortButton.runAndSend());
            sortButton.setCooldown(500);
        }
        this.settlementObjectFormManager = ((OEInventoryContainer)container).settlementObjectManager.getFormManager(this, this.inventoryForm, client);
        this.settlementObjectFormManager.addConfigButtonRow(this.inventoryForm, iconFlow, 4, -1);
        this.label.setWidth(iconFlow.next() - 8);
        FormFlow flow = new FormFlow(34);
        this.addSlots(flow);
        flow.next(4);
        this.inventoryForm.setHeight(flow.next());
        this.makeCurrent(this.inventoryForm);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.inventoryForm);
        this.settlementObjectFormManager.onWindowResized();
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
        int currentY = flow.next();
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((OEInventoryContainer)this.container).INVENTORY_START;
            int x = i % 10;
            if (x == 0) {
                currentY = flow.next(40);
            }
            this.slots[i] = this.inventoryForm.addComponent(this.getSlotComponent(slotIndex, 4 + x * 40, currentY));
        }
    }

    protected FormContainerSlot getSlotComponent(int slotIndex, int x, int y) {
        return new FormContainerSlot(this.client, this.container, slotIndex, x, y);
    }

    private void runEditUpdate() {
        OEInventory oeInventory = ((OEInventoryContainer)this.container).oeInventory;
        if (!oeInventory.canSetInventoryName()) {
            return;
        }
        if (this.label.isTyping()) {
            this.edit.setIcon(this.getInterfaceStyle().container_rename_save);
            this.renameTip = new LocalMessage("ui", "savebutton");
        } else {
            if (!this.label.getText().equals(oeInventory.getInventoryName().translate())) {
                oeInventory.setInventoryName(this.label.getText());
                ((OEInventoryContainer)this.container).renameButton.runAndSend(this.label.getText());
            }
            this.edit.setIcon(this.getInterfaceStyle().container_rename);
            this.renameTip = new LocalMessage("ui", "renamebutton");
            this.label.setText(oeInventory.getInventoryName().translate());
        }
        this.edit.setTooltips(this.renameTip);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.settlementObjectFormManager.updateButtons();
        super.draw(tickManager, perspective, renderBox);
    }
}

