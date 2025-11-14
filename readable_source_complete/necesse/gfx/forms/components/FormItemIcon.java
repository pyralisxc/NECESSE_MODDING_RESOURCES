/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FormItemIcon
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private boolean isHovering;
    public InventoryItem item;
    public boolean showNameAsTooltip;

    public FormItemIcon(int x, int y, InventoryItem item, boolean showNameAsTooltip) {
        this.position = new FormFixedPosition(x, y);
        this.item = item;
        this.showNameAsTooltip = showNameAsTooltip;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
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
        this.item.draw(perspective, this.getX(), this.getY(), false);
        this.addTooltips(perspective);
    }

    public void addTooltips(PlayerMob perspective) {
        if (this.isHovering()) {
            if (this.showNameAsTooltip) {
                GameTooltipManager.addTooltip(new StringTooltips(this.item.getItemDisplayName()), TooltipLocation.FORM_FOCUS);
            } else {
                GameTooltipManager.addTooltip(this.item.getTooltip(perspective, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormItemIcon.singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
    }

    public boolean isHovering() {
        return this.isHovering;
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

