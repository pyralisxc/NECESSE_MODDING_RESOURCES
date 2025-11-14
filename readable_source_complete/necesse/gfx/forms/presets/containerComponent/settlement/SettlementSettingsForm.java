/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import necesse.engine.GameAuth;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.BackgroundedGameTooltips;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.SettlementBoundsManager;

public class SettlementSettingsForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    protected Form settings;
    protected FormLabel disbandTimeLabel;
    protected Form name;
    protected FormTextInput nameInput;
    protected Form confirmDisbandForm;
    protected InventoryUpdateListener inventoryUpdateListener;

    public SettlementSettingsForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.settings = this.addComponent(new Form("settings", 400, 40));
        this.name = this.addComponent(new Form("name", 400, 80));
        this.nameInput = this.name.addComponent(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.name.getWidth() - 8, 40));
        if (((SettlementContainer)container).settlementData != null) {
            Biome biome = client.getLevel().getBiome(((SettlementContainer)container).settlementData.tileX, ((SettlementContainer)container).settlementData.tileY);
            this.nameInput.placeHolder = new LocalMessage("settlement", "defname", "biome", biome.getLocalization());
            String preName = ((SettlementContainer)container).settlementData.settlementName.translate();
            if (!this.nameInput.placeHolder.translate().equals(preName)) {
                this.nameInput.setText(preName);
            }
        } else {
            this.nameInput.placeHolder = new LocalMessage("settlement", "defname", "biome", BiomeRegistry.UNKNOWN.getLocalization());
        }
        this.nameInput.onSubmit(e -> {
            this.playTickSound();
            this.submitName();
            this.makeCurrent(this.settings);
        });
        this.name.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 40, this.name.getWidth() / 2 - 6)).onClicked(e -> {
            this.playTickSound();
            this.submitName();
            this.makeCurrent(this.settings);
        });
        this.name.addComponent(new FormLocalTextButton("ui", "backbutton", this.name.getWidth() / 2 + 2, 40, this.name.getWidth() / 2 - 6)).onClicked(e -> this.makeCurrent(this.settings));
        this.confirmDisbandForm = this.addComponent(new Form("confirmDisband", 400, 80));
        FormFlow confirmDisbandFlow = new FormFlow(20);
        this.confirmDisbandForm.addComponent(confirmDisbandFlow.nextY(new FormLocalLabel("ui", "settlementdisbandwarning", new FontOptions(20), 0, this.confirmDisbandForm.getWidth() / 2, 0, this.confirmDisbandForm.getWidth() - 40), 10));
        this.confirmDisbandForm.addComponent(confirmDisbandFlow.nextY(new FormLocalLabel("ui", "setllementdisbandconfirm", new FontOptions(20), 0, this.confirmDisbandForm.getWidth() / 2, 0, this.confirmDisbandForm.getWidth() - 40), 10));
        int buttonsY = confirmDisbandFlow.next(40);
        FormLocalTextButton confirmButton = this.confirmDisbandForm.addComponent(new FormLocalTextButton("ui", "confirmbutton", 10, buttonsY, this.confirmDisbandForm.getWidth() / 2 - 2 - 10));
        confirmButton.color = ButtonColor.RED;
        confirmButton.onClicked(e -> {
            this.playTickSound();
            container.disbandSettlement.runAndSend();
            client.closeContainer(true);
        });
        this.confirmDisbandForm.addComponent(new FormLocalTextButton("ui", "cancelbutton", this.confirmDisbandForm.getWidth() / 2 + 2, buttonsY, this.confirmDisbandForm.getWidth() / 2 - 2 - 10)).onClicked(e -> this.makeCurrent(this.settings));
        confirmDisbandFlow.next(10);
        this.confirmDisbandForm.setHeight(confirmDisbandFlow.next());
        this.update();
        this.makeCurrent(this.settings);
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementDataEvent.class, e -> this.update());
        this.inventoryUpdateListener = ((Container)this.container).getClientInventory().main.addSlotUpdateListener(new InventoryUpdateListener(){

            @Override
            public void onSlotUpdate(int slot) {
                SettlementSettingsForm.this.update();
            }

            @Override
            public boolean isDisposed() {
                return SettlementSettingsForm.this.isDisposed();
            }
        });
    }

    protected void update() {
        this.settings.clearComponents();
        this.disbandTimeLabel = null;
        boolean isOwner = ((SettlementDependantContainer)this.container).isSettlementOwner(this.client);
        FormFlow flow = new FormFlow(5);
        this.settings.addComponent(flow.nextY(new FormLocalLabel(((SettlementContainer)this.container).settlementData.settlementName, new FontOptions(20), 0, this.settings.getWidth() / 2, 0, this.settings.getWidth() - 20), 10));
        FormLocalTextButton changeName = this.settings.addComponent(new FormLocalTextButton("ui", "settmentchangename", 40, flow.next(40), this.settings.getWidth() - 80));
        changeName.onClicked(e -> this.makeCurrent(this.name));
        changeName.setActive(isOwner);
        if (!changeName.isActive()) {
            changeName.setLocalTooltip(new LocalMessage("ui", ((SettlementDependantContainer)this.container).hasSettlementOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }
        FormLocalTextButton changePrivacy = this.settings.addComponent(new FormLocalTextButton("ui", ((SettlementContainer)this.container).settlementData.isPrivate ? "settlementmakepub" : "settlementmakepriv", 40, flow.next(40), this.settings.getWidth() - 80));
        changePrivacy.onClicked(e -> ((SettlementContainer)this.container).changePrivacy.runAndSend(!((SettlementContainer)this.container).settlementData.isPrivate));
        changePrivacy.setActive(isOwner);
        if (!changePrivacy.isActive()) {
            changePrivacy.setLocalTooltip(new LocalMessage("ui", ((SettlementDependantContainer)this.container).hasSettlementOwner() ? "settlementowneronly" : "settlementclaimfirst"));
        }
        Rectangle currentSize = SettlementBoundsManager.getUncenteredRegionRectangleFromTier(((SettlementContainer)this.container).settlementData.flagTier);
        final int currentSizeTilesX = GameMath.getTileCoordByRegion(currentSize.width);
        final int currentSizeTilesY = GameMath.getTileCoordByRegion(currentSize.height);
        final Ingredient[] expansionRecipe = SettlementBoundsManager.getTierUpgradeCost(((SettlementContainer)this.container).settlementData.flagTier);
        final CanCraft expansionCanCraft = expansionRecipe != null ? ((Container)this.container).canCraftRecipe(expansionRecipe, ((Container)this.container).getCraftInventories(), true) : null;
        FormLocalTextButton expandButton = this.settings.addComponent(new FormLocalTextButton(new LocalMessage("ui", "settlementexpand"), 40, flow.next(40), this.settings.getWidth() - 80){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                ListGameTooltips tooltips = new ListGameTooltips();
                if (expansionRecipe == null) {
                    tooltips.add(GameColor.YELLOW.getColorCode() + Localization.translate("ui", "settlementfullyexpanded"));
                } else {
                    tooltips.add(Localization.translate("ui", "settlementexpandtip", "size", (Object)16) + "\n");
                    tooltips.add(Localization.translate("misc", "recipecostsing"));
                    for (int i = 0; i < expansionRecipe.length; ++i) {
                        Ingredient ingredient = expansionRecipe[i];
                        tooltips.add(ingredient.getTooltips(expansionCanCraft.haveIngredients[i], true));
                    }
                }
                tooltips.add(new SpacerGameTooltip(10));
                tooltips.add(GameColor.GREEN.getColorCode() + Localization.translate("ui", "settlementcurrentsize", "size", currentSizeTilesX + "x" + currentSizeTilesY));
                GameTooltipManager.addTooltip(new BackgroundedGameTooltips(tooltips, GameBackground.getItemTooltipBackground()), TooltipLocation.FORM_FOCUS);
            }
        });
        expandButton.setActive(isOwner && expansionRecipe != null && expansionCanCraft.canCraft());
        expandButton.onClicked(e -> {
            ((SettlementContainer)this.container).expandSettlement.runAndSend();
            expandButton.setCooldown(500);
        });
        FormLocalTextButton changeClaim = this.settings.addComponent(new FormLocalTextButton("ui", isOwner ? "settlementunclaim" : "settlementclaim", 40, flow.next(40), this.settings.getWidth() - 80));
        changeClaim.onClicked(e -> ((SettlementContainer)this.container).changeClaim.runAndSend(((SettlementContainer)this.container).settlementData.ownerAuth != GameAuth.getAuthentication()));
        changeClaim.setActive((!((SettlementDependantContainer)this.container).hasSettlementOwner() || isOwner) && ((SettlementDependantContainer)this.container).getSettlementDisbandTime() == 0L);
        if (!changeClaim.isActive() && ((SettlementDependantContainer)this.container).getSettlementDisbandTime() == 0L) {
            changeClaim.setLocalTooltip(new LocalMessage("ui", "settlementowneronly"));
        }
        flow.next(10);
        if (((SettlementDependantContainer)this.container).getSettlementDisbandTime() != 0L) {
            this.settings.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementclaimbyflag", new FontOptions(16), 0, this.settings.getWidth() / 2, 0, this.settings.getWidth() - 20), 5));
            this.disbandTimeLabel = this.settings.addComponent(new FormLabel("", new FontOptions(16), 0, this.settings.getWidth() / 2, flow.next(30)));
            this.updateDisbandTimeLabel();
            FormLocalTextButton disbandNow = this.settings.addComponent(new FormLocalTextButton("ui", "settlementdisbandnow", 40, flow.next(30), this.settings.getWidth() - 80, FormInputSize.SIZE_24, ButtonColor.RED));
            disbandNow.onClicked(e -> this.makeCurrent(this.confirmDisbandForm));
        } else if (isOwner) {
            FormLocalTextButton disbandButton = this.settings.addComponent(new FormLocalTextButton("ui", "settlementdisband", 40, flow.next(30), this.settings.getWidth() - 80, FormInputSize.SIZE_24, ButtonColor.RED));
            disbandButton.onClicked(e -> this.makeCurrent(this.confirmDisbandForm));
        }
        flow.next(10);
        this.settings.addComponent(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", ((SettlementContainer)this.container).settlementData.ownerName), new FontOptions(16), -1, 5, flow.next(20)));
        this.settings.setHeight(flow.next());
        GameWindow window = WindowManager.getWindow();
        this.onWindowResized(window);
        window.submitNextMoveEvent();
    }

    protected void updateDisbandTimeLabel() {
        if (this.disbandTimeLabel != null) {
            long timeToDisband = ((SettlementDependantContainer)this.container).getSettlementDisbandTime() - this.client.worldEntity.getTime();
            int secondsToDisband = (int)Math.ceil((double)timeToDisband / 1000.0);
            int minutesToDisband = secondsToDisband / 60;
            String timeToDisbandString = Localization.translate("ui", "settlementdisbandin", "minutes", minutesToDisband, "seconds", secondsToDisband -= minutesToDisband * 60);
            this.disbandTimeLabel.setText(timeToDisbandString);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateDisbandTimeLabel();
        super.draw(tickManager, perspective, renderBox);
    }

    protected void submitName() {
        ((SettlementContainer)this.container).changeName.runAndSend(this.getCurrentNameInput());
    }

    protected GameMessage getCurrentNameInput() {
        String text = this.nameInput.getText();
        if (text.isEmpty()) {
            return this.nameInput.placeHolder;
        }
        return new StaticMessage(text);
    }

    @Override
    public void onSetCurrent(boolean current) {
        if (current) {
            this.makeCurrent(this.settings);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this.settings);
        this.name.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.confirmDisbandForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementsettings");
    }

    @Override
    public String getTypeString() {
        return "settings";
    }

    @Override
    public void dispose() {
        super.dispose();
        this.inventoryUpdateListener.dispose();
    }
}

