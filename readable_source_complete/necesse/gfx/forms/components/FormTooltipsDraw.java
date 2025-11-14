/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class FormTooltipsDraw
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private GameTooltips tooltips;
    private int width;
    private int height;

    public FormTooltipsDraw(int x, int y, ListGameTooltips tooltips) {
        this.position = new FormFixedPosition(x, y);
        this.setTooltips(tooltips);
    }

    public void setTooltips(GameTooltips tooltips) {
        this.tooltips = tooltips;
        if (tooltips != null) {
            this.width = tooltips.getWidth();
            this.height = tooltips.getHeight();
        } else {
            this.width = 0;
            this.height = 0;
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.tooltips != null) {
            this.tooltips.draw(this.getX(), this.getY(), GameColor.DEFAULT_COLOR);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormTooltipsDraw.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
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

