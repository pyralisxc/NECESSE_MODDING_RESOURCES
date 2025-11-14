/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.network.client.Client
 */
package medievalsim.commandcenter.wrapper.widgets;

import java.lang.reflect.Field;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.DropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.EnumDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.LanguageDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.MultiChoiceWidget;
import medievalsim.commandcenter.wrapper.widgets.NumberInputWidget;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.commandcenter.wrapper.widgets.PermissionLevelDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.PlayerDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.RelativeIntInputWidget;
import medievalsim.commandcenter.wrapper.widgets.TeamDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.TextInputWidget;
import medievalsim.commandcenter.wrapper.widgets.ToggleButtonWidget;
import medievalsim.util.ModLogger;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;

public class ParameterWidgetFactory {
    private static String extractDefaultValue(ParameterHandler<?> handler) {
        if (handler == null) {
            return null;
        }
        try {
            String[] fieldNames;
            for (String fieldName : fieldNames = new String[]{"defaultValue", "def", "defValue"}) {
                try {
                    Field field = handler.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object defaultVal = field.get(handler);
                    if (defaultVal == null) continue;
                    if (defaultVal instanceof Boolean) {
                        return String.valueOf(defaultVal);
                    }
                    if (defaultVal instanceof Number) {
                        return String.valueOf(defaultVal);
                    }
                    if (defaultVal instanceof Enum) {
                        return ((Enum)defaultVal).name();
                    }
                    return defaultVal.toString();
                }
                catch (NoSuchFieldException e) {
                    // empty catch block
                }
            }
        }
        catch (Exception e) {
            ModLogger.debug("Could not extract default for handler %s: %s", handler.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public static ParameterWidget createWidget(ParameterMetadata parameter, int x, int y, Client client) {
        return ParameterWidgetFactory.createWidget(parameter, x, y, client, null);
    }

    public static ParameterWidget createWidget(ParameterMetadata parameter, int x, int y, Client client, String commandId) {
        ParameterMetadata.ParameterHandlerType type = parameter.getHandlerType();
        String defaultValue = ParameterWidgetFactory.extractDefaultValue(parameter.getHandler());
        switch (type) {
            case STRING: {
                if (parameter.hasPresets()) {
                    return new DropdownWidget(parameter, x, y, parameter.getPresets());
                }
                return new TextInputWidget(parameter, x, y, 200, defaultValue);
            }
            case INT: {
                return new NumberInputWidget(parameter, x, y, 100, false, defaultValue);
            }
            case FLOAT: {
                return new NumberInputWidget(parameter, x, y, 100, true, defaultValue);
            }
            case BOOL: {
                return new ToggleButtonWidget(parameter, x, y, client, defaultValue);
            }
            case RELATIVE_INT: {
                return new RelativeIntInputWidget(parameter, x, y);
            }
            case SERVER_CLIENT: {
                return new PlayerDropdownWidget(parameter, x, y, client);
            }
            case MULTI: {
                return new MultiChoiceWidget(parameter, x, y, client);
            }
            case TEAM: {
                return new TeamDropdownWidget(parameter, x, y);
            }
            case LANGUAGE: {
                return new LanguageDropdownWidget(parameter, x, y);
            }
            case PERMISSION_LEVEL: {
                return new PermissionLevelDropdownWidget(parameter, x, y);
            }
            case ENUM: {
                return new EnumDropdownWidget(parameter, x, y);
            }
            case CLIENT_CLIENT: {
                return new PlayerDropdownWidget(parameter, x, y, client);
            }
            case STORED_PLAYER: 
            case LEVEL_IDENTIFIER: 
            case CMD_NAME: 
            case PRESET_STRING: 
            case UNBAN: {
                return new TextInputWidget(parameter, x, y, 200, defaultValue);
            }
            case REST_STRING: {
                return new TextInputWidget(parameter, x, y, 400, defaultValue);
            }
            case ITEM: 
            case BUFF: 
            case ENCHANTMENT: 
            case ARMOR_SET: 
            case BIOME: 
            case TILE: {
                ModLogger.warn("Attempted to create widget for filtered creative parameter type: %s", new Object[]{type});
                return new TextInputWidget(parameter, x, y, 200, defaultValue);
            }
        }
        return new TextInputWidget(parameter, x, y, 200, defaultValue);
    }

    public static ParameterWidget createWidget(ParameterMetadata parameter, int x, int y) {
        return ParameterWidgetFactory.createWidget(parameter, x, y, null);
    }

    public static int getWidgetHeight(ParameterMetadata.ParameterHandlerType type) {
        switch (type) {
            case ITEM: 
            case BUFF: {
                return 30;
            }
            case MULTI: {
                return 30;
            }
        }
        return 30;
    }
}

