/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.container.mob.NetworkSellingShopItem;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.item.Item;

public class FormSellingShopComponent
extends FormComponent
implements FormPositionContainer,
FormRecipeList {
    private FormPosition position;
    private boolean active;
    protected ShopContainer shopContainer;
    protected NetworkSellingShopItem shopItem;
    private boolean isHovering;
    private boolean canCraft;
    private boolean updateCraftable;

    public FormSellingShopComponent(int x, int y, ShopContainer shopContainer, NetworkSellingShopItem shopItem) {
        this.position = new FormFixedPosition(x, y);
        this.shopContainer = shopContainer;
        this.shopItem = shopItem;
        this.active = true;
    }

    @Override
    protected void init() {
        super.init();
        this.updateCanCraft();
        GlobalData.craftingLists.add(this);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        if (!(this.isActive() && !event.isUsed() && event.state && this.canCraft && this.isMouseOver(event))) {
            return;
        }
        if (event.getID() == -100 || event.isRepeatEvent((Object)this)) {
            event.startRepeatEvents(this);
            int times = 1;
            if (Control.CRAFT_10.isDown()) {
                times = 10;
            } else if (Control.CRAFT_ALL.isDown()) {
                times = 65535;
            }
            this.shopContainer.buyItemAction.runAndSend(this.shopItem, times);
            if (event.shouldSubmitSound()) {
                this.playTickSound();
            }
            this.updateCanCraft();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!(this.isActive() && !event.isUsed() && event.buttonState && this.canCraft && this.isControllerFocus())) {
            return;
        }
        if (event.getState() == ControllerInput.MENU_SELECT || event.isRepeatEvent(this)) {
            event.startRepeatEvents(this);
            int times = 1;
            if (Control.CRAFT_10.isDown()) {
                times = 10;
            } else if (Control.CRAFT_ALL.isDown()) {
                times = 65535;
            }
            this.shopContainer.buyItemAction.runAndSend(this.shopItem, times);
            if (event.shouldSubmitSound()) {
                this.playTickSound();
            }
            this.updateCanCraft();
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void updateCraftable() {
        this.updateCraftable = true;
    }

    @Override
    public void updateRecipes() {
    }

    public void updateCanCraft() {
        this.canCraft = (this.shopItem.maxStock < 0 || this.shopItem.currentStock > 0) && this.shopItem.canAffordCost(this.shopContainer.client, this.shopContainer.getCraftInventories());
        this.updateCraftable = false;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltips;
        if (this.updateCraftable) {
            this.updateCanCraft();
        }
        Color drawCol = this.getDrawColor();
        HoverStateTextures slotTextures = this.getInterfaceStyle().inventoryslot_big;
        GameTexture slotTexture = this.isHovering() ? slotTextures.highlighted : slotTextures.active;
        slotTexture.initDraw().color(drawCol).draw(this.getX(), this.getY());
        this.shopItem.item.copy().draw(perspective, this.getX() + 4, this.getY() + 4, false);
        if (this.shopItem.maxStock >= 0) {
            FontOptions stockOptions = new FontOptions(Item.tipFontOptions).color(GameColor.ITEM_NORMAL.color.get());
            FontManager.bit.drawString(this.getX() + 4, this.getY() + 24, GameUtils.metricNumber(this.shopItem.currentStock), stockOptions);
        }
        if (this.isHovering() && (tooltips = this.getTooltips(perspective)) != null) {
            GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    public GameTooltips getTooltips(PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        int price = this.shopItem.getCurrentPrice();
        if (this.shopItem.maxStock > 0) {
            tooltips.add(Localization.translate("ui", "shopremainingstock", "stock", (Object)this.shopItem.currentStock));
            if (this.shopItem.noStockPrice > this.shopItem.fullStockPrice) {
                tooltips.add(Localization.translate("ui", "shoppriceincrease"));
            }
            tooltips.add(new SpacerGameTooltip(5));
        }
        tooltips.add(Localization.translate("ui", "shopbuytip", "coins", price, "amount", this.shopItem.item.getAmount()));
        tooltips.add(this.shopItem.item.getTooltip(perspective, new GameBlackboard()));
        return tooltips;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormSellingShopComponent.singleBox(new Rectangle(this.getX() + 2, this.getY() + 2, 36, 36));
    }

    public Color getDrawColor() {
        if (!this.isActive() || !this.canCraft) {
            return this.getInterfaceStyle().inactiveElementColor;
        }
        if (this.isHovering()) {
            return this.getInterfaceStyle().highlightElementColor;
        }
        return this.getInterfaceStyle().activeElementColor;
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
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

    @Override
    public void dispose() {
        super.dispose();
        GlobalData.craftingLists.remove(this);
    }
}

