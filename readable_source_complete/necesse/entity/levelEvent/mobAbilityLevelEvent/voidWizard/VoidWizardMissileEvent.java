/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.projectile.VoidWizardMissileProjectile;

public class VoidWizardMissileEvent
extends MobAbilityLevelEvent {
    private int tickCounter;
    private int targetID;
    private VoidWizard wizard;
    private Mob target;

    public VoidWizardMissileEvent() {
    }

    public VoidWizardMissileEvent(Mob owner, Mob target) {
        super(owner, GameRandom.globalRandom);
        this.target = target;
        this.targetID = target != null ? target.getUniqueID() : -1;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.targetID);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetID = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        if (this.owner == null) {
            return;
        }
        this.tickCounter = 0;
        this.target = GameUtils.getLevelMob(this.targetID, this.level);
        if (this.target == null) {
            GameLog.warn.println("Could not find target for dungeon wizard attack missile event, server level: " + this.isServer());
            this.over();
        }
        if (this.owner instanceof VoidWizard) {
            this.wizard = (VoidWizard)this.owner;
            this.wizard.swingAttack = true;
        }
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.owner == null || this.owner.removed() || this.tickCounter > 60) {
            this.over();
            return;
        }
        if (this.tickCounter % 6 == 0 && this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.owner == null || this.owner.removed() || !this.target.isSamePlace(this.owner) || this.tickCounter > 60) {
            this.over();
            return;
        }
        if (this.tickCounter % 6 == 0) {
            if (this.wizard != null) {
                this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
                this.wizard.playBoltSoundAbility.runAndSend(1.0f, 1.3f);
            }
            VoidWizardMissileProjectile p = new VoidWizardMissileProjectile(this.level, this.owner, this.target, VoidWizard.missile);
            this.level.entityManager.projectiles.add(p);
        }
    }
}

