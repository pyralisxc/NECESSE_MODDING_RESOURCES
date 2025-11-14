/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.CommandPartyCustomAction;

public class CommandPartyAttackAction
extends CommandPartyCustomAction {
    public CommandPartyAttackAction(Container container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs, Mob target) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        writer.putNextInt(target.getUniqueID());
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerClient client, ArrayList<HumanMob> mobs) {
        int targetUniqueID = reader.getNextInt();
        Mob targetMob = GameUtils.getLevelMob(targetUniqueID, client.getLevel());
        if (targetMob != null) {
            for (HumanMob mob : mobs) {
                mob.commandAttack(client, targetMob);
            }
        } else {
            client.sendPacket(new PacketRemoveMob(targetUniqueID));
        }
    }
}

