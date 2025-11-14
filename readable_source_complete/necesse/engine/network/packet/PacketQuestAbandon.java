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

public class PacketQuestAbandon
extends Packet {
    public final int questUniqueID;

    public PacketQuestAbandon(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
    }

    public PacketQuestAbandon(int questUniqueID) {
        this.questUniqueID = questUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Quest quest = client.getQuest(this.questUniqueID);
        if (quest != null) {
            quest.abandonFor(server, client);
        } else {
            client.sendPacket(new PacketQuestRemove(this.questUniqueID));
        }
    }
}

