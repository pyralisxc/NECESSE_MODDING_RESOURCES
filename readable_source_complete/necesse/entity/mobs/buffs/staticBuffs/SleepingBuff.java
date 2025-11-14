/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SleepingBuff
extends Buff {
    public SleepingBuff() {
        this.isVisible = false;
        this.shouldSave = false;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float manaRegen;
        GNDItemMap gndData = buff.getGndData();
        float regen = gndData.getFloat("regen");
        if (regen == 0.0f) {
            regen = Math.max((float)buff.owner.getMaxHealth() / 20.0f, 5.0f);
            gndData.setFloat("regen", regen);
        }
        if ((manaRegen = gndData.getFloat("manaRegen")) == 0.0f) {
            manaRegen = Math.max((float)buff.owner.getMaxMana() / 20.0f, 5.0f);
            gndData.setFloat("manaRegen", manaRegen);
        }
        buff.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, Float.valueOf(regen));
        buff.setModifier(BuffModifiers.MANA_REGEN_FLAT, Float.valueOf(manaRegen));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        ServerClient serverClient;
        super.serverTick(buff);
        if (buff.owner.isPlayer && buff.owner.isServer() && (serverClient = ((PlayerMob)buff.owner).getServerClient()) != null) {
            serverClient.refreshAFKTimer();
        }
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (!event.wasPrevented) {
            PlayerMob owner;
            ServerClient serverClient;
            if (buff.owner.isPlayer && buff.owner.isServer() && (serverClient = (owner = (PlayerMob)buff.owner).getServerClient()) != null) {
                serverClient.closeContainer(true);
            }
            buff.remove();
        }
    }
}

