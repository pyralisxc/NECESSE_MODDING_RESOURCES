/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.localization.Localization;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class QuicksandStacksBuff
extends Buff {
    public QuicksandStacksBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.sortByDuration = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.updateModifiers(buff);
    }

    @Override
    public void onStacksUpdated(ActiveBuff buff, ActiveBuff other) {
        super.onStacksUpdated(buff, other);
        this.updateModifiers(buff);
    }

    public void updateModifiers(ActiveBuff buff) {
        float progress = (float)buff.getStacks() / (float)buff.getMaxStacks();
        buff.setMinModifier(BuffModifiers.SLOW, Float.valueOf(GameMath.lerp(progress, 0.0f, 0.9f)), 1000000);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.tickValid(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickValid(buff);
    }

    public void tickValid(ActiveBuff buff) {
        Mob owner = buff.owner;
        int tileX = owner.getTileX();
        int tileY = owner.getTileY();
        if (owner.getLevel().getTileID(tileX, tileY) != TileRegistry.quicksandID || owner.getLevel().getObject(tileX, tileY).overridesInLiquid(owner.getLevel(), tileX, tileY, owner.getX(), owner.getY()) || owner.isWaterWalking() || owner.isFlying()) {
            buff.remove();
        }
    }

    @Override
    public int getRemainingStacksDuration(ActiveBuff buff, AtomicBoolean sendUpdatePacket) {
        return 100;
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 100;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return true;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltip(ab, blackboard);
        tooltip.add(Localization.translate("bufftooltip", "quicksandtip"));
        return tooltip;
    }

    @Override
    public String getDurationText(ActiveBuff buff) {
        float percentage = (float)buff.getStacks() / (float)buff.getMaxStacks();
        return (int)(percentage * 100.0f) + "%";
    }

    @Override
    public int getStacksDisplayCount(ActiveBuff buff) {
        return 0;
    }
}

