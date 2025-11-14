/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuildEntry;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuilds;
import necesse.engine.commands.serverCommands.setupCommand.DemoServerCommand;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class CharacterBuildsParameterHandler
extends ParameterHandler<CharacterBuilds> {
    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        String[] args = argument.arg.split(" ");
        if (argument.arg.endsWith(" ")) {
            args = GameUtils.concat(args, new String[]{""});
        }
        LinkedList<String> remainingBuilds = new LinkedList<String>(DemoServerCommand.builds.keySet());
        for (int i = 0; i < args.length; ++i) {
            String currentArg = args[i];
            ListIterator iterator = remainingBuilds.listIterator();
            boolean success = false;
            while (iterator.hasNext()) {
                String next = (String)iterator.next();
                if (!next.equals(currentArg)) continue;
                if (i == args.length - 1) {
                    return Collections.singletonList(new AutoComplete(1, next, true));
                }
                iterator.remove();
                success = true;
                break;
            }
            if (i < args.length - 1 && !success) {
                return Collections.emptyList();
            }
            if (success) continue;
            List<AutoComplete> nextCompletes = CharacterBuildsParameterHandler.autocompleteFromArray(remainingBuilds.toArray(new String[0]), null, null, new CmdArgument(argument.param, currentArg, argument.argCount));
            ArrayList<AutoComplete> finalCompletes = new ArrayList<AutoComplete>(nextCompletes.size());
            for (AutoComplete next : nextCompletes) {
                finalCompletes.add(new AutoComplete(1, next.newArgs, true));
            }
            return finalCompletes;
        }
        return Collections.emptyList();
    }

    @Override
    public CharacterBuilds parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        String[] args;
        ArrayList<CharacterBuildEntry> out = new ArrayList<CharacterBuildEntry>();
        for (String currentArg : args = arg.split(" ")) {
            boolean success = false;
            for (Map.Entry<String, CharacterBuild> entry : DemoServerCommand.builds.entrySet()) {
                if (!entry.getKey().equals(currentArg)) continue;
                out.add(new CharacterBuildEntry(entry.getKey(), entry.getValue()));
                success = true;
                break;
            }
            if (success) continue;
            throw new IllegalArgumentException("Could not find build with name \"" + currentArg + "\" for <" + parameter.name + ">");
        }
        return new CharacterBuilds(out.toArray(new CharacterBuildEntry[0]));
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public CharacterBuilds getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return new CharacterBuilds(new CharacterBuildEntry[0]);
    }

    @Override
    public int getArgsUsed() {
        return 10000;
    }
}

