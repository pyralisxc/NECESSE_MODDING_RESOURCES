/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.banners.logic;

import aphorea.items.banners.logic.AphBanner;
import aphorea.registry.AphModifiers;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class AphAbilityBanner
extends AphBanner {
    private final int abilityTicks;
    private float abilityCountTimer;

    public AphAbilityBanner(Item.Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks, int baseEffect, String ... extraToolTips) {
        super(rarity, range, buff, baseEffect, extraToolTips);
        this.abilityTicks = abilityTicks;
    }

    public AphAbilityBanner(Item.Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks, int baseEffect, String tooltips) {
        super(rarity, range, buff, baseEffect, tooltips + "effect", tooltips + "ability");
        this.abilityTicks = abilityTicks;
    }

    public int getAbilityTicks() {
        return this.abilityTicks;
    }

    public float getAbilityTicks(Mob mob) {
        float abilitySpeed = ((Float)mob.buffManager.getModifier(AphModifiers.INSPIRATION_ABILITY_SPEED)).floatValue();
        return (float)this.abilityTicks / abilitySpeed;
    }

    @Override
    public void tickHolding(InventoryItem item, PlayerMob player) {
        super.tickHolding(item, player);
        if (player.isServer() && this.abilityTicks != 0) {
            this.abilityCountTimer += 1.0f;
            if (this.abilityCountTimer > this.getAbilityTicks((Mob)player)) {
                this.runServerAbility(player.getLevel(), item, player);
                this.abilityCountTimer = 0.0f;
            }
        }
    }

    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        String[] effectReplacements = this.getEffectReplacements(perspective);
        String abilitySeconds = String.format("%.1f", Float.valueOf((float)this.getAbilityTicks() / 20.0f));
        if (this.extraToolTips.length == 0) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)(this.getStringID() + "effect"), (String[])effectReplacements));
            tooltips.add(Localization.translate((String)"itemtooltip", (String)(this.getStringID() + "ability"), (String)"time", (String)abilitySeconds));
            this.addExtraTooltips(tooltips, perspective);
        } else {
            for (String extraToolTip : this.extraToolTips) {
                tooltips.add(Localization.translate((String)"itemtooltip", (String)extraToolTip, (Object[])new Object[]{effectReplacements, "time", abilitySeconds}));
            }
        }
    }

    public void addExtraTooltips(ListGameTooltips tooltips, PlayerMob perspective) {
    }
}

