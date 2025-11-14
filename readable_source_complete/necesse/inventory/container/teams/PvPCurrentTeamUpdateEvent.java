/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PvPCurrentTeamUpdateEvent
extends ContainerEvent {
    public PvPTeamsContainer.TeamData currentTeam;
    public final ArrayList<PvPTeamsContainer.MemberData> members = new ArrayList();
    public final ArrayList<PvPTeamsContainer.MemberData> joinRequests = new ArrayList();
    public final ArrayList<PvPTeamsContainer.InviteData> invites = new ArrayList();

    public PvPCurrentTeamUpdateEvent(ServerClient client) {
        PlayerTeam playerTeam = client.getPlayerTeam();
        if (playerTeam != null) {
            this.currentTeam = new PvPTeamsContainer.TeamData(playerTeam);
            this.members.ensureCapacity(playerTeam.getMemberCount());
            for (Long memberAuth : playerTeam.getMembers()) {
                this.members.add(new PvPTeamsContainer.MemberData(client.getServer(), memberAuth));
            }
            this.joinRequests.ensureCapacity(playerTeam.getJoinRequestsCount());
            for (Long auth : playerTeam.getJoinRequests()) {
                this.joinRequests.add(new PvPTeamsContainer.MemberData(client.getServer(), auth));
            }
        }
        this.invites.ensureCapacity(client.teamInvites.size());
        for (Integer teamID : client.teamInvites) {
            PlayerTeam team = client.getServer().world.getTeams().getTeam(teamID);
            if (team == null) continue;
            this.invites.add(new PvPTeamsContainer.InviteData(team));
        }
    }

    public PvPCurrentTeamUpdateEvent(PacketReader reader) {
        super(reader);
        int i;
        if (reader.getNextBoolean()) {
            this.currentTeam = new PvPTeamsContainer.TeamData(reader);
            int totalMembers = reader.getNextShortUnsigned();
            for (i = 0; i < totalMembers; ++i) {
                this.members.add(new PvPTeamsContainer.MemberData(reader));
            }
            int totalJoinRequests = reader.getNextShortUnsigned();
            for (int i2 = 0; i2 < totalJoinRequests; ++i2) {
                this.joinRequests.add(new PvPTeamsContainer.MemberData(reader));
            }
        } else {
            this.currentTeam = null;
        }
        int totalInvites = reader.getNextShortUnsigned();
        for (i = 0; i < totalInvites; ++i) {
            this.invites.add(new PvPTeamsContainer.InviteData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        if (this.currentTeam != null) {
            writer.putNextBoolean(true);
            this.currentTeam.writeToPacket(writer);
            writer.putNextShortUnsigned(this.members.size());
            for (PvPTeamsContainer.MemberData member : this.members) {
                member.writeToPacket(writer);
            }
            writer.putNextShortUnsigned(this.joinRequests.size());
            for (PvPTeamsContainer.MemberData request : this.joinRequests) {
                request.writeToPacket(writer);
            }
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextShortUnsigned(this.invites.size());
        for (PvPTeamsContainer.InviteData invite : this.invites) {
            invite.writeToPacket(writer);
        }
    }
}

