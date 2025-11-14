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
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class PacketPlayerTeamInviteReceive
extends Packet {
    public final int teamID;
    public final String teamName;

    public PacketPlayerTeamInviteReceive(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.teamID = reader.getNextInt();
        this.teamName = reader.getNextString();
    }

    public PacketPlayerTeamInviteReceive(int teamID, String teamName) {
        this.teamID = teamID;
        this.teamName = teamName;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(teamID);
        writer.putNextString(teamName);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        FairType chatMsg = new FairType();
        chatMsg.append(ChatMessage.fontOptions, Localization.translate("misc", "teaminvite", "team", this.teamName));
        chatMsg.append(ChatMessage.fontOptions, "\n");
        chatMsg.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
        chatMsg.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", e -> {
            if (e.getID() == -100) {
                if (!e.state) {
                    client.network.sendPacket(new PacketPlayerTeamInviteReply(this.teamID, true));
                    client.chat.removeMessagesIf(c -> c instanceof TeamInviteMessage && ((TeamInviteMessage)c).teamID == this.teamID);
                }
                return true;
            }
            return false;
        }, null));
        chatMsg.append(ChatMessage.fontOptions, " ");
        chatMsg.append(ChatMessage.fontOptions, GameColor.RED.getColorCode());
        chatMsg.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "declinebutton") + "]", e -> {
            if (e.getID() == -100) {
                if (!e.state) {
                    client.network.sendPacket(new PacketPlayerTeamInviteReply(this.teamID, false));
                    client.chat.removeMessagesIf(c -> c instanceof TeamInviteMessage && ((TeamInviteMessage)c).teamID == this.teamID);
                }
                return true;
            }
            return false;
        }, null));
        client.chat.removeMessagesIf(c -> c instanceof TeamInviteMessage && ((TeamInviteMessage)c).teamID == this.teamID);
        client.chat.addMessage(new TeamInviteMessage(chatMsg.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.teamID));
    }

    private static class TeamInviteMessage
    extends ChatMessage {
        public final int teamID;

        public TeamInviteMessage(FairType type, int teamID) {
            super(type);
            this.teamID = teamID;
        }
    }
}

