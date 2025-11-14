/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormContentBox
 */
package customsettingslib.components;

import customsettingslib.settings.CustomModSettings;
import necesse.gfx.forms.components.FormContentBox;

public abstract class SettingsComponents {
    public static final int LEFT_MARGIN = 4;
    public static FormContentBox settingsForm;
    public static CustomModSettings customModSettings;

    public static int getRightMargin() {
        return settingsForm.getScrollBarWidth() + 4;
    }

    public static int getWidth() {
        return settingsForm.getWidth() - 4 - SettingsComponents.getRightMargin();
    }

    public abstract int addComponents(int var1, int var2);
}

