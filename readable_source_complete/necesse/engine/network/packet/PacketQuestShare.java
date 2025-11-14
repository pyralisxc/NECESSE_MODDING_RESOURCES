/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.packet.PacketQuestShareReceive;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestShare
extends Packet {
    public final int questUniqueID;

    public PacketQuestShare(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
    }

    public PacketQuestShare(int questUniqueID) {
        this.questUniqueID = questUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Quest quest = client.getQuest(this.questUniqueID);
        if (quest != null) {
            AtomicInteger counter = new AtomicInteger();
            if (quest.canShare()) {
                server.streamClients().filter(him -> client != him && !him.hasQuest(quest) && quest.canShareWith(client, (ServerClient)him)).forEach(c -> {
                    c.questInvites.put(quest.getUniqueID(), client);
                    c.sendPacket(new PacketQuestShareReceive(client, quest));
                    counter.incrementAndGet();
                });
            }
            if (counter.get() == 1) {
                client.sendChatMessage(new LocalMessage("quests", "shareresultsingle", "quest", quest.getTitle(), "count", counter.get()));
            } else {
                client.sendChatMessage(new LocalMessage("quests", "shareresultplural", "quest", quest.getTitle(), "count", counter.get()));
            }
        } else {
            client.sendPacket(new PacketQuestRemove(this.questUniqueID));
        }
    }
}

