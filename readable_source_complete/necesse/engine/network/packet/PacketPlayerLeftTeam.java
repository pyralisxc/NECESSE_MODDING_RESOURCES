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

public class PacketPlayerLeftTeam
extends Packet {
    public final long auth;
    public final String name;
    public final int teamID;
    public final String teamName;
    public final boolean isKick;

    public PacketPlayerLeftTeam(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.auth = reader.getNextLong();
        this.name = reader.getNextString();
        this.teamID = reader.getNextShort();
        this.teamName = reader.getNextString();
        this.isKick = reader.getNextBoolean();
    }

    public PacketPlayerLeftTeam(Server server, long auth, PlayerTeam oldTeam, boolean isKick) {
        this.auth = auth;
        ServerClient client = server.getClientByAuth(auth);
        if (client != null) {
            this.name = client.getName();
            this.teamID = oldTeam.teamID;
            this.teamName = oldTeam.getName();
        } else {
            this.name = server.usedNames.getOrDefault(auth, "N/A");
            this.teamID = oldTeam.teamID;
            this.teamName = oldTeam.getName();
        }
        this.isKick = isKick;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(auth);
        writer.putNextString(this.name);
        writer.putNextShort((short)this.teamID);
        writer.putNextString(this.teamName);
        writer.putNextBoolean(isKick);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target;
        ClientClient me = client.getClient();
        if (!(me == null || me.authentication != this.auth && me.getTeamID() != this.teamID || this.teamName.isEmpty())) {
            client.chat.addMessage(Localization.translate("misc", this.isKick ? "teamkick" : "teamleft", "player", this.name, "team", this.teamName));
        }
        if ((target = client.getClientByAuth(this.auth)) != null) {
            target.setTeamID(-1);
        }
    }
}

