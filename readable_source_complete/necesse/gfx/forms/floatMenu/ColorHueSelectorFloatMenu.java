/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormColorHuePicker;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.floatMenu.FormFloatMenu;

public abstract class ColorHueSelectorFloatMenu
extends FormFloatMenu {
    public FormColorHuePicker picker;

    public ColorHueSelectorFloatMenu(FormComponent parent, int width, int height, float startHue) {
        super(parent);
        Form form = new Form("hue", width, height);
        this.picker = form.addComponent(new FormColorHuePicker(5, 5, width - 10, height - 10, startHue));
        this.picker.onChanged(e -> this.onChanged(((FormColorHuePicker)e.from).getSelectedHue()));
        this.setForm(form);
    }

    public abstract void onChanged(float var1);
}

