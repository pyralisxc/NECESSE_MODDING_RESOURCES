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
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public abstract class FloatMenu {
    public final FormComponent parent;
    public ControllerFocusHandler disposeFocus;
    private Runnable removeFunction;
    private boolean isDisposed;
    private int drawX;
    private int drawY;

    public FloatMenu(FormComponent parent) {
        this.parent = parent;
        this.disposeFocus = parent;
    }

    public final void init(int drawX, int drawY, Runnable removeFunction) {
        this.drawX = drawX;
        this.drawY = drawY;
        this.removeFunction = removeFunction;
        this.init();
    }

    public void init() {
    }

    public void remove() {
        if (this.removeFunction == null) {
            throw new IllegalStateException("Cannot remove FloatMenu before it's added");
        }
        this.removeFunction.run();
    }

    public abstract void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3);

    public abstract void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3);

    public abstract void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6);

    public abstract void draw(TickManager var1, PlayerMob var2);

    public abstract boolean isMouseOver(InputEvent var1);

    public int getDrawX() {
        return this.drawX;
    }

    public int getDrawY() {
        return this.drawY;
    }

    public void dispose() {
        this.isDisposed = true;
    }

    public final boolean isDisposed() {
        return this.isDisposed;
    }
}

