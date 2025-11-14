/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.RecipeFilter;

public class CraftFilterForm
extends Form {
    private final RecipeFilter filter;
    private final Supplier<Boolean> hasUpdated;
    private final FormTextInput search;
    private final FormCheckBox craftable;
    private HashMap<String, FormCheckBox> categories = new HashMap();

    public CraftFilterForm(String name, int width, int minHeight, int maxHeight, RecipeFilter filter) {
        super(name, width, minHeight);
        this.filter = filter;
        this.hasUpdated = filter.addMonitor(this);
        this.search = this.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, this.getWidth() - 8, 100));
        this.search.rightClickToClear = true;
        this.search.placeHolder = new LocalMessage("ui", "searchtip");
        this.search.setText(filter.getSearchFilter());
        this.search.onChange(e -> filter.setSearchFilter(this.search.getText()));
        FormContentBox content = this.addComponent(new FormContentBox(0, 32, this.getWidth(), this.getHeight() - 24 - 16));
        FormFlow flow = new FormFlow(5);
        this.craftable = content.addComponent(new FormLocalCheckBox("ui", "filteronlycraftable", 4, flow.next(20), filter.craftableOnly())).onClicked(e -> filter.setCraftableOnly(((FormCheckBox)e.from).checked));
        ItemCategory.masterCategory.streamChildren().sorted().forEach(child -> {
            FormLocalCheckBox childCheckbox = content.addComponent(flow.nextY(new FormLocalCheckBox(child.displayName, 4, flow.next(), filter.containsCategoryFilter(child.stringID), this.getWidth() - 10), 4));
            childCheckbox.onClicked(e -> {
                if (((FormCheckBox)e.from).checked) {
                    filter.addCategoryFilter(child.stringID);
                } else {
                    filter.removeCategoryFilter(child.stringID);
                }
            });
            this.categories.put(child.stringID, childCheckbox);
        });
        int currentHeight = flow.next() + 5 + content.getY();
        this.setHeight(GameMath.limit(currentHeight, minHeight, maxHeight));
        content.setHeight(this.getHeight() - 24 - 8);
        content.setContentBox(new Rectangle(0, 0, this.getWidth(), flow.next() + 5));
    }

    public void updateFilter() {
        if (!this.search.isTyping()) {
            this.search.setText(this.filter.getSearchFilter());
        }
        this.craftable.checked = this.filter.craftableOnly();
        for (Map.Entry<String, FormCheckBox> entry : this.categories.entrySet()) {
            entry.getValue().checked = this.filter.containsCategoryFilter(entry.getKey());
        }
    }

    public void selectAllTextAndSetTypingTrue() {
        this.search.setTyping(true);
        this.search.selectAll();
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden && this.search.isTyping()) {
            this.search.setTyping(false);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.hasUpdated.get().booleanValue()) {
            this.updateFilter();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.filter.removeMonitor(this);
    }
}

