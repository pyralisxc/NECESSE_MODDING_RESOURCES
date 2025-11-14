/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.MobAbility;

public class MobAbilityRegistry {
    private final Mob mob;
    private boolean registryOpen = true;
    private ArrayList<MobAbility> abilities = new ArrayList();

    public MobAbilityRegistry(Mob mob) {
        this.mob = mob;
    }

    public void closeRegistry() {
        this.registryOpen = false;
    }

    public final void runAbility(int id, PacketReader reader) {
        if (id < 0 || id >= this.abilities.size()) {
            System.err.println("Could not find and run ability " + id + " for " + this.mob.toString());
        } else {
            this.abilities.get(id).executePacket(reader);
        }
    }

    public final <T extends MobAbility> T registerAbility(T ability) {
        if (!this.registryOpen) {
            throw new IllegalStateException("Cannot register mob abilities after initialization, must be done in constructor");
        }
        if (this.abilities.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more mob abilities for " + this.mob.toString());
        }
        this.abilities.add(ability);
        ability.onRegister(this.mob, this.abilities.size() - 1);
        return ability;
    }
}

