/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.supporter;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.BootsArmorItem;

public class SupporterBootsArmorItem
extends BootsArmorItem {
    public SupporterBootsArmorItem() {
        super(0, 0, Item.Rarity.UNIQUE, "supporterboots", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.UNDER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.UNDER_FACIAL_FEATURE;
    }

    @Override
    public boolean canMobEquip(Mob mob, InventoryItem item) {
        PlayerMob playerMob;
        NetworkClient client;
        if (mob == null) {
            return false;
        }
        if (mob.isPlayer && (client = (playerMob = (PlayerMob)mob).getNetworkClient()) != null) {
            return client.isSupporter();
        }
        return false;
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips baseTooltips = super.getBaseTooltips(item, perspective, blackboard);
        if (perspective != null && perspective.getNetworkClient().isSupporter()) {
            baseTooltips.add(new StringTooltips(Localization.translate("itemtooltip", "supporterset"), GameColor.ITEM_UNIQUE));
        }
        baseTooltips.add(new StringTooltips(Localization.translate("itemtooltip", "supportersetwarning"), GameColor.ITEM_RARE, 350));
        return baseTooltips;
    }
}

