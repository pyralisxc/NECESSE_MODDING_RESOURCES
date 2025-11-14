/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.playerStats;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.ItemRegistry;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemDisplayComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.playerStats.PlayerStatsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class PlayerStatsObtainedItemsForm
extends Form
implements PlayerStatsSubForm {
    private final PlayerStatsForm statsForm;
    private final FormContentBox itemList;
    private final FormDropdownSelectionButton<ListFilter> filterButton;
    private final Runnable backPressed;

    public PlayerStatsObtainedItemsForm(PlayerStatsForm statsForm, PlayerStats stats, int padding, Runnable backPressed) {
        super(statsForm.getWidth(), statsForm.getHeight());
        this.statsForm = statsForm;
        this.backPressed = backPressed;
        this.drawBase = false;
        LinkedList<String> obtainedItems = new LinkedList<String>();
        for (String s : stats.items_obtained.getStatItemsObtained()) {
            obtainedItems.add(s);
        }
        List<Item> items = ItemRegistry.getItems();
        items.removeIf(i -> !ItemRegistry.countsInStats(i.getID()) || obtainedItems.contains(i.getStringID()));
        if (stats.items_obtained.getTotalStatItems() >= ItemRegistry.getTotalStatItemsObtainable()) {
            this.addComponent(new FormLocalLabel("stats", "have_all_items", new FontOptions(20), 0, this.getWidth() / 2, padding, this.getWidth()));
            this.filterButton = null;
            this.itemList = null;
        } else {
            this.addComponent(new FormLocalLabel("stats", "filter", new FontOptions(16), -1, 5, padding));
            this.filterButton = this.addComponent(new FormDropdownSelectionButton(4, 20 + padding, FormInputSize.SIZE_20, ButtonColor.BASE, this.getWidth() - 8));
            this.filterButton.options.add(ListFilter.ALL, ListFilter.ALL.displayName);
            this.filterButton.options.add(ListFilter.OBTAINED, ListFilter.OBTAINED.displayName);
            this.filterButton.options.add(ListFilter.NOT_OBTAINED, ListFilter.NOT_OBTAINED.displayName);
            this.filterButton.setSelected(ListFilter.ALL, ListFilter.ALL.displayName);
            this.filterButton.onSelected(e -> this.updateList(stats, (ListFilter)((Object)((Object)e.value))));
            this.itemList = this.addComponent(new FormContentBox(0, 40 + padding, this.getWidth(), this.getHeight() - 40 - padding));
            this.updateList(stats, this.filterButton.getSelected());
        }
    }

    @Override
    public boolean backPressed() {
        this.backPressed.run();
        return true;
    }

    public void updateList(PlayerStats stats) {
        if (this.filterButton == null) {
            return;
        }
        this.updateList(stats, this.filterButton.getSelected());
    }

    private void updateList(PlayerStats stats, ListFilter filter) {
        if (this.itemList == null) {
            return;
        }
        this.itemList.clearComponents();
        int itemSize = 32;
        int itemPadding = 2;
        int maxPerLine = (this.getWidth() - itemPadding) / (itemSize + itemPadding);
        int edgePadding = (this.getWidth() - itemPadding) % (itemSize + itemPadding) / 2;
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        for (Item item : ItemRegistry.getItems()) {
            if (!ItemRegistry.countsInStats(item.getID()) || !filter.filter.apply(item, stats).booleanValue()) continue;
            items.add(item.getDefaultItem(null, 1));
        }
        items.sort(null);
        for (int i = 0; i < items.size(); ++i) {
            InventoryItem inventoryItem = (InventoryItem)items.get(i);
            int column = i % maxPerLine;
            int row = i / maxPerLine;
            final boolean obtained = stats.items_obtained.isStatItemObtained(inventoryItem.item.getStringID());
            this.itemList.addComponent(new FormItemDisplayComponent(column * (itemSize + itemPadding), row * (itemSize + itemPadding), inventoryItem){

                @Override
                public Color getItemDrawColor() {
                    if (!obtained) {
                        return new Color(25, 25, 25);
                    }
                    return super.getItemDrawColor();
                }

                @Override
                public GameTooltips getTooltip() {
                    StringTooltips tooltips = new StringTooltips(this.item.getItemDisplayName(), this.item.item.getRarityColor(this.item));
                    if (obtained) {
                        tooltips.add(Localization.translate("stats", "show_items_obtained"), GameColor.GREEN);
                    } else {
                        tooltips.add(Localization.translate("stats", "show_items_not_obtained"), GameColor.RED);
                    }
                    return tooltips;
                }
            });
        }
        this.itemList.fitContentBoxToComponents(edgePadding, 0, itemPadding, itemPadding);
    }

    @Override
    public void updateDisabled(int headerHeight) {
        this.setPosition(0, headerHeight);
        this.setHeight(this.statsForm.getHeight() - headerHeight);
        if (this.itemList != null) {
            this.itemList.setHeight(this.getHeight() - this.itemList.getY());
        }
    }

    private static enum ListFilter {
        ALL(new LocalMessage("stats", "show_items_all"), (i, s) -> true),
        OBTAINED(new LocalMessage("stats", "show_items_obtained"), (i, s) -> s.items_obtained.isStatItemObtained(i.getStringID())),
        NOT_OBTAINED(new LocalMessage("stats", "show_items_not_obtained"), (i, s) -> !s.items_obtained.isStatItemObtained(i.getStringID()));

        public final GameMessage displayName;
        public final BiFunction<Item, PlayerStats, Boolean> filter;

        private ListFilter(GameMessage displayName, BiFunction<Item, PlayerStats, Boolean> filter) {
            this.displayName = displayName;
            this.filter = filter;
        }
    }
}

