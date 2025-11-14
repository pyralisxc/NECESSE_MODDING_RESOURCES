/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import java.util.function.BiConsumer;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class VoidClientCommand
extends ModularChatCommand {
    private final BiConsumer<Client, CommandLog> logic;

    public VoidClientCommand(String name, String action, PermissionLevel permissionLevel, BiConsumer<Client, CommandLog> logic) {
        super(name, action, permissionLevel, false, new CmdParameter[0]);
        this.logic = logic;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        this.logic.accept(client, logs);
    }
}

