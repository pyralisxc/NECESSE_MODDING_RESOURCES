/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.CommandPartyCustomAction;

public class CommandPartyFollowMeAction
extends CommandPartyCustomAction {
    public CommandPartyFollowMeAction(Container container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerClient client, ArrayList<HumanMob> mobs) {
        for (HumanMob mob : mobs) {
            mob.commandFollow(client, client.playerMob);
        }
    }
}

