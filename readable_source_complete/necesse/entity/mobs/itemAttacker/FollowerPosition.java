/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class FollowerPosition {
    public final int x;
    public final int y;
    public final Function<Mob, MobMovement> movementGetter;

    public FollowerPosition(int x, int y, Function<Mob, MobMovement> movementGetter) {
        this.x = x;
        this.y = y;
        this.movementGetter = movementGetter;
    }

    public FollowerPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.movementGetter = m -> new MobMovementRelative((Mob)m, x, y, true, false);
    }
}

