/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.util.GameBlackboard;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemSearchTester;
import necesse.inventory.recipe.Recipe;

public class RecipeFilter {
    private final LinkedList<FilterMonitor> monitors = new LinkedList();
    private final HashSet<String> categoryFilters = new HashSet();
    private boolean craftableOnly;
    private ItemSearchTester searchFilter = ItemSearchTester.constructSearchTester("");

    public Supplier<Boolean> addMonitor(Object obj) {
        Objects.requireNonNull(obj);
        FilterMonitor e = new FilterMonitor(obj);
        this.monitors.add(e);
        return () -> {
            boolean out = e.hasUpdated;
            e.hasUpdated = false;
            return out;
        };
    }

    public void removeMonitor(Object obj) {
        this.monitors.removeIf(e -> e.list.equals(obj));
    }

    public void addCategoryFilter(String categoryStringID) {
        if (this.categoryFilters.add(categoryStringID)) {
            this.updateAll();
        }
    }

    public void removeCategoryFilter(String categoryStringID) {
        if (this.categoryFilters.remove(categoryStringID)) {
            this.updateAll();
        }
    }

    public boolean containsCategoryFilter(String categoryStringID) {
        return this.categoryFilters.contains(categoryStringID);
    }

    public void clearCategoryFilters() {
        if (!this.categoryFilters.isEmpty()) {
            this.categoryFilters.clear();
            this.updateAll();
        }
    }

    public void setCraftableOnly(boolean value) {
        if (this.craftableOnly != value) {
            this.craftableOnly = value;
            this.updateAll();
        }
    }

    public boolean craftableOnly() {
        return this.craftableOnly;
    }

    public void setSearchFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        if (!filter.equals(this.searchFilter.string)) {
            this.searchFilter = ItemSearchTester.constructSearchTester(filter);
            this.updateAll();
        }
    }

    public String getSearchFilter() {
        return this.searchFilter.string;
    }

    public boolean isValid(Recipe recipe, boolean canCraft) {
        if (!this.categoryFilters.isEmpty() && this.categoryFilters.stream().noneMatch(cat -> ItemCategory.getItemsCategory(recipe.resultItem.item).isOrHasParent((String)cat))) {
            return false;
        }
        if (this.craftableOnly && !canCraft) {
            return false;
        }
        return this.searchFilter.matches(recipe.resultItem, null, new GameBlackboard());
    }

    public List<ContainerRecipe> getFilteredRecipes(List<ContainerRecipe> unfiltered, Container container) {
        return unfiltered.stream().filter(cr -> {
            if (!this.categoryFilters.isEmpty() && this.categoryFilters.stream().noneMatch(cat -> ItemCategory.getItemsCategory(cr.recipe.resultItem.item).isOrHasParent((String)cat))) {
                return false;
            }
            if (container != null && !container.doesShowRecipe(cr.recipe, container.getCraftInventories())) {
                return false;
            }
            if (this.craftableOnly && container != null && !container.canCraftRecipe(cr.recipe, container.getCraftInventories(), false).canCraft()) {
                return false;
            }
            return this.searchFilter.matches(cr.recipe.resultItem, null, new GameBlackboard());
        }).collect(Collectors.toList());
    }

    private void updateAll() {
        this.monitors.forEach(e -> ((FilterMonitor)e).hasUpdated = true);
    }

    private static class FilterMonitor {
        public final Object list;
        private boolean hasUpdated = true;

        public FilterMonitor(Object list) {
            this.list = list;
        }
    }
}

