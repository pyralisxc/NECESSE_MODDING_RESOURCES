/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;

public abstract class MobChance
implements MobSpawnTable.CanSpawnPredicate,
MobSpawnTable.MobProducer,
MobSpawnTable.MobSpawner {
    public final int tickets;

    public MobChance(int tickets) {
        if (tickets <= 0) {
            throw new IllegalArgumentException("Tickets must be above 0");
        }
        this.tickets = tickets;
    }

    @Override
    public Collection<Mob> spawnMob(Level level, ServerClient client, Point spawnTile, Predicate<Mob> beforeTest, Consumer<Mob> beforeAdded, String purpose) {
        Mob mob = this.getMob(level, client, spawnTile);
        if (mob != null) {
            if (beforeTest != null && !beforeTest.test(mob)) {
                return null;
            }
            Point moveOffset = mob.getPathMoveOffset();
            if (mob.isValidSpawnLocation(level.getServer(), client, spawnTile.x * 32 + moveOffset.x, spawnTile.y * 32 + moveOffset.y)) {
                mob.onSpawned(spawnTile.x * 32 + moveOffset.x, spawnTile.y * 32 + moveOffset.y);
                if (beforeAdded != null) {
                    beforeAdded.accept(mob);
                }
                level.entityManager.mobs.add(mob);
                return Collections.singleton(mob);
            }
        }
        return null;
    }
}

