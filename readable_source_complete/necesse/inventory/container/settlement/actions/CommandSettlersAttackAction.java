/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.CommandSettlersCustomAction;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersAttackAction
extends CommandSettlersCustomAction {
    public CommandSettlersAttackAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs, Mob target) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        writer.putNextInt(target.getUniqueID());
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client, ArrayList<CommandMob> mobs) {
        int targetUniqueID = reader.getNextInt();
        Mob targetMob = GameUtils.getLevelMob(targetUniqueID, client.getLevel());
        if (targetMob != null) {
            for (CommandMob mob : mobs) {
                mob.commandAttack(client, targetMob);
            }
        } else {
            client.sendPacket(new PacketRemoveMob(targetUniqueID));
        }
    }
}

