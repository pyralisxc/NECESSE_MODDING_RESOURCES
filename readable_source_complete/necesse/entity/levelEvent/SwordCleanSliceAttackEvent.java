/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import necesse.entity.levelEvent.ShowAttackTickEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public abstract class SwordCleanSliceAttackEvent
extends ShowAttackTickEvent {
    public SwordCleanSliceAttackEvent() {
    }

    public SwordCleanSliceAttackEvent(AttackAnimMob attackMob, int attackSeed, int totalTicksPerAttack, SwordToolItem sword) {
        super(attackMob, attackSeed, totalTicksPerAttack, sword);
    }

    @Override
    public void tick(AttackAnimMob mob, float currentAttackProgress) {
        switch (this.attackMob.getDir()) {
            case 0: {
                this.tick(-90.0f, currentAttackProgress);
                break;
            }
            case 1: {
                this.tick(0.0f, currentAttackProgress);
                break;
            }
            case 2: {
                this.tick(90.0f, currentAttackProgress);
                break;
            }
            case 3: {
                this.tick(180.0f, currentAttackProgress);
            }
        }
    }

    public abstract void tick(float var1, float var2);
}

