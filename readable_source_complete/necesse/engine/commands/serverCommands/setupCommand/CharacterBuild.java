/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuildEntry;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.ServerClient;

public abstract class CharacterBuild
implements Comparable<CharacterBuild> {
    public final int applyPriority;

    public CharacterBuild(int applyPriority) {
        this.applyPriority = applyPriority;
    }

    public CharacterBuild() {
        this(0);
    }

    public abstract void apply(ServerClient var1);

    public void addApplies(List<CharacterBuild> applies) {
        applies.add(this);
    }

    @Override
    public int compareTo(CharacterBuild o) {
        return Integer.compare(this.applyPriority, o.applyPriority);
    }

    public static void apply(ServerClient target, CharacterBuildEntry ... builds) {
        ArrayList<CharacterBuild> sortedBuilds = new ArrayList<CharacterBuild>();
        for (CharacterBuildEntry entry : builds) {
            entry.build.addApplies(sortedBuilds);
        }
        sortedBuilds.sort(null);
        if (builds.length > 0) {
            for (CharacterBuild build : sortedBuilds) {
                build.apply(target);
            }
            if (target.hasSpawned()) {
                target.getServer().network.sendToAllClients(new PacketPlayerGeneral(target));
            }
        }
    }
}

