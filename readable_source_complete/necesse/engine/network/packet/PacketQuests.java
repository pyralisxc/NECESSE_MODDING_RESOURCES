/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;

public class PacketQuests
extends Packet {
    public PacketQuests(byte[] data) {
        super(data);
    }

    public PacketQuests(HashMap<Quest, Boolean> questsAndTracked) {
        PacketWriter writer = new PacketWriter(this);
        for (Map.Entry<Quest, Boolean> entry : questsAndTracked.entrySet()) {
            Quest quest = entry.getKey();
            writer.putNextShortUnsigned(quest.getID());
            quest.setupSpawnPacket(writer);
            writer.putNextBoolean(entry.getValue());
        }
    }

    public void readQuests(BiConsumer<Quest, Boolean> consumer) {
        PacketReader reader = new PacketReader(this);
        while (reader.hasNext()) {
            int questID = reader.getNextShortUnsigned();
            Quest quest = QuestRegistry.getNewQuest(questID);
            quest.applySpawnPacket(reader);
            boolean isTracked = reader.getNextBoolean();
            consumer.accept(quest, isTracked);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.loading.questsPhase.submitQuestsPacket(this);
    }
}

