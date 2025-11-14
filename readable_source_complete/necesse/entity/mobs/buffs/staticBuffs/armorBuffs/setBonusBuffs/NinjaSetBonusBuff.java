/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.NinjaShadowParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class NinjaSetBonusBuff
extends SetBonusBuff {
    public NinjaSetBonusBuff() {
        this.isVisible = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ATTACK_MOVEMENT_MOD, Float.valueOf(0.0f));
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if ((owner.dx != 0.0f || owner.dy != 0.0f) && owner.getLevel().tickManager().getTotalTicks() % 3L == 0L && !owner.isRiding() && owner.isPlayer) {
            owner.getLevel().entityManager.addParticle(new NinjaShadowParticle(owner.getLevel(), (PlayerMob)owner, buff == null), Particle.GType.COSMETIC);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "ninjaset1"), GameColor.ITEM_RARE));
        tooltips.add(Localization.translate("itemtooltip", "ninjaset2"));
        return tooltips;
    }
}

