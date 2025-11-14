/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public class FormComponentListTyped<T extends FormComponent>
extends FormComponent
implements ComponentListContainer<T> {
    protected boolean isHidden;
    private final ComponentList<T> components = new ComponentList<T>(this){

        @Override
        public InputEvent offsetEvent(InputEvent event, boolean allowOutside) {
            return event;
        }

        @Override
        public FormManager getManager() {
            return FormComponentListTyped.this.getManager();
        }
    };

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!this.isHidden()) {
            this.components.submitInputEvent(event, tickManager, perspective);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!this.isHidden()) {
            this.components.submitControllerEvent(event, tickManager, perspective);
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (!this.isHidden()) {
            if (draw) {
                Renderer.drawShape(area, false, 0.0f, 1.0f, 1.0f, 1.0f);
            }
            this.components.addNextControllerComponents(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.components.init();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (!this.isHidden()) {
            this.components.drawComponents(tickManager, perspective, renderBox);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (!this.isHidden()) {
            return this.components.stream().flatMap(t -> t.getHitboxes().stream()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return this.components.isMouseOver(event);
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    @Override
    public ComponentList<T> getComponentList() {
        return this.components;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.components.onWindowResized(window);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.components.disposeComponents();
    }
}

