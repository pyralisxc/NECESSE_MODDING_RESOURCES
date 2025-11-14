/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketContainerCustomAction;
import necesse.inventory.container.Container;

public abstract class ContainerCustomAction {
    private Container container;
    private int id = -1;

    public void onRegister(Container container, int id) {
        if (this.container != null) {
            throw new IllegalStateException("Cannot register same custom action twice");
        }
        this.container = container;
        this.id = id;
    }

    protected Container getContainer() {
        return this.container;
    }

    public abstract void executePacket(PacketReader var1);

    protected void runAndSendAction(Packet content) {
        if (this.container != null) {
            if (this.container.client.isServer()) {
                this.container.client.getServerClient().sendPacket(new PacketContainerCustomAction(this.container, this.id, content));
            } else if (this.container.client.isClient()) {
                this.container.client.getClientClient().getClient().network.sendPacket(new PacketContainerCustomAction(this.container, this.id, content));
            }
            this.executePacket(new PacketReader(content));
        } else {
            System.err.println("Cannot run custom action that hasn't been registered");
        }
    }
}

