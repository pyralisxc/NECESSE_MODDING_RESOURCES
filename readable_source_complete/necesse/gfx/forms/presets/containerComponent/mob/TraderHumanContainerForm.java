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
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.TraderHumanContainer;

public class TraderHumanContainerForm<T extends TraderHumanContainer>
extends ShopContainerForm<T> {
    public Form tradingMissionForm;
    public FormContainerSlot[] slots;
    public FormFairTypeLabel profitLabel;
    public int lastProfit;

    public TraderHumanContainerForm(Client client, T container) {
        super(client, container, 408, defaultHeight, defaultHeight);
        this.tradingMissionForm = this.addComponent(new Form("tradingMissionForm", 408, (((TraderHumanContainer)container).inventory.getSize() + 9) / 10 * 40 + 70 + 8), (form, active) -> container.setIsInTradingForm.runAndSend((boolean)active));
        String headerText = MobRegistry.getLocalization(((TraderHumanContainer)container).humanShop.getID()).translate();
        FormLabel label = this.tradingMissionForm.addComponent(new FormLabel("", new FontOptions(20), -1, 4, 4), -1000);
        FormFlow iconFlow = new FormFlow(this.tradingMissionForm.getWidth() - 4);
        FormContentIconButton stackButton = this.tradingMissionForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().inventory_quickstack_out, new GameMessage[0]){

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
        FormContentIconButton transferAllButton = this.tradingMissionForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_loot_all, new GameMessage[0]){

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
        FormContentIconButton restockButton = this.tradingMissionForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_quickstack_in, new LocalMessage("ui", "inventoryrestock")));
        restockButton.onClicked(e -> container.restockButton.runAndSend());
        restockButton.setCooldown(500);
        FormContentIconButton lootAllButton = this.tradingMissionForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "inventorylootall")));
        lootAllButton.onClicked(e -> container.lootButton.runAndSend());
        lootAllButton.setCooldown(500);
        headerText = GameUtils.maxString(headerText, new FontOptions(20), iconFlow.next() - 8);
        label.setText(headerText, iconFlow.next() - 8);
        this.slots = new FormContainerSlot[((TraderHumanContainer)container).INVENTORY_END - ((TraderHumanContainer)container).INVENTORY_START + 1];
        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((TraderHumanContainer)container).INVENTORY_START;
            int x = i % 10;
            int y = i / 10;
            this.slots[i] = this.tradingMissionForm.addComponent(new FormContainerBrokerSlot(client, (Container)container, slotIndex, 4 + x * 40, 4 + y * 40 + 30));
        }
        FormFlow flow = new FormFlow((((TraderHumanContainer)container).inventory.getSize() + 9) / 10 * 40 + 30);
        flow.next(10);
        int profitLabelY = flow.next(30);
        this.profitLabel = this.tradingMissionForm.addComponent(new FormFairTypeLabel(new StaticMessage(""), new FontOptions(16), FairType.TextAlign.LEFT, 10, profitLabelY));
        this.lastProfit = ((TraderHumanContainer)container).getProfit();
        this.updateProfitLabel(this.lastProfit);
        int buttonsY = flow.next(40);
        this.tradingMissionForm.addComponent(new FormLocalTextButton("ui", "traderstarttrading", 4, buttonsY, this.tradingMissionForm.getWidth() / 2 - 6)).onClicked(e -> container.startMissionAction.runAndSend());
        this.tradingMissionForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.tradingMissionForm.getWidth() / 2 + 2, buttonsY, this.tradingMissionForm.getWidth() / 2 - 6)).onClicked(e -> this.makeCurrent(this.dialogueForm));
        this.tradingMissionForm.setHeight(flow.next());
    }

    public void updateProfitLabel(int nextProfit) {
        FairType fairType = new FairType();
        FontOptions fontOptions = this.profitLabel.getFontOptions();
        fairType.append(fontOptions, Localization.translate("ui", "brokerprofit"));
        fairType.append(new FairItemGlyph(fontOptions.getSize(), new InventoryItem("coin")).dontShowTooltip());
        fairType.append(fontOptions, Integer.toString(nextProfit));
        this.profitLabel.setCustomFairType(fairType);
    }

    @Override
    protected void addShopDialogueOptions() {
        super.addShopDialogueOptions();
        if (((TraderHumanContainer)this.container).canDoTradingMission) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "traderwanttrading"), () -> this.makeCurrent(this.tradingMissionForm));
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.tradingMissionForm);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int nextProfit = ((TraderHumanContainer)this.container).getProfit();
        if (this.lastProfit != nextProfit) {
            this.updateProfitLabel(nextProfit);
            this.lastProfit = nextProfit;
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

