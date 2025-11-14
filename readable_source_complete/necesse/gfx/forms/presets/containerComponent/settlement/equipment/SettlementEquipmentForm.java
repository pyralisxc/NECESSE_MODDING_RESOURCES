/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement.equipment;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.ClipboardTracker;
import necesse.engine.Settings;
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
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.forms.presets.containerComponent.settlement.equipment.SettlementEquipmentContentBox;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFiltersEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementEquipmentForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    public int maxHeight;
    protected FormSwitcher setCurrentWhenLoaded;
    protected ArrayList<SettlementSettlerEquipmentFilterData> settlers;
    protected Form mainForm;
    protected SettlementEquipmentContentBox content;
    public int equipmentsSubscription = -1;
    public boolean newSettlerSelfManageEquipment = true;
    public boolean newSettlerPreferArmorSets = true;
    public ItemCategoriesFilter newSettlerEquipmentsFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
    public PasteButton newSettlerPasteButton;
    public ClipboardTracker<EquipmentFiltersForm.EquipmentFilterData> listClipboard;

    public SettlementEquipmentForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.maxHeight = 300;
        this.mainForm = this.addComponent(new Form("settlers", 500, 300));
        this.mainForm.addComponent(new FormLocalLabel("ui", "settlementequipment", new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5));
        FormContentIconToggleButton showHeadArmor = this.mainForm.addComponent(new FormContentIconToggleButton(this.mainForm.getWidth() - 28, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, this.getInterfaceStyle().button_escaped_20, new GameMessage[]{new LocalMessage("settingsui", "showsettlerheadarmor")}){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setToggled(Settings.showSettlerHeadArmor);
                super.draw(tickManager, perspective, renderBox);
            }
        });
        showHeadArmor.onToggled(e -> {
            Settings.showSettlerHeadArmor = ((FormButtonToggle)e.from).isToggled();
            Settings.saveClientSettings();
        });
        int filterButtonWidth = 24;
        int newSettlersButtonX = this.mainForm.getWidth() - filterButtonWidth - this.getInterfaceStyle().scrollbar.active.getHeight() - 2;
        int newSettlersY = 30;
        FormContentIconButton configureFilterButton = this.mainForm.addComponent(new FormContentIconButton(newSettlersButtonX, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_storage_config, new GameMessage[]{new LocalMessage("ui", "settlerfilterequipment")}){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setActive(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
                super.draw(tickManager, perspective, renderBox);
            }
        });
        configureFilterButton.onClicked(e -> {
            EquipmentFiltersForm filterForm = new EquipmentFiltersForm("changeequipmentfilter", 408, 300, this.newSettlerEquipmentsFilter, () -> this.newSettlerPreferArmorSets, client, new LocalMessage("ui", "settlementnewsettlers"), new LocalMessage("ui", "backbutton"), (SettlementContainer)container){
                final /* synthetic */ SettlementContainer val$container;
                {
                    this.val$container = settlementContainer;
                    super(name, width, height, equipmentFilter, preferArmorSets, client, header, buttonText);
                }

                @Override
                public void onSetPreferArmorSets(boolean preferArmorSets) {
                    SettlementEquipmentForm.this.newSettlerPreferArmorSets = preferArmorSets;
                    this.val$container.setNewSettlerEquipmentFilter.runAndSendPreferArmorSets(preferArmorSets);
                }

                @Override
                public void onItemsChanged(Item[] items, boolean allowed) {
                    this.val$container.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                }

                @Override
                public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                    this.val$container.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                }

                @Override
                public void onFullChange(ItemCategoriesFilter filter) {
                    this.val$container.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.fullChange(filter));
                }

                @Override
                public void onButtonPressed() {
                    SettlementEquipmentForm.this.makeCurrent(SettlementEquipmentForm.this.mainForm);
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
        FormContentIconButton pasteButton = this.mainForm.addComponent(new FormContentIconButton(newSettlersButtonX -= 24, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}){

            @Override
            public boolean isActive() {
                return super.isActive() && SettlementEquipmentForm.this.newSettlerSelfManageEquipment;
            }
        });
        pasteButton.onClicked(e -> {
            EquipmentFiltersForm.EquipmentFilterData equipmentFilterData = this.listClipboard.getValue();
            if (equipmentFilterData != null) {
                SaveData save = new SaveData("");
                equipmentFilterData.filter.addSaveData(save);
                this.newSettlerPreferArmorSets = equipmentFilterData.preferArmorSets;
                this.newSettlerEquipmentsFilter.applyLoadData(save.toLoadData());
                container.setNewSettlerEquipmentFilter.runAndSendPreferArmorSets(this.newSettlerPreferArmorSets);
                container.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.fullChange(this.newSettlerEquipmentsFilter));
                pasteButton.setActive(false);
            }
        });
        pasteButton.setupDragPressOtherButtons("equipmentPasteButton");
        this.newSettlerPasteButton = new PasteButton(pasteButton, () -> this.newSettlerPreferArmorSets, this.newSettlerEquipmentsFilter);
        FormContentIconButton copyButton = this.mainForm.addComponent(new FormContentIconButton(newSettlersButtonX -= 24, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setActive(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
                super.draw(tickManager, perspective, renderBox);
            }
        });
        copyButton.onClicked(e -> {
            EquipmentFiltersForm.EquipmentFilterData equipmentFilterData = new EquipmentFiltersForm.EquipmentFilterData(this.newSettlerPreferArmorSets, this.newSettlerEquipmentsFilter);
            WindowManager.getWindow().putClipboard(equipmentFilterData.getSaveData().getScript());
            this.listClipboard.forceUpdate();
        });
        FormContentIconToggleButton selfManageButton = this.mainForm.addComponent(new FormContentIconToggleButton(newSettlersButtonX -= 24, newSettlersY + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, this.getInterfaceStyle().button_escaped_20, new GameMessage[]{new LocalMessage("ui", "settlerselfmanagequipment")}){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setToggled(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
                super.draw(tickManager, perspective, renderBox);
            }
        });
        selfManageButton.onToggled(e -> {
            this.newSettlerSelfManageEquipment = ((FormButtonToggle)e.from).isToggled();
            container.setNewSettlerEquipmentFilter.runAndSendSelfManageEquipment(this.newSettlerSelfManageEquipment);
        });
        selfManageButton.setupDragToOtherButtons("selfManageEquipment");
        this.mainForm.addComponent(new FormLocalLabel("ui", "settlementnewsettlers", new FontOptions(20), -1, 5, newSettlersY + 5));
        this.mainForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, 60, this.mainForm.getWidth(), true));
        this.content = this.mainForm.addComponent(new SettlementEquipmentContentBox(this, 0, 62, this.mainForm.getWidth(), this.mainForm.getHeight() - 30 - 32));
        this.listClipboard = new ClipboardTracker<EquipmentFiltersForm.EquipmentFilterData>(){

            @Override
            public EquipmentFiltersForm.EquipmentFilterData parse(String clipboard) {
                try {
                    return new EquipmentFiltersForm.EquipmentFilterData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(EquipmentFiltersForm.EquipmentFilterData value) {
                SettlementEquipmentForm.this.newSettlerPasteButton.updateActive(value);
                SettlementEquipmentForm.this.content.updatePasteButtons(value);
            }
        };
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementSettlersChangedEvent.class, event -> ((SettlementContainer)this.container).requestSettlerEquipmentFilters.runAndSend());
        ((Container)((Object)this.container)).onEvent(SettlementSettlerEquipmentFiltersEvent.class, event -> {
            if (this.setCurrentWhenLoaded != null) {
                this.setCurrentWhenLoaded.makeCurrent(this);
            }
            this.setCurrentWhenLoaded = null;
            if (!this.containerForm.isCurrent(this)) {
                return;
            }
            this.settlers = event.settlers;
            this.content.updateEquipmentsContent();
        });
        ((Container)((Object)this.container)).onEvent(SettlementNewSettlerEquipmentFilterChangedEvent.class, event -> {
            this.newSettlerSelfManageEquipment = event.selfManageEquipment;
            this.newSettlerPreferArmorSets = event.preferArmorSets;
            if (event.change != null) {
                event.change.applyTo(this.newSettlerEquipmentsFilter);
            }
            this.listClipboard.forceUpdate();
            this.listClipboard.onUpdate(this.listClipboard.getValue());
        });
        ((Container)((Object)this.container)).onEvent(SettlementSettlerEquipmentFilterChangedEvent.class, event -> {
            if (this.settlers == null) {
                return;
            }
            for (SettlementSettlerEquipmentFilterData settler : this.settlers) {
                if (settler.mobUniqueID != event.mobUniqueID) continue;
                settler.preferArmorSets = event.preferArmorSets;
                if (event.change != null) {
                    event.change.applyTo(settler.equipmentFilter);
                }
                this.listClipboard.forceUpdate();
                this.listClipboard.onUpdate(this.listClipboard.getValue());
                return;
            }
            ((SettlementContainer)this.container).requestSettlerEquipmentFilters.runAndSend();
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
            if (this.equipmentsSubscription == -1) {
                this.equipmentsSubscription = ((SettlementContainer)this.container).subscribeEquipment.subscribe();
            }
            this.makeCurrent(this.mainForm);
        } else if (this.equipmentsSubscription != -1) {
            ((SettlementContainer)this.container).subscribeEquipment.unsubscribe(this.equipmentsSubscription);
            this.equipmentsSubscription = -1;
        }
    }

    @Override
    public void onMenuButtonClicked(FormSwitcher switcher) {
        this.setCurrentWhenLoaded = switcher;
        ((SettlementContainer)this.container).requestSettlerEquipmentFilters.runAndSend();
        if (this.equipmentsSubscription == -1) {
            this.equipmentsSubscription = ((SettlementContainer)this.container).subscribeEquipment.subscribe();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateSize();
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementequipment");
    }

    @Override
    public String getTypeString() {
        return "equipment";
    }

    public static class PasteButton {
        public final FormContentIconButton button;
        public final Supplier<Boolean> preferArmorSets;
        public final ItemCategoriesFilter equipmentsFilter;

        public PasteButton(FormContentIconButton button, Supplier<Boolean> preferArmorSets, ItemCategoriesFilter equipmentsFilter) {
            this.button = button;
            this.preferArmorSets = preferArmorSets;
            this.equipmentsFilter = equipmentsFilter;
        }

        public void updateActive(EquipmentFiltersForm.EquipmentFilterData data) {
            if (data != null && data.filter != null) {
                this.button.setActive(data.preferArmorSets != this.preferArmorSets.get() || !data.filter.isEqualsFilter(this.equipmentsFilter));
                return;
            }
            this.button.setActive(false);
        }
    }
}

