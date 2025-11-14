/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RecallScrollItem
extends ConsumableItem
implements AdventurePartyConsumableItem {
    public RecallScrollItem() {
        super(100, true);
        this.rarity = Item.Rarity.COMMON;
        this.itemCooldownTime.setBaseValue(2000);
        this.worldDrawSize = 32;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            int delay = this.getFlatAttackAnimTime(item);
            TeleportEvent e = new TeleportEvent(client, delay, client.spawnLevelIdentifier, 5.0f, null, newLevel -> {
                client.validateSpawnPoint(true);
                Point offset = new Point(16, 16);
                if (!client.isDefaultSpawnPoint()) {
                    offset = RespawnObject.calculateSpawnOffset(newLevel, client.spawnTile.x, client.spawnTile.y, client);
                }
                return new TeleportResult(true, client.spawnLevelIdentifier, client.spawnTile.x * 32 + offset.x, client.spawnTile.y * 32 + offset.y);
            });
            level.entityManager.events.addHidden(e);
        }
        return item;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        String out = super.canAttack(level, x, y, attackerMob, item);
        if (out != null) {
            return out;
        }
        return !attackerMob.buffManager.hasBuff("teleportsickness") ? null : "";
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.buffManager.hasBuff("teleportsickness")) {
            return "teleportsickness";
        }
        return null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        tooltips.add(Localization.translate("itemtooltip", "recalltip"));
        return tooltips;
    }

    @Override
    public ComparableSequence<Integer> getPartyPriority(Level level, HumanMob mob, ServerClient partyClient, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return new ComparableSequence<Integer>(-1000).thenBy(inventorySlot);
    }

    @Override
    public boolean canAndShouldPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        return mob.getHealthPercent() <= 0.25f || purpose.equals("secondwind");
    }

    @Override
    public InventoryItem onPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        mob.adventureParty.clear(true);
        mob.clearCommandsOrders(null);
        if (mob.isSettlerOnCurrentLevel()) {
            ServerSettlementData settlement = mob.getSettlerSettlementServerData();
            Point returnPos = SettlersWorldData.getReturnPos(mob, settlement);
            TeleportEvent teleportEvent = new TeleportEvent(mob, 0, settlement.getLevel().getIdentifier(), 0.0f, null, newLevel -> new TeleportResult(true, settlement.getLevel().getIdentifier(), returnPos.x, returnPos.y));
            mob.getLevel().entityManager.events.add(teleportEvent);
        } else {
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(mob.getLevel().getServer());
            settlersData.returnToSettlement(mob, true);
        }
        mob.clearCommandsOrders(null);
        LocalMessage message = new LocalMessage("ui", "adventurepartyleftrecall", "name", mob.getLocalization());
        partyClient.sendChatMessage(message);
        InventoryItem out = item.copy();
        if (this.isSingleUse(null)) {
            item.setAmount(item.getAmount() - 1);
        }
        return out;
    }

    @Override
    public boolean shouldPreventHit(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item) {
        return true;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "scroll");
    }
}

