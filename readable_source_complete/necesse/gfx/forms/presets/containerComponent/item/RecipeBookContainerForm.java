/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.item.ItemSearchTester;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class RecipeBookContainerForm<T extends RecipeBookContainer>
extends ContainerForm<T> {
    public FormTextInput searchFilter;
    public FormContainerMaterialSlot ingredientSlot;
    public FormIngredientRecipeList ingredientList;
    public int itemID;

    public RecipeBookContainerForm(Client client, T container) {
        super(client, 400, 320, container);
        InventoryItem guideItem = ((RecipeBookContainer)container).guideSlot.getItem(((RecipeBookContainer)container).client.playerMob.getInv());
        this.addComponent(new FormLocalLabel(guideItem == null ? new StaticMessage("NULL") : guideItem.getItemLocalization(), new FontOptions(20), -1, 10, 10));
        this.ingredientSlot = new FormContainerMaterialSlot(client, (Container)container, ((RecipeBookContainer)container).INGREDIENT_SLOT, this.getWidth() - 60, this.getHeight() - 50);
        this.addComponent(this.ingredientSlot);
        this.ingredientList = new FormIngredientRecipeList(6, this.getHeight() - 280 - 6, this.getWidth() - 6, this.getHeight() - 80, client, (RecipeBookContainer)container){
            final /* synthetic */ RecipeBookContainer val$container;
            {
                this.val$container = recipeBookContainer;
                super(x, y, width, height, client);
            }

            @Override
            public void onRecipeClicked(Recipe recipe, InputEvent event) {
                this.val$container.clearingIngredientSlot.runAndSend();
                RecipeBookContainerForm.this.ingredientSlot.ghostItem = recipe.resultItem;
                RecipeBookContainerForm.this.searchFilter.setText("");
                this.playTickSound();
                ControllerInput.submitNextRefreshFocusEvent();
            }

            @Override
            public void addTooltips(Recipe recipe, ListGameTooltips tooltips) {
                super.addTooltips(recipe, tooltips);
                if (Input.lastInputIsController) {
                    tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("controls", "inspecttip")));
                } else {
                    tooltips.add(new InputTooltip(-100, Localization.translate("controls", "inspecttip")));
                }
            }
        };
        this.addComponent(this.ingredientList);
        this.searchFilter = this.addComponent(new FormTextInput(26, this.getHeight() - 50, FormInputSize.SIZE_32_TO_40, this.getWidth() - 106, this.getHeight() - 20));
        this.searchFilter.placeHolder = new LocalMessage("ui", "searchtip");
        this.searchFilter.rightClickToClear = true;
        this.searchFilter.onChange(event -> this.updateFilter());
        this.updateFilter();
    }

    public void updateFilter() {
        Stream<Recipe> recipeStream = this.itemID == -1 ? Recipes.streamRecipes() : Recipes.getRecipesFromResultAndIngredient(this.itemID).stream();
        String searchText = this.searchFilter.getText();
        ItemSearchTester tester = ItemSearchTester.constructSearchTester(searchText);
        recipeStream = recipeStream.filter(recipe -> {
            if (tester.matches(recipe.resultItem, this.client.getPlayer(), new GameBlackboard())) {
                return true;
            }
            if (recipe.tech.displayName.translate().toLowerCase().contains(searchText)) {
                return true;
            }
            for (Ingredient ingredient : recipe.ingredients) {
                if (!ingredient.matchesSearch(this.client.getPlayer(), tester)) continue;
                return true;
            }
            return false;
        });
        this.ingredientList.setRecipes(recipeStream.collect(Collectors.toList()));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int nextItemID;
        InventoryItem item = this.ingredientSlot.ghostItem != null ? this.ingredientSlot.ghostItem : this.ingredientSlot.getContainerSlot().getItem();
        int n = nextItemID = item == null ? -1 : item.item.getID();
        if (this.itemID != nextItemID) {
            this.itemID = nextItemID;
            this.updateFilter();
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

