/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 */
package medievalsim.commandcenter.wrapper.util;

import java.lang.reflect.Field;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import necesse.engine.commands.parameterHandlers.ParameterHandler;

public class ParameterInfoExtractor {
    public static String getParameterDescription(ParameterMetadata param) {
        String handlerType;
        ParameterHandler<?> handler = param.getHandler();
        switch (handlerType = handler.getClass().getSimpleName()) {
            case "ItemParameterHandler": {
                return "Item ID (e.g., 'sword', 'stone'). Only obtainable items are valid.";
            }
            case "ServerClientParameterHandler": {
                return "Player name (online players only)";
            }
            case "BuffParameterHandler": {
                return "Buff ID (e.g., 'health', 'speed')";
            }
            case "IntParameterHandler": {
                return "Whole number (e.g., 100, -50)";
            }
            case "FloatParameterHandler": {
                return "Decimal number (e.g., 1.5, -2.3)";
            }
            case "BoolParameterHandler": {
                return "True or False";
            }
            case "RelativeIntParameterHandler": {
                return "Number or relative (+10, -5, ~100 for current position)";
            }
            case "BiomeParameterHandler": {
                return "Biome name (e.g., 'forest', 'desert')";
            }
            case "EnchantmentParameterHandler": {
                return "Enchantment ID";
            }
            case "TileParameterHandler": {
                return "Tile ID (ground/wall tiles)";
            }
            case "LanguageParameterHandler": {
                return "Language code (e.g., 'en', 'es', 'fr')";
            }
            case "PermissionLevelParameterHandler": {
                return "Permission level (USER, MODERATOR, ADMIN, OWNER)";
            }
            case "PresetStringParameterHandler": {
                CharSequence[] presets = ParameterInfoExtractor.extractPresets(handler);
                if (presets != null && presets.length > 0) {
                    return "One of: " + String.join((CharSequence)", ", presets);
                }
                return "Predefined text value";
            }
            case "MultiParameterHandler": {
                return "Multiple choice parameter (select type from dropdown)";
            }
            case "StringParameterHandler": {
                return "Text value";
            }
        }
        return "Parameter value (type: " + handlerType + ")";
    }

    public static String[] getExampleValues(ParameterMetadata param) {
        String handlerType;
        ParameterHandler<?> handler = param.getHandler();
        switch (handlerType = handler.getClass().getSimpleName()) {
            case "ItemParameterHandler": {
                return new String[]{"sword", "stone", "bread"};
            }
            case "IntParameterHandler": {
                return new String[]{"100", "-50", "0"};
            }
            case "FloatParameterHandler": {
                return new String[]{"1.5", "-2.3", "0.0"};
            }
            case "BoolParameterHandler": {
                return new String[]{"true", "false"};
            }
            case "RelativeIntParameterHandler": {
                return new String[]{"+10", "-5", "~", "100"};
            }
            case "BiomeParameterHandler": {
                return new String[]{"forest", "desert", "snow"};
            }
            case "PermissionLevelParameterHandler": {
                return new String[]{"USER", "MODERATOR", "ADMIN", "OWNER"};
            }
            case "PresetStringParameterHandler": {
                String[] presets = ParameterInfoExtractor.extractPresets(handler);
                if (presets != null && presets.length > 0) {
                    return presets;
                }
                return new String[]{"preset1", "preset2"};
            }
        }
        return new String[]{"example"};
    }

    private static String[] extractPresets(ParameterHandler<?> handler) {
        try {
            Field presetsField = handler.getClass().getDeclaredField("presets");
            presetsField.setAccessible(true);
            return (String[])presetsField.get(handler);
        }
        catch (Exception e) {
            return new String[0];
        }
    }

    public static boolean hasAutoComplete(ParameterMetadata param) {
        ParameterHandler<?> handler = param.getHandler();
        String handlerType = handler.getClass().getSimpleName();
        return handlerType.equals("ItemParameterHandler") || handlerType.equals("ServerClientParameterHandler") || handlerType.equals("BuffParameterHandler") || handlerType.equals("BiomeParameterHandler") || handlerType.equals("EnchantmentParameterHandler") || handlerType.equals("TileParameterHandler") || handlerType.equals("LanguageParameterHandler") || handlerType.equals("PermissionLevelParameterHandler") || handlerType.equals("PresetStringParameterHandler");
    }
}

