/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.CustomObjectItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;

public class SeedObjectItem
extends CustomObjectItem {
    private final SeedObject seed;

    public SeedObjectItem(SeedObject object, Supplier<GameTexture> textureSupplier) {
        super((GameObject)object, textureSupplier, 0, 0);
        this.seed = object;
        this.addGlobalIngredient("anycompostable");
    }

    public SeedObject getSeedObject() {
        return this.seed;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.seed.canBePlacedAsFlower) {
            tooltips.add(this.getFlowerItem().getObject().getItemTooltips(item, perspective));
        }
        if (perspective != null && SeedGunProjectileToolItem.SEED_AMMO_TYPES.contains(this.getStringID())) {
            Item[] validItems = new Item[]{ItemRegistry.getItem("seedgun")};
            Item firstItem = perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, validItems, "bulletammo");
            if (firstItem != null) {
                tooltips.add(GameColor.ITEM_QUEST.getColorCode() + Localization.translate("itemtooltip", this.getStringID() + "ammotip"));
            }
        }
        return tooltips;
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        ArrayList<ObjectPlaceOption> placeOptions = super.getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
        if (this.seed.canBePlacedAsFlower) {
            placeOptions.addAll(this.getFlowerItem().getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile));
        }
        return placeOptions;
    }

    public GameObject getFlowerObject() {
        return ObjectRegistry.getObject(this.seed.flowerID);
    }

    public ObjectItem getFlowerItem() {
        return this.getFlowerObject().getObjectItem();
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("itemcategory", "seeds");
    }
}

