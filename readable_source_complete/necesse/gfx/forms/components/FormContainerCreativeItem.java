/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSpawnCreativeItem;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;

public class FormContainerCreativeItem
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    public final InventoryItem item;
    protected final Client playerClient;
    private boolean isHovering;
    protected boolean isHidden;
    protected boolean isFilteredOut;

    public FormContainerCreativeItem(InventoryItem item, int x, int y, Client playerClient) {
        this.item = item;
        this.playerClient = playerClient;
        this.position = new FormFixedPosition(x, y);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isHidden) {
            return;
        }
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        if (event.isUsed()) {
            return;
        }
        if (!this.isMouseOver(event)) {
            return;
        }
        if (event.state && event.getID() == -100 || event.isRepeatEvent((Object)this.item)) {
            event.startRepeatEvents(this.item);
            int craftAmount = 1;
            boolean toInventory = false;
            if (Control.CRAFT_10.isDown()) {
                craftAmount = this.item.itemStackSize();
            } else if (Control.CRAFT_ALL.isDown()) {
                craftAmount = this.item.itemStackSize();
                toInventory = true;
            }
            PacketSpawnCreativeItem.runAndSendAction(this.playerClient, this.item.copy(craftAmount), toInventory ? PacketSpawnCreativeItem.Destination.Inventory : PacketSpawnCreativeItem.Destination.DragSlot, false, false);
            if (event.shouldSubmitSound()) {
                this.playTickSound();
            }
            event.use();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isHidden) {
            return;
        }
        if (!this.isControllerFocus()) {
            return;
        }
        if (event.buttonState && (event.getState() == ControllerInput.MENU_SELECT || event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) || event.isRepeatEvent(this.item)) {
            event.startRepeatEvents(this.item);
            int craftAmount = 1;
            if (Control.CRAFT_10.isDown()) {
                craftAmount = this.item.itemStackSize();
            } else if (Control.CRAFT_ALL.isDown()) {
                craftAmount = this.item.itemStackSize();
            }
            boolean toInventory = event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || event.getRepeatState() == ControllerInput.MENU_ITEM_ACTIONS_MENU;
            PacketSpawnCreativeItem.runAndSendAction(this.playerClient, this.item.copy(craftAmount), toInventory ? PacketSpawnCreativeItem.Destination.Inventory : PacketSpawnCreativeItem.Destination.DragSlot, false, true);
            if (event.shouldSubmitSound()) {
                this.playTickSound();
            }
            event.use();
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.isHidden) {
            return;
        }
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        if (this.isHidden) {
            return;
        }
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttohand"), ControllerInput.MENU_SELECT);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "crafttoinventory"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isHidden) {
            return;
        }
        boolean hovering = this.isHovering();
        Color color = this.getInterfaceStyle().activeElementColor;
        if (hovering) {
            color = this.getInterfaceStyle().highlightElementColor;
        }
        HoverStateTextures variantTexture = this.getInterfaceStyle().inventoryslot_small;
        GameTexture slotTexture = hovering ? variantTexture.highlighted : variantTexture.active;
        slotTexture.initDraw().color(color).draw(this.getX(), this.getY());
        this.item.draw(perspective, this.getX(), this.getY());
        if (hovering && !WindowManager.getWindow().isKeyDown(-100) && !WindowManager.getWindow().isKeyDown(-99)) {
            ListGameTooltips tooltips = new ListGameTooltips();
            tooltips.add(this.item.getTooltip(perspective, new GameBlackboard()));
            GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (this.isHidden) {
            return Collections.emptyList();
        }
        return FormContainerCreativeItem.singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public void setHovering() {
        this.isHovering = true;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    public boolean isFilteredOut() {
        return this.isFilteredOut;
    }

    public void setFilteredOut(boolean filteredOut) {
        this.isFilteredOut = filteredOut;
    }
}

