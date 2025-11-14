/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;

public class PacketContainerAction
extends Packet {
    public final short containerSlot;
    public final ContainerAction action;
    public final int actionResult;

    public PacketContainerAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.containerSlot = reader.getNextShort();
        this.action = ContainerAction.getContainerAction(reader.getNextByteUnsigned());
        this.actionResult = reader.getNextInt();
    }

    public PacketContainerAction(int containerSlot, ContainerAction action, int actionResult) {
        this.containerSlot = (short)containerSlot;
        this.action = action;
        this.actionResult = actionResult;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShort(this.containerSlot);
        writer.putNextByteUnsigned(action.getID());
        writer.putNextInt(actionResult);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ContainerActionResult result = client.getContainer().applyContainerAction(this.containerSlot, this.action);
        if (result.value != this.actionResult) {
            GameLog.warn.println(client.getName() + " container action result did not match expected. Got: " + result.value + ", Expected: " + this.actionResult);
            client.getContainer().markFullDirty();
        }
    }
}

