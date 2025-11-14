/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCraftAction;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.Inventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.recipe.CanCraft;

public class FormContainerRecipe
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private boolean active = true;
    protected Client client;
    protected Container container;
    public final ContainerRecipe recipe;
    public CanCraft canCraft;
    public boolean showRecipeOnUsableError = true;
    private boolean isHovering;

    public FormContainerRecipe(Client client, Container container, ContainerRecipe recipe, int x, int y) {
        this.client = client;
        this.container = container;
        this.recipe = recipe;
        this.canCraft = new CanCraft(recipe.recipe, true);
        this.position = new FormFixedPosition(x, y);
    }

    public boolean shouldHighlight() {
        return false;
    }

    public GameTooltips getHighlightTooltip() {
        return null;
    }

    public GameMessage getUsableError() {
        return null;
    }

    public void updateCanCraft(Collection<Inventory> invList) {
        this.canCraft = this.container.canCraftRecipe(this.recipe.recipe, invList == null ? this.container.getCraftInventories() : invList, true);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        if (event.isUsed()) {
            return;
        }
        if (!this.isActive()) {
            return;
        }
        if (!this.isMouseOver(event)) {
            return;
        }
        if (event.state && event.getID() == -100 || event.isRepeatEvent((Object)this.recipe)) {
            GameMessage usableError = this.getUsableError();
            if (usableError != null) {
                return;
            }
            event.startRepeatEvents(this.recipe);
            int craftAmount = 1;
            boolean toInventory = false;
            if (Control.CRAFT_10.isDown()) {
                craftAmount = 10;
            } else if (Control.CRAFT_ALL.isDown()) {
                craftAmount = this.recipe.recipe.resultItem.itemStackSize() / this.recipe.recipe.resultAmount;
                toInventory = true;
            }
            CanCraft canCraft = this.getCanCraft();
            if (canCraft == null) {
                canCraft = this.container.canCraftRecipe(this.recipe.recipe, this.container.getCraftInventories(), false);
            }
            if (canCraft.canCraft()) {
                int hash = this.recipe.recipe.getRecipeHash();
                int actionResult = this.container.applyCraftingAction(this.recipe.id, hash, craftAmount, toInventory);
                this.client.network.sendPacket(new PacketCraftAction(this.recipe.id, hash, craftAmount, actionResult, toInventory));
                if (actionResult > 0 && event.shouldSubmitSound()) {
                    this.playTickSound();
                }
                GlobalData.updateCraftable();
            }
            event.use();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!this.isActive()) {
            return;
        }
        if (!this.isControllerFocus()) {
            return;
        }
        if (event.buttonState && (event.getState() == ControllerInput.MENU_SELECT || event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) || event.isRepeatEvent(this.recipe)) {
            GameMessage usableError = this.getUsableError();
            if (usableError != null) {
                return;
            }
            event.startRepeatEvents(this.recipe);
            int craftAmount = 1;
            if (Control.CRAFT_10.isDown()) {
                craftAmount = 10;
            } else if (Control.CRAFT_ALL.isDown()) {
                craftAmount = this.recipe.recipe.resultItem.itemStackSize() / this.recipe.recipe.resultAmount;
            }
            CanCraft canCraft = this.getCanCraft();
            if (canCraft == null) {
                canCraft = this.container.canCraftRecipe(this.recipe.recipe, this.container.getCraftInventories(), false);
            }
            if (canCraft.canCraft()) {
                int hash = this.recipe.recipe.getRecipeHash();
                boolean toInventory = event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || event.getRepeatState() == ControllerInput.MENU_ITEM_ACTIONS_MENU;
                int actionResult = this.container.applyCraftingAction(this.recipe.id, hash, craftAmount, toInventory);
                this.client.network.sendPacket(new PacketCraftAction(this.recipe.id, hash, craftAmount, actionResult, toInventory));
                if (actionResult > 0 && event.shouldSubmitSound()) {
                    this.playTickSound();
                }
                GlobalData.updateCraftable();
            }
            event.use();
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttohand"), ControllerInput.MENU_SELECT);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttoinventory"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        boolean hovering = this.isHovering();
        GameMessage usableError = this.getUsableError();
        Color color = this.getInterfaceStyle().activeElementColor;
        if (hovering) {
            color = this.getInterfaceStyle().highlightElementColor;
        }
        CanCraft canCraft = this.getCanCraft();
        if (usableError != null || !this.isActive()) {
            color = this.getInterfaceStyle().deadElementColor;
        } else if (!canCraft.canCraft()) {
            color = this.getInterfaceStyle().inactiveElementColor;
        }
        boolean shouldHighlight = this.shouldHighlight();
        HoverStateTextures variantTexture = shouldHighlight ? this.getInterfaceStyle().inventoryslot_small_note : this.getInterfaceStyle().inventoryslot_small;
        GameTexture slotTexture = hovering ? variantTexture.highlighted : variantTexture.active;
        slotTexture.initDraw().color(color).draw(this.getX(), this.getY());
        this.recipe.recipe.draw(this.getX(), this.getY(), perspective, canCraft.canCraft());
        if (hovering && !WindowManager.getWindow().isKeyDown(-100) && !WindowManager.getWindow().isKeyDown(-99)) {
            GameTooltips highlightTooltip;
            ListGameTooltips tooltips = new ListGameTooltips();
            if (usableError != null || this.showRecipeOnUsableError) {
                tooltips.add(this.recipe.recipe.getTooltip(canCraft, perspective, new GameBlackboard()));
            }
            if (usableError != null) {
                if (this.showRecipeOnUsableError) {
                    tooltips.add(new SpacerGameTooltip(4));
                }
                tooltips.add(new StringTooltips(usableError.translate(), GameColor.RED));
            }
            if (shouldHighlight && (highlightTooltip = this.getHighlightTooltip()) != null) {
                tooltips.add(highlightTooltip);
            }
            GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    public CanCraft getCanCraft() {
        return this.canCraft;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormContainerRecipe.singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public void setHovering() {
        this.isHovering = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

