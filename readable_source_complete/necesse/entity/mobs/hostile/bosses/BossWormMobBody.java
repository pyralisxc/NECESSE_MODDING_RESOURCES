/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import necesse.entity.mobs.hostile.HostileWormMobBody;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;

public class BossWormMobBody<T extends BossWormMobHead<B, T>, B extends BossWormMobBody<T, B>>
extends HostileWormMobBody<T, B> {
    public BossWormMobBody(int health) {
        super(health);
        this.canDespawn = false;
        this.shouldSave = false;
    }

    @Override
    public int getRespawnTime() {
        return BossMob.getBossRespawnTime(this);
    }
}

