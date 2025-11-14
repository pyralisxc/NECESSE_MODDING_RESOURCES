/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.level.maps.Level;

public class ForceOfWindTrinketItem
extends TrinketItem {
    public ForceOfWindTrinketItem() {
        super(Item.Rarity.EPIC, 500, TrinketsLootTable.trinkets);
        this.attackAnimTime.setBaseValue(200);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        if (blackboard.getBoolean("equipped")) {
            tooltips.add(Localization.translate("itemtooltip", "fowtipequipped"));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "fowtipnotequipped"));
        }
        tooltips.add(Localization.translate("itemtooltip", "fowtip2"));
        return tooltips;
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("forceofwindtrinket")};
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int strength = 150;
        Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
        PacketForceOfWind.applyToMob(level, attackerMob, dir.x, dir.y, strength);
        PacketForceOfWind.addCooldownStack(attackerMob, 3.0f, level.isServer());
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();
        if (level.isServer()) {
            attackerMob.sendAttackerPacket(attackerMob, new PacketForceOfWind(attackerMob, dir.x, dir.y, strength));
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swoosh, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.5f).pitch(1.7f));
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        String out = super.canAttack(level, x, y, attackerMob, item);
        if (out != null) {
            return out;
        }
        return !attackerMob.isRiding() && !PacketForceOfWind.isOnCooldown(attackerMob) ? null : "";
    }
}

