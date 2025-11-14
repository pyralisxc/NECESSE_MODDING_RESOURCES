/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.events.AdventurePartyChangedEvent;

public abstract class CommandPartyCustomAction
extends ContainerCustomAction {
    public final Container container;

    public CommandPartyCustomAction(Container container) {
        this.container = container;
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

    protected boolean preCheckIfCanExecute(ServerClient client) {
        return true;
    }

    protected void sendSyncUpdate(ServerClient client) {
        client.sendPacket(new PacketAdventurePartyUpdate(client));
        new AdventurePartyChangedEvent().applyAndSendToClient(client);
    }

    @Override
    public void executePacket(PacketReader reader) {
        if (!this.container.client.isServer()) {
            return;
        }
        ServerClient client = this.container.client.getServerClient();
        if (!this.preCheckIfCanExecute(client)) {
            return;
        }
        int totalMobs = reader.getNextShortUnsigned();
        ArrayList<HumanMob> mobs = new ArrayList<HumanMob>(totalMobs);
        boolean sendUpdate = false;
        for (int i = 0; i < totalMobs; ++i) {
            int mobUniqueID = reader.getNextInt();
            Mob mob = client.getLevel().entityManager.mobs.get(mobUniqueID, false);
            if (mob == null) {
                mob = client.adventureParty.getMemberMob(mobUniqueID);
            }
            if (mob instanceof HumanMob && ((HumanMob)mob).canBeCommanded(client)) {
                mobs.add((HumanMob)mob);
                continue;
            }
            sendUpdate = true;
        }
        this.executePacket(reader, client, mobs);
        if (sendUpdate) {
            this.sendSyncUpdate(client);
        }
    }

    public abstract void executePacket(PacketReader var1, ServerClient var2, ArrayList<HumanMob> var3);
}

