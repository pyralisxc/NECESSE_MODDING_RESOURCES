/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContainerRecipe;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.inventory.Inventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.recipe.CanCraft;

public abstract class FormContainerCraftingListContentBox
extends FormContentBox
implements FormRecipeList {
    protected List<CraftableRecipe> allRecipes;
    protected ArrayList<FormContainerRecipe> recipeComponents = new ArrayList();
    protected Client client;
    protected boolean updateCraftable;
    protected boolean showHidden;
    protected boolean centerRecipes;
    protected boolean centerLastRow;

    public FormContainerCraftingListContentBox(int x, int y, int width, int height, Client client, boolean showHidden, boolean centerRecipes, boolean centerLastRow) {
        super(x, y, width, height);
        this.client = client;
        this.showHidden = showHidden;
        this.centerRecipes = centerRecipes;
        this.centerLastRow = centerLastRow;
    }

    public FormContainerCraftingListContentBox(int x, int y, int width, int height, Client client, boolean showHidden, boolean centerRecipes) {
        this(x, y, width, height, client, showHidden, centerRecipes, centerRecipes);
    }

    @Override
    protected void init() {
        super.init();
        this.updateRecipes();
        GlobalData.craftingLists.add(this);
    }

    public void updateList() {
        this.clearComponents();
        Container container = this.client.getContainer();
        this.recipeComponents = this.allRecipes.stream().filter(cr -> cr.shouldShow).map(r -> new FormContainerRecipe(this.client, container, r.recipe, 0, 0, (CraftableRecipe)r){
            final /* synthetic */ CraftableRecipe val$r;
            {
                this.val$r = craftableRecipe;
                super(client, container, recipe, x, y);
            }

            @Override
            public CanCraft getCanCraft() {
                return this.val$r.canCraft;
            }
        }).collect(Collectors.toCollection(ArrayList::new));
        int padding = 2;
        int fullWidth = this.getWidth();
        int availableWidth = fullWidth - this.getScrollBarWidth();
        boolean hasScrollbar = this.hasScrollbarY();
        int elementWidth = 32 + padding * 2;
        int elementsPerRow = availableWidth / elementWidth;
        int rows = this.recipeComponents.size() / elementsPerRow;
        for (int i = 0; i < this.recipeComponents.size(); ++i) {
            int row = i / elementsPerRow;
            int column = i % elementsPerRow;
            int x = column * elementWidth + padding;
            if (this.centerRecipes) {
                int recipesThisRow = this.centerLastRow ? Math.min(this.recipeComponents.size() - row * elementsPerRow, elementsPerRow) : elementsPerRow;
                int rowWidth = recipesThisRow * elementWidth;
                int width = fullWidth - (hasScrollbar ? 0 : this.getScrollBarWidth());
                x += (width - rowWidth) / 2 + padding * 2;
            }
            FormContainerRecipe comp = this.recipeComponents.get(i);
            this.addComponent(comp);
            comp.setPosition(x, row * elementWidth + padding);
        }
        this.setContentBox(new Rectangle(this.getWidth(), rows * elementWidth));
        WindowManager.getWindow().submitNextMoveEvent();
        this.updateCraftable = false;
    }

    @Override
    public void updateCraftable() {
        this.updateCraftable = true;
    }

    public void forceUpdateCraftable() {
        Container container = this.client.getContainer();
        Collection<Inventory> craftInventories = container.getCraftInventories();
        boolean updateList = false;
        for (CraftableRecipe cr : this.allRecipes) {
            if (!cr.updateCanCraftAndShouldShow(container, craftInventories, this.showHidden)) continue;
            updateList = true;
        }
        if (updateList) {
            this.updateList();
        }
        this.updateCraftable = false;
    }

    @Override
    public void updateRecipes() {
        Container container = this.client.getContainer();
        Collection<Inventory> craftInventories = container.getCraftInventories();
        this.allRecipes = this.streamAllRecipes().map(recipe -> new CraftableRecipe((ContainerRecipe)recipe, container, craftInventories, this.showHidden)).collect(Collectors.toList());
        this.updateList();
    }

    public abstract Stream<ContainerRecipe> streamAllRecipes();

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.updateCraftable) {
            this.forceUpdateCraftable();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        GlobalData.craftingLists.remove(this);
    }

    protected static class CraftableRecipe {
        public ContainerRecipe recipe;
        public CanCraft canCraft;
        public boolean shouldShow;

        public CraftableRecipe(ContainerRecipe recipe, Container container, Collection<Inventory> craftInventories, boolean showHidden) {
            this.recipe = recipe;
            this.updateCanCraftAndShouldShow(container, craftInventories, showHidden);
        }

        public boolean updateCanCraftAndShouldShow(Container container, Collection<Inventory> craftInventories, boolean showHidden) {
            boolean nextShouldShow;
            this.canCraft = container.canCraftRecipe(this.recipe.recipe, craftInventories, true);
            boolean bl = nextShouldShow = showHidden || container.doesShowRecipe(this.recipe.recipe, craftInventories);
            if (nextShouldShow != this.shouldShow) {
                this.shouldShow = nextShouldShow;
                return true;
            }
            return false;
        }
    }
}

