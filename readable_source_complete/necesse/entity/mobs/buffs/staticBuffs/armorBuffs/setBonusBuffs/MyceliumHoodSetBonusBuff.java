/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;

public class MyceliumHoodSetBonusBuff
extends SetBonusBuff
implements ActiveBuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        super.onItemAttacked(buff, targetX, targetY, attackerMob, attackHeight, item, slot, animAttack, attackMap);
    }

    @Override
    public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return this.getRunningAbilityContent(player, buff);
    }

    @Override
    public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        StaminaBuff.writeStaminaData(player, writer);
        return content;
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (buff.owner.isRiding()) {
            return false;
        }
        if (player.isServer() && !Settings.strictServerAuthority) {
            return true;
        }
        return StaminaBuff.canStartStaminaUsage(buff.owner);
    }

    @Override
    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        if (!player.isServer() || !Settings.strictServerAuthority) {
            StaminaBuff.readStaminaData(player, reader);
        }
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, (Mob)player, 1.0f, null), false);
    }

    @Override
    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        if (player.inLiquid()) {
            player.buffManager.removeBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, false);
        } else {
            ActiveBuff activeBuff = player.buffManager.getBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE);
            if (activeBuff != null) {
                activeBuff.setDurationLeftSeconds(1.0f);
            } else {
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, (Mob)player, 1.0f, null), false);
            }
            long msToDeplete = 4000L;
            float usage = 50.0f / (float)msToDeplete;
            if (!StaminaBuff.useStaminaAndGetValid(player, usage)) {
                return false;
            }
        }
        return !isRunningClient || Control.SET_ABILITY.isDown();
    }

    @Override
    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    @Override
    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        player.buffManager.removeBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, false);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "myceliumhoodset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

