/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.CommandPartyCustomAction;

public class CommandPartyDisbandAction
extends CommandPartyCustomAction {
    public CommandPartyDisbandAction(Container container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerClient client, ArrayList<HumanMob> mobs) {
        for (HumanMob mob : mobs) {
            mob.adventureParty.clear(true);
            if (mob.isSettlerWithinSettlement()) {
                mob.clearCommandsOrders(client);
                continue;
            }
            mob.commandGuard(null, mob.getX(), mob.getY());
            if (!(mob.getHealthPercent() <= 0.5f)) continue;
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(mob.getLevel().getServer());
            settlersData.returnToSettlement(mob, false);
        }
    }
}

