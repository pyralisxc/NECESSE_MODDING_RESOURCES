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

public class TextSeparator
extends SettingsComponents {
    public String key;

    public TextSeparator(String key) {
        this.key = key;
    }

    @Override
    public int addComponents(int y, int n) {
        int addedTop = n == 0 ? 0 : 10;
        FormLocalLabel label = (FormLocalLabel)settingsForm.addComponent((FormComponent)new FormLocalLabel("settingsui", this.key, new FontOptions(20), 0, 4 + TextSeparator.getWidth() / 2, y + addedTop, TextSeparator.getWidth()));
        return label.getHeight() + addedTop + 6;
    }
}

