/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.merchant;

import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class MerchantShirtArmorItem
extends ChestArmorItem {
    public GameTexture backpack_front;
    public GameTexture backpack_back;

    public MerchantShirtArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "merchantshirt", "merchantshirtarms", CosmeticArmorLootTable.cosmeticArmor);
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.backpack_front = GameTexture.fromFile("player/armor/merchantbackpack_front");
        this.backpack_back = GameTexture.fromFile("player/armor/merchantbackpack_back");
    }

    @Override
    public void addExtraDrawOptions(HumanDrawOptions options, InventoryItem item) {
        super.addExtraDrawOptions(options, item);
        options.addTopDraw((player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> this.backpack_front.initDraw().sprite(spriteX, spriteY, spriteRes).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
        options.addBehindDraw((player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> this.backpack_back.initDraw().sprite(spriteX, spriteY, spriteRes).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
    }
}

