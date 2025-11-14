/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.stats.ItemsObtainedStat;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconValueButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemSearchTester;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class ItemCategoriesFilterForm
extends Form {
    public final ItemCategoriesFilter filter;
    public final Mode mode;
    private ItemCategoryExpandedSetting expandedSetting;
    private final ItemsObtainedStat itemsObtainedStats;
    private final LinkedForm contentForm;
    private final HashMap<Integer, ItemForm> itemForms = new HashMap();
    private final HashMap<Integer, CategoryForm> categoryForms = new HashMap();

    public ItemCategoriesFilterForm(int x, int y, ItemCategoriesFilter filter, Mode mode, ItemCategoryExpandedSetting expandedSetting, ItemsObtainedStat itemsObtainedStats, boolean collapseEmpty) {
        super(0, 0);
        this.drawBase = false;
        this.filter = filter;
        this.mode = mode;
        this.expandedSetting = expandedSetting;
        this.itemsObtainedStats = itemsObtainedStats;
        this.setPosition(x, y);
        this.contentForm = this.addComponent(new LinkedForm("master", 0, 0, null, null));
        this.contentForm.drawBase = false;
        ItemCategoriesFilter.ItemCategoryFilter startCategory = filter.master;
        if (collapseEmpty) {
            List childrenWithItems;
            while ((childrenWithItems = startCategory.getChildren().stream().filter(c -> !this.isCategoryEmptyRecursive((ItemCategoriesFilter.ItemCategoryFilter)c)).collect(Collectors.toList())).size() == 1) {
                ItemCategoriesFilter.ItemCategoryFilter next;
                startCategory = next = (ItemCategoriesFilter.ItemCategoryFilter)childrenWithItems.get(0);
                this.expandedSetting = expandedSetting == null ? null : expandedSetting.getChild(next.category);
            }
        }
        if (startCategory.parent != null) {
            startCategory = startCategory.parent;
        }
        this.addChildren(this.contentForm, startCategory, expandedSetting);
        this.contentForm.updateYChildren();
        this.contentForm.updateDimensionsForward();
        this.updateDimensions();
    }

    public void onItemsChanged(Item[] items, boolean allowed) {
    }

    public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
    }

    public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
    }

    public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
    }

    public void onItemClicked(Item item) {
    }

    public abstract void onDimensionsChanged(int var1, int var2);

    public void updateDimensions() {
        int newHeight;
        int lastHeight;
        int newWidth;
        boolean update = false;
        int lastWidth = this.getWidth();
        if (lastWidth != (newWidth = this.contentForm.getWidth())) {
            update = true;
            this.setWidth(newWidth);
        }
        if ((lastHeight = this.getHeight()) != (newHeight = this.contentForm.getHeight())) {
            update = true;
            this.setHeight(newHeight);
        }
        if (update) {
            this.onDimensionsChanged(newWidth, newHeight);
        }
    }

    public void updateAllButtons() {
        for (ItemForm value : this.itemForms.values()) {
            value.updateButtonsForward();
            value.updateButtonsBack();
        }
    }

    public void updateButton(int itemID) {
        ItemForm itemForm = this.itemForms.get(itemID);
        if (itemForm != null) {
            itemForm.updateButtonsBack();
        }
    }

    public void updateButtons(ItemCategory category) {
        CategoryForm categoryForm = this.categoryForms.get(category.id);
        if (categoryForm != null) {
            categoryForm.updateButtonsForward();
            categoryForm.updateButtonsBack();
        }
    }

    public void setSearch(String searchString) {
        this.contentForm.updateSearch(searchString);
        this.contentForm.updateYChildren();
        this.contentForm.updateDimensionsForward();
        if (searchString == null || searchString.isEmpty()) {
            this.contentForm.setExpandedSetting(this.expandedSetting);
        } else {
            this.contentForm.setExpandedSetting(new ItemCategoryExpandedSetting(true));
        }
        this.updateDimensions();
    }

    private boolean isCategoryEmptyRecursive(ItemCategoriesFilter.ItemCategoryFilter category) {
        if (!category.hasAnyItems) {
            return category.getChildren().stream().allMatch(this::isCategoryEmptyRecursive);
        }
        return false;
    }

    protected void addChildren(LinkedForm form, ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter, ItemCategoryExpandedSetting lastExpandedSetting) {
        FormFlow flow = new FormFlow();
        AtomicReference<ItemForm> last = new AtomicReference<ItemForm>();
        itemCategoryFilter.getChildren().stream().sorted().forEach(c -> {
            ItemCategoryExpandedSetting nextSetting = lastExpandedSetting == null ? null : lastExpandedSetting.getChild(c.category);
            CategoryForm next = form.addComponent(new CategoryForm(form, (ItemCategoriesFilter.ItemCategoryFilter)c, nextSetting, (LinkedForm)last.get()));
            form.children.add(next);
            last.set((ItemForm)((Object)next));
            next.setPosition(0, flow.next(next.isHidden() ? 0 : next.getHeight()));
            form.setHeight(flow.next());
        });
        LinkedList knownItems = new LinkedList();
        LinkedList<Item> unknownItems = new LinkedList<Item>();
        itemCategoryFilter.streamItems().sorted(Comparator.comparing(i -> ItemRegistry.getDisplayName(i.getID()))).forEach(i -> {
            if (GlobalData.debugCheatActive() || this.itemsObtainedStats != null && this.itemsObtainedStats.isItemObtained(i.getStringID()) || GlobalData.stats().items_obtained.isItemObtained(i.getStringID())) {
                knownItems.add(i);
            } else {
                unknownItems.add((Item)i);
            }
        });
        for (Item item : knownItems) {
            ItemForm itemForm = form.addComponent(new ItemForm(item.getStringID(), form, (LinkedForm)last.get(), itemCategoryFilter, item));
            form.children.add(itemForm);
            itemForm.setPosition(0, flow.next(itemForm.isHidden() ? 0 : itemForm.getHeight()));
            form.setHeight(flow.next());
            last.set(itemForm);
            form.markHasItems();
        }
        if (!unknownItems.isEmpty()) {
            ItemForm itemForm = form.addComponent(new ItemForm(GameUtils.join(unknownItems.toArray(new Item[0]), Item::getStringID, "."), form, (LinkedForm)last.get(), itemCategoryFilter, unknownItems));
            form.children.add(itemForm);
            itemForm.setPosition(0, flow.next(itemForm.isHidden() ? 0 : itemForm.getHeight()));
            form.setHeight(flow.next());
            form.markHasItems();
        }
    }

    public static enum Mode {
        ALLOW_MAX_AMOUNT,
        ONLY_ALLOWED,
        ONLY_CLICK_ITEM;

    }

    protected static class LinkedForm
    extends Form {
        public boolean hasItems;
        public LinkedForm parent;
        public LinkedForm lastForm;
        public LinkedForm nextForm;
        public LinkedList<LinkedForm> children = new LinkedList();

        public LinkedForm(String name, int width, int height, LinkedForm parent, LinkedForm lastForm) {
            super(name, width, height);
            this.parent = parent;
            this.lastForm = lastForm;
            if (lastForm != null) {
                lastForm.nextForm = this;
            }
        }

        public void markHasItems() {
            this.hasItems = true;
            if (this.parent != null && !this.parent.hasItems) {
                this.parent.markHasItems();
            }
        }

        public void updateY() {
            if (this.lastForm != null) {
                if (this.lastForm.isHidden()) {
                    this.setY(this.lastForm.getY());
                } else {
                    this.setY(this.lastForm.getY() + this.lastForm.getHeight());
                }
            }
            if (this.nextForm != null) {
                this.nextForm.updateY();
            }
        }

        public void updateYChildren() {
            if (this.lastForm != null) {
                if (this.lastForm.isHidden()) {
                    this.setY(this.lastForm.getY());
                } else {
                    this.setY(this.lastForm.getY() + this.lastForm.getHeight());
                }
            }
            for (LinkedForm child : this.children) {
                child.updateYChildren();
            }
        }

        public final void updateDimensionsBack() {
            this.fixDimensions();
            this.updateY();
            if (this.parent != null) {
                this.parent.updateDimensionsBack();
            }
        }

        public final void updateDimensionsForward() {
            this.children.forEach(LinkedForm::updateDimensionsForward);
            this.fixDimensions();
            this.updateY();
        }

        public void fixDimensions() {
            this.setWidth(this.children.stream().filter(c -> !c.isHidden()).map(c -> c.getX() + c.getWidth()).max(Comparator.comparingInt(i -> i)).orElse(0));
            this.setHeight(this.children.stream().filter(c -> !c.isHidden()).map(c -> c.getY() + c.getHeight()).max(Comparator.comparingInt(i -> i)).orElse(0));
        }

        public void setExpandedSetting(ItemCategoryExpandedSetting setting) {
            for (LinkedForm child : this.children) {
                child.setExpandedSetting(setting);
            }
        }

        public void updateSearch(String searchString) {
            this.children.forEach(f -> f.updateSearch(searchString));
        }

        public final void updateButtonsBack() {
            this.updateButton();
            if (this.parent != null) {
                this.parent.updateButtonsBack();
            }
        }

        public final void updateButtonsForward() {
            this.updateButton();
            for (LinkedForm child : this.children) {
                child.updateButtonsForward();
            }
        }

        public void updateButton() {
        }
    }

    protected class ItemForm
    extends LinkedForm {
        public final Item[] items;
        public Item[] searchedItems;
        private final FormContentIconValueButton<CheckedState> toggleButton;
        private CheckedState state;
        private ItemCategoriesFilter.ItemLimits lastLimits;
        private final FormLocalLabel label;
        private final FormMouseHover labelHover;

        public ItemForm(String name, LinkedForm parent, LinkedForm lastForm, ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter, final Item ... items) {
            super(name, 20, 20, parent, lastForm);
            this.drawBase = false;
            for (Item item : items) {
                ItemCategoriesFilterForm.this.itemForms.put(item.getID(), this);
            }
            this.items = items;
            this.searchedItems = items;
            int currentX = 0;
            if (ItemCategoriesFilterForm.this.mode != Mode.ONLY_CLICK_ITEM) {
                this.toggleButton = this.addComponent(new FormContentIconValueButton(currentX, 0, FormInputSize.SIZE_20, ButtonColor.BASE));
                CheckedState.CHECKED.updateButton(this.toggleButton);
                this.toggleButton.onClicked(e -> {
                    if (this.state == CheckedState.CHECKED || this.state == CheckedState.DASH) {
                        for (Item item : this.searchedItems) {
                            ItemCategoriesFilterForm.this.filter.setItemAllowed(item, false);
                        }
                        ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, false);
                    } else {
                        for (Item item : this.searchedItems) {
                            ItemCategoriesFilterForm.this.filter.setItemAllowed(item, true);
                        }
                        ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, true);
                    }
                    this.updateButtonsBack();
                });
                this.toggleButton.setupDragToOtherButtons("pressItemCategory" + itemCategoryFilter.category.stringID, true, state -> {
                    if (state == CheckedState.CHECKED || state == CheckedState.ESCAPED) {
                        for (Item item : this.searchedItems) {
                            ItemCategoriesFilterForm.this.filter.setItemAllowed(item, state == CheckedState.CHECKED);
                        }
                        ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, state == CheckedState.CHECKED);
                        this.updateButtonsBack();
                        return true;
                    }
                    return false;
                });
                currentX += 20;
            } else {
                this.toggleButton = null;
            }
            if (items.length == 1) {
                this.addComponent(new FormItemPreview(currentX, 0, 20, items[0]));
                this.label = this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, currentX + 20 + 2, 2));
            } else {
                this.label = this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, currentX + 2, 2));
            }
            Rectangle labelBoundingBox = this.label.getBoundingBox();
            if (ItemCategoriesFilterForm.this.mode == Mode.ALLOW_MAX_AMOUNT) {
                this.labelHover = this.addComponent(new FormMouseHover(currentX + 2, 2, labelBoundingBox.width + 20, labelBoundingBox.height){

                    @Override
                    public GameTooltips getTooltips(PlayerMob perspective) {
                        if (items.length == 1) {
                            ListGameTooltips tooltips = new ListGameTooltips();
                            ItemCategoriesFilter.ItemLimits current = ItemCategoriesFilterForm.this.filter.getItemLimits(items[0]);
                            tooltips.add(new InputTooltip(-100, Localization.translate("ui", "setstoragelimit")));
                            if (current != null && !current.isDefault()) {
                                tooltips.add(new InputTooltip(-99, Localization.translate("ui", "clearstoragelimit")));
                            }
                            return tooltips;
                        }
                        return null;
                    }

                    @Override
                    public GameWindow.CURSOR getHoveringCursor(PlayerMob perspective) {
                        return items.length == 1 ? GameWindow.CURSOR.INTERACT : null;
                    }
                }, 1000);
                this.labelHover.acceptRightClicks = true;
                this.labelHover.onClicked(e -> {
                    if (items.length != 1) {
                        return;
                    }
                    ItemCategoriesFilter.ItemLimits current = ItemCategoriesFilterForm.this.filter.getItemLimits(items[0]);
                    if (e.event.getID() == -99) {
                        if (current != null && !current.isDefault()) {
                            ItemCategoriesFilter.ItemLimits next = new ItemCategoriesFilter.ItemLimits();
                            ItemCategoriesFilterForm.this.filter.setItemAllowed(items[0], next);
                            ItemCategoriesFilterForm.this.onItemLimitsChanged(items[0], next);
                        }
                        return;
                    }
                    Form form = new Form(200, 24);
                    FormTextInput limitInput = form.addComponent(new FormTextInput(0, 0, FormInputSize.SIZE_24, form.getWidth(), 7));
                    limitInput.placeHolder = new LocalMessage("ui", "storagelimit");
                    limitInput.setRegexMatchFull("([0-9]+)?");
                    limitInput.rightClickToClear = true;
                    limitInput.setText(current == null || current.isDefault() ? "" : "" + current.getMaxItems());
                    FormFloatMenu floatMenu = new FormFloatMenu(parent, form);
                    this.getManager().openFloatMenu((FloatMenu)floatMenu, -10, -12);
                    limitInput.onSubmit(e2 -> {
                        try {
                            ItemCategoriesFilter.ItemLimits next = limitInput.getText().isEmpty() ? (current == null ? null : new ItemCategoriesFilter.ItemLimits()) : new ItemCategoriesFilter.ItemLimits(Integer.parseInt(limitInput.getText()));
                            if (!(current == next || current != null && current.isSame(next))) {
                                ItemCategoriesFilterForm.this.filter.setItemAllowed(items[0], next);
                                ItemCategoriesFilterForm.this.onItemLimitsChanged(items[0], next);
                            }
                            floatMenu.remove();
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    });
                    limitInput.setTyping(true);
                });
            } else if (ItemCategoriesFilterForm.this.mode == Mode.ONLY_CLICK_ITEM && items.length == 1) {
                this.labelHover = this.addComponent(new FormMouseHover(currentX + 2, 2, labelBoundingBox.width + 20, labelBoundingBox.height){

                    @Override
                    public GameWindow.CURSOR getHoveringCursor(PlayerMob perspective) {
                        return GameWindow.CURSOR.INTERACT;
                    }
                }, 1000);
                this.labelHover.onClicked(e -> ItemCategoriesFilterForm.this.onItemClicked(items[0]));
            } else {
                this.labelHover = null;
            }
            this.fixDimensions();
            this.updateButton();
        }

        public ItemForm(String name, LinkedForm parent, LinkedForm lastForm, ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter, List<Item> items) {
            this(name, parent, lastForm, itemCategoryFilter, items.toArray(new Item[0]));
        }

        @Override
        public void fixDimensions() {
            Rectangle labelBox = this.label.getBoundingBox();
            this.setWidth(labelBox.x + labelBox.width + 4);
        }

        @Override
        public void updateSearch(String searchString) {
            super.updateSearch(searchString);
            ItemSearchTester tester = ItemSearchTester.constructSearchTester(searchString);
            this.searchedItems = (Item[])Arrays.stream(this.items).filter(i -> tester.matches(i.getDefaultItem(null, 1), null, new GameBlackboard())).toArray(Item[]::new);
            if (this.items.length != 1) {
                this.label.setLocalization(new LocalMessage("itemcategory", "unknownitems", "count", this.searchedItems.length));
                this.fixDimensions();
            }
            this.setHidden(this.searchedItems.length == 0);
            this.updateButton();
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            ItemCategoriesFilter.ItemLimits nextLimits;
            CheckedState nextState = this.getNextState();
            if (nextState != this.state) {
                this.updateButtonsBack();
            }
            ItemCategoriesFilter.ItemLimits itemLimits = nextLimits = this.items.length == 1 ? ItemCategoriesFilterForm.this.filter.getItemLimits(this.items[0]) : null;
            if (nextLimits != this.lastLimits || nextLimits != null && nextLimits.isSame(this.lastLimits)) {
                this.updateLabel();
            }
            super.draw(tickManager, perspective, renderBox);
        }

        @Override
        public void updateButton() {
            this.state = this.getNextState();
            if (this.toggleButton != null) {
                this.state.updateButton(this.toggleButton);
            }
        }

        public void updateLabel() {
            this.lastLimits = this.items.length == 1 ? ItemCategoriesFilterForm.this.filter.getItemLimits(this.items[0]) : null;
            this.label.setText(this.getLabelText());
            if (this.labelHover != null) {
                this.labelHover.width = this.label.getBoundingBox().width + 20;
            }
            this.updateDimensionsBack();
            this.updateDimensionsForward();
            ItemCategoriesFilterForm.this.updateDimensions();
            WindowManager.getWindow().submitNextMoveEvent();
        }

        public GameMessage getLabelText() {
            if (this.items.length == 1) {
                GameMessage itemName = ItemRegistry.getLocalization(this.items[0].getID());
                if (this.lastLimits == null || this.lastLimits.isDefault()) {
                    return itemName;
                }
                return new LocalMessage("ui", "storagelimitprefix", "count", this.lastLimits.getMaxItems(), "item", itemName);
            }
            return new LocalMessage("itemcategory", "unknownitems", "count", this.items.length);
        }

        protected CheckedState getNextState() {
            int itemsAllowed = 0;
            int itemsDefault = 0;
            for (Item item : this.searchedItems) {
                ItemCategoriesFilter.ItemLimits limits = ItemCategoriesFilterForm.this.filter.getItemLimits(item);
                if (limits == null) continue;
                ++itemsAllowed;
                if (!limits.isDefault()) continue;
                ++itemsDefault;
            }
            if (itemsAllowed == 0) {
                return CheckedState.ESCAPED;
            }
            if (itemsAllowed == this.searchedItems.length && itemsDefault == this.searchedItems.length) {
                return CheckedState.CHECKED;
            }
            return CheckedState.DASH;
        }
    }

    protected class CategoryForm
    extends LinkedForm {
        public final ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter;
        private final LinkedForm childrenForm;
        private final FormContentIconValueButton<CheckedState> toggleButton;
        private CheckedState lastState;
        private int lastMaxItems;
        private final FormContentIconButton expandButton;
        private final FormLocalLabel label;
        private final FormMouseHover labelHover;
        private ItemCategoryExpandedSetting expandedSetting;
        private boolean isExpanded;

        public CategoryForm(LinkedForm parent, final ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter, ItemCategoryExpandedSetting expandedSetting, LinkedForm lastForm) {
            super(itemCategoryFilter.category.stringID, 20, 20, parent, lastForm);
            this.isExpanded = false;
            this.itemCategoryFilter = itemCategoryFilter;
            this.expandedSetting = expandedSetting;
            ItemCategoriesFilterForm.this.categoryForms.put(itemCategoryFilter.category.id, this);
            this.drawBase = false;
            this.childrenForm = this.addComponent(new LinkedForm(this.name + " children", 20, 0, this, null));
            this.children.add(this.childrenForm);
            this.childrenForm.drawBase = false;
            this.childrenForm.setPosition(20, 20);
            this.childrenForm.setHidden(true);
            int currentX = 0;
            if (ItemCategoriesFilterForm.this.mode != Mode.ONLY_CLICK_ITEM) {
                this.toggleButton = this.addComponent(new FormContentIconValueButton(currentX, 0, FormInputSize.SIZE_20, ButtonColor.BASE));
                CheckedState.CHECKED.updateButton(this.toggleButton);
                this.toggleButton.onClicked(e -> {
                    if (this.lastState == CheckedState.CHECKED || this.lastState == CheckedState.DASH) {
                        itemCategoryFilter.setAllowed(false);
                        ItemCategoriesFilterForm.this.onCategoryChanged(itemCategoryFilter, false);
                    } else {
                        itemCategoryFilter.setAllowed(true);
                        ItemCategoriesFilterForm.this.onCategoryChanged(itemCategoryFilter, true);
                    }
                    this.updateButtonsForward();
                    this.updateButtonsBack();
                });
                this.toggleButton.setupDragToOtherButtons("pressItemCategory" + (itemCategoryFilter.parent == null ? "Null" : itemCategoryFilter.parent.category.stringID), true, state -> {
                    if (state == CheckedState.CHECKED || state == CheckedState.ESCAPED) {
                        itemCategoryFilter.setAllowed(state == CheckedState.CHECKED);
                        ItemCategoriesFilterForm.this.onCategoryChanged(itemCategoryFilter, state == CheckedState.CHECKED);
                        this.updateButtonsForward();
                        this.updateButtonsBack();
                        return true;
                    }
                    return false;
                });
                currentX += 20;
            } else {
                this.toggleButton = null;
            }
            this.updateButton();
            this.expandButton = this.addComponent(new FormContentIconButton(currentX, 0, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_collapsed_16, new GameMessage[0]));
            this.expandButton.onClicked(e -> this.setExpanded(!this.isExpanded));
            this.label = this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, (currentX += 20) + 2, 2));
            Rectangle labelBoundingBox = this.label.getBoundingBox();
            if (ItemCategoriesFilterForm.this.mode == Mode.ALLOW_MAX_AMOUNT) {
                this.labelHover = this.addComponent(new FormMouseHover(currentX + 2, 2, labelBoundingBox.width, labelBoundingBox.height){

                    @Override
                    public GameTooltips getTooltips(PlayerMob perspective) {
                        ListGameTooltips tooltips = new ListGameTooltips();
                        tooltips.add(new InputTooltip(-100, Localization.translate("ui", "setstoragelimit")));
                        if (!itemCategoryFilter.isDefault()) {
                            tooltips.add(new InputTooltip(-99, Localization.translate("ui", "clearstoragelimit")));
                        }
                        return tooltips;
                    }

                    @Override
                    public GameWindow.CURSOR getHoveringCursor(PlayerMob perspective) {
                        return GameWindow.CURSOR.INTERACT;
                    }
                }, 1000);
                this.labelHover.acceptRightClicks = true;
                this.labelHover.onClicked(e -> {
                    if (e.event.getID() == -99) {
                        if (!itemCategoryFilter.isDefault()) {
                            itemCategoryFilter.clearMaxItems();
                            ItemCategoriesFilterForm.this.onCategoryLimitsChanged(itemCategoryFilter, itemCategoryFilter.getMaxItems());
                        }
                        return;
                    }
                    Form form = new Form(200, 24);
                    FormTextInput limitInput = form.addComponent(new FormTextInput(0, 0, FormInputSize.SIZE_24, form.getWidth(), 7));
                    limitInput.placeHolder = new LocalMessage("ui", "storagelimit");
                    limitInput.setRegexMatchFull("([0-9]+)?");
                    limitInput.rightClickToClear = true;
                    limitInput.setText(itemCategoryFilter.isDefault() ? "" : "" + itemCategoryFilter.getMaxItems());
                    FormFloatMenu floatMenu = new FormFloatMenu(parent, form);
                    this.getManager().openFloatMenu((FloatMenu)floatMenu, -10, -12);
                    limitInput.onSubmit(e2 -> {
                        try {
                            int next = limitInput.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(limitInput.getText());
                            if (next != itemCategoryFilter.getMaxItems()) {
                                itemCategoryFilter.setMaxItems(next);
                                ItemCategoriesFilterForm.this.onCategoryLimitsChanged(itemCategoryFilter, next);
                            }
                            floatMenu.remove();
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    });
                    limitInput.setTyping(true);
                });
            } else {
                this.labelHover = null;
            }
            this.setWidth(currentX + labelBoundingBox.width + 10);
            ItemCategoriesFilterForm.this.addChildren(this.childrenForm, itemCategoryFilter, this.expandedSetting);
            if (this.expandedSetting != null && this.expandedSetting.isExpanded()) {
                this.setExpanded(true);
            }
            this.setHidden(!this.hasItems);
        }

        @Override
        public void setExpandedSetting(ItemCategoryExpandedSetting setting) {
            ItemCategoryExpandedSetting itemCategoryExpandedSetting = this.expandedSetting = setting == null ? null : setting.getChild(this.itemCategoryFilter.category);
            if (this.expandedSetting != null) {
                this.setExpanded(this.expandedSetting.isExpanded());
                for (LinkedForm child : this.children) {
                    child.setExpandedSetting(this.expandedSetting);
                }
            }
        }

        public void setExpanded(boolean expanded) {
            if (this.isExpanded != expanded) {
                this.isExpanded = expanded;
                if (this.expandedSetting != null) {
                    this.expandedSetting.setExpanded(expanded);
                }
                if (expanded) {
                    this.expandButton.setIcon(this.getInterfaceStyle().button_expanded_16);
                    this.childrenForm.setHidden(false);
                    this.childrenForm.updateDimensionsBack();
                } else {
                    this.expandButton.setIcon(this.getInterfaceStyle().button_collapsed_16);
                    this.childrenForm.setHidden(true);
                    this.childrenForm.updateDimensionsBack();
                }
                this.updateY();
                if (this.parent != null) {
                    this.parent.updateDimensionsBack();
                }
                ItemCategoriesFilterForm.this.updateDimensions();
            }
        }

        @Override
        public void fixDimensions() {
            int childrenWidth = this.children.stream().filter(c -> !c.isHidden()).map(c -> c.getX() + c.getWidth()).max(Comparator.comparingInt(i -> i)).orElse(0);
            this.setWidth(Math.max(childrenWidth, 40 + this.label.getBoundingBox().width + 10));
            this.setHeight(this.children.stream().filter(c -> !c.isHidden()).map(c -> c.getY() + c.getHeight()).max(Comparator.comparingInt(i -> i)).orElse(20));
        }

        @Override
        public void updateSearch(String searchString) {
            super.updateSearch(searchString);
            this.setHidden(!this.hasItems || this.childrenForm.children.stream().allMatch(Form::isHidden));
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            int nextMaxItems;
            CheckedState nextState = this.getNextState();
            if (this.lastState != nextState) {
                if (this.parent != null) {
                    this.parent.updateButtonsBack();
                }
                this.updateButtonsForward();
            }
            if ((nextMaxItems = this.itemCategoryFilter.getMaxItems()) != this.lastMaxItems) {
                this.updateLabel();
            }
            super.draw(tickManager, perspective, renderBox);
        }

        @Override
        public void updateButton() {
            this.lastState = this.getNextState();
            if (this.toggleButton != null) {
                this.lastState.updateButton(this.toggleButton);
            }
        }

        public void updateLabel() {
            this.lastMaxItems = this.itemCategoryFilter.getMaxItems();
            this.label.setText(this.getLabelText());
            if (this.labelHover != null) {
                this.labelHover.width = this.label.getBoundingBox().width;
            }
            this.updateDimensionsBack();
            this.updateDimensionsForward();
            ItemCategoriesFilterForm.this.updateDimensions();
            WindowManager.getWindow().submitNextMoveEvent();
        }

        public GameMessage getLabelText() {
            if (this.itemCategoryFilter.isDefault()) {
                return this.itemCategoryFilter.category.displayName;
            }
            return new LocalMessage("ui", "storagelimitprefix", "count", this.itemCategoryFilter.getMaxItems(), "item", this.itemCategoryFilter.category.displayName);
        }

        protected CheckedState getNextState() {
            if (this.itemCategoryFilter.isAllAllowed()) {
                if (this.itemCategoryFilter.isDefault() && this.itemCategoryFilter.isAllDefault()) {
                    return CheckedState.CHECKED;
                }
                return CheckedState.DASH;
            }
            if (this.itemCategoryFilter.isAnyAllowed()) {
                return CheckedState.DASH;
            }
            return CheckedState.ESCAPED;
        }
    }

    private static enum CheckedState {
        CHECKED(() -> Settings.UI.button_checked_20),
        ESCAPED(() -> Settings.UI.button_escaped_20),
        DASH(() -> Settings.UI.button_dash_20);

        public final Supplier<ButtonIcon> iconSupplier;

        private CheckedState(Supplier<ButtonIcon> iconSupplier) {
            this.iconSupplier = iconSupplier;
        }

        public void updateButton(FormContentIconValueButton<CheckedState> button) {
            button.setCurrent(this, this.iconSupplier.get());
        }
    }
}

