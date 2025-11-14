/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.state.MainGame
 *  necesse.gfx.gameFont.FontOptions
 */
package customsettingslib.settings;

import customsettingslib.components.CustomModSetting;
import customsettingslib.components.Paragraph;
import customsettingslib.components.SettingsComponents;
import customsettingslib.components.Space;
import customsettingslib.components.TextSeparator;
import customsettingslib.components.settings.BooleanSetting;
import customsettingslib.components.settings.ColorSetting;
import customsettingslib.components.settings.IntSetting;
import customsettingslib.components.settings.SelectionSetting;
import customsettingslib.components.settings.StringSetting;
import customsettingslib.settings.CustomModSettingsGetter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainGame;
import necesse.gfx.gameFont.FontOptions;

public class CustomModSettings
extends ModSettings {
    public static List<CustomModSettings> customModSettingsList = new ArrayList<CustomModSettings>();
    public final int position;
    public final LoadedMod mod;
    public final List<Runnable> onSavedListeners = new ArrayList<Runnable>();
    public final List<String> serverSettings = new ArrayList<String>();
    public final List<SettingsComponents> settingsDisplay = new ArrayList<SettingsComponents>();
    public final List<CustomModSetting<?>> settingsList = new ArrayList();
    public final Map<String, CustomModSetting<?>> settingsMap = new HashMap();
    public final Map<String, Object> serverDataSettings = new HashMap<String, Object>();

    public static Object getModSetting(String modID, String settingID) {
        for (CustomModSettings customModSettings : customModSettingsList) {
            if (!Objects.equals(customModSettings.mod.id, modID)) continue;
            return customModSettings.getSetting(settingID);
        }
        return null;
    }

    public static void addOnSavedListener(String modID, Runnable onSaved) {
        for (CustomModSettings customModSettings : customModSettingsList) {
            if (!Objects.equals(customModSettings.mod.id, modID)) continue;
            customModSettings.onSavedListeners.add(onSaved);
            break;
        }
    }

    public CustomModSettings(Runnable onSaved) {
        this.mod = LoadedMod.getRunningMod();
        if (onSaved != null) {
            this.onSavedListeners.add(onSaved);
        }
        this.position = customModSettingsList.size();
        customModSettingsList.add(this);
    }

    public CustomModSettings() {
        this(null);
    }

    public void addSaveData(SaveData saveData) {
        for (CustomModSetting<?> setting : this.settingsList) {
            setting.addSaveData(saveData);
        }
    }

    public void applyLoadData(LoadData loadData) {
        for (CustomModSetting<?> setting : this.settingsList) {
            setting.applyLoadData(loadData);
        }
    }

    public void addServerSettings(String ... serverSettingsIDs) {
        Collections.addAll(this.serverSettings, serverSettingsIDs);
    }

    public Object getSetting(String settingID) {
        return this.serverSettings.contains(settingID) && GlobalData.getCurrentState() instanceof MainGame ? this.serverDataSettings.get(settingID) : this.settingsMap.get(settingID).getValue();
    }

    public Object getSettingDefault(String settingID) {
        return this.settingsMap.get(settingID).getDefaultValue();
    }

    public CustomModSettingsGetter getGetter() {
        return new CustomModSettingsGetter(this);
    }

    public CustomModSettings addCustomComponents(SettingsComponents settingsComponents) {
        this.settingsDisplay.add(settingsComponents);
        return this;
    }

    public CustomModSettings addTextSeparator(String key) {
        this.addCustomComponents(new TextSeparator(key));
        return this;
    }

    public CustomModSettings addParagraph(String key, FontOptions fontOptions, int align, int spaceTop, int spaceBottom) {
        this.addCustomComponents(new Paragraph(key, fontOptions, align, spaceTop, spaceBottom));
        return this;
    }

    public CustomModSettings addParagraph(String key, FontOptions fontOptions, int align) {
        this.addCustomComponents(new Paragraph(key, fontOptions, align, 0, 4));
        return this;
    }

    public CustomModSettings addParagraph(String key, int spaceTop, int spaceBottom) {
        this.addCustomComponents(new Paragraph(key, new FontOptions(12), -1, spaceTop, spaceBottom));
        return this;
    }

    public CustomModSettings addParagraph(String key) {
        this.addCustomComponents(new Paragraph(key, new FontOptions(12), -1, 4, 6));
        return this;
    }

    public CustomModSettings addSpace(int height) {
        this.addCustomComponents(new Space(height));
        return this;
    }

    public CustomModSettings addCustomSetting(CustomModSetting<?> customModSetting) {
        this.settingsDisplay.add(customModSetting);
        this.settingsList.add(customModSetting);
        this.settingsMap.put(customModSetting.id, customModSetting);
        return this;
    }

    public CustomModSettings addBooleanSetting(String id, boolean defaultValue) {
        this.addCustomSetting(new BooleanSetting(id, defaultValue));
        return this;
    }

    public CustomModSettings addStringSetting(String id, String defaultValue, int maxLength, boolean large) {
        this.addCustomSetting(new StringSetting(id, defaultValue, maxLength, large));
        return this;
    }

    public CustomModSettings addIntSetting(String id, int defaultValue, int min, int max, IntSetting.DisplayMode displayMode, int shownDecimals) {
        this.addCustomSetting(new IntSetting(id, defaultValue, min, max, displayMode, shownDecimals));
        return this;
    }

    public CustomModSettings addIntSetting(String id, int defaultValue, int min, int max, IntSetting.DisplayMode displayMode) {
        this.addIntSetting(id, defaultValue, min, max, displayMode, 0);
        return this;
    }

    public CustomModSettings addSelectionSetting(String id, int defaultValue, SelectionSetting.Option ... options) {
        this.addCustomSetting(new SelectionSetting(id, defaultValue, options));
        return this;
    }

    public CustomModSettings addColorSetting(String id, Color defaultValue) {
        this.addCustomSetting(new ColorSetting(id, defaultValue));
        return this;
    }
}

