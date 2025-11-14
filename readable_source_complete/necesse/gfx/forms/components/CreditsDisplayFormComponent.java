/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.credits.GameCreditsDisplay;
import necesse.gfx.credits.GameCreditsDrawManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class CreditsDisplayFormComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected GameCreditsDisplay display;
    protected GameCreditsDrawManager drawManager;

    public CreditsDisplayFormComponent(int x, int y, GameWindow window, GameCreditsDisplay display) {
        this.position = new FormFixedPosition(x, y);
        this.display = display;
        this.drawManager = new GameCreditsDrawManager(window, display);
    }

    public void restart() {
        this.drawManager.restart();
    }

    public boolean isDone() {
        return this.drawManager.isDone();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.drawManager.draw(this.getX(), this.getY());
    }

    @Override
    public List<Rectangle> getHitboxes() {
        Dimension bounds = this.drawManager.getBounds();
        return CreditsDisplayFormComponent.singleBox(new Rectangle(this.getX(), this.getY(), bounds.width, bounds.height));
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return false;
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
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.drawManager.onWindowResized(window);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.drawManager.dispose();
    }
}

