/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.Container;

public class PacketContainerCustomAction
extends Packet {
    public final int containerUniqueSeed;
    public final int actionID;
    public final Packet actionContent;

    public PacketContainerCustomAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.containerUniqueSeed = reader.getNextInt();
        this.actionID = reader.getNextShort();
        this.actionContent = reader.getNextContentPacket();
    }

    public PacketContainerCustomAction(Container container, int actionID, Packet content) {
        this.containerUniqueSeed = container.uniqueSeed;
        this.actionID = actionID;
        this.actionContent = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.containerUniqueSeed);
        writer.putNextShort((short)actionID);
        writer.putNextContentPacket(this.actionContent);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Container container = client.getContainer();
        if (container.uniqueSeed == this.containerUniqueSeed) {
            container.runCustomAction(this.actionID, new PacketReader(this.actionContent));
        } else {
            GameLog.warn.println("Received invalid container update for " + client.getName() + " with unique seed " + this.containerUniqueSeed);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Container container = client.getContainer();
        if (container != null) {
            if (container.uniqueSeed == this.containerUniqueSeed) {
                container.runCustomAction(this.actionID, new PacketReader(this.actionContent));
            } else {
                GameLog.warn.println("Received invalid container update with unique seed " + this.containerUniqueSeed);
            }
        }
    }
}

