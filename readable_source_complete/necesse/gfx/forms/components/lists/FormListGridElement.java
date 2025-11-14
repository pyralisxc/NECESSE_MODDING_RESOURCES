/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Rectangle;
import necesse.engine.input.InputEvent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListElement;

public abstract class FormListGridElement<E extends FormGeneralGridList>
extends FormListElement<E> {
    @Override
    public boolean isMouseOver(E parent) {
        if (((FormComponent)parent).isControllerFocus(this)) {
            return true;
        }
        InputEvent event = this.getMoveEvent();
        if (event == null) {
            return false;
        }
        return new Rectangle(0, 0, ((FormGeneralGridList)parent).elementWidth, ((FormGeneralGridList)parent).elementHeight).contains(event.pos.hudX, event.pos.hudY);
    }
}

