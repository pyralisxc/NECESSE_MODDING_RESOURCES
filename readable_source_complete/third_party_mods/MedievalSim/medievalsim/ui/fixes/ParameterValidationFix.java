/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.ui.fixes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import medievalsim.commandcenter.wrapper.NecesseCommandMetadata;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.util.ModLogger;

public class ParameterValidationFix {
    public static Map<String, String> validateAndFixParameters(String commandId, Map<String, String> parameterValues, Map<String, ParameterMetadata> parameterMetadata) {
        HashMap<String, String> fixedValues = new HashMap<String, String>();
        for (Map.Entry<String, ParameterMetadata> entry : parameterMetadata.entrySet()) {
            String paramName = entry.getKey();
            ParameterMetadata metadata = entry.getValue();
            String rawValue = parameterValues.get(paramName);
            String fixedValue = ParameterValidationFix.fixParameterValue(paramName, rawValue, metadata);
            if (fixedValue != null) {
                fixedValues.put(paramName, fixedValue);
                ModLogger.info("[ParameterFix] Fixed parameter '{}': '{}' -> '{}'", paramName, rawValue, fixedValue);
                continue;
            }
            if (metadata.isOptional()) continue;
            ModLogger.warn("[ParameterFix] Required parameter '{}' is missing or invalid: '{}'", paramName, rawValue);
        }
        return fixedValues;
    }

    private static String fixParameterValue(String paramName, String rawValue, ParameterMetadata metadata) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            if (metadata.isOptional()) {
                return null;
            }
            return null;
        }
        String trimmedValue = rawValue.trim();
        switch (metadata.getHandlerType()) {
            case ENUM: {
                return ParameterValidationFix.fixEnumParameter(paramName, trimmedValue, metadata);
            }
            case STRING: 
            case PRESET_STRING: 
            case REST_STRING: {
                return ParameterValidationFix.fixStringParameter(paramName, trimmedValue, metadata);
            }
            case INT: {
                return ParameterValidationFix.fixIntParameter(paramName, trimmedValue, metadata);
            }
            case FLOAT: {
                return ParameterValidationFix.fixFloatParameter(paramName, trimmedValue, metadata);
            }
            case BOOL: {
                return ParameterValidationFix.fixBooleanParameter(paramName, trimmedValue, metadata);
            }
            case SERVER_CLIENT: 
            case STORED_PLAYER: {
                return ParameterValidationFix.fixPlayerParameter(paramName, trimmedValue, metadata);
            }
        }
        return trimmedValue;
    }

    private static String fixEnumParameter(String paramName, String value, ParameterMetadata metadata) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (paramName.contains("list/penalty")) {
            return ParameterValidationFix.fixDeathPenaltyEnum(value);
        }
        if (paramName.contains("list/frequency")) {
            return ParameterValidationFix.fixRaidFrequencyEnum(value);
        }
        return value.toUpperCase();
    }

    private static String fixDeathPenaltyEnum(String value) {
        String upper;
        if (value == null || value.isEmpty()) {
            return null;
        }
        switch (upper = value.toUpperCase()) {
            case "NONE": 
            case "NO PENALTY": {
                return "NONE";
            }
            case "DROP_MATS": 
            case "DROP MATS": 
            case "DROP MATERIALS": {
                return "DROP_MATS";
            }
            case "DROP_MAIN_INVENTORY": 
            case "DROP MAIN INVENTORY": 
            case "DROP MAIN": {
                return "DROP_MAIN_INVENTORY";
            }
            case "DROP_FULL_INVENTORY": 
            case "DROP FULL INVENTORY": 
            case "DROP ALL": {
                return "DROP_FULL_INVENTORY";
            }
            case "HARDCORE": {
                return "HARDCORE";
            }
        }
        return upper;
    }

    private static String fixRaidFrequencyEnum(String value) {
        String upper;
        if (value == null || value.isEmpty()) {
            return null;
        }
        switch (upper = value.toUpperCase()) {
            case "OFTEN": {
                return "OFTEN";
            }
            case "OCCASIONALLY": 
            case "SOMETIMES": {
                return "OCCASIONALLY";
            }
            case "RARELY": 
            case "SELDOM": {
                return "RARELY";
            }
            case "NEVER": 
            case "OFF": 
            case "DISABLED": {
                return "NEVER";
            }
        }
        return upper;
    }

    private static String fixStringParameter(String paramName, String value, ParameterMetadata metadata) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static String fixIntParameter(String paramName, String value, ParameterMetadata metadata) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int intValue = Integer.parseInt(value.trim());
            return String.valueOf(intValue);
        }
        catch (NumberFormatException e) {
            ModLogger.warn("[ParameterFix] Invalid integer value for '{}': '{}'", paramName, value);
            return null;
        }
    }

    private static String fixFloatParameter(String paramName, String value, ParameterMetadata metadata) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            float floatValue = Float.parseFloat(value.trim());
            return String.valueOf(floatValue);
        }
        catch (NumberFormatException e) {
            ModLogger.warn("[ParameterFix] Invalid float value for '{}': '{}'", paramName, value);
            return null;
        }
    }

    private static String fixBooleanParameter(String paramName, String value, ParameterMetadata metadata) {
        String lower;
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        switch (lower = value.trim().toLowerCase()) {
            case "true": 
            case "yes": 
            case "on": 
            case "1": 
            case "enabled": {
                return "true";
            }
            case "false": 
            case "no": 
            case "off": 
            case "0": 
            case "disabled": {
                return "false";
            }
        }
        ModLogger.warn("[ParameterFix] Invalid boolean value for '{}': '{}'", paramName, value);
        return null;
    }

    private static String fixPlayerParameter(String paramName, String value, ParameterMetadata metadata) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmedValue = value.trim();
        if (trimmedValue.contains(" (")) {
            trimmedValue = trimmedValue.substring(0, trimmedValue.indexOf(" ("));
        }
        return trimmedValue;
    }

    public static String[] validateAndFixParameters(NecesseCommandMetadata command, String[] parameterValues) {
        if (command == null || parameterValues == null) {
            return parameterValues;
        }
        try {
            HashMap<String, String> valueMap = new HashMap<String, String>();
            HashMap<String, ParameterMetadata> metadataMap = new HashMap<String, ParameterMetadata>();
            List<ParameterMetadata> parameters = command.getParameters();
            for (int i = 0; i < parameters.size() && i < parameterValues.length; ++i) {
                ParameterMetadata param = parameters.get(i);
                String value = parameterValues[i];
                valueMap.put(param.getName(), value);
                metadataMap.put(param.getName(), param);
            }
            Map<String, String> fixedValues = ParameterValidationFix.validateAndFixParameters(command.getId(), valueMap, metadataMap);
            String[] result = new String[parameterValues.length];
            for (int i = 0; i < parameters.size() && i < result.length; ++i) {
                ParameterMetadata param = parameters.get(i);
                String fixedValue = fixedValues.get(param.getName());
                result[i] = fixedValue != null ? fixedValue : parameterValues[i];
            }
            return result;
        }
        catch (Exception e) {
            ModLogger.error("[ParameterFix] Error validating parameters for command " + command.getId(), e);
            return parameterValues;
        }
    }

    public static class CommandSpecificFixes {
        public static Map<String, String> fixBanCommandParameters(Map<String, String> params) {
            HashMap<String, String> fixed = new HashMap<String, String>(params);
            String authName = (String)fixed.get("authentication/name");
            if (authName != null && !authName.trim().isEmpty()) {
                if ((authName = authName.trim()).contains(" (")) {
                    authName = authName.substring(0, authName.indexOf(" ("));
                }
                fixed.put("authentication/name", authName);
            }
            return fixed;
        }

        public static Map<String, String> fixDeathPenaltyCommandParameters(Map<String, String> params) {
            HashMap<String, String> fixed = new HashMap<String, String>(params);
            String penalty = (String)fixed.get("list/penalty");
            if (penalty == null || penalty.trim().isEmpty()) {
                ModLogger.warn("[ParameterFix] Death penalty command missing required list/penalty parameter");
                return fixed;
            }
            if ((penalty = ParameterValidationFix.fixDeathPenaltyEnum(penalty)) != null) {
                fixed.put("list/penalty", penalty);
            }
            return fixed;
        }
    }
}

