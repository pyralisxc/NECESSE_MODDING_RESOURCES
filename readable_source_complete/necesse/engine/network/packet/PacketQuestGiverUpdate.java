/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.QuestGiver;

public class PacketQuestGiverUpdate
extends Packet {
    public final int mobUniqueID;
    public final int[] questUniqueIDs;

    public PacketQuestGiverUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.questUniqueIDs = new int[reader.getNextShortUnsigned()];
        for (int i = 0; i < this.questUniqueIDs.length; ++i) {
            this.questUniqueIDs[i] = reader.getNextInt();
        }
    }

    public PacketQuestGiverUpdate(int mobUniqueID, int[] questUniqueIDs) {
        this.mobUniqueID = mobUniqueID;
        this.questUniqueIDs = questUniqueIDs;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextShortUnsigned(questUniqueIDs.length);
        for (int questUniqueID : questUniqueIDs) {
            writer.putNextInt(questUniqueID);
        }
    }

    public PacketQuestGiverUpdate(QuestGiver questGiver, ServerClient client) {
        this(((Mob)((Object)questGiver)).getUniqueID(), questGiver.getQuestGiverObject().getRequestedQuests(client).stream().mapToInt(q -> q.questUniqueID).toArray());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof QuestGiver) {
            ((QuestGiver)((Object)mob)).applyQuestGiverUpdatePacket(this, client);
        }
    }
}

