/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public interface SubmittedHitHandler<T> {
    public void handleHit(ServerClient var1, T var2, Mob var3);
}

