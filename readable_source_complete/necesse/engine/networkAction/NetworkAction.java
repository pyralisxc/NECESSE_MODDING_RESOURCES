/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.networkAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.networkAction.NetworkActionRegistry;

public abstract class NetworkAction<R> {
    private NetworkActionRegistry<R, ?> registry;
    private int id;

    public void onRegister(NetworkActionRegistry<R, ?> registry, int id) {
        if (this.registry != null) {
            throw new IllegalStateException(this.registry.actionCallName + " already registered");
        }
        this.registry = registry;
        this.id = id;
    }

    public R getRegistrar() {
        return this.registry.registrar;
    }

    public int getID() {
        return this.id;
    }

    public abstract void executePacket(PacketReader var1);

    protected void runAndSendAction(Packet content) {
        if (this.registry != null) {
            this.registry.runAndSendAction(this, content);
        } else {
            System.err.println("Cannot run action that's not registered");
        }
    }
}

