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
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class PacketPlayerTeamRequestReceive
extends Packet {
    public final long auth;
    public final String name;

    public PacketPlayerTeamRequestReceive(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.auth = reader.getNextLong();
        this.name = reader.getNextString();
    }

    public PacketPlayerTeamRequestReceive(long auth, String name) {
        this.auth = auth;
        this.name = name;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(auth);
        writer.putNextString(name);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        FairType chatMsg = new FairType();
        chatMsg.append(ChatMessage.fontOptions, Localization.translate("misc", "teamrequest", "name", this.name));
        chatMsg.append(ChatMessage.fontOptions, "\n");
        chatMsg.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
        chatMsg.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", e -> {
            if (e.getID() == -100) {
                if (!e.state) {
                    client.network.sendPacket(new PacketPlayerTeamRequestReply(this.auth, true));
                    client.chat.removeMessagesIf(c -> c instanceof TeamRequestMessage && ((TeamRequestMessage)c).auth == this.auth);
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
                    client.network.sendPacket(new PacketPlayerTeamRequestReply(this.auth, false));
                    client.chat.removeMessagesIf(c -> c instanceof TeamRequestMessage && ((TeamRequestMessage)c).auth == this.auth);
                }
                return true;
            }
            return false;
        }, null));
        client.chat.removeMessagesIf(c -> c instanceof TeamRequestMessage && ((TeamRequestMessage)c).auth == this.auth);
        client.chat.addMessage(new TeamRequestMessage(chatMsg.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.auth));
    }

    private static class TeamRequestMessage
    extends ChatMessage {
        public final long auth;

        public TeamRequestMessage(FairType type, long auth) {
            super(type);
            this.auth = auth;
        }
    }
}

