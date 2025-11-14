/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.followerSummonItem.petFollowerPlaceableItem;

import java.util.function.Supplier;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.followerSummonItem.FollowerSummonPlaceableItem;

public class PetFollowerPlaceableItem
extends FollowerSummonPlaceableItem {
    public PetFollowerPlaceableItem(String mobType, Item.Rarity rarity) {
        super(1, false, mobType, FollowPosition.WALK_CLOSE, "summonedpet", "summonedpet", 1);
        this.rarity = rarity;
        this.setItemCategory("misc", "pets");
        this.keyWords.add("pet");
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        AchievementManager.GET_PET_ITEMS.add(this.getStringID());
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (container.getClient().isServer()) {
                ServerClient client = container.getClient().getServerClient();
                GNDItemMap mapContent = new GNDItemMap();
                this.setupSummonMapContent(mapContent);
                if (this.canSummon(client.getLevel(), client.playerMob, item, mapContent) == null) {
                    this.summonServerMob(client.getLevel(), client.playerMob, item, mapContent);
                }
            }
            return new ContainerActionResult(42965565);
        };
    }

    @Override
    protected ListGameTooltips getAnimalTooltips(InventoryItem item, Attacker attacker) {
        ListGameTooltips tooltips = super.getAnimalTooltips(item, attacker);
        tooltips.add(Localization.translate("itemtooltip", "summonquicktip"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("mob", "pet");
    }
}

