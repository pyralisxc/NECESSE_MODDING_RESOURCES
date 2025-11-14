/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class ModularChatCommand
extends ChatCommand {
    public final boolean isCheat;
    public final String action;
    private CmdParameter[] parameters;

    public ModularChatCommand(String name, String action, PermissionLevel permissionLevel, boolean isCheat, CmdParameter ... parameters) {
        super(name, permissionLevel);
        this.action = action;
        this.isCheat = isCheat;
        this.parameters = parameters;
    }

    @Override
    public final boolean run(Client client, Server server, ServerClient serverClient, ArrayList<String> args, CommandLog logs) {
        while (!args.isEmpty() && args.get(args.size() - 1).length() == 0) {
            args.remove(0);
        }
        ArrayList<Object> parses = new ArrayList<Object>();
        ArrayList<String> errors = new ArrayList<String>();
        int totalParams = 0;
        for (CmdParameter p : this.parameters) {
            totalParams += p.countParameters();
        }
        CmdParameter.ArgCounter argCounter = new CmdParameter.ArgCounter(totalParams, args.size());
        for (CmdParameter p : this.parameters) {
            if (p.parse(client, server, serverClient, args, parses, errors, argCounter, logs)) continue;
            return false;
        }
        this.runModular(client, server, serverClient, parses.toArray(), errors.toArray(new String[0]), logs);
        return true;
    }

    public abstract void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6);

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, String[] args) {
        return CmdParameter.autoComplete(client, server, serverClient, this.parameters, args);
    }

    @Override
    public String getCurrentUsage(Client client, Server server, ServerClient serverClient, String[] args) {
        return CmdParameter.getCurrentUsage(this, client, server, serverClient, this.parameters, args);
    }

    @Override
    public String getUsage() {
        return CmdParameter.getUsage(this.parameters);
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public boolean isCheat() {
        return this.isCheat;
    }
}

