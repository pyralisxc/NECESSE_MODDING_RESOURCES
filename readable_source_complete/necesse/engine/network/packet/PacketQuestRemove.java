/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;

public class PacketQuestRemove
extends Packet {
    public final int questUniqueID;

    public PacketQuestRemove(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
    }

    public PacketQuestRemove(int questUniqueID) {
        this.questUniqueID = questUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
    }

    public PacketQuestRemove(Quest quest) {
        this(quest.getUniqueID());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.quests.removeQuest(this.questUniqueID);
    }
}

