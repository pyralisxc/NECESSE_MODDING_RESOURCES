/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import medievalsim.commandcenter.settings.AdminSetting;
import medievalsim.commandcenter.settings.SettingsScanner;

public class SettingsRegistry {
    private static final Map<String, AdminSetting<?>> settings = new ConcurrentHashMap();
    private static final Map<String, List<AdminSetting<?>>> settingsByCategory = new ConcurrentHashMap();
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }
        Map<String, List<AdminSetting<?>>> scannedSettings = SettingsScanner.scanAllSettings();
        for (Map.Entry<String, List<AdminSetting<?>>> entry : scannedSettings.entrySet()) {
            String category = entry.getKey();
            List<AdminSetting<?>> categorySettings = entry.getValue();
            settingsByCategory.put(category, categorySettings);
            for (AdminSetting<?> setting : categorySettings) {
                settings.put(setting.getId(), setting);
            }
        }
        initialized = true;
    }

    public static AdminSetting<?> getSetting(String id) {
        return settings.get(id);
    }

    public static Collection<AdminSetting<?>> getAllSettings() {
        return settings.values();
    }

    public static List<AdminSetting<?>> getSettingsByCategory(String category) {
        return settingsByCategory.getOrDefault(category, Collections.emptyList());
    }

    public static Set<String> getCategories() {
        return settingsByCategory.keySet();
    }

    public static List<AdminSetting<?>> getHotReloadSettings() {
        ArrayList hotReload = new ArrayList();
        for (AdminSetting<?> setting : settings.values()) {
            if (setting.requiresRestart() || setting.isReadOnly()) continue;
            hotReload.add(setting);
        }
        return hotReload;
    }

    public static List<AdminSetting<?>> getRestartRequiredSettings() {
        ArrayList restartRequired = new ArrayList();
        for (AdminSetting<?> setting : settings.values()) {
            if (!setting.requiresRestart()) continue;
            restartRequired.add(setting);
        }
        return restartRequired;
    }

    public static void clear() {
        settings.clear();
        settingsByCategory.clear();
        initialized = false;
    }
}

