/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormMouseHover
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    public int width;
    public int height;

    public FormMouseHover(int x, int y, int width, int height, boolean useMoveEvents) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.useHoverMoveEvents = useMoveEvents;
    }

    public FormMouseHover(int x, int y, int width, int height) {
        this(x, y, width, height, false);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isHovering()) {
            GameWindow.CURSOR hoveringCursor;
            GameTooltips tooltips = this.getTooltips(perspective);
            if (tooltips != null) {
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
            if ((hoveringCursor = this.getHoveringCursor(perspective)) != null) {
                Renderer.setCursor(hoveringCursor);
            }
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.clickedEvents.hasListeners()) {
            super.addNextControllerFocus(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
        }
    }

    public GameTooltips getTooltips(PlayerMob perspective) {
        return null;
    }

    public GameWindow.CURSOR getHoveringCursor(PlayerMob perspective) {
        return null;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormMouseHover.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
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

