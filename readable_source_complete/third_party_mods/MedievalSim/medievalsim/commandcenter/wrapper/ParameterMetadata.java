/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.parameterHandlers.BoolParameterHandler
 *  necesse.engine.commands.parameterHandlers.BuffParameterHandler
 *  necesse.engine.commands.parameterHandlers.FloatParameterHandler
 *  necesse.engine.commands.parameterHandlers.IntParameterHandler
 *  necesse.engine.commands.parameterHandlers.ItemParameterHandler
 *  necesse.engine.commands.parameterHandlers.MultiParameterHandler
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.commands.parameterHandlers.RelativeIntParameterHandler
 *  necesse.engine.commands.parameterHandlers.ServerClientParameterHandler
 *  necesse.engine.commands.parameterHandlers.StringParameterHandler
 */
package medievalsim.commandcenter.wrapper;

import java.lang.reflect.Field;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.BuffParameterHandler;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ItemParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.RelativeIntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;

public class ParameterMetadata {
    private final String name;
    private final boolean optional;
    private final boolean partOfUsage;
    private final ParameterHandler<?> handler;
    private final ParameterHandlerType handlerType;
    private final ParameterMetadata[] extraParams;

    public ParameterMetadata(String name, boolean optional, boolean partOfUsage, ParameterHandler<?> handler, ParameterHandlerType handlerType, ParameterMetadata[] extraParams) {
        this.name = name;
        this.optional = optional;
        this.partOfUsage = partOfUsage;
        this.handler = handler;
        this.handlerType = handlerType;
        this.extraParams = extraParams;
    }

    public static ParameterMetadata fromCmdParameter(CmdParameter cmdParam) {
        try {
            String name = cmdParam.name;
            boolean optional = cmdParam.optional;
            boolean partOfUsage = cmdParam.partOfUsage;
            ParameterHandler handler = cmdParam.param;
            ParameterHandlerType handlerType = ParameterMetadata.determineHandlerType(handler);
            CmdParameter[] extraCmdParams = cmdParam.extraParams;
            ParameterMetadata[] extraParams = new ParameterMetadata[extraCmdParams.length];
            for (int i = 0; i < extraCmdParams.length; ++i) {
                extraParams[i] = ParameterMetadata.fromCmdParameter(extraCmdParams[i]);
            }
            return new ParameterMetadata(name, optional, partOfUsage, handler, handlerType, extraParams);
        }
        catch (Exception e) {
            System.err.println("Failed to parse parameter metadata for: " + cmdParam.name);
            e.printStackTrace();
            return null;
        }
    }

    public static ParameterHandlerType determineHandlerType(ParameterHandler<?> handler) {
        String className;
        if (handler.getClass().getSimpleName().equals("ArmorSetParameterHandler")) {
            return ParameterHandlerType.ARMOR_SET;
        }
        if (handler instanceof ServerClientParameterHandler) {
            return ParameterHandlerType.SERVER_CLIENT;
        }
        if (handler instanceof ItemParameterHandler) {
            return ParameterHandlerType.ITEM;
        }
        if (handler instanceof BuffParameterHandler) {
            return ParameterHandlerType.BUFF;
        }
        if (handler instanceof RelativeIntParameterHandler) {
            return ParameterHandlerType.RELATIVE_INT;
        }
        if (handler instanceof IntParameterHandler) {
            return ParameterHandlerType.INT;
        }
        if (handler instanceof FloatParameterHandler) {
            return ParameterHandlerType.FLOAT;
        }
        if (handler instanceof BoolParameterHandler) {
            return ParameterHandlerType.BOOL;
        }
        if (handler instanceof MultiParameterHandler) {
            return ParameterHandlerType.MULTI;
        }
        switch (className = handler.getClass().getSimpleName()) {
            case "BiomeParameterHandler": {
                return ParameterHandlerType.BIOME;
            }
            case "EnchantmentParameterHandler": {
                return ParameterHandlerType.ENCHANTMENT;
            }
            case "TileParameterHandler": {
                return ParameterHandlerType.TILE;
            }
            case "ClientClientParameterHandler": {
                return ParameterHandlerType.CLIENT_CLIENT;
            }
            case "TeamParameterHandler": {
                return ParameterHandlerType.TEAM;
            }
            case "LanguageParameterHandler": {
                return ParameterHandlerType.LANGUAGE;
            }
            case "LevelIdentifierParameterHandler": {
                return ParameterHandlerType.LEVEL_IDENTIFIER;
            }
            case "PermissionLevelParameterHandler": {
                return ParameterHandlerType.PERMISSION_LEVEL;
            }
            case "EnumParameterHandler": {
                return ParameterHandlerType.ENUM;
            }
            case "StoredPlayerParameterHandler": {
                return ParameterHandlerType.STORED_PLAYER;
            }
            case "PresetStringParameterHandler": {
                return ParameterHandlerType.PRESET_STRING;
            }
            case "RestStringParameterHandler": {
                return ParameterHandlerType.REST_STRING;
            }
            case "CmdNameParameterHandler": {
                return ParameterHandlerType.CMD_NAME;
            }
            case "UnbanParameterHandler": {
                return ParameterHandlerType.UNBAN;
            }
            case "StringParameterHandler": {
                return ParameterHandlerType.STRING;
            }
        }
        return ParameterHandlerType.UNKNOWN;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isRequired() {
        return !this.optional;
    }

    public boolean isPartOfUsage() {
        return this.partOfUsage;
    }

    public ParameterHandler<?> getHandler() {
        return this.handler;
    }

    public ParameterHandlerType getHandlerType() {
        return this.handlerType;
    }

    public ParameterMetadata[] getExtraParams() {
        return this.extraParams;
    }

    public String getDisplayName() {
        if (this.name == null || this.name.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(this.name.charAt(0)));
        for (int i = 1; i < this.name.length(); ++i) {
            char c = this.name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append(' ');
            }
            result.append(c);
        }
        return result.toString();
    }

    public boolean supportsWorldClick() {
        return this.handlerType == ParameterHandlerType.RELATIVE_INT;
    }

    public boolean hasPresets() {
        if (this.handler.getClass().getSimpleName().equals("PresetStringParameterHandler")) {
            try {
                Field presetsField = this.handler.getClass().getDeclaredField("presets");
                presetsField.setAccessible(true);
                String[] presets = (String[])presetsField.get(this.handler);
                return presets != null && presets.length > 0;
            }
            catch (Exception e) {
                return false;
            }
        }
        if (this.handlerType == ParameterHandlerType.STRING && this.handler instanceof StringParameterHandler) {
            try {
                Field presetsField = StringParameterHandler.class.getDeclaredField("presets");
                presetsField.setAccessible(true);
                String[] presets = (String[])presetsField.get(this.handler);
                return presets != null && presets.length > 0;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public String[] getPresets() {
        if (this.handler.getClass().getSimpleName().equals("PresetStringParameterHandler")) {
            try {
                Field presetsField = this.handler.getClass().getDeclaredField("presets");
                presetsField.setAccessible(true);
                return (String[])presetsField.get(this.handler);
            }
            catch (Exception presetsField) {
                // empty catch block
            }
        }
        if (this.hasPresets() && this.handler instanceof StringParameterHandler) {
            try {
                Field presetsField = StringParameterHandler.class.getDeclaredField("presets");
                presetsField.setAccessible(true);
                return (String[])presetsField.get(this.handler);
            }
            catch (Exception e) {
                return new String[0];
            }
        }
        return new String[0];
    }

    public String toString() {
        return String.format("ParameterMetadata{name='%s', type=%s, optional=%b}", new Object[]{this.name, this.handlerType, this.optional});
    }

    public static enum ParameterHandlerType {
        SERVER_CLIENT,
        ITEM,
        BUFF,
        RELATIVE_INT,
        INT,
        FLOAT,
        BOOL,
        STRING,
        MULTI,
        ARMOR_SET,
        BIOME,
        ENCHANTMENT,
        TILE,
        CLIENT_CLIENT,
        TEAM,
        LANGUAGE,
        LEVEL_IDENTIFIER,
        PERMISSION_LEVEL,
        ENUM,
        STORED_PLAYER,
        PRESET_STRING,
        REST_STRING,
        CMD_NAME,
        UNBAN,
        UNKNOWN;

    }
}

