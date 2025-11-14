/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import java.awt.Color;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.forms.presets.ColorSelectorForm;

public abstract class ColorSelectorFloatMenu
extends FormFloatMenu {
    private boolean appliedPressed = false;

    public ColorSelectorFloatMenu(FormComponent parent, Color startColor) {
        super(parent);
        this.setForm(new ColorSelectorForm(startColor){

            @Override
            public void onApplied(Color color) {
                ColorSelectorFloatMenu.this.onApplied(color);
                ColorSelectorFloatMenu.this.appliedPressed = true;
                ColorSelectorFloatMenu.this.remove();
            }

            @Override
            public void onSelected(Color color) {
                ColorSelectorFloatMenu.this.onSelected(color);
            }
        });
    }

    public abstract void onApplied(Color var1);

    public abstract void onSelected(Color var1);

    @Override
    public void dispose() {
        if (!this.isDisposed() && !this.appliedPressed) {
            this.onApplied(null);
        }
        super.dispose();
    }
}

