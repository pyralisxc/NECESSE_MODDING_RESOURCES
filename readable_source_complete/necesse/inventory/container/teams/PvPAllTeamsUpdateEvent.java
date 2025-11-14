/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PvPAllTeamsUpdateEvent
extends ContainerEvent {
    public final ArrayList<PvPTeamsContainer.TeamData> teams = new ArrayList();

    public PvPAllTeamsUpdateEvent(Server server) {
        for (PlayerTeam team : server.world.getTeams().getTeams()) {
            if (!team.hasMembers()) continue;
            this.teams.add(new PvPTeamsContainer.TeamData(team));
        }
    }

    public PvPAllTeamsUpdateEvent(PacketReader reader) {
        super(reader);
        int totalTeams = reader.getNextShortUnsigned();
        for (int i = 0; i < totalTeams; ++i) {
            this.teams.add(new PvPTeamsContainer.TeamData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextShortUnsigned(this.teams.size());
        for (PvPTeamsContainer.TeamData team : this.teams) {
            team.writeToPacket(writer);
        }
    }
}

