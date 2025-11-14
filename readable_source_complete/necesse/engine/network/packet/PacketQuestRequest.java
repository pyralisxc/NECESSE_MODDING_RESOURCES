/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestRequest
extends Packet {
    public final int questUniqueID;

    public PacketQuestRequest(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
    }

    public PacketQuestRequest(int questUniqueID) {
        this.questUniqueID = questUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Quest quest = server.world.getQuests().getQuest(this.questUniqueID);
        if (quest != null) {
            client.sendPacket(new PacketQuest(quest, false));
        } else {
            client.sendPacket(new PacketQuestRemove(this.questUniqueID));
        }
    }
}

