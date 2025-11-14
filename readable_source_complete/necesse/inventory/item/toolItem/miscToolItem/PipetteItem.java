/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public class PipetteItem
extends ToolItem {
    public PipetteItem() {
        super(0, null);
        this.setItemCategory("equipment", "tools", "creative");
        this.keyWords.add("creative");
        this.keyWords.add("pipette");
        this.hungerUsage = 0.0f;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(PipetteItem.getToolTip());
        return tooltips;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob instanceof PlayerMob) {
            PlayerMob player = (PlayerMob)attackerMob;
            if (player.getDraggingItem() == item) {
                item.setAmount(0);
            }
            if (level.isClient()) {
                player.pipetteAt(x, y);
            }
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public static GameMessage getToolTip() {
        GameMessageBuilder builder = new GameMessageBuilder();
        builder.append(new LocalMessage("itemtooltip", "pipettetip"));
        builder.append(new StaticMessage("\n"));
        boolean bound = Input.lastInputIsController ? ControllerInput.getStateGlyph(ControllerInput.PIPETTE) != null : Control.PIPETTE.getKey() != -1;
        LocalMessage bindTip = bound ? new LocalMessage("ui", "pipettetip", "key", TypeParsers.getInputParseString(Control.PIPETTE)) : new LocalMessage("ui", "pipetteunboundtip", "controlname", Control.PIPETTE.text);
        builder.append(bindTip);
        return builder;
    }
}

