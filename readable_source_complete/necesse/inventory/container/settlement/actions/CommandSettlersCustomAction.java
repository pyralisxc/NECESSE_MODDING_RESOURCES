/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class CommandSettlersCustomAction
extends SettlementAccessRequiredContainerCustomAction {
    public CommandSettlersCustomAction(SettlementContainer container) {
        super(container);
    }

    protected PacketWriter setupPacket(Collection<Integer> mobUniqueIDs) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextShortUnsigned(mobUniqueIDs.size());
        for (int uniqueID : mobUniqueIDs) {
            writer.putNextInt(uniqueID);
        }
        return writer;
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int totalMobs = reader.getNextShortUnsigned();
        ArrayList<CommandMob> mobs = new ArrayList<CommandMob>(totalMobs);
        boolean sendUpdate = false;
        for (int i = 0; i < totalMobs; ++i) {
            int mobUniqueID = reader.getNextInt();
            Mob mob = data.getLevel().entityManager.mobs.get(mobUniqueID, false);
            if (mob instanceof CommandMob && ((CommandMob)((Object)mob)).canBeCommanded(client)) {
                mobs.add((CommandMob)((Object)mob));
                continue;
            }
            sendUpdate = true;
        }
        this.executePacket(reader, data, client, mobs);
        if (sendUpdate) {
            new SettlementSettlersChangedEvent(data).applyAndSendToClient(client);
        }
    }

    public abstract void executePacket(PacketReader var1, ServerSettlementData var2, ServerClient var3, ArrayList<CommandMob> var4);
}

