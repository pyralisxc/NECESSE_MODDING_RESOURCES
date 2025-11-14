/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuildEntry;
import necesse.engine.network.server.ServerClient;

class CharacterBuilds {
    public final CharacterBuildEntry[] builds;

    public CharacterBuilds(CharacterBuildEntry ... builds) {
        this.builds = builds;
    }

    public void apply(ServerClient target) {
        CharacterBuild.apply(target, this.builds);
    }
}

