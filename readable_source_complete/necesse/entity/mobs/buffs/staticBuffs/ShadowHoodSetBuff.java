/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class ShadowHoodSetBuff
extends Buff {
    public ShadowHoodSetBuff() {
        this.shouldSave = false;
        this.canCancel = false;
        this.isPassive = true;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (!buff.owner.buffManager.hasBuff(BuffRegistry.SetBonuses.SHADOWHOOD.getID())) {
            buff.remove();
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (!buff.owner.buffManager.hasBuff(BuffRegistry.SetBonuses.SHADOWHOOD.getID())) {
            buff.remove();
        }
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new StaticMessage(this.getStringID());
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        return new ListGameTooltips(BuffModifiers.ALL_DAMAGE.getTooltip(Float.valueOf(0.1f), Float.valueOf(0.0f)).toTooltip(false));
    }
}

