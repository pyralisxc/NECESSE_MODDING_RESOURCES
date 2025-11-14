/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormColorPicker;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;

public abstract class ColorSelectorForm
extends Form {
    private FormColorPicker picker = this.addComponent(new FormColorPicker(6, 6, this.getWidth() - 12, this.getHeight() - 40 - 6));
    private FormTextButton selectButton;

    public ColorSelectorForm(Color startColor) {
        this(null, startColor);
    }

    public ColorSelectorForm(String name, Color startColor) {
        super(name, 300, 240);
        this.picker.onChanged(e -> {
            Color selectedColor = this.picker.getSelectedColor();
            this.selectButton.setActive(selectedColor != null);
            if (selectedColor != null) {
                this.onSelected(selectedColor);
            }
        });
        this.picker.setSelectedColor(startColor);
        this.selectButton = this.addComponent(new FormLocalTextButton("ui", "selectbutton", 4, this.getHeight() - 40, this.getWidth() / 2 - 6));
        this.selectButton.onClicked(e -> this.onApplied(this.picker.getSelectedColor()));
        this.selectButton.setActive(this.picker.getSelectedColor() != null);
        this.addComponent(new FormLocalTextButton("ui", "cancelbutton", this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6)).onClicked(e -> this.onApplied(null));
    }

    public abstract void onApplied(Color var1);

    public abstract void onSelected(Color var1);
}

