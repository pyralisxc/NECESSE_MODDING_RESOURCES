/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.controller.ControllerFocusHandler;

public abstract class FormListElement<E extends FormGeneralList>
implements ControllerFocusHandler {
    private InputEvent moveEvent;

    public void setMoveEvent(InputEvent moveEvent) {
        this.moveEvent = moveEvent;
    }

    public InputEvent getMoveEvent() {
        return this.moveEvent;
    }

    public boolean isHovering() {
        return this.moveEvent != null;
    }

    protected abstract void draw(E var1, TickManager var2, PlayerMob var3, int var4);

    protected abstract void onClick(E var1, int var2, InputEvent var3, PlayerMob var4);

    @Override
    public final void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        throw new UnsupportedOperationException("Use onControllerEvent method instead");
    }

    protected abstract void onControllerEvent(E var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5);

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        return false;
    }

    public boolean isMouseOver(E parent) {
        if (((FormComponent)parent).isControllerFocus(this)) {
            return true;
        }
        InputEvent event = this.getMoveEvent();
        if (event == null) {
            return false;
        }
        return new Rectangle(0, 0, ((FormGeneralList)parent).width, ((FormGeneralList)parent).elementHeight).contains(event.pos.hudX, event.pos.hudY);
    }
}

