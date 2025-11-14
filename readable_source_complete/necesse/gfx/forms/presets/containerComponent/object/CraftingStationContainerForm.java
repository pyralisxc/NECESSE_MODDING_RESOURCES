/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContainerRecipe;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.RecipeFilter;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.hudManager.HudDrawElement;

public class CraftingStationContainerForm<T extends CraftingStationContainer>
extends ContainerFormSwitcher<T>
implements FormRecipeList {
    public Form craftingForm;
    protected FormContentBox craftingContent;
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    protected FormTextInput searchInput;
    protected FormLocalCheckBox onlyCraftable;
    protected FormLocalCheckBox useNearby;
    protected FormLocalCheckBox highlightNew;
    protected HudDrawElement rangeElement;
    protected List<SubForm> subForms = new ArrayList<SubForm>();
    private final ItemCategoryExpandedSetting expandedSetting;
    protected RecipeFilter filter;
    protected Supplier<Boolean> shouldUpdate;
    protected boolean updateCraftable;
    protected boolean updatePositions;
    protected FormFairTypeButton upgradeButton;
    protected CanCraft upgradeCanCraft;

    public CraftingStationContainerForm(Client client, T container) {
        super(client, container);
        int craftingObjectID = ((CraftingStationContainer)container).craftingStationObject.getCraftingObjectID();
        this.expandedSetting = Settings.getItemCategoryExpandedSetting(craftingObjectID, ItemCategory.craftingMasterCategory, true);
        RecipeFilter filter = Settings.getRecipeFilterSetting(ObjectRegistry.getObject(craftingObjectID));
        this.craftingForm = this.addComponent(new Form(((CraftingStationContainer)container).craftingStationObject.getCraftingFormWidth(), 300));
        FormFairTypeLabel headerLabel = this.craftingForm.addComponent(new FormFairTypeLabel(((CraftingStationContainer)container).header, new FontOptions(20), FairType.TextAlign.LEFT, 5, 5));
        this.craftingContent = this.craftingForm.addComponent(new FormContentBox(0, 32, this.craftingForm.getWidth(), this.craftingForm.getHeight() - 32 - 32 + 4));
        this.onlyCraftable = this.craftingForm.addComponent(new FormLocalCheckBox("ui", "filteronlycraftable", 5, this.craftingForm.getHeight() - 16 - 4, (boolean)Settings.craftingListOnlyCraftable.get()), 100);
        this.onlyCraftable.onClicked(e -> {
            Settings.craftingListOnlyCraftable.set(((FormCheckBox)e.from).checked);
            Settings.saveClientSettings();
        });
        Settings.craftingListOnlyCraftable.addChangeListener(v -> {
            this.onlyCraftable.checked = v;
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.useNearby = this.craftingForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, this.craftingForm.getHeight() - 16 - 4, Settings.craftingUseNearby.get()){

            @Override
            public GameTooltips getTooltip() {
                return new StringTooltips().add(Localization.translate("ui", "usenearbyinvtip"), 400);
            }
        }, 100);
        Settings.craftingUseNearby.addChangeListener(v -> {
            this.useNearby.checked = v;
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.useNearby.onClicked(e -> Settings.craftingUseNearby.set(((FormCheckBox)e.from).checked));
        this.useNearby.setPosition(new FormRelativePosition((FormPositionContainer)this.onlyCraftable, () -> this.onlyCraftable.getBoundingBox().width + 10, () -> 0));
        if (((CraftingStationContainer)container).craftingStationObject.allowHighlightOption() && ((CraftingStationContainer)container).techs.length > 1) {
            LocalMessage highlightBoxMessage = new LocalMessage("ui", "highlighttechrecipes", "tech", ((CraftingStationContainer)container).techs[0].displayName.translate());
            this.highlightNew = this.craftingForm.addComponent(new FormLocalCheckBox((GameMessage)highlightBoxMessage, 5, this.craftingForm.getHeight() - 16 - 4, (boolean)Settings.highlightNewRecipesToWorkstation.get()), 100);
            this.highlightNew.onClicked(e -> {
                Settings.highlightNewRecipesToWorkstation.set(((FormCheckBox)e.from).checked);
                Settings.saveClientSettings();
            });
            Settings.highlightNewRecipesToWorkstation.addChangeListener(v -> {
                this.highlightNew.checked = v;
                this.setHighlightTech(Settings.highlightNewRecipesToWorkstation.get() != false ? container.techs[0] : null);
            }, this::isDisposed);
            this.highlightNew.setPosition(new FormRelativePosition((FormPositionContainer)this.useNearby, () -> this.useNearby.getBoundingBox().width + 10, () -> 0));
        }
        FormFlow iconFlow = new FormFlow(this.craftingForm.getWidth() - 4);
        this.settlementObjectFormManager = ((CraftingStationContainer)container).settlementObjectManager.getFormManager(this, this.craftingForm, client);
        this.settlementObjectFormManager.addConfigButtonRow(this.craftingForm, iconFlow, 4, -1);
        int searchWidth = 150;
        this.searchInput = this.craftingForm.addComponent(new FormTextInput(iconFlow.next() - searchWidth, 4, FormInputSize.SIZE_24, searchWidth, -1, 100));
        this.searchInput.rightClickToClear = true;
        this.searchInput.placeHolder = new LocalMessage("ui", "searchtip");
        this.searchInput.setText(filter.getSearchFilter());
        this.searchInput.onChange(e -> this.setSearch(this.searchInput.getText()));
        iconFlow.next(-searchWidth - 4);
        if (((CraftingStationContainer)container).upgrade != null) {
            int upgradeButtonWidth = 180;
            final GameObject upgradeObject = ((CraftingStationContainer)container).upgrade.upgradeObject;
            final InventoryItem upgradeItem = new InventoryItem(upgradeObject.getObjectItem());
            LocalMessage upgradeMessage = new LocalMessage("ui", "upgradeto", "upgrade", TypeParsers.getItemParseString(upgradeItem));
            this.upgradeButton = this.craftingForm.addComponent(new FormFairTypeButton(upgradeMessage, iconFlow.next() - upgradeButtonWidth, 4, upgradeButtonWidth, FormInputSize.SIZE_24, ButtonColor.GREEN, (CraftingStationContainer)container){
                final /* synthetic */ CraftingStationContainer val$container;
                {
                    this.val$container = craftingStationContainer;
                    super(text, x, y, width, size, color);
                }

                @Override
                protected void addTooltips(PlayerMob perspective) {
                    super.addTooltips(perspective);
                    ListGameTooltips tooltips = new ListGameTooltips();
                    FairType upgradeToType = new FairType();
                    FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();
                    GameColor upgradeToColor = upgradeItem.item.getRarityColor(upgradeItem);
                    upgradeToType.append(fontOptions, Localization.translate("ui", "upgradeto", "upgrade", upgradeToColor.getColorCode() + upgradeObject.getDisplayName()));
                    upgradeToType.applyParsers(TypeParsers.GAME_COLOR);
                    tooltips.add(new FairTypeTooltip(upgradeToType));
                    tooltips.add(new SpacerGameTooltip(10));
                    tooltips.add(Localization.translate("misc", "recipecostsing"));
                    for (int i = 0; i < this.val$container.upgrade.cost.length; ++i) {
                        Ingredient ingredient = this.val$container.upgrade.cost[i];
                        tooltips.add(ingredient.getTooltips(CraftingStationContainerForm.this.upgradeCanCraft == null ? ingredient.getIngredientAmount() : CraftingStationContainerForm.this.upgradeCanCraft.haveIngredients[i], CraftingStationContainerForm.this.upgradeCanCraft != null && CraftingStationContainerForm.this.upgradeCanCraft.countAllIngredients));
                    }
                    GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                }
            });
            this.upgradeButton.setParsers(TypeParsers.ItemIcon(12));
            this.upgradeButton.onClicked(e -> container.upgradeStationAction.runAndSend());
            iconFlow.next(-upgradeButtonWidth - 4);
        }
        headerLabel.setMax(iconFlow.next() - 10, 1, true, true);
        filter.clearCategoryFilters();
        this.setFilter(filter);
        this.makeCurrent(this.craftingForm);
    }

    @Override
    protected void init() {
        super.init();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
        this.rangeElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!CraftingStationContainerForm.this.useNearby.isHovering()) {
                    return;
                }
                final SharedTextureDrawOptions options = ((CraftingStationContainer)((CraftingStationContainerForm)CraftingStationContainerForm.this).container).range.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((CraftingStationContainer)((CraftingStationContainerForm)CraftingStationContainerForm.this).container).objectX, ((CraftingStationContainer)((CraftingStationContainerForm)CraftingStationContainerForm.this).container).objectY, camera);
                if (options != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    });
                }
            }
        };
        this.client.getLevel().hudManager.addElement(this.rangeElement);
        GlobalData.craftingLists.add(this);
        this.updateCraftable = true;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isCurrent(this.craftingForm) && event.state && WindowManager.getWindow().isKeyDown(341) && event.getID() == 70) {
            event.use();
            this.searchInput.setTyping(true);
            this.searchInput.selectAll();
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    public void setFilter(RecipeFilter newFilter) {
        if (this.filter != null) {
            this.filter.removeMonitor(this);
            this.shouldUpdate = null;
        }
        this.filter = newFilter;
        if (this.filter != null) {
            this.setSearch(this.filter.getSearchFilter());
            this.shouldUpdate = this.filter.addMonitor(this);
        }
        this.forceUpdateCraftable();
    }

    public void setSearch(String string) {
        this.filter.setSearchFilter(string);
        for (SubForm subForm : this.subForms) {
            if (string == null || string.isEmpty()) {
                subForm.setExpandedSetting(this.expandedSetting);
                continue;
            }
            subForm.setExpandedSetting(new ItemCategoryExpandedSetting(ItemCategory.craftingMasterCategory, true));
        }
    }

    public void setHighlightTech(Tech tech) {
        for (SubForm subForm : this.subForms) {
            subForm.setHighlightTech(tech);
        }
    }

    @Override
    public void updateCraftable() {
        this.updateCraftable = true;
    }

    @Override
    public void updateRecipes() {
        for (SubForm categoryForm : this.subForms) {
            this.craftingContent.removeComponent(categoryForm);
        }
        this.subForms.clear();
        boolean divideIntoTechs = false;
        if (divideIntoTechs) {
            boolean addHeader = false;
            for (Tech tech : ((CraftingStationContainer)this.container).techs) {
                if (addHeader) {
                    TechHeaderForm form = this.craftingContent.addComponent(new TechHeaderForm(tech.getStringID() + "HeaderForm", this.craftingContent.getWidth() - 8 - this.craftingContent.getScrollBarWidth(), tech, true));
                    form.setX(4);
                    this.subForms.add(form);
                }
                List<ContainerRecipe> allRecipes = ((CraftingStationContainer)this.getContainer()).streamRecipes(tech).filter(r -> !r.isInventory).collect(Collectors.toList());
                this.addRecipes(allRecipes, false);
                addHeader = true;
            }
        } else {
            List<ContainerRecipe> allRecipes = ((CraftingStationContainer)this.getContainer()).streamRecipes(((CraftingStationContainer)this.container).techs).filter(r -> !r.isInventory).collect(Collectors.toList());
            this.addRecipes(allRecipes, false);
        }
        this.forceUpdateCraftable();
        this.setHighlightTech(this.highlightNew != null && this.highlightNew.checked ? ((CraftingStationContainer)this.container).techs[0] : null);
    }

    protected boolean addRecipes(List<ContainerRecipe> recipes, boolean addFirstBreakLine) {
        HashMap<String, ItemCategory> displayNameToCategory = new HashMap<String, ItemCategory>();
        HashMapArrayList<ItemCategory, ContainerRecipe> recipeMap = new HashMapArrayList<ItemCategory, ContainerRecipe>();
        for (ContainerRecipe r : recipes) {
            String displayName;
            ItemCategory displayNameCategory;
            ItemCategory category = r.recipe.getCraftingCategory();
            if (category == null) {
                category = ItemCategory.craftingManager.getItemsCategory(r.recipe.resultItem.item);
                int desiredDepth = ((CraftingStationContainer)this.container).categoryDepth;
                ItemCategory checkCategory = category;
                while (checkCategory != null) {
                    if (((CraftingStationContainer)this.container).forceCategorySolo.contains(checkCategory)) {
                        desiredDepth = checkCategory.depth;
                        break;
                    }
                    checkCategory = checkCategory.parent;
                }
                while (category.depth > desiredDepth) {
                    category = category.parent;
                }
            }
            if ((displayNameCategory = (ItemCategory)displayNameToCategory.get(displayName = category.displayName.translate())) != null) {
                category = displayNameCategory;
            } else {
                displayNameToCategory.put(displayName, category);
            }
            recipeMap.add(category, r);
        }
        ArrayList list = new ArrayList(recipeMap.keySet());
        list.sort(null);
        boolean addBreakLine = addFirstBreakLine;
        for (ItemCategory category : list) {
            CraftCategoryForm form = this.craftingContent.addComponent(new CraftCategoryForm(category.stringID + "CraftingForm", this.craftingContent.getWidth() - 8 - this.craftingContent.getScrollBarWidth(), addBreakLine, category, this.expandedSetting, (ArrayList)recipeMap.get(category)));
            form.setX(4);
            this.subForms.add(form);
            addBreakLine = true;
        }
        return addBreakLine;
    }

    public void forceUpdateCraftable() {
        if (((CraftingStationContainer)this.container).upgrade != null) {
            this.upgradeCanCraft = ((CraftingStationContainer)this.container).canCraftRecipe(((CraftingStationContainer)this.container).upgrade.cost, ((CraftingStationContainer)this.container).getCraftInventories(), true);
            this.upgradeButton.setActive(this.upgradeCanCraft.canCraft());
        } else {
            this.upgradeCanCraft = null;
        }
        for (SubForm subForm : this.subForms) {
            subForm.updateCraftable();
        }
        this.updatePositions = true;
        this.updateCraftable = false;
        WindowManager.getWindow().submitNextMoveEvent();
    }

    public void forceUpdatePositions() {
        FormFlow flow = new FormFlow(0);
        for (SubForm subForm : this.subForms) {
            if (subForm.isHidden()) continue;
            flow.nextY(subForm);
        }
        this.craftingContent.setContentBox(new Rectangle(this.craftingContent.getWidth(), flow.next()));
        this.updatePositions = false;
        WindowManager.getWindow().submitNextMoveEvent();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.shouldUpdate != null && this.shouldUpdate.get().booleanValue()) {
            this.forceUpdateCraftable();
        }
        if (this.updateCraftable) {
            this.forceUpdateCraftable();
        }
        if (this.updatePositions) {
            this.forceUpdatePositions();
        }
        if (this.useNearby.isHovering()) {
            Renderer.hudManager.fadeHUD();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.craftingForm, ((CraftingStationContainer)this.container).craftingStationObject.getCraftingFormXOffset());
        this.settlementObjectFormManager.onWindowResized();
    }

    @Override
    public void dispose() {
        super.dispose();
        GlobalData.craftingLists.remove(this);
        if (this.filter != null) {
            this.filter.removeMonitor(this);
        }
        this.shouldUpdate = null;
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
    }

    protected static abstract class SubForm
    extends Form {
        public SubForm(String name, int width, int height) {
            super(name, width, height);
        }

        public void setExpandedSetting(ItemCategoryExpandedSetting setting) {
        }

        public void setHighlightTech(Tech tech) {
        }

        public void updateCraftable() {
        }

        public abstract void updateDimensions();
    }

    protected static class TechHeaderForm
    extends SubForm {
        public TechHeaderForm(String name, int width, Tech tech, boolean addBreakLine) {
            super(name, width, 28 + (addBreakLine ? 4 : 0));
            this.drawBase = false;
            this.shouldLimitDrawArea = false;
            if (addBreakLine) {
                this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, 1, this.getWidth() - 8, true));
            }
            this.addComponent(new FormLocalLabel(tech.displayName, new FontOptions(20), -1, 5, 4 + (addBreakLine ? 4 : 0)));
        }

        @Override
        public void updateDimensions() {
        }

        @Override
        public List<Rectangle> getHitboxes() {
            if (this.isHidden()) {
                return TechHeaderForm.singleBox(new Rectangle(this.getX(), this.getY(), 0, 0));
            }
            return TechHeaderForm.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
    }

    protected class CraftCategoryForm
    extends SubForm {
        public final ItemCategory category;
        private ItemCategoryExpandedSetting expandedSetting;
        private final FormContentIconButton expandButton;
        private boolean isExpanded;
        protected boolean addBreakLine;
        protected Form contentForm;
        public ArrayList<ContainerRecipe> categoryRecipes;
        public Tech highlightTech;
        protected ArrayList<FormContainerRecipe> recipeComponents;

        public CraftCategoryForm(String name, int width, boolean addBreakLine, ItemCategory category, ItemCategoryExpandedSetting expandedSetting, ArrayList<ContainerRecipe> categoryRecipes) {
            super(name, width, 100);
            this.isExpanded = false;
            this.recipeComponents = new ArrayList();
            this.addBreakLine = addBreakLine;
            this.category = category;
            this.expandedSetting = expandedSetting == null ? null : expandedSetting.getChild(category);
            this.categoryRecipes = categoryRecipes;
            this.drawBase = false;
            this.shouldLimitDrawArea = false;
            if (addBreakLine) {
                this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, 1, this.getWidth() - 8, true));
            }
            this.expandButton = this.addComponent(new FormContentIconButton(2, 2 + (addBreakLine ? 4 : 0), FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_collapsed_16, new GameMessage[0]));
            this.expandButton.onClicked(e -> this.setExpanded(!this.isExpanded, false));
            this.addComponent(new FormLocalLabel(category.displayName, new FontOptions(16), -1, 24, 4 + (addBreakLine ? 4 : 0), width));
            this.contentForm = this.addComponent(new Form(name + "Content", width, 100));
            this.contentForm.setY(24 + (addBreakLine ? 4 : 0));
            this.contentForm.drawBase = false;
            this.setExpanded(this.expandedSetting == null || this.expandedSetting.isExpanded(), true);
        }

        @Override
        public void setExpandedSetting(ItemCategoryExpandedSetting setting) {
            ItemCategoryExpandedSetting itemCategoryExpandedSetting = this.expandedSetting = setting == null ? null : setting.getChild(this.category);
            if (this.expandedSetting != null) {
                this.setExpanded(this.expandedSetting.isExpanded(), false);
            }
        }

        public void setExpanded(boolean expanded, boolean forceUpdate) {
            if (this.isExpanded != expanded || forceUpdate) {
                this.isExpanded = expanded;
                if (this.expandedSetting != null) {
                    this.expandedSetting.setExpanded(expanded);
                }
                if (expanded) {
                    this.expandButton.setIcon(this.getInterfaceStyle().button_expanded_16);
                    this.contentForm.setHidden(false);
                } else {
                    this.expandButton.setIcon(this.getInterfaceStyle().button_collapsed_16);
                    this.contentForm.setHidden(true);
                }
                this.updateDimensions();
                CraftingStationContainerForm.this.updatePositions = true;
            }
        }

        @Override
        public void setHighlightTech(Tech tech) {
            this.highlightTech = tech;
        }

        @Override
        public void updateCraftable() {
            HashSet<Integer> hoveringRecipes = new HashSet<Integer>();
            int recipeControllerFocus = -1;
            for (FormContainerRecipe recipeComponent : this.recipeComponents) {
                if (recipeComponent.isHovering()) {
                    hoveringRecipes.add(recipeComponent.recipe.id);
                }
                if (recipeComponent.isControllerFocus()) {
                    recipeControllerFocus = recipeComponent.recipe.id;
                }
                this.contentForm.removeComponent(recipeComponent);
            }
            this.recipeComponents.clear();
            Collection<Inventory> craftInventories = ((CraftingStationContainer)CraftingStationContainerForm.this.container).getCraftInventories();
            this.recipeComponents = this.categoryRecipes.stream().map(r -> {
                FormContainerRecipe comp = new FormContainerRecipe(CraftingStationContainerForm.this.client, CraftingStationContainerForm.this.container, (ContainerRecipe)r, 0, 24, (ContainerRecipe)r){
                    final /* synthetic */ ContainerRecipe val$r;
                    {
                        this.val$r = containerRecipe;
                        super(client, container, recipe, x, y);
                    }

                    @Override
                    public boolean shouldHighlight() {
                        return this.val$r.recipe.tech == CraftCategoryForm.this.highlightTech;
                    }
                };
                comp.canCraft = ((CraftingStationContainer)CraftingStationContainerForm.this.container).canCraftRecipe(r.recipe, craftInventories, true);
                return comp;
            }).filter(cr -> {
                boolean doesShow = ((CraftingStationContainer)CraftingStationContainerForm.this.getContainer()).doesShowRecipe(cr.recipe.recipe, craftInventories);
                if (!doesShow) {
                    return false;
                }
                if (CraftingStationContainerForm.this.onlyCraftable.checked && !cr.canCraft.canCraft()) {
                    return false;
                }
                return CraftingStationContainerForm.this.filter == null || CraftingStationContainerForm.this.filter.isValid(cr.recipe.recipe, cr.canCraft.canCraft());
            }).collect(Collectors.toCollection(ArrayList::new));
            int padding = 2;
            int availableWidth = this.getWidth();
            int elementWidth = 32 + padding * 2;
            int elementsPerRow = availableWidth / elementWidth;
            int rows = this.recipeComponents.size() / elementsPerRow;
            if (this.recipeComponents.size() % elementsPerRow != 0) {
                ++rows;
            }
            for (int i = 0; i < this.recipeComponents.size(); ++i) {
                int row = i / elementsPerRow;
                int column = i % elementsPerRow;
                FormContainerRecipe comp = this.recipeComponents.get(i);
                if (comp.recipe.id == recipeControllerFocus) {
                    comp.tryPrioritizeControllerFocus();
                }
                if (hoveringRecipes.contains(comp.recipe.id)) {
                    comp.setHovering();
                }
                this.contentForm.addComponent(comp);
                comp.setPosition(column * elementWidth + padding, row * elementWidth + padding);
            }
            this.contentForm.setHeight(rows * elementWidth);
            this.updateDimensions();
        }

        @Override
        public void updateDimensions() {
            this.setHidden(this.isEmpty());
            if (this.contentForm.isHidden()) {
                this.setHeight(24 + (this.addBreakLine ? 4 : 0));
            } else {
                this.setHeight(24 + (this.addBreakLine ? 4 : 0) + this.contentForm.getHeight() + 4);
            }
        }

        @Override
        public boolean isEmpty() {
            return this.recipeComponents.isEmpty();
        }

        @Override
        public List<Rectangle> getHitboxes() {
            if (this.isHidden()) {
                return CraftCategoryForm.singleBox(new Rectangle(this.getX(), this.getY(), 0, 0));
            }
            return CraftCategoryForm.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
    }
}

