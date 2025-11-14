/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;

public class ComponentFloatMenu
extends FloatMenu {
    private FormComponent component;

    public ComponentFloatMenu(FormComponent parent, FormComponent component) {
        super(parent);
        this.setComponent(component);
    }

    public ComponentFloatMenu(FormComponent parent) {
        this(parent, null);
    }

    public void setComponent(FormComponent component) {
        if (this.component != component) {
            if (this.component != null) {
                this.component.dispose();
            }
            this.component = component;
            component.setManager(this.parent.getManager());
            component.inheritStyle(this.parent);
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.wasMouseClickEvent() && event.state) {
            this.remove();
        }
        this.component.handleInputEvent(event, tickManager, perspective);
        if (event.isMouseClickEvent()) {
            if (this.isMouseOver(event)) {
                event.use();
            } else if (event.state) {
                event.use();
                this.remove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        this.component.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        this.component.addNextControllerFocus(list, 0, 0, customNavigationHandler, area, draw);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        this.component.draw(tickManager, perspective, null);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return this.component.isMouseOver(event);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.component.dispose();
    }

    private class FormFloatPosition
    extends FormFixedPosition {
        public FormFloatPosition(int x, int y) {
            super(x, y);
        }

        @Override
        public int getX() {
            return ComponentFloatMenu.this.getDrawX() + super.getX();
        }

        @Override
        public int getY() {
            return ComponentFloatMenu.this.getDrawY() + super.getY();
        }
    }
}

