/*
 * Decompiled with CFR 0.152.
 */
package customsettingslib.components;

import customsettingslib.components.SettingsComponents;

public class Space
extends SettingsComponents {
    public int height;

    public Space(int height) {
        this.height = height;
    }

    @Override
    public int addComponents(int y, int n) {
        return this.height;
    }
}

