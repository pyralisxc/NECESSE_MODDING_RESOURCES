/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.polymorph;

import java.io.FileNotFoundException;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PolymorphMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class PolymorphBuff
extends Buff {
    protected String polymorphMobID;

    public PolymorphBuff(String polymorphMobID) {
        this.isImportant = true;
        this.canCancel = false;
        this.polymorphMobID = polymorphMobID;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        Mob owner = buff.owner;
        Level level = owner.getLevel();
        if (owner.isServer()) {
            if (owner.isMounted()) {
                return;
            }
            Mob mount = owner.getMount();
            if (mount instanceof PolymorphMob) {
                this.refreshDurationOnExistingMob((PolymorphMob)mount, buff.getDurationLeft());
            } else {
                this.spawnAndSetNewChicken(level, owner, buff.getDurationLeft());
            }
        }
    }

    private void refreshDurationOnExistingMob(PolymorphMob mob, int duration) {
        mob.removeAtTime = mob.getTime() + (long)duration;
    }

    private void spawnAndSetNewChicken(Level level, Mob target, int duration) {
        PolymorphMob polyMob = (PolymorphMob)MobRegistry.getMob(this.polymorphMobID, level);
        polyMob.removeAtTime = level.getTime() + (long)duration;
        polyMob.setPos(target.x, target.y, true);
        polyMob.dx = target.dx;
        polyMob.dy = target.dy;
        target.mount(polyMob, true, target.x, target.y, true);
        level.entityManager.mobs.add(polyMob);
        level.getServer().network.sendToClientsWithEntity(new PacketMobMount(target.getUniqueID(), polyMob.getUniqueID(), true, target.x, target.y), target);
    }

    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/polymorph");
        }
        catch (FileNotFoundException e) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
        }
    }
}

