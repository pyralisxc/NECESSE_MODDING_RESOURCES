/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PacketPlayerTeamInviteReply
extends Packet {
    public final int teamID;
    public final boolean accept;

    public PacketPlayerTeamInviteReply(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.teamID = reader.getNextInt();
        this.accept = reader.getNextBoolean();
    }

    public PacketPlayerTeamInviteReply(int teamID, boolean accept) {
        this.teamID = teamID;
        this.accept = accept;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(teamID);
        writer.putNextBoolean(accept);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        boolean sendUpdate = false;
        if (client.teamInvites.contains(this.teamID)) {
            client.teamInvites.remove(this.teamID);
            if (this.accept) {
                PlayerTeam team = server.world.getTeams().getTeam(this.teamID);
                if (team != null) {
                    PlayerTeam.addMember(server, team, client.authentication);
                } else {
                    sendUpdate = true;
                }
            } else {
                sendUpdate = true;
            }
        } else {
            sendUpdate = true;
        }
        if (sendUpdate) {
            PvPTeamsContainer.sendSingleUpdate(client);
        }
    }
}

