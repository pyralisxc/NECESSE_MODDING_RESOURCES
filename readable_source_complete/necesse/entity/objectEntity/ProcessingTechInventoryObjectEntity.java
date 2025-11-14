/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import necesse.engine.Settings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ProcessingInventoryObjectEntity;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.ProcessingTechRecipeCraftedEvent;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public abstract class ProcessingTechInventoryObjectEntity
extends ProcessingInventoryObjectEntity {
    public Tech[] techs;
    private int expectedCrafts;
    private ArrayList<InventoryItem> expectedResultItems;
    private TechProcessingHelp help = new TechProcessingHelp();

    public ProcessingTechInventoryObjectEntity(Level level, String type, int x, int y, int inputSlots, int outputSlots, Tech ... techs) {
        super(level, type, x, y, inputSlots, outputSlots);
        this.techs = techs;
    }

    @Override
    public boolean isValidInputItem(InventoryItem item) {
        if (item == null) {
            return false;
        }
        return Recipes.streamRecipes(this.techs).anyMatch(r -> {
            for (Ingredient ingredient : r.ingredients) {
                if (!ingredient.matchesItem(item.item)) continue;
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onSlotUpdate(int slot) {
        super.onSlotUpdate(slot);
        this.help.forceUpdate = true;
        this.expectedResultItems = null;
    }

    public FutureCrafts getExpectedResults() {
        if (this.expectedResultItems == null) {
            this.expectedCrafts = 0;
            this.expectedResultItems = new ArrayList();
            Inventory copy = this.inventory.copy();
            for (int j = 0; j < 1000; ++j) {
                boolean success = false;
                for (int i = this.inputSlots - 1; i >= 0; --i) {
                    InventoryRange useRange = new InventoryRange(copy, i, this.inputSlots - 1);
                    for (Recipe recipe : Recipes.getRecipes(this.techs)) {
                        if (!recipe.canCraftRange(this.getLevel(), null, useRange, false).canCraft()) continue;
                        InventoryItem resultItem = recipe.resultItem.copy(recipe.resultAmount);
                        resultItem.combineOrAddToList(this.getLevel(), null, this.expectedResultItems, "add");
                        recipe.craftRange(this.getLevel(), null, useRange);
                        ++this.expectedCrafts;
                        success = true;
                        break;
                    }
                    if (success) break;
                }
                if (!success) break;
            }
        }
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>(this.expectedResultItems.size());
        for (InventoryItem expectedResult : this.expectedResultItems) {
            out.add(expectedResult.copy());
        }
        return new FutureCrafts(this.expectedCrafts, out);
    }

    public FutureCrafts getCurrentAndExpectedResults() {
        FutureCrafts combined = this.getExpectedResults();
        for (int i = this.inputSlots; i < this.inventory.getSize(); ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (item == null) continue;
            item.copy().combineOrAddToList(this.getLevel(), null, combined.items, "add");
        }
        return combined;
    }

    @Override
    public boolean canProcessInput() {
        InventoryRange invRange = new InventoryRange(this.inventory, 0, this.inputSlots - 1);
        return Recipes.streamRecipes(this.techs).anyMatch(r -> r.canCraftRange(this.getLevel(), null, invRange, false).canCraft());
    }

    @Override
    public boolean processInput() {
        for (int i = this.inputSlots - 1; i >= 0; --i) {
            InventoryRange invRange = new InventoryRange(this.inventory, i, this.inputSlots - 1);
            for (Recipe recipe : Recipes.getRecipes(this.techs)) {
                if (!recipe.canCraftRange(this.getLevel(), null, invRange, false).canCraft() || !this.canAddOutput(recipe.resultItem.copy(recipe.resultAmount))) continue;
                ProcessingTechRecipeCraftedEvent event = new ProcessingTechRecipeCraftedEvent(recipe, recipe.craftRange(this.getLevel(), null, invRange), this);
                recipe.submitCraftedEvent(event);
                if (event.resultItem != null) {
                    this.addOutput(event.resultItem);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ProcessingHelp getProcessingHelp() {
        return this.help;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            FutureCrafts results = this.getCurrentAndExpectedResults();
            if (results.crafts > 0 || !results.items.isEmpty()) {
                StringTooltips tooltips = new StringTooltips("Expected results from " + results.crafts + " crafts:");
                for (InventoryItem result : results.items) {
                    tooltips.add("  " + result.getAmount() + "x " + result.getItemDisplayName());
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
            }
        }
    }

    private class TechProcessingHelp
    extends ProcessingHelp {
        public boolean forceUpdate = true;
        public boolean lastShowIngredientsAvailable = Settings.showIngredientsAvailable;
        public Recipe currentRecipe;
        public CanCraft currentRecipeCanCraft;
        public HashSet<Integer> showRecipeTooltip = new HashSet();
        public HashMap<Integer, Ingredient> inputGhostItems = new HashMap();
        public HashMap<Integer, InventoryItem> outputGhostItems = new HashMap();

        private TechProcessingHelp() {
        }

        public void update() {
            int i;
            this.forceUpdate = false;
            this.lastShowIngredientsAvailable = Settings.showIngredientsAvailable;
            this.currentRecipe = null;
            this.currentRecipeCanCraft = null;
            this.showRecipeTooltip.clear();
            this.inputGhostItems.clear();
            this.outputGhostItems.clear();
            InventoryRange inputRange = ProcessingTechInventoryObjectEntity.this.getInputInventoryRange();
            for (i = inputRange.endSlot; i >= inputRange.startSlot; --i) {
                InventoryRange invRange = new InventoryRange(ProcessingTechInventoryObjectEntity.this.inventory, i, inputRange.endSlot);
                for (Recipe recipe : Recipes.getRecipes(ProcessingTechInventoryObjectEntity.this.techs)) {
                    CanCraft canCraft = recipe.canCraftRange(ProcessingTechInventoryObjectEntity.this.getLevel(), null, invRange, ProcessingTechInventoryObjectEntity.this.isClient());
                    if (!canCraft.canCraft()) continue;
                    this.currentRecipe = recipe;
                    this.currentRecipeCanCraft = canCraft;
                    break;
                }
                if (this.currentRecipe != null) break;
            }
            if (this.currentRecipe == null) {
                for (Recipe recipe : Recipes.getRecipes(ProcessingTechInventoryObjectEntity.this.techs)) {
                    CanCraft canCraft = recipe.canCraftRange(ProcessingTechInventoryObjectEntity.this.getLevel(), null, inputRange, ProcessingTechInventoryObjectEntity.this.isClient());
                    if (!canCraft.hasAnyItems()) continue;
                    this.currentRecipe = recipe;
                    this.currentRecipeCanCraft = canCraft;
                    break;
                }
            }
            if (this.currentRecipe != null) {
                InventoryItem item;
                if (!this.currentRecipeCanCraft.hasAnyOfAllItems()) {
                    block3: for (i = 0; i < this.currentRecipe.ingredients.length; ++i) {
                        int j;
                        if (this.currentRecipeCanCraft.hasAnyIngredients(i)) continue;
                        Ingredient ingredient = this.currentRecipe.ingredients[i];
                        boolean found = false;
                        for (j = inputRange.startSlot; j <= inputRange.endSlot; ++j) {
                            item = inputRange.inventory.getItem(j);
                            if (item == null || !ingredient.matchesItem(item.item)) continue;
                            this.inputGhostItems.put(j, ingredient);
                            found = true;
                            break;
                        }
                        if (found) continue;
                        for (j = inputRange.startSlot; j <= inputRange.endSlot; ++j) {
                            if (!inputRange.inventory.isSlotClear(j)) continue;
                            this.inputGhostItems.put(j, ingredient);
                            this.showRecipeTooltip.add(j);
                            continue block3;
                        }
                    }
                }
                if (this.currentRecipe != null) {
                    InventoryItem resultItem = this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount);
                    InventoryRange outputRange = ProcessingTechInventoryObjectEntity.this.getOutputInventoryRange();
                    Inventory inv = ProcessingTechInventoryObjectEntity.this.inventory.copy();
                    inv.addItem(ProcessingTechInventoryObjectEntity.this.getLevel(), null, resultItem, outputRange.startSlot, outputRange.endSlot, false, "add", true, false, null);
                    for (int i2 = outputRange.startSlot; i2 <= outputRange.endSlot; ++i2) {
                        item = inv.getItem(i2);
                        if (item == null || !item.equals(ProcessingTechInventoryObjectEntity.this.getLevel(), this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount), true, false, "equals")) continue;
                        this.outputGhostItems.put(i2, this.currentRecipe.resultItem);
                        this.showRecipeTooltip.add(i2);
                    }
                }
            }
        }

        @Override
        public boolean isProcessing() {
            return ProcessingTechInventoryObjectEntity.this.isProcessing();
        }

        @Override
        public float getProcessingProgress() {
            return ProcessingTechInventoryObjectEntity.this.getProcessingProgress();
        }

        @Override
        public boolean needsFuel() {
            return false;
        }

        @Override
        public InventoryItem getGhostItem(int slot) {
            Item displayItem;
            InventoryItem outputGhost;
            if (this.shouldUpdate()) {
                this.update();
            }
            if ((outputGhost = this.outputGhostItems.get(slot)) != null) {
                return outputGhost;
            }
            Ingredient ingredient = this.inputGhostItems.get(slot);
            if (ingredient != null && (displayItem = ingredient.getDisplayItem()) != null) {
                return displayItem.getDefaultItem(null, 1);
            }
            return null;
        }

        @Override
        public GameTooltips getTooltip(int slot, PlayerMob perspective) {
            if (this.shouldUpdate()) {
                this.update();
            }
            if (this.showRecipeTooltip.contains(slot) && this.currentRecipe != null) {
                return this.currentRecipe.getTooltip(this.currentRecipeCanCraft, perspective, new GameBlackboard());
            }
            return null;
        }

        @Override
        public GameTooltips getCurrentRecipeTooltip(PlayerMob perspective) {
            if (this.shouldUpdate()) {
                this.update();
            }
            if (this.currentRecipe != null) {
                return this.currentRecipe.getTooltip(this.currentRecipeCanCraft, perspective, new GameBlackboard());
            }
            return null;
        }

        public boolean shouldUpdate() {
            if (this.forceUpdate) {
                return true;
            }
            return Settings.showIngredientsAvailable != this.lastShowIngredientsAvailable;
        }
    }

    public static class FutureCrafts {
        public final int crafts;
        public final ArrayList<InventoryItem> items;

        public FutureCrafts(int crafts, ArrayList<InventoryItem> items) {
            this.crafts = crafts;
            this.items = items;
        }
    }
}

