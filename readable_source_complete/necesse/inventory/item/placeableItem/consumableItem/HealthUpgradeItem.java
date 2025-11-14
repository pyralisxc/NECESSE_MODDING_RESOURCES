/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class HealthUpgradeItem
extends ConsumableItem {
    public HealthUpgradeItem(int stackSize) {
        super(stackSize, true);
        this.rarity = Item.Rarity.UNIQUE;
        this.allowRightClickToConsume = true;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        player.healthUpgradeManager.addUpgrade(this.getStringID(), 1);
        if (level.isServer()) {
            level.getServer().network.sendToAllClientsExcept(new PacketPlayerGeneral(player.getServerClient()), player.getServerClient());
        } else if (level.isClient()) {
            SoundManager.playSound(GameResources.eat, (SoundEffect)SoundEffect.effect(player));
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.healthUpgradeManager.canUpgrade(this.getStringID())) {
            return null;
        }
        return "invalidupgrade";
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }
}

