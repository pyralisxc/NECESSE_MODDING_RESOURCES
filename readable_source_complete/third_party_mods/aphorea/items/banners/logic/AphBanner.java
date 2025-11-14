/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.miscItem.BannerItem
 *  necesse.level.maps.Level
 */
package aphorea.items.banners.logic;

import aphorea.buffs.Banners.AphBannerBuff;
import aphorea.registry.AphModifiers;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.BannerItem;
import necesse.level.maps.Level;

public class AphBanner
extends BannerItem {
    public float[] baseEffect;
    public String[] extraToolTips;
    protected boolean addFloatReplacements;

    public AphBanner(Item.Rarity rarity, int range, Function<Mob, Buff> buff, float[] baseEffect, String ... extraToolTips) {
        super(rarity, range, buff);
        this.baseEffect = baseEffect;
        this.extraToolTips = extraToolTips;
        this.addFloatReplacements = false;
    }

    public AphBanner(Item.Rarity rarity, int range, Function<Mob, Buff> buff, float baseEffect, String ... extraToolTips) {
        this(rarity, range, buff, new float[]{baseEffect}, extraToolTips);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        this.addToolTips(tooltips, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"inspiration"));
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        String[] effectReplacements = this.getEffectReplacements(perspective);
        if (this.extraToolTips.length == 0) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)(this.getStringID() + "effect"), (String[])effectReplacements));
        } else {
            for (String extraToolTip : this.extraToolTips) {
                tooltips.add(Localization.translate((String)"itemtooltip", (String)extraToolTip, (String[])effectReplacements));
            }
        }
    }

    public String[] getEffectReplacements(PlayerMob perspective) {
        ArrayList<String> replacements = new ArrayList<String>();
        for (int i = 0; i < this.baseEffect.length; ++i) {
            replacements.add("effect" + (i == 0 ? "" : Integer.valueOf(i + 1)));
            float value = this.baseEffect[i] * (perspective == null ? (Float)AphModifiers.INSPIRATION_EFFECT.defaultBuffManagerValue : (Float)perspective.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)).floatValue();
            replacements.add(String.format("%.0f", Math.floor(value)));
            if (!this.addFloatReplacements) continue;
            replacements.add("effectfloat" + (i == 0 ? "" : Integer.valueOf(i + 1)));
            replacements.add(String.format("%.2f", Float.valueOf(value)));
        }
        return replacements.toArray(new String[0]);
    }

    public void tickHolding(InventoryItem item, PlayerMob player) {
        assert (player != null);
        GameUtils.streamNetworkClients((Level)player.getLevel()).filter(c -> this.shouldBuffPlayer(item, player, c.playerMob)).filter(c -> GameMath.diagonalMoveDistance((int)player.getX(), (int)player.getY(), (int)c.playerMob.getX(), (int)c.playerMob.getY()) <= (double)this.getPlayerRange()).forEach(c -> this.applyBuffs((Mob)c.playerMob, player));
        player.getLevel().entityManager.mobs.streamInRegionsInRange(player.x, player.y, this.getPlayerRange()).filter(m -> !m.removed()).filter(m -> this.shouldBuffMob(item, player, (Mob)m)).filter(m -> GameMath.diagonalMoveDistance((int)player.getX(), (int)player.getY(), (int)m.getX(), (int)m.getY()) <= (double)this.getPlayerRange()).forEach(m -> this.applyBuffs((Mob)m, player));
    }

    public void applyBuffs(Mob mob) {
        Buff buff = (Buff)this.buff.apply(mob);
        if (buff != null) {
            Attacker attacker;
            if (mob.buffManager.hasBuff(buff.getID()) && (attacker = mob.buffManager.getBuff(buff.getID()).getAttacker()) != null && attacker.getAttackOwner() != null) {
                return;
            }
            mob.buffManager.addBuff(new ActiveBuff(buff, mob, 100, null), false);
        }
    }

    public void applyBuffs(Mob mob, PlayerMob player) {
        Buff buff = (Buff)this.buff.apply(mob);
        if (buff != null) {
            ActiveBuff newBuff = new ActiveBuff(buff, mob, 100, (Attacker)player);
            if (mob.buffManager.hasBuff(newBuff.buff.getID())) {
                ActiveBuff antBuff = mob.buffManager.getBuff(newBuff.buff.getID());
                if (antBuff != null && antBuff.buff instanceof AphBannerBuff) {
                    if (AphBannerBuff.shouldChange(antBuff, newBuff)) {
                        this.addBuff(newBuff, mob, true);
                    }
                } else {
                    this.addBuff(newBuff, mob, true);
                }
            } else {
                this.addBuff(newBuff, mob, false);
            }
        }
    }

    public void addBuff(ActiveBuff ab, Mob mob, boolean forceOverride) {
        mob.buffManager.addBuff(ab, false, forceOverride);
    }

    public int getPlayerRange() {
        return this.range * 2;
    }

    public AphBanner addFloatReplacements(boolean addFloatReplacements) {
        this.addFloatReplacements = addFloatReplacements;
        return this;
    }
}

