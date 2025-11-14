/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class AmethystSwordToolItem
extends SwordToolItem {
    public AmethystSwordToolItem() {
        super(1000, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(56.0f).setUpgradedValue(1.0f, 93.33336f);
        this.attackRange.setBaseValue(80);
        this.knockback.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "amethystswordtip"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        if (level.isServer() && target != null) {
            BuffManager attackerBM = attacker.buffManager;
            float thresholdMod = attackerBM.getModifier(BuffModifiers.CRIT_CHANCE).floatValue() + attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE).floatValue();
            float crystallizeMod = attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE).floatValue() + attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE).floatValue();
            int stackThreshold = (int)GameMath.limit(10.0f - 7.0f * thresholdMod, 3.0f, 10.0f);
            float crystallizeDamageMultiplier = GameMath.limit(crystallizeMod, 2.0f, (float)stackThreshold);
            Buff crystallizeBuff = BuffRegistry.Debuffs.CRYSTALLIZE_BUFF;
            ActiveBuff ab = new ActiveBuff(crystallizeBuff, target, 10000, (Attacker)attacker);
            target.buffManager.addBuff(ab, true);
            if (target.buffManager.getBuff(crystallizeBuff).getStacks() >= stackThreshold) {
                level.entityManager.events.add(new CrystallizeShatterEvent(target, CrystallizeShatterEvent.ParticleType.AMETHYST));
                target.buffManager.removeBuff(crystallizeBuff, true);
                GameDamage finalDamage = this.getDamage(item).modDamage(crystallizeDamageMultiplier);
                target.isServerHit(finalDamage, 0.0f, 0.0f, 0.0f, attacker);
            }
        }
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.gemstoneSwords).volume(0.4f);
    }
}

