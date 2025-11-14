/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.RemovedHitHandler;
import necesse.entity.manager.SubmittedHitHandler;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class ClientSubmittedHits {
    private static final int INVALIDATE_TIMEOUT = 2000;
    private final EntityManager manager;
    private final Level level;
    private final LinkedList<SubmittedHit<Projectile>> projectileHits = new LinkedList();
    private final LinkedList<SubmittedHit<MobAbilityLevelEvent>> mobAbilityLevelEventHits = new LinkedList();

    public ClientSubmittedHits(EntityManager manager) {
        this.manager = manager;
        this.level = manager.level;
    }

    public void tick() {
        SubmittedHit<Projectile> first;
        while (!this.projectileHits.isEmpty() && !(first = this.projectileHits.getFirst()).isStillValid()) {
            this.projectileHits.removeFirst();
            if (((SubmittedHit)first).removedHandler == null) continue;
            ((SubmittedHit)first).removedHandler.onRemoved(first.client, first.attackerUniqueID, (Projectile)first.attacker, first.targetUniqueID, first.target);
        }
    }

    public void submitProjectileHit(ServerClient client, int projectileUniqueID, int targetUniqueID, SubmittedHitHandler<Projectile> hitHandler, RemovedHitHandler<Projectile> removedHandler) {
        SubmittedHit<Projectile> submittedHit = new SubmittedHit<Projectile>(projectileUniqueID, this.manager.projectiles.get(projectileUniqueID, true), targetUniqueID, client, hitHandler, removedHandler);
        if (!submittedHit.handleHit()) {
            this.projectileHits.add(submittedHit);
        }
    }

    public void submitNewProjectile(Projectile projectile) {
        this.handleNewAttacker(projectile, projectile.getUniqueID(), this.projectileHits.iterator());
    }

    public void submitMobAbilityLevelEventHit(ServerClient client, int eventUniqueID, int targetUniqueID, SubmittedHitHandler<MobAbilityLevelEvent> hitHandler, RemovedHitHandler<MobAbilityLevelEvent> removedHandler) {
        LevelEvent levelEvent = this.manager.events.get(eventUniqueID, true);
        MobAbilityLevelEvent toolItemEvent = levelEvent instanceof MobAbilityLevelEvent ? (MobAbilityLevelEvent)levelEvent : null;
        SubmittedHit<MobAbilityLevelEvent> submittedHit = new SubmittedHit<MobAbilityLevelEvent>(eventUniqueID, toolItemEvent, targetUniqueID, client, hitHandler, removedHandler);
        if (!submittedHit.handleHit()) {
            this.mobAbilityLevelEventHits.add(submittedHit);
        }
    }

    public void submitNewMobAbilityLevelEvent(MobAbilityLevelEvent event) {
        this.handleNewAttacker(event, event.getUniqueID(), this.mobAbilityLevelEventHits.iterator());
    }

    public void submitNewMob(Mob mob) {
        this.handleNewMob(mob, this.projectileHits.iterator());
    }

    private <T> void handleNewAttacker(T attacker, int attackerUniqueID, Iterator<SubmittedHit<T>> iterator) {
        while (iterator.hasNext()) {
            SubmittedHit<T> next = iterator.next();
            if (next.attackerUniqueID != attackerUniqueID) continue;
            next.attacker = attacker;
            if (!next.handleHit()) continue;
            iterator.remove();
        }
    }

    private <T> void handleNewMob(Mob mob, Iterator<SubmittedHit<T>> iterator) {
        while (iterator.hasNext()) {
            SubmittedHit<T> next = iterator.next();
            if (next.targetUniqueID != mob.getUniqueID()) continue;
            next.target = mob;
            if (!next.handleHit()) continue;
            iterator.remove();
        }
    }

    private class SubmittedHit<T> {
        public final int attackerUniqueID;
        public final int targetUniqueID;
        public final ServerClient client;
        public final long submitTime;
        private final SubmittedHitHandler<T> hitHandler;
        private final RemovedHitHandler<T> removedHandler;
        public T attacker;
        public Mob target;

        public SubmittedHit(int attackerUniqueID, T attacker, int targetUniqueID, ServerClient client, SubmittedHitHandler<T> hitHandler, RemovedHitHandler<T> removedHandler) {
            this.attackerUniqueID = attackerUniqueID;
            this.attacker = attacker;
            this.targetUniqueID = targetUniqueID;
            this.client = client;
            this.submitTime = ClientSubmittedHits.this.level.getWorldEntity().getTime();
            this.hitHandler = hitHandler;
            this.removedHandler = removedHandler;
            this.target = GameUtils.getLevelMob(targetUniqueID, ClientSubmittedHits.this.level);
        }

        public boolean isStillValid() {
            if (this.client.isDisposed()) {
                return false;
            }
            return this.submitTime + 2000L >= ClientSubmittedHits.this.level.getWorldEntity().getTime();
        }

        public boolean handleHit() {
            if (this.attacker != null && this.target != null) {
                this.hitHandler.handleHit(this.client, this.attacker, this.target);
                return true;
            }
            return false;
        }
    }
}

