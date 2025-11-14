/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.ClipboardTracker;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.Inventory;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public abstract class SettlementStorageConfigForm
extends FormSwitcher {
    public final Point tile;
    public final Client client;
    public final ItemCategoriesFilter filter;
    private final Form mainForm;
    private ClipboardTracker<ConfigData> listClipboard;
    private final FormContentIconButton pasteButton;
    public final ItemCategoriesFilterForm filterForm;
    public final FormDropdownSelectionButton<Integer> prioritySelect;
    public final FormDropdownSelectionButton<ItemCategoriesFilter.ItemLimitMode> limitMode;
    public final FormTextInput limitInput;

    public SettlementStorageConfigForm(String name, int width, int height, Point tile, Client client, Inventory inventory, GameMessage header, ItemCategoriesFilter filter, int currentPriority) {
        this.tile = tile;
        this.client = client;
        this.filter = filter;
        this.mainForm = this.addComponent(new Form(name, width, height));
        FormFlow flow = new FormFlow(5);
        if (header != null) {
            this.mainForm.addComponent(new FormLocalLabel(header, new FontOptions(20), -1, 5, flow.next(30)));
        }
        int priorityY = flow.next(28);
        this.prioritySelect = this.mainForm.addComponent(new FormDropdownSelectionButton(4, priorityY, FormInputSize.SIZE_24, ButtonColor.BASE, this.mainForm.getWidth() - 8));
        this.prioritySelect.options.add(300, SettlementInventory.getPriorityText(300));
        this.prioritySelect.options.add(200, SettlementInventory.getPriorityText(200));
        this.prioritySelect.options.add(100, SettlementInventory.getPriorityText(100));
        this.prioritySelect.options.add(0, SettlementInventory.getPriorityText(0));
        this.prioritySelect.options.add(-100, SettlementInventory.getPriorityText(-100));
        this.prioritySelect.options.add(-200, SettlementInventory.getPriorityText(-200));
        this.prioritySelect.options.add(-300, SettlementInventory.getPriorityText(-300));
        this.updatePrioritySelect(currentPriority);
        this.prioritySelect.onSelected(e -> this.onPriorityChange((Integer)e.value));
        int limitY = flow.next(28);
        this.limitMode = this.mainForm.addComponent(new FormDropdownSelectionButton(4, limitY, FormInputSize.SIZE_24, ButtonColor.BASE, this.mainForm.getWidth() / 2 - 6));
        for (ItemCategoriesFilter.ItemLimitMode value : ItemCategoriesFilter.ItemLimitMode.values()) {
            this.limitMode.options.add(value, value.displayName, value.tooltip == null ? null : () -> value.tooltip);
        }
        this.updateLimitMode();
        this.limitInput = this.mainForm.addComponent(new FormTextInput(this.mainForm.getWidth() / 2 + 2, limitY, FormInputSize.SIZE_24, this.mainForm.getWidth() / 2 - 6, 7));
        this.updateLimitInput();
        this.limitInput.setRegexMatchFull("([0-9]+)?");
        this.limitInput.rightClickToClear = true;
        this.limitInput.onSubmit(e -> {
            try {
                int next = this.limitInput.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(this.limitInput.getText());
                if (filter.maxAmount != next) {
                    filter.maxAmount = next;
                    this.updateLimitInput();
                    this.onLimitChange(filter.limitMode, filter.maxAmount);
                }
            }
            catch (NumberFormatException ex) {
                this.updateLimitInput();
            }
        });
        this.limitMode.onSelected(e -> {
            if (filter.limitMode != e.value) {
                filter.limitMode = (ItemCategoriesFilter.ItemLimitMode)((Object)((Object)e.value));
                this.limitInput.tooltip = filter.limitMode.inputPlaceholder;
                this.limitInput.placeHolder = filter.limitMode.inputPlaceholder;
                this.onLimitChange(filter.limitMode, filter.maxAmount);
            }
        });
        int searchY = flow.next(28);
        int contentY = flow.next();
        int contentHeight = height - contentY - 32;
        final FormContentBox filterContent = this.mainForm.addComponent(new FormContentBox(0, contentY, this.mainForm.getWidth(), contentHeight));
        ItemCategoryExpandedSetting expandedSetting = Settings.getItemCategoryExpandedSetting(name);
        this.filterForm = filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, filter, ItemCategoriesFilterForm.Mode.ALLOW_MAX_AMOUNT, expandedSetting, client.characterStats.items_obtained, true){

            @Override
            public void onDimensionsChanged(int width, int height) {
                filterContent.setContentBox(new Rectangle(0, 0, Math.max(SettlementStorageConfigForm.this.mainForm.getWidth(), width), this.getY() + height));
            }

            @Override
            public void onItemsChanged(Item[] items, boolean allowed) {
                SettlementStorageConfigForm.this.onItemsChanged(items, allowed);
            }

            @Override
            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                SettlementStorageConfigForm.this.onItemLimitsChanged(item, limits);
            }

            @Override
            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                SettlementStorageConfigForm.this.onCategoryChanged(category, allowed);
            }

            @Override
            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                SettlementStorageConfigForm.this.onCategoryLimitsChanged(category, maxItems);
            }
        });
        filterContent.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, this.mainForm.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
            if (!this.filterForm.filter.master.isAllAllowed() || !this.filterForm.filter.master.isAllDefault()) {
                this.filterForm.filter.master.setAllowed(true);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, true);
            }
        });
        filterContent.addComponent(new FormLocalTextButton("ui", "clearallbutton", this.mainForm.getWidth() / 2 + 2, 0, this.mainForm.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
            if (this.filterForm.filter.master.isAnyAllowed()) {
                this.filterForm.filter.master.setAllowed(false);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, false);
            }
        });
        FormTextInput searchInput = this.mainForm.addComponent(new FormTextInput(4, searchY, FormInputSize.SIZE_24, this.mainForm.getWidth() - 28 - 28 - 28 - 8, -1, 500));
        searchInput.placeHolder = new LocalMessage("ui", "searchtip");
        searchInput.onChange(e -> this.filterForm.setSearch(searchInput.getText()));
        this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28, searchY, FormInputSize.SIZE_24, ButtonColor.RED, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "settlementremovestorage"))).onClicked(e -> this.onRemove());
        this.pasteButton = this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28 - 28, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
        this.pasteButton.onClicked(e -> {
            ConfigData data = this.listClipboard.getValue();
            if (data != null && (filter.loadFromCopy(data.filter) || this.prioritySelect.getSelected() != data.priority)) {
                this.filterForm.updateAllButtons();
                this.updatePrioritySelect(data.priority);
                this.updateLimitMode();
                this.updateLimitInput();
                this.onFullChange(filter, data.priority);
            }
        });
        this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28 - 28 - 28, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
            SaveData save = new ConfigData(this.prioritySelect.getSelected(), filter).getSaveData();
            WindowManager.getWindow().putClipboard(save.getScript());
            this.listClipboard.forceUpdate();
        });
        this.listClipboard = new ClipboardTracker<ConfigData>(){

            @Override
            public ConfigData parse(String clipboard) {
                try {
                    return new ConfigData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(ConfigData value) {
                SettlementStorageConfigForm.this.pasteButton.setActive(value != null);
            }
        };
        this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", width / 2 - 4, this.mainForm.getHeight() - 28, width / 2, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> this.onBack());
        this.makeCurrent(this.mainForm);
    }

    public void updatePrioritySelect(int priority) {
        this.prioritySelect.setSelected(priority, SettlementInventory.getPriorityText(priority));
    }

    public void updateLimitMode() {
        this.limitMode.setSelected(this.filter.limitMode, this.filter.limitMode.displayName);
    }

    public void updateLimitInput() {
        this.limitInput.tooltip = this.filter.limitMode.inputPlaceholder;
        this.limitInput.placeHolder = this.filter.limitMode.inputPlaceholder;
        if (!this.limitInput.isTyping()) {
            if (this.filter.maxAmount != Integer.MAX_VALUE) {
                this.limitInput.setText(String.valueOf(this.filter.maxAmount));
            } else {
                this.limitInput.setText("");
            }
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        super.draw(tickManager, perspective, renderBox);
    }

    public void setPosFocus() {
        ContainerComponent.setPosFocus(this.mainForm);
    }

    public void setPosInventory() {
        ContainerComponent.setPosInventory(this.mainForm);
    }

    public abstract void onItemsChanged(Item[] var1, boolean var2);

    public abstract void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2);

    public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

    public abstract void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2);

    public abstract void onFullChange(ItemCategoriesFilter var1, int var2);

    public abstract void onPriorityChange(int var1);

    public abstract void onLimitChange(ItemCategoriesFilter.ItemLimitMode var1, int var2);

    public abstract void onRemove();

    public abstract void onBack();

    private static class ConfigData {
        public final int priority;
        public final ItemCategoriesFilter filter;

        public ConfigData(int priority, ItemCategoriesFilter filter) {
            this.priority = priority;
            this.filter = filter;
        }

        public ConfigData(LoadData save) {
            this.priority = save.getInt("priority", 0, false);
            this.filter = new ItemCategoriesFilter(false);
            this.filter.applyLoadData(save.getFirstLoadDataByName("filter"));
        }

        public SaveData getSaveData() {
            SaveData save = new SaveData("config");
            save.addInt("priority", this.priority);
            SaveData filtersSave = new SaveData("filter");
            this.filter.addSaveData(filtersSave);
            save.addSaveData(filtersSave);
            return save;
        }
    }
}

