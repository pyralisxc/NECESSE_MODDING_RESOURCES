/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class SelectItemFloatMenu
extends FormFloatMenu {
    public SelectItemFloatMenu(FormComponent parent, Client client, int width, int height) {
        super(parent);
        Form configureForm = new Form(width, height);
        final FormContentBox filterContent = configureForm.addComponent(new FormContentBox(0, 32, configureForm.getWidth(), configureForm.getHeight() - 32));
        ItemCategoriesFilter ingredientFilter = new ItemCategoriesFilter(true);
        ItemCategoryExpandedSetting expandedSetting = Settings.getItemCategoryExpandedSetting("dowhenthresholdcondition");
        ItemCategoriesFilterForm filterForm = filterContent.addComponent(new ItemCategoriesFilterForm(4, 0, ingredientFilter, ItemCategoriesFilterForm.Mode.ONLY_CLICK_ITEM, expandedSetting, client == null ? null : client.characterStats.items_obtained, true){

            @Override
            public void onDimensionsChanged(int width, int height) {
                filterContent.setContentBox(new Rectangle(0, 0, Math.max(this.getWidth(), width), this.getY() + height));
            }

            @Override
            public void onItemClicked(Item item) {
                SelectItemFloatMenu.this.onItemSelected(item);
            }
        });
        FormTextInput searchInput = configureForm.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, filterContent.getWidth() - 8, -1, 500));
        searchInput.placeHolder = new LocalMessage("ui", "searchtip");
        searchInput.onChange(searchEvent -> filterForm.setSearch(searchInput.getText()));
        this.setForm(configureForm);
    }

    public abstract void onItemSelected(Item var1);
}

