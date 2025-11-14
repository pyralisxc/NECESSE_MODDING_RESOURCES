/*
 * Decompiled with CFR 0.152.
 */
package customsettingslib.settings;

import customsettingslib.components.settings.SelectionSetting;
import customsettingslib.settings.CustomModSettings;
import java.awt.Color;

public class CustomModSettingsGetter {
    public final CustomModSettings customModSettings;

    public CustomModSettingsGetter(CustomModSettings customModSettings) {
        this.customModSettings = customModSettings;
    }

    public Object get(String settingID) {
        Object value = this.customModSettings.getSetting(settingID);
        return value == null ? this.customModSettings.getSettingDefault(settingID) : value;
    }

    public boolean getBoolean(String settingID) {
        return (Boolean)this.get(settingID);
    }

    public String getString(String settingID) {
        return (String)this.get(settingID);
    }

    public int getInt(String settingID) {
        return (Integer)this.get(settingID);
    }

    public float getFloat(String settingID, int decimals) {
        return (float)this.getInt(settingID) / (float)Math.pow(10.0, decimals);
    }

    public Color getColor(String settingID) {
        return new Color(this.getInt(settingID), true);
    }

    public Object getSelection(String settingID) {
        SelectionSetting.Option[] options = ((SelectionSetting)this.customModSettings.settingsMap.get((Object)settingID)).options;
        return options[this.getInt((String)settingID)].value;
    }
}

