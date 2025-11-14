/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCraftAction;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeFilter;
import necesse.inventory.recipe.Tech;

public class FormContainerCraftingList
extends FormGeneralGridList<CraftableRecipe>
implements FormRecipeList {
    protected Client client;
    protected List<CraftableRecipe> allRecipes;
    protected Tech[] techs;
    protected RecipeFilter filter;
    protected Supplier<Boolean> shouldUpdate;
    protected boolean isInventoryCrafting;
    protected boolean onlyCraftable;
    protected boolean showHidden;
    private boolean updateCraftable;
    public GameMessage usableError;
    public boolean showRecipeOnUsableError = true;
    public boolean shouldDrawScrollButtons = true;

    public FormContainerCraftingList(int x, int y, int width, int height, Client client, boolean showHidden, boolean isInventoryCrafting, Tech ... techs) {
        super(x, y, width, height, 36, 36);
        this.client = client;
        this.techs = techs;
        this.isInventoryCrafting = isInventoryCrafting;
        this.showHidden = showHidden;
        this.acceptMouseRepeatEvents = true;
    }

    @Override
    protected void init() {
        super.init();
        this.updateRecipes();
        GlobalData.craftingLists.add(this);
    }

    public void setFilter(RecipeFilter newFilter) {
        if (this.filter != null) {
            this.filter.removeMonitor(this);
            this.shouldUpdate = null;
        }
        this.filter = newFilter;
        if (this.filter != null) {
            this.shouldUpdate = this.filter.addMonitor(this);
        }
        if (this.allRecipes != null) {
            this.updateList();
        }
    }

    public void setOnlyCraftable(boolean onlyCraftable) {
        this.onlyCraftable = onlyCraftable;
        if (this.allRecipes != null) {
            this.updateList();
        }
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
        if (this.allRecipes != null) {
            this.updateList();
        }
    }

    protected void updateList() {
        Container container = this.getContainer();
        Collection<Inventory> craftInventories = container.getCraftInventories();
        this.allRecipes.forEach(cr -> {
            ((CraftableRecipe)cr).canCraft = container.canCraftRecipe(((CraftableRecipe)cr).recipe, craftInventories, true);
            ((CraftableRecipe)cr).doesShow = container.doesShowRecipe(((CraftableRecipe)cr).recipe, craftInventories);
        });
        this.elements.clear();
        boolean sendMoveEvent = false;
        for (CraftableRecipe cr2 : this.allRecipes) {
            if (!this.showHidden && !cr2.doesShow || this.onlyCraftable && !cr2.canCraft.canCraft() || this.filter != null && !this.filter.isValid(cr2.recipe, cr2.canCraft.canCraft())) {
                cr2.isCurrentlyShown = false;
                cr2.setMoveEvent(null);
                continue;
            }
            if (!cr2.isCurrentlyShown) {
                sendMoveEvent = true;
                cr2.isCurrentlyShown = true;
            }
            this.elements.add(cr2);
        }
        this.updateCraftable = false;
        this.limitMaxScroll();
        if (sendMoveEvent) {
            WindowManager.getWindow().submitNextMoveEvent();
        }
    }

    @Override
    public void updateCraftable() {
        this.updateCraftable = true;
    }

    @Override
    public void updateRecipes() {
        this.allRecipes = this.getContainer().streamRecipes(this.techs).filter(r -> this.isInventoryCrafting == r.isInventory).map(r -> new CraftableRecipe((ContainerRecipe)r, new CanCraft(r.recipe, true))).collect(Collectors.toList());
        this.updateCraftable();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.shouldUpdate != null && this.shouldUpdate.get().booleanValue()) {
            this.updateList();
        }
        if (this.updateCraftable) {
            this.updateList();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    protected void drawEmptyMessage(TickManager tickManager) {
        if (this.allRecipes.size() == 0) {
            return;
        }
        super.drawEmptyMessage(tickManager);
    }

    @Override
    public GameMessage getEmptyMessage() {
        return new LocalMessage("ui", "changefilters");
    }

    @Override
    public FontOptions getEmptyMessageFontOptions() {
        return new FontOptions(16);
    }

    public Container getContainer() {
        return this.client.getContainer();
    }

    @Override
    public void dispose() {
        super.dispose();
        GlobalData.craftingLists.remove(this);
        if (this.filter != null) {
            this.filter.removeMonitor(this);
        }
        this.shouldUpdate = null;
    }

    @Override
    protected void drawScrollButtons(TickManager tickManager) {
        if (this.shouldDrawScrollButtons) {
            super.drawScrollButtons(tickManager);
        }
    }

    protected class CraftableRecipe
    extends FormListGridElement<FormContainerCraftingList> {
        private final Recipe recipe;
        private final int recipeID;
        public boolean isCurrentlyShown;
        private CanCraft canCraft;
        private boolean doesShow;

        public CraftableRecipe(ContainerRecipe recipe, CanCraft canCraft) {
            this.recipe = recipe.recipe;
            this.recipeID = recipe.id;
            this.isCurrentlyShown = false;
            this.canCraft = canCraft;
        }

        @Override
        protected void draw(FormContainerCraftingList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            boolean mouseOver = this.isMouseOver(parent);
            Color color = FormContainerCraftingList.this.getInterfaceStyle().activeElementColor;
            if (mouseOver) {
                color = FormContainerCraftingList.this.getInterfaceStyle().highlightElementColor;
            }
            if (FormContainerCraftingList.this.usableError != null) {
                color = FormContainerCraftingList.this.getInterfaceStyle().deadElementColor;
            } else if (!this.canCraft.canCraft()) {
                color = FormContainerCraftingList.this.getInterfaceStyle().inactiveElementColor;
            }
            GameTexture slotTexture = mouseOver ? FormContainerCraftingList.this.getInterfaceStyle().inventoryslot_small.highlighted : FormContainerCraftingList.this.getInterfaceStyle().inventoryslot_small.active;
            slotTexture.initDraw().color(color).draw(2, 2);
            this.recipe.draw(2, 2, perspective);
            if (mouseOver && !WindowManager.getWindow().isKeyDown(-100) && !WindowManager.getWindow().isKeyDown(-99)) {
                ListGameTooltips tooltips = new ListGameTooltips();
                if (FormContainerCraftingList.this.usableError != null || FormContainerCraftingList.this.showRecipeOnUsableError) {
                    tooltips.add(this.recipe.getTooltip(this.canCraft, perspective, new GameBlackboard()));
                }
                if (FormContainerCraftingList.this.usableError != null) {
                    if (FormContainerCraftingList.this.showRecipeOnUsableError) {
                        tooltips.add(new SpacerGameTooltip(4));
                    }
                    tooltips.add(new StringTooltips(FormContainerCraftingList.this.usableError.translate(), GameColor.RED));
                }
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormContainerCraftingList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (FormContainerCraftingList.this.usableError != null) {
                return;
            }
            if (event.getID() == -100 || event.isRepeatEvent((Object)this.recipe)) {
                event.startRepeatEvents(this.recipe);
                int craftAmount = 1;
                boolean toInventory = false;
                if (Control.CRAFT_10.isDown()) {
                    craftAmount = 10;
                    toInventory = true;
                } else if (Control.CRAFT_ALL.isDown()) {
                    craftAmount = this.recipe.resultItem.itemStackSize() / this.recipe.resultAmount;
                    toInventory = true;
                }
                if (FormContainerCraftingList.this.getContainer().canCraftRecipe(this.recipe, FormContainerCraftingList.this.getContainer().getCraftInventories(), false).canCraft()) {
                    int hash = this.recipe.getRecipeHash();
                    int actionResult = FormContainerCraftingList.this.getContainer().applyCraftingAction(this.recipeID, hash, craftAmount, toInventory);
                    FormContainerCraftingList.this.client.network.sendPacket(new PacketCraftAction(this.recipeID, hash, craftAmount, actionResult, toInventory));
                    if (actionResult > 0 && event.shouldSubmitSound()) {
                        FormContainerCraftingList.this.playTickSound();
                    }
                    GlobalData.updateCraftable();
                }
            }
        }

        @Override
        protected void onControllerEvent(FormContainerCraftingList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (FormContainerCraftingList.this.usableError != null) {
                return;
            }
            if (event.getState() == ControllerInput.MENU_SELECT || event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || event.isRepeatEvent(this.recipe)) {
                event.startRepeatEvents(this.recipe);
                int craftAmount = 1;
                if (Control.CRAFT_10.isDown()) {
                    craftAmount = 10;
                } else if (Control.CRAFT_ALL.isDown()) {
                    craftAmount = this.recipe.resultItem.itemStackSize() / this.recipe.resultAmount;
                }
                if (FormContainerCraftingList.this.getContainer().canCraftRecipe(this.recipe, FormContainerCraftingList.this.getContainer().getCraftInventories(), false).canCraft()) {
                    int hash = this.recipe.getRecipeHash();
                    boolean toInventory = event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || event.getRepeatState() == ControllerInput.MENU_ITEM_ACTIONS_MENU;
                    int actionResult = FormContainerCraftingList.this.getContainer().applyCraftingAction(this.recipeID, hash, craftAmount, toInventory);
                    FormContainerCraftingList.this.client.network.sendPacket(new PacketCraftAction(this.recipeID, hash, craftAmount, actionResult, toInventory));
                    if (actionResult > 0 && event.shouldSubmitSound()) {
                        FormContainerCraftingList.this.playTickSound();
                    }
                    GlobalData.updateCraftable();
                }
            }
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttohand"), ControllerInput.MENU_SELECT);
            GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttoinventory"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
        }
    }
}

