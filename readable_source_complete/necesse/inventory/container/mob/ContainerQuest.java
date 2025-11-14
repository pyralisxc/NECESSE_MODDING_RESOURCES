/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;

public class ContainerQuest {
    public GameMessage introMessage;
    public Quest quest;
    public boolean canSkip;
    public GameMessage skipError;

    public ContainerQuest(GameMessage introMessage, Quest quest, boolean canSkip, GameMessage skipError) {
        Objects.requireNonNull(quest);
        this.introMessage = introMessage;
        this.quest = quest;
        this.canSkip = canSkip;
        this.skipError = skipError;
    }

    public ContainerQuest(PacketReader reader) {
        this.introMessage = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
        if (reader.getNextBoolean()) {
            int questID = reader.getNextShortUnsigned();
            this.quest = QuestRegistry.getNewQuest(questID);
            this.quest.applySpawnPacket(reader);
            this.canSkip = reader.getNextBoolean();
            this.skipError = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
        } else {
            this.quest = null;
            this.canSkip = false;
            this.skipError = null;
        }
    }

    public void writePacket(PacketWriter writer) {
        if (this.introMessage != null) {
            writer.putNextBoolean(true);
            this.introMessage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
        if (this.quest != null) {
            writer.putNextBoolean(true);
            writer.putNextShortUnsigned(this.quest.getID());
            this.quest.setupSpawnPacket(writer);
            writer.putNextBoolean(this.canSkip);
            if (this.skipError != null) {
                writer.putNextBoolean(true);
                this.skipError.writePacket(writer);
            } else {
                writer.putNextBoolean(false);
            }
        } else {
            writer.putNextBoolean(false);
        }
    }
}

