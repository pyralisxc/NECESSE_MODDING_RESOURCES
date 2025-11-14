/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.inventory.container.mob.ContainerExpedition;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ExpeditionList {
    public GameMessage selectDialogue;
    public GameMessage selectMessage;
    public GameMessage focusMessage;
    public GameMessage moreOptionsDialogue;
    public List<ContainerExpedition> expeditions;

    public ExpeditionList(GameMessage selectDialogue, GameMessage selectMessage, GameMessage focusMessage, GameMessage moreOptionsDialogue, List<ContainerExpedition> expeditions) {
        this.selectDialogue = selectDialogue;
        this.selectMessage = selectMessage;
        this.focusMessage = focusMessage;
        this.moreOptionsDialogue = moreOptionsDialogue;
        this.expeditions = expeditions;
    }

    public ExpeditionList(GameMessage selectDialogue, GameMessage selectMessage, GameMessage focusMessage, GameMessage moreOptionsDialogue, ServerSettlementData data, HumanShop mob, List<SettlerExpedition> expeditions) {
        this(selectDialogue, selectMessage, focusMessage, moreOptionsDialogue, ExpeditionList.convertToContainerExpeditions(data, mob, expeditions));
    }

    private static List<ContainerExpedition> convertToContainerExpeditions(ServerSettlementData data, HumanShop mob, List<SettlerExpedition> expeditions) {
        ArrayList<ContainerExpedition> out = new ArrayList<ContainerExpedition>(expeditions.size());
        long shopSeed = mob.getShopSeed();
        for (SettlerExpedition expedition : expeditions) {
            boolean available = expedition.isAvailable(data);
            if (available) {
                float successChance = expedition.getSuccessChance(data);
                int price = expedition.getCurrentCost(data, shopSeed);
                out.add(new ContainerExpedition(expedition, available, successChance, price));
                continue;
            }
            out.add(new ContainerExpedition(expedition));
        }
        return out;
    }

    public ExpeditionList(PacketReader reader) {
        this.selectDialogue = GameMessage.fromPacket(reader);
        this.selectMessage = GameMessage.fromPacket(reader);
        this.focusMessage = GameMessage.fromPacket(reader);
        this.moreOptionsDialogue = GameMessage.fromPacket(reader);
        int expeditionsLength = reader.getNextShortUnsigned();
        this.expeditions = new ArrayList<ContainerExpedition>(expeditionsLength);
        for (int i = 0; i < expeditionsLength; ++i) {
            this.expeditions.add(new ContainerExpedition(reader));
        }
    }

    public void writePacket(PacketWriter writer) {
        this.selectDialogue.writePacket(writer);
        this.selectMessage.writePacket(writer);
        this.focusMessage.writePacket(writer);
        this.moreOptionsDialogue.writePacket(writer);
        writer.putNextShortUnsigned(this.expeditions.size());
        for (ContainerExpedition expedition : this.expeditions) {
            expedition.writePacket(writer);
        }
    }
}

