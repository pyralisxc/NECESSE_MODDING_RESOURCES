/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.InteractingClients;

public interface ClientInteractMob {
    public InteractingClients getInteractingClients();

    default public boolean isBeingInteractedWith() {
        return !this.getInteractingClients().isEmpty();
    }

    default public void refreshInteracting(ServerClient client) {
        this.getInteractingClients().refresh(client);
    }

    default public void removeInteracting(ServerClient client) {
        this.getInteractingClients().remove(client);
    }
}

