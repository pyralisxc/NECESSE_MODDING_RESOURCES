/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import java.util.List;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameHair;
import necesse.gfx.HumanLook;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.level.maps.Level;

public class WigArmorItem
extends HelmetArmorItem {
    public WigArmorItem(int armor) {
        super(armor, null, 0, null, null);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    @Override
    public void loadItemTextures() {
    }

    @Override
    public GameTexture getArmorTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (item != null) {
            return GameHair.getHair(WigArmorItem.getHair(item.getGndData())).getHairTexture(WigArmorItem.getHairCol(item.getGndData()));
        }
        return super.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
    }

    @Override
    public GameTexture getBackArmorTexture(InventoryItem item, PlayerMob player) {
        if (item != null) {
            return GameHair.getHair(WigArmorItem.getHair(item.getGndData())).getBackHairTexture(WigArmorItem.getHairCol(item.getGndData()));
        }
        return super.getBackArmorTexture(item, player);
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (item != null) {
            return new GameSprite(GameHair.getHair(WigArmorItem.getHair(item.getGndData())).getWigTexture(WigArmorItem.getHairCol(item.getGndData())));
        }
        return super.getItemSprite(item, perspective);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return super.isSameGNDData(level, me, them, purpose) && me.getGndData().sameKeys(them.getGndData(), "hair", "haircol");
    }

    @Override
    public void addDefaultItems(List<InventoryItem> list, PlayerMob player) {
        for (int i = 0; i < GameHair.getTotalHair(); ++i) {
            HumanLook look = player == null ? new HumanLook() : new HumanLook(player.look);
            look.setHair(i);
            list.add(WigArmorItem.addWigData(new InventoryItem(this), look));
        }
        super.addDefaultItems(list, player);
    }

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        return WigArmorItem.addWigData(super.getDefaultItem(player, amount), player == null ? new HumanLook() : player.look);
    }

    public static int getHair(GNDItemMap gndData) {
        return gndData.getByte("hair") & 0xFF;
    }

    public static int getHairCol(GNDItemMap gndData) {
        return gndData.getByte("haircol") & 0xFF;
    }

    public static GNDItemMap getWigData(HumanLook look) {
        return WigArmorItem.addWigData((GNDItemMap)null, look);
    }

    public static GNDItemMap addWigData(GNDItemMap data, HumanLook look) {
        if (data == null) {
            data = new GNDItemMap();
        }
        data.setByte("hair", (byte)look.getHair());
        data.setByte("haircol", (byte)look.getHairColor());
        return data;
    }

    public static InventoryItem addWigData(InventoryItem item, HumanLook look) {
        WigArmorItem.addWigData(item.getGndData(), look);
        return item;
    }
}

