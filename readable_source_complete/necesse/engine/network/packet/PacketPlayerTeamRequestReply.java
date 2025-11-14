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

public class PacketPlayerTeamRequestReply
extends Packet {
    public final long auth;
    public final boolean accept;

    public PacketPlayerTeamRequestReply(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.auth = reader.getNextLong();
        this.accept = reader.getNextBoolean();
    }

    public PacketPlayerTeamRequestReply(long auth, boolean accept) {
        this.auth = auth;
        this.accept = accept;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(auth);
        writer.putNextBoolean(accept);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        PlayerTeam team = client.getPlayerTeam();
        if (team == null) {
            if (client.joinRequests.contains(this.auth)) {
                team = server.world.getTeams().createNewTeam(client);
                PlayerTeam.addMember(server, team, this.auth);
            } else {
                PvPTeamsContainer.sendSingleUpdate(client);
            }
        } else if (team.hasJoinRequested(this.auth)) {
            if (this.accept) {
                PlayerTeam.addMember(server, team, this.auth);
            } else {
                PlayerTeam.removeJoinRequest(server, team, this.auth);
            }
        } else {
            PvPTeamsContainer.sendSingleUpdate(client);
        }
    }
}

