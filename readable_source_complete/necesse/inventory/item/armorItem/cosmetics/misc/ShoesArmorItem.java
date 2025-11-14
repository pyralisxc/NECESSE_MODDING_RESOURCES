/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import java.awt.Color;
import necesse.engine.GlobalData;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.level.maps.Level;

public class ShoesArmorItem
extends BootsArmorItem {
    public ShoesArmorItem(int armor) {
        super(armor, 0, null, null);
    }

    @Override
    public void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/shoes");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        if (GlobalData.debugActive() && item != null) {
            Color col = ShoesArmorItem.getColor(item.getGndData());
            tooltips.add("Color: " + col.getRed() + ", " + col.getGreen() + ", " + col.getBlue());
        }
        return tooltips;
    }

    @Override
    protected void loadArmorTexture() {
        this.armorTexture = GameTexture.fromFile("player/armor/shoes");
    }

    @Override
    public Color getDrawColor(InventoryItem item, PlayerMob player) {
        if (item != null) {
            return ShoesArmorItem.getColor(item.getGndData());
        }
        return super.getDrawColor(item, player);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return super.isSameGNDData(level, me, them, purpose) && me.getGndData().sameKeys(them.getGndData(), "red", "green", "blue");
    }

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        return ShoesArmorItem.addColorData(super.getDefaultItem(player, amount), player == null ? new Color(255, 255, 255) : player.look.getShoesColor());
    }

    public static Color getColor(GNDItemMap gndData) {
        int red = gndData.getByte("red") & 0xFF;
        int green = gndData.getByte("green") & 0xFF;
        int blue = gndData.getByte("blue") & 0xFF;
        return new Color(red, green, blue);
    }

    public static GNDItemMap getColorData(Color color) {
        return ShoesArmorItem.addColorData((GNDItemMap)null, color);
    }

    public static GNDItemMap addColorData(GNDItemMap data, Color color) {
        if (data == null) {
            data = new GNDItemMap();
        }
        data.setByte("red", (byte)color.getRed());
        data.setByte("green", (byte)color.getGreen());
        data.setByte("blue", (byte)color.getBlue());
        return data;
    }

    public static InventoryItem addColorData(InventoryItem item, Color color) {
        ShoesArmorItem.addColorData(item.getGndData(), color);
        return item;
    }
}

