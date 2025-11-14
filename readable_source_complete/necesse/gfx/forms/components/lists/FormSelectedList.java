/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormSelectedElement;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormIndexEvent;

public abstract class FormSelectedList<E extends FormSelectedElement>
extends FormGeneralList<E> {
    private FormEventsHandler<FormIndexEvent<FormSelectedList<E>>> onSelected = new FormEventsHandler();
    private int selected;

    public FormSelectedList(int x, int y, int width, int height, int elementHeight) {
        super(x, y, width, height, elementHeight);
    }

    public FormSelectedList<E> onSelected(FormEventListener<FormIndexEvent<FormSelectedList<E>>> listener) {
        this.onSelected.addListener(listener);
        return this;
    }

    protected E getSelectedElement() {
        if (this.selected < 0 || this.selected >= this.elements.size()) {
            return null;
        }
        return (E)((FormSelectedElement)this.elements.get(this.selected));
    }

    @Override
    public void reset() {
        super.reset();
        this.clearSelected();
    }

    public int getSelectedIndex() {
        return this.selected;
    }

    public void setSelected(int selected) {
        int beforeSelected = this.selected;
        this.clearSelected();
        this.selected = selected;
        if (selected < 0 || selected >= this.elements.size()) {
            return;
        }
        FormIndexEvent<FormSelectedList> event = new FormIndexEvent<FormSelectedList>(this, selected);
        if (this.onSelected != null) {
            this.onSelected.onEvent(event);
        }
        if (event.hasPreventedDefault()) {
            this.selected = beforeSelected;
        } else {
            ((FormSelectedElement)this.elements.get(selected)).makeSelected(this);
        }
    }

    public void clearSelected() {
        this.selected = -1;
        this.elements.forEach(FormSelectedElement::clearSelected);
    }
}

