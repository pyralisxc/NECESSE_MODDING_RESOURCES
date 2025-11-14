/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class InviteMembersAction
extends ContainerCustomAction {
    public final PvPTeamsContainer container;

    public InviteMembersAction(PvPTeamsContainer container) {
        this.container = container;
    }

    public void runAndSend(ClientClient ... clients) {
        Packet customContent = new Packet();
        PacketWriter writer = new PacketWriter(customContent);
        writer.putNextShortUnsigned(clients.length);
        for (ClientClient client : clients) {
            writer.putNextByteUnsigned(client.slot);
        }
        this.runAndSendAction(customContent);
    }

    @Override
    public void executePacket(PacketReader reader) {
        if (!this.container.client.isServer()) {
            return;
        }
        ServerClient serverClient = this.container.client.getServerClient();
        PlayerTeam playerTeam = serverClient.getPlayerTeam();
        if (playerTeam == null) {
            return;
        }
        int clients = reader.getNextShortUnsigned();
        for (int i = 0; i < clients; ++i) {
            int slot = reader.getNextByteUnsigned();
            ServerClient target = serverClient.getServer().getClient(slot);
            if (target == null) continue;
            PlayerTeam.invitePlayer(serverClient.getServer(), playerTeam, target);
        }
    }
}

