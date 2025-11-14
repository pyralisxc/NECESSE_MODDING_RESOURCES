/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.ClientInteractMob;
import necesse.inventory.container.Container;

public class MobContainer
extends Container {
    private final Mob mob;
    private ClientInteractMob interactMob;
    protected boolean ignoreInteractRange;

    public MobContainer(NetworkClient client, int uniqueSeed, Mob mob) {
        super(client, uniqueSeed);
        this.mob = mob;
        if (mob instanceof ClientInteractMob) {
            this.interactMob = (ClientInteractMob)((Object)mob);
        }
    }

    @Override
    public void init() {
        super.init();
        if (this.client.isServer() && this.interactMob != null) {
            this.interactMob.refreshInteracting(this.client.getServerClient());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isServer() && this.interactMob != null) {
            this.interactMob.refreshInteracting(this.client.getServerClient());
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.client.isServer() && this.interactMob != null) {
            this.interactMob.removeInteracting(this.client.getServerClient());
        }
    }

    public Mob getMob() {
        return this.mob;
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (this.mob.removed()) {
            return false;
        }
        return this.ignoreInteractRange || this.mob.inInteractRange(client.playerMob);
    }
}

