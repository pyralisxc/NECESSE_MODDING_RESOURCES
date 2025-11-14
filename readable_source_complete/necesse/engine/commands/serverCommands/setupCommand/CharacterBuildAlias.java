/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.List;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.commands.serverCommands.setupCommand.DemoServerCommand;
import necesse.engine.network.server.ServerClient;

public class CharacterBuildAlias
extends CharacterBuild {
    public final String buildID;

    public CharacterBuildAlias(String buildID) {
        this.buildID = buildID;
    }

    @Override
    public void apply(ServerClient client) {
    }

    @Override
    public void addApplies(List<CharacterBuild> applies) {
        CharacterBuild build = DemoServerCommand.builds.get(this.buildID);
        if (build != null) {
            build.addApplies(applies);
        }
    }
}

