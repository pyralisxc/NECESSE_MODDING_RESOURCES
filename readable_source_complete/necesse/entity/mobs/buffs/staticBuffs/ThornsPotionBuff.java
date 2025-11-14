/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ThornsPotionBuff
extends Buff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (!event.wasPrevented && buff.owner.isServer()) {
            boolean hasOwnerInChain;
            Mob attackOwner = event.attacker != null ? event.attacker.getAttackOwner() : null;
            boolean bl = hasOwnerInChain = event.attacker != null && event.attacker.isInAttackOwnerChain(buff.owner);
            if (attackOwner != null && !hasOwnerInChain) {
                float dx = attackOwner.getX() - buff.owner.getX();
                float dy = attackOwner.getY() - buff.owner.getY();
                float damage = event.damage;
                if (attackOwner.isPlayer) {
                    damage /= 2.0f;
                }
                attackOwner.isServerHit(new GameDamage(damage, 0.0f), dx, dy, 50.0f, buff.owner);
            }
        }
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", this.getStringID());
    }
}

