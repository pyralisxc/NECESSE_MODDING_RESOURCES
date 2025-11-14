/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.commands.serverCommands.setupCommand.DemoServerCommand;
import necesse.engine.network.server.ServerClient;

public class CharacterBuildConcat
extends CharacterBuild {
    public final ArrayList<CharacterBuild> builds;
    public final ArrayList<String> buildIDs = new ArrayList();

    public CharacterBuildConcat(Object ... builds) {
        this.builds = new ArrayList();
        for (Object build : builds) {
            if (build instanceof CharacterBuild) {
                this.builds.add((CharacterBuild)build);
                continue;
            }
            if (build instanceof String) {
                this.buildIDs.add((String)build);
                continue;
            }
            throw new IllegalArgumentException("Build parameters must either be CharacterBuild or String");
        }
    }

    @Override
    public void apply(ServerClient client) {
    }

    @Override
    public void addApplies(List<CharacterBuild> applies) {
        for (CharacterBuild build : this.builds) {
            build.addApplies(applies);
        }
        for (String buildID : this.buildIDs) {
            CharacterBuild build = DemoServerCommand.builds.get(buildID);
            if (build == null) continue;
            build.addApplies(applies);
        }
    }
}

