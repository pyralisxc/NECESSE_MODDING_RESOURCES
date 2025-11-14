/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class OverchargedManaBuff
extends Buff {
    protected GameTexture chargingUp;
    protected GameTexture chargedUp;
    protected GameTexture fullyCharged;

    public OverchargedManaBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 20;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public int getStacksDisplayCount(ActiveBuff buff) {
        return 1;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return true;
    }

    @Override
    public String getDurationText(ActiveBuff buff) {
        return Integer.toString(buff.getStacks());
    }

    @Override
    public GameTexture getDrawIcon(ActiveBuff buff) {
        int stacks = buff.getStacks();
        if (stacks >= 10 && stacks < this.getStackSize(buff)) {
            return this.chargedUp;
        }
        if (stacks == this.getStackSize(buff)) {
            return this.fullyCharged;
        }
        return this.chargingUp;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        try {
            this.chargedUp = GameTexture.fromFileRaw("buffs/chargedup");
            this.chargingUp = GameTexture.fromFileRaw("buffs/chargingup");
            this.fullyCharged = GameTexture.fromFileRaw("buffs/fullycharged");
        }
        catch (FileNotFoundException e) {
            this.chargedUp = GameTexture.fromFile("buffs/unknown");
            this.chargingUp = GameTexture.fromFile("buffs/unknown");
            this.fullyCharged = GameTexture.fromFile("buffs/unknown");
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("buff", "overchargedmana"));
        int stacks = ab.getStacks();
        String tooltipText = stacks >= 10 && stacks < this.getStackSize(ab) ? Localization.translate("bufftooltip", "manacharged") : (stacks == this.getStackSize(ab) ? Localization.translate("bufftooltip", "fullycharged") : Localization.translate("bufftooltip", "chargingmana"));
        tooltips.add(tooltipText, 300);
        return tooltips;
    }

    public static class ArcanicHelmetAttacker
    implements Attacker {
        private final Mob owner;

        public ArcanicHelmetAttacker(Mob owner) {
            this.owner = owner;
        }

        @Override
        public GameMessage getAttackerName() {
            return this.owner.getAttackerName();
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.owner.getDeathMessages();
        }

        @Override
        public Mob getFirstAttackOwner() {
            return this.owner;
        }
    }
}

