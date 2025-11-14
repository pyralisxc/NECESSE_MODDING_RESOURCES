/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.awt.Rectangle;
import necesse.engine.ClipboardTracker;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class SettlementDietFilterForm
extends Form {
    private ClipboardTracker<DietData> listClipboard;
    private final FormContentIconButton pasteButton;
    private final FormContentBox filterContent;
    private ItemCategoriesFilterForm filterForm;

    public SettlementDietFilterForm(String name, int width, int height, Mob mob, ItemCategoriesFilter dietFilter, Client client) {
        this(name, width, height, dietFilter, client, MobRegistry.getLocalization(mob.getID()), new LocalMessage("ui", "backbutton"));
    }

    public SettlementDietFilterForm(String name, int width, int height, ItemCategoriesFilter dietFilter, Client client, GameMessage header, GameMessage buttonText) {
        super(name, width, height);
        FormFlow flow = new FormFlow(5);
        if (header != null) {
            String headerStr = header.translate();
            FontOptions headerFontOptions = new FontOptions(20);
            String headerMaxStr = GameUtils.maxString(headerStr, headerFontOptions, this.getWidth() - 10);
            this.addComponent(new FormLabel(headerMaxStr, headerFontOptions, -1, 5, flow.next(30)));
        }
        int searchY = flow.next(28);
        int searchButtonsX = this.getWidth() - 28;
        this.pasteButton = this.addComponent(new FormContentIconButton(searchButtonsX, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
        this.pasteButton.onClicked(e -> {
            DietData data = this.listClipboard.getValue();
            if (data != null) {
                SaveData save = new SaveData("");
                data.filter.addSaveData(save);
                dietFilter.applyLoadData(save.toLoadData());
                this.onFullChange(dietFilter);
            }
        });
        this.listClipboard = new ClipboardTracker<DietData>(){

            @Override
            public DietData parse(String clipboard) {
                try {
                    return new DietData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(DietData value) {
                SettlementDietFilterForm.this.pasteButton.setActive(value != null);
            }
        };
        this.addComponent(new FormContentIconButton(searchButtonsX -= 28, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
            DietData data = new DietData(dietFilter);
            WindowManager.getWindow().putClipboard(data.getSaveData().getScript());
            this.listClipboard.forceUpdate();
        });
        FormTextInput searchInput = this.addComponent(new FormTextInput(4, searchY, FormInputSize.SIZE_24, searchButtonsX - 8, -1, 500));
        searchInput.placeHolder = new LocalMessage("ui", "searchtip");
        searchInput.onChange(e -> this.filterForm.setSearch(searchInput.getText()));
        int filterHeight = this.getHeight() - flow.next() - (buttonText != null ? 24 : 0) - 4;
        this.filterContent = this.addComponent(new FormContentBox(0, flow.next(filterHeight), this.getWidth(), filterHeight));
        this.filterContent.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
            if (!this.filterForm.filter.master.isAllAllowed()) {
                this.filterForm.filter.master.setAllowed(true);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, true);
            }
        });
        this.filterContent.addComponent(new FormLocalTextButton("ui", "clearallbutton", this.getWidth() / 2 + 2, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
            if (this.filterForm.filter.master.isAnyAllowed()) {
                this.filterForm.filter.master.setAllowed(false);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, false);
            }
        });
        ItemCategoryExpandedSetting expandedSetting = Settings.getItemCategoryExpandedSetting("settlerdietfilter");
        this.filterForm = this.filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, dietFilter, ItemCategoriesFilterForm.Mode.ONLY_ALLOWED, expandedSetting, client.characterStats.items_obtained, true){

            @Override
            public void onDimensionsChanged(int width, int height) {
                SettlementDietFilterForm.this.filterContent.setContentBox(new Rectangle(0, 0, Math.max(SettlementDietFilterForm.this.getWidth(), width), this.getY() + height));
            }

            @Override
            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
            }

            @Override
            public void onItemsChanged(Item[] items, boolean allowed) {
                SettlementDietFilterForm.this.onItemsChanged(items, allowed);
            }

            @Override
            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                SettlementDietFilterForm.this.onCategoryChanged(category, allowed);
            }

            @Override
            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
            }
        });
        if (buttonText != null) {
            this.addComponent(new FormLocalTextButton(buttonText, width / 2, flow.next(), width / 2 - 4, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> this.onButtonPressed());
        }
    }

    public abstract void onItemsChanged(Item[] var1, boolean var2);

    public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

    public abstract void onFullChange(ItemCategoriesFilter var1);

    public abstract void onButtonPressed();

    public static class DietData {
        public final ItemCategoriesFilter filter;

        public DietData(ItemCategoriesFilter filter) {
            this.filter = filter;
        }

        public DietData(LoadData save) {
            if (!save.getName().equals("diet")) {
                throw new LoadDataException("Diet filter incorrect save component name");
            }
            this.filter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, false);
            this.filter.applyLoadData(save);
        }

        public SaveData getSaveData() {
            SaveData save = new SaveData("diet");
            this.filter.addSaveData(save);
            return save;
        }
    }
}

