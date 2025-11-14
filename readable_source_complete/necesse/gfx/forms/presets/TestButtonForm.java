/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class TestButtonForm
extends Form {
    public TestButtonForm(Runnable runnable) {
        super("testForm", 200, FormInputSize.SIZE_24.height + 40);
        this.setDraggingBox(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
        this.addComponent(new FormTextButton("Test", 20, 20, this.getWidth() - 40, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> runnable.run());
        this.setPosition(50, 50);
    }
}

