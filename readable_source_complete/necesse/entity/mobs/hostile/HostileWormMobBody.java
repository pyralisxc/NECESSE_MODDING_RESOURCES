/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.hostile.HostileWormMobHead;

public class HostileWormMobBody<T extends HostileWormMobHead<B, T>, B extends HostileWormMobBody<T, B>>
extends WormMobBody<T, B> {
    public HostileWormMobBody(int health) {
        super(health);
        this.isHostile = true;
        this.setTeam(-2);
    }
}

