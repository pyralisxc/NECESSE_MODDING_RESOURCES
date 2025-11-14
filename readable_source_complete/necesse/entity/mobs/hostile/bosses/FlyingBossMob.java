/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.mobs.hostile.bosses.BossMob;

public class FlyingBossMob
extends FlyingHostileMob {
    public FlyingBossMob(int health) {
        super(health);
        this.canDespawn = false;
        this.shouldSave = false;
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return false;
    }

    @Override
    public int getRespawnTime() {
        return BossMob.getBossRespawnTime(this);
    }
}

