/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.BackgroundedGameTooltips;
import necesse.gfx.gameTooltips.CompareGameTooltips;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.MinWidthGameTooltips;
import necesse.gfx.gameTooltips.OffsetGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.UpgradeStationContainer;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.hudManager.HudDrawElement;

public class UpgradeStationContainerForm<T extends UpgradeStationContainer>
extends ContainerFormSwitcher<T> {
    public Form mainForm = this.addComponent(new Form(400, 120));
    public UpgradedItem lastUpgradedItem;
    private final int upgradeContentY;
    private FormContentBox upgradeContent;
    protected FormLocalCheckBox useNearby;
    protected FormLocalTextButton upgradeButton;
    protected HudDrawElement rangeElement;
    protected FormRecipeList recipeUpdateListener;
    protected InventoryUpdateListener inventoryUpdateListener;

    public UpgradeStationContainerForm(Client client, T container) {
        super(client, container);
        FormFlow flow = new FormFlow(5);
        this.mainForm.addComponent(flow.nextY(new FormLocalLabel(((UpgradeStationContainer)container).upgradeEntity.getObject().getLocalization(), new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5), 5));
        this.mainForm.addComponent(flow.nextY(new FormLocalLabel("ui", "upgradeplaceitem", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 4, this.mainForm.getWidth() - 10), 5));
        final GameTexture[] decals = new GameTexture[]{this.getInterfaceStyle().inventoryslot_icon_weapon, this.getInterfaceStyle().inventoryslot_icon_helmet, this.getInterfaceStyle().inventoryslot_icon_gatewaytablet};
        this.mainForm.addComponent(new FormContainerSlot(client, (Container)container, ((UpgradeStationContainer)container).UPGRADE_SLOT, this.mainForm.getWidth() / 2 - 20, flow.next(45)){

            @Override
            public void drawDecal(PlayerMob perspective) {
                int decal = (int)(System.currentTimeMillis() % (long)(decals.length * 1000)) / 1000;
                this.setDecal(decals[decal]);
                super.drawDecal(perspective);
            }
        });
        this.upgradeContentY = flow.next();
        int upgradeButtonMaxWidth = 300;
        int upgradeButtonWidth = Math.min(this.mainForm.getWidth() - 8, upgradeButtonMaxWidth);
        this.upgradeButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "applyupgradebutton", this.mainForm.getWidth() / 2 - upgradeButtonWidth / 2, flow.next(28), upgradeButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE, (UpgradeStationContainer)container){
            final /* synthetic */ UpgradeStationContainer val$container;
            {
                this.val$container = upgradeStationContainer;
                super(category, key, x, y, width, size, color);
            }

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                if (UpgradeStationContainerForm.this.lastUpgradedItem != null) {
                    ListGameTooltips costTooltip = new ListGameTooltips();
                    if (UpgradeStationContainerForm.this.lastUpgradedItem.cost != null && UpgradeStationContainerForm.this.lastUpgradedItem.cost.length > 0) {
                        costTooltip.add(Localization.translate("misc", "recipecostsing"));
                        CanCraft canCraft = this.val$container.canUpgrade(UpgradeStationContainerForm.this.lastUpgradedItem, true);
                        for (int i = 0; i < UpgradeStationContainerForm.this.lastUpgradedItem.cost.length; ++i) {
                            Ingredient ingredient = UpgradeStationContainerForm.this.lastUpgradedItem.cost[i];
                            costTooltip.add(ingredient.getTooltips(canCraft.haveIngredients[i], true));
                        }
                    }
                    GameBlackboard blackboard = new GameBlackboard().set("compareItem", UpgradeStationContainerForm.this.lastUpgradedItem.lastItem).set("hideModifierAndRewards", true).set("forceAdd", true);
                    ListGameTooltips lastItemTooltip = UpgradeStationContainerForm.this.lastUpgradedItem.lastItem.item.getTooltips(UpgradeStationContainerForm.this.lastUpgradedItem.lastItem, perspective, new GameBlackboard().set("forceAdd", true));
                    ListGameTooltips upgradedItemTooltip = UpgradeStationContainerForm.this.lastUpgradedItem.upgradedItem.item.getTooltips(UpgradeStationContainerForm.this.lastUpgradedItem.upgradedItem, perspective, blackboard);
                    CompareGameTooltips compareTooltip = new CompareGameTooltips(new BackgroundedGameTooltips(lastItemTooltip, GameBackground.getItemTooltipBackground()), new BackgroundedGameTooltips(upgradedItemTooltip, GameBackground.getItemTooltipBackground()), 5, false);
                    ListGameTooltips finalTooltip = new ListGameTooltips();
                    finalTooltip.add(new OffsetGameTooltips(new BackgroundedGameTooltips(new MinWidthGameTooltips(costTooltip, compareTooltip.getWidth() - 5), GameBackground.getItemTooltipBackground()), compareTooltip.getDrawXOffset()));
                    finalTooltip.add(new SpacerGameTooltip(5));
                    finalTooltip.add(compareTooltip);
                    GameTooltipManager.addTooltip(finalTooltip, TooltipLocation.FORM_FOCUS);
                }
            }
        });
        this.upgradeButton.onClicked(e -> container.upgradeButton.runAndSend());
        this.useNearby = this.mainForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, flow.next(20), (boolean)Settings.craftingUseNearby.get()), 100);
        Settings.craftingUseNearby.addChangeListener(v -> {
            this.useNearby.checked = v;
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.useNearby.onClicked(e -> Settings.craftingUseNearby.set(((FormCheckBox)e.from).checked));
        this.lastUpgradedItem = ((UpgradeStationContainer)container).getUpgradedItem();
        this.updateFormContent();
        this.makeCurrent(this.mainForm);
    }

    @Override
    protected void init() {
        super.init();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
        this.rangeElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!UpgradeStationContainerForm.this.useNearby.isHovering()) {
                    return;
                }
                final SharedTextureDrawOptions options = ((UpgradeStationContainer)((UpgradeStationContainerForm)UpgradeStationContainerForm.this).container).ingredientRange.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((UpgradeStationContainer)((UpgradeStationContainerForm)UpgradeStationContainerForm.this).container).upgradeEntity.tileX, ((UpgradeStationContainer)((UpgradeStationContainerForm)UpgradeStationContainerForm.this).container).upgradeEntity.tileY, camera);
                if (options != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    });
                }
            }
        };
        this.client.getLevel().hudManager.addElement(this.rangeElement);
        this.recipeUpdateListener = new FormRecipeList(){

            @Override
            public void updateCraftable() {
                UpgradeStationContainerForm.this.updateFormContent();
            }

            @Override
            public void updateRecipes() {
            }
        };
        GlobalData.craftingLists.add(this.recipeUpdateListener);
        this.inventoryUpdateListener = ((UpgradeStationContainer)this.container).upgradeEntity.inventory.addSlotUpdateListener(new InventoryUpdateListener(){

            @Override
            public void onSlotUpdate(int slot) {
                UpgradeStationContainerForm.this.lastUpgradedItem = ((UpgradeStationContainer)UpgradeStationContainerForm.this.container).getUpgradedItem();
                UpgradeStationContainerForm.this.updateFormContent();
            }

            @Override
            public boolean isDisposed() {
                return UpgradeStationContainerForm.this.isDisposed();
            }
        });
    }

    public void updateFormContent() {
        if (this.upgradeContent != null) {
            this.mainForm.removeComponent(this.upgradeContent);
        }
        FormFlow flow = new FormFlow(this.upgradeContentY);
        this.upgradeContent = this.mainForm.addComponent(new FormContentBox(0, flow.next(), this.mainForm.getWidth(), 300));
        FormFlow upgradeFlow = new FormFlow();
        CanCraft canCraft = null;
        if (this.lastUpgradedItem != null && this.lastUpgradedItem.upgradedItem.item instanceof UpgradableItem) {
            FormFairTypeLabel label;
            canCraft = ((UpgradeStationContainer)this.container).canUpgrade(this.lastUpgradedItem, true);
            if (this.lastUpgradedItem.lastItem != null && this.lastUpgradedItem.lastItem.item instanceof UpgradableItem) {
                this.upgradeContent.addComponent(upgradeFlow.nextY(new FormLocalLabel("ui", "upgradesto", new FontOptions(20), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 2));
                this.upgradeContent.addComponent(upgradeFlow.nextY(new FormLocalLabel("ui", "upgradesbasestats", new FontOptions(12), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 5));
                ItemStatTipList stats = new ItemStatTipList();
                ((UpgradableItem)((Object)this.lastUpgradedItem.upgradedItem.item)).addUpgradeStatTips(stats, this.lastUpgradedItem.lastItem, this.lastUpgradedItem.upgradedItem, this.client.getPlayer(), null);
                for (ItemStatTip stat : stats) {
                    label = new FormFairTypeLabel("", this.upgradeContent.getMinContentWidth() / 2, 0);
                    label.setMaxWidth(this.upgradeContent.getMinContentWidth() - 10);
                    label.setTextAlign(FairType.TextAlign.CENTER);
                    label.setCustomFairType(stat.toFairType(new FontOptions(16), this.getInterfaceStyle().successTextColor, this.getInterfaceStyle().errorTextColor, this.getInterfaceStyle().warningTextColor, true));
                    this.upgradeContent.addComponent(upgradeFlow.nextY(label, 2));
                }
                upgradeFlow.next(10);
            }
            FontOptions costFont = new FontOptions(16);
            this.upgradeContent.addComponent(upgradeFlow.nextY(new FormLocalLabel(new LocalMessage("ui", "upgradescost"), new FontOptions(20), 0, this.upgradeContent.getWidth() / 2, 0), 2));
            for (int i = 0; i < this.lastUpgradedItem.cost.length; ++i) {
                Ingredient ingredient = this.lastUpgradedItem.cost[i];
                label = new FormFairTypeLabel("", this.upgradeContent.getMinContentWidth() / 2, 0);
                label.setMaxWidth(this.upgradeContent.getMinContentWidth() - 10);
                label.setTextAlign(FairType.TextAlign.CENTER);
                FairType text = ingredient.getTooltipText(costFont, canCraft.haveIngredients[i], this.getInterfaceStyle().activeTextColor, this.getInterfaceStyle().errorTextColor, true);
                label.setCustomFairType(text);
                this.upgradeContent.addComponent(upgradeFlow.nextY(label, 2));
            }
            upgradeFlow.next(10);
        } else {
            InventoryItem currentItem = ((UpgradeStationContainer)this.container).getSlot(((UpgradeStationContainer)this.container).UPGRADE_SLOT).getItem();
            if (currentItem != null) {
                String newError;
                String error = Localization.translate("ui", "itemnotupgradable");
                if (currentItem.item instanceof UpgradableItem && (newError = ((UpgradableItem)((Object)currentItem.item)).getCanBeUpgradedError(currentItem)) != null) {
                    error = newError;
                }
                this.upgradeContent.addComponent(upgradeFlow.nextY(new FormLabel(error, new FontOptions(16).color(this.getInterfaceStyle().errorTextColor), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 2));
                upgradeFlow.next(10);
            }
        }
        if (this.upgradeContent.getHeight() > upgradeFlow.next()) {
            this.upgradeContent.setHeight(upgradeFlow.next());
        }
        this.upgradeContent.setContentBox(new Rectangle(this.upgradeContent.getWidth(), upgradeFlow.next()));
        flow.next(this.upgradeContent.getHeight());
        flow.nextY(this.upgradeButton, 4);
        flow.nextY(this.useNearby, 4);
        this.upgradeButton.setActive(this.lastUpgradedItem != null && canCraft != null && canCraft.canCraft());
        this.mainForm.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.useNearby.isHovering()) {
            Renderer.hudManager.fadeHUD();
        }
        super.draw(tickManager, perspective, renderBox);
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
        Settings.hasCraftingListExpanded.cleanListeners();
        Settings.hasCraftingFilterExpanded.cleanListeners();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
        if (this.recipeUpdateListener != null) {
            GlobalData.craftingLists.remove(this.recipeUpdateListener);
        }
        if (this.inventoryUpdateListener != null) {
            this.inventoryUpdateListener.dispose();
        }
    }
}

