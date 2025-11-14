/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.CommandSettlersCustomAction;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersFollowMeAction
extends CommandSettlersCustomAction {
    public CommandSettlersFollowMeAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client, ArrayList<CommandMob> mobs) {
        for (CommandMob mob : mobs) {
            mob.commandFollow(client, client.playerMob);
        }
    }
}

