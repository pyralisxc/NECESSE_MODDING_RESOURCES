/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.CraftingGuideContainer;
import necesse.inventory.recipe.Recipes;

public class CraftingGuideContainerForm<T extends CraftingGuideContainer>
extends ContainerForm<T> {
    private FormContainerSlot ingredientSlot;
    private FormIngredientRecipeList ingredientList;
    private int itemID;

    public CraftingGuideContainerForm(Client client, T container) {
        super(client, 400, 160, container);
        InventoryItem guideItem = ((CraftingGuideContainer)container).guideSlot.getItem(((CraftingGuideContainer)container).client.playerMob.getInv());
        this.addComponent(new FormLocalLabel(guideItem == null ? new StaticMessage("NULL") : guideItem.getItemLocalization(), new FontOptions(20), -1, 10, 10));
        this.ingredientSlot = new FormContainerMaterialSlot(client, (Container)container, ((CraftingGuideContainer)container).INGREDIENT_SLOT, this.getWidth() - 60, this.getHeight() - 80);
        this.addComponent(this.ingredientSlot);
        this.ingredientList = new FormIngredientRecipeList(0, this.getHeight() - 120 - 6, this.getWidth() - 80, this.getHeight() - 40, client);
        this.addComponent(this.ingredientList);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int nextItemID;
        InventoryItem item = this.ingredientSlot.getContainerSlot().getItem();
        int n = nextItemID = item == null ? -1 : item.item.getID();
        if (this.itemID != nextItemID) {
            this.itemID = nextItemID;
            if (this.itemID == -1) {
                this.ingredientList.setRecipes(null);
            } else {
                this.ingredientList.setRecipes(Recipes.getRecipesFromResultAndIngredient(nextItemID));
            }
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

