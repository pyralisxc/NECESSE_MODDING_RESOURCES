/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.mob.ContainerQuest;

public class ShopContainerQuestUpdateEvent
extends ContainerEvent {
    public final ArrayList<ContainerQuest> quests;

    public ShopContainerQuestUpdateEvent(ArrayList<ContainerQuest> quests) {
        this.quests = quests;
    }

    public ShopContainerQuestUpdateEvent(PacketReader reader) {
        super(reader);
        if (reader.getNextBoolean()) {
            int size = reader.getNextShortUnsigned();
            this.quests = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                this.quests.add(new ContainerQuest(reader));
            }
        } else {
            this.quests = null;
        }
    }

    @Override
    public void write(PacketWriter writer) {
        if (this.quests != null) {
            writer.putNextBoolean(true);
            writer.putNextShortUnsigned(this.quests.size());
            for (ContainerQuest quest : this.quests) {
                quest.writePacket(writer);
            }
        } else {
            writer.putNextBoolean(false);
        }
    }
}

