/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.localComponents.FormLocalLabel
 *  necesse.gfx.gameFont.FontOptions
 */
package customsettingslib.components;

import customsettingslib.components.SettingsComponents;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class Paragraph
extends SettingsComponents {
    public String key;
    public FontOptions fontOptions;
    public int align;
    public int spaceTop;
    public int spaceBottom;

    public Paragraph(String key, FontOptions fontOptions, int align, int spaceTop, int spaceBottom) {
        this.key = key;
        this.fontOptions = fontOptions;
        this.align = align;
        this.spaceTop = spaceTop;
        this.spaceBottom = spaceBottom;
    }

    @Override
    public int addComponents(int y, int n) {
        int addedTop;
        int width = Paragraph.getWidth();
        int n2 = addedTop = n == 0 ? 0 : this.spaceTop;
        int startX = this.align == 0 ? width - Paragraph.getRightMargin() : (this.align == 1 ? width / 2 : 4);
        FormLocalLabel label = (FormLocalLabel)settingsForm.addComponent((FormComponent)new FormLocalLabel("settingsui", this.key, this.fontOptions, this.align, startX, y + addedTop, width));
        return label.getHeight() + addedTop + this.spaceBottom;
    }
}

