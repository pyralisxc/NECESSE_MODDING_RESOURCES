/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerBrokerSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.events.FullShopStockUpdateEvent;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.events.SingleShopStockUpdateEvent;
import necesse.inventory.container.mob.PawnbrokerContainer;

public class PawnbrokerContainerForm<T extends PawnbrokerContainer>
extends ShopContainerForm<T> {
    public Form pawnForm;
    public FormContainerSlot[] slots;
    public FormFairTypeLabel profitLabel;
    public FormFairTypeLabel wealthLabel;
    public int lastProfit;

    public PawnbrokerContainerForm(Client client, T container) {
        super(client, container, 408, defaultHeight, defaultHeight);
        this.pawnForm = this.addComponent(new Form("pawnForm", 408, (((PawnbrokerContainer)container).inventory.getSize() + 9) / 10 * 40 + 70 + 8), (form, active) -> container.setIsPawning.runAndSend((boolean)active));
        String headerText = MobRegistry.getLocalization(((PawnbrokerContainer)container).humanShop.getID()).translate();
        FormLabel label = this.pawnForm.addComponent(new FormLabel("", new FontOptions(20), -1, 4, 4), -1000);
        FormFlow iconFlow = new FormFlow(this.pawnForm.getWidth() - 4);
        FormContentIconButton stackButton = this.pawnForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().inventory_quickstack_out, new GameMessage[0]){

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
        FormContentIconButton transferAllButton = this.pawnForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_loot_all, new GameMessage[0]){

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
        });
        transferAllButton.mirrorY();
        transferAllButton.onClicked(e -> container.transferAll.runAndSend());
        transferAllButton.setCooldown(500);
        FormContentIconButton restockButton = this.pawnForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_quickstack_in, new LocalMessage("ui", "inventoryrestock")));
        restockButton.onClicked(e -> container.restockButton.runAndSend());
        restockButton.setCooldown(500);
        FormContentIconButton lootAllButton = this.pawnForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "inventorylootall")));
        lootAllButton.onClicked(e -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        headerText = GameUtils.maxString(headerText, new FontOptions(20), iconFlow.next() - 8);
        label.setText(headerText, iconFlow.next() - 8);
        this.slots = new FormContainerSlot[((PawnbrokerContainer)container).INVENTORY_END - ((PawnbrokerContainer)container).INVENTORY_START + 1];
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((PawnbrokerContainer)container).INVENTORY_START;
            int x = i % 10;
            int y = i / 10;
            this.slots[i] = this.pawnForm.addComponent(new FormContainerBrokerSlot(client, (Container)container, slotIndex, 4 + x * 40, 4 + y * 40 + 30));
        }
        if (((PawnbrokerContainer)container).shopWealth >= 0) {
            this.wealthLabel = this.pawnForm.addComponent(new FormFairTypeLabel(new StaticMessage(""), new FontOptions(16), FairType.TextAlign.LEFT, 10, this.pawnForm.getHeight() - 44));
        }
        this.profitLabel = this.pawnForm.addComponent(new FormFairTypeLabel(new StaticMessage(""), new FontOptions(16), FairType.TextAlign.LEFT, 10, this.pawnForm.getHeight() - 26));
        this.lastProfit = ((PawnbrokerContainer)container).getProfit();
        this.pawnForm.addComponent(new FormLocalTextButton("ui", "sellbutton", this.pawnForm.getWidth() - 154, this.pawnForm.getHeight() - 40, 150)).onClicked(e -> {
            int profit = container.getProfit();
            if (profit > container.shopWealth) {
                int lostValue = profit - container.shopWealth;
                ConfirmationForm confirmationForm = new ConfirmationForm("confirmPawning", 400, 400);
                confirmationForm.setupConfirmation(new LocalMessage("ui", "brokerconfirmsell", "value", lostValue), () -> {
                    container.sellButton.runAndSend(container.shopWealth);
                    this.makeCurrent(this.pawnForm);
                }, () -> this.makeCurrent(this.pawnForm));
                this.addAndMakeCurrentTemporary(confirmationForm);
            } else {
                container.sellButton.runAndSend(container.shopWealth);
            }
        });
        this.updateProfitLabel(this.lastProfit);
        this.updateWealthLabel();
    }

    @Override
    protected void init() {
        super.init();
        ((PawnbrokerContainer)this.container).onEvent(ShopWealthUpdateEvent.class, e -> this.updateWealthLabel());
        ((PawnbrokerContainer)this.container).onEvent(FullShopStockUpdateEvent.class, e -> this.updateWealthLabel());
        ((PawnbrokerContainer)this.container).onEvent(SingleShopStockUpdateEvent.class, e -> this.updateWealthLabel());
    }

    public void updateProfitLabel(int nextProfit) {
        FairType fairType = new FairType();
        FontOptions fontOptions = this.profitLabel.getFontOptions();
        fairType.append(fontOptions, Localization.translate("ui", "brokerprofit"));
        fairType.append(new FairItemGlyph(fontOptions.getSize(), new InventoryItem("coin")).dontShowTooltip());
        fairType.append(fontOptions, Integer.toString(nextProfit));
        this.profitLabel.setCustomFairType(fairType);
    }

    public void updateWealthLabel() {
        if (this.wealthLabel == null) {
            return;
        }
        FairType fairType = new FairType();
        FontOptions fontOptions = this.wealthLabel.getFontOptions();
        fairType.append(fontOptions, Localization.translate("ui", "shopwealth"));
        fairType.append(new FairItemGlyph(fontOptions.getSize(), new InventoryItem("coin")).dontShowTooltip());
        fairType.append(fontOptions, Integer.toString(((PawnbrokerContainer)this.container).shopWealth));
        this.wealthLabel.setCustomFairType(fairType);
    }

    @Override
    protected void addShopDialogueOptions() {
        super.addShopDialogueOptions();
        this.dialogueForm.addDialogueOption(new LocalMessage("ui", "brokerwantpawn"), () -> this.makeCurrent(this.pawnForm));
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.pawnForm);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int nextProfit = ((PawnbrokerContainer)this.container).getProfit();
        if (this.lastProfit != nextProfit) {
            this.updateProfitLabel(nextProfit);
            this.lastProfit = nextProfit;
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

