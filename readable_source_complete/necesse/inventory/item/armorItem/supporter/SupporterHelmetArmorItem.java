/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.supporter;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SupporterHelmetArmorItem
extends SetHelmetArmorItem {
    public GameTexture lightTexture;

    public SupporterHelmetArmorItem() {
        super(0, DamageTypeRegistry.MELEE, 0, null, null, Item.Rarity.UNIQUE, "supporterhelmet", "supporterchestplate", "supporterboots", "supportersetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
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
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
    }

    @Override
    public DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        DrawOptionsList options = new DrawOptionsList();
        options.add(super.getArmorDrawOptions(item, level, player, headItem, chestItem, feetItem, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask));
        Color col = this.getDrawColor(item, player);
        options.add(this.lightTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
        return options;
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

