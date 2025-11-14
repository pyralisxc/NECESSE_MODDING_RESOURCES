/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.misc;

import aphorea.data.AphWorldData;
import aphorea.items.vanillaitemtypes.AphMiscItem;
import aphorea.registry.AphData;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class GelSlimeNullifier
extends AphMiscItem {
    public GelSlimeNullifier() {
        super(1);
        this.rarity = Item.Rarity.LEGENDARY;
    }

    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 500;
    }

    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 10000;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isServer()) {
            AphWorldData currentData = AphData.getWorldData(level.getWorldEntity());
            boolean gelSlimesNulled = currentData.gelSlimesNulled;
            if (gelSlimesNulled) {
                currentData.gelSlimesNulled = false;
                PacketChatMessage mensaje = new PacketChatMessage(Localization.translate((String)"message", (String)"gelslimesunnulled"));
                GameUtils.streamServerClients((Level)level).forEach(j -> j.sendPacket((Packet)mensaje));
            } else {
                currentData.gelSlimesNulled = true;
                PacketChatMessage message = new PacketChatMessage(Localization.translate((String)"message", (String)"gelslimesnulled"));
                GameUtils.streamServerClients((Level)level).forEach(j -> j.sendPacket((Packet)message));
            }
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"gelslimenullifier"));
        return tooltips;
    }
}

