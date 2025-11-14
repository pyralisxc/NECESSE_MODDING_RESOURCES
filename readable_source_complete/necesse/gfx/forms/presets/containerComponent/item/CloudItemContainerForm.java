/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.container.item.CloudItemContainer;

public class CloudItemContainerForm<T extends CloudItemContainer>
extends ContainerForm<T> {
    protected FormContainerSlot[] slots;

    protected CloudItemContainerForm(Client client, T container, int height) {
        super(client, 408, height, container);
        FontOptions labelOptions = new FontOptions(20);
        FormLocalLabel label = this.addComponent(new FormLocalLabel(new StaticMessage(""), labelOptions, -1, 4, 4), -1000);
        FormFlow iconFlow = new FormFlow(this.getWidth() - 4);
        FormContentIconButton stackButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().inventory_quickstack_out, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                Input input = WindowManager.getWindow().getInput();
                StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventoryquickstack"));
                if (input.isKeyDown(340) || input.isKeyDown(344)) {
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
                Input input = WindowManager.getWindow().getInput();
                StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
                if (input.isKeyDown(340) || input.isKeyDown(344)) {
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
        FormContentIconButton restockButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_quickstack_in, new LocalMessage("ui", "inventoryrestock")));
        restockButton.onClicked(e -> container.restockButton.runAndSend());
        restockButton.setCooldown(500);
        FormContentIconButton lootAllButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "inventorylootall")));
        lootAllButton.onClicked(e -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        FormContentIconButton sortButton = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_sort, new LocalMessage("ui", "inventorysort")));
        sortButton.onClicked(e -> container.sortButton.runAndSend());
        sortButton.setCooldown(500);
        label.setLocalization(ItemRegistry.getLocalization(((CloudItemContainer)container).itemID), iconFlow.next() - 8);
        this.addSlots();
    }

    public CloudItemContainerForm(Client client, T container) {
        this(client, container, (((CloudItemContainer)container).CLOUD_END - ((CloudItemContainer)container).CLOUD_START + 1 + 9) / 10 * 40 + 38);
    }

    protected void addSlots() {
        this.slots = new FormContainerSlot[((CloudItemContainer)this.container).CLOUD_END - ((CloudItemContainer)this.container).CLOUD_START + 1];
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((CloudItemContainer)this.container).CLOUD_START;
            int x = i % 10;
            int y = i / 10;
            this.slots[i] = this.addComponent(new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, 4 + y * 40 + 30));
        }
    }
}

