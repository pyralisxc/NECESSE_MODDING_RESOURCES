/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;

public interface Attacker {
    public static final int DEFAULT_RESPAWN_TIME = 5000;

    public GameMessage getAttackerName();

    public DeathMessageTable getDeathMessages();

    public Mob getFirstAttackOwner();

    default public Mob getAttackOwner() {
        LinkedList<Mob> ownerChain = this.getAttackOwnerChain();
        return ownerChain.isEmpty() ? this.getFirstAttackOwner() : ownerChain.getLast();
    }

    default public LinkedList<Mob> getAttackOwnerChain() {
        LinkedList<Mob> out = new LinkedList<Mob>();
        HashSet<Mob> ownersFound = new HashSet<Mob>();
        Mob last = this.getFirstAttackOwner();
        while (last != null) {
            out.addLast(last);
            ownersFound.add(last);
            Mob next = last.getFirstAttackOwner();
            if (next != null) {
                if (last == next || ownersFound.contains(next)) {
                    return out;
                }
                last = next;
                continue;
            }
            return out;
        }
        return out;
    }

    default public PlayerMob getFirstPlayerOwner() {
        HashSet<Mob> ownersFound = new HashSet<Mob>();
        Mob last = this.getFirstAttackOwner();
        while (last != null) {
            if (last.isPlayer) {
                return (PlayerMob)last;
            }
            ownersFound.add(last);
            Mob next = last.getFirstAttackOwner();
            if (next != null) {
                if (last == next || ownersFound.contains(next)) {
                    return null;
                }
                last = next;
                continue;
            }
            return null;
        }
        return null;
    }

    default public HashSet<Mob> getAttackOwners() {
        HashSet<Mob> ownersFound = new HashSet<Mob>();
        Mob last = this.getFirstAttackOwner();
        while (last != null) {
            ownersFound.add(last);
            Mob next = last.getFirstAttackOwner();
            if (next != null) {
                if (last == next || ownersFound.contains(next)) {
                    return ownersFound;
                }
                last = next;
                continue;
            }
            return ownersFound;
        }
        return ownersFound;
    }

    default public boolean isInAttackOwnerChain(Mob target) {
        return this.getAttackOwners().contains(target);
    }

    default public boolean removed() {
        Mob attackOwner = this.getAttackOwner();
        if (attackOwner != null) {
            return attackOwner.removed();
        }
        return false;
    }

    default public int getAttackerUniqueID() {
        Mob attackOwner = this.getAttackOwner();
        if (attackOwner != null) {
            return attackOwner.getUniqueID();
        }
        return -1;
    }

    default public int getRespawnTime() {
        return 5000;
    }

    default public DeathMessageTable getDeathMessages(String key, int count) {
        return DeathMessageTable.fromRange(key, count);
    }

    default public boolean isTrapAttacker() {
        return false;
    }

    default public void addAttackersToSet(HashSet<Attacker> attackers) {
        attackers.add(this);
        for (Mob attackOwner : this.getAttackOwners()) {
            if (attackOwner == this) continue;
            attackOwner.addAttackersToSet(attackers);
        }
    }

    default public Attacker getAttackerDamageProxy() {
        return this;
    }
}

