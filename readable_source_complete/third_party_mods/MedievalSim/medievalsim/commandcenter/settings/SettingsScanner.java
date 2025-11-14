/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter.settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import medievalsim.commandcenter.settings.AdminSetting;
import medievalsim.commandcenter.settings.SettingType;
import medievalsim.util.Constants;
import medievalsim.util.RuntimeConstants;

public class SettingsScanner {
    public static Map<String, List<AdminSetting<?>>> scanAllSettings() {
        HashMap settingsByCategory = new HashMap();
        SettingsScanner.scanConstantsClass(Constants.BuildMode.class, "Build Mode", settingsByCategory, true);
        SettingsScanner.scanConstantsClass(Constants.Zones.class, "Zone Settings", settingsByCategory, true);
        SettingsScanner.scanConstantsClass(Constants.AdminTools.class, "Admin Tools", settingsByCategory, true);
        SettingsScanner.scanConstantsClass(Constants.Network.class, "Network", settingsByCategory, true);
        SettingsScanner.scanRuntimeClass(RuntimeConstants.Zones.class, "Zone Settings (Hot-Reload)", settingsByCategory);
        SettingsScanner.scanRuntimeClass(RuntimeConstants.BuildMode.class, "Build Mode (Hot-Reload)", settingsByCategory);
        return settingsByCategory;
    }

    private static void scanConstantsClass(Class<?> clazz, String category, Map<String, List<AdminSetting<?>>> map, boolean requiresRestart) {
        ArrayList settings = new ArrayList();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                AdminSetting<?> setting = SettingsScanner.createReadOnlySetting(field.getName(), value, category, requiresRestart);
                if (setting == null) continue;
                settings.add(setting);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (!settings.isEmpty()) {
            map.put(category, settings);
        }
    }

    private static void scanRuntimeClass(Class<?> clazz, String category, Map<String, List<AdminSetting<?>>> map) {
        ArrayList settings = new ArrayList();
        HashMap<String, Method> getters = new HashMap<String, Method>();
        HashMap<String, Method> setters = new HashMap<String, Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            String propertyName;
            if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) continue;
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterCount() == 0) {
                propertyName = name.substring(3);
                getters.put(propertyName, method);
                continue;
            }
            if (!name.startsWith("set") || method.getParameterCount() != 1) continue;
            propertyName = name.substring(3);
            setters.put(propertyName, method);
        }
        for (String propertyName : getters.keySet()) {
            AdminSetting<?> setting;
            Method getter = (Method)getters.get(propertyName);
            Method setter = (Method)setters.get(propertyName);
            if (getter == null || (setting = SettingsScanner.createRuntimeSetting(propertyName, getter, setter, category)) == null) continue;
            settings.add(setting);
        }
        if (!settings.isEmpty()) {
            map.put(category, settings);
        }
    }

    private static AdminSetting<?> createReadOnlySetting(String fieldName, Object value, String category, boolean requiresRestart) {
        if (value instanceof Integer) {
            return new AdminSetting.Builder(fieldName, SettingsScanner.formatFieldName(fieldName), SettingType.INTEGER).category(category).defaultValue((Integer)value).requiresRestart(requiresRestart).getter(() -> (Integer)value).build();
        }
        if (value instanceof Long) {
            return new AdminSetting.Builder(fieldName, SettingsScanner.formatFieldName(fieldName), SettingType.LONG).category(category).defaultValue((Long)value).requiresRestart(requiresRestart).getter(() -> (Long)value).build();
        }
        if (value instanceof Float) {
            return new AdminSetting.Builder(fieldName, SettingsScanner.formatFieldName(fieldName), SettingType.FLOAT).category(category).defaultValue((Float)value).requiresRestart(requiresRestart).getter(() -> (Float)value).build();
        }
        if (value instanceof Boolean) {
            return new AdminSetting.Builder(fieldName, SettingsScanner.formatFieldName(fieldName), SettingType.BOOLEAN).category(category).defaultValue((Boolean)value).requiresRestart(requiresRestart).getter(() -> (Boolean)value).build();
        }
        if (value instanceof String) {
            return new AdminSetting.Builder(fieldName, SettingsScanner.formatFieldName(fieldName), SettingType.STRING).category(category).defaultValue((String)value).requiresRestart(requiresRestart).getter(() -> (String)value).build();
        }
        return null;
    }

    private static AdminSetting<?> createRuntimeSetting(String propertyName, Method getter, Method setter, String category) {
        Class<?> returnType = getter.getReturnType();
        try {
            if (returnType == Integer.TYPE || returnType == Integer.class) {
                return new AdminSetting.Builder(propertyName, SettingsScanner.formatFieldName(propertyName), SettingType.INTEGER).category(category).getter(() -> {
                    try {
                        return (Integer)getter.invoke(null, new Object[0]);
                    }
                    catch (Exception e) {
                        return 0;
                    }
                }).setter(value -> {
                    if (setter != null) {
                        try {
                            setter.invoke(null, value);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }).build();
            }
            if (returnType == Long.TYPE || returnType == Long.class) {
                return new AdminSetting.Builder(propertyName, SettingsScanner.formatFieldName(propertyName), SettingType.LONG).category(category).getter(() -> {
                    try {
                        return (Long)getter.invoke(null, new Object[0]);
                    }
                    catch (Exception e) {
                        return 0L;
                    }
                }).setter(value -> {
                    if (setter != null) {
                        try {
                            setter.invoke(null, value);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }).build();
            }
            if (returnType == Float.TYPE || returnType == Float.class) {
                return new AdminSetting.Builder(propertyName, SettingsScanner.formatFieldName(propertyName), SettingType.FLOAT).category(category).getter(() -> {
                    try {
                        return (Float)getter.invoke(null, new Object[0]);
                    }
                    catch (Exception e) {
                        return Float.valueOf(0.0f);
                    }
                }).setter(value -> {
                    if (setter != null) {
                        try {
                            setter.invoke(null, value);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }).build();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private static String formatFieldName(String fieldName) {
        String[] words = fieldName.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            if (word.length() <= 0) continue;
            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() <= 1) continue;
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }
}

