/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketQuestTrack
extends Packet {
    public final int questUniqueID;
    public boolean tracked;

    public PacketQuestTrack(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.questUniqueID = reader.getNextInt();
        this.tracked = reader.getNextBoolean();
    }

    public PacketQuestTrack(int questUniqueID, boolean tracked) {
        this.questUniqueID = questUniqueID;
        this.tracked = tracked;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(questUniqueID);
        writer.putNextBoolean(tracked);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.setTrackedQuest(this.questUniqueID, this.tracked)) {
            client.sendPacket(new PacketQuestRemove(this.questUniqueID));
        }
    }
}

