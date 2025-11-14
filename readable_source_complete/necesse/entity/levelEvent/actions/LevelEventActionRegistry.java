/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.actions.LevelEventAction;

public class LevelEventActionRegistry {
    private final LevelEvent event;
    private boolean registryOpen = true;
    private ArrayList<LevelEventAction> actions = new ArrayList();

    public LevelEventActionRegistry(LevelEvent event) {
        this.event = event;
    }

    public void closeRegistry() {
        this.registryOpen = false;
    }

    public final void runAction(int id, PacketReader reader) {
        if (id < 0 || id >= this.actions.size()) {
            System.err.println("Could not find and run level event action " + id + " for " + this.event.toString());
        } else {
            this.actions.get(id).executePacket(reader);
        }
    }

    public final <T extends LevelEventAction> T registerAction(T action) {
        if (!this.registryOpen) {
            throw new IllegalStateException("Cannot register level event actions after initialization, must be done in constructor");
        }
        if (this.actions.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more level event actions for " + action.toString());
        }
        this.actions.add(action);
        action.onRegister(this.event, this.actions.size() - 1);
        return action;
    }
}

