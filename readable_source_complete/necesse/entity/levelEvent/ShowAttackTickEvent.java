/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public abstract class ShowAttackTickEvent
extends LevelEvent {
    protected int attackSeed;
    protected int totalTicksPerAttack;
    protected float lastTicksAttackProgress;
    protected AttackAnimMob attackMob;
    protected SwordToolItem sword;

    public ShowAttackTickEvent() {
    }

    public ShowAttackTickEvent(AttackAnimMob attackMob, int attackSeed, int totalTicksPerAttack, SwordToolItem sword) {
        super(false);
        this.attackMob = attackMob;
        this.attackSeed = attackSeed;
        this.totalTicksPerAttack = totalTicksPerAttack;
        this.sword = sword;
    }

    @Override
    public void init() {
        super.init();
        if (this.attackMob == null) {
            this.over();
        }
    }

    @Override
    public void tickMovement(float delta) {
        float nextProgressDelta;
        super.tickMovement(delta);
        if (this.isOver()) {
            return;
        }
        if (!this.attackMob.isAttacking || this.attackMob.attackSeed != this.attackSeed) {
            this.over();
            return;
        }
        float nextProgress = this.attackMob.getAttackAnimProgress();
        float progressPerTick = 1.0f / (float)this.totalTicksPerAttack;
        while ((nextProgressDelta = nextProgress - this.lastTicksAttackProgress) >= progressPerTick) {
            this.tick(this.attackMob, this.lastTicksAttackProgress);
            this.lastTicksAttackProgress += progressPerTick;
        }
    }

    public abstract void tick(AttackAnimMob var1, float var2);
}

