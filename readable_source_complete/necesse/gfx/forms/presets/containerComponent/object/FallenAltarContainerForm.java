/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.incursions.IncursionPerkTreeForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class FallenAltarContainerForm
extends ContainerFormSwitcher<FallenAltarContainer> {
    public Form placeTabletForm;
    public Form displayIncursionForm;
    public Form openIncursionForm;
    public IncursionPerkTreeForm incursionPerkTreeForm;
    public ConfirmationForm closeIncursionConfirmation;
    public FormContentBox displayIncursionDetailsBox;
    public FormContentBox openIncursionDetailsBox;
    private final FormContainerSlot tabletBox;
    public IncursionData currentIncursion = null;
    public FormLocalTextButton openButton;
    public FormLocalTextButton enterButton;
    public FormLocalTextButton closeButton;
    public FormLocalTextButton perkTreeButton;
    private final int displayIncursionFormBaseHeight = 315;
    private final int detailsIncursionFormBaseHeight = 205;
    private InventoryItem currentTablet;
    private int modifierHeight;
    private int lootHeight;

    public FallenAltarContainerForm(Client client, FallenAltarContainer container) {
        super(client, container);
        this.incursionPerkTreeForm = this.addComponent(new IncursionPerkTreeForm(client, container.altarEntity.altarData, container, this, 750, 550));
        this.incursionPerkTreeForm.perkTreeForm.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        FormFlow tabletFormFlow = new FormFlow(5);
        int perkButtonWidth = 200;
        this.placeTabletForm = this.addComponent(new Form("placetablet", 408, 120));
        this.placeTabletForm.addComponent(tabletFormFlow.nextY(new FormLocalLabel("incursion", "upgradeyouraltar", new FontOptions(20), 0, this.placeTabletForm.getWidth() / 2, 4, this.placeTabletForm.getWidth() - 10), 5));
        tabletFormFlow.next(5);
        this.perkTreeButton = this.placeTabletForm.addComponent(new FormLocalTextButton("ui", "incursionperktree", this.placeTabletForm.getWidth() / 2 - perkButtonWidth / 2, tabletFormFlow.next(40), perkButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.perkTreeButton.onClicked(e -> {
            client.getPlayer().setInventoryExtended(false);
            this.makeCurrent(this.incursionPerkTreeForm);
        });
        FormBreakLine bottomBreakLine = this.placeTabletForm.addComponent(tabletFormFlow.nextY(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, tabletFormFlow.next(), this.placeTabletForm.getWidth() - 20, true)));
        bottomBreakLine.color = new Color(0, 0, 0);
        tabletFormFlow.next(10);
        this.placeTabletForm.addComponent(tabletFormFlow.nextY(new FormLocalLabel("ui", "placegatewaytablet", new FontOptions(20), 0, this.placeTabletForm.getWidth() / 2, 4, this.placeTabletForm.getWidth() - 10), 5));
        FormContainerSlot tabletContainerSlot = new FormContainerSlot(client, container, container.TABLET_SLOT, this.placeTabletForm.getWidth() / 2 - 20, tabletFormFlow.next(50));
        this.placeTabletForm.addComponent(tabletContainerSlot).setDecal(this.getInterfaceStyle().inventoryslot_icon_gatewaytablet);
        this.placeTabletForm.setHeight(tabletFormFlow.next());
        FormFlow mainFlow = new FormFlow(4);
        this.displayIncursionForm = this.addComponent(new Form("fallenaltar", 400, 315));
        this.displayIncursionForm.addComponent(new FormLocalLabel("object", "fallenaltar", new FontOptions(20), 0, this.displayIncursionForm.getWidth() / 2, 5));
        mainFlow.next(20);
        this.displayIncursionForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 8, mainFlow.next() + 4, this.displayIncursionForm.getWidth() - 16, true));
        mainFlow.next(10);
        int mainFormContentStartY = mainFlow.next();
        this.displayIncursionDetailsBox = this.displayIncursionForm.addComponent(new FormContentBox(4, mainFormContentStartY, this.displayIncursionForm.getWidth(), 205));
        this.tabletBox = new FormContainerSlot(client, container, container.TABLET_SLOT, this.displayIncursionForm.getWidth() / 2 - 20, this.displayIncursionDetailsBox.getHeight() + 36);
        this.displayIncursionForm.addComponent(this.tabletBox).setDecal(this.getInterfaceStyle().inventoryslot_icon_gatewaytablet);
        FormFlow openFlow = new FormFlow(4);
        this.currentTablet = container.getSlot(container.TABLET_SLOT).getItem();
        this.openIncursionForm = this.addComponent(new Form("openincursion", 400, 315));
        this.openIncursionForm.addComponent(new FormLocalLabel("ui", "incursioncurrent", new FontOptions(16), 0, this.openIncursionForm.getWidth() / 2, openFlow.next(16)));
        this.openIncursionDetailsBox = this.openIncursionForm.addComponent(new FormContentBox(4, openFlow.next(205), this.openIncursionForm.getWidth() - 8, 205));
        this.enterButton = this.openIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionenterbutton", 4, openFlow.next(0), this.openIncursionForm.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE));
        this.enterButton.onClicked(e -> {
            if (container.altarEntity.hasOpenIncursion()) {
                container.enterIncursion.runAndSend();
            }
        });
        this.closeButton = this.openIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionclosebutton", 4, openFlow.next(40), this.openIncursionForm.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE));
        this.closeButton.onClicked(e -> {
            OpenIncursion openIncursion = container.altarEntity.getOpenIncursion();
            if (openIncursion != null) {
                GameMessageBuilder message = new GameMessageBuilder().append("ui", openIncursion.canComplete ? "incursionconfirmcomplete" : "incursionconfirmclose").append("\n\n").append("ui", "incursionconfirmdelete");
                this.closeIncursionConfirmation.setupConfirmation(message, () -> {
                    OpenIncursion confirmedOpenIncursion = container.altarEntity.getOpenIncursion();
                    if (confirmedOpenIncursion != null) {
                        container.closeIncursion.runAndSend(confirmedOpenIncursion.canComplete);
                        this.currentIncursion = null;
                    }
                    this.makeCurrent(this.placeTabletForm);
                    client.getPlayer().setInventoryExtended(true);
                }, () -> this.makeCurrent(this.openIncursionForm));
                this.makeCurrent(this.closeIncursionConfirmation);
            }
        });
        this.openIncursionForm.setHeight(openFlow.next());
        this.closeIncursionConfirmation = this.addComponent(new ConfirmationForm("closeincursionconfirm"));
        this.refreshIncursionDetails();
        this.refreshOpenIncursionDetails();
        if (container.altarEntity.hasOpenIncursion()) {
            this.makeCurrent(this.openIncursionForm);
        } else if (!this.updatePlaceTabletCurrent()) {
            this.makeCurrent(this.placeTabletForm);
            client.getPlayer().setInventoryExtended(true);
        }
        this.updateFormSize();
    }

    public void refreshCurrentForm() {
        this.refreshIncursionDetails();
        this.refreshOpenIncursionDetails();
    }

    public void refreshIncursionDetails() {
        this.displayIncursionDetailsBox.clearComponents();
        if (this.openButton != null) {
            this.displayIncursionForm.removeComponent(this.openButton);
        }
        this.openButton = null;
        this.currentIncursion = ((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem() != null ? GatewayTabletItem.getIncursionData(((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem()) : (((FallenAltarContainer)this.container).altarEntity.hasOpenIncursion() ? ((FallenAltarContainer)this.container).altarEntity.getOpenIncursion().incursionData : null);
        if (this.currentIncursion == null) {
            if (((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem() == null) {
                this.makeCurrent(this.placeTabletForm);
            }
            return;
        }
        if (this.currentIncursion instanceof BiomeMissionIncursionData) {
            this.updateFormSize();
        }
        this.currentIncursion.setUpDetails((FallenAltarContainer)this.container, this, this.displayIncursionDetailsBox, false);
        this.openButton = this.displayIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionopenbutton", 4, this.displayIncursionForm.getHeight() - 24 - 4, this.displayIncursionForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                GameTooltips tooltips = FallenAltarContainerForm.this.currentIncursion.getOpenButtonTooltips((FallenAltarContainer)FallenAltarContainerForm.this.container);
                if (tooltips != null) {
                    GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        this.openButton.onClicked(e -> ((FallenAltarContainer)this.container).openIncursion.runAndSend(this.currentIncursion.getUniqueID()));
    }

    public void refreshOpenIncursionDetails() {
        this.openIncursionDetailsBox.clearComponents();
        OpenIncursion openIncursion = ((FallenAltarContainer)this.container).altarEntity.getOpenIncursion();
        if (openIncursion != null) {
            openIncursion.incursionData.setUpDetails((FallenAltarContainer)this.container, this, this.openIncursionDetailsBox, true);
            if (openIncursion.canComplete) {
                this.closeButton.setLocalization("ui", "incursioncompletebutton");
                this.closeButton.color = ButtonColor.GREEN;
            } else {
                this.closeButton.setLocalization("ui", "incursionclosebutton");
                this.closeButton.color = ButtonColor.RED;
            }
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.refreshCurrentForm();
    }

    public void updateFormSize() {
        BiomeMissionIncursionData data = (BiomeMissionIncursionData)this.currentIncursion;
        if (data != null) {
            int lootSize = 0;
            lootSize += data.getPlayerPersonalIncursionCompleteRewards().getRewards(true).size();
            this.modifierHeight = (int)data.getDefaultLevelModifiers().count() * 18;
            this.lootHeight = (data.getLootCount() + (lootSize += data.getPlayerSharedIncursionCompleteRewards().getRewards(true).size())) * 20;
        }
        this.displayIncursionForm.setHeight(315 + this.modifierHeight + this.lootHeight);
        this.openIncursionForm.setHeight(315 + this.modifierHeight + this.lootHeight);
        this.displayIncursionDetailsBox.setHeight(205 + this.modifierHeight + this.lootHeight);
        this.openIncursionDetailsBox.setHeight(205 + this.modifierHeight + this.lootHeight);
        this.tabletBox.setY(this.displayIncursionDetailsBox.getHeight() + 36);
        this.enterButton.setY(this.openIncursionDetailsBox.getHeight() + 30);
        this.closeButton.setY(this.openIncursionDetailsBox.getHeight() + 66);
        ContainerComponent.setPosFocus(this.placeTabletForm);
        ContainerComponent.setPosFocus(this.displayIncursionForm);
        ContainerComponent.setPosFocus(this.openIncursionForm);
        if (this.displayIncursionForm.getY() <= 0) {
            int heightToRemove = Math.abs(8 - this.displayIncursionForm.getY());
            this.displayIncursionForm.setHeight(this.displayIncursionForm.getHeight() - heightToRemove);
            this.displayIncursionForm.setY(8);
            this.openIncursionForm.setHeight(this.displayIncursionForm.getHeight());
            this.openIncursionForm.setY(8);
            this.displayIncursionDetailsBox.setHeight(this.displayIncursionDetailsBox.getHeight() - heightToRemove - 5);
            this.openIncursionDetailsBox.setHeight(this.displayIncursionDetailsBox.getHeight());
            this.tabletBox.setY(this.displayIncursionDetailsBox.getHeight() + 40);
            this.enterButton.setY(this.openIncursionDetailsBox.getHeight() + 30);
            this.closeButton.setY(this.openIncursionDetailsBox.getHeight() + 66);
            ContainerComponent.setPosFocus(this.placeTabletForm);
            ContainerComponent.setPosFocus(this.displayIncursionForm);
            ContainerComponent.setPosFocus(this.openIncursionForm);
        }
    }

    public boolean updatePlaceTabletCurrent() {
        if (this.isCurrent(this.placeTabletForm) && this.currentIncursion != null && !((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).isClear()) {
            this.makeCurrent(this.displayIncursionForm);
            return true;
        }
        if (!this.isCurrent(this.placeTabletForm) && this.currentIncursion != null && ((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).isClear() && !this.isCurrent(this.incursionPerkTreeForm)) {
            this.makeCurrent(this.placeTabletForm);
            this.client.getPlayer().setInventoryExtended(true);
            return true;
        }
        return false;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        ContainerSlot tabletSlot = ((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT);
        if (this.currentTablet == null && tabletSlot.getItem() != null) {
            this.currentTablet = tabletSlot.getItem();
            this.refreshCurrentForm();
        }
        if (this.currentTablet != null && tabletSlot.getItem() != null && !tabletSlot.getItem().equals(perspective.getLevel(), this.currentTablet, false, false, "equals")) {
            this.currentTablet = tabletSlot.getItem();
            this.refreshCurrentForm();
        }
        if (((FallenAltarContainer)this.container).altarEntity.hasOpenIncursion()) {
            if (this.isCurrent(this.displayIncursionForm)) {
                this.refreshOpenIncursionDetails();
                this.makeCurrent(this.openIncursionForm);
            }
        } else {
            if (this.isCurrent(this.openIncursionForm)) {
                this.refreshIncursionDetails();
            }
            this.updatePlaceTabletCurrent();
            if (this.openButton != null) {
                if (this.currentIncursion != null) {
                    String error = this.currentIncursion.getCanOpenError((FallenAltarContainer)this.container);
                    this.openButton.setActive(error == null);
                    if (error != null && !error.isEmpty()) {
                        this.openButton.setTooltip(error);
                    } else {
                        this.openButton.setTooltip(null);
                    }
                } else {
                    this.openButton.setActive(true);
                }
            }
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public boolean shouldShowInventory() {
        return true;
    }

    @Override
    public boolean shouldShowToolbar() {
        return true;
    }
}

