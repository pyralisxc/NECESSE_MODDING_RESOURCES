/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.MageContainer;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class MageContainerForm<T extends MageContainer>
extends ShopContainerForm<T> {
    public Form enchantForm;
    public FormLabel costText;
    public FormLocalTextButton enchantButton;
    public FormLocalLabel costLabel;
    public FormItemPreview preview;

    public MageContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);
        FormFlow heightFlow = new FormFlow(5);
        this.enchantForm = this.addComponent(new Form("enchant", width, height), (form, active) -> container.setIsEnchanting.runAndSend((boolean)active));
        this.enchantForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.enchantForm.getWidth() - 104, 4, 100, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> this.makeCurrent(this.dialogueForm));
        this.enchantForm.addComponent(new FormLocalLabel("ui", "mageenchant", new FontOptions(20), -1, 4, heightFlow.next(40)));
        int enchantSlotY = heightFlow.next(50);
        this.enchantForm.addComponent(new FormContainerEnchantSlot(client, (Container)container, ((MageContainer)container).ENCHANT_SLOT, 40, enchantSlotY));
        this.enchantButton = this.enchantForm.addComponent(new FormLocalTextButton("ui", "mageconfirm", 90, enchantSlotY + 10, 150, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.enchantButton.onClicked(e -> container.enchantButton.runAndSend());
        this.costLabel = this.enchantForm.addComponent(new FormLocalLabel("ui", "magecost", new FontOptions(16), -1, 260, enchantSlotY - 4));
        this.preview = this.enchantForm.addComponent(new FormItemPreview(250, enchantSlotY + 10, "coin"));
        this.costText = this.enchantForm.addComponent(new FormLabel("x " + ((MageContainer)container).getEnchantCost(), new FontOptions(16), -1, this.preview.getX() + 30, enchantSlotY + 20));
        this.enchantForm.addComponent(heightFlow.nextY(new FormFairTypeLabel(new LocalMessage("ui", "mageenchanttip"), this.enchantForm.getWidth() / 2, 0).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.enchantForm.getWidth() - 20), 10));
        if (((MageContainer)container).mageMob.isSettler()) {
            GameMessageBuilder happinessDescription = new GameMessageBuilder().append(Settler.getMood(((MageContainer)container).settlerHappiness).getDescription()).append(" (").append(((MageContainer)container).settlerHappiness >= 0 ? "+" : "").append(Integer.toString(((MageContainer)container).settlerHappiness)).append(")");
            this.enchantForm.addComponent(heightFlow.nextY(new FormFairTypeLabel(happinessDescription, this.enchantForm.getWidth() / 2, 0).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.enchantForm.getWidth() - 20), 5));
            FormContentIconButton helpIcon = this.enchantForm.addComponent(new FormContentIconButton(this.enchantForm.getWidth() / 2 - 10, heightFlow.next(30), FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_help_20, new LocalMessage("ui", "mageenchantbiastip")));
            helpIcon.handleClicksIfNoEventHandlers = true;
        }
        this.enchantForm.setHeight(heightFlow.next());
        this.updateEnchantActive();
    }

    public MageContainerForm(Client client, T container) {
        this(client, container, 408, defaultHeight, defaultHeight);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                MageContainerForm.this.preview.setX(MageContainerForm.this.costLabel.getX() + MageContainerForm.this.costLabel.getBoundingBox().width);
                MageContainerForm.this.costText.setX(MageContainerForm.this.preview.getX() + 30);
            }

            @Override
            public boolean isDisposed() {
                return MageContainerForm.this.isDisposed();
            }
        });
    }

    @Override
    protected void addShopDialogueOptions() {
        super.addShopDialogueOptions();
        if (((MageContainer)this.container).humanShop instanceof MageHumanMob) {
            boolean valid = false;
            if (((MageContainer)this.container).sellingItems != null && !((MageContainer)this.container).sellingItems.isEmpty()) {
                valid = true;
            } else if (((MageContainer)this.container).buyingItems != null && !((MageContainer)this.container).buyingItems.isEmpty()) {
                valid = true;
            }
            if (valid) {
                this.dialogueForm.addDialogueOption(new LocalMessage("ui", "magewantenchant"), () -> this.makeCurrent(this.enchantForm));
            }
        }
    }

    private void updateEnchantActive() {
        this.costText.setText("x " + ((MageContainer)this.container).getEnchantCost());
        this.enchantButton.setActive(((MageContainer)this.container).canEnchant());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.enchantForm)) {
            this.updateEnchantActive();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.enchantForm);
    }
}

