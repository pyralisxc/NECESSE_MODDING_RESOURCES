/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FloatMenu;

public class ContinueComponentFloatMenu
extends FloatMenu {
    private final FormComponent formComponent;

    public ContinueComponentFloatMenu(FormComponent parent, ContinueComponent component) {
        super(parent);
        this.formComponent = (FormComponent)((Object)component);
        this.formComponent.setManager(parent.getManager());
        this.formComponent.inheritStyle(parent);
        component.onContinue(() -> {
            this.remove();
            ControllerInput.submitNextRefreshFocusEvent();
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.wasMouseClickEvent() && event.state) {
            this.remove();
        }
        this.formComponent.handleInputEvent(event, tickManager, perspective);
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
        this.formComponent.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        this.formComponent.addNextControllerFocus(list, 0, 0, customNavigationHandler, area, draw);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        this.formComponent.draw(tickManager, perspective, null);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return this.formComponent.isMouseOver(event);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.formComponent.dispose();
    }
}

