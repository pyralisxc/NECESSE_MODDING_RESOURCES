/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;

public class RecallFlaskItem
extends ConsumableItem {
    public RecallFlaskItem() {
        super(1, false);
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.RARE;
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
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
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
        tooltips.add(Localization.translate("itemtooltip", "recalltip"));
        if (!this.isSingleUse(perspective)) {
            tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
        }
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "flask");
    }
}

