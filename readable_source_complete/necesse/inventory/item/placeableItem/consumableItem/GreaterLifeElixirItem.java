/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.HealthUpgradeItem;
import necesse.level.maps.Level;

public class GreaterLifeElixirItem
extends HealthUpgradeItem {
    public GreaterLifeElixirItem() {
        super(10);
        this.rarity = Item.Rarity.EPIC;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
    }

    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    @Override
    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        SoundManager.playSound(GameResources.drink, (SoundEffect)SoundEffect.effect(player));
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "greaterlifeelixirtip"));
        return tooltips;
    }
}

