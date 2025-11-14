/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.attackHandler.GreatswordChargeLevel
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.battleaxe;

import aphorea.buffs.AdrenalineBuff;
import aphorea.items.tools.weapons.melee.battleaxe.logic.BattleaxeAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.registry.AphBuffs;
import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

public abstract class AphBattleaxeToolItem
extends AphGreatswordToolItem
implements ItemInteractAction {
    boolean isCharging;

    public AphBattleaxeToolItem(int enchantCost, GreatswordChargeLevel[] chargeLevels) {
        super(enchantCost, chargeLevels);
        this.keyWords.add("battleaxe");
        this.keyWords.remove("sword");
    }

    public static GreatswordChargeLevel[] getChargeLevel(int time, Color color) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(time, 1.0f, color)};
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"battleaxe"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"battleaxe2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"battleaxe3"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"adrenaline"));
        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canLevelInteract(level, x, y, attackerMob, item)) {
            this.onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, this.getLevelInteractAttackAnimTime(item, attackerMob), mapContent);
        } else {
            attackerMob.startAttackHandler((AttackHandler)new BattleaxeAttackHandler(attackerMob, slot, item, this, seed, x, y, attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH) ? 1.0f + 0.1f * (float)AdrenalineBuff.getAdrenalineLevel((Mob)attackerMob) : 1.0f, this.chargeLevels));
        }
        return item;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"battleaxe");
    }

    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding() && !attackerMob.isAttacking && !this.isCharging && !attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH) && !attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH_COOLDOWN);
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.addBuff(new ActiveBuff(AphBuffs.BERSERKER_RUSH, (Mob)attackerMob, 11.0f, null), true);
        AdrenalineBuff.giveAdrenaline((Mob)attackerMob, 3, 4000, false);
        return item;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        AdrenalineBuff.giveAdrenaline(attacker, 3000, true);
        super.hitMob(item, event, level, target, attacker);
    }
}

