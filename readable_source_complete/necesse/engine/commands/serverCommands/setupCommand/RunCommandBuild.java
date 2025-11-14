/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.network.server.ServerClient;

public class RunCommandBuild
extends CharacterBuild {
    public String command;

    public RunCommandBuild(String command) {
        this.command = command;
    }

    @Override
    public void apply(ServerClient client) {
        client.getServer().sendCommand(this.command, client);
    }
}

