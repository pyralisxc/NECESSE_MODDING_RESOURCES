/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class PacketPlayerJoinedTeam
extends Packet {
    public final long auth;
    public final String name;
    public final int teamID;
    public final String teamName;

    public PacketPlayerJoinedTeam(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.auth = reader.getNextLong();
        this.name = reader.getNextString();
        this.teamID = reader.getNextShort();
        this.teamName = reader.getNextString();
    }

    public PacketPlayerJoinedTeam(Server server, long auth) {
        PlayerTeam team;
        this.auth = auth;
        ServerClient client = server.getClientByAuth(auth);
        if (client != null) {
            this.name = client.getName();
            team = client.getPlayerTeam();
            if (team == null) {
                throw new IllegalStateException("Could not find team for " + client.getName());
            }
            this.teamID = team.teamID;
            this.teamName = team.getName();
        } else {
            this.name = server.usedNames.getOrDefault(auth, "N/A");
            team = server.world.getTeams().getPlayerTeam(auth);
            if (team == null) {
                throw new IllegalStateException("Could not find team for " + this.name + " auth " + auth);
            }
            this.teamID = team.teamID;
            this.teamName = team.getName();
        }
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(auth);
        writer.putNextString(this.name);
        writer.putNextShort((short)this.teamID);
        writer.putNextString(this.teamName);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target;
        ClientClient me = client.getClient();
        if (!(me == null || me.authentication != this.auth && me.getTeamID() != this.teamID || this.teamName.isEmpty())) {
            client.chat.addMessage(Localization.translate("misc", "teamjoin", "player", this.name, "team", this.teamName));
        }
        if ((target = client.getClientByAuth(this.auth)) != null) {
            target.setTeamID(this.teamID);
        }
    }
}

