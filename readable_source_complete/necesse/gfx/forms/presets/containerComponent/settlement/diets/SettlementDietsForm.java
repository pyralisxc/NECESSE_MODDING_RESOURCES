/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.ClipboardTracker;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietFilterForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietsContentBox;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerDietsData;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementDietsForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    public int maxHeight;
    protected FormSwitcher setCurrentWhenLoaded;
    protected ArrayList<SettlementSettlerDietsData> settlers;
    protected Form mainForm;
    protected SettlementDietsContentBox content;
    public int dietsSubscription = -1;
    public ItemCategoriesFilter newSettlerDiet = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
    public PasteButton newSettlerDietPasteButton;
    public ClipboardTracker<SettlementDietFilterForm.DietData> listClipboard;

    public SettlementDietsForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.maxHeight = 300;
        this.mainForm = this.addComponent(new Form("settlers", 500, 300));
        this.mainForm.addComponent(new FormLocalLabel("ui", "settlementdiets", new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5));
        int dietButtonWidth = 150;
        int newSettlersButtonX = this.mainForm.getWidth() - dietButtonWidth - this.getInterfaceStyle().scrollbar.active.getHeight() - 2;
        int newSettlersY = 30;
        this.mainForm.addComponent(new FormLocalTextButton("ui", "settlementchangediet", newSettlersButtonX, newSettlersY + 3, dietButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
            SettlementDietFilterForm filterForm = new SettlementDietFilterForm("changediet", 408, 250, this.newSettlerDiet, client, new LocalMessage("ui", "settlementnewsettlers"), new LocalMessage("ui", "backbutton"), (SettlementContainer)container){
                final /* synthetic */ SettlementContainer val$container;
                {
                    this.val$container = settlementContainer;
                    super(name, width, height, dietFilter, client, header, buttonText);
                }

                @Override
                public void onItemsChanged(Item[] items, boolean allowed) {
                    this.val$container.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                }

                @Override
                public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                    this.val$container.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                }

                @Override
                public void onFullChange(ItemCategoriesFilter filter) {
                    this.val$container.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.fullChange(filter));
                }

                @Override
                public void onButtonPressed() {
                    SettlementDietsForm.this.makeCurrent(SettlementDietsForm.this.mainForm);
                }

                @Override
                public void onWindowResized(GameWindow window) {
                    super.onWindowResized(window);
                    ContainerComponent.setPosInventory(this);
                }
            };
            this.addAndMakeCurrentTemporary(filterForm);
            filterForm.onWindowResized(WindowManager.getWindow());
        });
        FormContentIconButton pasteButton = this.mainForm.addComponent(new FormContentIconButton(newSettlersButtonX -= 24, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
        pasteButton.onClicked(e -> {
            SettlementDietFilterForm.DietData dietData = this.listClipboard.getValue();
            if (dietData != null) {
                SaveData save = new SaveData("");
                dietData.filter.addSaveData(save);
                this.newSettlerDiet.applyLoadData(save.toLoadData());
                container.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.fullChange(this.newSettlerDiet));
                pasteButton.setActive(false);
            }
        });
        pasteButton.setupDragPressOtherButtons("dietPasteButton");
        this.newSettlerDietPasteButton = new PasteButton(pasteButton, this.newSettlerDiet);
        this.mainForm.addComponent(new FormContentIconButton(newSettlersButtonX -= 24, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
            SettlementDietFilterForm.DietData dietData = new SettlementDietFilterForm.DietData(this.newSettlerDiet);
            WindowManager.getWindow().putClipboard(dietData.getSaveData().getScript());
            this.listClipboard.forceUpdate();
        });
        this.mainForm.addComponent(new FormLocalLabel("ui", "settlementnewsettlers", new FontOptions(20), -1, 5, newSettlersY + 5));
        this.mainForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, 60, this.mainForm.getWidth(), true));
        this.content = this.mainForm.addComponent(new SettlementDietsContentBox(this, 0, 62, this.mainForm.getWidth(), this.mainForm.getHeight() - 30 - 32));
        this.listClipboard = new ClipboardTracker<SettlementDietFilterForm.DietData>(){

            @Override
            public SettlementDietFilterForm.DietData parse(String clipboard) {
                try {
                    return new SettlementDietFilterForm.DietData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(SettlementDietFilterForm.DietData value) {
                SettlementDietsForm.this.newSettlerDietPasteButton.updateActive(value);
                SettlementDietsForm.this.content.updatePasteButtons(value);
            }
        };
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementSettlersChangedEvent.class, event -> ((SettlementContainer)this.container).requestSettlerDiets.runAndSend());
        ((Container)((Object)this.container)).onEvent(SettlementSettlerDietsEvent.class, event -> {
            if (this.setCurrentWhenLoaded != null) {
                this.setCurrentWhenLoaded.makeCurrent(this);
            }
            this.setCurrentWhenLoaded = null;
            if (!this.containerForm.isCurrent(this)) {
                return;
            }
            this.settlers = event.settlers;
            this.content.updateDietsContent();
        });
        ((Container)((Object)this.container)).onEvent(SettlementNewSettlerDietChangedEvent.class, event -> {
            event.change.applyTo(this.newSettlerDiet);
            this.listClipboard.forceUpdate();
            this.listClipboard.onUpdate(this.listClipboard.getValue());
        });
        ((Container)((Object)this.container)).onEvent(SettlementSettlerDietChangedEvent.class, event -> {
            if (this.settlers == null) {
                return;
            }
            for (SettlementSettlerDietsData settler : this.settlers) {
                if (settler.mobUniqueID != event.mobUniqueID) continue;
                event.change.applyTo(settler.dietFilter);
                this.listClipboard.forceUpdate();
                this.listClipboard.onUpdate(this.listClipboard.getValue());
                return;
            }
            ((SettlementContainer)this.container).requestSettlerDiets.runAndSend();
        });
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        super.draw(tickManager, perspective, renderBox);
    }

    public void updateSize() {
        this.mainForm.setHeight(Math.min(this.maxHeight, this.content.getY() + this.content.contentHeight));
        this.content.setContentBox(new Rectangle(0, 0, this.content.getWidth(), this.content.contentHeight));
        this.content.setWidth(this.mainForm.getWidth());
        this.content.setHeight(this.mainForm.getHeight() - this.content.getY());
        ContainerComponent.setPosInventory(this.mainForm);
    }

    @Override
    public void onSetCurrent(boolean current) {
        this.content.clearComponents();
        this.settlers = null;
        if (current) {
            if (this.dietsSubscription == -1) {
                this.dietsSubscription = ((SettlementContainer)this.container).subscribeDiets.subscribe();
            }
            this.makeCurrent(this.mainForm);
        } else if (this.dietsSubscription != -1) {
            ((SettlementContainer)this.container).subscribeDiets.unsubscribe(this.dietsSubscription);
            this.dietsSubscription = -1;
        }
    }

    @Override
    public void onMenuButtonClicked(FormSwitcher switcher) {
        this.setCurrentWhenLoaded = switcher;
        ((SettlementContainer)this.container).requestSettlerDiets.runAndSend();
        if (this.dietsSubscription == -1) {
            this.dietsSubscription = ((SettlementContainer)this.container).subscribeDiets.subscribe();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateSize();
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementdiets");
    }

    @Override
    public String getTypeString() {
        return "diets";
    }

    public static class PasteButton {
        public final FormContentIconButton button;
        public final ItemCategoriesFilter dietFilter;

        public PasteButton(FormContentIconButton button, ItemCategoriesFilter dietFilter) {
            this.button = button;
            this.dietFilter = dietFilter;
        }

        public void updateActive(SettlementDietFilterForm.DietData data) {
            if (data != null && data.filter != null) {
                this.button.setActive(!data.filter.isEqualsFilter(this.dietFilter));
                return;
            }
            this.button.setActive(false);
        }
    }
}

