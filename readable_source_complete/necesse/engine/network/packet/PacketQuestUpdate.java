/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuestRequest;
import necesse.engine.quest.Quest;

public class PacketQuestUpdate
extends Packet {
    public final int questUniqueID;
    public final Packet questContent;

    public PacketQuestUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
        this.questContent = reader.getNextContentPacket();
    }

    public PacketQuestUpdate(Quest quest) {
        this.questUniqueID = quest.getUniqueID();
        this.questContent = new Packet();
        quest.setupPacket(new PacketWriter(this.questContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.questUniqueID);
        writer.putNextContentPacket(this.questContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Quest quest = client.quests.getQuest(this.questUniqueID);
        if (quest != null) {
            quest.applyPacket(new PacketReader(this.questContent));
        } else {
            client.network.sendPacket(new PacketQuestRequest(this.questUniqueID));
        }
    }
}

