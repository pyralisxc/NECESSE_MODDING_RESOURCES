/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.ContainerCustomAction;

public class ContainerCustomActionRegistry {
    private final Container container;
    private boolean registryOpen = true;
    private ArrayList<ContainerCustomAction> actions = new ArrayList();

    public ContainerCustomActionRegistry(Container container) {
        this.container = container;
    }

    public void closeRegistry() {
        this.registryOpen = false;
    }

    public final void runAction(int id, PacketReader reader) {
        if (id < 0 || id >= this.actions.size()) {
            System.err.println("Could not find and run container custom action " + id);
        } else {
            this.actions.get(id).executePacket(reader);
        }
    }

    public final <T extends ContainerCustomAction> T registerAction(T action) {
        if (!this.registryOpen) {
            throw new IllegalStateException("Cannot register container custom actions after initialization, must be done in constructor.");
        }
        if (this.actions.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more container custom actions");
        }
        this.actions.add(action);
        action.onRegister(this.container, this.actions.size() - 1);
        return action;
    }
}

