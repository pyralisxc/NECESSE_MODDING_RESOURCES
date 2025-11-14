/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.ParsedCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.client.Client
 *  necesse.engine.world.WorldSettings
 */
package medievalsim.commandcenter.wrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import medievalsim.commandcenter.CommandCategory;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldSettings;

public class NecesseCommandMetadata {
    private final String id;
    private final String action;
    private final PermissionLevel permission;
    private final boolean isCheat;
    private final List<ParameterMetadata> parameters;
    private final CommandCategory category;

    public NecesseCommandMetadata(String id, String action, PermissionLevel permission, boolean isCheat, List<ParameterMetadata> parameters, CommandCategory category) {
        this.id = id;
        this.action = action;
        this.permission = permission;
        this.isCheat = isCheat;
        this.parameters = parameters;
        this.category = category;
    }

    public static NecesseCommandMetadata fromNecesseCommand(ModularChatCommand command, CommandCategory category) {
        try {
            String id = command.name;
            String action = command.getAction();
            PermissionLevel permission = command.permissionLevel;
            boolean isCheat = command.isCheat();
            Field parametersField = ModularChatCommand.class.getDeclaredField("parameters");
            parametersField.setAccessible(true);
            CmdParameter[] cmdParameters = (CmdParameter[])parametersField.get(command);
            ArrayList<ParameterMetadata> parameters = new ArrayList<ParameterMetadata>();
            if (cmdParameters != null) {
                for (int i = 0; i < cmdParameters.length; ++i) {
                    CmdParameter cmdParam = cmdParameters[i];
                    ParameterMetadata paramMeta = ParameterMetadata.fromCmdParameter(cmdParam);
                    if (paramMeta == null) continue;
                    parameters.add(paramMeta);
                }
            }
            return new NecesseCommandMetadata(id, action, permission, isCheat, parameters, category);
        }
        catch (Exception e) {
            System.err.println("Failed to parse command metadata for: " + command.name);
            e.printStackTrace();
            return null;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getAction() {
        return this.action;
    }

    public String getDisplayName() {
        if (this.id == null || this.id.isEmpty()) {
            return "Unknown";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(this.id.charAt(0)));
        for (int i = 1; i < this.id.length(); ++i) {
            char c = this.id.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(' ');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public PermissionLevel getPermission() {
        return this.permission;
    }

    public boolean isCheat() {
        return this.isCheat;
    }

    public List<ParameterMetadata> getParameters() {
        return this.parameters;
    }

    public CommandCategory getCategory() {
        return this.category;
    }

    public boolean isAvailableInWorld(Client client) {
        if (client == null || client.worldSettings == null) {
            return true;
        }
        WorldSettings settings = client.worldSettings;
        if (settings.creativeMode) {
            return true;
        }
        if (settings.allowCheats) {
            return true;
        }
        return !this.isCheat();
    }

    public String buildCommandString(String[] parameterValues) {
        StringBuilder cmd = new StringBuilder("/");
        cmd.append(this.id);
        int valueIndex = 0;
        for (int paramIndex = 0; paramIndex < this.parameters.size(); ++paramIndex) {
            ParameterMetadata param = this.parameters.get(paramIndex);
            String value = null;
            if (valueIndex < parameterValues.length) {
                value = parameterValues[valueIndex];
            }
            if (value == null || value.trim().isEmpty()) {
                if (param.isOptional()) {
                    ++valueIndex;
                    continue;
                }
                String errorMsg = "Required parameter '" + param.getName() + "' cannot be empty";
                throw new IllegalArgumentException(errorMsg);
            }
            String wrappedValue = ParsedCommand.wrapArgument((String)value);
            cmd.append(" ").append(wrappedValue);
            ++valueIndex;
        }
        return cmd.toString();
    }

    public String toString() {
        return String.format("NecesseCommandMetadata{id='%s', action='%s', permission=%s, parameters=%d}", this.id, this.action, this.permission, this.parameters.size());
    }
}

