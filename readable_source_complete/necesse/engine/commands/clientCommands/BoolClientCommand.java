/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import java.lang.reflect.Field;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;

public class BoolClientCommand
extends ModularChatCommand {
    private final BoolCommandLogic logic;

    public BoolClientCommand(String name, String action, PermissionLevel permissionLevel, String paramName, BoolCommandLogic logic) {
        super(name, action, permissionLevel, false, new CmdParameter(paramName, new PresetStringParameterHandler(true, "1", "0", "default"), true, new CmdParameter[0]));
        this.logic = logic;
    }

    public BoolClientCommand(String name, String action, PermissionLevel permissionLevel, String paramName, Field boolField, Object accessor) {
        this(name, action, permissionLevel, paramName, (Client c, CommandLog l, BoolCommandResult r) -> {
            try {
                Object realAccessor = accessor == null ? c : accessor;
                boolField.setBoolean(realAccessor, r.result(boolField.getBoolean(realAccessor)));
                l.add(boolField.getName() + ": " + boolField.getBoolean(realAccessor));
            }
            catch (IllegalAccessException e) {
                l.add("Cannot access field");
            }
        });
    }

    public BoolClientCommand(String name, String action, PermissionLevel permissionLevel, BoolCommandLogic logic) {
        this(name, action, permissionLevel, "1/0", logic);
    }

    public BoolClientCommand(String name, String action, PermissionLevel permissionLevel, Field boolField, Object accessor) {
        this(name, action, permissionLevel, "1/0", boolField, accessor);
    }

    public static BoolClientCommand create(String name, String action, PermissionLevel permissionLevel, String paramName, Class fieldClass, String fieldName, Object accessor) {
        try {
            return new BoolClientCommand(name, action, permissionLevel, paramName, fieldClass.getField(fieldName), accessor);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BoolClientCommand create(String name, String action, PermissionLevel permissionLevel, Class fieldClass, String fieldName, Object accessor) {
        return BoolClientCommand.create(name, action, permissionLevel, "1/0", fieldClass, fieldName, accessor);
    }

    public static BoolClientCommand create(String name, String action, PermissionLevel permissionLevel, String paramName, GameObject fieldObject, String fieldName) {
        try {
            return new BoolClientCommand(name, action, permissionLevel, paramName, fieldObject.getClass().getField(fieldName), fieldObject);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BoolClientCommand create(String name, String action, PermissionLevel permissionLevel, GameObject fieldObject, String fieldName) {
        return BoolClientCommand.create(name, action, permissionLevel, "1/0", fieldObject, fieldName);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String arg = (String)args[0];
        BoolCommandResult result = arg.equals("default") ? new BoolCommandResult(false, false) : new BoolCommandResult(true, arg.equals("1"));
        this.logic.apply(client, logs, result);
    }

    @FunctionalInterface
    public static interface BoolCommandLogic {
        public void apply(Client var1, CommandLog var2, BoolCommandResult var3);
    }

    public static class BoolCommandResult {
        public final boolean resultGiven;
        public final boolean result;

        private BoolCommandResult(boolean resultGiven, boolean result) {
            this.resultGiven = resultGiven;
            this.result = result;
        }

        public boolean result(boolean current) {
            if (!this.resultGiven) {
                return !current;
            }
            return this.result;
        }
    }
}

