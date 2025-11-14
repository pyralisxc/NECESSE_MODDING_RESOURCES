/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketQuestShareReply;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class PacketQuestShareReceive
extends Packet {
    public final int sharerSlot;
    public final int questUniqueID;
    public final GameMessage questTitle;

    public PacketQuestShareReceive(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.sharerSlot = reader.getNextByteUnsigned();
        this.questUniqueID = reader.getNextInt();
        this.questTitle = GameMessage.fromContentPacket(reader.getNextContentPacket());
    }

    public PacketQuestShareReceive(ServerClient sharer, Quest quest) {
        this.questUniqueID = quest.getUniqueID();
        this.sharerSlot = sharer.slot;
        this.questTitle = quest.getTitle();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.sharerSlot);
        writer.putNextInt(this.questUniqueID);
        writer.putNextContentPacket(this.questTitle.getContentPacket());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        String sharerName;
        ClientClient sharer = client.getClient(this.sharerSlot);
        if (sharer != null) {
            sharerName = sharer.getName();
        } else {
            sharerName = "N/A";
            client.network.sendPacket(new PacketRequestPlayerData(this.sharerSlot));
        }
        FairType chatMsg = new FairType();
        chatMsg.append(ChatMessage.fontOptions, new LocalMessage("misc", "questinvite", "player", sharerName, "quest", this.questTitle).translate());
        chatMsg.append(ChatMessage.fontOptions, "\n");
        chatMsg.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
        chatMsg.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", e -> {
            if (e.getID() == -100 && !e.state) {
                client.network.sendPacket(new PacketQuestShareReply(this.questUniqueID, true));
                client.chat.removeMessagesIf(c -> c instanceof QuestShareMessage && ((QuestShareMessage)c).questUniqueID == this.questUniqueID);
                return true;
            }
            return false;
        }, null));
        chatMsg.append(ChatMessage.fontOptions, " ");
        chatMsg.append(ChatMessage.fontOptions, GameColor.RED.getColorCode());
        chatMsg.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "declinebutton") + "]", e -> {
            if (e.getID() == -100 && !e.state) {
                client.network.sendPacket(new PacketQuestShareReply(this.questUniqueID, false));
                client.chat.removeMessagesIf(c -> c instanceof QuestShareMessage && ((QuestShareMessage)c).questUniqueID == this.questUniqueID);
                return true;
            }
            return false;
        }, null));
        client.chat.addMessage(new QuestShareMessage(chatMsg.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.questUniqueID));
    }

    private static class QuestShareMessage
    extends ChatMessage {
        public final int questUniqueID;

        public QuestShareMessage(FairType type, int questUniqueID) {
            super(type);
            this.questUniqueID = questUniqueID;
        }
    }
}

