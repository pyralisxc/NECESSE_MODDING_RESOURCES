/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;

public class FormFloatMenu
extends FloatMenu {
    private Form form;

    public FormFloatMenu(FormComponent parent, Form form) {
        super(parent);
        this.setForm(form);
    }

    public FormFloatMenu(FormComponent parent) {
        this(parent, null);
    }

    public void setForm(Form form, int xOffset, int yOffset) {
        if (form == null) {
            form = new Form(50, 50);
        }
        if (this.form != form) {
            if (this.form != null) {
                this.form.dispose();
            }
            this.form = form;
            form.setPosition(new FormFloatPosition(xOffset, yOffset));
            form.setManager(this.parent.getManager());
            form.inheritStyle(this.parent);
        }
    }

    public void setForm(Form form) {
        this.setForm(form, 0, 0);
    }

    @Override
    public int getDrawX() {
        return Math.min(WindowManager.getWindow().getHudWidth() - this.form.getWidth() - 4, super.getDrawX());
    }

    @Override
    public int getDrawY() {
        return Math.min(WindowManager.getWindow().getHudHeight() - this.form.getHeight() - 4, super.getDrawY());
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.wasMouseClickEvent() && event.state) {
            this.remove();
        }
        this.form.handleInputEvent(event, tickManager, perspective);
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
        this.form.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        this.form.addNextControllerFocus(list, 0, 0, customNavigationHandler, area, draw);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        this.form.draw(tickManager, perspective, null);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return this.form.isMouseOver(event);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.form.dispose();
    }

    private class FormFloatPosition
    extends FormFixedPosition {
        public FormFloatPosition(int x, int y) {
            super(x, y);
        }

        @Override
        public int getX() {
            return FormFloatMenu.this.getDrawX() + super.getX();
        }

        @Override
        public int getY() {
            return FormFloatMenu.this.getDrawY() + super.getY();
        }
    }
}

