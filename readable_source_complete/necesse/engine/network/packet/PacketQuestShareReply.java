/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestShareReply
extends Packet {
    public final int questUniqueID;
    public final boolean accepted;

    public PacketQuestShareReply(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
        this.accepted = reader.getNextBoolean();
    }

    public PacketQuestShareReply(int questUniqueID, boolean accepted) {
        this.questUniqueID = questUniqueID;
        this.accepted = accepted;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
        writer.putNextBoolean(accepted);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Quest quest = server.world.getQuests().getQuest(this.questUniqueID);
        if (quest != null) {
            if (client.questInvites.containsKey(this.questUniqueID)) {
                ServerClient from = client.questInvites.remove(this.questUniqueID);
                if (this.accepted) {
                    quest.onShared(server, from, client);
                }
            }
        } else {
            client.sendPacket(new PacketQuestRemove(this.questUniqueID));
        }
    }
}

