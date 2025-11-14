/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import medievalsim.ui.fixes.InputFocusManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class SearchableDropdown<T> {
    private final InputFocusManager.EnhancedTextInput searchInput;
    private final FormContentBox resultsBox;
    private final List<T> allItems;
    private final List<T> filteredItems;
    private final Function<T, String> displayFunction;
    private final Consumer<T> onSelected;
    private String lastFilter = "";
    private T selectedItem = null;
    private int tickCounter = 0;
    private boolean isDropdownOpen = false;
    private boolean hadFocusLastTick = false;
    private Form parentForm = null;
    private boolean isUpdating = false;

    public SearchableDropdown(int x, int y, int width, int dropdownHeight, String placeholder, List<T> items, Function<T, String> displayFunction, Consumer<T> onSelected) {
        this.allItems = new ArrayList<T>(items);
        this.filteredItems = new ArrayList<T>(items);
        this.displayFunction = displayFunction;
        this.onSelected = onSelected;
        this.searchInput = new InputFocusManager.EnhancedTextInput(x, y, FormInputSize.SIZE_32, width, 100);
        this.searchInput.placeHolder = new StaticMessage(placeholder);
        this.searchInput.onSubmit(e -> this.filterItems(this.searchInput.getText()));
        this.resultsBox = new FormContentBox(x, y + 40, width, dropdownHeight);
        this.buildResultsList();
    }

    public void tick(TickManager tickManager) {
        String currentText;
        ++this.tickCounter;
        if (this.isUpdating) {
            return;
        }
        boolean hasFocus = this.searchInput.isTyping();
        if (hasFocus && !this.hadFocusLastTick) {
            this.openDropdown();
        } else if (!hasFocus && this.hadFocusLastTick) {
            this.closeDropdown();
        }
        this.hadFocusLastTick = hasFocus;
        if (this.tickCounter % 5 == 0 && !(currentText = this.searchInput.getText()).equals(this.lastFilter)) {
            this.lastFilter = currentText;
            this.filterItems(currentText);
        }
    }

    private void openDropdown() {
        if (!this.isDropdownOpen && this.parentForm != null) {
            this.parentForm.addComponent((FormComponent)this.resultsBox, 100);
            this.isDropdownOpen = true;
        }
    }

    private void closeDropdown() {
        if (this.isDropdownOpen && this.parentForm != null) {
            this.parentForm.removeComponent((FormComponent)this.resultsBox);
            this.isDropdownOpen = false;
        }
    }

    private void filterItems(String filter) {
        String filterLower = filter.toLowerCase();
        this.filteredItems.clear();
        if (filter.trim().isEmpty()) {
            this.filteredItems.addAll(this.allItems);
        } else {
            for (T item : this.allItems) {
                String displayText = this.displayFunction.apply(item).toLowerCase();
                if (!displayText.contains(filterLower)) continue;
                this.filteredItems.add(item);
            }
        }
        this.buildResultsList();
    }

    private void buildResultsList() {
        this.resultsBox.clearComponents();
        int yPos = 5;
        int buttonWidth = this.resultsBox.getWidth() - 10;
        for (T item : this.filteredItems) {
            String displayText = this.displayFunction.apply(item);
            FormTextButton itemButton = new FormTextButton(displayText, 5, yPos, buttonWidth, FormInputSize.SIZE_20, ButtonColor.BASE);
            itemButton.onClicked(e -> {
                this.selectedItem = item;
                this.searchInput.setText(displayText);
                this.searchInput.setTyping(false);
                this.hadFocusLastTick = false;
                if (this.onSelected != null) {
                    this.onSelected.accept(item);
                }
                this.closeDropdown();
            });
            this.resultsBox.addComponent((FormComponent)itemButton);
            yPos += 25;
        }
        int contentHeight = Math.max(this.filteredItems.size() * 25 + 10, this.resultsBox.getHeight());
        this.resultsBox.setContentBox(new Rectangle(0, 0, this.resultsBox.getWidth(), contentHeight));
    }

    public void addToForm(Form form) {
        this.parentForm = form;
        form.addComponent((FormComponent)this.searchInput);
    }

    public void removeFromForm(Form form) {
        this.closeDropdown();
        form.removeComponent((FormComponent)this.searchInput);
        this.parentForm = null;
    }

    public InputFocusManager.EnhancedTextInput getSearchInput() {
        return this.searchInput;
    }

    public FormContentBox getResultsBox() {
        return this.resultsBox;
    }

    public T getSelectedItem() {
        return this.selectedItem;
    }

    public String getTypedText() {
        return this.searchInput.getText();
    }

    public boolean hasManualInput() {
        String typed = this.searchInput.getText().trim();
        return !typed.isEmpty() && this.selectedItem == null;
    }

    public void setSelectedItem(T item) {
        this.selectedItem = item;
        if (item != null) {
            this.searchInput.setText(this.displayFunction.apply(item));
        } else {
            this.searchInput.setText("");
        }
    }

    public void reset() {
        this.searchInput.setText("");
        this.lastFilter = "";
        this.filterItems("");
    }

    public void beginUpdate() {
        this.isUpdating = true;
    }

    public void endUpdate() {
        this.isUpdating = false;
    }

    public void updateItems(List<T> newItems) {
        this.allItems.clear();
        this.allItems.addAll(newItems);
        this.searchInput.setText("");
        this.lastFilter = "";
        this.hadFocusLastTick = false;
        this.closeDropdown();
        this.filterItems("");
    }
}

