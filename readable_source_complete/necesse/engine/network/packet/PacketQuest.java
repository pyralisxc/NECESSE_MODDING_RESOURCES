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
import necesse.engine.registries.QuestRegistry;

public class PacketQuest
extends Packet {
    public final int questID;
    public final boolean isNew;
    public final Quest quest;

    public PacketQuest(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questID = reader.getNextShortUnsigned();
        this.isNew = reader.getNextBoolean();
        this.quest = QuestRegistry.getNewQuest(this.questID);
        if (this.quest != null) {
            this.quest.applySpawnPacket(reader);
        }
    }

    public PacketQuest(Quest quest, boolean isNew) {
        this.questID = quest.getID();
        this.isNew = isNew;
        this.quest = quest;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(this.questID);
        writer.putNextBoolean(isNew);
        quest.setupSpawnPacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.quests.addQuest(this.quest, this.isNew);
    }
}

