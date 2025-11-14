/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.util.stream.Stream;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmiteLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.inventory.item.toolItem.swordToolItem.PerfectStormSwordToolItem;
import necesse.level.maps.Level;

public class PerfectStormBuff
extends Buff {
    protected static final float smiteCooldown = 0.4f;

    public PerfectStormBuff() {
        this.canCancel = false;
        this.isImportant = true;
        this.isVisible = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.owner.dx = 0.0f;
        buff.owner.dy = 0.0f;
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (event.wasPrevented) {
            return;
        }
        if (!this.canHit(event.target)) {
            return;
        }
        if (buff.owner.isClient()) {
            return;
        }
        if (event.attacker instanceof PerfectStormSwordToolItem.PerfectStormSwordAttacker) {
            SmiteLevelEvent smiteEvent = new SmiteLevelEvent(buff.owner, new GameRandom(event.target.getUniqueID()), event.target, new GameDamage(event.damage));
            buff.owner.getLevel().entityManager.addLevelEvent(smiteEvent);
        }
    }

    protected boolean canHit(Mob mob) {
        return !mob.removed() && (!mob.isHuman || mob instanceof TrainingDummyMob);
    }

    protected Stream<Mob> streamTargets(Level level, Mob ownerMob) {
        return Stream.concat(level.entityManager.mobs.getInRegionByTileRange(ownerMob.getTileX(), ownerMob.getTileY(), 3).stream(), GameUtils.streamServerClients(level).map(c -> c.playerMob));
    }
}

