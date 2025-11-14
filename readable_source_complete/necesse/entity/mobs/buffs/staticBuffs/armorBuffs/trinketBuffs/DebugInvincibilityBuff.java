/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;

public class DebugInvincibilityBuff
extends Buff {
    public DebugInvincibilityBuff() {
        this.shouldSave = false;
        this.isPassive = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobBeforeDamageOverTimeTakenEvent.class, event -> {
            if (this.runDamageTaken(buff, event.getExpectedHealth())) {
                event.prevent();
            }
        });
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.isPlayer && !((PlayerMob)buff.owner).hasInvincibility) {
            buff.owner.buffManager.removeBuff(this, false);
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isClient()) {
            buff.owner.getClient().chat.addMessage(GameColor.ITEM_QUEST.getColorCode() + "Clients cannot have debug invincibility buff. Use the /invincibility command instead");
        }
        buff.owner.buffManager.removeBuff(this, false);
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        if (this.runDamageTaken(buff, event.getExpectedHealth())) {
            event.prevent();
        }
    }

    protected boolean runDamageTaken(ActiveBuff buff, int expectedHealth) {
        Level level = buff.owner.getLevel();
        if (level.isServer() && expectedHealth <= 0) {
            int health = GameRandom.globalRandom.getIntOffset(buff.owner.getMaxHealth() / 2, buff.owner.getMaxHealth() / 10);
            buff.owner.setHealth(Math.max(10, health));
            return true;
        }
        return false;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add("Used for testing purposes");
        return tooltips;
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new StaticMessage("Invincibility debug buff");
    }
}

