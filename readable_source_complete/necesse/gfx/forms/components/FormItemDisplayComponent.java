/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FormItemDisplayComponent
extends FormComponent
implements FormPositionContainer {
    public final InventoryItem item;
    private FormPosition position;
    private boolean hovering;

    public FormItemDisplayComponent(int x, int y, InventoryItem item) {
        this.position = new FormFixedPosition(x, y);
        this.item = item;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.hovering = this.isMouseOver(event);
            if (this.hovering) {
                event.useMove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltip;
        this.item.drawIcon(perspective, this.getX(), this.getY(), 32, this.getItemDrawColor());
        if (this.hovering && (tooltip = this.getTooltip()) != null) {
            GameTooltipManager.addTooltip(tooltip, TooltipLocation.FORM_FOCUS);
        }
    }

    public Color getItemDrawColor() {
        return null;
    }

    public GameTooltips getTooltip() {
        return new StringTooltips(this.item.getItemDisplayName(), this.item.item.getRarityColor(this.item));
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormItemDisplayComponent.singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
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

