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
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public abstract class FormDropAtElement
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    protected int height;
    protected boolean isHovering;

    public FormDropAtElement(int x, int y, int width, int height) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
        } else if (event.isMouseClickEvent() && this.isHovering && !event.state) {
            this.onReleasedAt(event);
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
    }

    public abstract void onReleasedAt(InputEvent var1);

    public boolean isHovering() {
        return this.isHovering;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormDropAtElement.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
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
    public boolean shouldUseMouseEvents() {
        return false;
    }
}

