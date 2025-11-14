/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public abstract class AuraTrinketBuff
extends TrinketBuff {
    protected int auraRadius;

    public AuraTrinketBuff(int auraRadius) {
        this.auraRadius = auraRadius;
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.tickApplyBuffs(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickApplyBuffs(buff);
    }

    protected void tickApplyBuffs(ActiveBuff buff) {
        Mob mob = buff.owner;
        if (mob.isPlayer) {
            PlayerMob player = (PlayerMob)mob;
            GameUtils.streamNetworkClients(player.getLevel()).filter(c -> this.shouldBuffPlayer(player, c.playerMob)).filter(c -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double)this.auraRadius).forEach(c -> this.applyBuffs(c.playerMob));
            player.getLevel().entityManager.mobs.streamInRegionsInRange(player.x, player.y, this.auraRadius).filter(m -> !m.removed()).filter(m -> this.shouldBuffMob(player, (Mob)m)).filter(m -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), m.getX(), m.getY()) <= (double)this.auraRadius).forEach(this::applyBuffs);
        } else if (mob instanceof HumanMob) {
            HumanMob humanMob = (HumanMob)mob;
            GameUtils.streamNetworkClients(mob.getLevel()).filter(c -> this.settlerShouldBuffPlayer(humanMob, c.playerMob)).filter(c -> GameMath.diagonalMoveDistance(mob.getX(), mob.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double)this.auraRadius).forEach(c -> this.applyBuffs(c.playerMob));
            mob.getLevel().entityManager.mobs.streamInRegionsInRange(mob.x, mob.y, this.auraRadius).filter(m -> !m.removed()).filter(m -> this.settlerShouldBuffMob(humanMob, (Mob)m)).filter(m -> GameMath.diagonalMoveDistance(mob.getX(), mob.getY(), m.getX(), m.getY()) <= (double)this.auraRadius).forEach(this::applyBuffs);
        }
    }

    public boolean shouldBuffPlayer(PlayerMob from, PlayerMob target) {
        return from == target || from.isSameTeam(target);
    }

    public boolean shouldBuffMob(PlayerMob player, Mob target) {
        return target.isHuman && ((HumanMob)target).isFriendlyClient(player.getNetworkClient());
    }

    public boolean settlerShouldBuffPlayer(HumanMob from, PlayerMob target) {
        return from.isFriendlyClient(target.getNetworkClient());
    }

    public boolean settlerShouldBuffMob(HumanMob from, Mob target) {
        return from == target || target.isHuman && from.isFriendlyHuman((HumanMob)target);
    }

    protected abstract Buff getAuraBuff(Mob var1);

    public void applyBuffs(Mob mob) {
        Buff buff = this.getAuraBuff(mob);
        if (buff == null) {
            return;
        }
        ActiveBuff ab = new ActiveBuff(buff, mob, 100, null);
        mob.buffManager.addBuff(ab, false);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", this.getTooltipName()));
        return tooltips;
    }

    protected String getTooltipName() {
        return "ringoffiretip";
    }
}

