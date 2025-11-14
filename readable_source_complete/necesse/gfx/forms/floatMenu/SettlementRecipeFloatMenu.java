/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormSettlementWorkstationRecipeList;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.inventory.item.ItemSearchTester;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;

public abstract class SettlementRecipeFloatMenu
extends FormFloatMenu {
    public SettlementRecipeFloatMenu(FormComponent parent, int width, int height, PlayerMob perspective, SettlementWorkstationLevelObject workstationObject) {
        super(parent);
        Form form = new Form(width, height);
        FormSettlementWorkstationRecipeList list = form.addComponent(new FormSettlementWorkstationRecipeList(0, 28, form.getWidth(), form.getHeight() - 32, workstationObject){

            @Override
            public void onRecipeClicked(Recipe recipe, PlayerMob perspective) {
                SettlementRecipeFloatMenu.this.onRecipeClicked(recipe, perspective);
            }
        });
        FormTextInput filterInput = form.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, form.getWidth() - 8, 50), 1000);
        filterInput.placeHolder = new LocalMessage("ui", "searchtip");
        filterInput.rightClickToClear = true;
        filterInput.onChange(e -> {
            ItemSearchTester tester = ItemSearchTester.constructSearchTester(((FormTextInput)e.from).getText());
            list.setFilter(r -> tester.matches(r.resultItem, perspective, new GameBlackboard()));
        });
        this.setForm(form);
    }

    public abstract void onRecipeClicked(Recipe var1, PlayerMob var2);
}

