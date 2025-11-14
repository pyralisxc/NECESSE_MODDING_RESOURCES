/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class FormIngredientRecipeList
extends FormGeneralGridList<IngredientRecipe> {
    private Client client;

    public FormIngredientRecipeList(int x, int y, int width, int height, Client client) {
        super(x, y, width, height, 36, 36);
        this.client = client;
        this.setFilter(recipe -> true);
    }

    public void setFilter(Predicate<Recipe> filter) {
        this.setRecipes(Recipes.streamRecipes().filter(filter).collect(Collectors.toList()));
    }

    public void setRecipes(Collection<Recipe> recipes) {
        this.elements = new ArrayList();
        if (recipes != null) {
            this.elements.addAll(recipes.stream().map(x$0 -> new IngredientRecipe((Recipe)x$0)).collect(Collectors.toList()));
        }
        this.limitMaxScroll();
    }

    public void onRecipeClicked(Recipe recipe, InputEvent event) {
    }

    public void addTooltips(Recipe recipe, ListGameTooltips tooltips) {
    }

    @Override
    public GameMessage getEmptyMessage() {
        return new LocalMessage("ui", "insertmat");
    }

    public class IngredientRecipe
    extends FormListGridElement<FormIngredientRecipeList> {
        public final Recipe recipe;

        public IngredientRecipe(Recipe recipe) {
            this.recipe = recipe;
        }

        @Override
        protected void draw(FormIngredientRecipeList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color color = FormIngredientRecipeList.this.getInterfaceStyle().activeElementColor;
            if (this.isMouseOver(parent)) {
                color = FormIngredientRecipeList.this.getInterfaceStyle().highlightElementColor;
                GameWindow window = WindowManager.getWindow();
                if (!window.isKeyDown(-100) && !window.isKeyDown(-99)) {
                    ListGameTooltips tooltips = new ListGameTooltips(this.recipe.getTooltip(perspective, new GameBlackboard()));
                    if (this.recipe.isHidden) {
                        tooltips.add(new LocalMessage("tech", "madeinhidden", "tech", this.recipe.tech.displayName).translate());
                    } else {
                        tooltips.add(new LocalMessage("tech", "madein", "tech", this.recipe.tech.displayName).translate());
                    }
                    FormIngredientRecipeList.this.addTooltips(this.recipe, tooltips);
                    GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                }
            }
            GameTexture slotTexture = this.isMouseOver(parent) ? FormIngredientRecipeList.this.getInterfaceStyle().inventoryslot_small.highlighted : FormIngredientRecipeList.this.getInterfaceStyle().inventoryslot_small.active;
            slotTexture.initDraw().color(color).draw(2, 2);
            this.recipe.draw(2, 2, perspective);
        }

        @Override
        protected void onClick(FormIngredientRecipeList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            FormIngredientRecipeList.this.onRecipeClicked(this.recipe, event);
        }

        @Override
        protected void onControllerEvent(FormIngredientRecipeList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormIngredientRecipeList.this.onRecipeClicked(this.recipe, InputEvent.ControllerButtonEvent(event, tickManager));
            event.use();
        }
    }
}

