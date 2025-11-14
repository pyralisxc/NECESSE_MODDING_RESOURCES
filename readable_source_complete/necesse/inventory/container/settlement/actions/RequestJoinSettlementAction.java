/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RequestJoinSettlementAction
extends BooleanCustomAction {
    public final SettlementDependantContainer container;

    public RequestJoinSettlementAction(SettlementDependantContainer container) {
        this.container = container;
    }

    @Override
    protected void run(boolean assumedIsPublic) {
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                long ownerAuth = serverData.networkData.getOwnerAuth();
                if (ownerAuth != -1L) {
                    PlayerTeam team;
                    int teamID = serverData.networkData.getTeamID();
                    PlayerTeam playerTeam = team = teamID == -1 ? null : client.getServer().world.getTeams().getTeam(teamID);
                    if (team == null) {
                        ServerClient ownerClient;
                        if (assumedIsPublic) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client);
                        }
                        if ((ownerClient = client.getServer().getClientByAuth(ownerAuth)) != null) {
                            ownerClient.joinRequests.add(client.authentication);
                            ownerClient.sendPacket(new PacketPlayerTeamRequestReceive(client.authentication, client.getName()));
                        } else {
                            client.sendChatMessage(new LocalMessage("ui", "teamownerisnotonline"));
                        }
                    } else {
                        if (team.isPublic()) {
                            PlayerTeam.addMember(client.getServer(), team, client.authentication);
                        } else {
                            PlayerTeam.addJoinRequest(client.getServer(), team, client.authentication);
                        }
                        if (team.isPublic() != assumedIsPublic) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client);
                        }
                    }
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

